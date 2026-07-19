package com.bank.paymentrouting.message;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessagePersistenceService {

    private final RoutedMessageRepository routedMessageRepository;

    public MessagePersistenceService(RoutedMessageRepository routedMessageRepository) {
        this.routedMessageRepository = routedMessageRepository;
    }

    @Transactional(readOnly = true)
    public Optional<RoutedMessage> findByExternalMessageId(String externalMessageId) {
        return routedMessageRepository.findByExternalMessageId(externalMessageId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoutedMessage insertReceivedMessage(String externalMessageId, String payload) {
        return routedMessageRepository.saveAndFlush(new RoutedMessage(
                externalMessageId,
                payload,
                MessageStatus.RECEIVED,
                Instant.now()
        ));
    }
}