package com.gohenry.bank.domain.entity;

import com.gohenry.bank.domain.model.Currency;

import java.math.BigDecimal;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@Table(name = "Account")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class AccountEntity extends AbstractEntity {

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}
