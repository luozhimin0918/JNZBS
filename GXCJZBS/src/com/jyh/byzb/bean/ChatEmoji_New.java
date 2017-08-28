package com.jyh.byzb.bean;



public class ChatEmoji_New {

	private String name;//名字
	private String image;//网络地址
	private String path;//储存地址
	private String type;//类型
	
	private boolean isCaitiao;
	
	
	
	public boolean isCaitiao() {
		return isCaitiao;
	}
	public void setCaitiao(boolean isCaitiao) {
		this.isCaitiao = isCaitiao;
	}
	public ChatEmoji_New() {
		super();
	}
	public ChatEmoji_New(String name, String image, String path, String type, boolean isCaitiao) {
		super();
		this.name = name;
		this.image = image;
		this.path = path;
		this.type = type;
		this.isCaitiao = isCaitiao;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "ChatEmoji_New [name=" + name + ", image=" + image + ", path=" + path + ", type=" + type + ", isCaitiao=" + isCaitiao + "]";
	}
	
}
