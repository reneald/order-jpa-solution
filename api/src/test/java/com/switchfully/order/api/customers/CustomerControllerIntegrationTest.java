package com.switchfully.order.api.customers;

import com.switchfully.order.ControllerIntegrationTest;
import com.switchfully.order.api.customers.addresses.AddressDto;
import com.switchfully.order.api.customers.emails.EmailDto;
import com.switchfully.order.api.customers.phonenumbers.PhoneNumberDto;
import com.switchfully.order.api.interceptors.ControllerExceptionHandler;
import com.switchfully.order.domain.customers.Customer;
import com.switchfully.order.domain.customers.CustomerRepository;
import com.switchfully.order.domain.customers.CustomerTestBuilder;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerControllerIntegrationTest extends ControllerIntegrationTest {

    @Inject
    private CustomerRepository customerRepository;

    @Inject
    private CustomerMapper customerMapper;

    @After
    public void resetDatabase() {
        customerRepository.reset();
    }

    @Test
    public void createCustomer() {
        CustomerDto customerToCreate = new CustomerDto()
                .withFirstname("Bruce")
                .withLastname("Wayne")
                .withEmail(new EmailDto()
                        .withLocalPart("brucy")
                        .withDomain("bat.net")
                        .withComplete("brucy@bat.net"))
                .withPhoneNumber(new PhoneNumberDto()
                        .withNumber("485212121")
                        .withCountryCallingCode("+32"))
                .withAddress(new AddressDto()
                        .withStreetName("Secretstreet")
                        .withHouseNumber("841")
                        .withPostalCode("1238")
                        .withCountry("GothamCountry"));

        CustomerDto createdCustomer = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME), customerToCreate, CustomerDto.class);

        assertCustomerIsEqualIgnoringId(customerToCreate, createdCustomer);
    }

    @Test
    public void createCustomer_givenCustomerNotValidForCreationBecauseOfMissingFirstName_thenErrorObjectReturnedByControllerExceptionHandler() {
        CustomerDto customerToCreate = new CustomerDto()
                .withFirstname(null)
                .withLastname("Wayne")
                .withEmail(new EmailDto()
                        .withLocalPart("brucy")
                        .withDomain("bat.net")
                        .withComplete("brucy@bat.net"))
                .withPhoneNumber(new PhoneNumberDto()
                        .withNumber("485212121")
                        .withCountryCallingCode("+32"))
                .withAddress(new AddressDto()
                        .withStreetName("Secretstreet")
                        .withHouseNumber("841")
                        .withPostalCode("1238")
                        .withCountry("GothamCountry"));

        ControllerExceptionHandler.Error error = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME), customerToCreate, ControllerExceptionHandler.Error.class);

        assertThat(error).isNotNull();
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.getUniqueErrorId()).isNotNull().isNotEmpty();
        assertThat(error.getMessage()).contains("Invalid Customer provided for creation. " +
                "Provided object: Customer{id=");
    }

    @Test
    public void getAllCustomers() {
        customerRepository.save(CustomerTestBuilder.aCustomer().build());
        customerRepository.save(CustomerTestBuilder.aCustomer().build());
        customerRepository.save(CustomerTestBuilder.aCustomer().build());

        CustomerDto[] allCustomers = new TestRestTemplate()
                .getForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME), CustomerDto[].class);

        assertThat(allCustomers).hasSize(3);
    }

    @Test
    public void getAllCustomers_assertResultIsCorrectlyReturned() {
        Customer customerInDb = customerRepository.save(CustomerTestBuilder.aCustomer().build());

        CustomerDto[] allCustomers = new TestRestTemplate()
                .getForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME), CustomerDto[].class);

        assertThat(allCustomers).hasSize(1);
        assertThat(allCustomers[0])
                .isEqualToComparingFieldByFieldRecursively(customerMapper.toDto(customerInDb));
    }

    @Test
    public void getCustomer() {
        customerRepository.save(CustomerTestBuilder.aCustomer().build());
        Customer customerToFind = customerRepository.save(CustomerTestBuilder.aCustomer().build());
        customerRepository.save(CustomerTestBuilder.aCustomer().build());

        CustomerDto foundCustomer = new TestRestTemplate()
                .getForObject(format("http://localhost:%s/%s/%s", getPort(), CustomerController.RESOURCE_NAME, customerToFind.getId().toString()), CustomerDto.class);

        assertThat(foundCustomer)
                .isEqualToComparingFieldByFieldRecursively(customerMapper.toDto(customerToFind));
    }

    private void assertCustomerIsEqualIgnoringId(CustomerDto customerToCreate, CustomerDto createdCustomer) {
        assertThat(createdCustomer.getId()).isNotNull().isNotEmpty();
        assertThat(createdCustomer.getAddress()).isEqualToComparingFieldByField(customerToCreate.getAddress());
        assertThat(createdCustomer.getPhoneNumber()).isEqualToComparingFieldByField(customerToCreate.getPhoneNumber());
        assertThat(createdCustomer.getEmail()).isEqualToComparingFieldByField(customerToCreate.getEmail());
        assertThat(createdCustomer.getFirstname()).isEqualTo(customerToCreate.getFirstname());
        assertThat(createdCustomer.getLastname()).isEqualTo(customerToCreate.getLastname());
    }

}