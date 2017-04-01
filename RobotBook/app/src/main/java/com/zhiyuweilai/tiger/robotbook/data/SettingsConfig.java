package com.zhiyuweilai.tiger.robotbook.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by yaohu on 2017/3/27.
 */

public class SettingsConfig {

    public static final String IS_SHOW_GUIDE = "show_guide_page";
    public static final String IS_LOGIN = "islogin";
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
}
