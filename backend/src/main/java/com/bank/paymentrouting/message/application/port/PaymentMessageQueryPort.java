package com.bank.paymentrouting.message.application.port;

import java.util.List;
import java.util.Optional;

import com.bank.paymentrouting.message.domain.PaymentMessage;

public interface PaymentMessageQueryPort {

    List<PaymentMessage> findMessages(int page, int size);

    Optional<PaymentMessage> findMessageById(long id);
}
