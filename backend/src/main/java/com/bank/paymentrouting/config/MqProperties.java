package com.bank.paymentrouting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mq")
public record MqProperties(
        String queueManager,
        String channel,
        String connName,
        String queueName,
        String user,
        String password,
        boolean listenerEnabled,
        String concurrency,
        int publishMaxAttempts,
        long publishBackoffMs,
        long publishTimeoutMs,
        int publishCircuitFailureThreshold,
        long publishCircuitOpenMs
) {
}