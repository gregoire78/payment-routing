package com.bank.paymentrouting.message;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.demo.seed-enabled", havingValue = "true", matchIfMissing = true)
public class MessageBootstrapDataLoader implements ApplicationRunner {

    private final MessageIngestionService messageIngestionService;

    public MessageBootstrapDataLoader(MessageIngestionService messageIngestionService) {
        this.messageIngestionService = messageIngestionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        messageIngestionService.ingest(
                "BOOTSTRAP-0001",
                "{\"type\":\"PAYMENT\",\"amount\":1250.45,\"currency\":\"EUR\"}"
        );
    }
}