package com.mingzhi.enums;

/**
 * 支付状态枚举
 */
public enum PayStatusEnum {
    UNPAID(10, " 未支付"),
    Paid(20, "已支付"),
    PAYMENT_FAILED(30, "支付失败"),
    REFUNDED(40, "已退款");

    public final Integer type;
    public final String value;

    PayStatusEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
