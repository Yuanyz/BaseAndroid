package com.yuan.baseandroid.http;

import java.io.File;
import java.util.ArrayList;

/**
 * 网络请求参数
 * Created by YUAN on 2016/5/30.
 */
public class MyHttpRequestVo {
    /***
     * 请求地址
     */
    public String url;
    /***
     * 请求参数
     */
    public ArrayList<Param> params = new ArrayList<>();
    /**
     * 解析实体类型
     */
    public Class aClass;
    /***
     * 下载文件 存放路径
     */
    public File downloadFileDir;
    /***
     * 下载文件 存放路径
     */
    public String downloadFileName = "";
    /***
     * 多文件上传
     */
    public ArrayList<FileParam> upMuchFiles = new ArrayList<>();





}
