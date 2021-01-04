package com.gohenry.bank.domain.model;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.gohenry.bank.domain.model.Currency.EUR;
import static io.swagger.annotations.ApiModelProperty.AccessMode.READ_ONLY;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @ApiModelProperty(value ="The id number of the created account",readOnly = true)
    private Long id;

    @ApiModelProperty(value = "The id number of the owner of the account",readOnly = true)
    private Long customerId;

    @ApiModelProperty(value = "The IBAN of the account", readOnly = true)
    private String accountNumber;

    @ApiModelProperty(value = "The initial deposit amount of the account")
    @NotNull
    @Min(value = 0)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal balance;

    @ApiModelProperty(
            value = "The currency of the requested account",
            allowableValues = "GBP, EUR")
    @Builder.Default
    private Currency currency = EUR;
}
