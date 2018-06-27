package com.yuan.baseandroid.http;

/**
 * Created by YUAN on 2017/11/2.
 */

import java.io.File;

/***
 * 多文件上传实体封装
 */
public class FileParam {
    public FileParam(String fileKey, File file) {
        this.fileKey = fileKey;
        this.file = file;
    }

    public String fileKey;
    public File file;

    @Override
    public String toString() {
        return "{" + fileKey + "=" + file + "}";
    }
}
