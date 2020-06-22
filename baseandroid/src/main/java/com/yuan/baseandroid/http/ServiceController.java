package com.yuan.baseandroid.http;

import com.google.gson.Gson;
import com.yuan.baseandroid.BaseActivity;

import okhttp3.Request;

public class ServiceController {
    private static ServiceController controller = null;

    private BaseActivity activity;
    private Gson gson;

    private ServiceController(BaseActivity mContext) {
        activity = mContext;
        gson = new Gson();
    }

    public static ServiceController getInstance(BaseActivity mContext) {
        if (controller == null) {
            controller = new ServiceController(mContext);
        }
        return controller;
    }

    /***
     * 获取网络数据
     *
     * @param vo         请求参数
     * @param callback   数据回调
     */

    private void getDataFromServer(RequestVo vo,
                                   final OkHttpManager.ResultCallback callback) {
        OkHttpManager.getInstance(activity).sendRequest(vo,
                new OkHttpManager.RequestListener() {

                    @Override
                    public void onNetWorkNotAccess() {
                        callback.onError(null, new Exception("没有网络连接"));
                    }

                    @Override
                    public void onFailure() {
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        callback.onError(request, e);
                    }

                    @Override
                    public void onResponse(Object result) {
                        callback.onResponse(result);
                    }
                }, activity.getApplicationContext());
    }
}
