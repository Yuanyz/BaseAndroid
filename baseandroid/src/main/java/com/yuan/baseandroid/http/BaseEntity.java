package com.yuan.baseandroid.http;

import java.io.Serializable;

/**
 * 数据请求通用基类
 */
public class BaseEntity implements Serializable {
    private String message;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResult() {
        return code;
    }

    public void setResult(int result) {
        this.code = result;
    }
}
