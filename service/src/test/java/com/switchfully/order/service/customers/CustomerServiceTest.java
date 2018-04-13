package com.switchfully.order.service.customers;

import com.switchfully.order.domain.customers.Customer;
import com.switchfully.order.domain.customers.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static com.switchfully.order.domain.customers.CustomerTestBuilder.aCustomer;


public class CustomerServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CustomerService customerService;
    private CustomerValidator customerValidatorMock;
    private CustomerRepository customerRepositoryMock;

    @Before
    public void setupService() {
        customerRepositoryMock = Mockito.mock(CustomerRepository.class);
        customerValidatorMock = Mockito.mock(CustomerValidator.class);
        customerService = new CustomerService(customerRepositoryMock, customerValidatorMock);
    }

    @Test
    public void createCustomer_happyPath() {
        Customer customer = aCustomer().build();
        Mockito.when(customerValidatorMock.isValidForCreation(customer)).thenReturn(true);
        Mockito.when(customerRepositoryMock.save(customer)).thenReturn(customer);

        Customer createdCustomer = customerService.createCustomer(customer);

        Assertions.assertThat(createdCustomer).isNotNull();
    }

    @Test
    public void createCustomer_givenCustomerThatIsNotValidForCreation_thenThrowException() {
        Customer customer = aCustomer().build();
        Mockito.when(customerValidatorMock.isValidForCreation(customer)).thenReturn(false);
        Mockito.doThrow(IllegalStateException.class).when(customerValidatorMock)
                .throwInvalidStateException(customer, "creation");

        expectedException.expect(IllegalStateException.class);

        customerService.createCustomer(customer);
    }

}
