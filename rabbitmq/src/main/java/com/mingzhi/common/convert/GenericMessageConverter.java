package com.mingzhi.common.convert;

import com.mingzhi.common.serializer.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Objects;

public class GenericMessageConverter implements MessageConverter {
    private Serializer serializer;

    public GenericMessageConverter(Serializer serializer) {
        Objects.requireNonNull(serializer);
        this.serializer = serializer;
    }


    /**
     * Convert a Java object to a Message.
     * The default implementation calls {@link #toMessage(Object, MessageProperties)}.
     *
     * @param object            the object to convert
     * @param messageProperties The message properties.
     * @return the Message
     * @throws MessageConversionException in case of conversion failure
     * @since 2.1
     */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(this.serializer.serializeRaw(object), messageProperties);
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
        return this.serializer.deserialize(message.getBody());
    }
}
