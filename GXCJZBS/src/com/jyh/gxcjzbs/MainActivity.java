package com.jyh.gxcjzbs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jyh.gxcjzbs.basePack.BaseActivity;
import com.jyh.gxcjzbs.bean.KXTApplication;
import com.jyh.gxcjzbs.common.constant.SpConstant;
import com.jyh.gxcjzbs.common.utils.SPUtils;
import com.jyh.gxcjzbs.common.utils.ToastView;
import com.jyh.gxcjzbs.fragment.fragment_self;
import com.jyh.gxcjzbs.fragment.fragment_self.OnFragmentListener;
import com.jyh.gxcjzbs.fragment.fragment_web;
import com.jyh.gxcjzbs.fragment.fragment_zb;
import com.jyh.gxcjzbs.service.ImageService;
import com.jyh.gxcjzbs.common.utils.NetworkCenter;

import java.util.Timer;
import java.util.TimerTask;

import static com.jyh.gxcjzbs.common.constant.SpConstant.APPINFO_ALTERS_URL;
import static com.jyh.gxcjzbs.common.constant.SpConstant.APPINFO_CJRL_URL;
import static com.jyh.gxcjzbs.common.constant.SpConstant.APPINFO_HQ_URL;

public class MainActivity extends BaseActivity implements OnClickListener, OnFragmentListener {
    private fragment_web fragment_flash, fragment_kxthq, fragment_data;
    private fragment_zb fragment_zb;
    private fragment_self fragment_self;
    private ImageView imgdata, imgflash, imghq, imgself, imgjw;
    private KXTApplication application;
    private LinearLayout main_ll_flash, main_ll_yw, main_ll_hq, main_ll_rl, main_ll_self, main_zt_color;
    private long mExitTime;
    private FragmentManager fragmentManager;
    private int cuntenpage = 0;
    public static Activity main;

    Handler handler;
    private FragmentTransaction transaction;
    private Timer Checktimer;
    private boolean isCheck = true;
    private boolean mBound;

    Handler mianHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 10:
                    if (isCheck) {
                        isCheck = false;
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };
    private boolean maincolor = true;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setTheme(R.style.BrowserThemeDefault);
        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        main = this;

        cuntenpage = getIntent().getIntExtra("viewpager", 0);
        InitFind();
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        String enter = intent.getStringExtra("enter");
        String type = intent.getStringExtra("type");
        if (null != data && !data.equals("")) {
            Notifacation();
        } else if (null != enter && !enter.equals("")) {
            setTabSelection(4);
        } else if (null != type && type.contains("new")) {
            setTabSelection(1);
        } else if (null != type && type.contains("dian")) {
            setTabSelection(0);
            handler.sendEmptyMessage(2);
        } else if (null != type && type.contains("video")) {
            setTabSelection(0);
            handler.sendEmptyMessage(3);
        } else if (null != type && type.contains("flash")) {
            setTabSelection(0);
            handler.sendEmptyMessage(1);
        } else if (cuntenpage != 0) {
            setTabSelection(cuntenpage);
        } else
            setTabSelection(0);
        Checktimer = new Timer();

        Checktimer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (NetworkCenter.checkNetworkConnection(MainActivity.this)) {
                    isCheck = true;
                } else {
                    mianHandler.sendEmptyMessage(10);
                }
            }
        }, 0, 5 * 1000);

        if (intent.getBooleanExtra("isLoadImg", true)) {
            startService(new Intent(this, ImageService.class));
        }
    }

    @SuppressLint("NewApi")
    private void setTabSelection(int index) {

        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        try {
            switch (index) {
                case 0:
                    // 当点击了消息tab时，改变控件的图片和文字颜色
                    imgflash.setSelected(true);
                    cuntenpage = 0;
                    if (fragment_zb == null) {
                        // 如果MessageFragment为空，则创建一个并添加到界面上
                        fragment_zb = new fragment_zb();
                        transaction.add(R.id.frame_content, fragment_zb);
                    } else {
                        // 如果MessageFragment不为空，则直接将它显示出来
                        transaction.show(fragment_zb);
                    }
                    main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
//                    main_zt_color.setBackgroundResource(R.drawable.live_bg);
                    maincolor = true;
                    break;
                case 1:
                    // 当点击了消息tab时，改变控件的图片和文字颜色
                    imgjw.setSelected(true);
                    cuntenpage = 1;
                    if (fragment_flash == null) {
                        // 如果MessageFragment为空，则创建一个并添加到界面上
                        fragment_flash = new fragment_web();
                        Bundle bundle=new Bundle();
                        bundle.putString("url",SPUtils.getString(this,APPINFO_ALTERS_URL));
                        fragment_flash.setArguments(bundle);
                        transaction.add(R.id.frame_content, fragment_flash);
                    } else {
                        // 如果MessageFragment不为空，则直接将它显示出来
                        if (fragment_flash.error && KXTApplication.isHaveNet) {
                            fragment_flash = new fragment_web();
                            Bundle bundle=new Bundle();
                            bundle.putString("url",SPUtils.getString(this,APPINFO_ALTERS_URL));
                            fragment_flash.setArguments(bundle);
                            transaction.add(R.id.frame_content, fragment_flash);
                        } else
                            transaction.show(fragment_flash);
                    }
                    if (maincolor) {
                        main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
                        maincolor = false;
                    }
                    break;
                case 2:
                    // 当点击了动态tab时，改变控件的图片和文字颜色
                    imghq.setSelected(true);
                    cuntenpage = 2;
                    if (fragment_kxthq == null) {
                        fragment_kxthq = new fragment_web();
                        Bundle bundle=new Bundle();
                        bundle.putString("url",SPUtils.getString(this,APPINFO_HQ_URL));
                        fragment_kxthq.setArguments(bundle);
                        transaction.add(R.id.frame_content, fragment_kxthq);
                    } else {
                        if (fragment_kxthq.error && KXTApplication.isHaveNet) {
                            fragment_kxthq = new fragment_web();
                            Bundle bundle=new Bundle();
                            bundle.putString("url",SPUtils.getString(this,APPINFO_HQ_URL));
                            fragment_kxthq.setArguments(bundle);
                            transaction.add(R.id.frame_content, fragment_kxthq);
                        } else
                            transaction.show(fragment_kxthq);
                    }
                    if (maincolor) {
                        main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
                        maincolor = false;
                    }

                    SPUtils.save(MainActivity.this, SpConstant.GLOBAL_ISCHANGE, true);

                    break;
                case 3:
                    // 当点击了设置tab时，改变控件的图片和文字颜色
                    application.getMmp().put("4", "4");
                    imgdata.setSelected(true);
                    cuntenpage = 3;
                    if (fragment_data == null) {
                        // 如果SettingFragment为空，则创建一个并添加到界面上
                        fragment_data = new fragment_web();
                        Bundle bundle=new Bundle();
                        bundle.putString("url",SPUtils.getString(this,APPINFO_CJRL_URL));
                        fragment_data.setArguments(bundle);
                        transaction.add(R.id.frame_content, fragment_data);
                    } else {
                        Log.i("hehe", fragment_data.error + " " + KXTApplication.isHaveNet);
                        // 如果SettingFragment不为空，则直接将它显示出来
                        if (fragment_data.error && KXTApplication.isHaveNet) {
                            fragment_data = new fragment_web();
                            Bundle bundle=new Bundle();
                            bundle.putString("url",SPUtils.getString(this,APPINFO_CJRL_URL));
                            fragment_data.setArguments(bundle);
                            transaction.add(R.id.frame_content, fragment_data);
                        } else
                            transaction.show(fragment_data);
                    }
                    if (maincolor) {
                        main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
                        maincolor = false;
                    }
                    break;
                case 4:
                    imgself.setSelected(true);
                    cuntenpage = 4;
                    if (fragment_self == null) {
                        // 如果SettingFragment为空，则创建一个并添加到界面上
                        fragment_self = new fragment_self();
                        transaction.add(R.id.frame_content, fragment_self);
                    } else {
                        // 如果SettingFragment不为空，则直接将它显示出来
                        transaction.show(fragment_self);
                    }
                    if (maincolor) {
                        main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
                        maincolor = false;
                    }
                    break;
            }
            // transaction.commitAllowingStateLoss();
            transaction.commit();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void resetBtn() {
        imgdata.setSelected(false);
        imgflash.setSelected(false);
        imghq.setSelected(false);
        imgself.setSelected(false);
        imgjw.setSelected(false);
    }

    @SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction) {
        if (fragment_zb != null) {
            transaction.hide(fragment_zb);
        }
        if (fragment_kxthq != null) {
            transaction.hide(fragment_kxthq);
        }
        if (fragment_flash != null) {
            transaction.hide(fragment_flash);
        }
        if (fragment_self != null) {
            transaction.hide(fragment_self);
        }
        if (fragment_data != null) {
            transaction.hide(fragment_data);
        }
    }

    private void Notifacation() {
        // TODO Auto-generated method stub
        imgdata.setSelected(true);
        fragment_data = new fragment_web();
        Bundle bundle=new Bundle();
        bundle.putString("url", SPUtils.getString(this,APPINFO_CJRL_URL));
        fragment_data.setArguments(bundle);
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.replace(R.id.frame_content, fragment_data);
        // 事务管理提交
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("join", false) && fragment_zb != null) {
            getIntent().putExtra("join", false);
            fragment_zb.reJoin();
        }
    }

    private void clickAtBtn() {
        main_ll_flash.setOnClickListener(this);
        main_ll_hq.setOnClickListener(this);
        main_ll_rl.setOnClickListener(this);
        main_ll_self.setOnClickListener(this);
        main_ll_yw.setOnClickListener(this);
    }

    private void InitFind() {
        main_zt_color = (LinearLayout) findViewById(R.id.main_zt_color);
        main_ll_flash = (LinearLayout) findViewById(R.id.mian_ll_flash);
        main_ll_hq = (LinearLayout) findViewById(R.id.mian_ll_hq);
        main_ll_rl = (LinearLayout) findViewById(R.id.mian_ll_rl);
        main_ll_self = (LinearLayout) findViewById(R.id.mian_ll_self);
        main_ll_yw = (LinearLayout) findViewById(R.id.mian_ll_yw);
        imgdata = (ImageView) findViewById(R.id.imgdata);
        imgflash = (ImageView) findViewById(R.id.imgflash);
        imghq = (ImageView) findViewById(R.id.imghq);
        imgself = (ImageView) findViewById(R.id.imgself);
        imgjw = (ImageView) findViewById(R.id.imgjw);
        main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
        application = (KXTApplication) getApplication();
        application.addAct(this);
        clickAtBtn();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mian_ll_flash:
                setTabSelection(0);
                break;
            case R.id.mian_ll_yw:
                setTabSelection(1);
                break;
            case R.id.mian_ll_hq:
                setTabSelection(2);
                break;
            case R.id.mian_ll_rl:
                setTabSelection(3);
                break;
            case R.id.mian_ll_self:
                setTabSelection(4);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastView.makeText(this, "再按一次退出");
                mExitTime = System.currentTimeMillis();
            } else {
                application.exitAppAll();

            }
            return true;
        }
        // 拦截MENU按钮点击事件，让他无任何操作
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFragmentAction() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        main = null;
        super.onDestroy();
        application.ischange = true;
    }
}
