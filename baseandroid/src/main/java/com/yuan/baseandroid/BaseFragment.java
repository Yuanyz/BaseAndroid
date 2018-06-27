package com.yuan.baseandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuan.baseandroid.callback.OnPermissionsChecked;
import com.yuan.baseandroid.utils.FunController;
import com.yuan.baseandroid.utils.LogUtil;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @ClassName: BaseActivity
 * @Description: 提供公共方法的基类
 * @author:1214334172@qq.com
 * @date 2015年4月30日 上午10:35:18
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

    protected FunController controller;

    protected BaseActivity mContext;


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
        controller = FunController.getInstance(mContext);
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
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
        if (!TextUtils.isEmpty(left)) {
            tv_title_left.setVisibility(View.VISIBLE);
            tv_title_left.setText(left);
        }
        if (!TextUtils.isEmpty(right)) {
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
     * 展示toast 默认时长Toast.LENGTH_SHORT
     *
     * @param str toast的内容
     */
    protected void showToast(String str) {
        if (!TextUtils.isEmpty(str)) {
            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打印log
     *
     * @param toShow log内容
     */
    protected void showLog(String toShow) {
        if (!TextUtils.isEmpty(toShow)) {
            LogUtil.i(mContext, toShow);
        }
    }

}
