package com.bank.paymentrouting.message.domain;

import java.util.Optional;

import com.bank.paymentrouting.message.application.port.PaymentMessageStorePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

public class MessageIngestionDomainService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageIngestionDomainService.class);

    public MessageIngestionResult ingest(
            PaymentMessageStorePort messageStore,
            ExternalMessageId externalMessageId,
            MessagePayload payload
    ) {
        return messageStore.findByExternalMessageId(externalMessageId)
                .map(existing -> new MessageIngestionResult(existing, false))
                .orElseGet(() -> insertOrLoadExisting(messageStore, externalMessageId, payload));
    }

    private MessageIngestionResult insertOrLoadExisting(
            PaymentMessageStorePort messageStore,
            ExternalMessageId externalMessageId,
            MessagePayload payload
    ) {
        try {
            PaymentMessage inserted = messageStore.insertReceivedMessage(externalMessageId, payload);
            return new MessageIngestionResult(inserted, true);
        } catch (DataIntegrityViolationException exception) {
            LOGGER.info("Duplicate message ignored for externalMessageId={}", externalMessageId.value());
            Optional<PaymentMessage> existing = messageStore.findByExternalMessageId(externalMessageId);
            return new MessageIngestionResult(existing.orElseThrow(() -> exception), false);
        }
    }
}
