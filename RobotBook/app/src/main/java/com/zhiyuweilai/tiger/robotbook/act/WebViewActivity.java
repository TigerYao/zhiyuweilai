package com.zhiyuweilai.tiger.robotbook.act;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.zhiyuweilai.tiger.robotbook.R;
import com.zhiyuweilai.tiger.robotbook.view.webview.IWebPageView;
import com.zhiyuweilai.tiger.robotbook.view.webview.ImageClickInterface;
import com.zhiyuweilai.tiger.robotbook.view.webview.MyWebChromeClient;
import com.zhiyuweilai.tiger.robotbook.view.webview.MyWebViewClient;
import com.zhiyuweilai.tiger.robotbook.databinding.ActivityWebViewBinding;


/**
 * 网页可以处理:
 * 点击相应控件:拨打电话、发送短信、发送邮件、上传图片、播放视频
 * 进度条、返回网页上一层、显示网页标题
 */
public class WebViewActivity extends AppCompatActivity implements IWebPageView {

    // 进度条

    public boolean mProgress90;
    public boolean mPageFinish;
    //private MyWebChromeClient mWebChromeClient;
    private String mUrl = "file:///android_asset/callsms.html";//"http://47.93.12.142:8080/zuowen/login.html";
    private ActivityWebViewBinding webbinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
       webbinding = DataBindingUtil.setContentView( this,R.layout.activity_web_view);
        setTitle("详情");
        initWebView();
        webbinding.webview.loadUrl(mUrl);
    }

    private void getIntentData() {
//        if (getIntent() != null) {
//            mIsMovie = getIntent().getBooleanExtra("mIsMovie", false);
//            mUrl = getIntent().getStringExtra("mUrl");
//        }
    }

    private void initWebView() {
         webbinding.progressbar.setVisibility(View.VISIBLE);
        WebSettings ws =  webbinding.webview.getSettings();
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(true);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        // 启动应用缓存
        ws.setAppCacheEnabled(true);
        // 设置缓存模式
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);
        // 缩放比例 1
         webbinding.webview.setInitialScale(1);
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        ws.setDomStorageEnabled(true);
        // 排版适应屏幕
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否支持多个窗口。
        ws.setSupportMultipleWindows(true);

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/
        ws.setTextZoom(100);

//        mWebChromeClient = new MyWebChromeClient(this);
//         webbinding.webview.setWebChromeClient(mWebChromeClient);
        // 与js交互
         webbinding.webview.addJavascriptInterface(new ImageClickInterface(this), "client");
         webbinding.webview.setWebViewClient(new MyWebViewClient(this));
    }

    @Override
    public void hindProgressBar() {
         webbinding.progressbar.setVisibility(View.GONE);
    }

    @Override
    public void startProgress() {
        startProgress90();
    }

    @Override
    public void showWebView() {
         webbinding.webview.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindWebView() {
         webbinding.webview.setVisibility(View.INVISIBLE);
    }

    @Override
    public void progressChanged(int newProgress) {
        if (mProgress90) {
            int progress = newProgress * 100;
            if (progress > 900) {
                 webbinding.progressbar.setProgress(progress);
                if (progress == 1000) {
                     webbinding.progressbar.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void addImageClickListener() {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        // 如要点击一张图片在弹出的页面查看所有的图片集合,则获取的值应该是个图片数组
//        webView.loadUrl("javascript:(function(){" +
//                "var objs = document.getElementsByTagName(\"img\");" +
//                "for(var i=0;i<objs.length;i++)" +
//                "{" +
//                //  "objs[i].onclick=function(){alert(this.getAttribute(\"has_link\"));}" +
//                "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"),this.getAttribute(\"has_link\"));}" +
//                "}" +
//                "})()");

//        // 遍历所有的a节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
//        webView.loadUrl("javascript:(function(){" +
//                "var objs =document.getElementsByTagName(\"a\");" +
//                "for(var i=0;i<objs.length;i++)" +
//                "{" +
//                "objs[i].onclick=function(){" +
//                "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
//                "}" +
//                "})()");
    }

    /**
     * 进度条 假装加载到90%
     */
    public void startProgress90() {
        for (int i = 0; i < 900; i++) {
            final int progress = i + 1;
             webbinding.progressbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                     webbinding.progressbar.setProgress(progress);
                    if (progress == 900) {
                        mProgress90 = true;
                        if (mPageFinish) {
                            startProgress90to100();
                        }
                    }
                }
            }, (i + 1) * 2);
        }
    }

    /**
     * 进度条 加载到100%
     */
    public void startProgress90to100() {
        for (int i = 900; i <= 1000; i++) {
            final int progress = i + 1;
             webbinding.progressbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                     webbinding.progressbar.setProgress(progress);
                    if (progress == 1000) {
                         webbinding.progressbar.setVisibility(View.GONE);
                    }
                }
            }, (i + 1) * 2);
        }
    }




    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
       // mWebChromeClient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 上传图片之后的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE) {
//            mWebChromeClient.mUploadMessage(intent, resultCode);
//        } else if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
//            mWebChromeClient.mUploadMessageForAndroid5(intent, resultCode);
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //全屏播放退出全屏
//            if (mWebChromeClient.inCustomView()) {
//                hideCustomView();
//                return true;
//
//                //返回网页上一页
//            } else

            if ( webbinding.webview.canGoBack()) {
                 webbinding.webview.goBack();
                return true;

                //退出网页
            } else {
                 webbinding.webview.loadUrl("about:blank");
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
         webbinding.webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
         webbinding.webview.onResume();
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
         webbinding.webview.resumeTimers();
        // 设置为横屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( webbinding.webview != null) {
            ViewGroup parent = (ViewGroup)  webbinding.webview.getParent();
            if (parent != null) {
                parent.removeView( webbinding.webview);
            }
             webbinding.webview.removeAllViews();
             webbinding.webview.loadUrl("about:blank");
             webbinding.webview.stopLoading();
             webbinding.webview.setWebChromeClient(null);
             webbinding.webview.setWebViewClient(null);
             webbinding.webview.destroy();
        }
    }

    /**
     * 打开网页:
     *
     * @param mContext 上下文
     * @param mUrl      要加载的网页url
     * @param mIsMovie  是否是视频链接(视频链接布局不一致)
     */
    public static void loadUrl(Context mContext, String mUrl, boolean mIsMovie) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("mUrl", mUrl);
        intent.putExtra("mIsMovie", mIsMovie);
        mContext.startActivity(intent);
    }
}
