package com.gohenry.bank.unit;

import com.gohenry.bank.domain.entity.AccountEntity;
import com.gohenry.bank.domain.entity.TransactionEntity;
import com.gohenry.bank.domain.model.Transaction;
import com.gohenry.bank.exception.AccountNotFoundException;
import com.gohenry.bank.exception.TransferException;
import com.gohenry.bank.mapper.TransactionMapper;
import com.gohenry.bank.repository.AccountRepository;
import com.gohenry.bank.repository.TransactionRepository;
import com.gohenry.bank.service.TransferService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TransferServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransferService sut;

    @Test
    public void givenAccountId_whenTransferAmount_thenAmountIsTransferred() {
        var srcAccId = 111L;
        var destAccId = 222L;
        var transferredAmount = BigDecimal.valueOf(10);
        var transaction = buildTransaction(srcAccId, destAccId, transferredAmount);
        var srcAccountEntity = mock(AccountEntity.class);
        var destAccountEntity = mock(AccountEntity.class);

        given(accountRepository.findById(srcAccId)).willReturn(Optional.of(srcAccountEntity));
        given(accountRepository.findById(destAccId)).willReturn(Optional.of(destAccountEntity));
        given(srcAccountEntity.getBalance()).willReturn(BigDecimal.valueOf(100));
        given(destAccountEntity.getBalance()).willReturn(BigDecimal.ONE);
        given(transactionRepository.save(any(TransactionEntity.class))).willReturn(mock(TransactionEntity.class));
        given(transactionMapper.mapToDto(any(TransactionEntity.class))).willReturn(transaction);

        var actualTransaction = sut.transferFunds(transaction);

        verify(srcAccountEntity).setBalance(BigDecimal.valueOf(90));
        verify(destAccountEntity).setBalance(BigDecimal.valueOf(11));

        var transactionEntityCaptor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionRepository, times(2)).save(transactionEntityCaptor.capture());
        var transactionEntities = transactionEntityCaptor.getAllValues();
        assertThat(transactionEntities).isNotEmpty();

        var debitTransaction = transactionEntities.get(0);
        assertThat(debitTransaction.getAmount()).isEqualByComparingTo(transferredAmount);
        assertThat(debitTransaction.getSrcAccId()).isEqualTo(srcAccId);
        assertThat(debitTransaction.getDestAccId()).isEqualTo(destAccId);

        var creditTransaction = transactionEntities.get(1);
        assertThat(creditTransaction.getAmount()).isEqualByComparingTo(transferredAmount);
        assertThat(creditTransaction.getSrcAccId()).isEqualTo(destAccId);
        assertThat(creditTransaction.getDestAccId()).isEqualTo(srcAccId);

        assertThat(actualTransaction.getAmount()).isEqualByComparingTo(transferredAmount);
        assertThat(actualTransaction.getSrcAccId()).isEqualTo(debitTransaction.getSrcAccId());
        assertThat(actualTransaction.getDestAccId()).isEqualTo(debitTransaction.getDestAccId());
    }

    @Test
    public void givenValidAccountId_whenTransferredAmountIsHigherThanAvailableBalance_thenTransferExceptionIsThrown() {
        var srcAccId = 111L;
        var destAccId = 222L;
        var transferredAmount = BigDecimal.valueOf(1000);
        var transaction = buildTransaction(srcAccId, destAccId, transferredAmount);
        var srcAccountEntity = mock(AccountEntity.class);

        given(accountRepository.findById(srcAccId)).willReturn(Optional.of(srcAccountEntity));
        given(srcAccountEntity.getBalance()).willReturn(BigDecimal.valueOf(100));

        var thrown = catchThrowableOfType(() -> sut.transferFunds(transaction), TransferException.class);

        assertThat(thrown).isNotNull();
        var exceptionMessage = String.format("Insufficient funds available in your source account. Your current balance is: %d", 100);
        assertThat(thrown.getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    public void givenValidAccountId_whenTransferAmountIntoTheSameAccount_thenTransferExceptionIsThrown() {
        var srcAccId = 111L;
        var destAccId = 111L;
        var transferredAmount = BigDecimal.valueOf(10);
        var transaction = buildTransaction(srcAccId, destAccId, transferredAmount);

        var thrown = catchThrowableOfType(() -> sut.transferFunds(transaction), TransferException.class);

        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isEqualTo("Transfer operation can't be done on the same account");
    }

    @Test
    public void givenInvalidAccountId_whenTransferAmount_thenTransferExceptionIsThrown() {
        var srcAccId = 111L;
        var destAccId = 222L;
        var transferredAmount = BigDecimal.valueOf(100);
        var transaction = buildTransaction(srcAccId, destAccId, transferredAmount);

        given(accountRepository.findById(srcAccId)).willReturn(Optional.empty());

        var thrown = catchThrowableOfType(() -> sut.transferFunds(transaction), AccountNotFoundException.class);

        assertThat(thrown).isNotNull();
        var exceptionMessage = String.format("Account with %d id was not found", srcAccId);
        assertThat(thrown.getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    public void givenAccountId_whenGettingAssociatedAccountTransactionsAfterMentionedTime_thenListOfTransactionsIsReturned() {
        var accountId = 100L;
        var transactionsStartDate = LocalDateTime.of(2021, 1, 4, 12, 10,39).format(ISO_LOCAL_DATE_TIME);
        var transactionEntity = mock(TransactionEntity.class);

        var transaction = buildTransaction(accountId, 222l, BigDecimal.valueOf(10L));

        given(transactionRepository.findAllBySrcAccIdAndTransactionDateAfter(anyLong(), any(LocalDateTime.class))).willReturn(List.of(transactionEntity));

        given(transactionMapper.mapToDto(any(TransactionEntity.class))).willReturn(transaction);

        List<Transaction> transactionsHistory = sut.getTransactions(accountId, transactionsStartDate);

        assertThat(transactionsHistory).isNotEmpty();
        assertThat(transactionsHistory.size()).isEqualTo(1);
        assertThat(transactionsHistory.stream().findFirst().get().getSrcAccId()).isEqualTo(accountId);
    }

    private Transaction buildTransaction(long srcAccId, long destAccId, BigDecimal transferredAmount) {
        return Transaction.builder()
                .amount(transferredAmount)
                .srcAccId(srcAccId)
                .destAccId(destAccId)
                .build();
    }
}