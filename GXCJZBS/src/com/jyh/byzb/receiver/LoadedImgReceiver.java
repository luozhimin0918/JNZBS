package com.jyh.byzb.receiver;

import com.jyh.byzb.bean.KXTApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LoadedImgReceiver extends BroadcastReceiver {

	public LoadedImgReceiver() {
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub

		final int emojiNum = KXTApplication.emojiNum;
		KXTApplication.isLoadedImg = true;
//		Toast.makeText(context, "表情包加载完毕", 0).show();
	}

}
