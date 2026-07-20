package com.bank.paymentrouting.message.infrastructure.adapters.event;

import com.bank.paymentrouting.message.application.port.DomainEventPublisherPort;
import com.bank.paymentrouting.message.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringDomainEventPublisherAdapter implements DomainEventPublisherPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}