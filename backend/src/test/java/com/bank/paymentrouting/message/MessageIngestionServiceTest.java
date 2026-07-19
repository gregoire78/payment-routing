package com.bank.paymentrouting.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class MessageIngestionServiceTest {

    @Mock
        private MessagePersistenceService messagePersistenceService;

    @InjectMocks
    private MessageIngestionService messageIngestionService;

    @Test
    void shouldInsertMessageWhenExternalIdIsNew() {
        RoutedMessage saved = new RoutedMessage(
                "MSG-001",
                "payload",
                MessageStatus.RECEIVED,
                Instant.parse("2026-07-10T10:15:30Z")
        );

        when(messagePersistenceService.findByExternalMessageId("MSG-001")).thenReturn(Optional.empty());
        when(messagePersistenceService.insertReceivedMessage("MSG-001", "payload")).thenReturn(saved);

        MessageView result = messageIngestionService.ingest("MSG-001", "payload");

        assertThat(result.externalMessageId()).isEqualTo("MSG-001");
        assertThat(result.payload()).isEqualTo("payload");
        assertThat(result.status()).isEqualTo(MessageStatus.RECEIVED);
    }

    @Test
    void shouldReturnExistingMessageWhenDuplicateIsDetected() {
        RoutedMessage existing = new RoutedMessage(
                "MSG-002",
                "payload",
                MessageStatus.RECEIVED,
                Instant.parse("2026-07-10T10:15:30Z")
        );

        when(messagePersistenceService.findByExternalMessageId("MSG-002"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existing));
        when(messagePersistenceService.insertReceivedMessage("MSG-002", "payload"))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        MessageView result = messageIngestionService.ingest("MSG-002", "payload");

        assertThat(result.externalMessageId()).isEqualTo("MSG-002");
        verify(messagePersistenceService, times(2)).findByExternalMessageId("MSG-002");
    }

    @Test
    void shouldReturnExistingMessageWithoutInsertWhenAlreadyPresent() {
        RoutedMessage existing = new RoutedMessage(
                "MSG-003",
                "payload",
                MessageStatus.RECEIVED,
                Instant.parse("2026-07-10T10:15:30Z")
        );

        when(messagePersistenceService.findByExternalMessageId("MSG-003"))
                .thenReturn(Optional.of(existing));

        MessageView result = messageIngestionService.ingest("MSG-003", "payload");

        assertThat(result.externalMessageId()).isEqualTo("MSG-003");
        verify(messagePersistenceService, times(0)).insertReceivedMessage(any(), any());
    }
}