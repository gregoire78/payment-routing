package com.bank.paymentrouting.message.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistedMessageRepository extends JpaRepository<PersistedMessageEntity, Long> {

    Optional<PersistedMessageEntity> findByExternalMessageId(String externalMessageId);

    long countByExternalMessageId(String externalMessageId);
}