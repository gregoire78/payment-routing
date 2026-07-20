package com.bank.paymentrouting.message.infrastructure.persistence;

import java.time.Instant;
import java.util.Optional;

import com.bank.paymentrouting.message.domain.MessageStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessagePersistenceService {

    private final PersistedMessageRepository routedMessageRepository;

    public MessagePersistenceService(PersistedMessageRepository routedMessageRepository) {
        this.routedMessageRepository = routedMessageRepository;
    }

    @Transactional(readOnly = true)
    public Optional<PersistedMessageEntity> findByExternalMessageId(String externalMessageId) {
        return routedMessageRepository.findByExternalMessageId(externalMessageId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PersistedMessageEntity insertReceivedMessage(String externalMessageId, String payload) {
        return routedMessageRepository.saveAndFlush(new PersistedMessageEntity(
                externalMessageId,
                payload,
                MessageStatus.RECEIVED,
                Instant.now()
        ));
    }
}