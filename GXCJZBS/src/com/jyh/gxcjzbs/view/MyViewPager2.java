package com.jyh.gxcjzbs.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jyh.gxcjzbs.R;

/**
 * 解决viewPager滑动冲突
 * 
 * @author Administrator
 *
 */
public class MyViewPager2 extends ViewPager {

	private int isBqShow;// 判断表情框是否显示，显示则禁止外层viewpager滑动

	public MyViewPager2(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (getChildAt(0).findViewById(R.id.layout) != null) {
			isBqShow = getChildAt(0).findViewById(R.id.layout).findViewById(R.id.ll_facechoose).getVisibility();
			if (isBqShow == View.VISIBLE)
				return false;
			else
				return super.onInterceptTouchEvent(event);
		}
		return super.onInterceptTouchEvent(event);
	}
}
