package com.bank.paymentrouting.message.infrastructure.adapters.messaging;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.paymentrouting.config.MqProperties;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.infrastructure.messaging.MqMessageProducerAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MqPaymentMessagePublisherAdapter")
class MqPaymentMessagePublisherAdapterTest {

    @Mock
    private MqMessageProducerAdapter mqMessageProducerAdapter;

    @Mock
    private MqProperties mqProperties;

    @InjectMocks
    private MqPaymentMessagePublisherAdapter mqMessagePublisherAdapter;

    @Nested
    @DisplayName("publish")
    class Publish {

        @Test
        @DisplayName("publishes message to configured queue")
        void testPublishesMessageToConfiguredQueue() {
            // GIVEN
            when(mqProperties.queueName()).thenReturn("DEV.QUEUE.1");
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-001");
            MessagePayload payload = MessagePayload.of("payload");

            // WHEN
            mqMessagePublisherAdapter.publish(externalMessageId, payload);

            // THEN
            verify(mqMessageProducerAdapter).publish("DEV.QUEUE.1", "MSG-001", "payload");
        }
    }
}
