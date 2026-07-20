package com.bank.paymentrouting.message.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import com.bank.paymentrouting.message.domain.MessageStatus;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.infrastructure.persistence.PersistedMessageEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RoutedMessageMapper")
class RoutedMessageMapperTest {

    private final RoutedMessageMapper routedMessageMapper = new RoutedMessageMapper();

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("maps routed message entity to domain aggregate")
        void testMapsRoutedMessageEntityToDomainAggregate() {
            // GIVEN
            PersistedMessageEntity routedMessage = new PersistedMessageEntity(
                    "MSG-001",
                    "payload",
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );

            // WHEN
            PaymentMessage result = routedMessageMapper.toDomain(routedMessage);

            // THEN
            assertThat(result.id()).isNull();
            assertThat(result.externalMessageId().value()).isEqualTo("MSG-001");
            assertThat(result.payload().value()).isEqualTo("payload");
            assertThat(result.status()).isEqualTo(MessageStatus.RECEIVED);
            assertThat(result.receivedAt()).isEqualTo(Instant.parse("2026-07-10T10:15:30Z"));
        }
    }
}
