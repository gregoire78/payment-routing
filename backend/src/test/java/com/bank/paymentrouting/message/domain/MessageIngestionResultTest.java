package com.bank.paymentrouting.message.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MessageIngestionResult")
class MessageIngestionResultTest {

    @Nested
    @DisplayName("record")
    class Record {

        @Test
        @DisplayName("stores message and newly created flag")
        void testStoresMessageAndNewlyCreatedFlag() {
            // GIVEN
            PaymentMessage message = PaymentMessage.restore(
                    1L,
                    ExternalMessageId.of("MSG-001"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );

            // WHEN
            MessageIngestionResult result = new MessageIngestionResult(message, true);

            // THEN
            assertThat(result.message()).isEqualTo(message);
            assertThat(result.newlyCreated()).isTrue();
        }
    }
}
