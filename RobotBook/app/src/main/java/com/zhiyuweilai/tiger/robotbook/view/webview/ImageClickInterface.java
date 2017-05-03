package com.zhiyuweilai.tiger.robotbook.view.webview;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.zhiyuweilai.tiger.robotbook.data.SettingsConfig;
import com.zhiyuweilai.tiger.robotbook.utils.IntentUtils;

/**
 * Created by jingbin on 2016/11/17.
 * js通信接口
 */
public class ImageClickInterface {
    private Context context;

    public ImageClickInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void getToken(String token, String stuId){
        SettingsConfig.getInstance(context).saveInfo(token,stuId);
    }
    @JavascriptInterface
    public void startActivity(String uri) {
        try {
            IntentUtils.starIntent(context,uri);
        }catch (Exception e){

        }
    }
}
