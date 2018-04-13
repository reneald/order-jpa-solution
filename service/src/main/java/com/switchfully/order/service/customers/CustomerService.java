package com.switchfully.order.service.customers;

import com.switchfully.order.domain.customers.Customer;
import com.switchfully.order.domain.customers.CustomerRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
public class CustomerService {

    private CustomerRepository customerRepository;
    private CustomerValidator customerValidator;

    @Inject
    public CustomerService(CustomerRepository customerRepository, CustomerValidator customerValidator) {
        this.customerRepository = customerRepository;
        this.customerValidator = customerValidator;
    }

    public Customer createCustomer(Customer customer) {
        if (!customerValidator.isValidForCreation(customer)) {
            customerValidator.throwInvalidStateException(customer, "creation");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerRepository.getAll().values());
    }

    public Customer getCustomer(UUID id) {
        return customerRepository.get(id);
    }
}
