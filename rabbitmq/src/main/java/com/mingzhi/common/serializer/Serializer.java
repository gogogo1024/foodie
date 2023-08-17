package com.mingzhi.common.serializer;

/**
 * 序列化以及反序列化
 */
public interface Serializer {
    byte[] serializeRaw(Object data);

    String serialize(Object data);

    <T> T deserialize(String content);

    <T> T deserialize(byte[] content);
}
