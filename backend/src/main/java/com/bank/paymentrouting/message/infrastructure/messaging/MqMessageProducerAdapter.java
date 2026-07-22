package com.bank.paymentrouting.message.infrastructure.messaging;

import java.time.Clock;
import java.util.concurrent.TimeoutException;

import com.bank.paymentrouting.config.MqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MqMessageProducerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqMessageProducerAdapter.class);

    private final JmsTemplate jmsTemplate;
    private final MqProperties mqProperties;
    private final Clock clock;

    private int consecutiveFailures;
    private long circuitOpenedAtMillis;

    @Autowired
    public MqMessageProducerAdapter(JmsTemplate jmsTemplate, MqProperties mqProperties) {
        this(jmsTemplate, mqProperties, Clock.systemUTC());
    }

    MqMessageProducerAdapter(JmsTemplate jmsTemplate, MqProperties mqProperties, Clock clock) {
        this.jmsTemplate = jmsTemplate;
        this.mqProperties = mqProperties;
        this.clock = clock;
    }

    public synchronized void publish(String queueName, String externalMessageId, String payload) {
        long nowMillis = clock.millis();
        if (isCircuitOpen(nowMillis)) {
            throw new IllegalStateException("MQ publish circuit is open; skipping publish attempt");
        }

        RuntimeException lastFailure = null;
        int maxAttempts = Math.max(1, mqProperties.publishMaxAttempts());
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            long startMillis = clock.millis();
            try {
                doPublish(queueName, externalMessageId, payload);
                long elapsedMillis = clock.millis() - startMillis;
                if (elapsedMillis > mqProperties.publishTimeoutMs()) {
                    throw new RuntimeException(new TimeoutException("MQ publish exceeded timeout"));
                }

                resetCircuit();
                return;
            } catch (RuntimeException exception) {
                lastFailure = exception;
                recordFailure(clock.millis());

                if (attempt < maxAttempts) {
                    applyBackoff();
                }
            }
        }

        LOGGER.error("MQ publish failed after {} attempts", maxAttempts, lastFailure);
        throw lastFailure == null ? new IllegalStateException("MQ publish failed") : lastFailure;
    }

    private void doPublish(String queueName, String externalMessageId, String payload) {
        jmsTemplate.send(queueName, session -> {
            var message = session.createTextMessage(payload);
            message.setJMSCorrelationID(externalMessageId);
            return message;
        });
    }

    private boolean isCircuitOpen(long nowMillis) {
        if (consecutiveFailures < mqProperties.publishCircuitFailureThreshold()) {
            return false;
        }
        return nowMillis - circuitOpenedAtMillis < mqProperties.publishCircuitOpenMs();
    }

    private void recordFailure(long nowMillis) {
        consecutiveFailures++;
        if (consecutiveFailures == mqProperties.publishCircuitFailureThreshold()) {
            circuitOpenedAtMillis = nowMillis;
        }
    }

    private void resetCircuit() {
        consecutiveFailures = 0;
        circuitOpenedAtMillis = 0L;
    }

    private void applyBackoff() {
        long backoffMs = Math.max(0L, mqProperties.publishBackoffMs());
        if (backoffMs == 0L) {
            return;
        }
        try {
            Thread.sleep(backoffMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted during MQ publish backoff", exception);
        }
    }
}