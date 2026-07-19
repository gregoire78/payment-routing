package com.bank.paymentrouting.message;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private final RoutedMessageRepository routedMessageRepository;

    public MessageService(RoutedMessageRepository routedMessageRepository) {
        this.routedMessageRepository = routedMessageRepository;
    }

    public List<MessageView> listMessages(int page, int size) {
        return routedMessageRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "receivedAt")))
                .stream()
                .map(MessageView::fromEntity)
                .toList();
    }

    public MessageView getMessage(long id) {
        return routedMessageRepository.findById(id)
                .map(MessageView::fromEntity)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }
}