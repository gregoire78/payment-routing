package com.bank.paymentrouting.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.paymentrouting.message.application.usecase.IngestMessageUseCase;
import com.bank.paymentrouting.message.infrastructure.messaging.MqMessageListener;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MqMessageListener")
class MqMessageListenerTest {

    @Mock
    private IngestMessageUseCase ingestMessageUseCase;

    @Mock
    private Message message;

    @InjectMocks
    private MqMessageListener mqMessageListener;

    @Nested
    @DisplayName("onMessage")
    class OnMessage {

        @Test
        @DisplayName("uses correlation id when present")
        void testUsesCorrelationIdWhenPresent() throws JMSException {
            // GIVEN
            when(message.getBody(eq(String.class))).thenReturn("payload");
            when(message.getJMSCorrelationID()).thenReturn("CORR-001");

            // WHEN
            mqMessageListener.onMessage(message);

            // THEN
            verify(ingestMessageUseCase).execute("CORR-001", "payload");
        }

        @Test
        @DisplayName("falls back to message id when correlation id is blank")
        void testFallsBackToMessageIdWhenCorrelationIdIsBlank() throws JMSException {
            // GIVEN
            when(message.getBody(eq(String.class))).thenReturn("payload");
            when(message.getJMSCorrelationID()).thenReturn("   ");
            when(message.getJMSMessageID()).thenReturn("MSGID-001");

            // WHEN
            mqMessageListener.onMessage(message);

            // THEN
            verify(ingestMessageUseCase).execute("MSGID-001", "payload");
        }

        @Test
        @DisplayName("generates external id when both correlation and message id are missing")
        void testGeneratesExternalIdWhenBothCorrelationAndMessageIdAreMissing() throws JMSException {
            // GIVEN
            when(message.getBody(eq(String.class))).thenReturn("payload");
            when(message.getJMSCorrelationID()).thenReturn(null);
            when(message.getJMSMessageID()).thenReturn(" ");

            // WHEN
            mqMessageListener.onMessage(message);

            // THEN
            ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
            verify(ingestMessageUseCase).execute(idCaptor.capture(), eq("payload"));
            assertThat(idCaptor.getValue()).startsWith("generated-");
        }
    }
}
