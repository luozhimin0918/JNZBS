package com.jyh.gxcjzbs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.gxcjzbs.common.constant.SpConstant;
import com.jyh.gxcjzbs.common.constant.UrlConstant;
import com.jyh.gxcjzbs.common.utils.NetworkCenter;
import com.jyh.gxcjzbs.common.utils.volleyutil.NormalPostRequest;
import com.jyh.gxcjzbs.common.utils.SPUtils;
import com.jyh.gxcjzbs.common.utils.ToastView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePwdActivity extends Activity implements OnClickListener {

    private EditText old_pwd, new_pwd, re_pwd;
    private Button btn;
    private String token;
    private LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        this.setTheme(R.style.BrowserThemeDefault);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_changepwd);
        bindView();

    }

    private void bindView() {
        // TODO Auto-generated method stub
        old_pwd = (EditText) findViewById(R.id.changepwd_old);
        new_pwd = (EditText) findViewById(R.id.changepwd_new);
        re_pwd = (EditText) findViewById(R.id.changepwd_re);
        back = (LinearLayout) findViewById(R.id.changepwd_back);
        btn = (Button) findViewById(R.id.changepwd_btn);

        btn.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.changepwd_btn:
                if(!NetworkCenter.checkNetworkConnection(this)){
                    ToastView.makeText(this,"当前无网络，请稍后再试");
                    return;
                }
                if (!checkEdit())
                    return;
                ChangePwd();
                break;
            case R.id.changepwd_back:
                finish();
            default:
                break;
        }
    }

    /**
     * 提交新密码
     */
    protected void ChangePwd() {
        // TODO Auto-generated method stub

        String changpwdUrl = UrlConstant.URL_EDITPWD;

        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("oldpass", old_pwd.getText().toString().trim());
        map.put("newpass", new_pwd.getText().toString().trim());
        map.put("repeatpass", re_pwd.getText().toString().trim());

        NormalPostRequest normalPostRequest = new NormalPostRequest(changpwdUrl, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                try {
                    if ("200".equals(arg0.getString("code"))) {
                        ToastView.makeText(ChangePwdActivity.this, "密码更改成功");
                    } else
                        ToastView.makeText(ChangePwdActivity.this, arg0.getString("msg"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                ToastView.makeText(ChangePwdActivity.this, "密码不合法" + arg0);
            }
        }, map);

        Volley.newRequestQueue(this).add(normalPostRequest);
    }

    /**
     * 检查输入是否合法
     *
     * @return
     */
    private boolean checkEdit() {
        token = SPUtils.getString(this, SpConstant.USERINFO_TOKEN);
        String new_pwdStr = new_pwd.getText().toString().trim();
        int pwd_length = new_pwdStr.length();
        if (old_pwd.getText().toString().trim().equals("")) {
            ToastView.makeText(ChangePwdActivity.this, "密码不能为空");
        } else if (new_pwdStr.equals("")) {
            ToastView.makeText(ChangePwdActivity.this, "密码不能为空");
        } else if (pwd_length < 6 || pwd_length > 32) {
            ToastView.makeText(ChangePwdActivity.this, "密码最少6个字符，最多32个字符");
        } else if (new_pwdStr.equals(old_pwd.getText().toString().trim())) {
            ToastView.makeText(ChangePwdActivity.this, "新密码不能与旧密码一样");
        } else if (!new_pwdStr.equals(re_pwd.getText().toString().trim())) {
            ToastView.makeText(ChangePwdActivity.this, "两次密码输入不一致");
        } else if (token == null) {
            ToastView.makeText(ChangePwdActivity.this, "登录已失效，请重新登录");
        } else {
            return true;
        }
        return false;
    }
}
