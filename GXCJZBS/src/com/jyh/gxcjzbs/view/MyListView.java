package com.jyh.gxcjzbs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @author beginner
 * @version 1.0
 * @date 创建时间：2015年8月14日 上午10:58:28
 */
public class MyListView extends ListView {
    private Close close;
    private Context context;
    private FaceRelativeLayout faceRelativeLayout;

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MyListView(Context context) {
        super(context);
        this.context = context;
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setFaceRelativeLayout(FaceRelativeLayout faceRelativeLayout) {
        this.faceRelativeLayout = faceRelativeLayout;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            faceRelativeLayout.close();
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return super.onInterceptTouchEvent(ev);
    }

    interface Close {
        public void close();
    }
}
