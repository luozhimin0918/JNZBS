package com.jyh.gxcjzbs.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.jyh.gxcjzbs.common.utils.SystemUtils;

/**
 * 用以解决软键盘问题
 *
 * @author Administrator
 */
public class MyLinearLayout extends LinearLayout {

    private int height;
    private Context context;
    private float dpi;

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        // TODO Auto-generated constructor stub
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        // TODO Auto-generated constructor stub
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        // TODO Auto-generated constructor stub
    }

    public MyLinearLayout(Context context) {
        super(context);
        init(context);
        // TODO Auto-generated constructor stub
    }

    private void init(Context context) {
        this.context = context;
        dpi = SystemUtils.getDpi((Activity) context);
        Log.i("info", "dpi=" + dpi);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        height = h > oldh ? h : oldh;
        Log.i("sizechanged",""+(h-oldh)/dpi);
        if (h - oldh > 130 * dpi) {
            //软键盘隐藏
            getChildAt(0).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) ((height - 41 * dpi) / 2.75 + 0.5f)));
            getChildAt(1).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (41 * dpi + 0.5f)));

//            FaceRelativeLayout.layout.close();
            Intent intent = new Intent("hideorshow");
            intent.putExtra("hideorshow", 2);
            context.sendBroadcast(intent);
            Log.i("MyLinearLayout", "1");
        } else {
            //软键盘弹起
            getChildAt(0).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) ((height - 41 * dpi) / 2.75 + 0.5f)));
            getChildAt(1).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (41 * dpi + 0.5f)));
            Intent intent = new Intent("hideorshow");
            intent.putExtra("hideorshow", 1);
            context.sendBroadcast(intent);
            Log.i("MyLinearLayout", "2");
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
    }

}
