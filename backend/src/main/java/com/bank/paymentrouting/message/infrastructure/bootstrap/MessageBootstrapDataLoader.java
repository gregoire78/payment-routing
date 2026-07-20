package com.bank.paymentrouting.message.infrastructure.bootstrap;

import com.bank.paymentrouting.message.application.usecase.IngestMessageUseCase;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.demo.seed-enabled", havingValue = "true", matchIfMissing = true)
public class MessageBootstrapDataLoader implements ApplicationRunner {

    private final IngestMessageUseCase ingestMessageUseCase;

    public MessageBootstrapDataLoader(IngestMessageUseCase ingestMessageUseCase) {
        this.ingestMessageUseCase = ingestMessageUseCase;
    }

    @Override
    public void run(ApplicationArguments args) {
        ingestMessageUseCase.execute(
                "BOOTSTRAP-0001",
                "{\"type\":\"PAYMENT\",\"amount\":1250.45,\"currency\":\"EUR\"}"
        );
    }
}