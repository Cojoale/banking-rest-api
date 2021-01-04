package com.gohenry.bank.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@Table(name = "Transaction")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class TransactionEntity extends AbstractEntity {

    @Column(nullable = false)
    private Long srcAccId;

    @Column(nullable = false)
    private Long destAccId;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    private LocalDateTime transactionDate;
}
