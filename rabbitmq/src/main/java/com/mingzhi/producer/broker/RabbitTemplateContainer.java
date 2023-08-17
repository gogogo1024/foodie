package com.mingzhi.producer.broker;

import com.google.common.base.Splitter;
import com.mingzhi.api.Message;
import com.mingzhi.api.MessageType;
import com.mingzhi.common.convert.GenericMessageConverter;
import com.mingzhi.common.convert.RabbitMessageConverter;
import com.mingzhi.common.serializer.SerializerFactory;
import com.mingzhi.common.serializer.impl.JacksonSerializerFactory;
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
    private SerializerFactory serializerFactor = JacksonSerializerFactory.INSTANCE;
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
        // 序列化以及反序列化
        // 发消息: 自定义message通过springframework转换成ampqMessage）
        // 收消息: ampqMessage通过JacksonSerializer转换成自定义message）
        GenericMessageConverter genericMessageConverter = new GenericMessageConverter(
                serializerFactor.create());
        RabbitMessageConverter rabbitMessageConverter = new RabbitMessageConverter(genericMessageConverter);
        rabbitTemplate1.setMessageConverter(rabbitMessageConverter);
        String messageType = message.getMessageType();
        // 除了rapid send 都需要确认回调
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
