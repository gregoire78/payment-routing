package com.bank.paymentrouting.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.bank.paymentrouting.message.application.usecase.IngestMessageUseCase;
import com.bank.paymentrouting.message.application.usecase.PublishMessageUseCase;
import com.bank.paymentrouting.message.domain.event.MessagePublishedEvent;
import com.bank.paymentrouting.message.domain.event.MessageReceivedEvent;
import com.bank.paymentrouting.message.infrastructure.persistence.PersistedMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Tag("integration")
@ActiveProfiles("test")
@RecordApplicationEvents
@TestPropertySource(properties = {
        "app.mq.listener-enabled=false",
        "app.demo.seed-enabled=false"
})
@DisplayName("DomainEventFlowIntegrationTest")
class DomainEventFlowIntegrationTest {

    @Autowired
    private IngestMessageUseCase ingestMessageUseCase;

    @Autowired
    private PublishMessageUseCase publishMessageUseCase;

    @Autowired
    private PersistedMessageRepository routedMessageRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @BeforeEach
    void cleanDatabase() {
        routedMessageRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("emits message received event once for new ingestion")
    void testEmitsMessageReceivedEventOnceForNewIngestion() {
        // GIVEN
        String externalMessageId = "EVT-INGEST-" + UUID.randomUUID();

        // WHEN
        ingestMessageUseCase.execute(externalMessageId, "payload");
        ingestMessageUseCase.execute(externalMessageId, "payload");

        // THEN
        long count = applicationEvents.stream(MessageReceivedEvent.class)
                .filter(event -> event.externalMessageId().equals(externalMessageId))
                .count();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("emits message published event when publish use case is executed")
    void testEmitsMessagePublishedEventWhenPublishUseCaseIsExecuted() {
        // GIVEN
        String externalMessageId = "EVT-PUBLISH-" + UUID.randomUUID();

        // WHEN
        publishMessageUseCase.execute(externalMessageId, "payload");

        // THEN
        long count = applicationEvents.stream(MessagePublishedEvent.class)
                .filter(event -> event.externalMessageId().equals(externalMessageId))
                .count();
        assertThat(count).isEqualTo(1L);
    }
}
