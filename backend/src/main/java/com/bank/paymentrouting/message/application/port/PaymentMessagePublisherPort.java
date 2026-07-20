package com.bank.paymentrouting.message.application.port;

import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.MessagePayload;

public interface PaymentMessagePublisherPort {

    void publish(ExternalMessageId externalMessageId, MessagePayload payload);
}
