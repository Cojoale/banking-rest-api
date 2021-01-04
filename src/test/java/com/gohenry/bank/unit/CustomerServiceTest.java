package com.gohenry.bank.unit;

import com.gohenry.bank.domain.entity.CustomerEntity;
import com.gohenry.bank.domain.entity.TransactionEntity;
import com.gohenry.bank.domain.model.Customer;
import com.gohenry.bank.mapper.CustomerMapper;
import com.gohenry.bank.repository.CustomerRepository;
import com.gohenry.bank.service.CustomerService;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService sut;

    @Test
    public void givenCustomerDetails_whenCustomerIsCreatingAnAccount_thenCustomerIsReturned() {
        var customer = Customer.builder()
                .firstName("Jhon")
                .lastName("Doe")
                .ssn("1212122323345")
                .build();
        var customerEntity = Mockito.mock(CustomerEntity.class);

        given(customerMapper.mapToEntity(any(Customer.class))).willReturn(customerEntity);
        given(customerRepository.save(customerEntity)).willReturn(customerEntity);
        given(customerMapper.mapToDto(any(CustomerEntity.class))).willReturn(customer);

        var customerResponse = sut.createCustomer(customer);

        assertThat(customerResponse).isNotNull();
        assertThat(customerResponse.getSsn()).isEqualTo(customer.getSsn());

        verify(customerRepository, atMostOnce()).save(any());
    }
}
