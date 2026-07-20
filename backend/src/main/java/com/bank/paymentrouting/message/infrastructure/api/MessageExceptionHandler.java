package com.bank.paymentrouting.message.infrastructure.api;

import java.time.Instant;
import java.util.Map;

import com.bank.paymentrouting.message.application.exception.MessageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MessageExceptionHandler {

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(MessageNotFoundException exception) {
        return Map.of(
                "timestamp", Instant.now(),
                "message", exception.getMessage()
        );
    }
}