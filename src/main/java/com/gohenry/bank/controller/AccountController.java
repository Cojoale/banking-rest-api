package com.gohenry.bank.controller;

import com.gohenry.bank.domain.model.Account;
import com.gohenry.bank.service.AccountService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "customers/{customerId}/accounts")
@AllArgsConstructor
@Api(tags = "Account API")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @ApiOperation(value = "Creates and returns an account with its associated account number and current balance", response = Account.class)
    @PostMapping()
    @ResponseStatus(CREATED)
    public Account create(@PathVariable("customerId") long customerId,
                          @RequestBody @Valid Account account) {
        account.setCustomerId(customerId);
        return accountService.createAccount(account);
    }

    @ApiOperation(value = "Get available balance for a specific account", response = Account.class)
    @GetMapping("{accountId}")
    @ResponseStatus(OK)
    public Account get(@PathVariable("accountId") long accountId) {
        return accountService.getAccount(accountId);
    }
}