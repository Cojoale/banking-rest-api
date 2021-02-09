package com.gohenry.bank.exception.handling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
class ValidationError {
    private final String field;
    private final String message;
}
