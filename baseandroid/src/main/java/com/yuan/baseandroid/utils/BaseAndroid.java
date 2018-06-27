package com.yuan.baseandroid.utils;

/**
 * Created by YUAN on 2017/8/17.
 */

public class BaseAndroid {
    private static boolean IS_DEBUG = true;//是否为debug模式

    public static boolean isDebug() {
        return IS_DEBUG;
    }

    public static void setIsDebug(boolean isDebug) {
        IS_DEBUG = isDebug;
    }
}
