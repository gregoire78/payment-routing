package com.bank.paymentrouting.message;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(long id) {
        super("Message not found: " + id);
    }
}