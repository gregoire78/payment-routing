package com.bank.paymentrouting.message.application;

import com.bank.paymentrouting.message.domain.event.MessagePublishedEvent;
import com.bank.paymentrouting.message.domain.event.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MessageDomainEventLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDomainEventLogger.class);

    @EventListener
    public void onMessageReceived(MessageReceivedEvent event) {
        LOGGER.info("DomainEvent=MessageReceived externalMessageId={} occurredAt={}",
                event.externalMessageId(), event.occurredAt());
    }

    @EventListener
    public void onMessagePublished(MessagePublishedEvent event) {
        LOGGER.info("DomainEvent=MessagePublished externalMessageId={} occurredAt={}",
                event.externalMessageId(), event.occurredAt());
    }
}
