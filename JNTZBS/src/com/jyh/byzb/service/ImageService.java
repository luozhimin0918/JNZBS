package com.jyh.byzb.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jyh.byzb.bean.ChatEmojiTitle;
import com.jyh.byzb.bean.ChatEmoji_New;
import com.jyh.byzb.bean.EmojiBean;
import com.jyh.byzb.bean.KXTApplication;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.constant.UrlConstant;
import com.jyh.byzb.common.utils.emoji_utils.FaceConversionUtil;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.ToastView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 表情加载service
 */
public class ImageService extends Service {

    private ExecutorService executorService;

    private RequestQueue queue;
    private Map<String, String> emojiMaps;//所有表情和彩条

    private List<ChatEmojiTitle> emojiTitles;//表情包下标
    private List<String> caitiaos;

    private List<EmojiBean> emojiBeen;

    private boolean isLoaded = false;

    private int typeNum = 0;// 图片种类数

    private final String liveBaseUrl = UrlConstant.URL_BASE;

    public Handler handler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 111: {
                    if (isLoaded) {
                        try {
                            for (int i = 0; i < typeNum; i++) {
                                FaceConversionUtil.getInstace().emoji.get(i).clear();
                                FaceConversionUtil.getInstace().emojiList.get(i).clear();
                                FaceConversionUtil.getInstace().getFileText(getApplicationContext(), i);
                            }
                            KXTApplication.isLoadedImg = true;
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                            KXTApplication.isLoadedImg = false;
                            KXTApplication.isLoadedImgError = true;
                        }
                        if (!KXTApplication.isLoadedImgError)
                            sendBroadcast(new Intent("loadimg"));
                    } else {
                        try {
                            Thread.sleep(200);
                            handler.sendEmptyMessage(111);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case 100: {
                    ToastView.makeText(getApplicationContext(), "表情加载异常，正在再加载中，请稍后再试");
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });

    private Mybind mybind;

    private LoadImgErrorreceiver imgReceiver;
    private String emojiversion_new;//最新版表情版本号
    private String emojiversion_old;//当前表情版本号

    public ImageService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ImageService", "oncreate");
        imgReceiver = new LoadImgErrorreceiver();
        registerReceiver(imgReceiver, new IntentFilter("loaderror"));
        emojiversion_new = SPUtils.getString(this, SpConstant.APPINFO_EMOJI_VERSION_NEW);
        emojiversion_old = SPUtils.getString(this, SpConstant.APPINFO_EMOJI_VERSION_OLD);
        if (emojiversion_old == null)
            emojiversion_old = "";
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mybind;
    }

    public class Mybind extends Binder {
        public ImageService Getservire() {
            return ImageService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ImageService", "onStartCommand");
        // TODO Auto-generated method stub
        executorService = Executors.newFixedThreadPool(10);
        emojiMaps = new HashMap<>();
        emojiTitles = new ArrayList<>();
        caitiaos = new ArrayList<>();
        emojiBeen = new ArrayList<>();
        typeNum = 0;
//        dataSqlte = new SCDataSqlte(this);
//        db = dataSqlte.getWritableDatabase();
        // getJsonObj(liveBaseUrl + "/Appapi/Phiz/nav", 1);
        getJsonObj(liveBaseUrl + "/Appapi/Phiz/all", 3);
        return super.onStartCommand(intent, flags, startId);
    }

    private void getJsonObj(String url, final int i) {
        queue = ((KXTApplication) getApplication()).getQueue();

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                Log.i("image", "" + arg0.toString());
                try {
                    switch (i) {
                        case 1:
                            //下载所有普通表情
                            if ("200".equals(arg0.getString("code"))) {
                                org.json.JSONArray array = arg0.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    typeNum += 1;
                                    JSONObject object = array.getJSONObject(i);
                                    String code = object.getString("code");
                                    String name = object.getString("name");
                                    emojiTitles.add(new ChatEmojiTitle(typeNum, code, name, false));
                                    getEmoji(1, i, code, name, array.length());
                                }
                            }
                            break;
                        case 2:
                            //下载所有彩条
                            if ("200".equals(arg0.getString("code"))) {
                                org.json.JSONArray array = arg0.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    typeNum += 1;
                                    JSONObject object = (JSONObject) array.get(i);
                                    String code = object.getString("code");
                                    String name = object.getString("name");
                                    emojiTitles.add(new ChatEmojiTitle(typeNum, code, name, true));
                                    getEmoji(2, i, code, name, array.length());
                                }

                            }
                            break;
                        case 3:
                            //加载所有普通表情
                            if ("200".equals(arg0.getString("code"))) {
                                org.json.JSONArray array = arg0.getJSONArray("data");

                                int len = array.length();
                                for (int j = 0; j < len; j++) {
                                    JSONObject jsonObject = array.getJSONObject(j);
                                    String imagePath = jsonObject.getString("image");
                                    String code1 = jsonObject.getString("code");
                                    emojiMaps.put(code1, imagePath);
                                }
                            }
                            getJsonObj(liveBaseUrl + "/Appapi/Caitiao/all", 4);
                            break;
                        case 4:
                            //加载所有彩条
                            if ("200".equals(arg0.getString("code"))) {
                                org.json.JSONArray array = arg0.getJSONArray("data");
                                int len = array.length();
                                for (int j = 0; j < len; j++) {
                                    JSONObject jsonObject = array.getJSONObject(j);
                                    String imagePath = jsonObject.getString("image");
                                    String code1 = jsonObject.getString("code");
                                    caitiaos.add(code1);
                                    emojiMaps.put(code1, imagePath);
                                }
                            }
                            ((KXTApplication) getApplication()).setCaitiaos(caitiaos);
                            ((KXTApplication) getApplication()).setEmojiMaps(emojiMaps);
                            getJsonObj(liveBaseUrl + "/Appapi/Phiz/nav", 1);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub

            }
        });
        queue.add(request);
    }

    protected void getEmoji(final int i, final int position, final String code, String name, final int length) {
        // TODO Auto-generated method stub
        String url = null;
        if (i == 1)
            url = liveBaseUrl + "/Appapi/Phiz/page?code=" + code;
        else
            url = liveBaseUrl + "/Appapi/Caitiao/page?code=" + code;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                try {

                    if ("200".equals(arg0.getString("code"))) {
                        org.json.JSONArray array = arg0.getJSONArray("data");
                        int len = array.length();
                        for (int j = 0; j < len; j++) {
                            JSONObject jsonObject = array.getJSONObject(j);
                            final String imagePath = jsonObject.getString("image");
                            final String code1 = jsonObject.getString("code");

                            ChatEmoji_New chatEmoji_New = new ChatEmoji_New(code1, imagePath, getFilesDir().getPath() + "/emoji/" + code + "/"
                                    + imagePath.substring(imagePath.lastIndexOf("/") + 1), code, false);
                            emojiBeen.add(new EmojiBean(chatEmoji_New.getImage(), chatEmoji_New.getName(), chatEmoji_New.getPath(),
                                    chatEmoji_New.getType()));
                        }

                        if (i == 1 && position == length - 1) {
                            getJsonObj(liveBaseUrl + "/Appapi/Caitiao/nav", 2);
                        } else if (i == 2 && position == length - 1) {
                            KXTApplication.emojiNum = typeNum;
                            isLoaded = true;
                            List<ChatEmojiTitle> list = emojiTitles;
                            int caitiaonum = 0;
                            for (ChatEmojiTitle chatEmojiTitle : emojiTitles) {
                                if (chatEmojiTitle.isCaitiao()) {
                                    list.remove(chatEmojiTitle);
                                    list.add(caitiaonum++, chatEmojiTitle);
                                    break;
                                }
                            }
                            ((KXTApplication) getApplication()).caitiaoNum = caitiaonum;
                            ((KXTApplication) getApplication()).setChatEmojiTitles(list);
                            ((KXTApplication) getApplication()).setEmojiBeen(emojiBeen);
                            handler.sendEmptyMessage(111);
                            getJsonObj2(liveBaseUrl + "/Appapi/Phiz/nav", 1);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
            }
        });
        queue.add(jsonObjectRequest);
    }

    protected void getEmoji2(final int i, final String code, String name) {
        // TODO Auto-generated method stub
        String url = null;
        if (i == 1)
            url = liveBaseUrl + "/Appapi/Phiz/page?code=" + code;
        else
            url = liveBaseUrl + "/Appapi/Caitiao/page?code=" + code;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                try {
                    if ("200".equals(arg0.getString("code"))) {
                        org.json.JSONArray array = arg0.getJSONArray("data");
                        int len = array.length();
                        if (emojiversion_new.equals(emojiversion_old)) {
                            //没有新版本可更新
                            for (int j = 0; j < len; j++) {
                                JSONObject jsonObject = array.getJSONObject(j);
                                final String imagePath = jsonObject.getString("image");
                                final String code1 = jsonObject.getString("code");
                                saveImg(code, imagePath, code1);
                            }
                        } else {
                            SPUtils.save(ImageService.this, SpConstant.APPINFO_EMOJI_VERSION_OLD, emojiversion_new);
                            //更新版本
                            for (int j = 0; j < len; j++) {
                                JSONObject jsonObject = array.getJSONObject(j);
                                final String imagePath = jsonObject.getString("image");
                                final String code1 = jsonObject.getString("code");
                                saveImg2(code, imagePath, code1);
                            }
                        }

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void saveImg(final String code, final String imagePath, final String code1) {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    return;
                }
                File file = new File(getFilesDir().getPath() + "/emoji/" + code);
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(getFilesDir().getPath() + "/emoji/" + code + "/" + imagePath.substring(imagePath.lastIndexOf("/") + 1));
                try {
                    if (file.exists()) {
                        return;
                    }
                    InputStream inputStream = null;
                    URL url = new URL(imagePath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(20000);
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                    }
                    byte[] buffer = new byte[4096];
                    int len = 0;
                    FileOutputStream outStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    outStream.close();
                    // 通知图库更新
                    // sendBroadcast(new
                    // Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    // Uri.fromFile(file)));
                } catch (Exception e) {
                    file.delete();
                }
            }
        });
    }

    private void saveImg2(final String code, final String imagePath, final String code1) {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    return;
                }
                File file = new File(getFilesDir().getPath() + "/emoji/" + code);
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(getFilesDir().getPath() + "/emoji/" + code + "/" + imagePath.substring(imagePath.lastIndexOf("/") + 1));
                try {
                    if (file.exists()) {
                        file.delete();
                        file = new File(getFilesDir().getPath() + "/emoji/" + code + "/" + imagePath.substring(imagePath.lastIndexOf("/") + 1));
                    }
                    InputStream inputStream = null;
                    URL url = new URL(imagePath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(20000);
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                    }
                    byte[] buffer = new byte[4096];
                    int len = 0;
                    FileOutputStream outStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    outStream.close();
                    // 通知图库更新
                    // sendBroadcast(new
                    // Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    // Uri.fromFile(file)));
                } catch (Exception e) {
                    file.delete();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(imgReceiver);
        super.onDestroy();
    }

    class LoadImgErrorreceiver extends BroadcastReceiver {

        public LoadImgErrorreceiver() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            handler.sendEmptyMessage(100);
        }

    }

    private void getJsonObj2(String url, final int i) {
        queue = ((KXTApplication) getApplication()).getQueue();

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                try {
                    switch (i) {
                        case 1:
                            if ("200".equals(arg0.getString("code"))) {
                                org.json.JSONArray array = arg0.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    String code = object.getString("code");
                                    String name = object.getString("name");
                                    getEmoji2(1, code, name);
                                }
                            }
                            break;
                        case 2:
                            if ("200".equals(arg0.getString("code"))) {
                                org.json.JSONArray array = arg0.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = (JSONObject) array.get(i);
                                    String code = object.getString("code");
                                    String name = object.getString("name");
                                    getEmoji2(2, code, name);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub

            }
        });
        queue.add(request);
    }
}
