package com.bank.paymentrouting.message.infrastructure.adapters.messaging;

import com.bank.paymentrouting.config.MqProperties;
import com.bank.paymentrouting.message.application.port.PaymentMessagePublisherPort;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.infrastructure.messaging.MqMessageProducer;
import org.springframework.stereotype.Component;

@Component
public class MqPaymentMessagePublisherAdapter implements PaymentMessagePublisherPort {

    private final MqMessageProducer mqMessageProducer;
    private final MqProperties mqProperties;

    public MqPaymentMessagePublisherAdapter(MqMessageProducer mqMessageProducer, MqProperties mqProperties) {
        this.mqMessageProducer = mqMessageProducer;
        this.mqProperties = mqProperties;
    }

    @Override
    public void publish(ExternalMessageId externalMessageId, MessagePayload payload) {
        mqMessageProducer.publish(mqProperties.queueName(), externalMessageId.value(), payload.value());
    }
}