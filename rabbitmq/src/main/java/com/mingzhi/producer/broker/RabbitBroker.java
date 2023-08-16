package com.mingzhi.producer.broker;

import com.mingzhi.api.Message;

/**
 * 不同类型消息接口
 */
public interface RabbitBroker {
    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);

}
