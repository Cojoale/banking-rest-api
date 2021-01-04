package com.gohenry.bank.integration;

import com.gohenry.bank.domain.model.Account;
import com.gohenry.bank.domain.model.Currency;
import com.gohenry.bank.exception.handling.CustomErrorResponse;

import org.junit.Test;

import java.math.BigDecimal;

import static com.gohenry.bank.domain.model.Currency.EUR;
import static com.gohenry.bank.domain.model.Currency.GBP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class AccountIntegrationTest extends IntegrationTestContext {

    @Test
    public void givenCustomerId_whenCreatingAccount_thenAccountIsCreated() {
        var currency = GBP;
        var initialBalance = BigDecimal.valueOf(5);
        var customerId = createCustomerEntity().getId();
        var createAccountRequest = createAccountRequest(currency, initialBalance);

        var accountResponseEntity = restTemplate.postForEntity(url + "/" + customerId + "/accounts",
                createAccountRequest,
                Account.class);

        assertThat(accountResponseEntity).isNotNull();

        var createAccountResponse = accountResponseEntity.getBody();

        assertThat(createAccountResponse).isNotNull()
                                         .hasNoNullFieldsOrProperties();
        assertThat(createAccountResponse.getCurrency()).isEqualTo(currency);
        assertThat(createAccountResponse.getBalance()).isEqualTo(initialBalance);
        assertThat(createAccountResponse.getCustomerId()).isEqualTo(customerId);

    }


    @Test
    public void givenCustomerId_whenCreatingAccountWithInvalidBalance_thenHttpStatus400IsReturned() {
        var initialBalance = BigDecimal.valueOf(20000000);
        var customerId = createCustomerEntity().getId();

        Account createAccountRequest = createAccountRequest(EUR, initialBalance);

        var accountUrl = String.format("%s/%s/accounts", url, customerId);

        var accountResponseEntity = restTemplate.postForEntity(accountUrl,
                createAccountRequest,
                Account.class);

        assertThat(accountResponseEntity).isNotNull();
        assertThat(accountResponseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void givenInvalidCustomerId_whenCreatingAccount_thenHttpStatus400IsReturned() {
        var initialBalance = BigDecimal.valueOf(100);
        var invalidCustomerId = Long.valueOf(2222222);

        var createAccountRequest = createAccountRequest(GBP, initialBalance);

        var createAccountUrl = this.url + "/" + invalidCustomerId + "/accounts";

        var accountResponseEntity = restTemplate.postForEntity(createAccountUrl,
                createAccountRequest,
                CustomErrorResponse.class);

        assertThat(accountResponseEntity).isNotNull();
        assertThat(accountResponseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(accountResponseEntity.getBody()).isNotNull();
        assertThat(accountResponseEntity.getBody().getErrorMsg()).isEqualTo(String.format("Customer having %d id was not found", invalidCustomerId));
        assertThat(accountResponseEntity.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    public void givenAccountId_whenGettingBalance_thenAccountWithBalanceIsReturned() {
        var initalAmount = BigDecimal.valueOf(20);
        var customer = createCustomerEntity();
        var accountEntity = createAccount(customer, initalAmount);
        var accountUrl = url + "/" + customer.getId() + "/accounts/" + accountEntity.getId();

        var accountResponseEntity = restTemplate.getForEntity(accountUrl, Account.class);

        assertThat(accountResponseEntity).isNotNull();
        assertThat(accountResponseEntity.getBody()).isNotNull();
        assertThat(accountResponseEntity.getBody().getBalance()).isEqualByComparingTo(accountEntity.getBalance());
    }

    @Test
    public void givenNotExistingAccountId_whenGettingBalance_thenHttpStatus404WithAccountNotFoundMessageIsReturned() {
        var customer = createCustomerEntity();
        var accountId = 11111;

        var accountUrl = url + "/" + customer.getId() + "/accounts/" + accountId;
        var accountResponseEntity = restTemplate.getForEntity(accountUrl, Account.class);

        assertThat(accountResponseEntity).isNotNull();
        assertThat(accountResponseEntity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void givenCustomerAccountId_whenGettingAccount_thenAccountIsReturned() {
        var initalAmount = BigDecimal.valueOf(20);
        var customer = createCustomerEntity();
        var accountEntity = createAccount(customer, initalAmount);

        var accountUrl = url + "/" + customer.getId() + "/accounts/" + accountEntity.getId();
        var accountResponseEntity = restTemplate.getForEntity(accountUrl, Account.class);

        assertThat(accountResponseEntity).isNotNull();
        assertThat(accountResponseEntity.getBody()).isNotNull();
        assertThat(accountResponseEntity.getBody().getBalance()).isEqualByComparingTo(accountEntity.getBalance());
    }

    private Account createAccountRequest(Currency currency, BigDecimal initialBalance) {
        return Account.builder()
                      .currency(currency)
                      .balance(initialBalance)
                      .build();
    }

}
