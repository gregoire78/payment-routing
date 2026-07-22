package com.bank.paymentrouting.message.infrastructure.messaging;

import java.util.UUID;

import com.bank.paymentrouting.message.application.port.IngestMessagePort;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.mq.listener-enabled", havingValue = "true")
public class MqMessageListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqMessageListenerAdapter.class);

    private final IngestMessagePort ingestMessagePort;

    public MqMessageListenerAdapter(IngestMessagePort ingestMessagePort) {
        this.ingestMessagePort = ingestMessagePort;
    }

    @JmsListener(destination = "${app.mq.queue-name}", containerFactory = "mqJmsListenerContainerFactory")
    public void onMessage(Message message) throws JMSException {
        String payload = message.getBody(String.class);
        String externalMessageId = resolveExternalMessageId(message);

        ingestMessagePort.execute(externalMessageId, payload);
        LOGGER.debug("Message received from MQ with externalMessageId={}", externalMessageId);
    }

    private String resolveExternalMessageId(Message message) throws JMSException {
        String correlationId = message.getJMSCorrelationID();
        if (correlationId != null && !correlationId.isBlank()) {
            return correlationId;
        }

        String messageId = message.getJMSMessageID();
        if (messageId != null && !messageId.isBlank()) {
            return messageId;
        }

        return "generated-" + UUID.randomUUID();
    }
}
