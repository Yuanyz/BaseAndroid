package com.yuan.baseandroid.utils;

import android.os.Environment;

public class ConstantValue {
    public static Boolean IS_DEBUG = true; // 是否为debug模式
    //文件缓存目录
    public static String FILE_CACHE = Environment.getExternalStorageDirectory() + "/Android/data/com.yuanyz.wheelshop/";
    //缩略图
    public static String IMAGE_CACHE = FILE_CACHE + "Image/";
    //崩溃日志存储目录
    public static String CRASH_DIR = FILE_CACHE + "crash/";

    public static String BASE_URL = "";
}