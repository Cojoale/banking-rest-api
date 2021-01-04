package com.gohenry.bank.mapper;

import com.gohenry.bank.domain.entity.TransactionEntity;
import com.gohenry.bank.domain.model.Transaction;

import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction mapToDto(TransactionEntity transactionEntity) {
        return Transaction.builder()
                          .srcAccId(transactionEntity.getSrcAccId())
                          .destAccId(transactionEntity.getDestAccId())
                          .amount(transactionEntity.getAmount())
                          .transactionDate(transactionEntity.getCreatedAt())
                          .type(transactionEntity.getType())
                          .description(transactionEntity.getDescription())
                          .build();
    }
}

