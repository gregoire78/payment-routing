package com.bank.paymentrouting.message.infrastructure.adapters.event;

import static org.mockito.Mockito.verify;

import java.time.Instant;

import com.bank.paymentrouting.message.domain.event.MessageReceivedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpringDomainEventPublisherAdapter")
class SpringDomainEventPublisherAdapterTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private SpringDomainEventPublisherAdapter springDomainEventPublisherAdapter;

    @Test
    @DisplayName("publishes domain event through Spring event publisher")
    void testPublishesDomainEventThroughSpringEventPublisher() {
        // GIVEN
        MessageReceivedEvent event = new MessageReceivedEvent("MSG-500", Instant.parse("2026-07-10T10:15:30Z"));

        // WHEN
        springDomainEventPublisherAdapter.publish(event);

        // THEN
        verify(applicationEventPublisher).publishEvent(event);
    }
}
