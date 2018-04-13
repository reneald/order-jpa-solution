package com.switchfully.order.domain;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.UUID;

import static com.switchfully.order.domain.customers.CustomerTestBuilder.aCustomer;

public class EntityTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void generateId_givenCustomerWithoutId_whenGeneratingId_thenGenerateId() {
        Entity aCustomer = aCustomer().build();
        aCustomer.generateId();
        Assertions.assertThat(aCustomer.getId())
                .isNotNull()
                .isNotEqualTo("");
    }

    @Test
    public void generateId_givenCustomerWithId_whenGeneratingId_thenThrowException() {
        UUID id = UUID.randomUUID();
        Entity customer = aCustomer().build();
        Whitebox.setInternalState(customer, "id", id);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Generating an ID for a customer that already has an ID (" + id + ") is not allowed.");

        customer.generateId();
    }

}