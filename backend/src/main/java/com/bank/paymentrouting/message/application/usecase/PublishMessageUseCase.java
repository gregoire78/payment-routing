package com.bank.paymentrouting.message.application.usecase;

import java.time.Instant;

import com.bank.paymentrouting.message.application.port.DomainEventPublisherPort;
import com.bank.paymentrouting.message.application.port.PaymentMessagePublisherPort;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.domain.event.MessagePublishedEvent;
import org.springframework.stereotype.Service;

@Service
public class PublishMessageUseCase {

    private final PaymentMessagePublisherPort messagePublisher;
    private final DomainEventPublisherPort domainEventPublisher;

    public PublishMessageUseCase(PaymentMessagePublisherPort messagePublisher, DomainEventPublisherPort domainEventPublisher) {
        this.messagePublisher = messagePublisher;
        this.domainEventPublisher = domainEventPublisher;
    }

    public void execute(String externalMessageId, String payload) {
        messagePublisher.publish(ExternalMessageId.of(externalMessageId), MessagePayload.of(payload));
        domainEventPublisher.publish(new MessagePublishedEvent(externalMessageId, Instant.now()));
    }
}
