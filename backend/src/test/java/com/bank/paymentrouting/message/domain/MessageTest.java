package com.bank.paymentrouting.message.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Message")
class MessageTest {

    @Nested
    @DisplayName("received")
    class Received {

        @Test
        @DisplayName("creates received aggregate with null id")
        void testCreatesReceivedAggregateWithNullId() {
            // GIVEN
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-001");
            MessagePayload payload = MessagePayload.of("payload");
            Instant receivedAt = Instant.parse("2026-07-10T10:15:30Z");

            // WHEN
            PaymentMessage message = PaymentMessage.received(externalMessageId, payload, receivedAt);

            // THEN
            assertThat(message.id()).isNull();
            assertThat(message.externalMessageId()).isEqualTo(externalMessageId);
            assertThat(message.payload()).isEqualTo(payload);
            assertThat(message.status()).isEqualTo(MessageStatus.RECEIVED);
            assertThat(message.receivedAt()).isEqualTo(receivedAt);
        }
    }

    @Nested
    @DisplayName("markRouted")
    class MarkRouted {

        @Test
        @DisplayName("transitions from received to routed")
        void testTransitionsFromReceivedToRouted() {
            // GIVEN
            PaymentMessage message = PaymentMessage.restore(
                    1L,
                    ExternalMessageId.of("MSG-001"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );

            // WHEN
            PaymentMessage routed = message.markRouted();

            // THEN
            assertThat(routed.status()).isEqualTo(MessageStatus.ROUTED);
        }

        @Test
        @DisplayName("rejects transition when current status is not received")
        void testRejectsTransitionWhenCurrentStatusIsNotReceived() {
            // GIVEN
            PaymentMessage message = PaymentMessage.restore(
                    1L,
                    ExternalMessageId.of("MSG-001"),
                    MessagePayload.of("payload"),
                    MessageStatus.FAILED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );

            // WHEN / THEN
            assertThatThrownBy(message::markRouted)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not allowed");
        }
    }

    @Nested
    @DisplayName("markFailed")
    class MarkFailed {

        @Test
        @DisplayName("transitions from received to failed")
        void testTransitionsFromReceivedToFailed() {
            // GIVEN
            PaymentMessage message = PaymentMessage.restore(
                    1L,
                    ExternalMessageId.of("MSG-001"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );

            // WHEN
            PaymentMessage failed = message.markFailed();

            // THEN
            assertThat(failed.status()).isEqualTo(MessageStatus.FAILED);
        }

        @Test
        @DisplayName("rejects transition when current status is not received")
        void testRejectsTransitionWhenCurrentStatusIsNotReceived() {
            // GIVEN
            PaymentMessage message = PaymentMessage.restore(
                    1L,
                    ExternalMessageId.of("MSG-001"),
                    MessagePayload.of("payload"),
                    MessageStatus.ROUTED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );

            // WHEN / THEN
            assertThatThrownBy(message::markFailed)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not allowed");
        }
    }
}
