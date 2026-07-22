package com.bank.paymentrouting.message.application.port;

public interface PublishMessagePort {

    void execute(String externalMessageId, String payload);
}