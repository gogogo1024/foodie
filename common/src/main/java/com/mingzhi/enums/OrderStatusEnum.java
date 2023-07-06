package com.mingzhi.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatusEnum {
    WAIT_PAY(1, " 待付款"),
    WAIT_DELIVER(2, "已付款，待发货"),
    WAIT_RECEIVE(1, "已发货，待收货"),
    SUCCESS(2, "交易成功"),
    CLOSE(1, " 交易关闭");


    public final Integer type;
    public final String value;

    OrderStatusEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
