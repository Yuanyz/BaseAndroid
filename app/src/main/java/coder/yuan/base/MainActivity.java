package coder.yuan.base;

import android.view.View;

import com.yuan.baseandroid.BaseActivity;

public class MainActivity extends BaseActivity {

    /**
     * 加载布局
     */
    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_main);
        setTitleBarView(null, null, "首页", null, null);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
    }

    @Override
    protected void leftTitleClick() {
        super.leftTitleClick();
        showToast("left");
    }

    @Override
    protected void rightTitleClick() {
        super.rightTitleClick();
        showToast("right");
    }


    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

}
