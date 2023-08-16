package com.mingzhi.component;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RabbitSender {
    // 消息确认回调监听，确认broker收到消息
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         *
         * @param correlationData correlation data for the callback.
         * @param ack true for ack, false for nack
         * @param cause An optional cause, for nack, when available, otherwise null.
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if (correlationData != null) {
                System.out.println("message ack result: " + ack + ", correlationData: " + correlationData.getId());
            }
        }
    };
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Object message, Map<String, Object> properties) {
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message<?> msg = MessageBuilder.createMessage(message, messageHeaders);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                "exchange-1",
                "springboot.rabbit",
                msg,
                message1 -> {
                    System.out.println("---> post to do: " + message1);
                    return message1;
                },
                correlationData
        );

    }

}
