package com.bank.paymentrouting.message.infrastructure.adapters.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.domain.MessageStatus;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.infrastructure.persistence.MessagePersistenceService;
import com.bank.paymentrouting.message.infrastructure.persistence.PersistedMessageEntity;
import com.bank.paymentrouting.message.infrastructure.persistence.mapper.RoutedMessageMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("JpaPaymentMessageStoreAdapter")
class JpaPaymentMessageStoreAdapterTest {

    @Mock
    private MessagePersistenceService messagePersistenceService;

    @Mock
    private RoutedMessageMapper routedMessageMapper;

    @InjectMocks
    private JpaPaymentMessageStoreAdapter jpaPaymentMessageStoreAdapter;

    @Nested
    @DisplayName("findByExternalMessageId")
    class FindByExternalMessageId {

        @Test
        @DisplayName("returns mapped domain message when persisted entity exists")
        void testReturnsMappedDomainMessageWhenPersistedEntityExists() {
            // GIVEN
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-301");
            PersistedMessageEntity persisted = new PersistedMessageEntity(
                    "MSG-301",
                    "payload-301",
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            PaymentMessage expected = PaymentMessage.restore(
                    301L,
                    ExternalMessageId.of("MSG-301"),
                    MessagePayload.of("payload-301"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            when(messagePersistenceService.findByExternalMessageId("MSG-301")).thenReturn(Optional.of(persisted));
            when(routedMessageMapper.toDomain(persisted)).thenReturn(expected);

            // WHEN
            Optional<PaymentMessage> result = jpaPaymentMessageStoreAdapter.findByExternalMessageId(externalMessageId);

            // THEN
            assertThat(result).contains(expected);
            verify(messagePersistenceService).findByExternalMessageId("MSG-301");
            verify(routedMessageMapper).toDomain(persisted);
        }

        @Test
        @DisplayName("returns empty when persisted entity does not exist")
        void testReturnsEmptyWhenPersistedEntityDoesNotExist() {
            // GIVEN
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-302");
            when(messagePersistenceService.findByExternalMessageId("MSG-302")).thenReturn(Optional.empty());

            // WHEN
            Optional<PaymentMessage> result = jpaPaymentMessageStoreAdapter.findByExternalMessageId(externalMessageId);

            // THEN
            assertThat(result).isEmpty();
            verify(messagePersistenceService).findByExternalMessageId("MSG-302");
            verify(routedMessageMapper, never()).toDomain(org.mockito.ArgumentMatchers.any());
        }
    }

    @Nested
    @DisplayName("insertReceivedMessage")
    class InsertReceivedMessage {

        @Test
        @DisplayName("inserts through persistence service and maps persisted entity")
        void testInsertsThroughPersistenceServiceAndMapsPersistedEntity() {
            // GIVEN
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-303");
            MessagePayload payload = MessagePayload.of("payload-303");
            PersistedMessageEntity persisted = new PersistedMessageEntity(
                    "MSG-303",
                    "payload-303",
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            PaymentMessage expected = PaymentMessage.restore(
                    303L,
                    ExternalMessageId.of("MSG-303"),
                    MessagePayload.of("payload-303"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            when(messagePersistenceService.insertReceivedMessage("MSG-303", "payload-303")).thenReturn(persisted);
            when(routedMessageMapper.toDomain(persisted)).thenReturn(expected);

            // WHEN
            PaymentMessage result = jpaPaymentMessageStoreAdapter.insertReceivedMessage(externalMessageId, payload);

            // THEN
            assertThat(result).isEqualTo(expected);
            verify(messagePersistenceService).insertReceivedMessage("MSG-303", "payload-303");
            verify(routedMessageMapper).toDomain(persisted);
        }
    }
}
