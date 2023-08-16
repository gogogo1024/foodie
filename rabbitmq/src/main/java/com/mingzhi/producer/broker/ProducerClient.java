package com.mingzhi.producer.broker;

import com.mingzhi.api.Message;
import com.mingzhi.api.MessageProducer;
import com.mingzhi.api.MessageType;
import com.mingzhi.api.SendCallback;
import com.mingzhi.api.exception.MessageRunTimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRunTimeException {
        Objects.requireNonNull(message.getTopic());
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.CONFIRM -> rabbitBroker.confirmSend(message);
            case MessageType.RAPID -> rabbitBroker.rapidSend(message);
            case MessageType.RELIANT -> rabbitBroker.reliantSend(message);
            default -> {
            }
        }
    }

    @Override
    public void send(Message message) throws MessageRunTimeException {

    }

    @Override
    public void send(List<Message> messageList) throws MessageRunTimeException {

    }
}
