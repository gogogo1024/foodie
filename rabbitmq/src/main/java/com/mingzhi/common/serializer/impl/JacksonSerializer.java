package com.mingzhi.common.serializer.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mingzhi.common.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JacksonSerializer implements Serializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonSerializer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    private final JavaType javaType;

    public JacksonSerializer(JavaType javaType) {
        this.javaType = objectMapper.getTypeFactory().constructType(javaType);
    }

    public static JacksonSerializer createParametricType(Class<?> cls) {
        return new JacksonSerializer(objectMapper.getTypeFactory().constructType(cls));
    }

    @Override
    public byte[] serializeRaw(Object data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            LOGGER.error("serializer error", e);
        }
        return null;

    }

    @Override
    public String serialize(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            LOGGER.error("serializer error", e);
        }
        return null;
    }

    @Override
    public <T> T deserialize(String content) {
        try {
            return objectMapper.readValue(content, javaType);
        } catch (IOException e) {
            LOGGER.error("deserializer error", e);
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] content) {
        try {
            return objectMapper.readValue(content, javaType);
        } catch (IOException e) {
            LOGGER.error("deserializer error", e);
        }
        return null;
    }
}
