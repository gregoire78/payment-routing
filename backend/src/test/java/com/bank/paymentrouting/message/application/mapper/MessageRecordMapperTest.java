package com.bank.paymentrouting.message.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import com.bank.paymentrouting.message.domain.MessageStatus;
import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.domain.MessagePayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MessageRecordMapper")
class MessageRecordMapperTest {

    private final MessageRecordMapper messageRecordMapper = new MessageRecordMapper();

    @Nested
    @DisplayName("toRecord")
    class ToRecord {

        @Test
        @DisplayName("maps domain message to application record")
        void testMapsDomainMessageToApplicationRecord() {
            // GIVEN
            PaymentMessage message = PaymentMessage.restore(
                    10L,
                    ExternalMessageId.of("MSG-010"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );

            // WHEN
            PaymentMessageRecord result = messageRecordMapper.toRecord(message);

            // THEN
            assertThat(result.id()).isEqualTo(10L);
            assertThat(result.externalMessageId()).isEqualTo("MSG-010");
            assertThat(result.payload()).isEqualTo("payload");
            assertThat(result.status()).isEqualTo(MessageStatus.RECEIVED);
            assertThat(result.receivedAt()).isEqualTo(Instant.parse("2026-07-10T10:15:30Z"));
        }
    }
}
