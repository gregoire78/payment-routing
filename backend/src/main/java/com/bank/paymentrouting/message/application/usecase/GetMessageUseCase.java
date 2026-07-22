package com.bank.paymentrouting.message.application.usecase;

import com.bank.paymentrouting.message.application.exception.MessageNotFoundException;
import com.bank.paymentrouting.message.application.mapper.MessageRecordMapper;
import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.application.port.GetMessagePort;
import com.bank.paymentrouting.message.application.port.PaymentMessageQueryPort;
import org.springframework.stereotype.Service;

@Service
public class GetMessageUseCase implements GetMessagePort {

    private final PaymentMessageQueryPort messageQueryStore;
    private final MessageRecordMapper messageRecordMapper;

    public GetMessageUseCase(PaymentMessageQueryPort messageQueryStore, MessageRecordMapper messageRecordMapper) {
        this.messageQueryStore = messageQueryStore;
        this.messageRecordMapper = messageRecordMapper;
    }

    @Override
    public PaymentMessageRecord execute(long id) {
        return messageQueryStore.findMessageById(id)
                .map(messageRecordMapper::toRecord)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }
}
