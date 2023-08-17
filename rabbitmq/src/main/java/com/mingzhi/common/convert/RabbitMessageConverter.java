package com.mingzhi.common.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Objects;

/**
 * decorator mode
 */
public class RabbitMessageConverter implements MessageConverter {
    public GenericMessageConverter delegate;

    public RabbitMessageConverter(GenericMessageConverter genericMessageConverter) {
        Objects.requireNonNull(genericMessageConverter);
        this.delegate = genericMessageConverter;
    }

    /**
     * Convert a Java object to a Message.
     *
     * @param object            the object to convert
     * @param messageProperties The message properties.
     * @return the Message
     * @throws MessageConversionException in case of conversion failure
     */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return this.delegate.toMessage(object, messageProperties);
    }


    /**
     * Convert from a Message to a Java object.
     *
     * @param message the message to convert
     * @return the converted Java object
     * @throws MessageConversionException in case of conversion failure
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return (com.mingzhi.api.Message) this.delegate.fromMessage(message);
    }
}
