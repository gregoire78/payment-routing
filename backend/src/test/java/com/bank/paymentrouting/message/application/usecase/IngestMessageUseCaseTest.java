package com.bank.paymentrouting.message.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.time.Instant;

import com.bank.paymentrouting.message.domain.MessageStatus;
import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.application.port.DomainEventPublisherPort;
import com.bank.paymentrouting.message.application.port.PaymentMessageStorePort;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.domain.MessageIngestionResult;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.domain.MessageIngestionDomainService;
import com.bank.paymentrouting.message.domain.event.MessageReceivedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngestMessageUseCase")
class IngestMessageUseCaseTest {

    @Mock
    private PaymentMessageStorePort messageStore;

    @Mock
    private MessageIngestionDomainService messageIngestionDomainService;

    @Mock
    private DomainEventPublisherPort domainEventPublisher;

    @InjectMocks
    private IngestMessageUseCase ingestMessageUseCase;

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("delegates ingestion to domain service and publishes message received event when newly created")
        void testDelegatesIngestionToDomainServiceAndPublishesMessageReceivedEventWhenNewlyCreated() {
            // GIVEN
            PaymentMessage expectedDomainMessage = PaymentMessage.restore(
                    1L,
                    ExternalMessageId.of("MSG-001"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-001");
            MessagePayload payload = MessagePayload.of("payload");
            when(messageIngestionDomainService.ingest(messageStore, externalMessageId, payload))
                    .thenReturn(new MessageIngestionResult(expectedDomainMessage, true));

            // WHEN
            PaymentMessageRecord result = ingestMessageUseCase.execute("MSG-001", "payload");

            // THEN
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.externalMessageId()).isEqualTo("MSG-001");
            assertThat(result.payload()).isEqualTo("payload");
            assertThat(result.status()).isEqualTo(MessageStatus.RECEIVED);
            verify(messageIngestionDomainService).ingest(messageStore, externalMessageId, payload);
            verify(domainEventPublisher).publish(any(MessageReceivedEvent.class));
        }

        @Test
        @DisplayName("does not publish message received event when message already exists")
        void testDoesNotPublishMessageReceivedEventWhenMessageAlreadyExists() {
            // GIVEN
            PaymentMessage existingMessage = PaymentMessage.restore(
                    1L,
                    ExternalMessageId.of("MSG-001"),
                    MessagePayload.of("payload"),
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-001");
            MessagePayload payload = MessagePayload.of("payload");
            when(messageIngestionDomainService.ingest(messageStore, externalMessageId, payload))
                    .thenReturn(new MessageIngestionResult(existingMessage, false));

            // WHEN
            PaymentMessageRecord result = ingestMessageUseCase.execute("MSG-001", "payload");

            // THEN
            assertThat(result.id()).isEqualTo(1L);
            verify(domainEventPublisher, times(0)).publish(any(MessageReceivedEvent.class));
        }
    }
}
