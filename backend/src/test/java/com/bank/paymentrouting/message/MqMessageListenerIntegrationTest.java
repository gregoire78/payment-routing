package com.bank.paymentrouting.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.bank.paymentrouting.message.domain.MessageStatus;
import com.bank.paymentrouting.message.infrastructure.persistence.PersistedMessageEntity;
import com.bank.paymentrouting.message.infrastructure.persistence.PersistedMessageRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("integration")
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.mq.listener-enabled=true",
        "app.demo.seed-enabled=false",
        "app.mq.concurrency=1-1"
})
class MqMessageListenerIntegrationTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private PersistedMessageRepository routedMessageRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void cleanDatabase() {
        routedMessageRepository.deleteAllInBatch();
    }

    @Test
    void testConsumeMqMessageAndPersistIt() {
        String externalMessageId = "IT-MQ-" + UUID.randomUUID();
        String payload = "{\"type\":\"PAYMENT\",\"amount\":99.99}";

        jmsTemplate.send("DEV.QUEUE.1", session -> {
            var message = session.createTextMessage(payload);
            message.setJMSCorrelationID(externalMessageId);
            return message;
        });

        PersistedMessageEntity storedMessage = awaitMessage(externalMessageId, Duration.ofSeconds(10));

        assertThat(storedMessage.getExternalMessageId()).isEqualTo(externalMessageId);
        assertThat(storedMessage.getPayload()).isEqualTo(payload);
        assertThat(storedMessage.getStatus()).isEqualTo(MessageStatus.RECEIVED);
    }

    @Test
    void testRemainIdempotentWhenSameMessageIsReceivedTwice() {
        String externalMessageId = "IT-DUP-" + UUID.randomUUID();
        String payload = "{\"type\":\"PAYMENT\",\"amount\":42.00}";

        sendWithCorrelationId(externalMessageId, payload);
        sendWithCorrelationId(externalMessageId, payload);

        awaitCount(externalMessageId, 1L, Duration.ofSeconds(10));

        assertThat(routedMessageRepository.countByExternalMessageId(externalMessageId)).isEqualTo(1L);
    }

    @Test
        void testExposeMessageViaRestAfterMqConsumption() throws Exception {
        String externalMessageId = "IT-REST-" + UUID.randomUUID();
        String payload = "{\"type\":\"PAYMENT\",\"amount\":120.00}";

        sendWithCorrelationId(externalMessageId, payload);
        PersistedMessageEntity storedMessage = awaitMessage(externalMessageId, Duration.ofSeconds(10));

        HttpResponse<String> response = httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/messages/" + storedMessage.getId()))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"id\":" + storedMessage.getId());
        assertThat(response.body()).contains("\"externalMessageId\":\"" + externalMessageId + "\"");
        assertThat(response.body()).contains("\"payload\":\"{\\\"type\\\":\\\"PAYMENT\\\",\\\"amount\\\":120.00}\"");
        assertThat(response.body()).contains("\"status\":\"RECEIVED\"");
    }

    @Test
    void testPublishMessageThroughRestAndPersistIt() throws Exception {
        String externalMessageId = "IT-PUBLISH-" + UUID.randomUUID();
        String payload = "{\"type\":\"PAYMENT\",\"amount\":75.50}";

        HttpResponse<String> response = httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/messages/publish"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                    {
                      "externalMessageId": "%s",
                                            "payload": "%s"
                    }
                                        """.formatted(externalMessageId, payload.replace("\"", "\\\""))))
                .build(),
            HttpResponse.BodyHandlers.ofString()
        );

        assertThat(response.statusCode()).isEqualTo(202);
                assertThat(response.body()).contains("\"status\":\"accepted\"");
                assertThat(response.body()).contains("\"externalMessageId\":\"" + externalMessageId + "\"");

        PersistedMessageEntity storedMessage = awaitMessage(externalMessageId, Duration.ofSeconds(10));

        assertThat(storedMessage.getPayload()).isEqualTo(payload);
        assertThat(storedMessage.getStatus()).isEqualTo(MessageStatus.RECEIVED);
    }

    private void sendWithCorrelationId(String externalMessageId, String payload) {
        jmsTemplate.send("DEV.QUEUE.1", session -> {
            var message = session.createTextMessage(payload);
            message.setJMSCorrelationID(externalMessageId);
            return message;
        });
    }

    private PersistedMessageEntity awaitMessage(String externalMessageId, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            var message = routedMessageRepository.findByExternalMessageId(externalMessageId);
            if (message.isPresent()) {
                return message.get();
            }
            pause();
        }
        throw new AssertionError("Timed out waiting for message " + externalMessageId);
    }

    private void awaitCount(String externalMessageId, long expectedCount, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            long actualCount = routedMessageRepository.countByExternalMessageId(externalMessageId);
            if (actualCount == expectedCount) {
                return;
            }
            pause();
        }
        throw new AssertionError("Timed out waiting for count " + expectedCount + " for " + externalMessageId);
    }

    private void pause() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for MQ processing", exception);
        }
    }
}