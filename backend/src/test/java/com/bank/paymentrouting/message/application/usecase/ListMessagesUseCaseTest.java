package com.bank.paymentrouting.message.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import com.bank.paymentrouting.message.domain.MessageStatus;
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
@DisplayName("ListMessagesUseCase")
class ListMessagesUseCaseTest {

    @Mock
    private PaymentMessageQueryPort messageQueryStore;

    @Mock
    private MessageRecordMapper messageRecordMapper;

    @InjectMocks
    private ListMessagesUseCase listMessagesUseCase;

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("returns messages from query store")
        void testReturnsMessagesFromQueryStore() {
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
            when(messageQueryStore.findMessages(0, 20)).thenReturn(List.of(domainMessage));
            when(messageRecordMapper.toRecord(domainMessage)).thenReturn(expected);

            // WHEN
            List<PaymentMessageRecord> result = listMessagesUseCase.execute(0, 20);

            // THEN
            assertThat(result).containsExactly(expected);
            verify(messageQueryStore).findMessages(0, 20);
            verify(messageRecordMapper).toRecord(domainMessage);
        }
    }
}
