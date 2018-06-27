package com.yuan.baseandroid.http;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.yuan.baseandroid.entity.BaseEntity;
import com.yuan.baseandroid.utils.CommonUtil;
import com.yuan.baseandroid.utils.FileUtil;
import com.yuan.baseandroid.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by YUAN on 2016/5/30.
 */
public class OkHttpClientManager {

    public static final int retry = 2;
    private static final int Timeout = 10;//超时时间

    public static final int POST = 1;
    public static final int GET = 2;
    public static final int DOWNLOAD_FILE = 3;
    static OkHttpClientManager mInstance;
    private static OkHttpClient mOkHttpClient;
    private static Handler mDelivery;
    private static Gson gson;
    private static Context mContext;


    private OkHttpClientManager() {
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

    public static OkHttpClientManager getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
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
     * 异步基于get请求
     *
     * @param vo       请求参数
     * @param callback 数据回调
     * @param listener 网络监听
     * @param context  上下文
     */
    private void _get(MyHttpRequestVo vo, final ResultCallback callback, RequestListener listener, Context context) {
        final Request request = new Request.Builder()
                .url(vo.url)
                .build();
        deliveryResult(callback, request, listener, context, vo);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param vo       请求参数
     * @param callback 数据回调
     * @param listener 网络监听
     * @param context  上下文
     */
    private void _upMuchFile(MyHttpRequestVo vo, ResultCallback callback, RequestListener listener, Context context) {
        Request request = buildUpMuchFileRequest(vo);
        deliveryResult(callback, request, listener, context, vo);
    }

    /**
     * 异步下载文件
     *
     * @param vo       请求参数
     * @param callback 数据回调
     * @param listener 网络监听
     * @param context  上下文
     */
    private void _downloadFile(final MyHttpRequestVo vo, final ResultCallback
            callback, final RequestListener listener, Context context) {
        final Request request = new Request.Builder()
                .url(vo.url)
                .build();
        LogUtil.e("HTTP", "okHttp==========DOWN_URL:" + vo.url);
        final Call call = mOkHttpClient.newCall(request);
        if (CommonUtil.isNetworkAvailable(context) == 0) {
            listener.onNetWorkNotAccess();
            return;
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("HTTP", "okHttp==========DOWN_URL:" + e.toString());
                listener.onFailure();
                sendFailedStringCallback(request, e, callback, listener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file;
                    if (vo.downloadFileName.equals("")) {
                        file = new File(vo.downloadFileDir.getAbsolutePath(), FileUtil.getFileName(vo.url));
                    } else {
                        file = new File(vo.downloadFileDir.getAbsolutePath(), vo.downloadFileName);
                    }
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    LogUtil.e("HTTP", "okHttp==========DOWN_URL:" + file.getAbsolutePath());
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (IOException e) {
                    LogUtil.e("HTTP", "okHttp==========DOWN_URL:" + e.toString());
                    sendFailedStringCallback(response.request(), e, callback, listener);
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

    /***
     * 构建多文件上传Request
     */
    private Request buildUpMuchFileRequest(MyHttpRequestVo vo) {
        RequestBody requestBody = null;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (vo.params != null && vo.params.size() > 0) {
            for (Param param : vo.params) {
                if (null != param.value) {
                    builder.addFormDataPart(param.key, param.value);
                }
            }
        }
        if (vo.upMuchFiles != null && vo.upMuchFiles.size() > 0) {
            //遍历upMuchFiles中所有文件到builder
            for (FileParam fileParam : vo.upMuchFiles) {
                builder.addFormDataPart(fileParam.fileKey, fileParam.file.getName(), RequestBody.create(null, fileParam.file));
            }
        }
        builder.addFormDataPart("device", "Android");
        requestBody = builder.build();
        return new Request.Builder()
                .url(vo.url)
                .post(requestBody)
                .build();
    }

    /**
     * GET请求
     */
    private void deliveryResultGet(final ResultCallback callback, final Request request,
                                   final RequestListener listener, final Context context, final MyHttpRequestVo vo) {
        if (context != null) {
            if (CommonUtil.isNetworkAvailable(context) == 0) {
                listener.onNetWorkNotAccess();
            } else {
                LogUtil.e("HTTP", "okHttp==========URL:" + vo.url);
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("HTTP", "okHttp==========onFailure:" + e.toString());
                        sendFailedStringCallback(request, e, callback, listener);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            final String string = response.body().string();
                            LogUtil.e("HTTP", "okHttp==========onResponse:" + string);
                            Object o;
                            if (null == vo.aClass) {
                                o = gson.fromJson(string, BaseEntity.class);
                            } else {
                                o = gson.fromJson(string, vo.aClass);
                            }
                            sendSuccessResultCallback(o, callback);
                        } catch (Exception e) {
                            LogUtil.e("HTTP", "okHttp==========Exception:" + e.toString());
                            sendFailedStringCallback(response.request(), e, callback, listener);
                        }
                    }
                });
            }
        }
    }

    /**
     * 文件上传POST
     */
    private void deliveryResult(final ResultCallback callback, final Request request,
                                final RequestListener listener, final Context context, final MyHttpRequestVo vo) {
        if (context != null) {
            if (CommonUtil.isNetworkAvailable(context) == 0) {
                listener.onNetWorkNotAccess();
            } else {
                StringBuffer log = new StringBuffer();
                log.append(vo.url + "?");
                for (Param param : vo.params) {
                    log.append(param.key + "=" + param.value + "&");
                }
                LogUtil.e("HTTP", "okHttp==========URL:" + log.toString().substring(0, log.length() - 1));
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("HTTP", "okHttp==========onFailure:" + e.toString());
                        sendFailedStringCallback(request, e, callback, listener);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            final String string = response.body().string();
                            LogUtil.e("HTTP", "okHttp==========onResponse:" + string);
                            Object o;
                            if (null == vo.aClass) {
                                o = gson.fromJson(string, BaseEntity.class);
                            } else {
                                o = gson.fromJson(string, vo.aClass);
                            }
                            sendSuccessResultCallback(o, callback);
                        } catch (Exception e) {
                            LogUtil.e("HTTP", "okHttp==========Exception:" + e.toString());
                            sendFailedStringCallback(response.request(), e, callback, listener);
                        }
                    }

                });
            }
        }
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback, final RequestListener listener) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onFailure();
                }
                if (callback != null) {
                    callback.onError(request, e);
                }

            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
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
    }

    /**
     * 调用接收回调
     */
    public interface ResultCallback {
        void onError(Request request, Exception e);

        void onResponse(Object result);
    }

    public void sendRequest(int type, MyHttpRequestVo vo, ResultCallback
            callback, RequestListener listener, Context context) {
        try {
            switch (type) {
                case OkHttpClientManager.POST:
                    _upMuchFile(vo, callback, listener, context);
                    break;
                case OkHttpClientManager.GET:
                    _get(vo, callback, listener, context);
                    break;
                case OkHttpClientManager.DOWNLOAD_FILE:
                    _downloadFile(vo, callback, listener, context);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

