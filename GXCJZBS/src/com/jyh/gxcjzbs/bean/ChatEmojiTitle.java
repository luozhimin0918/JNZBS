package com.jyh.gxcjzbs.bean;

public class ChatEmojiTitle {

	private int id;
	private String code;
	private String name;
	private boolean isCaitiao;
	
	
	public boolean isCaitiao() {
		return isCaitiao;
	}
	public void setCaitiao(boolean isCaitiao) {
		this.isCaitiao = isCaitiao;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "ChatEmojiTitle [id=" + id + ", code=" + code + ", name=" + name + ", isCaitiao=" + isCaitiao + "]";
	}
	public ChatEmojiTitle() {
		super();
	}
	public ChatEmojiTitle(int id, String code, String name, boolean isCaitiao) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.isCaitiao = isCaitiao;
	}
	
	
}
