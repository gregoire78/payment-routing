package com.bank.paymentrouting.message.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ExternalMessageId")
class ExternalMessageIdTest {

    @Nested
    @DisplayName("of")
    class Of {

        @Test
        @DisplayName("creates value object when input is valid")
        void testCreatesValueObjectWhenInputIsValid() {
            // GIVEN / WHEN
            ExternalMessageId externalMessageId = ExternalMessageId.of("MSG-001");

            // THEN
            assertThat(externalMessageId.value()).isEqualTo("MSG-001");
        }

        @Test
        @DisplayName("rejects blank input")
        void testRejectsBlankInput() {
            // WHEN / THEN
            assertThatThrownBy(() -> ExternalMessageId.of(" "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not be blank");
        }

        @Test
        @DisplayName("rejects null input")
        void testRejectsNullInput() {
            // WHEN / THEN
            assertThatThrownBy(() -> ExternalMessageId.of(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("must not be null");
        }
    }
}
