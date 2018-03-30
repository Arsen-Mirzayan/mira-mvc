package com.mira.mvc.system;

/**
 * ����������, ����� �������� ����� ���������� ������������
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
