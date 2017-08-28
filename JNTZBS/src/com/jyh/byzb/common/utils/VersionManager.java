package com.jyh.byzb.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.jyh.byzb.R;
import com.jyh.byzb.bean.UpdateInfo;
import com.jyh.byzb.bean.VersionUtil;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.constant.UrlConstant;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * version manager: 版本管理器
 *
 * @author Administrator yyq
 */
public class VersionManager {
    // 单例--饿汉式（线程安全）
    private static VersionManager instance = new VersionManager();
    private ExecutorService cachedThreadPool;
    private Handler handler;
    private String updateUrl = UrlConstant.URL_VERSION;

    private VersionManager() {
        init();
    }

    private void init() {
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    /**
     * 对外暴露，得到本类对象
     */
    public static VersionManager getInstance() {
        return instance;
    }

    /**
     * demo-软件更新（超市入口）
     */
    public void checkVersion(Context context, Handler handler) {
        try {
            this.handler = handler;
            getServerVersion(context);
        } catch (Exception e) {
            handler.sendEmptyMessage(40);
            e.printStackTrace();
        }
    }

    /**
     * 用url去请求网络，从网络下载配置文件信息update_info.xml
     *
     * @return
     */
    private void getServerVersion(final Context context) {

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                UpdateInfo updateInfo = getVersion(context);
                if (updateInfo != null) {
                    processVersionInfo(context, updateInfo);
                }
            }
        });
    }

    private UpdateInfo getVersion(Context context) {
        HttpClient client = null;
        try {
            client = new DefaultHttpClient();
            HttpGet get = new HttpGet(updateUrl);
            client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            HttpResponse execute = client.execute(get);
            UpdateInfo updateInfo_ = new UpdateInfo();
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                JSONObject obj = new JSONObject(EntityUtils.toString(
                        execute.getEntity(), "GBK"));
                if (!("" + obj.getInt("versionCode")).equals("")
                        && !obj.getString("versionName").equals("")
                        && !obj.getString("url").equals("")
                        && !obj.getString("description").equals("")) {
                    updateInfo_.setDescription(obj.getString("description"));
                    updateInfo_.setUrl(obj.getString("url"));
                    updateInfo_.setVersionCode(obj.getInt("versionCode"));
                    updateInfo_.setVersionName(obj.getString("versionName"));
                    SPUtils.save(context, SpConstant.VERSION_DESCRIPTION, obj.getString("description"));
                    SPUtils.save(context, SpConstant.VERSION_URL, obj.getString("url"));
                    return updateInfo_;
                } else {
                    handler.sendEmptyMessage(30);
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(30);
        } finally {
            if (client != null && client.getConnectionManager() != null) {
                client.getConnectionManager().shutdown();
            }
        }
        return null;
    }

    /**
     * 拿到了配置文件信息（包括versionId, versionName, apk地址, 新版本介绍）
     *
     * @param updateInfo
     */
    private void processVersionInfo(Context context, UpdateInfo updateInfo) {
        if (updateInfo != null) {
            VersionUtil versionUtil_JYH = new VersionUtil(context);
            // // 本地-版本名称
            int localVersionCode = versionUtil_JYH.getLocalVersionCode(); // 本地-版本号
            // Server-版本名称
            int serverVersionCode = updateInfo.getVersionCode(); // Server-版本号
            Log.i("serverVersion", serverVersionCode + "    "
                    + localVersionCode);
            if (serverVersionCode > localVersionCode) {
                handler.sendEmptyMessage(60);
            } else {
                handler.sendEmptyMessage(70);
            }
        } else {

        }

    }

    /**
     * 弹出更新对话框
     *
     * @param updateInfo
     */
    public void showUpdateDialog(final UpdateInfo updateInfo,
                                 final Context context) {

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("更新提示")
                .setIcon(R.drawable.cjrl_effect_bg_green)
                .setMessage(updateInfo.getDescription())
                .setPositiveButton("立即升级",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String url = updateInfo.getUrl();
                                downloadApk(url, context);
                                dialog.cancel(); // 此处下载新版程序文件并安装,本文已经很长,这里不再赘述
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false).create();
        alertDialog.show();
    }

    /**
     * url的格式为：http://kuaixun360.com/d/KXT.apk 这个模块的作用，根据给定的url跳转到浏览器去下载更新
     *
     * @param url 下载地址（可能会改变）
     */
    protected void downloadApk(String url, Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }
}
