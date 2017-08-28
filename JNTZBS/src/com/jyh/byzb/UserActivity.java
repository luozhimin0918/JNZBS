package com.jyh.byzb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.SPUtils;

public class UserActivity extends Activity implements OnClickListener {
    private LinearLayout changeName, changePwd, back, logout;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.setTheme(R.style.BrowserThemeDefault);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_user);
        findViewById(R.id.user_login).setVisibility(View.VISIBLE);
        findView();
    }

    private void findView() {
        // TODO Auto-generated method stub
        changeName = (LinearLayout) findViewById(R.id.self_change_name);
        changePwd = (LinearLayout) findViewById(R.id.self_change_pwd);
        back = (LinearLayout) findViewById(R.id.user_back);
        logout = (LinearLayout) findViewById(R.id.self_ll_out);

        name = SPUtils.getString(this, SpConstant.USERINFO_LOGIN_NAME);
        if (name != null)
            ((TextView) findViewById(R.id.self_username)).setText(name);

        back.setOnClickListener(this);
        changeName.setOnClickListener(this);
        changePwd.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.self_change_name:
                Intent intent = new Intent(this, ChangeNameActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.self_change_pwd:
                startActivity(new Intent(this, ChangePwdActivity.class));
                break;
            case R.id.user_back:
                finish();
                break;
            case R.id.self_ll_out:
                LoginInfoUtils.logout(this);
                if (LoginInfoUtils.needRequireLogin(this)) {
                    // 强制登录
                    Intent LoginIntent = new Intent(this, Login_One.class);
                    LoginIntent.putExtra("from", "self");
                    startActivity(LoginIntent);
                    if (MainActivity.main != null)
                        MainActivity.main.finish();
                    finish();
                } else
                    finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 666) {
            String name = SPUtils.getString(this, SpConstant.USERINFO_LOGIN_NAME);
            if (name != null)
                ((TextView) findViewById(R.id.self_username)).setText(name);
            setResult(666);
        }
    }
}
