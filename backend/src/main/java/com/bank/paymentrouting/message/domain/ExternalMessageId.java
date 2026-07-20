package com.bank.paymentrouting.message.domain;

import java.util.Objects;

public record ExternalMessageId(String value) {

    public ExternalMessageId {
        Objects.requireNonNull(value, "externalMessageId must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("externalMessageId must not be blank");
        }
    }

    public static ExternalMessageId of(String value) {
        return new ExternalMessageId(value);
    }
}
