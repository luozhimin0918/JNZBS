package com.jyh.byzb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jyh.byzb.R;
import com.pnikosis.materialishprogress.ProgressWheel;


/**
 * Created by DaiYao on 2016/5/21 0021.
 */
public class PageLoadLayout extends FrameLayout implements View.OnClickListener {
    private final String TAG = this.getClass().getName();

    private final String LOADING = "loading";
    private final String LOADERROR = "loaderror";
    private final String NETERROR = "neterror";
    private final String NODATA = "nodata";


    public interface OnAfreshLoadListener {
        void OnAfreshLoad();
    }

    private OnAfreshLoadListener onAfreshLoadListener;

    private LayoutInflater mInflater;

    public PageLoadLayout(Context context) {
        this(context, null);
    }

    public PageLoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInflater = LayoutInflater.from(context);
    }

    private View llLoading;

    public void startLoading() {
        removeLoading();
        llLoading = mInflater.inflate(R.layout.volley_loading, null);
        llLoading.setOnClickListener(null);

        llLoading.setTag(LOADING);

        ProgressWheel progressWheel = (ProgressWheel) llLoading.findViewById(R.id.id_volley_loading_pro);
        progressWheel.spin();

        updateState();
    }

    public void loadSuccess() {
        removeLoading();
    }

    public void loadError(String str) {
        removeLoading();
        llLoading = mInflater.inflate(R.layout.volley_load_error, null);
        TextView error_text = (TextView) llLoading.findViewById(R.id.id_volley_load_error);
        error_text.setText(str);
        llLoading.setOnClickListener(this);
        llLoading.setTag(LOADERROR);
        updateState();
    }

    public void loadEmpty() {
        removeLoading();
        llLoading = mInflater.inflate(R.layout.volley_load_error, null);
        llLoading.setOnClickListener(this);
        llLoading.setTag(LOADERROR);
        updateState();
    }

    public void netError() {
        removeLoading();
        llLoading = mInflater.inflate(R.layout.volley_load_offnet, null);
        llLoading.setOnClickListener(this);
        llLoading.setTag(NETERROR);
        updateState();
    }

    public void loadNoData(String msg) {
        removeLoading();
        llLoading = mInflater.inflate(R.layout.volley_load_nodata, null);
        TextView textView = (TextView) llLoading.findViewById(R.id.id_volley_load_nodata);
        textView.setText(msg);
        llLoading.setOnClickListener(this);
        llLoading.setTag(NODATA);
        updateState();
    }

    private void updateState() {
        addView(llLoading);
    }

    public void addCustomView(View view) {
        removeLoading();
        llLoading = view;
        updateState();
    }

    private void removeLoading() {
        if (llLoading != null) {
            if (LOADING.equals(llLoading.getTag())) {
                ProgressWheel progresswheel = (ProgressWheel) llLoading.findViewWithTag("progresswheel");
                progresswheel.stopSpinning();
            }
            removeView(llLoading);
        }
    }

    public void setOnAfreshLoadListener(OnAfreshLoadListener onAfreshLoadListener) {
        this.onAfreshLoadListener = onAfreshLoadListener;
    }

    @Override
    public void onClick(View v) {
        //重新调用接口
        if (onAfreshLoadListener != null) {
            startLoading();
            onAfreshLoadListener.OnAfreshLoad();
        }
    }

}
