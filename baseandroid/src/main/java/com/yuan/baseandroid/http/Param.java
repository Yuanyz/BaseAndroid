package com.yuan.baseandroid.http;

/**
 * Created by YUAN on 2017/11/2.
 */

public class Param {
    public Param(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String key;
    public String value;

    @Override
    public String toString() {
        return "{" + key + "=" + value + "}";
    }
}
