package com.switchfully.order.domain.customers;

import com.switchfully.order.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static com.switchfully.order.domain.customers.CustomerTestBuilder.aCustomer;

public class CustomerRepositoryIntegrationTest extends IntegrationTest {

    @Inject
    private CustomerRepository repository;

    @Test
    public void save() {
        Customer customerToSave = aCustomer().build();

        Customer savedCustomer = repository.save(customerToSave);

        Assertions.assertThat(savedCustomer.getId()).isNotNull();
        Assertions.assertThat(repository.get(savedCustomer.getId()))
                .isEqualToComparingFieldByField(savedCustomer);
    }

    @Test
    public void update() {
        Customer customerToSave = aCustomer().withFirstname("Jo").withLastname("Jorissen").build();
        Customer savedCustomer = repository.save(customerToSave);


        Customer updatedCustomer = repository.update(aCustomer()
                .withId(savedCustomer.getId())
                .withFirstname("Joske")
                .withLastname("Jorissen")
                .build());

        Assertions.assertThat(updatedCustomer.getId()).isNotNull().isEqualTo(savedCustomer.getId());
        Assertions.assertThat(updatedCustomer.getFirstname()).isEqualTo("Joske");
        Assertions.assertThat(updatedCustomer.getLastname()).isEqualTo("Jorissen");
        Assertions.assertThat(repository.getAll()).hasSize(1);
    }

    @Test
    public void get() {
        Customer savedCustomer = repository.save(aCustomer().build());

        Customer actualCustomer = repository.get(savedCustomer.getId());

        Assertions.assertThat(actualCustomer)
                .isEqualToComparingFieldByField(savedCustomer);
    }

    @Test
    public void getAll() {
        Customer customerOne = repository.save(aCustomer().build());
        Customer customerTwo = repository.save(aCustomer().build());

        List<Customer> allCustomers = repository.getAll();

        Assertions.assertThat(allCustomers)
                .containsExactlyInAnyOrder(customerOne, customerTwo);
    }

}