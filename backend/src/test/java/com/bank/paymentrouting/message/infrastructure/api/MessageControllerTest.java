package com.bank.paymentrouting.message.infrastructure.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.application.usecase.GetMessageUseCase;
import com.bank.paymentrouting.message.application.usecase.ListMessagesUseCase;
import com.bank.paymentrouting.message.application.usecase.PublishMessageUseCase;
import com.bank.paymentrouting.message.domain.MessageStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageController")
class MessageControllerTest {

    @Mock
    private ListMessagesUseCase listMessagesUseCase;

    @Mock
    private GetMessageUseCase getMessageUseCase;

    @Mock
    private PublishMessageUseCase publishMessageUseCase;

    @InjectMocks
    private MessageController messageController;

    @Nested
    @DisplayName("listMessages")
    class ListMessages {

        @Test
        @DisplayName("maps records from use case to API response")
        void testMapsRecordsFromUseCaseToApiResponse() {
            // GIVEN
            PaymentMessageRecord record = new PaymentMessageRecord(
                    10L,
                    "MSG-010",
                    "payload-10",
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            when(listMessagesUseCase.execute(0, 20)).thenReturn(List.of(record));

            // WHEN
            List<PaymentMessageResponse> result = messageController.listMessages(0, 20);

            // THEN
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo(10L);
            assertThat(result.getFirst().externalMessageId()).isEqualTo("MSG-010");
            assertThat(result.getFirst().payload()).isEqualTo("payload-10");
            assertThat(result.getFirst().status()).isEqualTo(MessageStatus.RECEIVED);
            verify(listMessagesUseCase).execute(0, 20);
        }
    }

    @Nested
    @DisplayName("getMessage")
    class GetMessage {

        @Test
        @DisplayName("maps a single record from use case to API response")
        void testMapsSingleRecordFromUseCaseToApiResponse() {
            // GIVEN
            PaymentMessageRecord record = new PaymentMessageRecord(
                    11L,
                    "MSG-011",
                    "payload-11",
                    MessageStatus.ROUTED,
                    Instant.parse("2026-07-11T10:15:30Z")
            );
            when(getMessageUseCase.execute(11L)).thenReturn(record);

            // WHEN
            PaymentMessageResponse result = messageController.getMessage(11L);

            // THEN
            assertThat(result.id()).isEqualTo(11L);
            assertThat(result.externalMessageId()).isEqualTo("MSG-011");
            assertThat(result.status()).isEqualTo(MessageStatus.ROUTED);
            verify(getMessageUseCase).execute(11L);
        }
    }

    @Nested
    @DisplayName("publishMessage")
    class PublishMessage {

        @Test
        @DisplayName("delegates publish to use case and returns accepted payload")
        void testDelegatesPublishToUseCaseAndReturnsAcceptedPayload() {
            // GIVEN
            PublishMessageRequest request = new PublishMessageRequest("MSG-012", "payload-12");

            // WHEN
            Map<String, String> result = messageController.publishMessage(request);

            // THEN
            verify(publishMessageUseCase).execute("MSG-012", "payload-12");
            assertThat(result)
                    .containsEntry("status", "accepted")
                    .containsEntry("externalMessageId", "MSG-012");
        }
    }
}