package com.microservices.projectservice.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid operator")
@NoArgsConstructor
public class UnexpectedOperatorException extends RuntimeException {
    public UnexpectedOperatorException(String message) {
        super(message);
    }
}
