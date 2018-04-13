package com.switchfully.order.domain.customers;

import com.switchfully.order.domain.Repository;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class CustomerRepository extends Repository<Customer, CustomerDatabase>{

    @Inject
    public CustomerRepository(CustomerDatabase database) {
        super(database);
    }
}
