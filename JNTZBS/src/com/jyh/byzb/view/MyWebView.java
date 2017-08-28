package com.jyh.byzb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/** 
 * @author  beginner 
 * @date 创建时间：2015年8月11日 上午9:46:40 
 * @version 1.0  
 */
public class MyWebView extends WebView {

	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public MyWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	        onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());
	    }
	    return super.onTouchEvent(ev);
	}

}
