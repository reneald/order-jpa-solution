package com.switchfully.order.domain;

import com.switchfully.order.domain.customers.Customer;
import com.switchfully.order.domain.customers.CustomerDatabase;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static com.switchfully.order.domain.customers.CustomerTestBuilder.aCustomer;

public class EntityDatabaseTest {

    @Test
    public void poplulate_getAll() {
        EntityDatabase<Customer> database = new CustomerDatabase();
        Customer customer1 = aCustomer().build();
        Customer customer2 = aCustomer().build();
        Customer customer3 = aCustomer().build();

        customer1.generateId();
        customer2.generateId();
        customer3.generateId();

        database.poplulate(customer1, customer2, customer3);

        Assertions.assertThat(database.getAll()).hasSize(3);
        Assertions.assertThat(database.getAll().values()).containsExactlyInAnyOrder(customer1, customer2, customer3);
    }

    @Test
    public void save_givenDifferentId_thenStoreBoth() {
        EntityDatabase<Customer> database = new CustomerDatabase();
        Customer customer = aCustomer().build();
        customer.generateId();
        database.poplulate(customer);

        Customer customer2 = aCustomer().build();
        customer2.generateId();
        database.save(customer2);

        Assertions.assertThat(database.getAll()).hasSize(2);
    }

    @Test
    public void save_givenSameId_thenUpdateExisting() {
        EntityDatabase<Customer> database = new CustomerDatabase();
        Customer customer = aCustomer().withFirstname("OriginalName").build();
        customer.generateId();
        database.poplulate(customer);

        Customer customer2 = aCustomer().withFirstname("NewName").build();
        Whitebox.setInternalState(customer2, "id", customer.getId());
        database.save(customer2);

        Assertions.assertThat(database.getAll()).hasSize(1);
        Assertions.assertThat(database.getAll().get(customer.getId()).getFirstname()).isEqualTo("NewName");
    }

}