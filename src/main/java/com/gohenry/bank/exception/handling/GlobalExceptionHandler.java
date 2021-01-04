package com.gohenry.bank.exception.handling;

import com.gohenry.bank.exception.AccountCreationException;
import com.gohenry.bank.exception.AccountNotFoundException;
import com.gohenry.bank.exception.TransferException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({AccountCreationException.class, TransferException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<CustomErrorResponse> badRequest(RuntimeException e) {
        var error = new CustomErrorResponse((e.getMessage()));
        error.setTimestamp(now());
        error.setStatus(BAD_REQUEST.value());

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseBody
    public ResponseEntity<CustomErrorResponse> notFound(Exception e) {
        var error = new CustomErrorResponse((e.getMessage()));
        error.setTimestamp(now());
        error.setStatus(NOT_FOUND.value());

        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<CustomErrorResponse> error500(Exception e) {
        var error = new CustomErrorResponse((e.getMessage()));
        error.setTimestamp(now());
        error.setStatus(INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }
}
