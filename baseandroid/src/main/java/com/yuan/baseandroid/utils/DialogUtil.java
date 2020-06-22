package com.yuan.baseandroid.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanyz.wheelshop.R;
import com.yuanyz.wheelshop.widget.CustomDialog;


public class DialogUtil {

    public interface OnDialogClick {
        void onClickLeft(DialogInterface dialog);

        void onClickRight(DialogInterface dialog);
    }

    /**
     * 展示选择弹框
     *
     * @param title         标题
     * @param message       内容
     * @param leftStr       左侧按钮文字
     * @param rightStr      右侧按钮文字
     * @param onDialogClick 按钮点击事件
     */
    public static void showChooseDialog(Context context, String title,
                                        String message, String leftStr, String rightStr, final OnDialogClick onDialogClick) {
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        if (null == title || "".equals(title)) {
            title = context.getString(R.string.dialog_title_submit);
        }
        customBuilder.setTitle(title);
        customBuilder.setMessage(message);
        customBuilder
                .setLeftButton(null == leftStr ? context.getString(R.string.ok) : leftStr, (dialog, which) -> {
                    dialog.dismiss();
                    if (null != onDialogClick) {
                        onDialogClick.onClickLeft(dialog);
                    }
                });
        customBuilder
                .setRightButton(null == rightStr ? context.getString(R.string.cancel) : rightStr, (dialog, which) -> {
                    dialog.dismiss();
                    if (null != onDialogClick) {
                        onDialogClick.onClickRight(dialog);
                    }
                });
        customBuilder.create().show();
    }

    /**
     * 展示提示弹框
     *
     * @param title           标题
     * @param message         内容
     * @param btnStr          按钮文字
     * @param onClickListener 按钮点击事件
     */
    public static void showInfoDialog(Context context, String title,
                                      String message, String btnStr, final DialogInterface.OnClickListener onClickListener) {
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        if (null != title && !"".equals(title)) {
            customBuilder.setTitle(title);
        }
        if (null == btnStr || "".equals(btnStr)) {
            btnStr = context.getString(R.string.ok);
        }
        customBuilder.setMessage(message);
        customBuilder
                .setLeftButton(btnStr, (dialog, which) -> {
                    dialog.dismiss();
                    if (null != onClickListener) {
                        onClickListener.onClick(dialog, which);
                    }
                });
        customBuilder.create().show();
    }

    /**
     * 展示loading弹框
     *
     * @param context 上下文
     * @param msg     展示文字
     * @return dialog实例
     */
    public static Dialog createLoadingDialog(Context context, String msg, boolean isCancel) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        ImageView spaceshipImage = (ImageView) v
                .findViewById(R.id.iv_dialog);
        TextView tipTextView = (TextView) v.findViewById(R.id.tv_dialog);// 提示文字
        AnimationDrawable aniDraw = (AnimationDrawable) spaceshipImage.getBackground();
        aniDraw.start();
        if (TextUtils.isEmpty(msg)) {
            tipTextView.setVisibility(View.GONE);
        } else {
            tipTextView.setVisibility(View.VISIBLE);
            tipTextView.setText(msg);// 设置加载信息
        }
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        if (NetworkUtil.isNetworkAvailable(context) == 0) {
            loadingDialog.setCancelable(true);
        } else {
            loadingDialog.setCancelable(isCancel);
        }

        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return loadingDialog;

    }
}
