package com.bank.paymentrouting.message;

import java.time.Instant;

public record MessageView(
        Long id,
        String externalMessageId,
        String payload,
        MessageStatus status,
        Instant receivedAt
) {
    public static MessageView fromEntity(RoutedMessage message) {
        return new MessageView(
                message.getId(),
                message.getExternalMessageId(),
                message.getPayload(),
                message.getStatus(),
                message.getReceivedAt()
        );
    }
}