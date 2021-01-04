package com.gohenry.bank.integration;

import com.gohenry.bank.domain.model.Customer;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerIntegrationTest extends IntegrationTestContext {

    @Test
    public void givenCustomerDetails_whenCreatingCustomer_thenCustomerWithAssociatedIdIsReturned() {
        var firstName = "Jhon";
        var lastName = "Doe";
        var ssn = "1212121212123";

        var customerRequest = Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .ssn(ssn)
                .build();

        var responseEntity = restTemplate.postForEntity(url, customerRequest, Customer.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var customer = responseEntity.getBody();
        assertThat(customer).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(customer.getFirstName()).isEqualTo(firstName);
        assertThat(customer.getLastName()).isEqualTo(lastName);
        assertThat(customer.getSsn()).isEqualTo(ssn);
    }

}
