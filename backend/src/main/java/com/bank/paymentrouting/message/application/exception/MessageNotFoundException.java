package com.bank.paymentrouting.message.application.exception;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(long id) {
        super("Message not found: " + id);
    }
}