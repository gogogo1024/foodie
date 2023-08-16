package com.mingzhi.api;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = -5929236461856319760L;
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

    public Message() {
    }

    public Message(String messageId, String topic, String routingKey, Map<String, Object> properties, int delayMills, String messageType) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.properties = properties;
        this.delayMills = delayMills;
        this.messageType = messageType;
    }
}
