package com.jyh.gxcjzbs.common.utils.emoji_utils;

import android.content.Context;
import android.util.Log;

import com.jyh.gxcjzbs.bean.ChatEmojiTitle;
import com.jyh.gxcjzbs.bean.ChatEmoji_New;
import com.jyh.gxcjzbs.bean.EmojiBean;
import com.jyh.gxcjzbs.bean.KXTApplication;
import com.jyh.gxcjzbs.common.utils.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************
 *
 * @author 廖乃波
 * @文件名称 : EmojiFileUtils.java
 * @创建时间 : 2013-1-27 下午02:35:09
 * @文件描述 : 文件工具类
 * *****************************************
 */
public class EmojiFileUtils {
    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    public static List<ChatEmoji_New> getEmojiFile(Context context, int i) {
        try {
            List<ChatEmoji_New> list = new ArrayList<ChatEmoji_New>();
            list.clear();
            List<EmojiBean> emojiBeen = ((KXTApplication) (context.getApplicationContext())).getEmojiBeen();
            for (EmojiBean bean : emojiBeen) {
                List<ChatEmojiTitle> emojiTitles = ((KXTApplication) context.getApplicationContext()).getChatEmojiTitles();
                if (emojiTitles != null && emojiTitles.get(i) != null) {
                    ChatEmojiTitle chatEmojiTitle = emojiTitles.get(i);
                    boolean isCaitiao = false;
                    if (chatEmojiTitle.isCaitiao()) {
                        isCaitiao = true;
                    }
                    Log.i("emojiBean", " type=" + chatEmojiTitle.getCode() + " type2=" + bean.getType());
                    if (chatEmojiTitle.getCode().equals(bean.getType()))
                        list.add(new ChatEmoji_New(bean.getName(), bean.getImage(), bean.getPath(), bean.getType(), isCaitiao));
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            KXTApplication.isLoadedImgError = true;
            ToastView.makeText(context, "表情初始化失败");
        }
        return null;
    }
}
