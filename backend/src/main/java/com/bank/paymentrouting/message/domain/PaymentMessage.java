package com.bank.paymentrouting.message.domain;

import java.time.Instant;
import java.util.Objects;

public final class PaymentMessage {

    private final Long id;
    private final ExternalMessageId externalMessageId;
    private final MessagePayload payload;
    private final MessageStatus status;
    private final Instant receivedAt;

    private PaymentMessage(
            Long id,
            ExternalMessageId externalMessageId,
            MessagePayload payload,
            MessageStatus status,
            Instant receivedAt
    ) {
        this.id = id;
        this.externalMessageId = Objects.requireNonNull(externalMessageId, "externalMessageId must not be null");
        this.payload = Objects.requireNonNull(payload, "payload must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.receivedAt = Objects.requireNonNull(receivedAt, "receivedAt must not be null");
    }

    public static PaymentMessage received(ExternalMessageId externalMessageId, MessagePayload payload, Instant receivedAt) {
        return new PaymentMessage(null, externalMessageId, payload, MessageStatus.RECEIVED, receivedAt);
    }

    public static PaymentMessage restore(
            Long id,
            ExternalMessageId externalMessageId,
            MessagePayload payload,
            MessageStatus status,
            Instant receivedAt
    ) {
        return new PaymentMessage(id, externalMessageId, payload, status, receivedAt);
    }

    public PaymentMessage markRouted() {
        ensureTransitionAllowed(MessageStatus.ROUTED);
        return new PaymentMessage(id, externalMessageId, payload, MessageStatus.ROUTED, receivedAt);
    }

    public PaymentMessage markFailed() {
        ensureTransitionAllowed(MessageStatus.FAILED);
        return new PaymentMessage(id, externalMessageId, payload, MessageStatus.FAILED, receivedAt);
    }

    private void ensureTransitionAllowed(MessageStatus targetStatus) {
        if (status != MessageStatus.RECEIVED) {
            throw new IllegalStateException(
                    "Transition from " + status + " to " + targetStatus + " is not allowed"
            );
        }
    }

    public Long id() {
        return id;
    }

    public ExternalMessageId externalMessageId() {
        return externalMessageId;
    }

    public MessagePayload payload() {
        return payload;
    }

    public MessageStatus status() {
        return status;
    }

    public Instant receivedAt() {
        return receivedAt;
    }
}
