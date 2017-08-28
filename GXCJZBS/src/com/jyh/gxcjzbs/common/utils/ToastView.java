package com.jyh.gxcjzbs.common.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jyh.gxcjzbs.R;


/**
 * Created by DaiYao on 2016/9/20.
 */
public class ToastView {
    private static Toast toast;

    public static void makeText2(Context context, String txt) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_widget_toast, null);
        TextView tvTip = (TextView) view.findViewById(R.id.tips_msg);
        tvTip.setText(txt);

        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public static void makeText(Context context, String txt) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_widget_toast2, null);
        TextView tvTip = (TextView) view.findViewById(R.id.tips_msg);
        tvTip.setText(txt);

        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, SystemUtils.dip2px(context, 60));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}
