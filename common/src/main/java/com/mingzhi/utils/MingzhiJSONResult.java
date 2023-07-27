package com.mingzhi.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MingzhiJSONResult {
    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;

    @JsonIgnore
    private String ok;    // 不使用

    public MingzhiJSONResult() { //默认会有一个空构造

    }

    public MingzhiJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public MingzhiJSONResult(Integer status, String msg, Object data, String ok) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.ok = ok;
    }

    public MingzhiJSONResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public static MingzhiJSONResult build(Integer status, String msg, Object data) {
        return new MingzhiJSONResult(status, msg, data);
    }

    public static MingzhiJSONResult build(Integer status, String msg, Object data, String ok) {
        return new MingzhiJSONResult(status, msg, data, ok);
    }

    public static MingzhiJSONResult ok(Object data) {
        return new MingzhiJSONResult(data);
    }

    public static MingzhiJSONResult ok() {
        return new MingzhiJSONResult(null);
    }

    public static MingzhiJSONResult errorMsg(String msg) {
        return new MingzhiJSONResult(500, msg, null);
    }

    public static MingzhiJSONResult errorMap(Object data) {
        return new MingzhiJSONResult(501, "error", data);
    }

    public static MingzhiJSONResult errorTokenMsg(String msg) {
        return new MingzhiJSONResult(502, msg, null);
    }

    public static MingzhiJSONResult errorException(String msg) {
        return new MingzhiJSONResult(555, msg, null);
    }

    public static MingzhiJSONResult errorUserQQ(String msg) {
        return new MingzhiJSONResult(556, msg, null);
    }

    public static MingzhiJSONResult errorUserTicket(String msg) {
        return new MingzhiJSONResult(557, msg, null);
    }

    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

}
