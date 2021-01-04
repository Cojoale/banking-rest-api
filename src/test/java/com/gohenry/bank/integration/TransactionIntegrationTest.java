package com.gohenry.bank.integration;

import com.gohenry.bank.domain.entity.TransactionEntity;
import com.gohenry.bank.domain.model.Transaction;
import com.gohenry.bank.repository.TransactionRepository;
import com.gohenry.bank.service.AccountService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static com.gohenry.bank.domain.entity.TransactionType.CREDIT;
import static com.gohenry.bank.domain.entity.TransactionType.DEBIT;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class TransactionIntegrationTest extends IntegrationTestContext {

    private String transactionUrl;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    @Before
    public void buildUrl() {
        transactionUrl = url + "/%d/accounts/%d/transactions";
    }

    @Test
    public void givenCustomerAccount_whenTransferringFundsInAnotherAccount_thenTransferIsPerformedAndTransactionOfTheAccountOwnerIsReturned() {
        var sourceAccountBalance = BigDecimal.valueOf(1000);
        var destinationAccountBalance = BigDecimal.valueOf(2000);
        var transferredAmount = BigDecimal.valueOf(500);
        var customerEntity1 = createCustomerEntity();
        var customerEntity2 = createCustomerEntity();

        var sourceAccountId = createAccount(customerEntity1, sourceAccountBalance).getId();
        var destinationAccountId = createAccount(customerEntity2, destinationAccountBalance).getId();

        var transactionRequest = createTransactionRequest(transferredAmount, sourceAccountId, destinationAccountId);
        var url = buildUrl(customerEntity1.getId(), sourceAccountId);

        var responseEntity = restTemplate.postForEntity(url, transactionRequest, Transaction.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(CREATED);

        assertThatReturnedDebitTransactionIsValid(transferredAmount, sourceAccountId, destinationAccountId, responseEntity.getBody());

        assertThatAssociatedCreditTransactionIsCreatedAndValid(transferredAmount, sourceAccountId, destinationAccountId);
    }

    @Test
    public void givenCustomerAcount_whenTransferedAmounRequestedIsHigherThenBalance_thenTransactionExceptionIsReturned() {
        var sourceAccountBalance = BigDecimal.valueOf(1000);
        var destinationAccountBalance = BigDecimal.valueOf(2000);
        var transferredAmount = BigDecimal.valueOf(3000);
        var customerEntity1 = createCustomerEntity();
        var customerEntity2 = createCustomerEntity();

        var sourceAccountId = createAccount(customerEntity1, sourceAccountBalance).getId();
        var destinationAccountId = createAccount(customerEntity2, destinationAccountBalance).getId();

        var transactionRequest = createTransactionRequest(transferredAmount, sourceAccountId, destinationAccountId);

        var url = buildUrl(customerEntity1.getId(), sourceAccountId);
        var transactionResponseEntity = restTemplate.postForEntity(url, transactionRequest, Transaction.class);

        assertThat(transactionResponseEntity).isNotNull();
        assertThat(transactionResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void givenMultipleCustomerAcounts_whenTransferringFundsInAnotherPersonalAccount_thenTransferIsPerformed() {
        var sourceAccountBalance = BigDecimal.valueOf(1000);
        var destinationAccountBalance = BigDecimal.valueOf(2000);
        var transferredAmount = BigDecimal.valueOf(200);
        var customerEntity1 = createCustomerEntity();

        var sourceAccountId = createAccount(customerEntity1, sourceAccountBalance).getId();
        var destinationAccountId = createAccount(customerEntity1, destinationAccountBalance).getId();

        var transactionRequest = createTransactionRequest(transferredAmount, sourceAccountId, destinationAccountId);

        var url = buildUrl(customerEntity1.getId(), sourceAccountId);


        var transactionResponseEntity = restTemplate.postForEntity(url, transactionRequest, Transaction.class);

        assertThat(transactionResponseEntity).isNotNull();
        assertThat(transactionResponseEntity.getStatusCode()).isEqualTo(CREATED);

        assertThatReturnedDebitTransactionIsValid(transferredAmount, sourceAccountId, destinationAccountId, transactionResponseEntity.getBody());

        assertThatAssociatedCreditTransactionIsCreatedAndValid(transferredAmount, sourceAccountId, destinationAccountId);
    }

    @Test
    public void givenCustomerAccount_whenTransferringFundsInAnotherAccount_thenTransferIsPerformedAndAccountsBalanceIsUpdatedAccordingly() {
        var sourceAccountBalance = BigDecimal.valueOf(5000);
        var destinationAccountBalance = BigDecimal.valueOf(24000);
        var transferredAmount = BigDecimal.valueOf(1000);
        var customerEntity1 = createCustomerEntity();

        var sourceAccountId = createAccount(customerEntity1, sourceAccountBalance).getId();
        var destinationAccountId = createAccount(customerEntity1, destinationAccountBalance).getId();

        var transactionRequest = createTransactionRequest(transferredAmount, sourceAccountId, destinationAccountId);
        var url = buildUrl(customerEntity1.getId(), sourceAccountId);

        var responseEntity = restTemplate.postForEntity(url, transactionRequest, Transaction.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(CREATED);

        assertThatReturnedDebitTransactionIsValid(transferredAmount, sourceAccountId, destinationAccountId, responseEntity.getBody());

        assertThatAssociatedCreditTransactionIsCreatedAndValid(transferredAmount, sourceAccountId, destinationAccountId);

        var srcAccount = accountService.getAccount(sourceAccountId);
        assertThat(srcAccount).isNotNull();
        assertThat(srcAccount.getBalance()).isEqualByComparingTo(sourceAccountBalance.subtract(transferredAmount));

        var destAccount = accountService.getAccount(destinationAccountId);
        assertThat(destAccount).isNotNull();
        assertThat(destAccount.getBalance()).isEqualByComparingTo(destinationAccountBalance.add(transferredAmount));
    }

    @Test
    public void givenMultipleTransferringFundsOperationsInAnotherAccount_whenTransactionHistoryIsRequested_thenListOfTransactionsPerformedOnSrcAccountIsReturned() {
        var sourceAccountBalance = BigDecimal.valueOf(1000);
        var destinationAccountBalance = BigDecimal.valueOf(2000);
        var customerEntity1 = createCustomerEntity();
        var customerEntity2 = createCustomerEntity();

        var sourceAccountId = createAccount(customerEntity1, sourceAccountBalance).getId();
        var destinationAccountId = createAccount(customerEntity2, destinationAccountBalance).getId();

        createTransaction(BigDecimal.valueOf(500), sourceAccountId, destinationAccountId);
        createTransaction(BigDecimal.valueOf(600), sourceAccountId, destinationAccountId);
        createTransaction(BigDecimal.valueOf(700), sourceAccountId, destinationAccountId);

        var fromDateTime = now().minusHours(1).format(ISO_LOCAL_DATE_TIME);

        var url = buildUrl(customerEntity1.getId(), sourceAccountId) + "?fromDateTime=" + fromDateTime;

        var responseEntity = restTemplate.exchange(url, GET, null, List.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        assertThat(responseEntity.getBody().size()).isEqualTo(3);
    }

    private void createTransaction(BigDecimal transferredAmount, Long sourceAccountId, Long destinationAccountId) {
        transactionRepository.saveAndFlush(TransactionEntity.builder()
                                                            .srcAccId(sourceAccountId)
                                                            .destAccId(destinationAccountId)
                                                            .amount(transferredAmount)
                                                            .transactionDate(now())
                                                            .type(CREDIT)
                                                            .build());
    }

    private Transaction createTransactionRequest(BigDecimal transferredAmount, Long sourceAccountId, Long destinationAccountId) {
        return Transaction.builder()
                          .srcAccId(sourceAccountId)
                          .destAccId(destinationAccountId)
                          .amount(transferredAmount)
                          .description("transfer test")
                          .transactionDate(now())
                          .type(DEBIT)
                          .build();
    }

    private String buildUrl(long customerId, long accountId) {
        return String.format(transactionUrl, customerId, accountId);
    }

    private void assertThatReturnedDebitTransactionIsValid(BigDecimal transferredAmount, Long sourceAccountId, Long destinationAccountId,
                                                           Transaction transactionResponse) {
        assertThat(transactionResponse).isNotNull();
        assertThat(transactionResponse.getAmount()).isEqualTo(transferredAmount);
        assertThat(transactionResponse.getType()).isEqualTo(DEBIT);
        assertThat(transactionResponse.getTransactionDate()).isEqualToIgnoringSeconds(now());
        assertThat(transactionResponse.getSrcAccId()).isEqualTo(sourceAccountId);
        assertThat(transactionResponse.getDestAccId()).isEqualTo(destinationAccountId);
    }

    private void assertThatAssociatedCreditTransactionIsCreatedAndValid(BigDecimal transferredAmount, Long sourceAccountId, Long destinationAccountId) {
        var transactionEntityOptional = transactionRepository.findAllBySrcAccIdAndTransactionDateAfter(destinationAccountId, now().minusDays(1))
                                                             .stream()
                                                             .findFirst();
        assertThat(transactionEntityOptional).isNotEmpty();
        var transactionEntity = transactionEntityOptional.get();
        assertThat(transactionEntity.getAmount()).isEqualByComparingTo(transferredAmount);
        assertThat(transactionEntity.getType()).isEqualTo(CREDIT);
        assertThat(transactionEntity.getTransactionDate()).isEqualToIgnoringSeconds(now());
        assertThat(transactionEntity.getSrcAccId()).isEqualTo(destinationAccountId);
        assertThat(transactionEntity.getDestAccId()).isEqualTo(sourceAccountId);
    }


}
