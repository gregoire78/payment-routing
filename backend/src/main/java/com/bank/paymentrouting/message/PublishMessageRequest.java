package com.bank.paymentrouting.message;

import jakarta.validation.constraints.NotBlank;

public record PublishMessageRequest(
        @NotBlank String externalMessageId,
        @NotBlank String payload
) {
}