package com.jyh.gxcjzbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.jyh.gxcjzbs.bean.KXTApplication;
import com.jyh.gxcjzbs.common.constant.UrlConstant;
import com.jyh.gxcjzbs.common.utils.NetworkCenter;
import com.jyh.gxcjzbs.common.utils.SystemUtils;
import com.jyh.gxcjzbs.common.utils.LoginInfoUtils;
import com.jyh.gxcjzbs.common.utils.volleyutil.NormalPostRequest;
import com.jyh.gxcjzbs.common.utils.ToastView;
import com.jyh.gxcjzbs.common.utils.dialogutils.BounceTopEnter;
import com.jyh.gxcjzbs.common.utils.dialogutils.NormalDialog;
import com.jyh.gxcjzbs.common.my_interface.OnBtnClickL;
import com.jyh.gxcjzbs.common.utils.dialogutils.SlideBottomExit;
import com.jyh.gxcjzbs.sqlte.SCDataSqlte;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 强制登录界面
 *
 * @author Administrator
 */
public class Login_One extends Activity implements OnClickListener {

    private KXTApplication application;
    private RequestQueue queue;

    private String name, pwd;

    private EditText edit_name, edit_pwd;
    private Button login, register;
    private LinearLayout back;

    private String from;
    private boolean isFromLive;
    private BounceTopEnter bas_in;
    private SlideBottomExit bas_out;
    private NormalDialog dialog;
    private boolean isWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 透明状态栏
        getWindow().addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
        getWindow().addFlags(LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //删除聊天界面的悬浮按钮，防止本Activity透明
//        if (GotyeLiveActivity.live != null && !GotyeLiveActivity.live.isDestroyed() && ((GotyeLiveActivity) GotyeLiveActivity.live).isFloatViewShowing()) {
//            ((GotyeLiveActivity) GotyeLiveActivity.live).removeFloatView();
//        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        // 获取RequestQueue实例
        application = (KXTApplication) getApplication();
        application.addAct(this);
        queue = application.getQueue();

        from = getIntent().getStringExtra("from");
        if (from != null) {
            if ("live".equals(from)) {
                isFromLive = true;
                isWelcome = false;
//                setDialogStyle();
            } else if ("welcome".equals(from)) {
                isWelcome = true;
                isFromLive = false;
            } else if ("self".equals(from)) {
                isFromLive = false;
                isWelcome = false;
            } else  if ("null".equals(from)) {
                isFromLive = true;
                isWelcome = false;
            } else {
                    isFromLive = false;
                    isWelcome = false;

            }
        } else {
            isFromLive = false;
            isWelcome = false;
        }
        findview();
    }

    private void findview() {
        // TODO Auto-generated method stub

        if (isFromLive) {
//            findViewById(R.id.title).setVisibility(View.GONE);
            findViewById(R.id.title).setBackgroundColor(Color.TRANSPARENT);
            findViewById(R.id.tab).setBackgroundResource(R.drawable.login_bg);
            findViewById(R.id.bg).setBackground(null);
//            findViewById(R.id.login_logo).setVisibility(View.GONE);
//            findViewById(R.id.loginone_register).setVisibility(View.GONE);
        } else if (isWelcome) {
//            findViewById(R.id.title).setVisibility(View.GONE);
            findViewById(R.id.title).setBackgroundColor(Color.TRANSPARENT);
            findViewById(R.id.tab).setBackgroundResource(R.drawable.login_bg);
            findViewById(R.id.bg).setBackground(null);
        } else {
            findViewById(R.id.title).setBackgroundColor(Color.rgb(17, 119, 224));
            findViewById(R.id.tab).setBackgroundColor(Color.rgb(17, 119, 224));
            findViewById(R.id.bg).setBackgroundResource(R.drawable.login_bg);
        }
        findViewById(R.id.title).setVisibility(View.VISIBLE);
        edit_name = (EditText) findViewById(R.id.login_name);
        edit_pwd = (EditText) findViewById(R.id.login_pwd);

        back = (LinearLayout) findViewById(R.id.self_fk_img);
        back.setOnClickListener(this);

        login = (Button) findViewById(R.id.loginone_login);
        register = (Button) findViewById(R.id.loginone_register);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    /**
     * 登录
     */
    private void Login() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("account", name);
        map.put("password", pwd);

        NormalPostRequest normalPostRequest = new NormalPostRequest(UrlConstant.URL_LOGIN, new Listener<JSONObject>() {

            private SCDataSqlte dataSqlte;

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                String code;
                try {
                    code = arg0.getString("code");
                    if ("200".equals(code)) {
                        // 登录成功
                        JSONObject data = arg0.getJSONObject("data");
                        // "token": "457cede2bc6aa7a3683af6ffd4cb5a19",
                        // "member_id": "1",
                        // "expired_time": 1462350254,
                        // "user_info": {
                        // "id": "1",
                        // "name": "青之羽",
                        // "rid": "17"

                        LoginInfoUtils.login(Login_One.this, data);

                        if (!isFromLive) {
                            Intent intent = new Intent(Login_One.this, MainActivity.class);
                            if ("self".equals(from))
                                intent.putExtra("enter", "self");
                            if ("zb".equals(from))
                                intent.putExtra("join", true);
                            startActivity(intent);
                            superFinish();
                        } else {
                            finish();
                        }
                    } else {
                        // 登录失败,
                        ToastView.makeText(application, "登录失败," + arg0.getString("msg"));
                    }
                } catch (JSONException e) {
                    // 登录失败
                    ToastView.makeText(application, "登录失败," + e.toString());
                    e.printStackTrace();
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // 登录失败
                ToastView.makeText(application, "登录失败," + arg0);
            }
        }, map);
        queue.add(normalPostRequest);
    }

    private void superFinish() {
        super.finish();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.loginone_login:
                if(!NetworkCenter.checkNetworkConnection(this)){
                    ToastView.makeText(this,"当前无网络，请稍后再试");
                    return;
                }
                name = edit_name.getText().toString().trim();
                pwd = edit_pwd.getText().toString().trim();
                Login();
                break;
            case R.id.loginone_register:
                Intent intent = new Intent(this, Register_One.class);
                if (isFromLive) {
                    intent.putExtra("from", "live");
                } else if (isWelcome) {
                    intent.putExtra("from", "welcome");
                }
                startActivity(intent);
                break;
            case R.id.self_fk_img:
                onBackPressed();
                break;
        }
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
        getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isFromLive) {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                finish();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        if (isFromLive)
            hintKbTwo();
       /* if (GotyeLiveActivity.live != null) {
            GotyeLiveActivity.live.changeTitle();
        }
        if (GenseeActivity.live != null) {
            GenseeActivity.live.changeTitle();
        }*/
        if (LoginInfoUtils.needRequireLogin(this)) {

            bas_in = new BounceTopEnter();
            bas_out = new SlideBottomExit();
            dialog = new NormalDialog(this);
            dialog.isTitleShow(false)
                    // 设置背景颜色
                    .bgColor(Color.parseColor("#383838"))
                    // 设置dialog角度
                    .cornerRadius(5)
                    // 设置内容
                    .content("是否确定退出程序?")
                    // 设置居中
                    .contentGravity(Gravity.CENTER)
                    // 设置内容字体颜色
                    .contentTextColor(Color.parseColor("#ffffff"))
                    // 设置线的颜色
                    .dividerColor(Color.parseColor("#222222"))
                    // 设置字体
                    .btnTextSize(15.5f, 15.5f)
                    // 设置取消确定颜色
                    .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                    .btnPressColor(Color.parseColor("#2B2B2B"))//
                    .widthScale(0.85f)//
                    .showAnim(bas_in)//
                    .dismissAnim(bas_out)//
                    .show();

            dialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.dismiss();
                }
            }, new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.dismiss();
                    if (MainActivity.main != null && !MainActivity.main.isDestroyed())
                        MainActivity.main.finish();
                    application.exitAppAll();
                    System.exit(0);
                    System.gc();
                }
            });

        } else {
            if ("self".equals(from)) {
                if (MainActivity.main != null && !MainActivity.main.isDestroyed()) {
                    super.finish();
                } else {
                    Intent intent = new Intent(Login_One.this, MainActivity.class);
                    intent.putExtra("enter", "self");
                    intent.putExtra("isLoadImg", false);
                    startActivity(intent);
                    super.finish();
                }
            } else {
                super.finish();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
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
}
