package com.gohenry.bank.exception.handling;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private String errorMsg;

    private int status;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime generatedAt;

    private List<ValidationError> errors;


    public ErrorResponse(int statusCode, String message,LocalDateTime timestamp) {
        this.status = statusCode;
        this.errorMsg = message;
        this.generatedAt = timestamp;
    }

    public void addValidationError(String field, String message) {
        if (Objects.isNull(errors)) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }
}
