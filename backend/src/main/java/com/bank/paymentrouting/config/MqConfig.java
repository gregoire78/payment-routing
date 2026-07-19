package com.bank.paymentrouting.config;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

@Configuration
@EnableJms
public class MqConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqConfig.class);

    @Bean
    ConnectionFactory mqConnectionFactory(MqProperties mqProperties) throws JMSException {
        MQConnectionFactory factory = new MQConnectionFactory();
        factory.setQueueManager(mqProperties.queueManager());
        factory.setChannel(mqProperties.channel());
        factory.setConnectionNameList(mqProperties.connName());
        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        factory.setStringProperty(WMQConstants.USERID, mqProperties.user());
        factory.setStringProperty(WMQConstants.PASSWORD, mqProperties.password());
        return factory;
    }

    @Bean
    DefaultJmsListenerContainerFactory mqJmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MqProperties mqProperties
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setSessionTransacted(true);
        factory.setConcurrency(mqProperties.concurrency());
        factory.setErrorHandler(t -> LOGGER.error("Unhandled MQ listener error", t));
        return factory;
    }

    @Bean
    JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }
}