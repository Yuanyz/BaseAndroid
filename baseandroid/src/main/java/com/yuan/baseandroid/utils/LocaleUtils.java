package com.yuan.baseandroid.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import com.yuanyz.wheelshop.macro.SPMacro;

import java.util.Locale;

public class LocaleUtils {
    /**
     * 中文
     */
    public static final Locale LOCALE_CHINESE = Locale.CHINESE;
    /**
     * 英文
     */
    public static final Locale LOCALE_ENGLISH = Locale.ENGLISH;
    /**
     * 瑞典文
     */
    public static final Locale LOCALE_SWEDEN = new Locale("sv","SE");
    /**
     * 获取当前的Locale
     *
     * @param pContext Context
     * @return Locale
     */
    public static Locale getCurrentLocale(Context pContext) {
        Locale _Locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0有多语言设置获取顶部的语言
            _Locale = pContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            _Locale = pContext.getResources().getConfiguration().locale;
        }
        return _Locale;
    }

    /**
     * 设置语言
     *
     * @param context  上下文
     * @param language 语言
     */
    public static void setLanguage(Context context, String language) {
        if (!language.equals("")) {
            if (language.equals("zh")) {
                LocaleUtils.updateLocale(context, LocaleUtils.LOCALE_CHINESE);
            } else if (language.equals("en")) {
                LocaleUtils.updateLocale(context, LocaleUtils.LOCALE_ENGLISH);
            }else if (language.equals("sv")){
                LocaleUtils.updateLocale(context, LocaleUtils.LOCALE_SWEDEN);
            }
        }
    }

    /**
     * 更新Locale
     *
     * @param pContext       Context
     * @param newLocale New User Locale
     */
    private static void updateLocale(Context pContext, Locale newLocale) {
        Configuration configuration = pContext.getResources().getConfiguration();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            configuration.setLocale(newLocale);
//        } else {
//            configuration.locale = newLocale;
//        }
//        DisplayMetrics _DisplayMetrics = pContext.getResources().getDisplayMetrics();
//        pContext.getResources().updateConfiguration(configuration, _DisplayMetrics);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            pContext.createConfigurationContext(configuration);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            configuration.setLocale(newLocale);
            pContext.createConfigurationContext(configuration);
        }
    }

    /**
     * 判断需不需要更新
     *
     * @param pContext       Context
     * @param pNewUserLocale New User Locale
     * @return true / false
     */
    private static boolean needUpdateLocale(Context pContext, Locale pNewUserLocale) {
        return pNewUserLocale != null && !getCurrentLocale(pContext).getLanguage().contains(pNewUserLocale.getLanguage());
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Context wrapContext(Context context) {
        Resources resources = context.getResources();
        String language = NoClearSharedPrefer.getInstance(context).readConfigString(SPMacro.LANGUAGE);
        Locale locale = null;
        if (language.equals("zh")) {
            locale= LocaleUtils.LOCALE_CHINESE;
        } else if (language.equals("en")) {
            locale= LocaleUtils.LOCALE_ENGLISH;
        }else if (language.equals("sv")){
            locale= LocaleUtils.LOCALE_SWEDEN;
        }else {
            locale = Locale.getDefault();
        }

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);
        return context.createConfigurationContext(configuration);
    }
}
