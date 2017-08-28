package com.jyh.byzb;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.entity.UserInfo;
import com.gensee.net.AbsRtAction;
import com.gensee.player.OnPlayListener;
import com.gensee.player.Player;
import com.gensee.view.GSVideoView;
import com.jyh.byzb.adapter.PrivateNewMsgAdapter;
import com.jyh.byzb.bean.KXTApplication;
import com.jyh.byzb.bean.UserBean;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.my_interface.LiveFunctionBtnChange;
import com.jyh.byzb.common.my_interface.LiveFunctionBtnQuitChange;
import com.jyh.byzb.common.my_interface.ServiceQuitChange;
import com.jyh.byzb.common.utils.ItemAnimator;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.PrivateChatUtils;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.SystemUtils;
import com.jyh.byzb.fragment.Fragment_chat;
import com.jyh.byzb.fragment.Fragment_function;
import com.jyh.byzb.fragment.Fragment_kefu;
import com.jyh.byzb.service.ChatService;
import com.jyh.byzb.view.FaceRelativeLayout;
import com.jyh.byzb.view.MyViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

/**
 * @author beginner
 * @version 1.0
 * @date 创建时间：2015年7月21日 下午4:53:38
 */
public class GenseeActivity extends FragmentActivity implements OnClickListener, OnPlayListener, LiveFunctionBtnChange,
        LiveFunctionBtnQuitChange, ServiceQuitChange {

    private TextView chatBtn, serviceBtn;
    private ImageView playBtn;
    public static TextView functionBtn;
    private ImageView backBtn, fullBtn;
    private View chatbg, servicebg, functionbg;
    public LinearLayout menu;
    private FrameLayout layout;
    private MyViewPager2 viewPager;
    private SharedPreferences preferences, appinfo, userinfo;
    private InputMethodManager imm;
    private LinearLayout.LayoutParams params;
    private Display display;
    private boolean isShow;// 判断当前全屏按钮是否显示

    private boolean isDialogShow = true;
    private boolean isDialogTextShow = false;
    private String dialogText = "";

    private boolean isUp;//是否在前台

    // 视频下方的小界面
    public Fragment_chat fragment_chat;
    private Fragment_function fragment_function;
    private Fragment_kefu fragment_kf;
    private List<Fragment> fragments;
    private DemoAdapter adapter;

    private int screenWidth, screenHeight;// 屏幕宽高

    private Timer timer = new Timer();
    public static GenseeActivity live;
    private boolean isChange = false;// 用来判断功能界面是否刷新(登录/我的)

    private int num;
    private CallPhoneBroadcaseReceiver receiver;
    private HideOrShowTitle hideOrShowTitle;
    private boolean isFull;// 是否为全屏

    //悬浮图标
    public LinearLayout mFloatLayout;
    public ImageView chat_dl;
    public ImageView chat_cl;
    public ImageView chat_chat;
    /**
     * 屏幕的宽度和高度
     */
    protected int mScreenWidth;
    protected int mScreenHeight;
    private boolean isAdded = false;

    private Player player;
    private boolean play = true;
    private View privateBtn;
    private View privatePoint;
    private PopupWindow newMsgPop;
    private RecyclerView rvPrivate;
    private PrivateNewMsgAdapter privateNewMsgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LiveActivity", "create");
        live = this;
        preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
        setTheme(R.style.BrowserThemeDefault);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        // 禁止黑屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_genseelive);
        //获取屏幕宽高,用于计算悬浮按钮位置
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        screenOffOrOn();

        appinfo = getSharedPreferences("appinfo", Context.MODE_PRIVATE);
        userinfo = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

        findView();

        player = new Player();
        player.setGSVideoView(surfaceViewContainer);
        player.join(this, KXTApplication.initParam, this);
        GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置横屏
            fullBtn.setSelected(false);
            isFull = true;
        } else
            isFull = false;

        setTimerTask();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        receiver = new CallPhoneBroadcaseReceiver();
        hideOrShowTitle = new HideOrShowTitle();
        registerReceiver(hideOrShowTitle, new IntentFilter("hideorshow"));
        registerReceiver(receiver, filter);
        bindService();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        Log.i("genseeam", "" + (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED));

    }

    // Gotye
    private GSVideoView surfaceViewContainer;
    private boolean isGotyeChange;

    @Override
    public void onServiceBtnChange(boolean isChange) {
        if (isChange) {
            serviceBtn.setText("客服");
        } else {
            serviceBtn.setText("返回");
        }
    }

    public void showPrivateBtn(UserBean userBean) {
        try {
            handler.removeMessages(7);
            handler.removeMessages(8);
            if (userBean != null) {
                privateBtn.setVisibility(View.VISIBLE);
                Message message = new Message();
                message.what = 7;
                message.obj = userBean;
                handler.sendMessage(message);
                handler.sendEmptyMessageDelayed(8, 3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示新私信提示框
     *
     * @param userBean
     */
    private UserBean userBean;

    private void showNewMsgView(UserBean bean) {
        this.userBean = bean;
        if (newMsgPop == null) {
            View rootView = LayoutInflater.from(this).inflate(R.layout.pop_private_new, null, false);

            rvPrivate = (RecyclerView) rootView.findViewById(R.id.tv_private);

            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvPrivate.setLayoutManager(manager);
            ItemAnimator animator = new ItemAnimator();
            rvPrivate.setItemAnimator(animator);

            privateNewMsgAdapter = new PrivateNewMsgAdapter(this, new ArrayList());

            ImageView closeView = (ImageView) rootView.findViewById(R.id.iv_close);
            closeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    newMsgPop.dismiss();
                }
            });
            rvPrivate.setHasFixedSize(true);
            rvPrivate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    newMsgPop.dismiss();
                    PrivateChatUtils.savePrivateChatNewPoint(GenseeActivity.this, userBean.getUid(), PrivateChatUtils.POINT_HINT);
                    Intent intent = new Intent(GenseeActivity.this, PrivateChatActivity.class);
                    intent.putExtra(PrivateChatActivity.INTENT_TYPE, PrivateChatActivity.TYPE_CHAT);
                    intent.putExtra(PrivateChatActivity.INTENT_TNAME, userBean.getName());
                    intent.putExtra(PrivateChatActivity.INTENT_TUID, userBean.getUid());
                    intent.putExtra(PrivateChatActivity.INTENT_TRID, userBean.getRid());
                    startActivity(intent);
                    return false;
                }
            });
            newMsgPop = new PopupWindow(rootView, SystemUtils.dip2px(this, 205), SystemUtils.dip2px(this, 40));
            newMsgPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    rvPrivate.setAdapter(null);
                }
            });
            newMsgPop.setFocusable(true);
            newMsgPop.setOutsideTouchable(false);
            newMsgPop.setBackgroundDrawable(new BitmapDrawable());
            newMsgPop.setTouchable(true);
        }

        if (newMsgPop.isShowing()) {
            rvPrivate.setAdapter(privateNewMsgAdapter = new PrivateNewMsgAdapter(this, new ArrayList()));
            privateNewMsgAdapter.remove();
            privateNewMsgAdapter.add();
            return;
        } else {
            WindowManager m = getWindowManager();
            Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
            float size = SystemUtils.getDpi(this);
            newMsgPop.showAtLocation(privateBtn, Gravity.TOP | Gravity.RIGHT, -SystemUtils.dip2px(this, 10), (int) (d.getHeight() - (d
                    .getHeight() - (float) 40 * size -
                    SystemUtils
                            .getStatuBarHeight(this)) / 2.75 * 1.75) + SystemUtils.dip2px(this, 10));

            rvPrivate.setAdapter(privateNewMsgAdapter = new PrivateNewMsgAdapter(this, new ArrayList()));

            privateNewMsgAdapter.add();

        }
        PrivateChatUtils.savePrivateChatNewPoint(this, bean.getUid(), PrivateChatUtils.POINT_SHOW);
        privatePoint.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏新私信提示框
     */
    private void hideNewMsgView() {
        if (privateNewMsgAdapter != null) {
            privateNewMsgAdapter.remove();
        }

        if (newMsgPop != null && newMsgPop.isShowing())
            newMsgPop.dismiss();
    }


    public enum Orientation {
        Horizontal, Vertical
    }

    private Orientation orientation;
    private String playState;
    private View loadingView;
    public TextView loadingText;
    public static boolean isPlay;

    private void findView() {

        menu = (LinearLayout) findViewById(R.id.menuId);
        surfaceViewContainer = (GSVideoView) findViewById(R.id.layout);
        loadingText = (TextView) findViewById(R.id.textview);
        loadingView = findViewById(R.id.loading);
        loadingView.setVisibility(View.VISIBLE);
        isDialogShow = true;


        viewPager = (MyViewPager2) findViewById(R.id.live_fragment);
        chatBtn = (TextView) findViewById(R.id.chatId);
        serviceBtn = (TextView) findViewById(R.id.serviceId);
        functionBtn = (TextView) findViewById(R.id.functionId);
        backBtn = (ImageView) findViewById(R.id.livebackId);

        playBtn = (ImageView) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);

        chatBtn.setOnClickListener(this);
        serviceBtn.setOnClickListener(this);
        functionBtn.setOnClickListener(this);

        fullBtn = (ImageView) findViewById(R.id.liveFullId);
        chatbg = findViewById(R.id.chatbgId);
        servicebg = findViewById(R.id.servicebgId);
        layout = (FrameLayout) findViewById(R.id.showLayout);
        functionbg = findViewById(R.id.functionbgId);
        params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        display = getWindowManager().getDefaultDisplay();
        chatbg.setSelected(true);

        surfaceViewContainer.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        fullBtn.setOnClickListener(this);

        mFloatLayout = (LinearLayout) findViewById(R.id.floatBtn);
        chat_chat = (ImageView) findViewById(R.id.chat_chat);
        chat_cl = (ImageView) findViewById(R.id.chat_cl);
        chat_dl = (ImageView) findViewById(R.id.chat_dl);

        privateBtn = findViewById(R.id.private_btn);
        privatePoint = findViewById(R.id.private_btn_point);

        if (PrivateChatUtils.isShowPrivateMsgBtn(this)) {
            privateBtn.setVisibility(View.VISIBLE);
            if (PrivateChatUtils.isShowPoint(this)) {
                privatePoint.setVisibility(View.VISIBLE);
            } else {
                privatePoint.setVisibility(View.GONE);
            }
        } else {
            privateBtn.setVisibility(View.GONE);
            privatePoint.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(SPUtils.getString(this, SpConstant.APPINFO_CL_URL))) {
            chat_cl.setVisibility(View.GONE);
        } else {
            chat_cl.setVisibility(View.VISIBLE);
        }

        chat_dl.setOnClickListener(this);
        chat_cl.setOnClickListener(this);
        chat_chat.setOnClickListener(this);
        privateBtn.setOnClickListener(this);

        initViewpager();
    }

    private void initViewpager() {
        viewPager.setOffscreenPageLimit(3);
        fragment_chat = new Fragment_chat();
        fragment_kf = new Fragment_kefu();
        Bundle bundle = new Bundle();
        bundle.putString("url", appinfo.getString("kefu_url", SPUtils.getString(getApplicationContext(), SpConstant.APPINFO_KEFU_URL)));
        fragment_kf.setArguments(bundle);
        fragment_function = new Fragment_function();
        fragments = new ArrayList<>();
        fragments.add(0, fragment_chat);
        fragments.add(1, fragment_kf);
        fragments.add(2, fragment_function);
        adapter = new DemoAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                selecteTag(arg0);
                boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    try {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                    }
                }
                num = arg0;
                try {
                    if (arg0 == 0) {
                        createFloatView();
                    } else {
                        fragment_chat.rl_bottom.setVisibility(View.GONE);
                        removeFloatView();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    try {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                    }
                }
            }
        });
        createFloatView();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.chatId:
                selecteTag(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.serviceId:
                selecteTag(1);
                viewPager.setCurrentItem(1);
                removeFloatView();
                fragment_kf.reload();
                break;
            case R.id.functionId:
                if (isChatView) {
                    isChatView = false;
                } else {
                    selecteTag(2);
                    viewPager.setCurrentItem(2);
                }
                break;
            case R.id.livebackId:
                onBackPressed();
                break;
            case R.id.layout:
                timer.cancel();
                timer.purge();
                timer = new Timer();
                handler.sendEmptyMessage(2);
                setTimerTask();
                break;
            case R.id.liveFullId:
                if (!isFull) {
                    GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置横屏
                    fullBtn.setSelected(false);
                } else {
                    GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
                    fullBtn.setSelected(true);
                }

                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                }, 5 * 1000);
                break;
            case R.id.chat_chat:
                if (fragment_chat != null && fragment_chat.rl_bottom != null) {
                    GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
                    fragment_chat.rl_bottom.setVisibility(View.VISIBLE);
                    // 获取编辑框焦点
                    fragment_chat.mEditTextContent.setFocusable(true);
                    fragment_chat.mEditTextContent.requestFocus();
                    //打开软键盘
                    InputMethodManager imm = (InputMethodManager) GenseeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    removeFloatView();
                }
                break;
            case R.id.chat_cl:
                removeFloatView();
                Intent intent = new Intent(GenseeActivity.this, CLActivity.class);
                intent.putExtra("from", "chat");
                startActivity(intent);
                break;
            case R.id.chat_dl:
//                functionBtn.setText("返回");
                Intent intent2 = new Intent(GenseeActivity.this, Login_One.class);
                intent2.putExtra("from", "live");
                startActivity(intent2);
                break;
            case R.id.playBtn:
                //暂停开始播放
                if (play) {
                    playBtn.setSelected(true);
                    player.leave();
                    play = false;
                } else {
                    playBtn.setSelected(false);
                    player.join(this, KXTApplication.initParam, this);
                    play = true;
                }
                break;
            case R.id.private_btn:
                Intent privateIntent = new Intent(this, PrivateChatActivity.class);
                privateIntent.putExtra(PrivateChatActivity.INTENT_TYPE, PrivateChatActivity.TYPE_LIST);
                startActivity(privateIntent);
                break;
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        if (isFull) {
            GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
            fullBtn.setSelected(true);
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }, 5 * 1000);
        } else {
            finish();
        }
    }

    @Override
    public void finish() {
        Log.i("LiveActivity", "finish");
        if (MainActivity.main != null && !MainActivity.main.isDestroyed()) {
            super.finish();
        } else {
            Intent intent = new Intent(GenseeActivity.this, MainActivity.class);
            intent.putExtra("isLoadImg", false);
            startActivity(intent);
        }
        super.finish();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_genseelive);
        if (isFull) {
            findView3();
            try {
                createFloatView();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fullBtn.setSelected(false);
        } else {
            try {
                fragment_chat.rl_bottom.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            findView2();
            try {
                removeFloatView();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fullBtn.setSelected(true);
        }
        isFull = !isFull;
        Log.i("info", "....1");
        timer.cancel();
        timer.purge();
        timer = new Timer();
        handler.sendEmptyMessage(2);
        setTimerTask();
    }

    private void findView3() {
        // TODO Auto-generated method stub
        findViewById(R.id.rootView).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        menu = (LinearLayout) findViewById(R.id.menuId);
        surfaceViewContainer = (GSVideoView) findViewById(R.id.layout);
        loadingText = (TextView) findViewById(R.id.textview);
        loadingView = findViewById(R.id.loading);
        loadingText.setText(dialogText);
        if (isDialogShow) {
            loadingView.setVisibility(View.VISIBLE);
        } else {
            loadingView.setVisibility(View.GONE);
        }

        if (isDialogTextShow) {
            loadingText.setVisibility(View.VISIBLE);
        } else {
            loadingText.setVisibility(View.GONE);
        }
        player.setGSVideoView(surfaceViewContainer);

        viewPager = (MyViewPager2) findViewById(R.id.live_fragment);
        chatBtn = (TextView) findViewById(R.id.chatId);
        serviceBtn = (TextView) findViewById(R.id.serviceId);
        functionBtn = (TextView) findViewById(R.id.functionId);

        playBtn = (ImageView) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);

        if (play) {
            playBtn.setSelected(false);
        } else {
            playBtn.setSelected(true);
        }

        chatBtn.setOnClickListener(this);
        serviceBtn.setOnClickListener(this);
        functionBtn.setOnClickListener(this);

        backBtn = (ImageView) findViewById(R.id.livebackId);
        fullBtn = (ImageView) findViewById(R.id.liveFullId);
        chatbg = findViewById(R.id.chatbgId);
        servicebg = findViewById(R.id.servicebgId);
        layout = (FrameLayout) findViewById(R.id.showLayout);
        functionbg = findViewById(R.id.functionbgId);
        params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        display = getWindowManager().getDefaultDisplay();
        chatbg.setSelected(true);

        surfaceViewContainer.setOnClickListener(this);
        chatbg.setOnClickListener(this);
        servicebg.setOnClickListener(this);
        functionbg.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        fullBtn.setOnClickListener(this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                selecteTag(arg0);
                boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    try {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                    }
                }
                if (arg0 == 0) {
                    createFloatView();
                } else {
                    removeFloatView();
                }
                num = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    try {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                    }
                }
            }
        });

        mFloatLayout = (LinearLayout) findViewById(R.id.floatBtn);
        chat_chat = (ImageView) findViewById(R.id.chat_chat);
        chat_cl = (ImageView) findViewById(R.id.chat_cl);
        chat_dl = (ImageView) findViewById(R.id.chat_dl);
        privateBtn = findViewById(R.id.private_btn);
        privatePoint = findViewById(R.id.private_btn_point);

        if (PrivateChatUtils.isShowPrivateMsgBtn(this)) {
            privateBtn.setVisibility(View.VISIBLE);
            if (PrivateChatUtils.isShowPoint(this)) {
                privatePoint.setVisibility(View.VISIBLE);
            } else {
                privatePoint.setVisibility(View.GONE);
            }
        } else {
            privateBtn.setVisibility(View.GONE);
            privatePoint.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(SPUtils.getString(this, SpConstant.APPINFO_CL_URL))) {
            chat_cl.setVisibility(View.GONE);
        } else {
            chat_cl.setVisibility(View.VISIBLE);
        }


        chat_dl.setOnClickListener(this);
        chat_cl.setOnClickListener(this);
        privateBtn.setOnClickListener(this);
        chat_chat.setOnClickListener(this);
    }

    private void findView2() {
        // TODO Auto-generated method stub
        findViewById(R.id.rootView).setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        surfaceViewContainer = (GSVideoView) findViewById(R.id.layout);
        player.setGSVideoView(surfaceViewContainer);
        loadingText = (TextView) findViewById(R.id.textview);
        loadingView = findViewById(R.id.loading);
        if (isDialogShow)
            loadingView.setVisibility(View.VISIBLE);
        else
            loadingView.setVisibility(View.GONE);

        loadingText.setText(dialogText);
        if (isDialogTextShow)
            loadingText.setVisibility(View.VISIBLE);
        else
            loadingText.setVisibility(View.GONE);
        playBtn = (ImageView) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);
        if (play) {
            playBtn.setSelected(false);
        } else {
            playBtn.setSelected(true);
        }

        backBtn = (ImageView) findViewById(R.id.livebackId);
        fullBtn = (ImageView) findViewById(R.id.liveFullId);
        try {
            chatbg.setOnClickListener(this);
            servicebg.setOnClickListener(this);
            functionbg.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        display = getWindowManager().getDefaultDisplay();

        surfaceViewContainer.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        fullBtn.setOnClickListener(this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                selecteTag(arg0);
                boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    try {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                    }
                }
                if (arg0 == 0) {
                    createFloatView();
                } else {
                    removeFloatView();
                }
                num = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    try {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                    }
                }
            }
        });

    }

    /**
     * 选择tag,tag背景变化
     *
     * @param arg0 tag下标
     */
    private void selecteTag(int arg0) {
        switch (arg0) {
            case 0:
                createFloatView();
                chatbg.setSelected(true);
                servicebg.setSelected(false);
                functionbg.setSelected(false);
                break;
            case 1:
                removeFloatView();
                chatbg.setSelected(false);
                servicebg.setSelected(true);
                functionbg.setSelected(false);
                break;

            case 2:
                removeFloatView();
                chatbg.setSelected(false);
                servicebg.setSelected(false);
                functionbg.setSelected(true);
                break;

            default:
                break;
        }
    }

    class DemoAdapter extends FragmentPagerAdapter {
        public DemoAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            if (object instanceof Fragment_function)
                return POSITION_NONE;
            return POSITION_UNCHANGED;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragment_chat;
                case 1:
                    return fragment_kf;
                case 2:
                    return fragment_function;
            }
            return null;
        }

        @Override
        public int getCount() {
            // 多少页
            return fragments.size();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (isFull) {
                        findViewById(R.id.rootView).setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    }
                    playBtn.setVisibility(View.GONE);
                    backBtn.setVisibility(View.GONE);
                    fullBtn.setVisibility(View.GONE);
                    break;
                case 2:
                    if (isFull) {
                        findViewById(R.id.rootView).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                    if (isPlay) {
                        playBtn.setVisibility(View.VISIBLE);
                    } else
                        playBtn.setVisibility(View.GONE);
                    backBtn.setVisibility(View.VISIBLE);
                    fullBtn.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    loadingView.setVisibility(View.GONE);
                    loadingText.setText(dialogText);
                    break;
                case 5:
                    loadingView.setVisibility(View.VISIBLE);
                    loadingText.setText(dialogText);
                    break;
                case 6:
                    showLoginAnim();
                    break;
                case 7:
                    showNewMsgView((UserBean) msg.obj);
                    break;
                case 8:
                    hideNewMsgView();
                    break;
                default:
                    break;
            }
        }
    };

    private void showLoginAnim() {
        if (isDialogShow) {
            loadingView.setVisibility(View.VISIBLE);
        } else {
            loadingView.setVisibility(View.GONE);
        }
        if (isDialogTextShow) {
            loadingText.setVisibility(View.VISIBLE);
        } else {
            loadingText.setVisibility(View.GONE);
        }
    }

    /**
     * 重力感应监听者
     */

    /**
     * 定时隐藏后退按钮和全屏按钮
     */
    private void setTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 2 * 1000);
    }

    @Override
    protected void onStop() {
        Log.i("LiveActivity", "stop");
        // if (fragment_chat != null)
        // fragment_chat.onDestroy();
        removeFloatView();
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.i("LiveActivity", "pause");
        player.videoSet(true);
        isChange = true;
        super.onPause();
        isUp = false;
    }

    @Override
    protected void onRestart() {
        Log.i("LiveActivity", "restart");
        super.onRestart();

//        fragment_kf = new Fragment_kefu();
//        Bundle bundle = new Bundle();
//        bundle.putString("url", SPUtils.getString(this, SpConstant.APPINFO_KEFU_URL));
//        fragment_kf.setArguments(bundle);
//        fragment_function = new Fragment_function();
//        fragments = new ArrayList<>();
//        fragments.add(0, fragment_chat);
//        fragments.add(1, fragment_kf);
//        fragments.add(2, fragment_function);
//        adapter = new DemoAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(num);

        bindService();
    }

    @Override
    protected void onResume() {
        Log.i("LiveActivity", "resume");
        // mLivePlayer.setDataSource(url);
        player.videoSet(false);
        fragment_chat = new Fragment_chat();
        if (isChange) {
            isChange = false;
            fragment_function = new Fragment_function();
        }
        adapter.notifyDataSetChanged();
        if (viewPager.getCurrentItem() == 0) {
            try {
                if (fragment_chat.rl_bottom.getVisibility() == View.VISIBLE) {
                    fragment_chat.rl_bottom.setVisibility(View.GONE);
                }
                createFloatView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onResume();
        isUp = true;
        if (PrivateChatUtils.isShowPrivateMsgBtn(this)) {
            if (PrivateChatUtils.isShowPoint(this))
                privatePoint.setVisibility(View.VISIBLE);
            else
                privatePoint.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("LiveActivity", "destory");
        unregisterReceiver(receiver);
        unregisterReceiver(mBatInfoReceiver);
        unregisterReceiver(hideOrShowTitle);
        KXTApplication.core.clearAuth();
        KXTApplication.IsOut = true;
        if (player != null) {
            player.leave();
            player.release(this);
        }
        live = null;
        stopService(intent);
        super.onDestroy();
    }

    public Fragment_chat getFragment() {
        return fragment_chat;
    }

    class CallPhoneBroadcaseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("action" + intent.getAction());
            // 如果是去电
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            } else {
                // 查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
                // 如果我们想要监听电话的拨打状况，需要这么几步 :
                /*
                 * 第一：获取电话服务管理器TelephonyManager manager =
				 * this.getSystemService(TELEPHONY_SERVICE);
				 * 第二：通过TelephonyManager注册我们要监听的电话状态改变事件。manager.listen(new
				 * MyPhoneStateListener(),
				 * PhoneStateListener.LISTEN_CALL_STATE);
				 * 这里的PhoneStateListener.LISTEN_CALL_STATE就是我们想要
				 * 监听的状态改变事件，初次之外，还有很多其他事件哦。 第三步：通过extends
				 * PhoneStateListener来定制自己的规则。将其对象传递给第二步作为参数。
				 * 第四步：这一步很重要，那就是给应用添加权限。android.permission.READ_PHONE_STATE
				 */

                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                tm.listen(listener2, PhoneStateListener.LISTEN_CALL_STATE);
                // 设置一个监听器
            }
        }
    }

    PhoneStateListener listener2 = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // 注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    System.out.println("挂断");
                    player.audioSet(false);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    System.out.println("接听");
                    player.audioSet(true);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    System.out.println("响铃:来电号码" + incomingNumber);
                    break;
            }
        }
    };
    private BroadcastReceiver mBatInfoReceiver;
    private Intent intent;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void isChange(boolean ischange) {
        // TODO Auto-generated method stub
        if (ischange) {
            functionBtn.setText("返回");
        } else {
            functionBtn.setText("登录");
        }
    }

    private boolean isChatView = false;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void changeTitle() {
        // TODO Auto-generated method stub
        String text = functionBtn.getText().toString().trim();
        if (text != null) {
            if ("功能".equals(text)) {
                functionBtn.setText("返回");
                if (chatbg.isSelected()) {
                    functionbg.setSelected(true);
                    chatbg.setSelected(false);
                    isChatView = true;
                } else {
                    isChatView = false;
                }
            } else {
                functionBtn.setText("功能");
                if (isChatView) {
                    functionbg.setSelected(false);
                    chatbg.setSelected(true);
                } else {

                }
            }
        }
    }

    /**
     * 屏幕明灭监听
     */
    private void screenOffOrOn() {
        // TODO Auto-generated method stub
        IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        // filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.d("info", "screen off 关闭声音");
                    KXTApplication.player.setMute(true);
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    Log.d("info", "screen unlock 打开声音");
                    KXTApplication.player.setMute(false);
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }


    /**
     * 绑定聊天socket
     */
    private void bindService() {
        if (!(SystemUtils.isServiceRunning(this, ChatService.class.getName()))) {
            intent = new Intent(GenseeActivity.this, ChatService.class);
            startService(intent);
        }
    }

    public void createFloatView() {
//        GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        try {
            mFloatLayout.setVisibility(View.VISIBLE);
            if (LoginInfoUtils.isLogin(this)) {
                chat_dl.setVisibility(View.GONE);
            } else {
                chat_dl.setVisibility(View.VISIBLE);
            }
            if (PrivateChatUtils.isShowPrivateMsgBtn(this)) {
                privateBtn.setVisibility(View.VISIBLE);
                if (PrivateChatUtils.isShowPoint(this)) {
                    privatePoint.setVisibility(View.VISIBLE);
                } else {
                    privatePoint.setVisibility(View.GONE);
                }
            } else {
                privateBtn.setVisibility(View.GONE);
                privatePoint.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFloatView() {
        try {
            mFloatLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 控制菜单栏显示隐藏
     */
    public class HideOrShowTitle extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("hideorshow", 0);
            if (type == 1) {
                menu.setVisibility(View.GONE);
            } else if (type == 2) {
                menu.setVisibility(View.VISIBLE);
                GenseeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                if (FaceRelativeLayout.viewFace.getVisibility() == View.VISIBLE) {
                } else {
                    fragment_chat.rl_bottom.setVisibility(View.GONE);
                    if (isUp)
                        createFloatView();
                    else
                        removeFloatView();
                }

            } else if (type == 3) {
                removeFloatView();
                functionBtn.setText("返回");
                Intent intent2 = new Intent(GenseeActivity.this, Login_One.class);
                intent2.putExtra("from", "live");
                startActivity(intent2);
            }
        }
    }

    private AudioManager am;
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                Log.i("genseeam", "AUDIOFOCUS_LOSS_TRANSIENT");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                Log.i("genseeam", "AUDIOFOCUS_GAIN");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.i("genseeam", "AUDIOFOCUS_LOSS");
                am.abandonAudioFocus(afChangeListener);
                // Stop playback
            }
        }
    };

    @Override
    public void onJoin(int i) {

        String msg = null;
        switch (i) {
            case JOIN_OK:
                msg = "加入成功";
                isDialogShow = false;
                isDialogTextShow = false;
                dialogText = msg;
                isPlay = true;
                handler.sendEmptyMessage(4);
                break;
            case JOIN_CONNECTING:
                msg = "正在加入";
                isDialogShow = true;
                isDialogTextShow = false;
                isPlay = false;
                handler.sendEmptyMessage(5);
                break;
            case JOIN_CONNECT_FAILED:
                msg = "连接失败";
                isDialogShow = false;
                isDialogTextShow = true;
                dialogText = msg;
                isPlay = false;
                handler.sendEmptyMessage(5);
                break;
            case JOIN_RTMP_FAILED:
                msg = "连接服务器失败";
                isDialogShow = false;
                isDialogTextShow = true;
                dialogText = msg;
                isPlay = false;
                handler.sendEmptyMessage(5);
                break;
            case JOIN_TOO_EARLY:
                msg = "直播还未开始";
                isDialogShow = false;
                isDialogTextShow = true;
                dialogText = msg;
                isPlay = false;
                handler.sendEmptyMessage(5);
                break;
            case JOIN_LICENSE:
                msg = "人数已满";
                isDialogShow = false;
                isDialogTextShow = true;
                dialogText = msg;
                isPlay = false;
                handler.sendEmptyMessage(5);
                break;
            default:
                msg = "加入返回错误" + i;
                isDialogShow = false;
                isDialogTextShow = true;
                dialogText = msg;
                isPlay = false;
                handler.sendEmptyMessage(5);
                break;

        }
        handler.sendEmptyMessage(6);
    }

    @Override
    public void onUserJoin(UserInfo userInfo) {

    }

    @Override
    public void onUserLeave(UserInfo userInfo) {

    }

    @Override
    public void onUserUpdate(UserInfo userInfo) {

    }

    @Override
    public void onRosterTotal(int i) {

    }

    @Override
    public void onReconnecting() {

    }

    @Override
    public void onLeave(int i) {
// 当前用户退出
        // bJoinSuccess = false;
        String msg = null;
        switch (i) {
            case LEAVE_NORMAL:
                msg = "您已经退出直播间";
                break;
            case LEAVE_KICKOUT:
                msg = "您已被踢出直播间";
                break;
            case LEAVE_TIMEOUT:
                msg = "连接超时，您已经退出直播间";
                break;
            case LEAVE_CLOSE:
                msg = "直播已经停止";
                break;
            case LEAVE_UNKNOWN:
                msg = "您已退出直播间，请检查网络、直播间等状态";
                break;
            default:
                break;
        }
        if (null != msg) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        // if (mPlayer != null) {
        // mPlayer.release(getApplicationContext());
        // }
        // toastMsg(msg);
    }

    @Override
    public void onCaching(boolean b) {

    }

    @Override
    public void onErr(int i) {
        String msg = null;
        switch (i) {
            case AbsRtAction.ErrCode.ERR_DOMAIN:
                msg = "域名domain不正确";
                break;
            case AbsRtAction.ErrCode.ERR_TIME_OUT:
                msg = "请求超时，稍后重试";
                break;
            case AbsRtAction.ErrCode.ERR_SITE_UNUSED:
                msg = "站点不可用，请联系客服或相关人员";
                break;
            case AbsRtAction.ErrCode.ERR_UN_NET:
                msg = "网络不可用，请检查网络连接正常后再试";
                break;
            case AbsRtAction.ErrCode.ERR_SERVICE:
                msg = "service  错误，请确认是webcast还是training";
                break;
            case AbsRtAction.ErrCode.ERR_PARAM:
                msg = "initparam参数不全";
                break;
            case AbsRtAction.ErrCode.ERR_THIRD_CERTIFICATION_AUTHORITY:
                msg = "第三方认证失败";
                break;
            case AbsRtAction.ErrCode.ERR_NUMBER_UNEXIST:
                msg = "编号不存在";
                break;
            case AbsRtAction.ErrCode.ERR_TOKEN:
                msg = "口令错误";
                break;
            case AbsRtAction.ErrCode.ERR_LOGIN:
                msg = "站点登录帐号或登录密码错误";
                break;
            default:
                msg = "错误：errCode = " + i;
                break;
        }
        isDialogShow = false;
        isDialogTextShow = true;
        dialogText = msg;
        isPlay = false;
        handler.sendEmptyMessage(5);
        if (msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        Log.i("videoinfo", msg);
        handler.sendEmptyMessage(6);
    }

    @Override
    public void onDocSwitch(int i, String s) {

    }

    @Override
    public void onVideoBegin() {

    }

    @Override
    public void onVideoEnd() {

    }

    @Override
    public void onVideoSize(int i, int i1, boolean b) {

    }

    @Override
    public void onAudioLevel(int i) {

    }

    @Override
    public void onPublish(boolean b) {
        if (!b) {
            isPlay = false;
            playState = "直播结束";
            dialogText = playState;
            isDialogShow = false;
            handler.sendEmptyMessage(4);
        } else {
            isPlay = true;
            playState = "";
            isDialogShow = false;
            handler.sendEmptyMessage(5);
        }
        handler.sendEmptyMessage(6);
    }

    @Override
    public void onSubject(String s) {

    }

    @Override
    public void onPageSize(int i, int i1, int i2) {

    }

    @Override
    public void onPublicMsg(long l, String s) {

    }

    @Override
    public void onLiveText(String s, String s1) {

    }

    @Override
    public void onRollcall(int i) {

    }

    @Override
    public void onLottery(int i, String s) {

    }

    @Override
    public void onFileShare(int i, String s, String s1) {

    }

    @Override
    public void onFileShareDl(int i, String s, String s1) {

    }

    @Override
    public void onInvite(int i, boolean b) {

    }

    @Override
    public void onMicNotify(int i) {

    }
}
