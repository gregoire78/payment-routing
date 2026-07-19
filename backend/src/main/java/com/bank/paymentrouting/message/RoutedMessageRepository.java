package com.bank.paymentrouting.message;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutedMessageRepository extends JpaRepository<RoutedMessage, Long> {

	Optional<RoutedMessage> findByExternalMessageId(String externalMessageId);

	long countByExternalMessageId(String externalMessageId);
}