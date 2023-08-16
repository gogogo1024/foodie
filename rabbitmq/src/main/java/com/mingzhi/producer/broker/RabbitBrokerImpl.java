package com.mingzhi.producer.broker;

import com.mingzhi.api.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitBrokerImpl implements RabbitBroker {
    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    @Override
    public void rapidSend(Message message) {
        sendKernel(message);

    }

    /**
     * 异步线程池发送消息
     *
     * @param message 消息
     */
    private void sendKernel(Message message) {
        AsyncBaseQueue.submit((Runnable) () -> {
            CorrelationData correlationData = new CorrelationData(
                    String.format("%s#%s",
                            message.getMessageId(),
                            System.currentTimeMillis())
            );
            String routingKey = message.getRoutingKey();
            String topic = message.getTopic();
            rabbitTemplateContainer.getTemplate(message).convertAndSend(topic, routingKey, message, correlationData);
            log.info("#RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId:{}", message.getMessageId());
        });
    }

    @Override
    public void confirmSend(Message message) {

    }

    @Override
    public void reliantSend(Message message) {

    }
}
