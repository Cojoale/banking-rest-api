package com.gohenry.bank.exception;

public class TransferException extends RuntimeException {
    public TransferException(String message) {
        super(message);
    }
}
