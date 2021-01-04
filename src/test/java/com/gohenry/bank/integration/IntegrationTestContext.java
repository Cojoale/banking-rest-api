package com.gohenry.bank.integration;

import com.gohenry.bank.domain.entity.AccountEntity;
import com.gohenry.bank.domain.entity.CustomerEntity;
import com.gohenry.bank.repository.AccountRepository;
import com.gohenry.bank.repository.CustomerRepository;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.Random;

import static java.lang.System.currentTimeMillis;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("integration")
public class IntegrationTestContext {

    @ClassRule
    public static MySQLContainer mysql = new MySQLContainer<>(DockerImageName.parse("mysql:5.7.22"));

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String url = "http://localhost:%d/customers";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @LocalServerPort
    private int port;

    @Before
    public void config() {
        url = String.format(url, port);
    }

    protected AccountEntity createAccount(CustomerEntity customer, BigDecimal initalAmount) {
        return accountRepository.save(AccountEntity.builder()
                                                   .customer(customer)
                                                   .balance(initalAmount)
                                                   .accountNumber(String.format("GHNR%d", currentTimeMillis()))
                                                   .build());
    }

    protected CustomerEntity createCustomerEntity() {
        return customerRepository.save(CustomerEntity.builder()
                                                     .ssn(generateRandomSsn())
                                                     .firstName("John")
                                                     .lastName("Doe")
                                                     .build());
    }

    private String generateRandomSsn() {
        return new StringBuilder().append(new Random().nextLong()).subSequence(1, 14).toString();
    }

    @Test
    public void contextLoad(){
    }
}
