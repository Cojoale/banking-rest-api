package com.gohenry.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class BankApplication {

    public static void main(String[] args) {
        MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:5.7.22"));
        mySQLContainer.start();
        SpringApplication.run(BankApplication.class, args);
    }

}