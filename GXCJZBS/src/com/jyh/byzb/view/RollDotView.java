package com.jyh.byzb.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.jyh.byzb.R;
import com.jyh.byzb.common.utils.SystemUtil;


/**
 * Created by Mr'Dai on 2017/5/18.
 */

public class RollDotView extends View {

    private int circleSize;
    private int circleCount = 4;
    private int circlePadding;
    private int selectedPosition = 0;
    private int defaultCircleColor;
    private int selectedCircleColor;

    private Paint circlePaint;

    public RollDotView(Context context) {
        this(context, null);
    }

    public RollDotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollDotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);

        circleSize = SystemUtil.dp2px(getContext(), 3.0f);
        circlePadding = SystemUtil.dp2px(getContext(), 5.0f);

    }


    public void setCircleSize(int circleSize) {
        this.circleSize = circleSize;
    }

    public void setCircleCount(int circleCount) {
        this.circleCount = circleCount;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        defaultCircleColor = ContextCompat.getColor(getContext(), R.color.unselectedPointColor);
        selectedCircleColor = ContextCompat.getColor(getContext(), R.color.selectedPointColor);

        if(circleCount==1){
            return;
        }
        for (int i = 0; i < circleCount; i++) {
            if (selectedPosition == i) {
                circlePaint.setColor(selectedCircleColor);
                canvas.drawCircle(circleSize * 2 * i + circleSize + circlePadding * i, circleSize, circleSize,
                        circlePaint);
            } else {
                circlePaint.setColor(defaultCircleColor);
                canvas.drawCircle(circleSize * 2 * i + circleSize + circlePadding * i, circleSize, circleSize,
                        circlePaint);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int measureWidth = (circleSize * 2) * circleCount + circlePadding * circleCount - circlePadding;
        int measureHeight = circleSize + circleSize;
        setMeasuredDimension(measureWidth, measureHeight);

    }

    public void onChangeTheme() {
        postInvalidate();
    }
}
