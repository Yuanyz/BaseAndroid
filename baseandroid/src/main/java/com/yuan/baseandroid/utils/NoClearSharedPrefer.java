package com.yuan.baseandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuan.baseandroid.macro.SPMacro;


/**
 * SharedPreferences存储退出登录不清除的数据
 */

public class NoClearSharedPrefer {
    public static NoClearSharedPrefer noClearSharedPrefer;

    public SharedPreferences preferences;

    public NoClearSharedPrefer(Context context) {
        preferences = context.getSharedPreferences(SPMacro.NO_CLEAN_SP_NAME, Context.MODE_PRIVATE);
    }

    public static NoClearSharedPrefer getInstance(Context context) {
        noClearSharedPrefer = new NoClearSharedPrefer(context);
        return noClearSharedPrefer;
    }

    public void writeConfig(String name, Object value) {
        if (value instanceof String){
            preferences.edit().putString(name, String.valueOf(value)).apply();
        }else if (value instanceof Integer){
            preferences.edit().putInt(name, (Integer) value).apply();
        }else if (value instanceof Boolean){
            preferences.edit().putBoolean(name, (Boolean) value).apply();
        }else if (value instanceof Long){
            preferences.edit().putLong(name, (Long) value).apply();
        }
    }

    public void removeString(String name) {
        preferences.edit().remove(name).apply();
    }

    public long readConfigLong(String key) {
        return preferences.getLong(key, 0);
    }

    public int readConfigInt(String key) {
        return preferences.getInt(key, 0);
    }

    public Boolean readConfigBol(String key) {
        return preferences.getBoolean(key, false);
    }

    public String readConfigString(String key) {
        return preferences.getString(key, "");
    }

    //清空本地数据
    public void clearConfig() {
        preferences.edit().clear().apply();
    }

}
