package com.bank.paymentrouting.message.infrastructure.persistence;

import java.time.Instant;

import com.bank.paymentrouting.message.domain.MessageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "routed_messages", indexes = {
        @Index(name = "idx_routed_messages_received_at", columnList = "received_at"),
        @Index(name = "idx_routed_messages_status", columnList = "status")
})
public class PersistedMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_message_id", nullable = false, unique = true, length = 100)
    private String externalMessageId;

    @Column(name = "payload", nullable = false, columnDefinition = "text")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private MessageStatus status;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    protected PersistedMessageEntity() {
    }

    public PersistedMessageEntity(String externalMessageId, String payload, MessageStatus status, Instant receivedAt) {
        this.externalMessageId = externalMessageId;
        this.payload = payload;
        this.status = status;
        this.receivedAt = receivedAt;
    }

    public Long getId() {
        return id;
    }

    public String getExternalMessageId() {
        return externalMessageId;
    }

    public String getPayload() {
        return payload;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}