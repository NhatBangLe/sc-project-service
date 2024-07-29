package com.microservices.projectservice.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Some attributes is not acceptable")
@NoArgsConstructor
public class IllegalAttributeException extends RuntimeException {

    public IllegalAttributeException(String message) {
        super(message);
    }
}
