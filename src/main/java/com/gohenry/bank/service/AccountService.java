package com.gohenry.bank.service;

import com.gohenry.bank.domain.model.Account;
import com.gohenry.bank.exception.AccountCreationException;
import com.gohenry.bank.exception.AccountNotFoundException;
import com.gohenry.bank.exception.CustomerNotFoundException;
import com.gohenry.bank.mapper.AccountMapper;
import com.gohenry.bank.repository.AccountRepository;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    private final CustomerService customerService;

    public Account createAccount(Account account) {
        log.info(format("Start creating account with initial deposit of %d %s , customer id: %d",
                account.getBalance().intValue(), account.getCurrency(), account.getCustomerId()));
        try {
            var customerEntity = customerService.getCustomerEntity(account.getCustomerId());
            var accountEntity = accountMapper.mapToEntity(account);

            accountEntity.setCustomer(customerEntity);
            accountEntity.setAccountNumber(format("GHNR%d", currentTimeMillis()));

            var savedAccountEntity = accountRepository.save(accountEntity);

            return accountMapper.mapToDto(savedAccountEntity);
        } catch (CustomerNotFoundException e) {
            log.error(format("Exception while creating account. Customer with %d not found", account.getCustomerId()));
            throw new AccountCreationException(e.getMessage());
        }
    }

    public Account getAccount(Long accountId) {
        log.info(format("Getting account details for account with id: %d ", accountId));
        return accountRepository.findById(accountId)
                .map(accountMapper::mapToDto)
                .orElseThrow(() -> new AccountNotFoundException(format("Account with id: %d was not found.", accountId)
                ));
    }
}
