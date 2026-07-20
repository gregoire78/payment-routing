package com.bank.paymentrouting.message.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.bank.paymentrouting.message.application.port.DomainEventPublisherPort;
import com.bank.paymentrouting.message.application.port.PaymentMessagePublisherPort;
import com.bank.paymentrouting.message.domain.ExternalMessageId;
import com.bank.paymentrouting.message.domain.MessagePayload;
import com.bank.paymentrouting.message.domain.event.MessagePublishedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublishMessageUseCase")
class PublishMessageUseCaseTest {

    @Mock
    private PaymentMessagePublisherPort messagePublisher;

    @Mock
    private DomainEventPublisherPort domainEventPublisher;

    @InjectMocks
    private PublishMessageUseCase publishMessageUseCase;

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("publishes message through output port")
        void testPublishesMessageThroughOutputPort() {
            // GIVEN
            String externalMessageId = "MSG-001";
            String payload = "payload";

            // WHEN
            publishMessageUseCase.execute(externalMessageId, payload);

            // THEN
            verify(messagePublisher).publish(ExternalMessageId.of(externalMessageId), MessagePayload.of(payload));
            verify(domainEventPublisher).publish(any(MessagePublishedEvent.class));
        }
    }
}
