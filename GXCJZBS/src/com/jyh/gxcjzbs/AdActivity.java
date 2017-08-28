package com.jyh.gxcjzbs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.jyh.gxcjzbs.common.utils.LoginInfoUtils;
import com.jyh.gxcjzbs.common.utils.imageutils.DownLoaderImage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 广告界面
 *
 * @author beginner
 * @version 1.0
 * @date 创建时间：2015年7月21日 下午4:53:38
 */
public class AdActivity extends FragmentActivity implements OnClickListener {

    private SimpleDraweeView img;
    private String url;
    private Intent intent;
    private Timer timer;
    private String imgpath;

    private boolean isNeedLogin;

    private Handler handler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    timer = new Timer();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            startA_ctivity();
                        }
                    }, 2 * 1000);
                    break;
                case 2:
                    startA_ctivity();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private WebView webView;
    private TextView ad_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ad);
        ad_btn = (TextView) findViewById(R.id.ad_btn);
        ad_btn.setOnClickListener(this);
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String titl) {
                // TODO Auto-generated method stub
                super.onReceivedTitle(view, titl);
                ((TextView) findViewById(R.id.ad_title_tv)).setText(titl);
            }

        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && !url.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivityForResult(intent, 200);
                }
            }
        });

        if (LoginInfoUtils.isLogin(this)) {
            // 登录有效
            isNeedLogin = false;
        } else {
            isNeedLogin = LoginInfoUtils.needRequireLogin(this);
        }

        imgpath = getIntent().getStringExtra("image");
        url = getIntent().getStringExtra("url");
        img = (SimpleDraweeView) findViewById(R.id.img);
        img.setOnClickListener(this);
        findViewById(R.id.ad_img_back).setOnClickListener(this);
        intent = new Intent(AdActivity.this, MainActivity.class);
        Bitmap bitmap = new DownLoaderImage(AdActivity.this).getBitmapCache(imgpath);
//        Bitmap bitmap = new ImageDownLoader(this).getBitmapCache(imgpath);
        Log.i("info", "_bitmap=null?" + (bitmap == null));
        try {
            ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                    handler.sendEmptyMessage(1);
                }

                @Override
                public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    Log.i("hehe", "Fresco error:" + throwable);
                    handler.sendEmptyMessage(2);
                }
            };
            DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener)
                    .setUri(Uri.parse(imgpath))
                    // other setters
                    .build();
            img.setController(controller);
//            share = url;
        } catch (Exception exception) {
            exception.printStackTrace();
            startA_ctivity();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img:
                // 广告跳转
//                Log.i("url", url);
//                if (url != null && !url.equals("")) {
//                    timer.cancel();
//                    timer.purge();
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivityForResult(intent, 200);
//                }
                if (url != null && !url.equals("")) {
                    timer.cancel();
                    timer.purge();
                    ad_btn.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                    findViewById(R.id.webviewId).setVisibility(View.VISIBLE);
                    webView.loadUrl(url);
                }
                break;
            case R.id.ad_img_back:
            case R.id.ad_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        timer.purge();
        startA_ctivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            startA_ctivity();
        }
    }

    private void startA_ctivity() {
        if (isNeedLogin) {
            Intent LoginIntent = new Intent(AdActivity.this, Login_One.class);
            LoginIntent.putExtra("from", "welcome");
            startActivity(LoginIntent);
            finish();
        } else {
            startActivity(intent);
            finish();
        }
    }

}
