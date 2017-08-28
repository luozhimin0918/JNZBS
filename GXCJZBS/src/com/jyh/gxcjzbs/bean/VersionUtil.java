package com.jyh.gxcjzbs.bean;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * VersionUtil类,这个类主要用来获取当前已安装的安卓应用程序的版本信息,
 * 主要用来与服务器端的xml中的版本信息对比判断是否提示更新
 * @author Administrator
 *
 */
public class VersionUtil {

	
	private static int localVersionCode;
	private static String localVersionName;

	public VersionUtil(Context context){
		try {
			getCurVersion(context);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	//获取版本号 
    public static void getCurVersion(Context context) 
            throws NameNotFoundException { 
        // 获取PackageManager 实例 
        PackageManager packageManager = context.getPackageManager(); 
        // 获得context所属类的包名，0表示获取版本信息 
        PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0); 
        localVersionCode = packageInfo.versionCode;
        localVersionName = packageInfo.versionName;
    }

	public int getLocalVersionCode() {
		return localVersionCode;
	}

	public String getLocalVersionName() {
		return localVersionName;
	} 
    
}
