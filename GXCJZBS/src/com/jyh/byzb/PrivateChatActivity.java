package com.jyh.byzb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jyh.byzb.adapter.ChatMsgAdapter;
import com.jyh.byzb.adapter.PrivateChatUserAdapter;
import com.jyh.byzb.bean.ChatMsgEntity;
import com.jyh.byzb.bean.EventBusBean;
import com.jyh.byzb.bean.KXTApplication;
import com.jyh.byzb.bean.UserBean;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.constant.UrlConstant;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.PrivateChatUtils;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.SystemUtils;
import com.jyh.byzb.common.utils.ToastView;
import com.jyh.byzb.common.utils.volleyutil.NormalPostRequest;
import com.jyh.byzb.view.FaceRelaPriveLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 项目名:GXCJZBS
 * 类描述:私聊界面
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/5.
 */

public class PrivateChatActivity extends FragmentActivity implements FaceRelaPriveLayout.BackFinshListening{

    public static String INTENT_TNAME = "tname";
    public static String INTENT_TUID = "tuid";
    public static String INTENT_TRID = "trid";
    public static String INTENT_TYPE = "type";

    public static String TYPE_CHAT = "chat";
    public static String TYPE_LIST = "list";

    ListView userListView;
    ListView chatListView;
    SimpleDraweeView chatPhoto;
    TextView chatName;
    Button chatLogin;
    LinearLayout btnFace;
    LinearLayout caitiao;
    RelativeLayout chatLoginview;
    ImageView btnSend;
    TextView tvSend;
    EditText etSendmessage;
    LinearLayout rlInput;
    ViewPager vpContains;
    LinearLayout ivImage;
    RecyclerView recyclerView;
    LinearLayout llFacechoose;
    LinearLayout chatLayout;
    private Timer timer;
    private int i;
    private PrivateChatUserAdapter userAdapter;
    private TextView titleTv;
    private boolean isQuit = true;
    private FaceRelaPriveLayout faceRelativeLayout;

    private void initView() {
        userListView = (ListView) findViewById(R.id.userListView);
        chatListView = (ListView) findViewById(R.id.chatListView);
        chatPhoto = (SimpleDraweeView) findViewById(R.id.chat_photo);
        chatName = (TextView) findViewById(R.id.chat_name);
        chatLogin = (Button) findViewById(R.id.chat_login);
        btnFace = (LinearLayout) findViewById(R.id.btn_face);
        caitiao = (LinearLayout) findViewById(R.id.caitiao);
        chatLoginview = (RelativeLayout) findViewById(R.id.chat_loginview);
        btnSend = (ImageView) findViewById(R.id.btn_send);
        tvSend = (TextView) findViewById(R.id.tv_send);
        etSendmessage = (EditText) findViewById(R.id.et_sendmessage);
        rlInput = (LinearLayout) findViewById(R.id.rl_input);
        vpContains = (ViewPager) findViewById(R.id.vp_contains);
        ivImage = (LinearLayout) findViewById(R.id.iv_image);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        llFacechoose = (LinearLayout) findViewById(R.id.ll_facechoose);
        faceRelativeLayout = (FaceRelaPriveLayout) findViewById(R.id.layout);
        faceRelativeLayout.hideCT();
        chatLayout = (LinearLayout) findViewById(R.id.chatLayout);
        titleTv = (TextView) findViewById(R.id.title_tv);
        findViewById(R.id.break_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etSendmessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    faceRelativeLayout.close();
                }
                return false;
            }
        });
        btnSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    faceRelativeLayout.close();
                    if (etSendmessage.getText().toString().length() <= 120) {
                        try {
                            if (etSendmessage.getText().toString().trim().equals("") || etSendmessage.getText().toString().length()
                                    <= 0) {
                                ToastView.makeText(PrivateChatActivity.this, "内容为空");
                            } else {
                                send();
                                timer = new Timer();
                                setTimerTask();
                                btnSend.setVisibility(View.INVISIBLE);
                                tvSend.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(3);
                    }
                }
                return false;
            }
        });

    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    if (i == 0 || i < 0) {
                        timer.cancel();
                        timer.purge();
                        btnSend.setVisibility(View.VISIBLE);
                        tvSend.setVisibility(View.GONE);
                        i = Integer.parseInt(SPUtils.getString(PrivateChatActivity.this, SpConstant.USERINFO_LIMIT_CHAT_TIME)) + 1;
                    }
                    i = i - 1;
                    tvSend.setText("" + i);
                    break;
                case 3:
                    ToastView.makeText(PrivateChatActivity.this, "输入字数超过范围");
                    break;
            }
        }

    };

    private void setTimerTask() {
        i = Integer.parseInt(SPUtils.getString(this, SpConstant.USERINFO_LIMIT_CHAT_TIME)) + 1;
        tvSend.setText("" + (i - 1));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
            }
        }, 1 * 1000, 1 * 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    private String tName, fName;
    private String tUid, fUid;
    private String tRid, fRid;
    private String type;
    private String toKen;
    private RequestQueue queue;
    private ChatMsgAdapter chatMsgAdapter;
    private InputMethodManager imm;
    private String contString;
    private String msgType;
    private KXTApplication application;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);
        initView();
//        setDialogStyle();
        faceRelativeLayout.setBackInterface(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        application = (KXTApplication) getApplication();
        try {
            i = Integer.parseInt(SPUtils.getString(this, SpConstant.USERINFO_LIMIT_CHAT_TIME)) + 1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        queue = Volley.newRequestQueue(this);
        getTUserInfo(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getTUserInfo(intent);
    }

    /**
     * 设置根布局宽高
     */
    private void setDialogStyle() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = d.getHeight() - SystemUtils.getStatuBarHeight(this); // 高度设置为屏幕的1.0
        p.width = d.getWidth(); // 宽度设置为屏幕的1.0
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.0f; // 设置黑暗度

        getWindow().setAttributes(p); // 设置生效
        getWindow().setGravity(Gravity.BOTTOM); // 设置居中
    }

    /**
     * 获取私聊对象信息
     *
     * @param intent
     */
    private void getTUserInfo(Intent intent) {
        if (intent != null) {
            tName = intent.getStringExtra(INTENT_TNAME);
            tUid = intent.getStringExtra(INTENT_TUID);
            tRid = intent.getStringExtra(INTENT_TRID);
            type = intent.getStringExtra(INTENT_TYPE);
        }
        getFUserInfo();
        showView();
    }

    /**
     * 获取私聊对象信息
     *
     * @param userBean
     */
    public void getTUserInfo(UserBean userBean) {
        tName = userBean.getName();
        tUid = userBean.getUid();
        tRid = userBean.getRid();
        type = TYPE_CHAT;
        getFUserInfo();
        showView();
    }

    private void getFUserInfo() {
        toKen = SPUtils.getString(this, SpConstant.USERINFO_TOKEN);
        if (LoginInfoUtils.isLogin(this)) {
            fName = SPUtils.getString(this, SpConstant.USERINFO_LOGIN_NAME);
            fRid = SPUtils.getString(this, SpConstant.USERINFO_LOGIN_RID);
            fUid = SPUtils.getString(this, SpConstant.USERINFO_LOGIN_UID);
        } else {
            fUid = SPUtils.getString(this, SpConstant.USERINFO_UID);
            fName = SPUtils.getString(this, SpConstant.USERINFO_NAME);
            fRid = SPUtils.getString(this, SpConstant.USERINFO_RID);
        }
    }

    /**
     * 根据type显示响应的布局
     */
    private void showView() {
        if (TYPE_CHAT.equals(type)) {
            //显示私聊界面
            chatLayout.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.GONE);
            titleTv.setText("和 " + tName + " 私聊");
            getChatList();
        } else {
            //显示私聊列表
            chatLayout.setVisibility(View.GONE);
            userListView.setVisibility(View.VISIBLE);
            titleTv.setText("私信");
            setUserList();
        }
    }

    /**
     * 设置私聊对象列表信息
     */
    private void setUserList() {
        List userList = PrivateChatUtils.getPrivateChatUserList(this);
        if (userList != null && userList.size() != 0) {
            if (userAdapter == null) {
                userAdapter = new PrivateChatUserAdapter(this, userList);
                userListView.setAdapter(userAdapter);
            } else {
                userAdapter.setUsers(userList);
            }
        }
    }

    /**
     * 获取私聊记录
     */
    private void getChatList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("my_id", fUid);
        map.put("other_id", tUid);
        NormalPostRequest normalPostRequest = new NormalPostRequest(UrlConstant.CHAT_PRIVATEHISTORY, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    List<ChatMsgEntity> chatMsgEntities = JSON.parseArray(jsonObject.getString("data"), ChatMsgEntity.class);
                    //清空上一次记录
                    if (chatMsgAdapter != null) {
                        chatMsgAdapter.clear();
                    }
                    if (chatMsgEntities != null && chatMsgEntities.size() > 0) {
                        if (chatMsgAdapter == null) {
                            chatMsgAdapter = new ChatMsgAdapter(PrivateChatActivity.this, chatMsgEntities, chatListView);
                            chatListView.setAdapter(chatMsgAdapter);
                            chatListView.setSelection(chatMsgAdapter.getCount() - 1);
                        } else {
                            chatMsgAdapter.addAll(chatMsgEntities);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("info", volleyError.getMessage());
            }
        }, map);
        queue.add(normalPostRequest);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusBean bean) {
        try {
            if (type.equals(TYPE_CHAT)) {
                PrivateChatUtils.savePrivateChatNewPoint(this, tUid, PrivateChatUtils.POINT_HINT);
                if (bean == EventBusBean.NEW_CHATMSG) {
                    Bundle bundle = (Bundle) bean.getObject();
                    String chatType = bundle.getString("type");
                    String t_uid = bundle.getString("t_uid");
                    String f_uid = bundle.getString("f_uid");
                    if (chatType != null && chatType.equals("private")) {
                        //私聊
                        if (f_uid == null || t_uid == null) {
                            //私聊中uid不可能为空
                            return;
                        }
                        if ((f_uid.equals(tUid) && t_uid.equals(fUid))) {
                            //属于自己与对方的私聊,且是对方发给自己的私聊(自己发的私聊已在本地显示,避免重复)
                            ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
                            chatMsgEntity.setData(bundle.getString("data"));
                            chatMsgEntity.setIs_checked(bundle.getString("is_checked"));
                            chatMsgEntity.setT_uid(t_uid);
                            chatMsgEntity.setT_rid(bundle.getString("t_rid"));
                            chatMsgEntity.setT_name(bundle.getString("t_name"));
                            chatMsgEntity.setF_name(bundle.getString("f_name"));
                            chatMsgEntity.setF_rid(bundle.getString("f_rid"));
                            chatMsgEntity.setF_uid(f_uid);
                            chatMsgEntity.setTime(bundle.getString("time"));
                            chatMsgEntity.setId(bundle.getString("id"));
                            if (chatMsgAdapter != null) {
                                chatMsgAdapter.add(chatMsgEntity);
                                chatListView.setSelection(chatMsgAdapter.getCount() - 1);
                            } else {
                                List<ChatMsgEntity> chatMsgEntities = new ArrayList<>();
                                chatMsgEntities.add(chatMsgEntity);
                                chatMsgAdapter = new ChatMsgAdapter(this, chatMsgEntities, chatListView);
                                chatListView.setAdapter(chatMsgAdapter);
                            }
                        }
                    }

                } else if (bean == EventBusBean.DEL_CHATMSG) {
                    if (chatMsgAdapter == null) return;
                    Bundle bundle = (Bundle) bean.getObject();
                    String id = bundle.getString("id");
                    List<ChatMsgEntity> msgs = chatMsgAdapter.getColl();
                    Iterator<ChatMsgEntity> iterator = msgs.iterator();
                    while (iterator.hasNext()) {
                        ChatMsgEntity next = iterator.next();
                        if (id.equals(next.getId()))
                            iterator.remove();
                    }
                    chatMsgAdapter.notifyDataSetChanged();
                    chatListView.setSelection(chatMsgAdapter.getCount() - 1);
                } else if (bean == EventBusBean.SEND_CHATMSG) {
                    send();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send() {
        getFUserInfo();
        boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
        if (isOpen) {
            try {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                        (getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
            }
        }
        contString = etSendmessage.getText().toString();
        msgType = null;
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setIs_checked("1");
            entity.setT_uid(tUid);
            entity.setT_rid(tRid);
            entity.setT_name(tName);
            entity.setF_name(fName);
            entity.setF_rid(fRid);
            entity.setF_uid(fUid);
            entity.setData(contString);
            entity.setTime("" + System.currentTimeMillis() / 1000);
            if (chatMsgAdapter == null) {
                List<ChatMsgEntity> chatMsgEntities = new ArrayList<>();
                chatMsgEntities.add(entity);
                chatMsgAdapter = new ChatMsgAdapter(PrivateChatActivity.this, chatMsgEntities, chatListView);
                chatListView.setAdapter(chatMsgAdapter);
            } else
                chatMsgAdapter.add(entity);
            chatListView.setSelection(chatMsgAdapter.getCount() - 1);
            etSendmessage.setText("");
            SendMassage();
        }
    }

    private void SendMassage() {
        // TODO Auto-generated method stub
        Map<String, String> map = new HashMap<String, String>();
        // cmd:message
        // token:37fdb20b00078ed4ab37cf1d472073cd //游客不用传token
        // f_uid:1
        // f_name:会员-102
        // f_rid:2
        // t_uid: 0
        // t_name: ""
        // t_rid: 0
        // data:sdfsdaf
        // time:1460106061
        // type:public
        map.put("cmd", "message");
        map.put("token", toKen);// 游客不用传token
        map.put("f_uid", fUid);
        map.put("f_name", fName);
        map.put("f_rid", fRid);
        map.put("t_uid", tUid);
        map.put("t_name", tName);
        map.put("t_rid", tRid);
        map.put("data", contString);
        map.put("time", "" + System.currentTimeMillis() / 1000);
        map.put("type", "private");

        NormalPostRequest postRequest = new NormalPostRequest(UrlConstant.URL_SEND, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                try {
                    if ("200".equals(arg0.getString("code"))) {
                        PrivateChatUtils.savePrivateChatUserInfo(application, tName, tUid, tRid);
                    } else if ("401".equals(arg0.getString("code"))) {
                        ToastView.makeText(application, "消息发送失败,Token已过期");
                    } else {
                        ToastView.makeText(application, "消息发送失败," + arg0.getString("msg"));
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                ToastView.makeText(application, "消息发送失败," + arg0);
            }
        }, map);
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        if (isQuit) {
            super.onBackPressed();
        } else {
            isQuit = true;
            if (type.equals(TYPE_CHAT)) {
                List userList = PrivateChatUtils.getPrivateChatUserList(this);
                if (userList != null && userList.size() != 0) {
                    type = TYPE_LIST;
                    showView();
                } else {
                    super.onBackPressed();
                }
            } else
                super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        queue.cancelAll(mRequestFilter);
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(chatMsgAdapter!=null){
                chatMsgAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        getFUserInfo();
        try {
            faceRelativeLayout.onReflshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    RequestQueue.RequestFilter mRequestFilter = new RequestQueue.RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            Object mRequestTag = request.getTag();
            return mRequestTag == null;
        }
    };

    public void setQuit(boolean isQuit) {
        this.isQuit = isQuit;
    }

    @Override
    public void setBackFinsh() {
        finish();
    }
}
