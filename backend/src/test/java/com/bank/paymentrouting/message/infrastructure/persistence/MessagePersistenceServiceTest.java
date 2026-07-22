package com.bank.paymentrouting.message.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.bank.paymentrouting.message.domain.MessageStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessagePersistenceService")
class MessagePersistenceServiceTest {

    @Mock
    private PersistedMessageRepository routedMessageRepository;

    @InjectMocks
    private MessagePersistenceService messagePersistenceService;

    @Nested
    @DisplayName("findByExternalMessageId")
    class FindByExternalMessageId {

        @Test
        @DisplayName("delegates lookup to repository")
        void testDelegatesLookupToRepository() {
            // GIVEN
            PersistedMessageEntity expected = new PersistedMessageEntity(
                    "MSG-100",
                    "payload-100",
                    MessageStatus.RECEIVED,
                    java.time.Instant.parse("2026-07-10T10:15:30Z")
            );
            when(routedMessageRepository.findByExternalMessageId("MSG-100"))
                    .thenReturn(Optional.of(expected));

            // WHEN
            Optional<PersistedMessageEntity> result = messagePersistenceService.findByExternalMessageId("MSG-100");

            // THEN
            assertThat(result).contains(expected);
            verify(routedMessageRepository).findByExternalMessageId("MSG-100");
        }
    }

    @Nested
    @DisplayName("insertReceivedMessage")
    class InsertReceivedMessage {

        @Test
        @DisplayName("saves entity with received status and current timestamp")
        void testSavesEntityWithReceivedStatusAndCurrentTimestamp() {
            // GIVEN
            ArgumentCaptor<PersistedMessageEntity> captor = ArgumentCaptor.forClass(PersistedMessageEntity.class);
            PersistedMessageEntity persisted = new PersistedMessageEntity(
                    "MSG-200",
                    "payload-200",
                    MessageStatus.RECEIVED,
                    java.time.Instant.parse("2026-07-10T10:15:30Z")
            );
            when(routedMessageRepository.saveAndFlush(captor.capture())).thenReturn(persisted);

            // WHEN
            PersistedMessageEntity result = messagePersistenceService.insertReceivedMessage("MSG-200", "payload-200");

            // THEN
            PersistedMessageEntity saved = captor.getValue();
            assertThat(saved.getExternalMessageId()).isEqualTo("MSG-200");
            assertThat(saved.getPayload()).isEqualTo("payload-200");
            assertThat(saved.getStatus()).isEqualTo(MessageStatus.RECEIVED);
            assertThat(saved.getReceivedAt()).isNotNull();
            assertThat(result).isEqualTo(persisted);
        }
    }
}
