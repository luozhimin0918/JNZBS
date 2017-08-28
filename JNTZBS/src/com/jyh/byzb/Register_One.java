package com.jyh.byzb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.jyh.byzb.bean.KXTApplication;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.constant.UrlConstant;
import com.jyh.byzb.common.utils.NetworkCenter;
import com.jyh.byzb.common.utils.SystemUtils;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.volleyutil.NormalPostRequest;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.ToastView;
import com.jyh.byzb.sqlte.SCDataSqlte;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register_One extends Activity implements OnClickListener {

    private EditText edit_account, edit_name, edit_pwd, edit_repwd;
    private WebView webView;
    private Button register;
    private LinearLayout back;
    private String account, name, pwd, repwd;

    private KXTApplication application;
    private RequestQueue queue;

    protected SCDataSqlte dataSqlte;

    private String from;

    protected boolean isFromLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        from = getIntent().getStringExtra("from");
        // 透明状态栏
        getWindow().addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_registrt_one);

        application = (KXTApplication) getApplication();
        queue = application.getQueue();

        if (from != null && "live".equals(from)) {
            isFromLive = true;
//            setDialogStyle();
        } else {
            isFromLive = false;
        }

        findview();
        final String url = SPUtils.getString(this, SpConstant.APPINFO_REGISTER_URL);
        if ((url != null && !"".equals(url))) {
            webView.setVisibility(View.VISIBLE);
            findViewById(R.id.local).setVisibility(View.GONE);

            WebSettings webSeting = webView.getSettings();
            webSeting.setJavaScriptEnabled(true);
            webSeting.setLoadsImagesAutomatically(true);
            webSeting.setLoadWithOverviewMode(true);
            webSeting.setUseWideViewPort(true);
            webSeting.setDisplayZoomControls(false);
            webSeting.setDisplayZoomControls(false);
            webSeting.setBuiltInZoomControls(true);
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    if (url.startsWith("http") || url.startsWith("https")) {
                        return super.shouldInterceptRequest(view, url);
                    } else {
                        if (isAppInstalled()) {
                            Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(in);
                        }else{
//                            ToastView.makeText(Register_One.this,"请先安装QQ");
                        }
                        return null;
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    // TODO Auto-generated method stub
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    view.loadUrl(url);
                }

            });
            webView.loadUrl(url);

        } else {
            webView.setVisibility(View.GONE);
            findViewById(R.id.local).setVisibility(View.VISIBLE);
        }

    }

    private void findview() {
        // TODO Auto-generated method stub
        webView = (WebView) findViewById(R.id.web);
        edit_account = (EditText) findViewById(R.id.registerone_account);
        edit_name = (EditText) findViewById(R.id.registerone_name);
        edit_pwd = (EditText) findViewById(R.id.registerone_pwd);
        edit_repwd = (EditText) findViewById(R.id.registerone_repwd);
        back = (LinearLayout) findViewById(R.id.self_fk_img);

//        if (from != null && "live".equals(from)) {
//            findViewById(R.id.title).setVisibility(View.GONE);
//            back.setVisibility(View.GONE);
//        } else {
        findViewById(R.id.title).setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
//        }
        register = (Button) findViewById(R.id.registerone_register);
        register.setOnClickListener(this);

        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.registerone_register:
                if(!NetworkCenter.checkNetworkConnection(this)){
                    ToastView.makeText(this,"当前无网络，请稍后再试");
                    return;
                }
                account = edit_account.getText().toString().trim();
                name = edit_name.getText().toString().trim();
                pwd = edit_pwd.getText().toString().trim();
                repwd = edit_repwd.getText().toString().trim();
                register();
                break;
            case R.id.self_fk_img:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void register() {
        // TODO Auto-generated method stub
        Map<String, String> map = new HashMap<String, String>();
        map.put("account", account);
        map.put("nickname", name);
        map.put("password", pwd);
        map.put("repassword", repwd);
        NormalPostRequest normalPostRequest = new NormalPostRequest(UrlConstant.URL_REGISTER, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                String code;
                try {
                    code = arg0.getString("code");
                    if ("200".equals(code)) {
                        // 登录成功
                        JSONObject data = arg0.getJSONObject("data");
                        Log.i("hehe", arg0.toString());
                        // "token": "457cede2bc6aa7a3683af6ffd4cb5a19",
                        // "member_id": "1",
                        // "expired_time": 1462350254,
                        // "user_info": {
                        // "id": "1",
                        // "name": "青之羽",
                        // "rid": "17"
                        LoginInfoUtils.login(Register_One.this, data);

                        if (!isFromLive) {
                            Intent intent = new Intent(Register_One.this, MainActivity.class);
                            if ("self".equals(from))
                                intent.putExtra("enter", "self");
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        // 注册失败,
                        ToastView.makeText(application, "注册失败," + arg0.getString("msg"));
                    }
                } catch (JSONException e) {
                    // 注册失败
                    ToastView.makeText(application, "注册失败," + e.toString());
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                ToastView.makeText(application, "注册失败," + arg0);
            }
        }, map);

        queue.add(normalPostRequest);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

    private void setDialogStyle() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        float size = SystemUtils.getDpi(this);
        LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.0f; // 设置黑暗度
        p.height = (int) ((d.getHeight() - (float) 40 * size - SystemUtils.getStatuBarHeight(this)) / 2.75 * 1.75);
        getWindow().setAttributes(p); // 设置生效
        getWindow().setGravity(Gravity.BOTTOM); // 设置在底部
        // ...but notify us that it happened.
        getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // Note that flag changes must happen *before* the content view is set.
        getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        hintKbTwo();
       /* if (GotyeLiveActivity.live != null) {
           GotyeLiveActivity.live.changeTitle();
        }
        if (GenseeActivity.live != null) {
            GenseeActivity.live.changeTitle();
        }*/
        super.finish();
    }


    //此方法只是关闭软键盘
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public boolean isAppInstalled() {
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            // System.out.println("没有安装");
            return false;
        } else {
            // System.out.println("已经安装");
            return true;
        }
    }
}
