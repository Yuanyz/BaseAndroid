package com.yuan.baseandroid.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yuan.baseandroid.R;

import java.util.ArrayList;
import java.util.List;

public class PictureSelectDialog {
    private Context context;
    private Dialog dialog;
    private TextView txt_title;
    private TextView txt_cancel;
    private LinearLayout lLayout_content;
    private ScrollView sLayout_content;
    private boolean showTitle = false;
    private List<SheetItem> sheetItemList;
    private Display display;
    private OnSheetItemClickListener listener;

    public PictureSelectDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public PictureSelectDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_picture_select, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        sLayout_content = (ScrollView) view.findViewById(R.id.sLayout_content);
        lLayout_content = (LinearLayout) view
                .findViewById(R.id.lLayout_content);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);
        txt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.PictureSelectDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        return this;
    }

    public PictureSelectDialog setTitle(String title) {
        showTitle = true;
        txt_title.setVisibility(View.VISIBLE);
        txt_title.setText(title);
        return this;
    }

    public PictureSelectDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public PictureSelectDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public PictureSelectDialog addSheetItem(ArrayList<SheetItem> sheets, OnSheetItemClickListener listener) {
        this.listener = listener;
        if (sheetItemList == null) {
            sheetItemList = new ArrayList<>();
        }
        if (null != sheets) {
            sheetItemList.addAll(sheets);
        }
        return this;
    }

    /**
     * 设置条目布局
     */
    private void setSheetItems() {
        if (sheetItemList == null || sheetItemList.size() <= 0) {
            return;
        }

        int size = sheetItemList.size();

        // 添加条目过多的时候控制高度
        if (size >= 7) {
            LayoutParams params = (LayoutParams) sLayout_content
                    .getLayoutParams();
            params.height = display.getHeight() / 2;
            sLayout_content.setLayoutParams(params);
        }
        // 循环添加条目
        for (int i = 0; i < size; i++) {
            final int index = i;
            SheetItem sheetItem = sheetItemList.get(i);
            TextView textView = new TextView(context);
            textView.setText(sheetItem.getName());
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            // 字体颜色
            textView.setTextColor(sheetItem.getColor());
            // 高度
            float scale = context.getResources().getDisplayMetrics().density;
            int height = (int) (45 * scale + 0.5f);
            textView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, height));
            // 点击事件
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != listener) {
                        listener.onItemClick(index);
                    }
                    dialog.dismiss();
                }
            });
            View line = LayoutInflater.from(context).inflate(
                    R.layout.layout_line, null);
            lLayout_content.addView(textView);
            lLayout_content.addView(line);
        }
    }

    public void show() {
        setSheetItems();
        dialog.show();
    }

    public interface OnSheetItemClickListener {
        void onItemClick(int position);
    }

    public static class SheetItem {
        private String name;
        private int color;

        public SheetItem(String name, int color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
