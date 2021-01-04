package com.gohenry.bank.service;

import com.gohenry.bank.domain.entity.AccountEntity;
import com.gohenry.bank.domain.entity.TransactionEntity;
import com.gohenry.bank.domain.model.Transaction;
import com.gohenry.bank.exception.AccountNotFoundException;
import com.gohenry.bank.exception.TransferException;
import com.gohenry.bank.mapper.TransactionMapper;
import com.gohenry.bank.repository.AccountRepository;
import com.gohenry.bank.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.gohenry.bank.domain.entity.TransactionType.CREDIT;
import static com.gohenry.bank.domain.entity.TransactionType.DEBIT;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class TransferService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final TransactionMapper transactionMapper;

    /**
     * Here the srcAccountID is considered to be the owner account and the initiator of the transaction,
     * hence we return the DEBIT transaction, the one that belongs to the owner
     */
    @Transactional
    public Transaction transferFunds(@NonNull Transaction transaction) {
        log.info(format("Transferring amount: %d from source account with id: %d to destination account id: %d"
                , transaction.getAmount().intValue(), transaction.getSrcAccId().intValue(), transaction.getDestAccId().intValue()));
        checkDifferentAccounts(transaction);

        var srcAccId = transaction.getSrcAccId();
        var destAccId = transaction.getDestAccId();

        var sourceAccountEntity = getAccount(srcAccId);
        var amount = transaction.getAmount();
        checkAvailableBalance(sourceAccountEntity, amount);

        var destAccountEntity = getAccount(destAccId);

        sourceAccountEntity.setBalance(sourceAccountEntity.getBalance().subtract(amount));
        destAccountEntity.setBalance(destAccountEntity.getBalance().add(amount));

        var debitTransaction = createDebitTransaction(transaction);
        createCreditTransaction(transaction);

        return debitTransaction;
    }

    private Transaction createDebitTransaction(Transaction transaction) {
        var transactionEntity = transactionRepository.save(TransactionEntity.builder()
                .srcAccId(transaction.getSrcAccId())
                .destAccId(transaction.getDestAccId())
                .amount(transaction.getAmount())
                .transactionDate(now())
                .description(transaction.getDescription())
                .type(DEBIT)
                .build()
        );

        return transactionMapper.mapToDto(transactionEntity);
    }

    private void createCreditTransaction(Transaction transaction) {
        transactionRepository.save(TransactionEntity.builder()
                .srcAccId(transaction.getDestAccId())
                .destAccId(transaction.getSrcAccId())
                .amount(transaction.getAmount())
                .transactionDate(now())
                .description(transaction.getDescription())
                .type(CREDIT)
                .build()
        );
    }

    private void checkDifferentAccounts(Transaction transaction) {
        if (transaction.getSrcAccId() == transaction.getDestAccId()) {
            var transferException = new TransferException("Transfer operation can't be done on the same account");
            log.error(transferException.getMessage(), transferException);
            throw transferException;
        }
    }

    private void checkAvailableBalance(AccountEntity account, BigDecimal amount) {
        var balance = account.getBalance();
        if (balance.compareTo(amount) < 0) {
            var transferException = new TransferException(format("Insufficient funds available in your " +
                    "source account. Your current balance is: %s", balance.toPlainString()));
            log.error(transferException.getMessage(), transferException);
            throw transferException;
        }
    }

    private AccountEntity getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    var accountNotFoundException = new AccountNotFoundException(
                            format("Account with %d id was not found", accountId));
                    log.error(accountNotFoundException.getMessage(), accountNotFoundException);
                    return accountNotFoundException;
                });
    }

    public List<Transaction> getTransactions(Long accountId, String transactionsStartDate) {
        log.info(format("Getting transactions history for account with id: %d starting with %s",accountId ,transactionsStartDate));
        var transactionDate = LocalDateTime.parse(transactionsStartDate, ISO_LOCAL_DATE_TIME);

        return transactionRepository.findAllBySrcAccIdAndTransactionDateAfter(accountId, transactionDate)
                .stream()
                .map(transactionMapper::mapToDto)
                .collect(toList());
    }
}
