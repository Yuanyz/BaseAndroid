package com.yuan.baseandroid;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.yuan.baseandroid.callback.OnPermissionsChecked;
import com.yuan.baseandroid.http.ServiceController;
import com.yuan.baseandroid.macro.SPMacro;
import com.yuan.baseandroid.utils.DialogUtil;
import com.yuan.baseandroid.utils.LogUtil;

import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseFragment extends Fragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    protected ServiceController serviceController;
    protected BaseActivity mContext;
    protected SharedPreferences preferences;
    //权限申请
    private String[] perms;
    private static final int REQUEST_PERMISSION_BT = 1;
    private OnPermissionsChecked permissionsListener;
    private Dialog progressDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    /**
     * Android生命周期回调方法-创建
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        preferences = mContext.getSharedPreferences(SPMacro.SP_NAME, Context.MODE_PRIVATE);
        serviceController = ServiceController.getInstance(mContext);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        initData();
        setListener();
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 设置监听
     */
    protected abstract void setListener();

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.re_title_left) {
            mContext.finish();
        } else if (id == R.id.re_title_right) {
            rightTitleClick();
        }
    }


    /**
     * title右边点击
     */
    protected void rightTitleClick() {
    }

    /***
     * 设置标题
     * @param left 左侧文章
     * @param leftRes 左侧图片
     * @param title 标题
     * @param rightRes 右侧图片
     * @param right 右侧文字
     */
    protected void setTitleBarView(String left, Integer leftRes, String title, Integer rightRes, String right) {
        View titleBarView = getActivity().findViewById(R.id.title);
        RelativeLayout re_title_left = titleBarView
                .findViewById(R.id.re_title_left);
        RelativeLayout re_title_right = titleBarView
                .findViewById(R.id.re_title_right);
        TextView tv_title = titleBarView.findViewById(R.id.tv_title);
        TextView tv_title_right = titleBarView
                .findViewById(R.id.tv_title_right);
        ImageView title_right_icon = titleBarView
                .findViewById(R.id.title_right_icon);
        TextView tv_title_left = titleBarView
                .findViewById(R.id.tv_title_left);
        ImageView title_left_icon = titleBarView
                .findViewById(R.id.title_left_icon);
        if (null != title && !"".equals(title)) {
            tv_title.setText(title);
        }
        if (null != left) {
            tv_title_left.setVisibility(View.VISIBLE);
            tv_title_left.setText(left);
        }
        if (null != right) {
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
        re_title_left.setOnClickListener(this);
        re_title_right.setOnClickListener(this);
    }

    /**
     * 获取接口请求控制器
     *
     * @return 接口请求控制器
     */
    public ServiceController getServiceController() {
        return serviceController;
    }

    /**
     * 展示toast 默认时长Toast.LENGTH_SHORT
     *
     * @param str toast的内容
     */
    public void showToast(Object str) {
        if (str == null) {
            return;
        }
        if (str instanceof String) {
            Toast.makeText(mContext.getApplicationContext(), String.valueOf(str), Toast.LENGTH_SHORT).show();
        } else if (str instanceof Integer) {
            Toast.makeText(mContext.getApplicationContext(), mContext.getString((Integer) str), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 动态申请权限
     *
     * @param perms               权限数组
     * @param permissionsListener 是否授权回调
     */
    protected void checkPermission(String[] perms, OnPermissionsChecked permissionsListener) {
        this.perms = perms;
        this.permissionsListener = permissionsListener;
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            permissionsListener.OnPermissionsAgree();
        } else {
            EasyPermissions.requestPermissions(getActivity(), "需要获取以下权限才可使用",
                    REQUEST_PERMISSION_BT, perms);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.hasPermissions(mContext, perms)) {
                if (null != permissionsListener) {
                    permissionsListener.OnPermissionsAgree();
                }
            } else {
                if (null != permissionsListener) {
                    permissionsListener.OnPermissionsRefuse();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (null != permissionsListener) {
            permissionsListener.OnPermissionsAgree();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(getActivity(), perms)) {
            new AppSettingsDialog.Builder(getActivity()).build().show();
        }
    }

    /**
     * 向SharedPreferences写入数据
     *
     * @param name  key
     * @param value value
     */
    public void writeConfig(String name, Object value) {
        if (value instanceof String) {
            preferences.edit().putString(name, String.valueOf(value)).apply();
        } else if (value instanceof Integer) {
            preferences.edit().putInt(name, (Integer) value).apply();
        } else if (value instanceof Boolean) {
            preferences.edit().putBoolean(name, (Boolean) value).apply();
        } else if (value instanceof Long) {
            preferences.edit().putLong(name, (Long) value).apply();
        }
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

    /**
     * 展示选择弹框
     *
     * @param title           标题
     * @param message         内容
     * @param onClickListener 确定按钮点击事件
     */
    public void showChooseDialog(String title, String message, DialogUtil.OnDialogClick onClickListener) {
        DialogUtil.showChooseDialog(mContext, title, message, null, null, onClickListener);
    }

    /**
     * 展示选择弹框
     *
     * @param title           标题
     * @param message         内容
     * @param leftStr         取消文字
     * @param rightStr        确定文字
     * @param onClickListener 确定按钮点击事件
     */
    public void showChooseDialog(String title, String message, String leftStr, String rightStr, DialogUtil.OnDialogClick onClickListener) {
        DialogUtil.showChooseDialog(mContext, title, message, leftStr, rightStr, onClickListener);
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
     * 关闭弹框
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 打印log
     *
     * @param toShow log内容
     */
    public void showLog(String toShow) {
        if (!TextUtils.isEmpty(toShow)) {
            LogUtil.e(mContext, toShow);
        }
    }
}
