package com.microservices.projectservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DataConflictException extends IllegalArgumentException {
    public DataConflictException(String message) {
        super(message);
    }
}
