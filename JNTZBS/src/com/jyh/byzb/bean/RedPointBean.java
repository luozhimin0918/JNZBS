package com.jyh.byzb.bean;

/**
 * 项目名:GXCJZBS
 * 类描述:红点bean
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/13.
 */

public class RedPointBean {
    private String uid;
    private String isShow;

    public RedPointBean() {
    }

    public RedPointBean(String uid, String isShow) {

        this.uid = uid;
        this.isShow = isShow;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
