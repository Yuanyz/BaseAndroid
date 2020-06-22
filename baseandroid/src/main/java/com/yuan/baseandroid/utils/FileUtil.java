package com.yuan.baseandroid.utils;

import java.io.File;
import java.io.FileInputStream;

public class FileUtil {
    /**
     * 获取指定文件大小(单位：字节)
     *
     * @param file 文件
     * @return
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return size;
    }

    /**
     * 获取文件名
     *
     * @param path 下载地址
     */
    public static String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }
}
