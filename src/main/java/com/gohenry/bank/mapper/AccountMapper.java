package com.gohenry.bank.mapper;

import com.gohenry.bank.domain.entity.AccountEntity;
import com.gohenry.bank.domain.model.Account;

import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountEntity mapToEntity(Account account) {
        return AccountEntity.builder()
                            .currency(account.getCurrency())
                            .balance(account.getBalance())
                            .build();
    }

    public Account mapToDto(AccountEntity accountEntity) {
        return Account.builder()
                      .id(accountEntity.getId())
                      .currency(accountEntity.getCurrency())
                      .balance(accountEntity.getBalance())
                      .customerId(accountEntity.getCustomer().getId())
                      .accountNumber(accountEntity.getAccountNumber())
                      .build();
    }
}
