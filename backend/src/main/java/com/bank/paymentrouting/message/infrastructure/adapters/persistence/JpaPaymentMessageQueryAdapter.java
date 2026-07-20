package com.bank.paymentrouting.message.infrastructure.adapters.persistence;

import java.util.List;
import java.util.Optional;

import com.bank.paymentrouting.message.application.port.PaymentMessageQueryPort;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import com.bank.paymentrouting.message.infrastructure.persistence.PersistedMessageRepository;
import com.bank.paymentrouting.message.infrastructure.persistence.mapper.RoutedMessageMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class JpaPaymentMessageQueryAdapter implements PaymentMessageQueryPort {

    private final PersistedMessageRepository routedMessageRepository;
    private final RoutedMessageMapper routedMessageMapper;

    public JpaPaymentMessageQueryAdapter(
            PersistedMessageRepository routedMessageRepository,
            RoutedMessageMapper routedMessageMapper
    ) {
        this.routedMessageRepository = routedMessageRepository;
        this.routedMessageMapper = routedMessageMapper;
    }

    @Override
    public List<PaymentMessage> findMessages(int page, int size) {
        return routedMessageRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "receivedAt")))
                .stream()
                .map(routedMessageMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<PaymentMessage> findMessageById(long id) {
        return routedMessageRepository.findById(id)
                .map(routedMessageMapper::toDomain);
    }
}