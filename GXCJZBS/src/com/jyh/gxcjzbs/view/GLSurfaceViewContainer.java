package com.jyh.gxcjzbs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.gotye.live.player.GLSurfaceView;

public class GLSurfaceViewContainer extends RelativeLayout {
    private GLSurfaceView surfaceView;
    private Context context;
    public GLSurfaceViewContainer(Context context) {
        super(context);
        this.context = context ;
        init();
    }

    public GLSurfaceViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context ;
        init();
    }

    public GLSurfaceViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context ;
        init();
    }
    private void init()
    {
        this.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        surfaceView = new com.gotye.live.player.GLSurfaceView(context);
        surfaceView.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT) );
        addView(surfaceView);
    }
    public GLSurfaceView getSurfaceView()
    {
        return  surfaceView;
    }


}
