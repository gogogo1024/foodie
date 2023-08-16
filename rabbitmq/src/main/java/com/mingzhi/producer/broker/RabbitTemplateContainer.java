package com.mingzhi.producer.broker;

import com.google.common.base.Splitter;
import com.mingzhi.api.Message;
import com.mingzhi.api.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
/*
  RabbitTemplate容器
  提高发送效率
  根据主题分类RabbitTemplate
 */
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {
    private final Splitter splitter = Splitter.on("#");
    private Map<String, RabbitTemplate> rabbitTemplateMap = new HashMap<>();
    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * 获取主题对应的RabbitTemplate
     * 如果存在topic对应的RabbitTemplate直接返回
     * 不存在时新建RabbitTemplate后，返回新建的RabbitTemplate
     *
     * @param message 消息
     */
    public RabbitTemplate getTemplate(Message message) {
        Objects.requireNonNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitTemplateMap.get(topic);
        if (rabbitTemplate != null) {
            log.info("#RabbitTemplateContainer.getTemplate# topic: {} ", topic);
            return rabbitTemplate;
        } else {
            RabbitTemplate rabbitTemplate1 = setNewRabbitTemplate(message, topic);
            rabbitTemplateMap.putIfAbsent(topic, rabbitTemplate1);
            return rabbitTemplate1;
        }

    }

    /**
     * 设置新的RabbitTemplate
     *
     * @param message 消息
     * @param topic   主题
     * @return RabbitTemplate
     */
    private RabbitTemplate setNewRabbitTemplate(Message message, String topic) {
        RabbitTemplate rabbitTemplate1 = new RabbitTemplate(connectionFactory);
        rabbitTemplate1.setExchange(topic);
        rabbitTemplate1.setRetryTemplate(new RetryTemplate());
        rabbitTemplate1.setRoutingKey(message.getRoutingKey());
        // TODO 高性能序列化
//            rabbitTemplate1.setMessageConverter("");
        String messageType = message.getMessageType();
        if (!Objects.equals(messageType, MessageType.RAPID)) {
            rabbitTemplate1.setConfirmCallback(this);
        }
        return rabbitTemplate1;
    }

    /**
     * Confirmation callback.
     *
     * @param correlationData correlation data for the callback.
     * @param ack             true for ack, false for nack
     * @param cause           An optional cause, for nack, when available, otherwise null.
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        List<String> stringList;
        if (correlationData != null) {
            // #RabbitBrokerImpl.sendKernel#中封装的
            stringList = splitter.splitToList(correlationData.getId());
            String messageId = stringList.get(0);
            String sendTime = stringList.get(1);
            if (ack) {
                log.info(
                        "send message ok messageId: {}, sendTime: {} ",
                        messageId, sendTime);
            } else {
                log.error(
                        "send message fail messageId: {}, sendTime: {} ",
                        messageId, sendTime);
            }
        }

    }
}
