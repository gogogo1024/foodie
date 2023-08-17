package com.mingzhi.common.serializer.impl;

import com.mingzhi.api.Message;
import com.mingzhi.common.serializer.Serializer;
import com.mingzhi.common.serializer.SerializerFactory;

public class JacksonSerializerFactory implements SerializerFactory {
    public static final SerializerFactory INSTANCE = new JacksonSerializerFactory();

    @Override
    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }
}
