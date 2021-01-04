package com.gohenry.bank.controller;

import com.gohenry.bank.domain.model.Customer;
import com.gohenry.bank.service.CustomerService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
@Api(tags = "Customer API")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @ApiOperation(value = "Retrieve all bank customers", response = Customer.class,
            responseContainer = "List")
    @GetMapping
    @ResponseStatus(OK)
    public List<Customer> getAll() {
        return customerService.getCustomers();
    }

    @ApiOperation(value = "Creates and returns a new customer", response = Customer.class)
    @PostMapping
    @ResponseStatus(CREATED)
    public Customer create(@RequestBody @Valid Customer customer) {
        return customerService.createCustomer(customer);
    }
}
