package com.bank.paymentrouting.message.application.port;

import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;

public interface IngestMessagePort {

    PaymentMessageRecord execute(String externalMessageId, String payload);
}