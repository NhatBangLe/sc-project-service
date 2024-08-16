package com.microservices.projectservice.exception;

import lombok.NoArgsConstructor;

import java.util.NoSuchElementException;

@NoArgsConstructor
public class NoEntityFoundException extends NoSuchElementException {
    public NoEntityFoundException(String message) {
        super(message);
    }
}
