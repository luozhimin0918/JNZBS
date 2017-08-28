package com.jyh.gxcjzbs.common.constant;

/**
 * 项目名:CJDJCZBS
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2016/11/25.
 */

public class UrlConstant {

    public static final String URL_BASE = "http://gxsp.108tec.com";
//    public static final String URL_BASE = "http://live1.108tec.com";

    //配置信息
    public static final String URL_INDEX = URL_BASE + "/Appapi/index/Config?system=android";
    //获取用户登录信息
    public static final String URL_USERINFO = URL_BASE + "/Appapi/Member/userinfo";
    //聊天记录
    public static final String URL_CHATHISTORY = URL_BASE + "/Appapi/Chat/history";
    //更改用户名
    public static final String URL_EDITNAME = URL_BASE + "/Appapi/Member/editName";
    //用户名审核状态
    public static final String URL_NIKENAMESTATUS = URL_BASE + "/Appapi/Member/nikeNameStatus";
    //更改密码
    public static final String URL_EDITPWD = URL_BASE + "/Appapi/Member/editPwd";
    //策略
    public static final String URL_CELUE = URL_BASE + "/Appapi/Index/kefu?code=celue";
    //登录
    public static final String URL_LOGIN = URL_BASE + "/Appapi/Member/login";
    //注册
    public static final String URL_REGISTER = URL_BASE + "/Appapi/Member/register";
    //发送聊天信息
    public static final String URL_SEND = URL_BASE + "/Appapi/Chat/handle";
    //检查版本信息
    public static final String URL_VERSION = URL_BASE + "/Appapi/Index/version";
    //首页信息
    public static final String URL_NAV_INDEX=URL_BASE + "/Appapi/Nav/index";

    /**
     * 私聊记录
     */
    public static final String CHAT_PRIVATEHISTORY = URL_BASE + "/Appapi/Chat/privateHistory";

}
