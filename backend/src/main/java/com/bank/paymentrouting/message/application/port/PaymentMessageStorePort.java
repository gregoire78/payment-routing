package com.bank.paymentrouting.message.application.port;

import java.util.Optional;

import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.domain.MessagePayload;

public interface PaymentMessageStorePort {

    Optional<PaymentMessage> findByExternalMessageId(ExternalMessageId externalMessageId);

    PaymentMessage insertReceivedMessage(ExternalMessageId externalMessageId, MessagePayload payload);
}
