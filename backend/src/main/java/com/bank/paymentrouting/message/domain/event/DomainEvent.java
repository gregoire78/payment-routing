package com.bank.paymentrouting.message.domain.event;

import java.time.Instant;

public interface DomainEvent {

    Instant occurredAt();
}
