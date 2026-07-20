package com.bank.paymentrouting.message.infrastructure.persistence.mapper;

import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.infrastructure.persistence.PersistedMessageEntity;
import org.springframework.stereotype.Component;

@Component
public class RoutedMessageMapper {

    public PaymentMessage toDomain(PersistedMessageEntity routedMessage) {
        return PaymentMessage.restore(
                routedMessage.getId(),
                ExternalMessageId.of(routedMessage.getExternalMessageId()),
                MessagePayload.of(routedMessage.getPayload()),
                routedMessage.getStatus(),
                routedMessage.getReceivedAt()
        );
    }
}
