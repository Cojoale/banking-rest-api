package com.gohenry.bank.controller;

import com.gohenry.bank.domain.model.Transaction;
import com.gohenry.bank.service.TransferService;

import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping(value = "/customers/{customerId}/accounts/{accountId}/transactions")
@AllArgsConstructor
@Api(tags = "Transaction API")
@Slf4j
public class TransactionController {

    private final TransferService transferService;

    @ApiOperation(value = "Transfer funds between two different accounts", response = Transaction.class)
    @PostMapping
    @ResponseStatus(CREATED)
    public Transaction transferFunds(@PathVariable("accountId") long accountId,
                                     @RequestBody @Valid Transaction transaction) {
        transaction.setSrcAccId(accountId);
        return transferService.transferFunds(transaction);
    }

    @ApiOperation(value = "Get transaction history of a given account", response = Transaction.class,
            responseContainer = "List")
    @GetMapping
    @ResponseStatus(OK)
    public List<Transaction> getTransactionsHistory(@PathVariable("accountId") long accountId,
                                                    @ApiParam(
                                                            value = "The start date of the account transactions history",
                                                            required = true)
                                                    @RequestParam("fromDateTime") String fromDateTime) {
        return transferService.getTransactions(accountId, fromDateTime);
    }
}
