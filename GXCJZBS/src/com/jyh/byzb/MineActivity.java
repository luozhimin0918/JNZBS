package com.jyh.byzb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.utils.SystemUtils;
import com.jyh.byzb.common.utils.imageutils.ImageDownLoader;
import com.jyh.byzb.common.utils.imageutils.ImageDownLoader.AsyncImageLoaderListener;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.SPUtils;

public class MineActivity extends Activity {
    private WebView webView;
    private TextView tv;
    private TextView name;
    private ImageView img, logout;
    private ImageView typeImg;
    private TextView typeName;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.BrowserThemeDefault);
        setContentView(R.layout.activity_mine);

        setDialogStyle();

        getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL); // ...but
        // notify
        // us
        // that
        // it
        // happened.
        getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        // Note that flag changes
        // must happen *before* the
        // content view is set.
        name = (TextView) findViewById(R.id.name);
        img = (ImageView) findViewById(R.id.img);
        typeImg = (ImageView) findViewById(R.id.typeImg);
        typeName = (TextView) findViewById(R.id.typeName);
        logout = (ImageView) findViewById(R.id.logoutId);
        logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 退出登录
                LoginInfoUtils.logout(MineActivity.this);
                if (LoginInfoUtils.needRequireLogin(MineActivity.this)) {
                    // 强制登录
                    Intent LoginIntent = new Intent(MineActivity.this, Login_One.class);
                    startActivity(LoginIntent);
                    MineActivity.this.finish();
                    if (null != MainActivity.main)
                        MainActivity.main.finish();
                    try {
                        if (GotyeLiveActivity.live != null) {
                            GotyeLiveActivity.live.finish();
                        }
                    } finally {
                        try {
                            if (GenseeActivity.live != null) {
                                GenseeActivity.live.finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    MineActivity.this.finish();
                    try {
                        GenseeActivity.live.fragment_chat.faceRelativeLayout.onReflshView();
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            GotyeLiveActivity.live.fragment_chat.faceRelativeLayout.onReflshView();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        name.setText(SPUtils.getString(MineActivity.this, SpConstant.USERINFO_LOGIN_NAME));

        typeName.setText(SPUtils.getString(MineActivity.this, SpConstant.USERINFO_R_NAME));
        String imgString = SPUtils.getString(MineActivity.this, SpConstant.USERINFO_IMAGE);
        Bitmap bm = new ImageDownLoader(this).getBitmapCache(imgString);
        if (bm != null) {
            typeImg.setImageBitmap(bm);
        } else {
            new ImageDownLoader(this).loadImage(imgString, new AsyncImageLoaderListener() {

                @Override
                public void onImageLoader(Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    typeImg.setImageBitmap(bitmap);
                }
            });
        }
    }

    private void setDialogStyle() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        float size = SystemUtils.getDpi(this);
        LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 1.0); // 高度设置为屏幕的1.0
        p.height = (int) ((d.getHeight() - (float) 40 * size - SystemUtils.getStatuBarHeight(this)) / 2.75 * 1.75); // 高度设置为屏幕的1.0
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.0f; // 设置黑暗度

        getWindow().setAttributes(p); // 设置生效
        getWindow().setGravity(Gravity.BOTTOM); // 设置居中
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
        if (GotyeLiveActivity.live != null) {
            GotyeLiveActivity.live.changeTitle();
        }
        if (GenseeActivity.live != null) {
            GenseeActivity.live.changeTitle();
        }
        super.finish();
    }
}
