package com.bank.paymentrouting.message.application;

import com.bank.paymentrouting.message.domain.MessageIngestionDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageApplicationConfig {

    @Bean
    MessageIngestionDomainService messageIngestionDomainService() {
        return new MessageIngestionDomainService();
    }
}