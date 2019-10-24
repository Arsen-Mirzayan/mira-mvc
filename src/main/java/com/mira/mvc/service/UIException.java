package com.mira.mvc.service;

/**
 * Исключение, текст которого можно показывать пользователю
 */
public class UIException extends RuntimeException {
    protected String code;

    public UIException(String code) {
        this.code = code;
    }

    public UIException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
