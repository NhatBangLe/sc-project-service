package com.microservices.projectservice.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NoEntityFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNoEntityFoundException(NoEntityFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(IllegalAttributeException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public String handleIllegalAttributeException(IllegalAttributeException e) {
        return e.getMessage();
    }

    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public String handleDataConflictException(DataConflictException e) {
        return e.getMessage();
    }

}
