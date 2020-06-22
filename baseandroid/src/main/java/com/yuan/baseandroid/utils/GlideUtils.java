package com.yuan.baseandroid.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.yuanyz.wheelshop.R;

public class GlideUtils {

    private static RequestOptions getOptions() {
        return new RequestOptions()
                .placeholder(R.drawable.ic_img_loading)
                .error(R.drawable.ic_img_loading)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    private static RequestOptions getCircleOptions() {
        return new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_img_loading)
                .error(R.drawable.ic_img_loading)
                .transform(new GlideCircleTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    private static RequestOptions getHeadIcoOptions() {
        return new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.user_male)
                .error(R.drawable.user_male)
                .transform(new GlideCircleTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    /**
     * 根据 url（url图片已经是缩略图了） 来加载图片
     *
     * @param context  请求的上下文，Glide请求会根据该上下文的生命周期控制
     * @param url      图片地址
     * @param targetIV 目标 ImageView
     *                 with(Context context)，使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制。
     *                 with(Activity activity)，使用Activity作为上下文，Glide的请求会受到Activity生命周期控制。
     *                 with(FragmentActivity activity)，Glide的请求会受到FragmentActivity生命周期控制。
     *                 with(android.app.Fragment fragment)，Glide的请求会受到Fragment 生命周期控制。
     *                 with(android.support.v4.app.Fragment fragment)，Glide的请求会受到Fragment生命周期控制。
     */
    public static void loadImageWithUrl(Context context, Object url, ImageView targetIV) {
        if (url instanceof String && ((String) url).startsWith("http")) {
            url = new GlideUrl(String.valueOf(url), new LazyHeaders.Builder().build());
        }
        if (context != null) {
            Glide.with(context)
                    .load(url)
                    //加载设置
                    .apply(getOptions())
                    //默认图片加载的动画方式
                    .into(targetIV);
        }

    }

    public static void loadImageWithUrl(Context context, Object url, ImageView targetIV, RequestOptions options) {
        if (url instanceof String && ((String) url).startsWith("http")) {
            url = new GlideUrl(String.valueOf(url), new LazyHeaders.Builder().build());
        }
        if (context != null) {
            Glide.with(context)
                    .load(url)
                    //加载设置
                    .apply(options)
                    //默认图片加载的动画方式
                    .into(targetIV);
        }
    }

    /**
     * 圆形图片裁剪
     *
     * @param context
     * @param url
     * @param targetIV
     */
    public static void loadImageCropCircle(Context context, Object url, ImageView targetIV) {
        if (url instanceof String && ((String) url).startsWith("http")) {
            url = new GlideUrl(String.valueOf(url), new LazyHeaders.Builder().build());
        }
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(url)
                //加载设置
                .apply(getCircleOptions())
                //默认图片加载的动画方式
                .into(targetIV);

    }

    /**
     * 清除磁盘缓存和内存缓存大小
     *
     * @param context
     */
    public static void clearCache(final Context context) {
        new Thread(() -> {
            //清除磁盘缓存  必须在后台线程中调用，建议同时clearMemory()
            Glide.get(context).clearDiskCache();
        }).start();
        //清除内存缓存  必须在UI线程中调用
        Glide.get(context).clearMemory();
    }
}
