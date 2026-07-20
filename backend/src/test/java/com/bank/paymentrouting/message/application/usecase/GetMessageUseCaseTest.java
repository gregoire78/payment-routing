package com.bank.paymentrouting.message.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.bank.paymentrouting.message.domain.MessageStatus;
import com.bank.paymentrouting.message.application.exception.MessageNotFoundException;
import com.bank.paymentrouting.message.application.mapper.MessageRecordMapper;
import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.application.port.PaymentMessageQueryPort;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.domain.MessagePayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMessageUseCase")
class GetMessageUseCaseTest {

    @Mock
    private PaymentMessageQueryPort messageQueryStore;

    @Mock
    private MessageRecordMapper messageRecordMapper;

    @InjectMocks
    private GetMessageUseCase getMessageUseCase;

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("returns message when found")
        void testReturnsMessageWhenFound() {
            // GIVEN
            PaymentMessage domainMessage = PaymentMessage.restore(
                1L,
                ExternalMessageId.of("MSG-001"),
                MessagePayload.of("payload"),
                MessageStatus.RECEIVED,
                Instant.parse("2026-07-10T10:15:30Z")
            );
            PaymentMessageRecord expected = new PaymentMessageRecord(
                    1L,
                    "MSG-001",
                    "payload",
                    MessageStatus.RECEIVED,
                    Instant.parse("2026-07-10T10:15:30Z")
            );
            when(messageQueryStore.findMessageById(1L)).thenReturn(Optional.of(domainMessage));
            when(messageRecordMapper.toRecord(domainMessage)).thenReturn(expected);

            // WHEN
            PaymentMessageRecord result = getMessageUseCase.execute(1L);

            // THEN
            assertThat(result).isEqualTo(expected);
            verify(messageQueryStore).findMessageById(1L);
            verify(messageRecordMapper).toRecord(domainMessage);
        }

        @Test
        @DisplayName("throws not found when message does not exist")
        void testThrowsNotFoundWhenMessageDoesNotExist() {
            // GIVEN
            when(messageQueryStore.findMessageById(55L)).thenReturn(Optional.empty());

            // WHEN / THEN
            assertThatThrownBy(() -> getMessageUseCase.execute(55L))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessageContaining("55");
        }
    }
}
