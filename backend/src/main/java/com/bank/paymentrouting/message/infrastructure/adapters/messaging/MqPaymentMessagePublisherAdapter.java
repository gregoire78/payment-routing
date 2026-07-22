package com.bank.paymentrouting.message.infrastructure.adapters.messaging;

import com.bank.paymentrouting.config.MqProperties;
import com.bank.paymentrouting.message.application.port.PaymentMessagePublisherPort;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.infrastructure.messaging.MqMessageProducerAdapter;
import org.springframework.stereotype.Component;

@Component
public class MqPaymentMessagePublisherAdapter implements PaymentMessagePublisherPort {

    private final MqMessageProducerAdapter mqMessageProducerAdapter;
    private final MqProperties mqProperties;

    public MqPaymentMessagePublisherAdapter(MqMessageProducerAdapter mqMessageProducerAdapter, MqProperties mqProperties) {
        this.mqMessageProducerAdapter = mqMessageProducerAdapter;
        this.mqProperties = mqProperties;
    }

    @Override
    public void publish(ExternalMessageId externalMessageId, MessagePayload payload) {
        mqMessageProducerAdapter.publish(mqProperties.queueName(), externalMessageId.value(), payload.value());
    }
}