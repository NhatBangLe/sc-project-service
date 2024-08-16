package com.microservices.projectservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IllegalAttributeException extends IllegalArgumentException {
    public IllegalAttributeException(String message) {
        super(message);
    }
}
