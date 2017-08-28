package com.jyh.gxcjzbs.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 ******************************************
 * @author 廖乃波
 * @文件名称 : ChatMsgEntity.java
 * @创建时间 : 2013-1-27 下午02:33:33
 * @文件描述 : 消息实体
 ******************************************
 */
public class ChatMsgEntity implements Parcelable{

	// "id":"751",
	// "f_uid":"-46104266028",
	// "f_name":"游客-j028u",
	// "f_rid":"1",
	// "t_uid":"0",
	// "t_name":"",
	// "t_rid":"0",
	// "data":"sdfsdfsdfad",
	// "time":"1461042667",
	// "is_checked":"1",
	// "checked_uid":"2736"
	private String f_uid;
	private String f_name;
	private String f_rid;
	private String t_uid;
	private String t_name;
	private String t_rid;
	private String data;
	private String is_checked;
	private String time;
	private String id;

	public String getF_uid() {
		return f_uid;
	}

	public void setF_uid(String f_uid) {
		this.f_uid = f_uid;
	}

	public String getF_name() {
		return f_name;
	}

	public void setF_name(String f_name) {
		this.f_name = f_name;
	}

	public String getF_rid() {
		return f_rid;
	}

	public void setF_rid(String f_rid) {
		this.f_rid = f_rid;
	}

	public String getT_uid() {
		return t_uid;
	}

	public void setT_uid(String t_uid) {
		this.t_uid = t_uid;
	}

	public String getT_name() {
		return t_name;
	}

	public void setT_name(String t_name) {
		this.t_name = t_name;
	}

	public String getT_rid() {
		return t_rid;
	}

	public void setT_rid(String t_rid) {
		this.t_rid = t_rid;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getIs_checked() {
		return is_checked;
	}

	public void setIs_checked(String is_checked) {
		this.is_checked = is_checked;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return f_uid + "," + f_name + "," + f_rid + "," + t_uid + "," + t_name
				+ "," + t_rid + "," + data + "," + is_checked + "," + time + "," + id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(f_uid);
		dest.writeString(f_name);
		dest.writeString(f_rid);
		dest.writeString(t_uid);
		dest.writeString(t_name);
		dest.writeString(t_rid);
		dest.writeString(data);
		dest.writeString(is_checked);
		dest.writeString(time);
		dest.writeString(id);
	}

	public ChatMsgEntity(String f_uid, String f_name, String f_rid, String t_uid, String t_name, String t_rid, String data,
			String is_checked, String time, String id) {
		super();
		this.f_uid = f_uid;
		this.f_name = f_name;
		this.f_rid = f_rid;
		this.t_uid = t_uid;
		this.t_name = t_name;
		this.t_rid = t_rid;
		this.data = data;
		this.is_checked = is_checked;
		this.time = time;
		this.id = id;
	}

	public ChatMsgEntity() {
		super();
	}

	private ChatMsgEntity(Parcel parcel){
		f_uid = parcel.readString();
		f_name = parcel.readString();
		f_rid = parcel.readString();
		t_uid = parcel.readString();
		t_name = parcel.readString();
		t_rid = parcel.readString();
		data = parcel.readString();
		is_checked = parcel.readString();
		time = parcel.readString();
		id = parcel.readString();
	}
	
	public static final Parcelable.Creator<ChatMsgEntity> CREATOR = new Parcelable.Creator<ChatMsgEntity>() 
		     {
		         public ChatMsgEntity createFromParcel(Parcel in) 
		         {
		             return new ChatMsgEntity(in);
		         }

		         public ChatMsgEntity[] newArray(int size) 
		         {
		             return new ChatMsgEntity[size];
		         }
		     };
}
