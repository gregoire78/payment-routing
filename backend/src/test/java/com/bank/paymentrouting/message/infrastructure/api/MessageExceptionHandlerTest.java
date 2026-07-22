package com.bank.paymentrouting.message.infrastructure.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;

import com.bank.paymentrouting.message.application.exception.MessageNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MessageExceptionHandler")
class MessageExceptionHandlerTest {

    private final MessageExceptionHandler messageExceptionHandler = new MessageExceptionHandler();

    @Test
    @DisplayName("returns timestamp and message for not found exception")
    void testReturnsTimestampAndMessageForNotFoundException() {
        // GIVEN
        MessageNotFoundException exception = new MessageNotFoundException(42L);

        // WHEN
        Map<String, Object> response = messageExceptionHandler.handleNotFound(exception);

        // THEN
        assertThat(response.get("message")).isEqualTo(exception.getMessage());
        assertThat(response.get("timestamp")).isInstanceOf(Instant.class);
    }
}
