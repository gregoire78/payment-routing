package com.bank.paymentrouting.message.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.bank.paymentrouting.message.application.port.PaymentMessageStorePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageIngestionDomainService")
class MessageIngestionDomainServiceTest {

    @Mock
    private PaymentMessageStorePort messageStore;

    @InjectMocks
    private MessageIngestionDomainService messageIngestionDomainService;

    @Nested
    @DisplayName("ingest")
    class Ingest {

        @Test
        @DisplayName("returns existing message when already present")
        void testReturnsExistingMessageWhenAlreadyPresent() {
            // GIVEN
            PaymentMessage existing = PaymentMessage.restore(
                    1L,
                    ExternalMessageId.of("MSG-001"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-001");
            MessagePayload payload = MessagePayload.of("payload");
            when(messageStore.findByExternalMessageId(externalMessageId)).thenReturn(Optional.of(existing));

            // WHEN
            MessageIngestionResult result = messageIngestionDomainService.ingest(messageStore, externalMessageId, payload);

            // THEN
            assertThat(result.message()).isEqualTo(existing);
            assertThat(result.newlyCreated()).isFalse();
            verify(messageStore, times(0)).insertReceivedMessage(externalMessageId, payload);
        }

        @Test
        @DisplayName("inserts message when external id is new")
        void testInsertsMessageWhenExternalIdIsNew() {
            // GIVEN
            PaymentMessage inserted = PaymentMessage.restore(
                    2L,
                    ExternalMessageId.of("MSG-002"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-002");
            MessagePayload payload = MessagePayload.of("payload");
            when(messageStore.findByExternalMessageId(externalMessageId)).thenReturn(Optional.empty());
            when(messageStore.insertReceivedMessage(externalMessageId, payload)).thenReturn(inserted);

            // WHEN
            MessageIngestionResult result = messageIngestionDomainService.ingest(messageStore, externalMessageId, payload);

            // THEN
            assertThat(result.message()).isEqualTo(inserted);
            assertThat(result.newlyCreated()).isTrue();
        }

        @Test
        @DisplayName("returns existing message when concurrent duplicate occurs")
        void testReturnsExistingMessageWhenConcurrentDuplicateOccurs() {
            // GIVEN
            PaymentMessage existing = PaymentMessage.restore(
                    3L,
                    ExternalMessageId.of("MSG-003"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-003");
            MessagePayload payload = MessagePayload.of("payload");
            when(messageStore.findByExternalMessageId(externalMessageId))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(existing));
            when(messageStore.insertReceivedMessage(externalMessageId, payload))
                    .thenThrow(new DataIntegrityViolationException("duplicate"));

            // WHEN
            MessageIngestionResult result = messageIngestionDomainService.ingest(messageStore, externalMessageId, payload);

            // THEN
            assertThat(result.message()).isEqualTo(existing);
            assertThat(result.newlyCreated()).isFalse();
            verify(messageStore, times(2)).findByExternalMessageId(externalMessageId);
        }

        @Test
        @DisplayName("rethrows integrity violation when duplicate cannot be reloaded")
        void testRethrowsIntegrityViolationWhenDuplicateCannotBeReloaded() {
            // GIVEN
            DataIntegrityViolationException expected = new DataIntegrityViolationException("duplicate");
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-004");
            MessagePayload payload = MessagePayload.of("payload");
            when(messageStore.findByExternalMessageId(externalMessageId))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.empty());
            when(messageStore.insertReceivedMessage(externalMessageId, payload))
                    .thenThrow(expected);

            // WHEN / THEN
            assertThatThrownBy(() -> messageIngestionDomainService.ingest(messageStore, externalMessageId, payload))
                    .isSameAs(expected);
        }
    }
}
