package com.jyh.byzb.bean;

/**
 * 软件更新信息（实体类）-金银汇
 * @author Administrator
 *
 */
public class UpdateInfo {

	private int versionCode;   //versionCode:版本代码
	private String versionName; //versionName:版本号 
    private String url; // url路径 --下载路径
    private String description; // 更新说明信息
    private boolean forceUpdate = false; //是否强制升级？默认是false
    
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isForceUpdate() {
		return forceUpdate;
	}
	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
    
}
