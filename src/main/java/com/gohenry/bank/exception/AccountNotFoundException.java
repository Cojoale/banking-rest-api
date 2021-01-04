package com.gohenry.bank.exception;

public class AccountNotFoundException extends RuntimeException{
    public  AccountNotFoundException(String messsage){
        super(messsage);
    }
}
