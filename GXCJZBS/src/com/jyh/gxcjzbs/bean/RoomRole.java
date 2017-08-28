package com.jyh.gxcjzbs.bean;
/** 
 * 直播室权限
 * @author  beginner 
 * @date 创建时间：2015年7月24日 下午4:52:20 
 * @version 1.0  
 */
public class RoomRole {
	
//	"id": "1",
//    "name": "游客",
//    "type": "0",
//    "limit_chat_time": "5", //发公聊时间间隔
//    "power_whisper": "0", //私聊权限
//    "limit_colorbar_time": "5", //发彩条间隔
//    "power_upload_pic": "1", //上传图片权限
//    "limit_account_time": "0",
//    "status": "1",
//    "sort": "0",
//    "power_visit_room": "1", //是否能进房间
//    "style_chat_text": "",
//    "image": "http://cdn0.108tec.com/SamV9/Uploads/Picture/2016-04-15/5710873c5b2c9.png" //角色图片
	
	private String id;
    private String name;//身份
    private String type;
    private String limit_chat_time;
    private String power_whisper;
    private String limit_colorbar_time;
    private String power_upload_pic;
    private String limit_account_time;
    private String status;
    private String sort;
    private String power_visit_room;
    private String style_chat_text;
    private String image;

    
    public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getLimit_chat_time() {
		return limit_chat_time;
	}


	public void setLimit_chat_time(String limit_chat_time) {
		this.limit_chat_time = limit_chat_time;
	}


	public String getPower_whisper() {
		return power_whisper;
	}


	public void setPower_whisper(String power_whisper) {
		this.power_whisper = power_whisper;
	}


	public String getLimit_colorbar_time() {
		return limit_colorbar_time;
	}


	public void setLimit_colorbar_time(String limit_colorbar_time) {
		this.limit_colorbar_time = limit_colorbar_time;
	}


	public String getPower_upload_pic() {
		return power_upload_pic;
	}


	public void setPower_upload_pic(String power_upload_pic) {
		this.power_upload_pic = power_upload_pic;
	}


	public String getLimit_account_time() {
		return limit_account_time;
	}


	public void setLimit_account_time(String limit_account_time) {
		this.limit_account_time = limit_account_time;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getSort() {
		return sort;
	}


	public void setSort(String sort) {
		this.sort = sort;
	}


	public String getPower_visit_room() {
		return power_visit_room;
	}


	public void setPower_visit_room(String power_visit_room) {
		this.power_visit_room = power_visit_room;
	}


	public String getStyle_chat_text() {
		return style_chat_text;
	}


	public void setStyle_chat_text(String style_chat_text) {
		this.style_chat_text = style_chat_text;
	}


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}


	@Override
	public String toString() {
		return "RoomRole [id=" + id + ", name=" + name + ", type=" + type + ", limit_chat_time=" + limit_chat_time + ", power_whisper="
				+ power_whisper + ", limit_colorbar_time=" + limit_colorbar_time + ", power_upload_pic=" + power_upload_pic
				+ ", limit_account_time=" + limit_account_time + ", status=" + status + ", sort=" + sort + ", power_visit_room="
				+ power_visit_room + ", style_chat_text=" + style_chat_text + ", image=" + image + "]";
	}

}
