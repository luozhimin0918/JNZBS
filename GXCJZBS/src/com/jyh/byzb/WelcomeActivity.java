package com.jyh.byzb;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jyh.byzb.basePack.BaseActivity;
import com.jyh.byzb.bean.ChatMsgEntity;
import com.jyh.byzb.bean.KXTApplication;
import com.jyh.byzb.bean.RoomRole;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.constant.UrlConstant;
import com.jyh.byzb.common.utils.imageutils.ImageDownLoader;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.volleyutil.NormalPostRequest;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.dialogutils.BounceTopEnter;
import com.jyh.byzb.common.utils.dialogutils.MaterialDialog;
import com.jyh.byzb.common.my_interface.OnBtnClickL;
import com.jyh.byzb.common.utils.dialogutils.SlideBottomExit;
import com.jyh.byzb.common.utils.NetworkCenter;
import com.jyh.byzb.common.utils.VersionManager;
import com.jyh.byzb.sqlte.SCDataSqlte;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelcomeActivity extends BaseActivity {

    private KXTApplication application;
    LinearLayout rl_splash;
    private VersionManager versionManager;
    private boolean isEnter = false;
    private static MaterialDialog testDialog;// 网络异常提示Dialog
    private BounceTopEnter bas_in;
    private SlideBottomExit bas_out;
    private RequestQueue queue;
    private boolean IsFirstError = true;
    private SCDataSqlte sqlOpenHelper;// 用以保存直播室相关信息
    protected boolean isLoadAD;// 判断是否有广告
    protected boolean isNeedLogin = false;// 是否强制登录
    public String load_ad_image;// 广告图片下载地址
    private String adurl2;// 广告图片点击跳转地址
    private boolean isFirstLoading = true;// 用以防止数据库重复读写

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        if (!isTastRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        setContentView(R.layout.activity_enter);
        MobclickAgent.setDebugMode(true);
        rl_splash = (LinearLayout) findViewById(R.id.rl_splash);
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();
        application = (KXTApplication) getApplication();
        application.addAct(this);
        queue = application.getQueue();

        handler.sendEmptyMessageDelayed(40, 10 * 1000);
        versionManager = VersionManager.getInstance();
        testDialog = new MaterialDialog(this);

        if (NetworkCenter.checkNetwork_JYH(this)) {
            versionManager.checkVersion(this, handler);
        } else {
            handler.sendEmptyMessageDelayed(40, 8 * 1000);
        }

    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 30:
//                    ToastView.makeText(WelcomeActivity.this, "获取版本信息异常");
                    TagsTask(UrlConstant.URL_INDEX, 5);
                    break;
                case 40:
                    if (!isEnter) {
                        if (!testDialog.isShowing()) {
                            testDialog//
                                    .btnNum(1).content("网络异常，请检查网络。")//
                                    .btnText("确定")//
                                    .showAnim(bas_in)//
                                    .dismissAnim(bas_out)//
                                    .show();
                            testDialog.setOnBtnClickL(new OnBtnClickL() {

                                @Override
                                public void onBtnClick() {
                                    // TODO Auto-generated method stub
                                    testDialog.dismiss();
                                    handler.sendEmptyMessageDelayed(90, 1000);
                                }
                            });
                            testDialog.setCanceledOnTouchOutside(false);
                        }
                    }
                    break;
                case 50:
                    if (!testDialog.isShowing()) {
                        testDialog.content("当前网络不稳定，请检查手机网络")//
                                .btnText("取消", "确定")//
                                .showAnim(bas_in)//
                                .dismissAnim(bas_out)//
                                .show();
                        testDialog.setOnBtnClickL(new OnBtnClickL() {// left btn
                            @Override
                            public void onBtnClick() {
                                testDialog.dismiss();
                                handler.sendEmptyMessageDelayed(90, 1000);
                            }
                        }, new OnBtnClickL() {// right btn click listener
                            @Override
                            public void onBtnClick() {
                                Intent intent = null;
                                // 先判断当前系统版本
                                if (android.os.Build.VERSION.SDK_INT > 10) { // 3.0以上
                                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                } else {
                                    intent = new Intent();
                                    intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
                                }
                                startActivity(intent);
                                testDialog.dismiss();
                                handler.sendEmptyMessageDelayed(90, 1000);

                            }
                        });
                        testDialog.setCanceledOnTouchOutside(false);
                    }
                    break;
                case 60:
                    // 启动更新
                    String description = SPUtils.getString(getApplicationContext(), SpConstant.VERSION_DESCRIPTION);
                    final String versionurl = SPUtils.getString(getApplicationContext(), SpConstant.VERSION_URL);
                    if (!testDialog.isShowing()) {
                        testDialog.content(description)//
                                .btnText("取消", "确定")//
                                .showAnim(bas_in)//
                                .dismissAnim(bas_out)//
                                .show();
                        testDialog.setOnBtnClickL(new OnBtnClickL() {// left btn
                            @Override
                            public void onBtnClick() {
                                handler.sendEmptyMessageDelayed(70, 2 * 1000);
                                testDialog.dismiss();
                            }
                        }, new OnBtnClickL() {// right btn click listener
                            @Override
                            public void onBtnClick() {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(versionurl));
                                startActivity(intent);
                                testDialog.dismiss();
                                handler.sendEmptyMessageDelayed(90, 1000);
                            }
                        });
                        testDialog.setCanceledOnTouchOutside(false);
                    }
                    break;
                case 70:
                    TagsTask(UrlConstant.URL_INDEX, 5);
                    break;
                case 80:
                    if (null != testDialog && testDialog.isShowing()) {
                        testDialog.dismiss();
                    }
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 90:
                    application.exitAppAll();
                default:
                    break;
            }
        }

        ;
    };

    private void TagsTask(final String url, final int i) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (testDialog.isShowing()) {
                    return;
                }
                ResolveData(response, i);
                IsFirstError = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                if (IsFirstError) {
                    TagsTask(url, i);
                    IsFirstError = false;
                } else {
                    isEnter = false;
                    handler.sendEmptyMessage(40);
                }
            }

        });
        queue.add(jsObjRequest);
    }

    public void ResolveData(JSONObject jsondata, int i) {
        try {
            switch (i) {
                case 5: {
                    // App配置信息
                    JSONObject job = jsondata;
                    JSONObject data = job.getJSONObject("data");
                    // appinfo 配置信息
                    JSONObject appinfoJob = data.getJSONObject("appinfo");
                    // 强制登录
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_REQUIRE_LOGIN, appinfoJob.getString("require_login"));
                    // 不用强制登录
                    // SPUtils.save(getApplicationContext(),"require_login", "");

                    // loadAd 广告
                    JSONObject loadAd = data.getJSONObject("load_ad");

                    Log.i("url", "loadad=" + loadAd.toString());
                    adurl2 = loadAd.getString("url");
                    load_ad_image = loadAd.getString("image");

                    if (load_ad_image != null && !"".equals(load_ad_image))
                        isLoadAD = true;
                    else
                        isLoadAD = false;

                    // userinfo 用户信息
                    JSONObject userinfoJob = data.getJSONObject("userinfo");
                    // videoinfo 直播室信息
                    JSONObject videoinfoJob = data.getJSONObject("video_info2");
                    JSONObject detailJob = null;
                    JSONObject qinjia = null;
                    if (videoinfoJob.getString("type").equals("live_gensee")) {
                        detailJob = videoinfoJob.getJSONObject("live_gensee");
                    } else if (videoinfoJob.getString("type").equals("live_108")) {
                        qinjia = videoinfoJob.getJSONObject("live_108");
                    }
                    // roomrole 用户角色信息
                    JSONArray roomrolesJoA = data.getJSONArray("roomrole");
                    List<RoomRole> roomRoles = new ArrayList<>();

                    JSONObject job2;

                    for (int i1 = 0; i1 < roomrolesJoA.length(); i1++) {
                        job2 = roomrolesJoA.getJSONObject(i1);
                        RoomRole roleRoomRole = new RoomRole();
                        roleRoomRole.setId(job2.getString("id"));
                        roleRoomRole.setName(job2.getString("name"));
                        roleRoomRole.setType(job2.getString("type"));
                        roleRoomRole.setLimit_chat_time(job2.getString("limit_chat_time"));
                        roleRoomRole.setPower_whisper(job2.getString("power_whisper"));
                        roleRoomRole.setLimit_colorbar_time(job2.getString("limit_colorbar_time"));
                        roleRoomRole.setPower_upload_pic(job2.getString("power_upload_pic"));
                        roleRoomRole.setLimit_account_time(job2.getString("limit_account_time"));
                        roleRoomRole.setStatus(job2.getString("status"));
                        roleRoomRole.setSort(job2.getString("sort"));
                        roleRoomRole.setPower_visit_room(job2.getString("power_visit_room"));
                        roleRoomRole.setStyle_chat_text(job2.getString("style_chat_text"));
                        roleRoomRole.setImage(job2.getString("image"));

                        roomRoles.add(roleRoomRole);
                        new ImageDownLoader(WelcomeActivity.this).loadImage(job2.getString("image"), null);
                    }

                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_APPID, appinfoJob.getString("appid"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_APPNAME, appinfoJob.getString("name"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_GATE, appinfoJob.getString("gate"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_KEFU_URL, appinfoJob.getString("kefu_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_USERLIST_URL, appinfoJob.getString("userlist_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_IMAGES_URL, appinfoJob.getString("images_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_COURSE_URL, appinfoJob.getString("course_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_SUMMARY_URL, appinfoJob.getString("summary_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_BULLETIN_URL, appinfoJob.getString("bulletin_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_FN_NAV_URL, appinfoJob.getString("fn_nav_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_FN_URL, appinfoJob.getString("fn_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_CJRL_URL, appinfoJob.getString("cjrl_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_ALTERS_URL, appinfoJob.getString("alters_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_HQ_URL, appinfoJob.getString("hq_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_UPLOAD_IMAGES_URL, appinfoJob.getString("upload_images_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_REGISTER_URL, appinfoJob.getString("register_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_CL_URL, appinfoJob.getString("tactics_url"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_CL_BAN, appinfoJob.getString("tactics_ban_rid"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_CL_BAN, appinfoJob.getString("tactics_ban_rid"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_INDEX_BG, appinfoJob.getString("index_bg"));
                    SPUtils.save(getApplicationContext(), SpConstant.APPINFO_EMOJI_VERSION_NEW, appinfoJob.getString("phiz_version"));

                    SPUtils.save(getApplicationContext(), SpConstant.USERINFO_NAME, userinfoJob.getString("name"));
                    SPUtils.save(getApplicationContext(), SpConstant.USERINFO_RID, userinfoJob.getString("rid"));
                    SPUtils.save(getApplicationContext(),SpConstant.USERINFO_LOGIN_RID,userinfoJob.getString("rid"));
                    SPUtils.save(getApplicationContext(), SpConstant.USERINFO_UID, userinfoJob.getString("id"));

                    SPUtils.save(getApplicationContext(), SpConstant.VIDEO_TYPE, videoinfoJob.getString("type"));
                    if (videoinfoJob.getString("type").equals("live_108")) {
                        if (qinjia != null || !"".equals(qinjia)) {
                            // gensee
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_ID, "");
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_ROOMID, "");
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_PWD, "");
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_SITE, "");
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_CTXZ, "");
                            // Gotye
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GOTYEROOMID, qinjia.getString("ROOMID"));
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GOTYEPASSWORD, qinjia.getString("PASSWORD"));
                        }
                    } else {
                        if (detailJob != null || !"".equals(detailJob)) {
                            // gensee
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_ID, detailJob.getString("ID"));
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_ROOMID, detailJob.getString("ROOMID"));
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_PWD, detailJob.getString("PASSWORD"));
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_SITE, detailJob.getString("SITE"));
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GENSEE_CTXZ, detailJob.getString("CTX"));
                            // Gotye
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GOTYEROOMID, "");
                            SPUtils.save(getApplicationContext(), SpConstant.VIDEO_GOTYEPASSWORD, "");
                        }
                    }

                    if (isFirstLoading) {
                        // 防止多次存储数据
                        sqlOpenHelper = new SCDataSqlte(WelcomeActivity.this);
                        SQLiteDatabase dbw = sqlOpenHelper.getWritableDatabase();

                        Map<String, String> map = new HashMap<String, String>();
                        for (int i1 = 0; i1 < roomRoles.size(); i1++) {

                            RoomRole roomRole = roomRoles.get(i1);
                            if ("1".equals(roomRole.getId())) {
                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_R_NAME, roomRole.getName());
                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_LIMIT_CHAT_TIME, roomRole.getLimit_chat_time());
                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_POWER_PRIVATE, roomRole.getPower_whisper());
                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_LIMIT_COLORBAR_TIME, roomRole.getLimit_colorbar_time());
                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_IMAGE, roomRole.getImage());
                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_POWER_VISIT_ROOM, roomRole.getPower_visit_room());
                            }
                            dbw.execSQL(
                                    "insert into roomrole (id,name,type, limit_chat_time, power_whisper,"
                                            + "limit_colorbar_time,power_upload_pic,limit_account_time,"
                                            + "status,sort,power_visit_room,style_chat_text,image) values (?,?,?,?,?,?,?,?,?,?,?,?,?);",
                                    new Object[]{roomRole.getId(), roomRole.getName(), roomRole.getType(), roomRole.getLimit_chat_time(),
                                            roomRole.getPower_whisper(), roomRole.getLimit_colorbar_time(), roomRole.getPower_upload_pic(),
                                            roomRole.getLimit_account_time(), roomRole.getStatus(), roomRole.getSort(),
                                            roomRole.getPower_visit_room(), roomRole.getStyle_chat_text(), roomRole.getImage()});
                        }
                        dbw.close();
                        isFirstLoading = false;
                    }

                    String token = SPUtils.getString(getApplicationContext(), SpConstant.USERINFO_TOKEN);

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("token", token == null ? "" : token);

                    // 获取用户登录信息
                    NormalPostRequest normalPostRequest = new NormalPostRequest(UrlConstant.URL_USERINFO,
                            new Listener<JSONObject>() {

                                private SQLiteDatabase dbw;

                                @Override
                                public void onResponse(JSONObject arg0) {
                                    // TODO Auto-generated method stub
                                    String code;
                                    try {
                                        code = arg0.getString("code");
                                        if ("200".equals(code)) {
                                            JSONObject data = arg0.getJSONObject("data");
                                            SPUtils.save(getApplicationContext(), SpConstant.USERINFO_LOGIN_NAME, data.getString("name"));
                                            SPUtils.save(getApplicationContext(), SpConstant.USERINFO_LOGIN_UID, data.getString("id"));
                                            SPUtils.save(getApplicationContext(), SpConstant.USERINFO_LOGIN_RID, data.getString("rid"));

                                            dbw = sqlOpenHelper.getWritableDatabase();
                                            Cursor cursor = dbw.rawQuery("select * from roomrole where id=?",
                                                    new String[]{data.getString("rid")});
                                            while (cursor.moveToNext()) {
                                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_R_NAME, cursor.getString(cursor.getColumnIndex("name")));
                                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_LIMIT_CHAT_TIME, cursor.getString(cursor.getColumnIndex("limit_chat_time")));
                                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_LIMIT_COLORBAR_TIME, cursor.getString(cursor.getColumnIndex("limit_colorbar_time")));
                                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_IMAGE, cursor.getString(cursor.getColumnIndex("image")));
                                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_POWER_VISIT_ROOM, cursor.getString(cursor.getColumnIndex("power_visit_room")));
                                                SPUtils.save(getApplicationContext(), SpConstant.USERINFO_POWER_PRIVATE, cursor.getString(cursor.getColumnIndex("power_whisper")));
                                            }
                                            cursor.close();
                                            dbw.close();
                                        } else {
                                            // 获取登录信息失败
                                            isNeedLogin = LoginInfoUtils.needRequireLogin(getApplicationContext());
                                        }
                                    } catch (JSONException e) {
                                        // 获取登录信息失败
                                        isNeedLogin = LoginInfoUtils.needRequireLogin(getApplicationContext());
                                        e.printStackTrace();
                                    }
                                    TagsTask(UrlConstant.URL_CHATHISTORY, 6);
                                }
                            }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            // 获取登录信息失败
                            isNeedLogin = LoginInfoUtils.needRequireLogin(getApplicationContext());
                            TagsTask(UrlConstant.URL_CHATHISTORY, 6);
                        }
                    }, map);
                    queue.add(normalPostRequest);
                    break;
                }
                case 6: {
                    // 历史聊天记录
                    Log.i("info", "history=" + jsondata);
                    JSONArray array1 = jsondata.getJSONArray("data");
                    List<ChatMsgEntity> chatMsgEntities = new ArrayList<ChatMsgEntity>();
                    for (int i1 = 0; i1 < array1.length(); i1++) {
                        JSONObject object1 = (JSONObject) array1.get(i1);
                        Log.i("info1", object1.toString());
                        ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
                        if (object1.getString("f_name") != null && !object1.getString("f_name").equals("")) {
                            chatMsgEntity.setData(object1.getString("data"));
                            chatMsgEntity.setIs_checked("");
                            chatMsgEntity.setT_uid(object1.getString("t_uid"));
                            chatMsgEntity.setT_rid(object1.getString("t_rid"));
                            chatMsgEntity.setT_name(object1.getString("t_name"));
                            chatMsgEntity.setF_name(object1.getString("f_name"));
                            chatMsgEntity.setF_rid(object1.getString("f_rid"));
                            chatMsgEntity.setF_uid(object1.getString("f_uid"));
                            chatMsgEntity.setTime(object1.getString("time"));
                            chatMsgEntity.setId(object1.getString("id"));
                            chatMsgEntities.add(chatMsgEntity);

                        }
                    }
                    application.setChatMsgEntities(chatMsgEntities);
                    final Intent Mainintent = new Intent(WelcomeActivity.this, MainActivity.class);

                    if (!isLoadAD) {
                        // 不用加载广告
                        if (isNeedLogin) {
                            // 强制登录
                            Intent LoginIntent = new Intent(WelcomeActivity.this, Login_One.class);
                            LoginIntent.putExtra("from", "welcome");
                            startActivity(LoginIntent);
                            finish();
                        } else {
                            // 不用强制登录
                            startActivity(Mainintent);
                            finish();
                        }
                    } else
                        // 加载广告
                        if (load_ad_image != null && !"".equals(load_ad_image)) {
                            Load_ad();
                        } else {
                            if (isNeedLogin) {
                                // 强制登录
                                Intent LoginIntent = new Intent(WelcomeActivity.this, Login_One.class);
                                LoginIntent.putExtra("from", "welcome");
                                startActivity(LoginIntent);
                                finish();
                            } else {
                                // 不用强制登录
                                startActivity(Mainintent);
                                finish();
                            }
                        }

                    break;
                }
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Response", "异常");
        }
    }

    /*
     * 广告加载
     */
    private void Load_ad() {
        Intent intent = new Intent(WelcomeActivity.this, AdActivity.class);
        Log.i("url", "image=" + load_ad_image + " url=" + adurl2);
        intent.putExtra("image", load_ad_image);
        intent.putExtra("url", adurl2);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        testDialog = null;
        super.onDestroy();
    }


    public void onPause() {
        super.onPause();
        isEnter = true;
    }

    /**
     * 判断mainactivity是否处于栈顶
     *
     * @return true在栈顶false不在栈顶
     */
    private boolean isTastRoot() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(WelcomeActivity.class.getName());
    }

}
