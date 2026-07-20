package com.bank.paymentrouting.message.infrastructure.api;

import jakarta.validation.constraints.NotBlank;

public record PublishMessageRequest(
        @NotBlank String externalMessageId,
        @NotBlank String payload
) {
}