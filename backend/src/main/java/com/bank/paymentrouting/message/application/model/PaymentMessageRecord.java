package com.bank.paymentrouting.message.application.model;

import java.time.Instant;

import com.bank.paymentrouting.message.domain.MessageStatus;

public record PaymentMessageRecord(
        Long id,
        String externalMessageId,
        String payload,
        MessageStatus status,
        Instant receivedAt
) {
}
