package com.switchfully.order.api.customers;

import com.switchfully.order.ControllerIntegrationTest;
import com.switchfully.order.api.customers.addresses.AddressDto;
import com.switchfully.order.api.customers.emails.EmailDto;
import com.switchfully.order.api.customers.phonenumbers.PhoneNumberDto;
import com.switchfully.order.api.interceptors.ControllerExceptionHandler;
import com.switchfully.order.domain.customers.CustomerRepository;
import com.switchfully.order.service.customers.CustomerService;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerControllerIntegrationTest extends ControllerIntegrationTest {

    @Inject
    private CustomerService customerService;

    @Inject
    private CustomerMapper customerMapper;

    @Inject
    private CustomerRepository customerRepository;

    @Override
    public void clearDatabase() {
        customerRepository.getEntityManager().createQuery("DELETE FROM Customer").executeUpdate();
    }

    @Test
    public void createCustomer() {
        CustomerDto createdCustomer = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME), createACustomer(), CustomerDto.class);

        assertCustomerIsEqualIgnoringId(createACustomer(), createdCustomer);
    }

    @Test
    public void createCustomer_givenCustomerNotValidForCreationBecauseOfMissingFirstName_thenErrorObjectReturnedByControllerExceptionHandler() {
        CustomerDto customerToCreate = createACustomer().withFirstname(null);

        ControllerExceptionHandler.Error error = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME), customerToCreate, ControllerExceptionHandler.Error.class);

        assertThat(error).isNotNull();
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.getUniqueErrorId()).isNotNull().isNotEmpty();
        assertThat(error.getMessage()).contains("Invalid Customer provided for creation. " +
                "Provided object: Customer{id=");
    }

    @Test
    public void getAllCustomers_given2CreatedCustomers_whenGetAllCustomers_thenReturnAllCustomers() {
        new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME),
                        createACustomer(), CustomerDto.class);
        new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME),
                        createACustomer(), CustomerDto.class);

        CustomerDto[] allCustomers = new TestRestTemplate()
                .getForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME), CustomerDto[].class);

        assertThat(allCustomers).hasSize(2);
    }

    @Test
    public void getAllCustomers__assertResultIsCorrectlyReturned() {
        CustomerDto customerInDb = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME),
                        createACustomer(), CustomerDto.class);

        CustomerDto[] allCustomers = new TestRestTemplate()
                .getForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME), CustomerDto[].class);

        assertThat(allCustomers).hasSize(1);
        assertThat(allCustomers[0])
                .isEqualToComparingFieldByFieldRecursively(customerInDb);
    }

    @Test
    public void getCustomer_given3CreatedCustomers_whenGetSpecificCustomer_thenReturnOnlyThatCustomer() {
        new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME),
                        createACustomer(), CustomerDto.class);
        CustomerDto customerToFind = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME),
                        createACustomer().withFirstname("Minion"), CustomerDto.class);
        new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), CustomerController.RESOURCE_NAME),
                        createACustomer(), CustomerDto.class);

        CustomerDto foundCustomer = new TestRestTemplate()
                .getForObject(format("http://localhost:%s/%s/%s", getPort(), CustomerController.RESOURCE_NAME, customerToFind.getId()), CustomerDto.class);

        assertThat(foundCustomer)
                .isEqualToComparingFieldByFieldRecursively(customerToFind);
    }

    private CustomerDto createACustomer() {
        return new CustomerDto()
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