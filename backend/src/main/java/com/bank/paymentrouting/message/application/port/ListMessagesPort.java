package com.bank.paymentrouting.message.application.port;

import java.util.List;

import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;

public interface ListMessagesPort {

    List<PaymentMessageRecord> execute(int page, int size);
}