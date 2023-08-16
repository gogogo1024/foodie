package com.mingzhi.api.exception;

public class MessageRunTimeException extends Exception {


    private static final long serialVersionUID = -4737843281539934019L;

    public MessageRunTimeException() {
        super();
    }

    public MessageRunTimeException(String message) {
        super(message);
    }

    public MessageRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRunTimeException(Throwable cause) {
        super(cause);
    }
}
