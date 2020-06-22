package com.yuan.baseandroid.http;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.yuan.baseandroid.utils.ConstantValue;
import com.yuan.baseandroid.utils.LogUtil;
import com.yuan.baseandroid.utils.NetworkUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by YUAN on 2016/5/30.
 */
public class OkHttpManager {
    public static final int retry = 2;
    private static final int Timeout = 100;//超时时间

    private static OkHttpManager mInstance;
    private static OkHttpClient mOkHttpClient;
    private static Handler mDelivery;
    private static Gson gson;
    private static Context mContext;

    private OkHttpManager() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Timeout, TimeUnit.SECONDS)
                .writeTimeout(Timeout, TimeUnit.SECONDS)
                .readTimeout(Timeout, TimeUnit.SECONDS).build();
        //cookie enabled
//        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(mContext.getMainLooper());
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        gson = new Gson();
    }

    public static OkHttpManager getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            synchronized (OkHttpManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取OkHttpClient
     *
     * @return OkHttpClient
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 构建网络请求
     *
     * @param vo       请求参数
     * @param listener 网络监听
     * @param context  上下文
     */
    private void _buildRequest(RequestVo vo, RequestListener listener, Context context) {
        if (vo.getMethodType() == RequestVo.DOWNLOAD_FILE) {
            _downloadFile(vo, listener, context);
        } else {
            if (vo.getMethodType() != RequestVo.UPLOAD_FILE) {//需要拼接url,
                String url = ConstantValue.BASE_URL + vo.getUrl();
                vo.setUrl(url);
            }
            Request request = buildRequestByType(vo);
            deliveryResult(request, listener, context, vo);
        }
    }

    /**
     * 异步下载文件
     *
     * @param vo       请求参数
     * @param listener 网络监听
     * @param context  上下文
     */
    private void _downloadFile(final RequestVo vo, final RequestListener listener, Context context) {
        final Request request = new Request.Builder()
                .url(vo.getUrl())
                .build();
        LogUtil.e("HTTP", "okHttp==========DOWN_URL:" + vo.getUrl());
        final Call call = mOkHttpClient.newCall(request);
        if (NetworkUtil.isNetworkAvailable(context) == 0) {
            listener.onNetWorkNotAccess();
            return;
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("HTTP", "okHttp==========DOWN_URL:" + e.toString());
                listener.onFailure();
                sendFailedStringCallback(request, e, listener);
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file;
                    if (vo.getDownloadFileName().equals("")) {
                        file = new File(vo.getDownloadFileDir().getAbsolutePath(), getFileName(vo.getUrl()));
                    } else {
                        file = new File(vo.getDownloadFileDir().getAbsolutePath(), vo.getDownloadFileName());
                    }
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    LogUtil.e("HTTP", "okHttp==========DOWN_URL:" + file.getAbsolutePath());
                    sendSuccessResultCallback(file.getAbsolutePath(), listener);
                } catch (IOException e) {
                    LogUtil.e("HTTP", "okHttp==========DOWN_URL:" + e.toString());
                    sendFailedStringCallback(response.request(), e, listener);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    /**
     * 根据请求类型构建Request
     *
     * @param vo 请求封装
     * @return request
     */
    private Request buildRequestByType(RequestVo vo) {
        Request.Builder request = new Request.Builder();
        request.addHeader("CLIENTVERSION", "V1");
        for (Param header : vo.getHead()) {
            request.addHeader(header.key, header.value);
        }
        for (Param param : vo.getLink()) {
            if (vo.getUrl().contains(param.key)) {
                String url = vo.getUrl().replace(param.key, param.value);
                vo.setUrl(url);
            }
        }
        switch (vo.getMethodType()) {
            case RequestVo.POST_FORM:
            case RequestVo.UPLOAD_FILE:
                RequestBody requestBody = null;
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("type", "android");
                for (Param param : vo.getParams()) {
                    builder.addFormDataPart(param.key, param.value);
                }
                for (FileParam fileParam : vo.getUpMuchFiles()) {
                    builder.addFormDataPart(fileParam.fileKey, fileParam.file.getName(), RequestBody.create(MediaType.parse("application-octecstream"), fileParam.file));
                }
                requestBody = builder.build();
                request.post(requestBody);
                break;
            case RequestVo.POST_BODY:
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, vo.getBody().toString());
                request.post(body);
                break;
            case RequestVo.GET:
                StringBuilder url = new StringBuilder(vo.getUrl() + "?");
                for (Param param : vo.getParams()) {
                    url.append(param.key + "=" + param.value + "&");
                }
                vo.setUrl(url.toString().substring(0, url.length() - 1));
                break;
            case RequestVo.DELETE:
                request.delete();
                break;
        }
        request.url(vo.getUrl());
        return request.build();
    }

    /**
     * 文件上传POST
     */
    private void deliveryResult(final Request request, final RequestListener listener, Context context, final RequestVo vo) {
        if (context != null) {
            if (NetworkUtil.isNetworkAvailable(context) == 0) {
                listener.onNetWorkNotAccess();
                LogUtil.e("HTTP", "okHttp==========ERROR:无网络连接");
            } else {
                buildRequestLog(vo);
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("HTTP", "okHttp==========onFailure:" + e.toString());
                        sendFailedStringCallback(request, e, listener);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            final String string = response.body().string();
                            Object o;
                            if (null == vo.getBean()) {
                                o = string;
                            } else {
                                o = gson.fromJson(string, vo.getBean());
                            }
                            sendSuccessResultCallback(o, listener);
                        } catch (Exception e) {
                            LogUtil.e("HTTP", "okHttp==========Exception:" + e.toString());
                            sendFailedStringCallback(response.request(), e, listener);
                        }
                    }

                });
            }
        }
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final RequestListener listener) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onFailure();
                    listener.onError(request, e);
                }
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final RequestListener callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    /**
     * 基类接收监听
     */
    public interface RequestListener {
        void onNetWorkNotAccess();

        void onFailure();

        void onError(Request request, Exception e);

        void onResponse(Object result);
    }

    /**
     * 调用接收回调
     */
    public interface ResultCallback {
        void onError(Request request, Exception e);

        void onResponse(Object result);
    }

    public void sendRequest(RequestVo vo, RequestListener listener, Context context) {
        _buildRequest(vo, listener, context);
    }

    /**
     * 获取下载地址的文件名
     *
     * @param path 下载地址
     * @return
     */
    public String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 构建请求log
     *
     * @param vo 请求参数
     */
    private void buildRequestLog(RequestVo vo) {
        if (vo.getHead().size() > 0) {
            StringBuilder log = new StringBuilder();
            for (Param param : vo.getHead()) {
                log.append(param.key + "=" + param.value + "---");
            }
            LogUtil.e("HTTP", "okHttp==========HEAD:" + log.toString().substring(0, log.length() - 3));
        }
        LogUtil.e("HTTP", "okHttp==========METHOD:" + vo.getMethod());
        LogUtil.e("HTTP", "okHttp==========URL:" + vo.getUrl());
        if (vo.getParams().size() > 0) {
            StringBuilder log = new StringBuilder();
            for (Param param : vo.getParams()) {
                log.append(param.key + "=" + param.value + "&");
            }
            LogUtil.e("HTTP", "okHttp==========PARAMS:" + log.toString().substring(0, log.length() - 1));
        }
        LogUtil.e("HTTP", "okHttp==========BODY:" + vo.getBody().toString());
    }
}

