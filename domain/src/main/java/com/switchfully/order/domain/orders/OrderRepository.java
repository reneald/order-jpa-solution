package com.switchfully.order.domain.orders;

import com.switchfully.order.domain.Repository;
import com.switchfully.order.domain.orders.orderitems.events.OrderItemCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Named
public class OrderRepository extends Repository<Order, OrderDatabase> {

    private ApplicationEventPublisher eventPublisher;

    @Inject
    public OrderRepository(OrderDatabase database, ApplicationEventPublisher eventPublisher) {
        super(database);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order save(Order entity) {
        Order savedOrder = super.save(entity);
        savedOrder.getOrderItems()
                .forEach(orderItem -> eventPublisher.publishEvent(new OrderItemCreatedEvent(orderItem)));
        return savedOrder;
    }

    public List<Order> getOrdersForCustomer(UUID customerId) {
        return getDatabase().getAll().values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }
}
