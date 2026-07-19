package com.bank.paymentrouting.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class MessageIngestionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageIngestionService.class);

    private final MessagePersistenceService messagePersistenceService;

    public MessageIngestionService(MessagePersistenceService messagePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
    }

    public MessageView ingest(String externalMessageId, String payload) {
        return messagePersistenceService.findByExternalMessageId(externalMessageId)
                .map(MessageView::fromEntity)
                .orElseGet(() -> insertOrLoadExisting(externalMessageId, payload));
    }

    private MessageView insertOrLoadExisting(String externalMessageId, String payload) {
        try {
            RoutedMessage routedMessage = messagePersistenceService.insertReceivedMessage(externalMessageId, payload);
            return MessageView.fromEntity(routedMessage);
        } catch (DataIntegrityViolationException exception) {
            LOGGER.info("Duplicate message ignored for externalMessageId={}", externalMessageId);
            return messagePersistenceService.findByExternalMessageId(externalMessageId)
                    .map(MessageView::fromEntity)
                    .orElseThrow(() -> exception);
        }
    }
}