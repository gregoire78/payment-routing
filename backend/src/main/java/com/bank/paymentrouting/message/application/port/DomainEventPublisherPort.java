package com.bank.paymentrouting.message.application.port;

import com.bank.paymentrouting.message.domain.event.DomainEvent;

public interface DomainEventPublisherPort {

    void publish(DomainEvent event);
}
