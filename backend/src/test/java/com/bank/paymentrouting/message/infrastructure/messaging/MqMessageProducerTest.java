package com.bank.paymentrouting.message.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import com.bank.paymentrouting.config.MqProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("MqMessageProducer")
class MqMessageProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Spy
    private Clock clock = Clock.fixed(Instant.parse("2026-07-20T12:00:00Z"), ZoneOffset.UTC);

    private MqProperties newProperties(int maxAttempts, int threshold) {
        return new MqProperties(
                "QM1",
                "DEV.APP.SVRCONN",
                "ibmmq(1414)",
                "DEV.QUEUE.1",
                "app",
                "passw0rd",
                true,
                "1-1",
                maxAttempts,
                0L,
                1500L,
                threshold,
                30_000L
        );
    }

    @Nested
    @DisplayName("publish")
    class Publish {

        @Test
        @DisplayName("retries publish when transient failures happen")
        void testRetriesPublishWhenTransientFailuresHappen() {
            // GIVEN
            MqMessageProducer producer = new MqMessageProducer(jmsTemplate, newProperties(3, 5), clock);
            doThrow(new JmsException("first") { })
                    .doThrow(new JmsException("second") { })
                    .doNothing()
                    .when(jmsTemplate)
                    .send(eq("DEV.QUEUE.1"), any());

            // WHEN
            producer.publish("DEV.QUEUE.1", "MSG-001", "payload");

            // THEN
            verify(jmsTemplate, times(3)).send(eq("DEV.QUEUE.1"), any());
        }

        @Test
        @DisplayName("opens circuit after configured failures and rejects immediately")
        void testOpensCircuitAfterConfiguredFailuresAndRejectsImmediately() {
            // GIVEN
            MqMessageProducer producer = new MqMessageProducer(jmsTemplate, newProperties(1, 2), clock);
            doThrow(new JmsException("fail") { })
                    .when(jmsTemplate)
                    .send(eq("DEV.QUEUE.1"), any());

            // WHEN / THEN
            assertThatThrownBy(() -> producer.publish("DEV.QUEUE.1", "MSG-001", "payload"))
                    .isInstanceOf(JmsException.class)
                    .hasMessageContaining("fail");
            assertThatThrownBy(() -> producer.publish("DEV.QUEUE.1", "MSG-001", "payload"))
                    .isInstanceOf(JmsException.class)
                    .hasMessageContaining("fail");

            assertThatThrownBy(() -> producer.publish("DEV.QUEUE.1", "MSG-001", "payload"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("circuit is open");

            verify(jmsTemplate, times(2)).send(eq("DEV.QUEUE.1"), any());
        }

        @Test
        @DisplayName("resets circuit after a successful publish")
        void testResetsCircuitAfterSuccessfulPublish() {
            // GIVEN
            MqMessageProducer producer = new MqMessageProducer(jmsTemplate, newProperties(2, 2), clock);
            doThrow(new JmsException("fail") { })
                    .doNothing()
                    .doNothing()
                    .when(jmsTemplate)
                    .send(eq("DEV.QUEUE.1"), any());

            // WHEN
            producer.publish("DEV.QUEUE.1", "MSG-001", "payload");
            producer.publish("DEV.QUEUE.1", "MSG-001", "payload");

            // THEN
            verify(jmsTemplate, times(3)).send(eq("DEV.QUEUE.1"), any());
        }
    }
}
