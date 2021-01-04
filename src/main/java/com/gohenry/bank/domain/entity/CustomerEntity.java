package com.gohenry.bank.domain.entity;

import org.hibernate.annotations.NaturalId;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
@Table(name = "Customer")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class CustomerEntity extends AbstractEntity{

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @NaturalId
    @Column(nullable = false)
    private String ssn;

    @OneToMany(mappedBy = "customer")
    private List<AccountEntity> accounts;
}
