package com.jyh.gxcjzbs.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCenter {

	/**
	 * 检查网络连接状态
	 * @param context
	 * @return
	 */
	public static boolean checkNetworkConnection(Context context){
		return checkNetwork_JYH(context);
	}
	/**
	 * 检查网络
	 * @param context
	 * @return true=连接，false=断开
	 */
	public static boolean checkNetwork_JYH(Context context){
		if (isNetworkConnected(context)) {
			if (isWifiConnected(context)) {
				//wifi（应该优先连wifi）
				return true;
			}
			if (isMobileConnected(context)) {
				//连接了mobile网络，3G网络，GPRS
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 判断是否有网络连接3-31
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context){
		if (context!=null) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   //得到一个对象，然后把对象转成manager
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo!=null) {
				return networkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/**
	 * 判断WIFI是否可用
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context){
		if (context!=null) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (networkInfo!=null) {
				return networkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/**
	 * 判断Mobile是否可用
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context){
		if (context!=null) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (networkInfo!=null) {
				return networkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/**
	 * 得到网络连接类型
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context){
		if (context != null) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo!=null && networkInfo.isAvailable()) {
				return networkInfo.getType();
			}
		}
		return -1;
	}
}
