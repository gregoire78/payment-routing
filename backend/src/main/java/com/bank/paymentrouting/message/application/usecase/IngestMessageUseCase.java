package com.bank.paymentrouting.message.application.usecase;

import java.time.Instant;

import com.bank.paymentrouting.message.application.port.DomainEventPublisherPort;
import com.bank.paymentrouting.message.domain.MessageIngestionResult;
import com.bank.paymentrouting.message.domain.event.MessageReceivedEvent;
import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.application.port.PaymentMessageStorePort;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.domain.MessageIngestionDomainService;
import org.springframework.stereotype.Service;

@Service
public class IngestMessageUseCase {

    private final PaymentMessageStorePort messageStore;
    private final MessageIngestionDomainService messageIngestionDomainService;
    private final DomainEventPublisherPort domainEventPublisher;

    public IngestMessageUseCase(
            PaymentMessageStorePort messageStore,
            MessageIngestionDomainService messageIngestionDomainService,
            DomainEventPublisherPort domainEventPublisher
    ) {
        this.messageStore = messageStore;
        this.messageIngestionDomainService = messageIngestionDomainService;
        this.domainEventPublisher = domainEventPublisher;
    }

    public PaymentMessageRecord execute(String externalMessageId, String payload) {
        MessageIngestionResult ingestionResult = messageIngestionDomainService.ingest(
                messageStore,
                ExternalMessageId.of(externalMessageId),
                MessagePayload.of(payload)
        );
        PaymentMessage message = ingestionResult.message();
        if (ingestionResult.newlyCreated()) {
            domainEventPublisher.publish(new MessageReceivedEvent(message.externalMessageId().value(), Instant.now()));
        }
        return new PaymentMessageRecord(
            message.id(),
            message.externalMessageId().value(),
            message.payload().value(),
            message.status(),
            message.receivedAt()
        );
    }
}
