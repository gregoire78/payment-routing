package com.bank.paymentrouting.message.domain;

import java.util.Objects;

public record MessagePayload(String value) {

    public MessagePayload {
        Objects.requireNonNull(value, "payload must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("payload must not be blank");
        }
    }

    public static MessagePayload of(String value) {
        return new MessagePayload(value);
    }
}
