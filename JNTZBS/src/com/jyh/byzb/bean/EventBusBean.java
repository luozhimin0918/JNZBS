package com.jyh.byzb.bean;

/**
 * 项目名:CJDJCZBS
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2016/11/23.
 */

public enum EventBusBean {
    NEW_CHATMSG, //新消息
    DEL_CHATMSG, //删除信息
    USERINFO_CHANGE,//用户信息发生变化
    SEND_CHATMSG; //发送信息

    private Object object;

    public EventBusBean setObj(Object object) {
        this.object = object;
        return this;
    }

    public Object getObject() {
        return object;
    }

}
