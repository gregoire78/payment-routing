package com.bank.paymentrouting.message.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MessagePayload")
class MessagePayloadTest {

    @Nested
    @DisplayName("of")
    class Of {

        @Test
        @DisplayName("creates value object when input is valid")
        void testCreatesValueObjectWhenInputIsValid() {
            // GIVEN / WHEN
            MessagePayload messagePayload = MessagePayload.of("{\"type\":\"PAYMENT\"}");

            // THEN
            assertThat(messagePayload.value()).isEqualTo("{\"type\":\"PAYMENT\"}");
        }

        @Test
        @DisplayName("rejects blank input")
        void testRejectsBlankInput() {
            // WHEN / THEN
            assertThatThrownBy(() -> MessagePayload.of("\t"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not be blank");
        }

        @Test
        @DisplayName("rejects null input")
        void testRejectsNullInput() {
            // WHEN / THEN
            assertThatThrownBy(() -> MessagePayload.of(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("must not be null");
        }
    }
}
