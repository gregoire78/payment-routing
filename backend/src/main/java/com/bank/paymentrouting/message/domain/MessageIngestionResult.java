package com.bank.paymentrouting.message.domain;

public record MessageIngestionResult(
        PaymentMessage message,
        boolean newlyCreated
) {
}
