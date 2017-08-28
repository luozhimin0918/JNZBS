package com.jyh.byzb.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jyh.byzb.GenseeActivity;
import com.jyh.byzb.GotyeLiveActivity;
import com.jyh.byzb.R;
import com.jyh.byzb.adapter.ChatMsgAdapter;
import com.jyh.byzb.bean.ChatMsgEntity;
import com.jyh.byzb.bean.EventBusBean;
import com.jyh.byzb.bean.KXTApplication;
import com.jyh.byzb.bean.UserBean;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.constant.UrlConstant;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.SystemUtils;
import com.jyh.byzb.common.utils.ToastView;
import com.jyh.byzb.common.utils.imageutils.ImageDownLoader;
import com.jyh.byzb.common.utils.volleyutil.NormalPostRequest;
import com.jyh.byzb.service.ChatService;
import com.jyh.byzb.view.FaceRelativeLayout;
import com.jyh.byzb.view.MyListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 聊天界面
 */
@SuppressWarnings("deprecation")
public class Fragment_chat extends Fragment {
    private View view;
    private ImageView mBtnSend;
    public static EditText mEditTextContent;
    public static MyListView mListView;
    private ChatMsgAdapter mAdapter;
    private InputMethodManager imm;
    private List<ChatMsgEntity> mDataArrays;// 及时数据
    private boolean iscon = false;
    private KXTApplication application;
    private ImageDownLoader loader;
    private Timer timer;
    private TextView mtv_send;
    private int i;
    private String from_name, from_roid, uid, toKen;
    private Bundle bundle;
    public static View rl_bottom;
    public FaceRelativeLayout faceRelativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        application = (KXTApplication) getActivity().getApplication();

        i = Integer.parseInt(SPUtils.getString(getContext(), SpConstant.USERINFO_LIMIT_CHAT_TIME)) + 1;

        from_name = SPUtils.getString(getContext(), SpConstant.USERINFO_NAME);
        from_roid = SPUtils.getString(getContext(), SpConstant.USERINFO_RID);
        uid = SPUtils.getString(getContext(), SpConstant.USERINFO_UID);

        queue = application.getQueue();

        if (application.getChatMsgEntities() == null) {
            GetHostoryData();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.layout_chat, null);
        Log.i("fragment", "onCreateView");
        initView();
        if (bundle != null) {
            mDataArrays = getSavedInstanceState(bundle);// 得到Fragment销毁时保存的数据
        }
        if (mAdapter == null) {
            mAdapter = new ChatMsgAdapter(getActivity(), new ArrayList<>(mDataArrays), mListView);
            mListView.setAdapter(mAdapter);
            mListView.setSelection(mAdapter.getCount() - 1);
        }
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rl_bottom.setVisibility(View.GONE);
                try {
                    ((GotyeLiveActivity) activity).createFloatView();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        ((GenseeActivity) activity).createFloatView();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                return false;
            }
        });

        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        Log.i("fragment", "onDestroyView");
        super.onDestroyView();
        try {
            application.setChatMsgEntities(mAdapter.getColl());
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().unregister(this);
    }

    private void getToken() {
        toKen = SPUtils.getString(getContext(), SpConstant.USERINFO_TOKEN);
        if (LoginInfoUtils.isLogin(getContext())) {
            from_name = SPUtils.getString(getContext(), SpConstant.USERINFO_LOGIN_NAME);
            from_roid = SPUtils.getString(getContext(), SpConstant.USERINFO_LOGIN_RID);
            uid = SPUtils.getString(getContext(), SpConstant.USERINFO_LOGIN_UID);
        } else {
            uid = SPUtils.getString(getContext(), SpConstant.USERINFO_UID);
            from_name = SPUtils.getString(getContext(), SpConstant.USERINFO_NAME);
            from_roid = SPUtils.getString(getContext(), SpConstant.USERINFO_RID);
        }
    }

    private List<ChatMsgEntity> getSavedInstanceState(Bundle savedInstanceState) {
        List<ChatMsgEntity> chatMsgEntities = null;
        if (savedInstanceState != null) {
            List<ChatMsgEntity> chatMsgsStr = savedInstanceState.getParcelableArrayList("chatMsg");
            int i, len = chatMsgsStr.size();
            chatMsgEntities = new ArrayList<>();
            for (i = 0; i < len; i++) {
                try {
                    ChatMsgEntity str = chatMsgsStr.get(i);
                    chatMsgEntities.add(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ((KXTApplication) (getActivity().getApplication())).setChatMsgEntities(chatMsgEntities);
        }
        return chatMsgEntities;
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    if (i == 0 || i < 0) {
                        timer.cancel();
                        timer.purge();
                        mBtnSend.setVisibility(View.VISIBLE);
                        mtv_send.setVisibility(View.GONE);
                        try {
                            i = Integer.parseInt(SPUtils.getString(getContext(), SpConstant.USERINFO_LIMIT_CHAT_TIME)) + 1;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            i = 15;
                        } catch (NullPointerException e){
                            i=15;
                        }
                        if(faceRelativeLayout.getIsCaitiao()){
                            faceRelativeLayout.setCansend(true);
                        }
                    }
                    i = i - 1;
                    mtv_send.setText("" + i);
                    break;
                case 3:
                    ToastView.makeText(getActivity(), "输入字数超过范围");
                    break;
                case 100:
                    if (mDataArrays.size() > 50) {
                        mDataArrays.remove(0);
                    }
                    mListView.setSelection(mDataArrays.size() - 1);
                    break;
                case 101:
                    mAdapter = null;
                    if (mAdapter == null) {
                        mAdapter = new ChatMsgAdapter(getActivity(), new ArrayList<>(mDataArrays), mListView);
                        mListView.setAdapter(mAdapter);
                        mListView.setSelection(mAdapter.getCount() - 1);
                    }
                    break;
            }
        }

    };
    private String contString;
    private Activity activity;
    private RequestQueue queue;
    private String msgType;

    public void initView() {
        mDataArrays = new ArrayList<>();

        mListView = (MyListView) view.findViewById(R.id.listview);
        faceRelativeLayout = (FaceRelativeLayout) (view.findViewById(R.id.layout));
        mListView.setFaceRelativeLayout(faceRelativeLayout);

        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        float size = SystemUtils.getDpi(getActivity());
        WindowManager.LayoutParams p = getActivity().getWindow().getAttributes(); // 获取对话框当前的参数值
        params.height = (int) ((d.getHeight() - (float) 40 * size - SystemUtils
                .getStatuBarHeight(getActivity())) / 2.75 * 1.75);
        mListView.setLayoutParams(params);

        mBtnSend = (ImageView) view.findViewById(R.id.btn_send);
        mtv_send = (TextView) view.findViewById(R.id.tv_send);
        rl_bottom = view.findViewById(R.id.rl_bottom);

        mtv_send.setText("" + (i - 1));
        mBtnSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    faceRelativeLayout.close();
                    if (mEditTextContent.getText().toString().length() <= 120) {
                        try {
                            if (mEditTextContent.getText().toString().trim().equals("") || mEditTextContent.getText().toString().length()
                                    <= 0) {
                                ToastView.makeText(getActivity(), "内容为空");
                            } else {
                                send();
                                timer = new Timer();
                                setTimerTask();
                                mBtnSend.setVisibility(View.INVISIBLE);
                                mtv_send.setVisibility(View.VISIBLE);
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
        mEditTextContent = (EditText) view.findViewById(R.id.et_sendmessage);
        mEditTextContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    faceRelativeLayout.close();
                }
                return false;
            }
        });
        if (application.getChatMsgEntities() != null)
            mDataArrays.addAll(application.getChatMsgEntities());
        else if (bundle != null)
            mDataArrays.addAll(getSavedInstanceState(bundle));
    }

    private void setTimerTask() {
        i = Integer.parseInt(SPUtils.getString(getContext(), SpConstant.USERINFO_LIMIT_CHAT_TIME)) + 1;
        mtv_send.setText("" + (i - 1));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
            }
        }, 1 * 1000, 1 * 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }
    private void setTimerTaskTwo() {
        i = Integer.parseInt(SPUtils.getString(getContext(), SpConstant.USERINFO_LIMIT_COLORBAR_TIME)) + 1;
        mtv_send.setText("" + (i - 1));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
            }
        }, 1 * 1000, 1 * 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    private void send() {
        getToken();
        boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
        if (isOpen) {
            try {
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity()
                        .getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
            }
        }
        contString = mEditTextContent.getText().toString();
        msgType = null;
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            List<String> strings = application.getCaitiaos();
            boolean isCaitiao = false;
            for (String string : strings) {
                if (contString.contains(string))
                    isCaitiao = true;
            }
            if (isCaitiao) {
                entity.setData((":caitiao" + contString).replace(" ", ""));
                contString = (":caitiao" + contString).replace(" ", "");
                msgType = "caitiao";
            } else {
                entity.setData(contString);
                msgType = "public";
            }

            entity.setIs_checked("1");
            entity.setT_uid("");
            entity.setT_rid("");
            entity.setT_name("");
            entity.setF_name(from_name);
            entity.setF_rid(from_roid);
            entity.setF_uid(uid);

            entity.setTime("" + System.currentTimeMillis() / 1000);
            mDataArrays.add(entity);
            mAdapter.add(entity);
            handler.sendEmptyMessage(100);
            mEditTextContent.setText("");
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
        map.put("f_uid", uid);
        map.put("f_name", from_name);
        map.put("f_rid", from_roid);
        map.put("t_uid", "0");
        map.put("t_name", "");
        map.put("t_rid", "0");
        map.put("data", contString);
        map.put("time", "" + System.currentTimeMillis() / 1000);
        map.put("type", msgType);

        NormalPostRequest postRequest = new NormalPostRequest(UrlConstant.URL_SEND, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                try {
                    if ("200".equals(arg0.getString("code"))) {
                        ToastView.makeText(application, "消息发送成功");
                        try {
                            if (((GotyeLiveActivity) activity).mFloatLayout != null) {
                                ((GotyeLiveActivity) activity).mFloatLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                if (((GenseeActivity) activity).mFloatLayout != null) {
                                    ((GenseeActivity) activity).mFloatLayout.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        rl_bottom.setVisibility(View.GONE);

                        if(faceRelativeLayout.getIsCaitiao()){
                            timer = new Timer();
                            setTimerTaskTwo();
                            mBtnSend.setVisibility(View.INVISIBLE);
                            mtv_send.setVisibility(View.VISIBLE);
                        }


                    } else if ("401".equals(arg0.getString("code"))) {
                        faceRelativeLayout.setCansend(true);
                        ToastView.makeText(application, "消息发送失败,Token已过期");
                    } else {
                        faceRelativeLayout.setCansend(true);
                        ToastView.makeText(application, "消息发送失败," + arg0.getString("msg"));
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                faceRelativeLayout.setCansend(true);
                ToastView.makeText(application, "消息发送失败," + arg0);
            }
        }, map);
        queue.add(postRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getToken();
        try {
            faceRelativeLayout.onReflshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (iscon) {
            handler.sendEmptyMessage(101);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        iscon = true;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<ChatMsgEntity> chatMsgs = mDataArrays;
        ArrayList<ChatMsgEntity> chatMsgsStr = new ArrayList<ChatMsgEntity>();
        int i, len = chatMsgs.size();
        for (i = 0; i < len; i++) {
            try {
                chatMsgsStr.add(chatMsgs.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        outState.putParcelableArrayList("chatMsg", chatMsgsStr);
        ((KXTApplication) getActivity().getApplication()).setChatMsgEntities(mDataArrays);
        bundle = outState;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.activity = activity;
    }

    /**
     * 获取历史信息
     */

    private List<ChatMsgEntity> chatMsgEntities1 = null;

    private void GetHostoryData() {

        chatMsgEntities1 = new ArrayList<>();

        JsonObjectRequest request = new JsonObjectRequest(UrlConstant.URL_CHATHISTORY, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);

                        ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
                        if (object.getString("f_name") != null && !object.getString("f_name").equals("") && object.getString("t_name") !=
                                null) {

                            chatMsgEntity.setData(object.getString("data"));
                            chatMsgEntity.setIs_checked(object.getString("is_checked"));
                            chatMsgEntity.setT_uid(object.getString("t_uid"));
                            chatMsgEntity.setT_rid(object.getString("t_rid"));
                            chatMsgEntity.setT_name(object.getString("t_name"));
                            chatMsgEntity.setF_name(object.getString("f_name"));
                            chatMsgEntity.setF_rid(object.getString("f_rid"));
                            chatMsgEntity.setF_uid(object.getString("f_uid"));
                            chatMsgEntity.setTime(object.getString("time"));
                            chatMsgEntity.setId(object.getString("id"));

                            chatMsgEntities1.add(chatMsgEntity);
                            application.setChatMsgEntities(chatMsgEntities1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(request);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusBean bean) {

        try {
            if (bean == EventBusBean.NEW_CHATMSG) {
                Bundle bundle = (Bundle) bean.getObject();
                String chatType = bundle.getString("type");
                String f_name = bundle.getString("f_name");
                String f_rid = bundle.getString("f_rid");
                String f_uid = bundle.getString("f_uid");
                if (chatType != null && chatType.equals("private")) {
                    //私聊
                    UserBean userBean = new UserBean(f_name, f_uid, f_rid);
                    if (activity instanceof GenseeActivity) {
                        ((GenseeActivity) activity).showPrivateBtn(userBean);
                    } else if (activity instanceof GotyeLiveActivity) {
                        ((GotyeLiveActivity) activity).showPrivateBtn(userBean);
                    }
                    return;
                }
                ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
                chatMsgEntity.setData(bundle.getString("data"));
                chatMsgEntity.setIs_checked(bundle.getString("is_checked"));
                chatMsgEntity.setT_uid(bundle.getString("t_uid"));
                chatMsgEntity.setT_rid(bundle.getString("t_rid"));
                chatMsgEntity.setT_name(bundle.getString("t_name"));
                chatMsgEntity.setF_name(f_name);
                chatMsgEntity.setF_rid(f_rid);
                chatMsgEntity.setF_uid(f_uid);
                chatMsgEntity.setTime(bundle.getString("time"));
                chatMsgEntity.setId(bundle.getString("id"));
                if (!uid.equals(f_uid)) {
                    mDataArrays.add(chatMsgEntity);
                    mAdapter.add(chatMsgEntity);
                    //						Collections.sort(mDataArrays, new ComparatorChatMsg());
                }
                handler.sendEmptyMessage(100);
            } else if (bean == EventBusBean.DEL_CHATMSG) {
                Bundle bundle = (Bundle) bean.getObject();
                String id = bundle.getString("id");
                List<ChatMsgEntity> coll = mAdapter.getColl();
                Iterator<ChatMsgEntity> iterator = coll.iterator();
                while (iterator.hasNext()) {
                    ChatMsgEntity next = iterator.next();
                    if (id.equals(next.getId())) {
                        iterator.remove();
                        break;
                    }
                }
                mDataArrays.clear();
                mDataArrays.addAll(coll);
                handler.sendEmptyMessage(100);
            } else if (bean == EventBusBean.SEND_CHATMSG) {
                send();
                handler.sendEmptyMessage(100);
            } else if (bean == EventBusBean.USERINFO_CHANGE) {
                ChatService.wsc.disconnect();
                ChatService.wsc.reconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//	public class comparatorchatmsg implements comparator {
//
//		public int compare(object arg0, object arg1) {
//			chatmsgentity user0 = (chatmsgentity) arg0;
//			chatmsgentity user1 = (chatmsgentity) arg1;
//
//			if (user1 == null || user0 == null)
//				return 0;
//			if (user1.gettime() == null || user0.gettime() == null)
//				return 0;
//			int flag = user0.gettime().compareto(user1.gettime());
//			if (flag == 0) {
//				if (user1.getid() == null || user0.getid() == null)
//					return 0;
//				return user0.getid().compareto(user1.getid());
//			} else {
//				return flag;
//			}
//		}
//
//	}
}
