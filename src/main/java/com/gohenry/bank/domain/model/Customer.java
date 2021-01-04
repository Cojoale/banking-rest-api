package com.gohenry.bank.domain.model;

import javax.persistence.Access;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Customer {

    @ApiModelProperty(value = "The unique id number of the customer", readOnly = true)
    private Long id;

    @ApiModelProperty(value = "First name of the customer")
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @ApiModelProperty(value = "Last name of the customer")
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @ApiModelProperty(value = "The social security number of the customer")
    @NotBlank
    @Size(min = 13, max = 13)
    private String ssn;
}
