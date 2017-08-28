package com.jyh.byzb.common.utils;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;

import com.jyh.byzb.common.constant.VarConstant;

import java.lang.reflect.Method;

/**
 * 项目名称：com.jyh.dyby.tool
 * 类描述：权限工具类
 * 创建人：Administrator
 * 创建时间：2016/9/2714:47
 * 修改人：Administrator
 * 修改时间：2016/9/2714:47
 * 修改备注：
 */
public class PermissionUtils {

    /** @hide No operation specified. */
    public static final int OP_NONE = -1;
    /** @hide Access to coarse location information. */
    public static final int OP_COARSE_LOCATION = 0;
    /** @hide Access to fine location information. */
    public static final int OP_FINE_LOCATION = 1;
    /** @hide Causing GPS to run. */
    public static final int OP_GPS = 2;
    /** @hide */
    public static final int OP_VIBRATE = 3;
    /** @hide */
    public static final int OP_READ_CONTACTS = 4;
    /** @hide */
    public static final int OP_WRITE_CONTACTS = 5;
    /** @hide */
    public static final int OP_READ_CALL_LOG = 6;
    /** @hide */
    public static final int OP_WRITE_CALL_LOG = 7;
    /** @hide */
    public static final int OP_READ_CALENDAR = 8;
    /** @hide */
    public static final int OP_WRITE_CALENDAR = 9;
    /** @hide */
    public static final int OP_WIFI_SCAN = 10;
    /** @hide */
    public static final int OP_POST_NOTIFICATION = 11;
    /** @hide */
    public static final int OP_NEIGHBORING_CELLS = 12;
    /** @hide */
    public static final int OP_CALL_PHONE = 13;
    /** @hide */
    public static final int OP_READ_SMS = 14;
    /** @hide */
    public static final int OP_WRITE_SMS = 15;
    /** @hide */
    public static final int OP_RECEIVE_SMS = 16;
    /** @hide */
    public static final int OP_RECEIVE_EMERGECY_SMS = 17;
    /** @hide */
    public static final int OP_RECEIVE_MMS = 18;
    /** @hide */
    public static final int OP_RECEIVE_WAP_PUSH = 19;
    /** @hide */
    public static final int OP_SEND_SMS = 20;
    /** @hide */
    public static final int OP_READ_ICC_SMS = 21;
    /** @hide */
    public static final int OP_WRITE_ICC_SMS = 22;
    /** @hide */
    public static final int OP_WRITE_SETTINGS = 23;
    /** @hide */
    public static final int OP_SYSTEM_ALERT_WINDOW = 24;
    /** @hide */
    public static final int OP_ACCESS_NOTIFICATIONS = 25;
    /** @hide */
    public static final int OP_CAMERA = 26;
    /** @hide */
    public static final int OP_RECORD_AUDIO = 27;
    /** @hide */
    public static final int OP_PLAY_AUDIO = 28;
    /** @hide */
    public static final int OP_READ_CLIPBOARD = 29;
    /** @hide */
    public static final int OP_WRITE_CLIPBOARD = 30;
    /** @hide */
    public static final int OP_TAKE_MEDIA_BUTTONS = 31;
    /** @hide */
    public static final int OP_TAKE_AUDIO_FOCUS = 32;
    /** @hide */
    public static final int OP_AUDIO_MASTER_VOLUME = 33;
    /** @hide */
    public static final int OP_AUDIO_VOICE_VOLUME = 34;
    /** @hide */
    public static final int OP_AUDIO_RING_VOLUME = 35;
    /** @hide */
    public static final int OP_AUDIO_MEDIA_VOLUME = 36;
    /** @hide */
    public static final int OP_AUDIO_ALARM_VOLUME = 37;
    /** @hide */
    public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
    /** @hide */
    public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
    /** @hide */
    public static final int OP_WAKE_LOCK = 40;
    /** @hide Continually monitoring location data. */
    public static final int OP_MONITOR_LOCATION = 41;
    /** @hide Continually monitoring location data with a relatively high power request. */
    public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
    /** @hide Retrieve current usage stats via {@link UsageStatsManager}. */
    public static final int OP_GET_USAGE_STATS = 43;
    /** @hide */
    public static final int OP_MUTE_MICROPHONE = 44;
    /** @hide */
    public static final int OP_TOAST_WINDOW = 45;
    /** @hide Capture the device's display contents and/or audio */
    public static final int OP_PROJECT_MEDIA = 46;
    /** @hide Activate a VPN connection without user intervention. */
    public static final int OP_ACTIVATE_VPN = 47;
    /** @hide */
    public static final int _NUM_OP = 48;

    /**
     * 检测权限是否被禁用 0:未被禁用 1:禁用
     * @param context
     * @param op 权限编号
     * @return
     */
    @TargetApi(19)
    public static int checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            try {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                Method dispatchMethod = AppOpsManager.class.getMethod(
                        "checkOp", new Class[] { int.class, int.class,
                                String.class });
                int mode = (Integer) dispatchMethod.invoke(
                        appOpsManager,
                        new Object[] { op, Binder.getCallingUid(),
                                context.getPackageName() });
                return mode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 跳转到权限设置界面
     * @param context
     */
    public static void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", VarConstant.pageageName, null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", VarConstant.pageageName);
        }
        context.startActivity(localIntent);
    }
}
