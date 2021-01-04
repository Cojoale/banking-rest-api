package com.gohenry.bank.exception.handling;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
@NoArgsConstructor
public class CustomErrorResponse {

    private String errorMsg;

    private int status;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime timestamp;

    public CustomErrorResponse(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
