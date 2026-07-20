package com.bank.paymentrouting.message.domain.event;

import java.time.Instant;

public record MessageReceivedEvent(
        String externalMessageId,
        Instant occurredAt
) implements DomainEvent {
}
