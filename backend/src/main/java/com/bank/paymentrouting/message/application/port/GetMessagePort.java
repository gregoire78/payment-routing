package com.bank.paymentrouting.message.application.port;

import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;

public interface GetMessagePort {

    PaymentMessageRecord execute(long id);
}