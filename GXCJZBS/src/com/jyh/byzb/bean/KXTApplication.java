package com.jyh.byzb.bean;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.gensee.entity.InitParam;
import com.gotye.live.core.GLCore;
import com.gotye.live.player.GLPlayer;
import com.jyh.byzb.R;
import com.jyh.byzb.service.ImageService;
import com.jyh.byzb.common.utils.emoji_utils.FaceConversionUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KXTApplication extends Application {

    public static InitParam initParam;
    private List<Activity> activities; // 保存子activity的实例
    private Object TSData;
    private int Cunrentfragment;
    private int index = 0;
    public boolean ischange = false;
    public boolean isfirst = true;
    public boolean HqIsOk = false;
    public boolean isAppcet = false;
    public String RAServer = "";
    public String RAToken = "";
    public String TopRAServer = "";
    public String TopRAToken = "";
    public String flashRAServer = "";
    public String flashRAToken = "";
    private List<String> codes;
    private List<String> topCodes;
    private Map<String, String> mmp = new HashMap<String, String>();
    public String TzServer = "";
    public String TzToken = "";
    public static boolean isSendToServer = false;// 是否发送给服务器
    public static boolean IsOut = false;
    private RequestQueue queue;

    public static int emojiNum;// 表情包数量
    public static int caitiaoNum;// 彩条包数量

    public static boolean isLoadedImg = false;// 表情是否加载完毕
    public static boolean isLoadedImgError = false;// 表情是否加载异常

    public static boolean isHaveNet = true;

    // Gotye视频所需参数
    public static GLPlayer player;
    public static GLCore core;
    public static boolean isFirst = true;

    private Map<String, String> emojiMaps;
    private List<ChatEmojiTitle> chatEmojiTitles;
    private List<String> caitiaos;
    private List<ChatEmoji_New> chatEmoji_News;

    public List<ChatEmoji_New> getChatEmoji_News() {
        return chatEmoji_News;
    }

    public void setChatEmoji_News(List<ChatEmoji_New> chatEmoji_News) {
        this.chatEmoji_News = chatEmoji_News;
    }

    public List<String> getCaitiaos() {
        return caitiaos;
    }

    public void setCaitiaos(List<String> caitiaos) {
        this.caitiaos = caitiaos;
    }

    public List<ChatEmojiTitle> getChatEmojiTitles() {
        return chatEmojiTitles;
    }

    public void setChatEmojiTitles(List<ChatEmojiTitle> chatEmojiTitles) {
        this.chatEmojiTitles = chatEmojiTitles;
    }

    public Map<String, String> getEmojiMaps() {
        return emojiMaps;
    }

    public void setEmojiMaps(Map<String, String> emojiMaps) {
        this.emojiMaps = emojiMaps;
    }

    public Map<String, String> getMmp() {
        return mmp;
    }

    public void setMmp(Map<String, String> mmp) {
        this.mmp = mmp;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public List<String> getTopCodes() {
        return topCodes;
    }

    public void setTopCodes(List<String> topCodes) {
        this.topCodes = topCodes;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Context getContext() {
        return getApplicationContext();
    }

    public Object getTSData() {
        return TSData;
    }

    public void setTSData(Object tSData) {
        TSData = tSData;
    }

    public int getCunrentfragment() {
        return Cunrentfragment;
    }

    public void setCunrentfragment(int cunrentfragment) {
        Cunrentfragment = cunrentfragment;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activities = new ArrayList<Activity>();
        Fresco.initialize(this);
        IsOut = false;
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
        Intent in = new Intent();
        in.setAction("MyBroadcastReceiver2");
        sendBroadcast(in);

        // Gotye视频参数初始化
        core = GLCore.getInstance();
        player = GLPlayer.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString(GLCore.PROPERTY_COMPANY, "live108");// 传入公司的ID
        bundle.putString(GLCore.PROPERTY_ACCESS_KEY, "3fc068b0-2656-4cd0-903d-40cf76f86651");// 传入AK
        // bundle.putString(GLCore.PROPERTY_COMPANY, "");// 传入公司的ID
        // bundle.putString(GLCore.PROPERTY_ACCESS_KEY,
        // "eaa6da7a910e422e8279677470a1eb9f");// 传入AK
        core.init(this, bundle);
        player.init(core);
        //初始化网络图片缓存库

        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_default_adimage)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);

    }

    public void addAct(Activity act) {
        if (activities != null) {
            activities.add(act);
        }
    }

    public void removeAct(Activity act) {
        if (activities != null) {
            activities.remove(act);
            act.finish();
        }
    }

    public void exitAppAll() {
        for (Activity act : activities) {
            if (null != act && !act.isDestroyed())
                act.finish();
        }
        IsOut = true;
        this.mmp.clear();
        FaceConversionUtil.getInstace().ClearDate();
        isSendToServer = false;
        stopSer();
        System.gc();
    }

    public void exitApp() {
        IsOut = true;
        if (activities != null && activities.size() > 0) {
            for (Activity act : activities) {
                if (!act.getClass().getSimpleName().equals("MainActivity")) {
                    act.finish();
                }
            }
        }
        FaceConversionUtil.getInstace().ClearDate();
        stopSer();
    }

    private void stopSer() {
        // **因为后台还有Service在运行，所以KXTApplication没有销毁，所以activities中变量还存在，得清空该集合！
        if (activities != null && activities.size() > 0) {
            activities.clear();
        }
        // 停止服务
        stopService(new Intent(this, ImageService.class));
        System.exit(0);
        System.gc();
    }

    public RequestQueue getQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        return queue;
    }

    public void setQueue(RequestQueue queue) {
        this.queue = queue;
    }

    private List<ChatMsgEntity> chatMsgEntities;

    public List<ChatMsgEntity> getChatMsgEntities() {
        return chatMsgEntities;
    }

    public void setChatMsgEntities(List<ChatMsgEntity> chatMsgEntities) {
        this.chatMsgEntities = chatMsgEntities;
    }

    private List<EmojiBean> emojiBeen;

    public List<EmojiBean> getEmojiBeen() {
        return emojiBeen;
    }

    public void setEmojiBeen(List<EmojiBean> emojiBeen) {
        this.emojiBeen = emojiBeen;
    }
}
