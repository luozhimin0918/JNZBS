package com.jyh.gxcjzbs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jyh.gxcjzbs.common.constant.SpConstant;
import com.jyh.gxcjzbs.common.constant.UrlConstant;
import com.jyh.gxcjzbs.common.utils.SPUtils;
import com.jyh.gxcjzbs.common.utils.SystemUtils;
import com.jyh.gxcjzbs.view.MyWebView;

/*
 * 策略
 */
public class CLActivity extends Activity {
    private String uri;
    private MyWebView webView;
    private View tv;

    private boolean isCl = false;//是否为策略

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.BrowserThemeDefault);
        setContentView(R.layout.fragment_kefu);
        String cl_url = SPUtils.getString(this, SpConstant.APPINFO_CL_URL);
        String cl_ban = SPUtils.getString(this,SpConstant.APPINFO_CL_BAN);
        String rid = SPUtils.getString(this,SpConstant.USERINFO_LOGIN_RID);
        uri = UrlConstant.URL_CELUE;
        Log.i("info", "bans=" + cl_ban + " uri=" + cl_url);

//        String token=userinfo.getString("token",null);
//        if(token!=null){
            if (cl_ban.contains(",")) {
                String[] bans = cl_ban.split(",");
                for (String ban : bans) {
                    if (!rid.equals(ban)) {
                        Log.i("info", "rid=" + rid + " banid=" + ban);
                        uri = cl_url;
                        isCl = true;
                        break;
                    } else {
                        isCl = false;
                    }
                }
            } else {
                if (!rid.equals(cl_ban)) {
                    isCl = true;
                    uri = cl_url;
                } else {
                    isCl = false;
                }
            }
//        }else{
//            isCl=false;
//        }

        setDialogStyle(isCl);
        getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL,
                LayoutParams.FLAG_NOT_TOUCH_MODAL); // ...but notify us that it
        // happened.
        getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        // Note that flag changes
        // must happen *before* the
        // content view is set.
        tv = (View) findViewById(R.id.tvId);
        tv.setVisibility(View.GONE);
        webView = (MyWebView) findViewById(R.id.webView1);
        webView.setVisibility(View.VISIBLE);
        // if (packageInfo == null) {
        // webView.setClickable(false);
        // } else
        // webView.setClickable(true);
//        findViewById(R.id.dialog_view).setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                // Log.i("kefu", "shouldOverrideUrlLoading" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(v, url);
                if (url.contains("mqqwpa")) {
                    v.setVisibility(View.GONE);
                } else
                    v.setVisibility(View.VISIBLE);
                // view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView v, int errorCode, String description, String failingUrl) {
                v.setVisibility(View.GONE);
                v.loadUrl(uri);

                // webView.setVisibility(View.GONE);
                // tv.setVisibility(View.VISIBLE);
                // view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
            }

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return super.shouldInterceptRequest(view, url);
                } else {
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(in);
                    finish();
                    return null;
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);// 是否支持JavaScript
        webView.getSettings().setBuiltInZoomControls(true);//
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.addJavascriptInterface(new isQQ(), "QQ");
        webView.loadUrl(uri);
    }

    @SuppressWarnings("deprecation")
    private void setDialogStyle(boolean isCl) {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        float size = SystemUtils.getDpi(this);
        LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        if (isCl) {
            p.height = (int) ((d.getHeight() - (float) 40 * size - SystemUtils
                    .getStatuBarHeight(this)) / 2.75 * 1.75); // 高度设置为屏幕的1.0
            if (GotyeLiveActivity.live != null && !GotyeLiveActivity.live.isDestroyed()) {
                GotyeLiveActivity.live.changeTitle();
            }
            if (GenseeActivity.live != null && !GenseeActivity.live.isDestroyed()) {
                GenseeActivity.live.changeTitle();
            }
        } else {
            p.height = (int) (SystemUtils.dip2px(this, 120)); // 高度设置为屏幕的1.0
        }
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.0f; // 设置黑暗度

        getWindow().setAttributes(p); // 设置生效
        getWindow().setGravity(Gravity.BOTTOM); // 设置居中
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            finish();
            try {
                ((GotyeLiveActivity) GotyeLiveActivity.live).createFloatView();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void finish() {
        if (isCl && GotyeLiveActivity.live != null) {
            GotyeLiveActivity.live.changeTitle();
        }
        if (isCl && GenseeActivity.live != null) {
            GenseeActivity.live.changeTitle();
        }
        super.finish();
    }

    class isQQ {
        @JavascriptInterface
        public boolean isAppInstalled() {
            PackageInfo packageInfo;
            try {
                packageInfo = getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
            } catch (Exception e) {
                packageInfo = null;
                e.printStackTrace();
            }
            if (packageInfo == null) {
                Log.i("hehe", "ddddddddddddddddd");
                return false;
            } else {
                Log.i("hehe", "eeeeeeeeeeeeeeeee");
                return true;
            }
        }
    }
}
