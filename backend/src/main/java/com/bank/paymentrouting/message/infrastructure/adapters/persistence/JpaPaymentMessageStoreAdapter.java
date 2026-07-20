package com.bank.paymentrouting.message.infrastructure.adapters.persistence;

import java.util.Optional;

import com.bank.paymentrouting.message.application.port.PaymentMessageStorePort;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.infrastructure.persistence.MessagePersistenceService;
import com.bank.paymentrouting.message.infrastructure.persistence.PersistedMessageEntity;
import com.bank.paymentrouting.message.infrastructure.persistence.mapper.RoutedMessageMapper;
import org.springframework.stereotype.Component;

@Component
public class JpaPaymentMessageStoreAdapter implements PaymentMessageStorePort {

    private final MessagePersistenceService messagePersistenceService;
    private final RoutedMessageMapper routedMessageMapper;

    public JpaPaymentMessageStoreAdapter(
            MessagePersistenceService messagePersistenceService,
            RoutedMessageMapper routedMessageMapper
    ) {
        this.messagePersistenceService = messagePersistenceService;
        this.routedMessageMapper = routedMessageMapper;
    }

    @Override
    public Optional<PaymentMessage> findByExternalMessageId(ExternalMessageId externalMessageId) {
        return messagePersistenceService.findByExternalMessageId(externalMessageId.value())
                .map(routedMessageMapper::toDomain);
    }

    @Override
    public PaymentMessage insertReceivedMessage(ExternalMessageId externalMessageId, MessagePayload payload) {
        PersistedMessageEntity routedMessage = messagePersistenceService.insertReceivedMessage(
                externalMessageId.value(),
                payload.value()
        );
        return routedMessageMapper.toDomain(routedMessage);
    }
}