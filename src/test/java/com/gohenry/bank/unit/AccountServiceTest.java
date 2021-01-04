package com.gohenry.bank.unit;


import com.gohenry.bank.domain.entity.AccountEntity;
import com.gohenry.bank.domain.entity.CustomerEntity;
import com.gohenry.bank.domain.model.Account;
import com.gohenry.bank.domain.model.Currency;
import com.gohenry.bank.exception.AccountCreationException;
import com.gohenry.bank.exception.CustomerNotFoundException;
import com.gohenry.bank.mapper.AccountMapper;
import com.gohenry.bank.repository.AccountRepository;
import com.gohenry.bank.service.AccountService;
import com.gohenry.bank.service.CustomerService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AccountService sut;

    @Test
    public void givenCreateAccountRequest_whenCreatingAnAccount_thenAccountIsCreated() {
        var customerId = 100L;
        var createAccountRequest = Account.builder()
                .customerId(customerId)
                .balance(new BigDecimal("10.00"))
                .build();

        var customerEntity = mock(CustomerEntity.class);
        var accountEntity = mock(AccountEntity.class);
        var savedAccountEntity = mock(AccountEntity.class);
        var createAccountResponse = mock(Account.class);

        given(customerService.getCustomerEntity(customerId)).willReturn(customerEntity);
        given(accountMapper.mapToEntity(createAccountRequest)).willReturn(accountEntity);
        given(accountRepository.save(accountEntity)).willReturn(savedAccountEntity);
        given(accountMapper.mapToDto(savedAccountEntity)).willReturn(createAccountResponse);

        var accountResponse = sut.createAccount(createAccountRequest);

        assertThat(accountResponse).isSameAs(createAccountResponse);
        verify(accountEntity).setAccountNumber(anyString());
    }

    @Test
    public void givenCreateAccountRequest_whenCreatingAnAccountAndCustomerNotFound_thenAccountIsNotCreated() {
        var customerId = 100L;
        var createAccountRequest = Account.builder()
                .customerId(customerId)
                .balance(BigDecimal.valueOf(100L))
                .currency(Currency.EUR)
                .build();
        given(customerService.getCustomerEntity(customerId)).willThrow(CustomerNotFoundException.class);

        var thrown = catchThrowableOfType(() -> sut.createAccount(createAccountRequest), AccountCreationException.class);

        assertThat(thrown).isNotNull();
    }
}

