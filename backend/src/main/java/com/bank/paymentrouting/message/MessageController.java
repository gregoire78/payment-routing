package com.bank.paymentrouting.message;

import com.bank.paymentrouting.config.MqProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.ACCEPTED;

@Validated
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final MqMessageProducer mqMessageProducer;
    private final MqProperties mqProperties;

    public MessageController(
            MessageService messageService,
            MqMessageProducer mqMessageProducer,
            MqProperties mqProperties
    ) {
        this.messageService = messageService;
        this.mqMessageProducer = mqMessageProducer;
        this.mqProperties = mqProperties;
    }

    @GetMapping
    public List<MessageView> listMessages(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return messageService.listMessages(page, size);
    }

    @GetMapping("/{id}")
    public MessageView getMessage(@PathVariable long id) {
        return messageService.getMessage(id);
    }

    @PostMapping("/publish")
    @ResponseStatus(ACCEPTED)
    public Map<String, String> publishMessage(@Valid @RequestBody PublishMessageRequest request) {
        mqMessageProducer.publish(mqProperties.queueName(), request);
        return Map.of(
                "status", "accepted",
                "externalMessageId", request.externalMessageId()
        );
    }
}