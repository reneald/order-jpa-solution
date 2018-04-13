package com.switchfully.order.service.orders;

import com.switchfully.order.domain.customers.CustomerRepository;
import com.switchfully.order.domain.items.ItemRepository;
import com.switchfully.order.domain.orders.Order;
import com.switchfully.order.domain.orders.OrderRepository;
import com.switchfully.order.infrastructure.exceptions.EntityNotFoundException;
import com.switchfully.order.infrastructure.exceptions.EntityNotValidException;
import com.switchfully.order.infrastructure.exceptions.NotAuthorizedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.UUID;

import static com.switchfully.order.domain.customers.CustomerTestBuilder.aCustomer;
import static com.switchfully.order.domain.items.ItemTestBuilder.anItem;
import static com.switchfully.order.domain.orders.OrderTestBuilder.anOrder;
import static com.switchfully.order.domain.orders.orderitems.OrderItemTestBuilder.anOrderItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;

public class OrderServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private OrderService orderService;
    private OrderValidator orderValidatorMock;
    private OrderRepository orderRepositoryMock;
    private CustomerRepository customerRepositoryMock;
    private ItemRepository itemRepositoryMock;

    @Before
    public void setupService() {
        orderRepositoryMock = Mockito.mock(OrderRepository.class);
        orderValidatorMock = Mockito.mock(OrderValidator.class);
        customerRepositoryMock = Mockito.mock(CustomerRepository.class);
        itemRepositoryMock = Mockito.mock(ItemRepository.class);
        orderService = new OrderService(customerRepositoryMock, itemRepositoryMock, orderRepositoryMock, orderValidatorMock);
    }

    @Test
    public void createOrder_happyPath() {
        Order order = anOrder().build();
        Mockito.when(orderValidatorMock.isValidForCreation(order)).thenReturn(true);
        Mockito.when(orderRepositoryMock.save(order)).thenReturn(order);
        Mockito.when(customerRepositoryMock.get(any(UUID.class))).thenReturn(aCustomer().build());
        Mockito.when(itemRepositoryMock.get(any(UUID.class))).thenReturn(anItem().build());

        Order createdOrder = orderService.createOrder(order);

        assertThat(createdOrder).isNotNull();
    }

    @Test
    public void createOrder_givenOrderThatIsNotValidForCreation_thenThrowException() {
        Order order = anOrder().build();
        Mockito.when(orderValidatorMock.isValidForCreation(order)).thenReturn(false);
        Mockito.doThrow(IllegalStateException.class).when(orderValidatorMock)
                .throwInvalidStateException(order, "creation");

        expectedException.expect(IllegalStateException.class);

        orderService.createOrder(order);
    }

    @Test
    public void createOrder_customerDoesNotExist() {
        UUID customerId = UUID.randomUUID();
        Order order = anOrder().withCustomerId(customerId).build();
        Mockito.when(orderValidatorMock.isValidForCreation(order)).thenReturn(true);
        Mockito.when(orderRepositoryMock.save(order)).thenReturn(order);
        Mockito.when(customerRepositoryMock.get(customerId)).thenReturn(null);

        expectedException.expect(EntityNotFoundException.class);
        expectedException.expectMessage("During creation of a new order when checking if the referenced " +
                "customer exists, the following entity was not found: Customer with id = " + customerId.toString());

        orderService.createOrder(order);
    }

    @Test
    public void createOrder_orderItemReferencingNonExistingItem() {
        Order order = anOrder().withOrderItems(anOrderItem().withItemId(UUID.randomUUID()).build()).build();
        Mockito.when(orderValidatorMock.isValidForCreation(order)).thenReturn(true);
        Mockito.when(orderRepositoryMock.save(order)).thenReturn(order);
        Mockito.when(customerRepositoryMock.get(any(UUID.class))).thenReturn(aCustomer().build());
        Mockito.when(itemRepositoryMock.get(any(UUID.class))).thenReturn(null);

        expectedException.expect(EntityNotValidException.class);
        expectedException.expectMessage("During creation of a new order when checking if all the ordered " +
                "items exist, the following entity was found to be invalid: " + order.toString());

        orderService.createOrder(order);
    }

    @Test
    public void reorderOrder() {
        UUID originalOrderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Mockito.when(orderRepositoryMock.get(originalOrderId))
                .thenReturn(anOrder()
                        .withCustomerId(customerId)
                        .withOrderItems(anOrderItem()
                                .withItemId(itemId)
                                .build())
                        .build());
        Mockito.when(customerRepositoryMock.get(customerId)).thenReturn(aCustomer().build());
        Mockito.when(itemRepositoryMock.get(itemId)).thenReturn(anItem().build());
        Order expectedOrder = anOrder().build();
        Mockito.when(orderRepositoryMock.save(any(Order.class))).thenReturn(expectedOrder);

        Order orderFromReorder = orderService.reorderOrder(originalOrderId);

        assertThat(orderFromReorder).isEqualTo(expectedOrder);
    }

    @Test
    public void reorderOrder_givenAnInvalidCustomer_thenThrowException() {
        UUID originalOrderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Mockito.when(orderRepositoryMock.get(originalOrderId))
                .thenReturn(anOrder().withCustomerId(customerId).build());
        Mockito.when(customerRepositoryMock.get(customerId)).thenReturn(null);

        expectedException.expect(NotAuthorizedException.class);
        expectedException.expectMessage("Customer " + customerId.toString() + " is not allowed to reorder the " +
                "Order " + originalOrderId.toString() + " because he's not the owner of that order!");

        orderService.reorderOrder(originalOrderId);
    }

}
