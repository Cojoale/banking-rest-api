package com.gohenry.bank.domain.model;

import com.gohenry.bank.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {

    @ApiModelProperty(value = "The IBAN of the sender")
    @NotNull
    private Long srcAccId;

    @ApiModelProperty(value = "The IBAN of the receiver")
    @NotNull
    private Long destAccId;

    @ApiModelProperty(value = "The transferred amount")
    @NotNull
    @Min(value = 0)
    @Digits(integer = 5,fraction = 2)
    private BigDecimal amount;

    @ApiModelProperty(value = "The description of the transfer")
    private String description;

    @ApiModelProperty(value = "The type of the transfer",
            allowableValues = "DEBIT, CREDIT", readOnly = true)
    private TransactionType type;

    @ApiModelProperty(value = "The date of the transfer", readOnly = true)
    private LocalDateTime transactionDate;
}
