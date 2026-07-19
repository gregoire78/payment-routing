package com.bank.paymentrouting.message;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MqMessageProducer {

    private final JmsTemplate jmsTemplate;

    public MqMessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void publish(String queueName, PublishMessageRequest request) {
        jmsTemplate.send(queueName, session -> {
            var message = session.createTextMessage(request.payload());
            message.setJMSCorrelationID(request.externalMessageId());
            return message;
        });
    }
}