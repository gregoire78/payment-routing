package com.bank.paymentrouting.message.application.usecase;

import java.util.List;

import com.bank.paymentrouting.message.application.mapper.MessageRecordMapper;
import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.application.port.PaymentMessageQueryPort;
import org.springframework.stereotype.Service;

@Service
public class ListMessagesUseCase {

    private final PaymentMessageQueryPort messageQueryStore;
    private final MessageRecordMapper messageRecordMapper;

    public ListMessagesUseCase(PaymentMessageQueryPort messageQueryStore, MessageRecordMapper messageRecordMapper) {
        this.messageQueryStore = messageQueryStore;
        this.messageRecordMapper = messageRecordMapper;
    }

    public List<PaymentMessageRecord> execute(int page, int size) {
        return messageQueryStore.findMessages(page, size)
                .stream()
                .map(messageRecordMapper::toRecord)
                .toList();
    }
}
