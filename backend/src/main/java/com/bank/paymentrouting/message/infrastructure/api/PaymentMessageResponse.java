package com.bank.paymentrouting.message.infrastructure.api;

import java.time.Instant;

import com.bank.paymentrouting.message.domain.MessageStatus;

public record PaymentMessageResponse(
        Long id,
        String externalMessageId,
        String payload,
        MessageStatus status,
        Instant receivedAt
) {
}