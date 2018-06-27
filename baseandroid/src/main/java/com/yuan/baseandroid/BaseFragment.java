package com.yuan.baseandroid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuan.baseandroid.callback.OnPermissionsChecked;
import com.yuan.baseandroid.http.MyHttpRequestVo;
import com.yuan.baseandroid.http.OkHttpClientManager;
import com.yuan.baseandroid.utils.AppManager;
import com.yuan.baseandroid.utils.DialogUtil;
import com.yuan.baseandroid.utils.LogUtil;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @ClassName: BaseActivity
 * @Description: 提供公共方法的基类
 * @author:1214334172@qq.com
 * @date 2015年4月30日 上午10:35:18
 * <p>
 * 支持GSYVideoPlayer  AppCompatActivity
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    protected RelativeLayout re_title;
    protected LinearLayout ll_title_left;
    protected TextView tv_title_left;
    protected ImageView title_left_icon;
    protected LinearLayout ll_title_right;
    protected TextView tv_title_right;
    protected ImageView title_right_icon;
    protected TextView tv_title;

    protected BaseActivity mContext;
    private SharedPreferences preferences;
    protected Dialog progressDialog;

    //权限申请
    private String[] perms;
    private static final int REQUEST_PERMISSION_BT = 1;
    private OnPermissionsChecked permissionsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    /**
     * Android生命周期回调方法-创建
     */
    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mContext = (BaseActivity) getActivity();
        preferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        initData();
        setListener();
    }

    /**
     * 动态申请权限
     * @param perms 权限数组
     * @param permissionsListener 是否授权回调
     */
    protected void checkPermission(String[] perms, OnPermissionsChecked permissionsListener) {
        this.perms = perms;
        this.permissionsListener = permissionsListener;
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            permissionsListener.OnPermissionsAgree();
        } else {
            EasyPermissions.requestPermissions(this, "需要获取以下权限才可使用",
                    REQUEST_PERMISSION_BT, perms);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.hasPermissions(mContext, perms)) {
                permissionsListener.OnPermissionsAgree();
            } else {
                permissionsListener.OnPermissionsRefuse();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        permissionsListener.OnPermissionsAgree();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 设置监听
     */
    protected abstract void setListener();

    /**
     * title右边点击
     */
    protected void rightTitleClick() {

    }

    /**
     * title左边点击
     */
    protected void leftTitleClick() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_title_left) {
            leftTitleClick();
        } else if (v.getId() == R.id.ll_title_right) {
            rightTitleClick();
        }
    }


    /***
     * 设置标题 若为空时请传入null
     * @param left 左侧文章
     * @param leftRes 左侧图片
     * @param title 标题
     * @param rightRes 右侧图片
     * @param right 右侧文字
     */
    protected void setTitleBarView(String left, Integer leftRes, String title, Integer rightRes, String right) {
        View titleBarView = getActivity().findViewById(R.id.title);
        re_title = (RelativeLayout) titleBarView.findViewById(R.id.re_title);
        ll_title_left = (LinearLayout) titleBarView
                .findViewById(R.id.ll_title_left);
        ll_title_right = (LinearLayout) titleBarView
                .findViewById(R.id.ll_title_right);
        tv_title = (TextView) titleBarView.findViewById(R.id.tv_title);
        tv_title_right = (TextView) titleBarView
                .findViewById(R.id.tv_title_right);
        title_right_icon = (ImageView) titleBarView
                .findViewById(R.id.title_right_icon);
        tv_title_left = (TextView) titleBarView
                .findViewById(R.id.tv_title_left);
        title_left_icon = (ImageView) titleBarView
                .findViewById(R.id.title_left_icon);
        if (!isEmpty(title)) {
            tv_title.setText(title);
        }
        if (!isEmpty(left)) {
            tv_title_left.setVisibility(View.VISIBLE);
            tv_title_left.setText(left);
        }
        if (!isEmpty(right)) {
            tv_title_right.setVisibility(View.VISIBLE);
            tv_title_right.setText(right);
        }
        if (null != leftRes) {
            title_left_icon.setVisibility(View.VISIBLE);
            title_left_icon.setBackgroundResource(leftRes);
        }
        if (null != rightRes) {
            title_right_icon.setVisibility(View.VISIBLE);
            title_right_icon.setBackgroundResource(rightRes);
        }
        ll_title_left.setOnClickListener(this);
        ll_title_right.setOnClickListener(this);
    }

    /**
     * 展示选择弹框
     *
     * @param title           标题
     * @param message         内容
     * @param onClickListener 确定按钮点击事件
     */
    public void showChooseDialog(String title, String message, DialogInterface.OnClickListener onClickListener) {
        DialogUtil.showChooseDialog(mContext, title, message, "取消", "确定", onClickListener);
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
        DialogUtil.showChooseDialog(mContext, title, message, cancel, enter, onClickListener);
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
        DialogUtil.showInfoDialog(mContext, title, message, btnStr, onClickListener);
    }

    /**
     * 展示消息弹框
     *
     * @param message  弹框提示文字
     * @param isCancel 是否可点击外部取消
     */
    public void showProgressDialog(String message, boolean isCancel) {
        if (null != mContext && !mContext.isFinishing()) {
            if (progressDialog == null) {
                progressDialog = DialogUtil.createLoadingDialog(mContext, message, isCancel);
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

    /**
     * 判断字符串是否为空
     *
     * @param str 校验的数据
     * @return true or false
     */
    public boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }

    /**
     * 展示toast 默认时长Toast.LENGTH_SHORT
     *
     * @param str toast的内容
     */
    protected void showToast(String str) {
        if (!isEmpty(str)) {
            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打印log
     *
     * @param toShow log内容
     */
    protected void showLog(String toShow) {
        if (!isEmpty(toShow)) {
            LogUtil.i(mContext, toShow);
        }
    }

    /**
     * 打印log
     *
     * @param tag    TAG
     * @param toShow log内容
     */
    protected void showLog(Object tag, String toShow) {
        if (!isEmpty(toShow)) {
            LogUtil.i(tag, toShow);
        }
    }

    /**
     * 跳转Activity
     *
     * @param cls 目标Activity
     */
    public void forward(Class cls) {
        Intent intent = new Intent(mContext, cls);
        startActivity(intent);
    }

    /**
     * 跳转并结束当前Activity
     *
     * @param cls 目标Activity
     */
    protected void forwardAndFinish(Class cls) {
        Intent intent = new Intent(mContext, cls);
        startActivity(intent);
        AppManager.getAppManager().finishActivity();
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
        OkHttpClientManager.getInstance(mContext).sendRequest(methodType, vo, callback,
                new OkHttpClientManager.RequestListener() {

                    @Override
                    public void onNetWorkNotAccess() {
//                        noNetConnection();
                    }

                    @Override
                    public void onFailure() {
                    }
                }, getActivity());
    }

}
