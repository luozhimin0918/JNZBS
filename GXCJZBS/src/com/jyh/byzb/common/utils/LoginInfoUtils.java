package com.jyh.byzb.common.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jyh.byzb.bean.EventBusBean;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.sqlte.SCDataSqlte;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 项目名:CJDJCZBS
 * 类描述:登录工具类
 * 创建人:苟蒙蒙
 * 创建日期:2016/11/28.
 */

public class LoginInfoUtils {

    /**
     * 是否已登录
     *
     * @param context
     * @return
     */
    public static boolean isLogin(Context context) {
        String token = SPUtils.getString(context, SpConstant.USERINFO_TOKEN);
        Long time = SPUtils.getLong(context, SpConstant.USERINFO_EXPIRED_TIME);//token过期时间戳

        if (token == null || token.trim().equals(""))
            return false;
        if (time > (System.currentTimeMillis() / 1000)) {
            return true;
        }
        return false;
    }

    /**
     * 登录后保存信息
     *
     * @param context
     * @param data
     */
    public static void login(Context context, JSONObject data) {
        try {
            SPUtils.save(context, SpConstant.USERINFO_TOKEN, data.getString("token"));
            SPUtils.save(context, SpConstant.USERINFO_EXPIRED_TIME, data.getLong("expired_time"));

            JSONObject user_info = data.getJSONObject("user_info");

            SPUtils.save(context, SpConstant.USERINFO_LOGIN_NAME, user_info.getString("name"));
            SPUtils.save(context, SpConstant.USERINFO_LOGIN_UID, user_info.getString("id"));
            SPUtils.save(context, SpConstant.USERINFO_LOGIN_RID, user_info.getString("rid"));
            SPUtils.save(context, SpConstant.GLOBAL_ISFIRSTWTG, true);

            setRoleInfo(context, user_info);

            EventBus.getDefault().post(EventBusBean.USERINFO_CHANGE);
        } catch (JSONException e) {
            ToastView.makeText(context, "登录失败");
            logout(context);
            e.printStackTrace();
        }
    }

    /**
     * 设置当前账号对应的角色信息
     *
     * @param context
     * @param user_info
     * @throws JSONException
     */
    private static void setRoleInfo(Context context, JSONObject user_info) throws JSONException {
        SCDataSqlte dataSqlte = new SCDataSqlte(context);
        SQLiteDatabase db = dataSqlte.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from roomrole where id=?", new String[]{user_info.getString("rid")});
        while (cursor.moveToNext()) {
            SPUtils.save(context, SpConstant.USERINFO_R_NAME, cursor.getString(cursor.getColumnIndex("name")));
            SPUtils.save(context, SpConstant.USERINFO_LIMIT_CHAT_TIME, cursor.getString(cursor.getColumnIndex("limit_chat_time")));
            SPUtils.save(context, SpConstant.USERINFO_LIMIT_COLORBAR_TIME, cursor.getString(cursor.getColumnIndex("limit_colorbar_time")));
            SPUtils.save(context, SpConstant.USERINFO_POWER_VISIT_ROOM, cursor.getString(cursor.getColumnIndex("power_visit_room")));
            SPUtils.save(context, SpConstant.USERINFO_POWER_PRIVATE, cursor.getString(cursor.getColumnIndex("power_whisper")));
            SPUtils.save(context, SpConstant.USERINFO_IMAGE, cursor.getString(cursor.getColumnIndex("image")));
        }
        cursor.close();
        db.close();
    }

    /**
     * 退出登录
     *
     * @param context
     */
    public static void logout(Context context) {

        SPUtils.save(context, SpConstant.USERINFO_TOKEN, null);
        SPUtils.save(context, SpConstant.USERINFO_EXPIRED_TIME, null);
        SPUtils.save(context, SpConstant.USERINFO_LOGIN_UID, SPUtils.getString(context, SpConstant.USERINFO_UID));
        SPUtils.save(context, SpConstant.USERINFO_LOGIN_RID, SPUtils.getString(context, SpConstant.USERINFO_RID));
        SPUtils.save(context, SpConstant.USERINFO_LOGIN_NAME, SPUtils.getString(context, SpConstant.USERINFO_NAME));
        SPUtils.save(context, SpConstant.GLOBAL_ISFIRSTWTG, true);
        SCDataSqlte dataSqlte = new SCDataSqlte(context);
        SQLiteDatabase db = dataSqlte.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from roomrole where id=?", new String[]{"1"});
        while (cursor.moveToNext()) {
            SPUtils.save(context, SpConstant.USERINFO_R_NAME, cursor.getString(cursor.getColumnIndex("name")));
            SPUtils.save(context, SpConstant.USERINFO_LIMIT_CHAT_TIME, cursor.getString(cursor.getColumnIndex("limit_chat_time")));
            SPUtils.save(context, SpConstant.USERINFO_LIMIT_COLORBAR_TIME, cursor.getString(cursor.getColumnIndex("limit_colorbar_time")));
            SPUtils.save(context, SpConstant.USERINFO_POWER_VISIT_ROOM, cursor.getString(cursor.getColumnIndex("power_visit_room")));
            SPUtils.save(context, SpConstant.USERINFO_POWER_PRIVATE, cursor.getString(cursor.getColumnIndex("power_whisper")));
            SPUtils.save(context, SpConstant.USERINFO_IMAGE, cursor.getString(cursor.getColumnIndex("image")));
        }
        cursor.close();
        db.close();
        EventBus.getDefault().post(EventBusBean.USERINFO_CHANGE);
    }

    /**
     * 清空上一账号的私聊用户信息
     *
     * @param context
     */
    private static void clearPrivateChatUserMsg(Context context) {
        PrivateChatUtils.saveIsShowPrivateMsgBtn(context, false);
    }

    /**
     * 是否需要强制登录
     *
     * @param context
     * @return
     */
    public static boolean needRequireLogin(Context context) {
        String require_login = SPUtils.getString(context, SpConstant.APPINFO_REQUIRE_LOGIN);
        if (require_login != null && "1".equals(require_login)) {
            return true;
        }
        return false;
    }

    /**
     * 获取用户进入直播间的权限
     *
     * @return
     */
    public static boolean isCanJoin(Context context) {
        String can = SPUtils.getString(context, SpConstant.USERINFO_POWER_VISIT_ROOM);
        if (can == null) return false;
        if ("1".equals(can))
            return true;
        else
            return false;
    }

    /**
     * 是否可以私聊
     *
     * @param sqlOpenHelper
     * @param rid
     * @return
     */
    public static boolean isCanPrivateChat(SQLiteOpenHelper sqlOpenHelper, String rid) {
        SQLiteDatabase dbw = sqlOpenHelper.getWritableDatabase();
        Cursor cursor = dbw.rawQuery("select * from roomrole where id=?",
                new String[]{rid});
        String can = null;
        while (cursor.moveToNext()) {
            can = cursor.getString(cursor.getColumnIndex
                    ("power_whisper"));
        }
        cursor.close();
        dbw.close();
        if (can == null) return false;
        if ("1".equals(can))
            return true;
        else
            return false;
    }

    public static String getUid(Context context) {
        if (isLogin(context)) {
            return SPUtils.getString(context, SpConstant.USERINFO_LOGIN_UID);
        } else {
            return SPUtils.getString(context, SpConstant.USERINFO_UID);
        }
    }

    public static String getRid(Context context) {
        if (isLogin(context)) {
            return SPUtils.getString(context, SpConstant.USERINFO_LOGIN_RID);
        } else {
            return SPUtils.getString(context, SpConstant.USERINFO_RID);
        }
    }

    public static String getName(Context context) {
        if (isLogin(context)) {
            return SPUtils.getString(context, SpConstant.USERINFO_LOGIN_NAME);
        } else {
            return SPUtils.getString(context, SpConstant.USERINFO_NAME);
        }
    }

}
