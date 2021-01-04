package com.gohenry.bank.repository;


import com.gohenry.bank.domain.entity.TransactionEntity;

import com.gohenry.bank.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
     List<TransactionEntity> findAllBySrcAccIdAndTransactionDateAfter(Long srcAccId, LocalDateTime transactionDate);
}
