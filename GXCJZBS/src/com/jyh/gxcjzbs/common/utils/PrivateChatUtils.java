package com.jyh.gxcjzbs.common.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.jyh.gxcjzbs.bean.RedPointBean;
import com.jyh.gxcjzbs.bean.UserBean;
import com.jyh.gxcjzbs.common.constant.SpConstant;
import com.jyh.gxcjzbs.common.constant.VarConstant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 项目名:GXCJZBS
 * 类描述:私聊工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/11.
 */

public class PrivateChatUtils {

    public static boolean isShowPrivateMsgBtn(Context context) {

        if (LoginInfoUtils.isLogin(context)) {
            return SPUtils.getBoolean2(context, LoginInfoUtils.getUid(context), SpConstant.PRIVATGE_IS_SHOWBTN);
        } else {
            return SPUtils.getBoolean2(context, VarConstant.UNLOGIN, SpConstant.PRIVATGE_IS_SHOWBTN);
        }

    }

    public static void saveIsShowPrivateMsgBtn(Context context, boolean b) {
        if (LoginInfoUtils.isLogin(context)) {
            SPUtils.save2(context, LoginInfoUtils.getUid(context), SpConstant.PRIVATGE_IS_SHOWBTN, b);
        } else {
            SPUtils.save2(context, VarConstant.UNLOGIN, SpConstant.PRIVATGE_IS_SHOWBTN, b);
        }
    }

    /**
     * 获取私聊用户列表信息
     *
     * @param context
     * @return
     */
    public static List<UserBean> getPrivateChatUserList(Context context) {

        String userList;
        if (LoginInfoUtils.isLogin(context)) {
            userList = SPUtils.getString2(context, LoginInfoUtils.getUid(context), SpConstant.PRIVATGE_CHAT_USERLIST);
        } else {
            userList = SPUtils.getString2(context, VarConstant.UNLOGIN, SpConstant.PRIVATGE_CHAT_USERLIST);
        }
        if (userList == null) return null;

        try {
            return JSON.parseArray(userList, UserBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存私聊用户信息
     *
     * @param context
     * @param userBean
     */
    private static void savePrivateChatUserInfo(Context context, UserBean userBean) {
        List<UserBean> userList = getPrivateChatUserList(context);

        String set;
        if (LoginInfoUtils.isLogin(context)) {
            set = SPUtils.getString2(context, LoginInfoUtils.getUid(context), SpConstant.PRIVATGE_CHAT_USERLIST);
        } else {
            set = SPUtils.getString2(context, VarConstant.UNLOGIN, SpConstant.PRIVATGE_CHAT_USERLIST);
        }
        if (set == null) set = "";
        if (userList != null) {
            if (isSavedPrivateUserInfo(userBean, userList)) {

            } else {
                userList.add(userBean);
            }
        } else {
            userList = new ArrayList<>();
            userList.add(userBean);
        }
        if (userList.size() > 50)
            userList.remove(0);

        set = JSON.toJSONString(userList);

        if (LoginInfoUtils.isLogin(context)) {
            SPUtils.save2(context, LoginInfoUtils.getUid(context), SpConstant.PRIVATGE_CHAT_USERLIST, set);
        } else {
            SPUtils.save2(context, VarConstant.UNLOGIN, SpConstant.PRIVATGE_CHAT_USERLIST, set);
        }
    }

    /**
     * 是否以保存过私聊信息,若保存过替换之
     *
     * @param userBean
     * @param userList
     * @return
     */
    private static boolean isSavedPrivateUserInfo(UserBean userBean, List<UserBean> userList) {
        Iterator<UserBean> iterator = userList.iterator();
        while (iterator.hasNext()) {
            UserBean bean = iterator.next();
            if (bean.getUid().equals(userBean.getUid())) {
                bean.setRid(userBean.getRid());
                bean.setName(userBean.getName());
                return true;
            }
        }
        return false;
    }

    public static final String POINT_SHOW = "1";//显示红点
    public static final String POINT_HINT = "0";//隐藏红点

    /**
     * 是否已保存过该条私聊用户信息
     *
     * @param context
     * @param tName
     * @param tUid
     * @param tRid
     * @return
     */
    public static void savePrivateChatUserInfo(Context context, String tName, String tUid, String tRid) {
        UserBean userBean = new UserBean();
        userBean.setName(tName);
        userBean.setRid(tRid);
        userBean.setUid(tUid);
        savePrivateChatUserInfo(context, userBean);
    }

    /**
     * 保存红点状态
     *
     * @param context
     * @param uid
     * @param isShow
     */
    public static void savePrivateChatNewPoint(Context context, String uid, String isShow) {
        Set<String> set;
        if (LoginInfoUtils.isLogin(context)) {
            set = SPUtils.getStringSet(context, LoginInfoUtils.getUid(context), SpConstant.PRIVATGE_NEWMSG);
        } else {
            set = SPUtils.getStringSet(context, VarConstant.UNLOGIN, SpConstant.PRIVATGE_NEWMSG);
        }
        if (set == null) set = new HashSet<>();
        else set = new HashSet<>(set);
        if (isSavedRedPoiont(set, uid, isShow)) {
        } else {
            set.add(JSON.toJSONString(new RedPointBean(uid, isShow)));
        }

        if (LoginInfoUtils.isLogin(context)) {
            SPUtils.save2(context, LoginInfoUtils.getUid(context), SpConstant.PRIVATGE_NEWMSG, set);
        } else {
            SPUtils.save2(context, VarConstant.UNLOGIN, SpConstant.PRIVATGE_NEWMSG, set);
        }


    }

    private static boolean isSavedRedPoiont(Set<String> set, String uid, String isShow) {
        for (String s : set) {
            RedPointBean redPointBean = JSON.parseObject(s, RedPointBean.class);
            if (redPointBean.getUid().equals(uid)) {
                set.remove(s);
                set.add(JSON.toJSONString(new RedPointBean(uid, isShow)));
                return true;
            }
        }
        return false;
    }

    /**
     * 是否显示红点
     *
     * @param context
     * @param uid
     * @return
     */
    public static boolean isShowPoint(Context context, String uid) {

        Set<String> set;
        if (LoginInfoUtils.isLogin(context)) {
            set = SPUtils.getStringSet(context, LoginInfoUtils.getUid(context), SpConstant.PRIVATGE_NEWMSG);
        } else {
            set = SPUtils.getStringSet(context, VarConstant.UNLOGIN, SpConstant.PRIVATGE_NEWMSG);
        }
        if (set == null) return false;

        for (String s : set) {
            RedPointBean redPointBean = JSON.parseObject(s, RedPointBean.class);
            if (redPointBean.getUid().equals(uid)) {
                String isShowPoint = redPointBean.getIsShow();
                if (isShowPoint == null || isShowPoint.equals("")) return false;
                return isShowPoint.equals(POINT_SHOW);
            }
        }

        return false;
    }

    /**
     * 外部私聊红点是否显示
     *
     * @param context
     * @return
     */
    public static boolean isShowPoint(Context context) {

        List<UserBean> privateChatUserList = getPrivateChatUserList(context);
        if (privateChatUserList == null || privateChatUserList.size() == 0) return false;
        for (UserBean userBean : privateChatUserList) {
            if (isShowPoint(context, userBean.getUid())) return true;
        }
        return false;
    }
}
