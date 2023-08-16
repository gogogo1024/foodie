package com.mingzhi.component;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class RabbitConsumer {
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
        }
    };
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @param message 消息
     * @param channel 通道
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue-1", durable = "true"),
            exchange = @Exchange(name = "exchange-1", durable = "true", type = "topic"),
            ignoreDeclarationExceptions = "true",
            key = "springboot.*"
    ))

    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws IOException {
        System.out.println("consumer message: " + message.getPayload());
        Long deliverTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliverTag, false);


    }

    public void send(Object message, Map<String, Object> properties) {
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message<?> msg = MessageBuilder.createMessage(message, messageHeaders);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                "exchange-1",
                "springboot.rabbit",
                msg,
                new MessagePostProcessor() {
                    @Override
                    public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
                        System.out.println("---> post to do: " + message);
                        return null;
                    }
                },
                correlationData
        );

    }

}
