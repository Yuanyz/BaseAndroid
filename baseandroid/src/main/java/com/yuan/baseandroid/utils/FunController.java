package com.yuan.baseandroid.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.yuan.baseandroid.BaseActivity;
import com.yuan.baseandroid.http.MyHttpRequestVo;
import com.yuan.baseandroid.http.OkHttpClientManager;

public class FunController {
    private static FunController controller = null;

    private BaseActivity activity;
    private Dialog progressDialog;
    private SharedPreferences preferences;

    private FunController(BaseActivity mContext) {
        activity = mContext;
        preferences = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public static FunController getInstance(BaseActivity mContext) {
        if (controller == null) {
            controller = new FunController(mContext);
        }
        return controller;
    }

    /**
     * 向SharedPreferences写入String数据
     *
     * @param name  key
     * @param value 数据
     */
    public void writeConfig(String name, String value) {
        preferences.edit().putString(name, value).apply();
    }

    /**
     * 向SharedPreferences写入Long数据
     *
     * @param name  key
     * @param value 数据
     */
    public void writeConfig(String name, Long value) {
        preferences.edit().putLong(name, value).apply();
    }

    /**
     * 向SharedPreferences写入int数据
     *
     * @param name  key
     * @param value 数据
     */
    public void writeConfig(String name, int value) {
        preferences.edit().putInt(name, value).apply();
    }

    /**
     * 向SharedPreferences写入Boolean数据
     *
     * @param name  key
     * @param value 数据
     */
    public void writeConfig(String name, Boolean value) {
        preferences.edit().putBoolean(name, value).apply();
    }

    /**
     * 删除SharedPreferences数据
     *
     * @param name key
     */
    public void removeString(String name) {
        preferences.edit().remove(name).apply();
    }

    /**
     * 读取SharedPreferences数据
     *
     * @param key key
     * @return 对应数据类型
     */
    public long readConfigLong(String key) {
        return preferences.getLong(key, 0);
    }

    public int readConfigInt(String key) {
        return preferences.getInt(key, -1);
    }

    public Boolean readConfigBol(String key) {
        return preferences.getBoolean(key, false);
    }

    public String readConfigString(String key) {
        return preferences.getString(key, "");
    }

    /**
     * 清空SharedPreferences数据
     */
    public void clearConfig() {
        preferences.edit().clear().apply();
    }

    /***
     * 获取网络数据
     *
     * @param methodType 请求方式
     * @param vo         请求参数
     * @param callback   数据回调
     */

    public void getDataFromServer(int methodType, MyHttpRequestVo vo,
                                   OkHttpClientManager.ResultCallback callback) {
        OkHttpClientManager.getInstance(activity).sendRequest(methodType, vo, callback,
                new OkHttpClientManager.RequestListener() {

                    @Override
                    public void onNetWorkNotAccess() {
//                        noNetConnection();
                    }

                    @Override
                    public void onFailure() {
                    }
                }, activity.getApplicationContext());
    }

    /**
     * 展示选择弹框
     *
     * @param title           标题
     * @param message         内容
     * @param onClickListener 确定按钮点击事件
     */
    public void showChooseDialog(String title, String message, DialogInterface.OnClickListener onClickListener) {
        DialogUtil.showChooseDialog(activity, title, message, "取消", "确定", onClickListener);
    }

    /**
     * 展示选择弹框
     *
     * @param title           标题
     * @param message         内容
     * @param cancel          取消文字
     * @param enter           确定文字
     * @param onClickListener 确定按钮点击事件
     */
    public void showChooseDialog(String title, String message, String cancel, String enter, DialogInterface.OnClickListener onClickListener) {
        DialogUtil.showChooseDialog(activity, title, message, cancel, enter, onClickListener);
    }

    /**
     * 展示提示弹框
     *
     * @param title           标题
     * @param message         内容
     * @param btnStr          按钮文字
     * @param onClickListener 按钮点击事件
     */
    public void showNoticeDialog(String title, String message, String btnStr, DialogInterface.OnClickListener onClickListener) {
        DialogUtil.showInfoDialog(activity, title, message, btnStr, onClickListener);
    }

    /**
     * 展示消息弹框
     *
     * @param message  弹框提示文字
     * @param isCancel 是否可点击外部取消
     */
    public void showProgressDialog(String message, boolean isCancel) {
        if (null != activity && !activity.isFinishing()) {
            if (progressDialog == null) {
                progressDialog = DialogUtil.createLoadingDialog(activity, message, isCancel);
            }
            progressDialog.show();
        }
    }

    /**
     * 关闭弹框
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
