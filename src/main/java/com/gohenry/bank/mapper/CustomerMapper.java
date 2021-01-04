package com.gohenry.bank.mapper;

import com.gohenry.bank.domain.entity.CustomerEntity;
import com.gohenry.bank.domain.model.Customer;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomerMapper {

    public CustomerEntity mapToEntity(Customer customer) {

        return CustomerEntity.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .ssn(customer.getSsn())
                .build();
    }

    public Customer mapToDto(CustomerEntity customerEntity) {

        return Customer.builder()
                .id(customerEntity.getId())
                .firstName(customerEntity.getFirstName())
                .lastName(customerEntity.getLastName())
                .ssn(customerEntity.getSsn())
                .build();
    }
}
