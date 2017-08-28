package com.jyh.gxcjzbs.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jyh.gxcjzbs.R;
import com.jyh.gxcjzbs.bean.EventBusBean;
import com.jyh.gxcjzbs.bean.KXTApplication;
import com.jyh.gxcjzbs.common.constant.SpConstant;
import com.jyh.gxcjzbs.common.utils.LoginInfoUtils;
import com.jyh.gxcjzbs.common.utils.PrivateChatUtils;
import com.jyh.gxcjzbs.common.utils.SPUtils;
import com.jyh.gxcjzbs.common.utils.NetworkCenter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocket.ConnectionHandler;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

public class ChatService extends IntentService {

    private FlashService flashService;
    public static WebSocketConnection wsc;

    private Bundle bundle = new Bundle();

    private RequestQueue queue;
    private JsonArrayRequest jsonArrayRequest;

    private String gate_Url;// gate 地址
    private String ip_Url;
    private ArrayList<String> data = new ArrayList<String>();// gate
    private int i = 0;// data下标

    protected int k = 0;// 获取gate失败次数
    protected boolean IsOk;// socket是否连接

    private int running = 1;// 用于发送心跳

    private String id, name, rid;

    private KXTApplication application;

    public ChatService(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public ChatService() {
        super("ChatService");
    }

    public class FlashService extends Binder {
        public ChatService Getservire() {
            return ChatService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStart(intent, startId);
        handler.postDelayed(runnable, 300 * 1000);// 发送心跳包 300秒

        // 获取gate地址
        gate_Url = SPUtils.getString(this, SpConstant.APPINFO_GATE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setContentTitle("WF Update Service");
        builder.setContentText("wf update service is running！");
        builder.setWhen(System.currentTimeMillis());
        // 让该service前台运行，避免手机休眠时系统自动杀掉该服务
        // 如果 id 为 0 ，那么状态栏的 notification 将不会显示。
        startForeground(0, builder.build());

        return super.onStartCommand(intent, START_STICKY, startId);
    }

    private String GetToken() {
        // TODO Auto-generated method stub 3-12
        // {"r":"ddcj","cmd":"login","uid":"-46094312468","rid":"1","name":"游客-m468h","mark":"login"}
        // r 为直播室code
        // String Token =
        // "{\"r\":\"live2\",\"cmd\":\"login\",\"uid\":\"-46094312468\",\"rid\":\"1\",\"name\":\"游客-m468h\",\"mark\":\"login\"}";

        if (LoginInfoUtils.isLogin(this)) {
            // 登录有效
            id = SPUtils.getString(this, SpConstant.USERINFO_LOGIN_UID);
            rid = SPUtils.getString(this, SpConstant.USERINFO_LOGIN_RID);
            name = SPUtils.getString(this, SpConstant.USERINFO_LOGIN_NAME);
        } else {
            id = SPUtils.getString(this, SpConstant.USERINFO_UID);
            rid = SPUtils.getString(this, SpConstant.USERINFO_RID);
            name = SPUtils.getString(this, SpConstant.USERINFO_NAME);
        }

        String Token = "{\"r\":\"" + SPUtils.getString(getApplicationContext(), SpConstant.APPINFO_APPID) + "\",\"cmd\":\"login\"," +
                "\"uid\":\"" + id + "\",\"rid\":\"" + rid
                + "\",\"name\":\"" + name + "\",\"mark\":\"login\"}";
        return Token;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return flashService;
    }

    /**
     * 从 gate 获取socket地址
     *
     * @param Url
     */
    private void GetIPUrl(final String Url) {
        // TODO Auto-generated method stub
        application = (KXTApplication) getApplication();
        queue = application.getQueue();
        jsonArrayRequest = new JsonArrayRequest(Url, new Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray arg0) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = arg0;
                    for (int i = 0; i < array.length(); i++) {
                        String object = (String) array.get(i);
                        data.add(object);
                    }
                    data.add(data.get(0));
                    Log.i("socket", "data=" + data.toString());
                    k = 0;
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                k++;
                if (k < 2) {
                    handler.sendEmptyMessageDelayed(1, 10 * 1000);
                } else {
                }
            }

        });
        jsonArrayRequest.setCacheEntry(null);
        queue.add(jsonArrayRequest);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
                        GetIPUrl(gate_Url);
                    } else {
                        Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 30:
                    if (null != wsc && wsc.isConnected()) {
                        wsc.sendTextMessage(GetToken());
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    protected void GetIP() {
        // TODO Auto-generated method stub
        if (data.size() > 0 && NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
            ip_Url = "ws://" + data.get((int) (Math.random() * (data.size() - 1) + 0.5));
            Log.i("socket", "data=" + ip_Url);
            connect(ip_Url);
        }
    }

    /**
     * 重连逻辑: 从 gate 获取到的数据是这样的 ["114.55.63.226:9506", "114.55.62.212:9506",
     * "114.55.61.117:9506"] 新建一个数组 把 第0个元素加到最后 ["114.55.63.226:9506",
     * "114.55.62.212:9506", "114.55.61.117:9506"，"114.55.63.226:9506"];
     * 然后从这个新的数组里随机获取一个 进程连接 断线后重连时把 断线的链接从 原数组里删除 如114.55.63.226:9506断线了 ，从
     * ["114.55.63.226:9506", "114.55.62.212:9506", "114.55.61.117:9506"] 中删除
     * ，得到 [ "114.55.62.212:9506", "114.55.61.117:9506"] 再重复之前的操作 把第0个元素加到最后 [
     * "114.55.62.212:9506", "114.55.61.117:9506"，"114.55.62.212:9506"]
     * 然后从这个新的数组里随机获取一个 进程连接 直到原数组为空的时候再重新从 gate里获取 服务器信息
     *
     * @param SocketIP
     */
    private void connect(String SocketIP) {
        try {
            if (wsc == null) {
                wsc = new WebSocketConnection();
            }
            if (wsc.isConnected()) {
                IsOk = true;
                return;
            }
            wsc.connect(SocketIP, new ConnectionHandler() {

                @Override
                public void onBinaryMessage(byte[] payload) {

                }

                @Override
                public void onClose(int code, String reason) {
                    Log.i("socket", "close");
                    IsOk = false;
                    if (NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
                        try {
                            if (data.size() > 0) {
                                data.remove(ip_Url.replace("ws://", "").trim());
                                data.add(data.get(0));
                                Log.i("socket", "data=" + data.toString());
                            } else {
                                GetIPUrl(gate_Url);
                            }
                            if (!application.IsOut)
                                GetIP();
                            // Thread.sleep(10 * 1000);
                            // if (!application.IsOut) {
                            // if (a < 3) {
                            // if (!wsc.isConnected()) {
                            // wsc.reconnect();
                            // }
                            // } else {
                            // wsc.disconnect();
                            // i++;
                            // if (i > data.size()) {
                            // i = 0;
                            // data.clear();
                            // GetIPUrl(gate_Url);
                            // }
                            // GetIP();
                            // }
                            // }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onOpen() {
                    Log.i("socket", "open");
                    handler.sendEmptyMessage(30);
                }

                @Override
                public void onRawTextMessage(byte[] payload) {
                }

                @Override
                public void onTextMessage(String payload) {
                    IsOk = true;
                    Log.i("socket", "test=" + payload);
                    try {
                        JSONObject jsonObject = new JSONObject(payload);
                        String cmd = jsonObject.getString("cmd");
                        if ("message".equals(cmd)) {
                            String type = jsonObject.optString("type");
                            String f_uid = jsonObject.getString("f_uid");
                            String f_name = jsonObject.getString("f_name");
                            String f_rid = jsonObject.getString("f_rid");
                            if (type != null && type.equals("private")) {
                                String uid = LoginInfoUtils.getUid(ChatService.this);
                                if (uid.equals(f_uid)) {
                                    return;
                                } else {
                                    PrivateChatUtils.savePrivateChatUserInfo(ChatService.this, f_name, f_uid, f_rid);
                                    PrivateChatUtils.saveIsShowPrivateMsgBtn(ChatService.this,true);
                                }
                            }
                            bundle.clear();
                            bundle.putString("f_uid", f_uid);
                            bundle.putString("f_name", f_name);
                            bundle.putString("f_rid", f_rid);
                            bundle.putString("t_uid", jsonObject.getString("t_uid"));
                            bundle.putString("t_name", jsonObject.getString("t_name"));
                            bundle.putString("t_rid", jsonObject.getString("t_rid"));
                            bundle.putString("data", jsonObject.optString("data"));
                            bundle.putString("time", jsonObject.optString("time"));
                            bundle.putString("is_checked", jsonObject.optString("is_checked"));
                            bundle.putString("id", jsonObject.optString("id"));
                            bundle.putString("type", type);
                            bundle.putString("cmd", cmd);
                            EventBus.getDefault().post(EventBusBean.NEW_CHATMSG.setObj(bundle));
                        } else if ("delMsg".equals(cmd)) {
                            bundle.clear();
                            bundle.putString("cmd", cmd);
                            bundle.putString("id", jsonObject.getString("id"));
                            EventBus.getDefault().post(EventBusBean.DEL_CHATMSG);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                }

            });
        } catch (WebSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            IsOk = false;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        while (running == 1) {
            if (data.size() > 0) {
                if (!IsOk) {
                    GetIP();
                }
            }
            if (data.size() < 1 && NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
                GetIPUrl(gate_Url);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            // 要做的事情
            if (null != wsc && wsc.isConnected() && NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
                wsc.sendTextMessage("");
                Log.i("socket", "心跳包");
            }
            handler.postDelayed(this, 300 * 1000);
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (null != wsc && wsc.isConnected()) {
            wsc.disconnect();
        }
        IsOk = false;
        handler.removeCallbacks(runnable);
        running = 0;
        super.onDestroy();
    }
}
