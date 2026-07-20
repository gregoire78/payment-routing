package com.bank.paymentrouting.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AppCorsProperties")
class AppCorsPropertiesTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("uses default origin when value is null")
        void testUsesDefaultOriginWhenValueIsNull() {
            // GIVEN / WHEN
            AppCorsProperties appCorsProperties = new AppCorsProperties(null);

            // THEN
            assertThat(appCorsProperties.allowedOrigins())
                    .containsExactly("http://localhost:4200");
        }

        @Test
        @DisplayName("uses default origin when list is empty")
        void testUsesDefaultOriginWhenListIsEmpty() {
            // GIVEN / WHEN
            AppCorsProperties appCorsProperties = new AppCorsProperties(List.of());

            // THEN
            assertThat(appCorsProperties.allowedOrigins())
                    .containsExactly("http://localhost:4200");
        }

        @Test
        @DisplayName("copies provided origins as immutable list")
        void testCopiesProvidedOriginsAsImmutableList() {
            // GIVEN
            List<String> input = new ArrayList<>();
            input.add("http://localhost:4200");
            input.add("https://bank.example");

            // WHEN
            AppCorsProperties appCorsProperties = new AppCorsProperties(input);
            input.add("https://mutated.example");

            // THEN
            assertThat(appCorsProperties.allowedOrigins())
                    .containsExactly("http://localhost:4200", "https://bank.example");
            assertThatThrownBy(() -> appCorsProperties.allowedOrigins().add("https://other.example"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
