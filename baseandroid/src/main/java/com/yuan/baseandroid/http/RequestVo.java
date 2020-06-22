package com.yuan.baseandroid.http;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * 网络请求参数
 */
public class RequestVo {
    public static final int POST_FORM = 1;
    public static final int GET = 2;
    public static final int DOWNLOAD_FILE = 3;
    public static final int POST_BODY = 4;
    public static final int DELETE = 5;
    public static final int UPLOAD_FILE = 6;//全路径上传文件

    //网络请求方式
    private int methodType = POST_BODY;
    //请求地址
    private String url;
    //替换请求链接
    private ArrayList<Param> link = new ArrayList<>();
    //请求头
    private ArrayList<Param> head = new ArrayList<>();
    //请求参数
    private ArrayList<Param> params = new ArrayList<>();
    //body
    private Object body = new JSONObject();
    //多文件上传
    private ArrayList<FileParam> upMuchFiles = new ArrayList<>();
    //解析实体类型
    private Class aClass;
    //下载文件 存放路径
    private File downloadFileDir;
    //下载文件 存放路径
    private String downloadFileName = "";

    /**
     * 设置网络请求方式
     *
     * @param method 请求方式
     */
    public void setHttpMethod(int method) {
        methodType = method;
    }

    /**
     * 设置请求链接
     *
     * @param url 请求链接
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 设置实体解析类
     *
     * @param aClass 实体解析类
     */
    public void setClass(Class aClass) {
        this.aClass = aClass;
    }

    /**
     * 替换请求链接
     *
     * @param name  替换名
     * @param value 替换值
     */
    public void setLinks(String name, String value) {
        link.add(new Param(name, value));
    }

    /**
     * 设置请求参数
     *
     * @param name  参数名
     * @param value 参数值
     */
    public void setParams(String name, String value) {
        params.add(new Param(name, value));
    }

    /**
     * 设置请求头
     *
     * @param name  请求头名
     * @param value 请求头参数
     */
    public void setHead(String name, String value) {
        head.add(new Param(name, value));
    }

    /**
     * 设置上传文件
     *
     * @param fileKey 上传key
     * @param file    文件
     */
    public void setUpFile(String fileKey, File file) {
        upMuchFiles.add(new FileParam(fileKey, file));
    }

    /**
     * 设置文件下载信息
     *
     * @param fileDir  下载文件存放路径
     * @param fileName 下载文件存放名称
     */
    public void setDownInfo(File fileDir, String fileName) {
        this.downloadFileDir = fileDir;
        this.downloadFileName = fileName;
    }

    public int getMethodType() {
        return methodType;
    }

    public String getMethod() {
        switch (methodType) {
            case POST_FORM:
                return "POST_FORM";
            case GET:
                return "GET";
            case DOWNLOAD_FILE:
                return "DOWNLOAD_FILE";
            case POST_BODY:
                return "POST_BODY";
            case DELETE:
                return "DELETE";
            default:
                return "";
        }
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<Param> getHead() {
        return head;
    }

    public ArrayList<Param> getParams() {
        return params;
    }

    public ArrayList<FileParam> getUpMuchFiles() {
        return upMuchFiles;
    }

    public Class getBean() {
        return aClass;
    }

    public File getDownloadFileDir() {
        return downloadFileDir;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public ArrayList<Param> getLink() {
        return link;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
