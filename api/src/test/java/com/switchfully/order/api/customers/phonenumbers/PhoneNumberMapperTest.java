package com.switchfully.order.api.customers.phonenumbers;

import com.switchfully.order.domain.customers.phonenumbers.PhoneNumber;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static com.switchfully.order.domain.customers.phonenumbers.PhoneNumber.PhoneNumberBuilder.phoneNumber;

public class PhoneNumberMapperTest {

    @Test
    public void toDto() {
        PhoneNumberDto phoneNumberDto = new PhoneNumberMapper().toDto(phoneNumber()
                .withNumber("4848522541")
                .withCountryCallingCode("+32")
                .build());

        Assertions.assertThat(phoneNumberDto)
                .isEqualToComparingFieldByField(new PhoneNumberDto()
                        .withNumber("4848522541")
                        .withCountryCallingCode("+32"));
    }

    @Test
    public void toDomain() {
        PhoneNumber phoneNumber = new PhoneNumberMapper().toDomain(new PhoneNumberDto()
                .withNumber("4848522541")
                .withCountryCallingCode("+32"));

        Assertions.assertThat(phoneNumber)
                .isEqualToComparingFieldByField(phoneNumber()
                        .withNumber("4848522541")
                        .withCountryCallingCode("+32")
                        .build());
    }

}