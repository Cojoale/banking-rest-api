package com.gohenry.bank.service;

import com.gohenry.bank.domain.entity.CustomerEntity;
import com.gohenry.bank.domain.model.Customer;
import com.gohenry.bank.exception.CustomerNotFoundException;
import com.gohenry.bank.mapper.CustomerMapper;
import com.gohenry.bank.repository.CustomerRepository;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public Customer createCustomer(@NonNull Customer customer) {

        var customerEntity = customerMapper.mapToEntity(customer);

        var savedCustomerEntity = customerRepository.save(customerEntity);

        return customerMapper.mapToDto(savedCustomerEntity);
    }

    public List<Customer> getCustomers() {
        log.info("Getting all existing customers");
        return customerRepository.findAll()
                                 .stream()
                                 .map(customerMapper::mapToDto)
                                 .collect(toList());
    }

    public CustomerEntity getCustomerEntity(Long customerId) {
        log.info(format("Getting details for customer with id: %d", customerId));
        return customerRepository.findById(customerId)
                                 .orElseThrow(() -> new CustomerNotFoundException(
                                         String.format("Customer having %s id was not found", customerId)
                                 ));
    }
}
