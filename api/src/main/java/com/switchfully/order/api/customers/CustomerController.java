package com.switchfully.order.api.customers;

import com.switchfully.order.service.customers.CustomerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/" + CustomerController.RESOURCE_NAME)
public class CustomerController {

    public static final String RESOURCE_NAME = "customers   ";

    private CustomerService customerService;
    private CustomerMapper customerMapper;

    @Inject
    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        return customerMapper.toDto(
                customerService.createCustomer(
                        customerMapper.toDomain(customerDto)));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CustomerDto updateItem(@PathVariable String id, @RequestBody CustomerDto customerDto) {
        return customerMapper.toDto(
                customerService.updateCustomer(
                        customerMapper.toDomain(UUID.fromString(id), customerDto)));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path="/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CustomerDto getCustomer(@PathVariable String id) {
        return customerMapper.toDto(
                customerService.getCustomer(UUID.fromString(id)));
    }

}
