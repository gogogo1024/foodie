package com.mingzhi.api;

public final class MessageType {
    /* 快速消息 不保证消息可靠以及不确认 */
    public final static String RAPID = "0";
    /* 确认消息 不保证消息可靠以及确认 */
    public final static String CONFIRM = "1";
    /* 可靠消息 保证消息可靠以及确认 */
    public final static String RELIANT = "2";

}
