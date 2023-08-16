package com.mingzhi.api;

import com.mingzhi.api.exception.MessageRunTimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * builder mode
 */
public class MessageBuilder {
    /* 消息id */
    private String messageId;
    /* 消息主题 */
    private String topic;
    /* 消息路由规则 */
    private String routingKey = "";
    /* 消息附加属性 */
    private Map<String, Object> properties = new HashMap<>();
    /* 延迟消息毫秒数 */
    private int delayMills;
    /* 消息类型 */
    private String messageType = MessageType.CONFIRM;

    private MessageBuilder() {

    }

    private static MessageBuilder create() {
        return new MessageBuilder();
    }

    public MessageBuilder withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public MessageBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder withRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public MessageBuilder withProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public MessageBuilder withProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public MessageBuilder withDelayMills(int delayMills) {
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder withMessageType(String messageType) throws Exception {
        if (!(Objects.equals(messageType, MessageType.RAPID)
                || Objects.equals(messageType, MessageType.CONFIRM)
                || Objects.equals(messageType, MessageType.RELIANT))) {
            throw new Exception("设置的messageType类型不对");
        }
        this.messageType = messageType;
        return this;
    }

    public Message build() throws MessageRunTimeException {
        // TODO 参数检验
        if (messageId == null) {
            messageId = UUID.randomUUID().toString();
        }
        if (topic == null) {
            throw new MessageRunTimeException("topic is null");
        }
        return new Message(messageId, topic, routingKey, properties, delayMills, messageType);
    }

}
