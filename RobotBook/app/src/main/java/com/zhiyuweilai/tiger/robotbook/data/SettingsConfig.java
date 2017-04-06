package com.zhiyuweilai.tiger.robotbook.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by yaohu on 2017/3/27.
 */

public class SettingsConfig {

    public static final String IS_SHOW_GUIDE = "show_guide_page";
    public static final String IS_LOGIN = "islogin";
    public static final String REQUEST_CONFIG = "REQUEST_CONFIG";
    private SharedPreferences mSharedPreferences;
    private Context mCtx;

    public SettingsConfig(Context ctx) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private static SettingsConfig instance;

    public static SettingsConfig getInstance(Context ctx) {
        if (instance == null)
            instance = new SettingsConfig(ctx);
        return instance;
    }

    public void isShowGuidePage(boolean isshow) {
        mSharedPreferences.edit().putBoolean(IS_SHOW_GUIDE, isshow).commit();
    }

    public boolean showGuidePage() {
        return mSharedPreferences.getBoolean(IS_SHOW_GUIDE, true);
    }

    public void setRequestConfig(String requestConfig) {
        mSharedPreferences.edit().putString(REQUEST_CONFIG, requestConfig).commit();
        mSharedPreferences.edit().putBoolean(IS_LOGIN, TextUtils.isEmpty(requestConfig)).commit();
    }

    public String getRequestConfig() {
        return mSharedPreferences.getString(REQUEST_CONFIG, null);
    }

    public boolean isLogin() {
        return mSharedPreferences.getBoolean(IS_LOGIN, false);
    }

}
