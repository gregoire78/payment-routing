package com.bank.paymentrouting.message.application.mapper;

import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.domain.PaymentMessage;
import org.springframework.stereotype.Component;

@Component
public class MessageRecordMapper {

    public PaymentMessageRecord toRecord(PaymentMessage message) {
        return new PaymentMessageRecord(
                message.id(),
                message.externalMessageId().value(),
                message.payload().value(),
                message.status(),
                message.receivedAt()
        );
    }
}
