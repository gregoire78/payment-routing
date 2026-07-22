package com.bank.paymentrouting.message.infrastructure.api;

import com.bank.paymentrouting.message.application.model.PaymentMessageRecord;
import com.bank.paymentrouting.message.application.port.GetMessagePort;
import com.bank.paymentrouting.message.application.port.ListMessagesPort;
import com.bank.paymentrouting.message.application.port.PublishMessagePort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;

@Validated
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final ListMessagesPort listMessagesPort;
    private final GetMessagePort getMessagePort;
    private final PublishMessagePort publishMessagePort;

    public MessageController(
            ListMessagesPort listMessagesPort,
            GetMessagePort getMessagePort,
            PublishMessagePort publishMessagePort
    ) {
        this.listMessagesPort = listMessagesPort;
        this.getMessagePort = getMessagePort;
        this.publishMessagePort = publishMessagePort;
    }

    @GetMapping
    public List<PaymentMessageResponse> listMessages(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return listMessagesPort.execute(page, size)
                .stream()
                .map(this::toView)
                .toList();
    }

    @GetMapping("/{id}")
    public PaymentMessageResponse getMessage(@PathVariable long id) {
        return toView(getMessagePort.execute(id));
    }

    @PostMapping("/publish")
    @ResponseStatus(ACCEPTED)
    public Map<String, String> publishMessage(@Valid @RequestBody PublishMessageRequest request) {
        publishMessagePort.execute(request.externalMessageId(), request.payload());
        return Map.of(
                "status", "accepted",
                "externalMessageId", request.externalMessageId()
        );
    }

    private PaymentMessageResponse toView(PaymentMessageRecord messageRecord) {
        return new PaymentMessageResponse(
                messageRecord.id(),
                messageRecord.externalMessageId(),
                messageRecord.payload(),
                messageRecord.status(),
                messageRecord.receivedAt()
        );
    }
}