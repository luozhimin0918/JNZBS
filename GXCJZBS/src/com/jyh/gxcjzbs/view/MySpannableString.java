package com.jyh.gxcjzbs.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jyh.gxcjzbs.GotyeLiveActivity;
import com.jyh.gxcjzbs.R;
import com.jyh.gxcjzbs.bean.KXTApplication;
import com.jyh.gxcjzbs.common.constant.SpConstant;
import com.jyh.gxcjzbs.common.utils.BCConvert;
import com.jyh.gxcjzbs.common.utils.SystemUtils;
import com.jyh.gxcjzbs.common.utils.emoji_utils.FaceConversionUtil;
import com.jyh.gxcjzbs.common.utils.SPUtils;
import com.jyh.gxcjzbs.common.utils.ToastView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 用于聊天信息显示
 *
 * @author Administrator
 */
public class MySpannableString {

    private static boolean isHavaString1;// 表情前面有字符串
    private static boolean isHavaString2;// 表情后面有字符串

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime = 0;

    private static MySpannableString mySpannableString;

    public static int screenWidth;

    private static int ImageW_H = 50;// 表情大小

    // 保存于内存中的表情HashMap
    private static HashMap<String, String> emojiMap = new HashMap<String, String>();
    private static LayoutParams layoutParams;
    private static LayoutParams layoutParams2;
    private static LayoutParams layoutParams3;
    private static LayoutParams layoutParams4;

    private static TextPaint newPaint;

    private static int Image_W;
    private static int Image_H;
    private static String imageBasePath;

    private MySpannableString(Context context) {
        super();
        emojiMap = FaceConversionUtil.getInstace().getEmojiMap();

        imageBasePath = SPUtils.getString(context, SpConstant.APPINFO_UPLOAD_IMAGES_URL);

        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams2 = new LayoutParams(ImageW_H, ImageW_H);
        layoutParams3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        newPaint = new TextPaint();
        int textSize = (int) (context.getResources().getDisplayMetrics().scaledDensity * 16 + 0.5f);
        newPaint.setTextSize(textSize);
    }

    public static MySpannableString getInstance(Context context) {
        if (mySpannableString == null)
            mySpannableString = new MySpannableString(context);

        return mySpannableString;
    }

    /**
     * @param tvContent
     * @param context
     * @param string
     * @throws Exception
     */
    public static void initView(LinearLayout tvContent, Context context, String string) throws Exception {
        // TODO Auto-generated method stub

        WindowManager wm = ((Activity) context).getWindowManager();
        screenWidth = wm.getDefaultDisplay().getWidth() - SystemUtils.dip2px(context, 144);

        if (tvContent.getChildCount() > 0)
            tvContent.removeAllViews();
        ArrayList<String> splitString = SplitString(string.trim());
        View view = null;
        if (splitString.size() <= 0) {
            view = new TextView(context);
            TextView tv = (TextView) view;
            setTextStyle(tv);
            tv.setText(string);

            tvContent.addView(view, layoutParams3);

        } else {
            getView(tvContent, context, splitString);
        }
    }

    /**
     * 设置text样式
     *
     * @param tv
     */
    private static void setTextStyle(TextView tv) {
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(Color.BLACK);
    }

    /**
     * @param layout
     * @param context
     * @param splitString
     * @throws Exception
     */
    private static void getView(LinearLayout layout, final Context context, ArrayList<String> splitString) throws Exception {
        // TODO Auto-generated method stub

        LinearLayout layout2 = new LinearLayout(context);// 第一行根布局
        LinearLayout layout3 = null;// 第二行根布局
        LinearLayout layout4 = null;// 第三行根布局
        LinearLayout layout5 = null;// 第四行根布局
        LinearLayout layout6 = null;// 第五行根布局
        LinearLayout layout7 = null;// 第六行根布局
        LinearLayout layout8 = null;// 第七行根布局
        LinearLayout layout9 = null;// 第八行根布局
        LinearLayout layout10 = null;// 第九行根布局
        LinearLayout layout11 = null;// 第十行根布局

        layoutParams2.gravity = Gravity.CENTER_VERTICAL;
        layoutParams3.gravity = Gravity.CENTER_VERTICAL;

        layout2.setOrientation(LinearLayout.HORIZONTAL);

        float size = 0f;// 聊天信息长度,用以判断是否需要换行
        for (String str : splitString) {
            if (str.contains("1_")) {
                // 普通字符
                str = BCConvert.DBC2SBC(str.replace("1_", ""));

                TextView textView = new TextView(context);
                setTextStyle(textView);
                textView.setText(str);
                textView.setSingleLine();

                float newPaintWidth = newPaint.measureText(str);// 计算文本总长度
                int textNum = (int) (screenWidth / newPaintWidth * str.length() - 0.5);// 一行可以显示多少文字

                size += newPaintWidth;

                if (size <= screenWidth) {
                    // 第一行
                    layout2.addView(textView, layoutParams3);
                } else if (size > screenWidth && size <= 2 * screenWidth) {
                    // 换行,第二行
                    // 判断换行位置
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    TextView textView2 = new TextView(context);
                    setTextStyle(textView2);
                    if (size - newPaintWidth < screenWidth) {
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout2.addView(textView, layoutParams3);
                    } else {
                        nlong = screenWidth - (size - screenWidth - newPaintWidth);
                        textView2.setText(str);
                    }
                    if (layout3 == null) {
                        layout3 = new LinearLayout(context);
                        layout3.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    layout3.addView(textView2, layoutParams3);
                } else if (size > 2 * screenWidth && size <= 3 * screenWidth) {
                    // 第三行
                    Log.i("hehe", "3" + str);
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    if (size - newPaintWidth <= screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        Log.i("hehe", "nlong=" + nlong + " first=" + first + " str.length()" + str.length());
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum));
                        layout2.addView(textView, layoutParams3);
                        if (layout3 == null) {
                            layout3 = new LinearLayout(context);
                            layout3.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout3.addView(textView2, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView3, layoutParams3);
                    } else if (size - newPaintWidth > screenWidth && size - newPaintWidth <= 2 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);

                        nlong = 2 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout3.addView(textView, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                    } else {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        textView2.setText(str);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                    }
                } else if (size > 3 * screenWidth && size <= 4 * screenWidth) {
                    // 第四行
                    Log.i("hehe", "4" + str);
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    if (size - newPaintWidth <= screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum));

                        layout2.addView(textView, layoutParams3);
                        if (layout3 == null) {
                            layout3 = new LinearLayout(context);
                            layout3.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout3.addView(textView2, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView3, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView4, layoutParams3);
                    } else if (size - newPaintWidth > screenWidth && size - newPaintWidth <= 2 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);

                        nlong = 2 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(first, first + textNum));
                        textView2.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView3.setText(str.substring(first + 2 * textNum));
                        layout3.addView(textView, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView3, layoutParams3);
                    } else if (size - newPaintWidth > 2 * screenWidth && size - newPaintWidth <= 3 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        nlong = 3 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout4.addView(textView, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView2, layoutParams3);
                    } else {
                        nlong = 4 * screenWidth - (size - newPaintWidth);
                        textView.setText(str);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView, layoutParams3);
                    }
                } else if (size > 4 * screenWidth && size <= 5 * screenWidth) {
                    // 第五行
                    Log.i("hehe", "5" + str);
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    if (size - newPaintWidth <= screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum));

                        layout2.addView(textView, layoutParams3);
                        if (layout3 == null) {
                            layout3 = new LinearLayout(context);
                            layout3.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout3.addView(textView2, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView3, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView4, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView5, layoutParams3);
                    } else if (size - newPaintWidth > screenWidth && size - newPaintWidth <= 2 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);

                        nlong = 2 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum));
                        layout3.addView(textView, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView3, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView4, layoutParams3);
                    } else if (size - newPaintWidth > 2 * screenWidth && size - newPaintWidth <= 3 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        nlong = 3 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum));
                        layout4.addView(textView, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView2, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView3, layoutParams3);
                    } else if (size - newPaintWidth > 3 * screenWidth && size - newPaintWidth <= 4 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        nlong = 4 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout5.addView(textView, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView2, layoutParams3);
                    } else {
                        nlong = 5 * screenWidth - (size - newPaintWidth);
                        textView.setText(str);
                        if (layout6 == null)
                            layout6 = new LinearLayout(context);
                        layout6.setOrientation(LinearLayout.HORIZONTAL);
                        layout6.addView(textView, layoutParams3);
                    }
                } else if (size > 5 * screenWidth && size <= 6 * screenWidth) {
                    // 第六行
                    Log.i("hehe", "6" + str);
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    if (size - newPaintWidth <= screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum));

                        layout2.addView(textView, layoutParams3);
                        if (layout3 == null) {
                            layout3 = new LinearLayout(context);
                            layout3.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout3.addView(textView2, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView3, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView4, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView5, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView6, layoutParams3);
                    } else if (size - newPaintWidth > screenWidth && size - newPaintWidth <= 2 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);

                        nlong = 2 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum));
                        layout3.addView(textView, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView3, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView4, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView5, layoutParams3);
                    } else if (size - newPaintWidth > 2 * screenWidth && size - newPaintWidth <= 3 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        nlong = 3 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum));
                        layout4.addView(textView, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView2, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView3, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView4, layoutParams3);
                    } else if (size - newPaintWidth > 3 * screenWidth && size - newPaintWidth <= 4 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        nlong = 4 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum));
                        layout5.addView(textView, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView2, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView3, layoutParams3);
                    } else if (size - newPaintWidth > 4 * screenWidth && size - newPaintWidth <= 5 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        nlong = 5 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout6.addView(textView, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView2, layoutParams3);
                    } else {
                        nlong = 6 * screenWidth - (size - newPaintWidth);
                        textView.setText(str);
                        if (layout7 == null)
                            layout7 = new LinearLayout(context);
                        layout7.setOrientation(LinearLayout.HORIZONTAL);
                        layout7.addView(textView, layoutParams3);
                    }
                } else if (size > 6 * screenWidth && size <= 7 * screenWidth) {
                    // 第七行
                    Log.i("hehe", "7" + str);
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    if (size - newPaintWidth <= screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum));

                        layout2.addView(textView, layoutParams3);
                        if (layout3 == null) {
                            layout3 = new LinearLayout(context);
                            layout3.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout3.addView(textView2, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView3, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView4, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView5, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView6, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView7, layoutParams3);
                    } else if (size - newPaintWidth > screenWidth && size - newPaintWidth <= 2 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);

                        nlong = 2 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum));
                        layout3.addView(textView, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView3, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView4, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView5, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView6, layoutParams3);
                    } else if (size - newPaintWidth > 2 * screenWidth && size - newPaintWidth <= 3 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        nlong = 3 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum));
                        layout4.addView(textView, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView2, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView3, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView4, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView5, layoutParams3);
                    } else if (size - newPaintWidth > 3 * screenWidth && size - newPaintWidth <= 4 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        nlong = 4 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum));
                        layout5.addView(textView, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView2, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView3, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView4, layoutParams3);
                    } else if (size - newPaintWidth > 4 * screenWidth && size - newPaintWidth <= 5 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        nlong = 5 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum));
                        layout6.addView(textView, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView2, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView3, layoutParams3);
                    } else if (size - newPaintWidth > 5 * screenWidth && size - newPaintWidth <= 6 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        nlong = 6 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout7.addView(textView, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView2, layoutParams3);
                    } else {
                        nlong = 7 * screenWidth - (size - newPaintWidth);
                        textView.setText(str);
                        if (layout8 == null)
                            layout8 = new LinearLayout(context);
                        layout8.setOrientation(LinearLayout.HORIZONTAL);
                        layout8.addView(textView, layoutParams3);
                    }
                } else if (size > 7 * screenWidth && size <= 8 * screenWidth) {
                    // 第八行
                    Log.i("hehe", "8" + str);
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    if (size - newPaintWidth <= screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        TextView textView8 = new TextView(context);
                        setTextStyle(textView8);
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum, first + 6 * textNum));
                        textView8.setText(str.substring(first + 6 * textNum));

                        layout2.addView(textView, layoutParams3);
                        if (layout3 == null) {
                            layout3 = new LinearLayout(context);
                            layout3.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout3.addView(textView2, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView3, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView4, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView5, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView6, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView7, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView8, layoutParams3);
                    } else if (size - newPaintWidth > screenWidth && size - newPaintWidth <= 2 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);

                        nlong = 2 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum));
                        layout3.addView(textView, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView3, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView4, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView5, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView6, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView7, layoutParams3);
                    } else if (size - newPaintWidth > 2 * screenWidth && size - newPaintWidth <= 3 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        nlong = 3 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum));
                        layout4.addView(textView, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView2, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView3, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView4, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView5, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView6, layoutParams3);
                    } else if (size - newPaintWidth > 3 * screenWidth && size - newPaintWidth <= 4 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        nlong = 4 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum));
                        layout5.addView(textView, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView2, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView3, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView4, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView5, layoutParams3);
                    } else if (size - newPaintWidth > 4 * screenWidth && size - newPaintWidth <= 5 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        nlong = 5 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum));
                        layout6.addView(textView, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView2, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView3, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView4, layoutParams3);
                    } else if (size - newPaintWidth > 5 * screenWidth && size - newPaintWidth <= 6 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        nlong = 6 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum));
                        layout7.addView(textView, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView2, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView3, layoutParams3);
                    } else if (size - newPaintWidth > 6 * screenWidth && size - newPaintWidth <= 7 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        nlong = 7 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout8.addView(textView, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView2, layoutParams3);
                    } else {
                        nlong = 8 * screenWidth - (size - newPaintWidth);
                        textView.setText(str);
                        if (layout9 == null)
                            layout9 = new LinearLayout(context);
                        layout9.setOrientation(LinearLayout.HORIZONTAL);
                        layout9.addView(textView, layoutParams3);
                    }
                } else if (size > 8 * screenWidth && size <= 9 * screenWidth) {
                    // 第九行
                    Log.i("hehe", "9" + str);
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    if (size - newPaintWidth <= screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        TextView textView8 = new TextView(context);
                        setTextStyle(textView8);
                        TextView textView9 = new TextView(context);
                        setTextStyle(textView9);
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum, first + 6 * textNum));
                        textView8.setText(str.substring(first + 6 * textNum, first + 7 * textNum));
                        textView9.setText(str.substring(first + 7 * textNum));

                        layout2.addView(textView, layoutParams3);
                        if (layout3 == null) {
                            layout3 = new LinearLayout(context);
                            layout3.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout3.addView(textView2, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView3, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView4, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView5, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView6, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView7, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView8, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView9, layoutParams3);
                    } else if (size - newPaintWidth > screenWidth && size - newPaintWidth <= 2 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        TextView textView8 = new TextView(context);
                        setTextStyle(textView8);

                        nlong = 2 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum, first + 6 * textNum));
                        textView8.setText(str.substring(first + 6 * textNum));
                        layout3.addView(textView, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView3, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView4, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView5, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView6, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView7, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView8, layoutParams3);
                    } else if (size - newPaintWidth > 2 * screenWidth && size - newPaintWidth <= 3 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        nlong = 3 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum));
                        layout4.addView(textView, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView2, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView3, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView4, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView5, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView6, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView7, layoutParams3);
                    } else if (size - newPaintWidth > 3 * screenWidth && size - newPaintWidth <= 4 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        nlong = 4 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum));
                        layout5.addView(textView, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView2, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView3, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView4, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView5, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView6, layoutParams3);
                    } else if (size - newPaintWidth > 4 * screenWidth && size - newPaintWidth <= 5 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        nlong = 5 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum));
                        layout6.addView(textView, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView2, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView3, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView4, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView5, layoutParams3);
                    } else if (size - newPaintWidth > 5 * screenWidth && size - newPaintWidth <= 6 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        nlong = 6 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum));
                        layout7.addView(textView, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView2, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView3, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView4, layoutParams3);
                    } else if (size - newPaintWidth > 6 * screenWidth && size - newPaintWidth <= 7 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        nlong = 7 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum));
                        layout8.addView(textView, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView2, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView3, layoutParams3);
                    } else if (size - newPaintWidth > 7 * screenWidth && size - newPaintWidth <= 8 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        nlong = 8 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout9.addView(textView, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView2, layoutParams3);
                    } else {
                        nlong = 9 * screenWidth - (size - newPaintWidth);
                        textView.setText(str);
                        if (layout10 == null)
                            layout10 = new LinearLayout(context);
                        layout10.setOrientation(LinearLayout.HORIZONTAL);
                        layout10.addView(textView, layoutParams3);
                    }
                } else if (size > 8 * screenWidth && size <= 9 * screenWidth) {
                    // 第十行
                    Log.i("hehe", "10" + str);
                    float nlong;// 所在行还剩多少位置可以用来显示文字
                    int first;
                    if (size - newPaintWidth <= screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        TextView textView8 = new TextView(context);
                        setTextStyle(textView8);
                        TextView textView9 = new TextView(context);
                        setTextStyle(textView9);
                        TextView textView10 = new TextView(context);
                        setTextStyle(textView10);
                        nlong = screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum, first + 6 * textNum));
                        textView8.setText(str.substring(first + 6 * textNum, first + 7 * textNum));
                        textView9.setText(str.substring(first + 7 * textNum, first + 8 * textNum));
                        textView10.setText(str.substring(first + 8 * textNum));

                        layout2.addView(textView, layoutParams3);
                        if (layout3 == null) {
                            layout3 = new LinearLayout(context);
                            layout3.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout3.addView(textView2, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView3, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView4, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView5, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView6, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView7, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView8, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView9, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView10, layoutParams3);
                    } else if (size - newPaintWidth > screenWidth && size - newPaintWidth <= 2 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        TextView textView8 = new TextView(context);
                        setTextStyle(textView8);
                        TextView textView9 = new TextView(context);
                        setTextStyle(textView9);

                        nlong = 2 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum, first + 6 * textNum));
                        textView8.setText(str.substring(first + 6 * textNum, first + 7 * textNum));
                        textView9.setText(str.substring(first + 7 * textNum));
                        layout3.addView(textView, layoutParams3);
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView3, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView4, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView5, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView6, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView7, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView8, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView9, layoutParams3);
                    } else if (size - newPaintWidth > 2 * screenWidth && size - newPaintWidth <= 3 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        TextView textView8 = new TextView(context);
                        setTextStyle(textView8);
                        nlong = 3 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum, first + 6 * textNum));
                        textView8.setText(str.substring(first + 6 * textNum));
                        layout4.addView(textView, layoutParams3);
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView2, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView3, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView4, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView5, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView6, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView7, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView8, layoutParams3);
                    } else if (size - newPaintWidth > 3 * screenWidth && size - newPaintWidth <= 4 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        TextView textView7 = new TextView(context);
                        setTextStyle(textView7);
                        nlong = 4 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum, first + 5 * textNum));
                        textView7.setText(str.substring(first + 5 * textNum));
                        layout5.addView(textView, layoutParams3);
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView2, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView3, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView4, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView5, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView6, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView7, layoutParams3);
                    } else if (size - newPaintWidth > 4 * screenWidth && size - newPaintWidth <= 5 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        TextView textView6 = new TextView(context);
                        setTextStyle(textView6);
                        nlong = 5 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum, first + 4 * textNum));
                        textView6.setText(str.substring(first + 4 * textNum));
                        layout6.addView(textView, layoutParams3);
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView2, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView3, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView4, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView5, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView6, layoutParams3);
                    } else if (size - newPaintWidth > 5 * screenWidth && size - newPaintWidth <= 6 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        TextView textView5 = new TextView(context);
                        setTextStyle(textView5);
                        nlong = 6 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum, first + 3 * textNum));
                        textView5.setText(str.substring(first + 3 * textNum));
                        layout7.addView(textView, layoutParams3);
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView2, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView3, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView4, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView5, layoutParams3);
                    } else if (size - newPaintWidth > 6 * screenWidth && size - newPaintWidth <= 7 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        TextView textView4 = new TextView(context);
                        setTextStyle(textView4);
                        nlong = 7 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum, first + 2 * textNum));
                        textView4.setText(str.substring(first + 2 * textNum));
                        layout8.addView(textView, layoutParams3);
                        if (layout9 == null) {
                            layout9 = new LinearLayout(context);
                            layout9.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout9.addView(textView2, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView3, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView4, layoutParams3);
                    } else if (size - newPaintWidth > 7 * screenWidth && size - newPaintWidth <= 8 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        TextView textView3 = new TextView(context);
                        setTextStyle(textView3);
                        nlong = 8 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first, first + textNum));
                        textView3.setText(str.substring(first + textNum));
                        layout9.addView(textView, layoutParams3);
                        if (layout10 == null) {
                            layout10 = new LinearLayout(context);
                            layout10.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout10.addView(textView2, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView3, layoutParams3);
                    } else if (size - newPaintWidth > 8 * screenWidth && size - newPaintWidth <= 9 * screenWidth) {
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        nlong = 9 * screenWidth - (size - newPaintWidth);
                        first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                        textView.setText(str.substring(0, first));
                        textView2.setText(str.substring(first));
                        layout10.addView(textView, layoutParams3);
                        if (layout11 == null) {
                            layout11 = new LinearLayout(context);
                            layout11.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout11.addView(textView2, layoutParams3);
                    } else {
                        nlong = 10 * screenWidth - (size - newPaintWidth);
                        textView.setText(str);
                        if (layout11 == null)
                            layout11 = new LinearLayout(context);
                        layout11.setOrientation(LinearLayout.HORIZONTAL);
                        layout11.addView(textView, layoutParams3);
                    }
                } else {
                    //大于十行
                }

            } else if (str.contains("2_")) {
                // 表情图片
                str = str.replace("2_", "");
                SimpleDraweeView gifImageView = new SimpleDraweeView(context);
                String s = null;
                if (str.contains("[img=")) {
                    // 自定义图片
                    s = imageBasePath + str.replace("[", "").replace("]", "").replace("img=", "");
                    final String ss = s;
                    gifImageView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            // 弹出自定义图片的原图
                            long currentTime = Calendar.getInstance().getTimeInMillis();
                            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                                try {
                                    ((GotyeLiveActivity) context).removeFloatView();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // 防止卡顿时，双击显示两次
                                lastClickTime = currentTime;

                                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_gif, null);
                                final GifImageView draweeView = (GifImageView) view.findViewById(R.id.gif);
                                // final ImageView del = (ImageView)
                                // view.findViewById(R.id.gif_del);

                                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                                asyncHttpClient.get(ss, new AsyncHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                                        // TODO Auto-generated method stub

                                        GifDrawable drawable = null;
                                        try {
                                            drawable = new GifDrawable(arg2);
                                            draweeView.setBackgroundDrawable(drawable);
                                        } catch (IOException e1) {
                                            // TODO Auto-generated catch block
                                            e1.printStackTrace();
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(arg2, 0, arg2.length);
                                            draweeView.setImageBitmap(bitmap);
                                        }
                                        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        alertDialog.setCancelable(true);
                                        alertDialog.setCanceledOnTouchOutside(true);
                                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                try {
                                                    ((GotyeLiveActivity) context).createFloatView();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        alertDialog.show();
                                        alertDialog.setContentView(view);
                                        // del.setOnClickListener(new
                                        // OnClickListener() {
                                        //
                                        // @Override
                                        // public void onClick(View v) {
                                        // // TODO Auto-generated method
                                        // // stub
                                        // alertDialog.dismiss();
                                        // }
                                        // });
                                    }

                                    @Override
                                    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                                        // TODO Auto-generated method stub
                                        ToastView.makeText(context, "加载网络图片出错");
                                    }

                                });
                            }
                        }
                    });
                    // 设置图片缩放方式
                    GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setActualImageScaleType(
                            ScaleType.CENTER_INSIDE).build();
                    gifImageView.setHierarchy(hierarchy);
                } else {
                    // 普通表情
                    s = ((KXTApplication) context.getApplicationContext()).getEmojiMaps().get(str.replace("[", "").replace("]", ""));
                    // 设置图片缩放方式
                    GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setActualImageScaleType(
                            ScaleType.FIT_CENTER).build();
                    gifImageView.setHierarchy(hierarchy);
                }

                try {
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(Uri.parse(s))
                            .setAutoPlayAnimations(true)// 设置加载图片完成后是否直接进行播放
                            .build();
                    gifImageView.setController(draweeController);
                    if (str.contains("[img=")) {
                        size += 100;
                    } else
                        size += ImageW_H;
                } catch (Exception e) {

                    TextView textView = new TextView(context);
                    setTextStyle(textView);
                    textView.setText(str);

                    float newPaintWidth = newPaint.measureText(str);// 计算文本总长度
                    size += newPaintWidth;

                    if (size <= screenWidth) {
                        // 第一行
                        layout2.addView(textView, layoutParams3);
                    } else if (size > screenWidth && size <= 2 * screenWidth) {
                        // 换行,第二行
                        Log.i("info", "e2");
                        float nlong;// 所在行还剩多少位置可以用来显示文字
                        int first;
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        if (size - newPaintWidth < screenWidth) {
                            nlong = screenWidth - (size - newPaintWidth);
                            first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                            textView.setText(str.substring(0, first));
                            textView2.setText(str.substring(first));
                            layout2.addView(textView, layoutParams3);
                        } else {
                            nlong = screenWidth - (size - screenWidth - newPaintWidth);
                            textView2.setText(str);
                        }
                        Log.i("info", "string=" + str + "\n 剩余长度：" + nlong + "\n 总长：" + newPaintWidth + "\n 比例：" + nlong / newPaintWidth
                                + "\n 显示字数:" + (nlong / newPaintWidth * str.length() - 0.5));
                        if (layout3 == null)
                            layout3 = new LinearLayout(context);
                        layout3.setOrientation(LinearLayout.HORIZONTAL);
                        layout3.addView(textView2, layoutParams3);
                    } else if (size > 2 * screenWidth && size <= 3 * screenWidth) {
                        // 第三行

                        float nlong;// 所在行还剩多少位置可以用来显示文字
                        int first;
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        if (size - newPaintWidth < 2 * screenWidth) {
                            nlong = 2 * screenWidth - (size - newPaintWidth);
                            first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                            textView.setText(str.substring(0, first));
                            textView2.setText(str.substring(first));
                            layout3.addView(textView, layoutParams3);
                        } else {
                            nlong = 2 * screenWidth - (size - 2 * screenWidth - newPaintWidth);
                            textView2.setText(str);
                        }
                        if (layout4 == null) {
                            layout4 = new LinearLayout(context);
                            layout4.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout4.addView(textView2, layoutParams3);
                    } else if (size > 3 * screenWidth) {
                        // 第四行
                        float nlong;// 所在行还剩多少位置可以用来显示文字
                        int first;
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        if (size - newPaintWidth < 3 * screenWidth) {
                            nlong = 3 * screenWidth - (size - newPaintWidth);
                            first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                            textView.setText(str.substring(0, first));
                            textView2.setText(str.substring(first));
                            layout4.addView(textView, layoutParams3);
                        } else {
                            nlong = 3 * screenWidth - (size - 3 * screenWidth - newPaintWidth);
                            textView2.setText(str);
                        }
                        if (layout5 == null) {
                            layout5 = new LinearLayout(context);
                            layout5.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout5.addView(textView2, layoutParams3);
                    } else if (size > 4 * screenWidth) {
                        // 第五行
                        float nlong;// 所在行还剩多少位置可以用来显示文字
                        int first;
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        if (size - newPaintWidth < 4 * screenWidth) {
                            nlong = 4 * screenWidth - (size - newPaintWidth);
                            first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                            textView.setText(str.substring(0, first));
                            textView2.setText(str.substring(first));
                            layout5.addView(textView, layoutParams3);
                        } else {
                            nlong = 4 * screenWidth - (size - 4 * screenWidth - newPaintWidth);
                            textView2.setText(str);
                        }
                        if (layout6 == null) {
                            layout6 = new LinearLayout(context);
                            layout6.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout6.addView(textView2, layoutParams3);
                    } else if (size > 5 * screenWidth) {
                        // 第六行
                        float nlong;// 所在行还剩多少位置可以用来显示文字
                        int first;
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        if (size - newPaintWidth < 5 * screenWidth) {
                            nlong = 5 * screenWidth - (size - newPaintWidth);
                            first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                            textView.setText(str.substring(0, first));
                            textView2.setText(str.substring(first));
                            layout6.addView(textView, layoutParams3);
                        } else {
                            nlong = 5 * screenWidth - (size - 5 * screenWidth - newPaintWidth);
                            textView2.setText(str);
                        }
                        if (layout7 == null) {
                            layout7 = new LinearLayout(context);
                            layout7.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout7.addView(textView2, layoutParams3);
                    } else if (size > 6 * screenWidth) {
                        // 第七行
                        float nlong;// 所在行还剩多少位置可以用来显示文字
                        int first;
                        TextView textView2 = new TextView(context);
                        setTextStyle(textView2);
                        if (size - newPaintWidth < 6 * screenWidth) {
                            nlong = 6 * screenWidth - (size - newPaintWidth);
                            first = (int) (nlong / newPaintWidth * str.length() - 0.5);
                            textView.setText(str.substring(0, first));
                            textView2.setText(str.substring(first));
                            layout7.addView(textView, layoutParams3);
                        } else {
                            nlong = 6 * screenWidth - (size - 6 * screenWidth - newPaintWidth);
                            textView2.setText(str);
                        }
                        if (layout8 == null) {
                            layout8 = new LinearLayout(context);
                            layout8.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        layout8.addView(textView2, layoutParams3);
                    }
                    e.printStackTrace();
                    continue;
                }
                if (str.contains("[img=")) {
                    layoutParams4 = new LayoutParams(100, 100);
                } else
                    layoutParams4 = layoutParams2;
                if (size > screenWidth && size <= screenWidth * 2) {
                    // 换行，第二行
                    if (layout3 == null) {
                        layout3 = new LinearLayout(context);
                        layout3.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    layout3.addView(gifImageView, layoutParams4);
                    // size = 0f;
                } else if (size > screenWidth * 2 && size <= screenWidth * 3) {
                    // 第三行
                    if (layout4 == null) {
                        layout4 = new LinearLayout(context);
                        layout4.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    layout4.addView(gifImageView, layoutParams4);
                } else if (size <= screenWidth) {
                    // 第一行
                    layout2.addView(gifImageView, layoutParams4);
                } else if (size > screenWidth * 3 && size <= screenWidth * 4) {
                    //第四行
                    if (layout5 == null) {
                        layout5 = new LinearLayout(context);
                        layout5.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    layout5.addView(gifImageView, layoutParams4);
                } else if (size > 4 * screenWidth && size <= screenWidth * 5) {
                    //第五行
                    if (layout6 == null) {
                        layout6 = new LinearLayout(context);
                        layout6.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    layout6.addView(gifImageView, layoutParams4);
                } else if (size > 5 * screenWidth && size <= screenWidth * 6) {
                    //第六行
                    if (layout7 == null) {
                        layout7 = new LinearLayout(context);
                        layout7.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    layout7.addView(gifImageView, layoutParams4);
                } else if (size > 6 * screenWidth) {
                    //第七行
                    if (layout8 == null) {
                        layout8 = new LinearLayout(context);
                        layout8.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    layout8.addView(gifImageView, layoutParams4);
                }
            }

        }
        layout.addView(layout2, layoutParams);
        if (layout3 != null)
            layout.addView(layout3, layoutParams);
        if (layout4 != null)
            layout.addView(layout4, layoutParams);
        if (layout5 != null)
            layout.addView(layout5, layoutParams);
        if (layout6 != null)
            layout.addView(layout6, layoutParams);
        if (layout7 != null)
            layout.addView(layout7, layoutParams);
        if (layout8 != null)
            layout.addView(layout8, layoutParams);
        if (layout9 != null)
            layout.addView(layout9, layoutParams);
        if (layout10 != null)
            layout.addView(layout10, layoutParams);
        if (layout11 != null)
            layout.addView(layout11, layoutParams);

    }

    /**
     * 截取字符串，将其分为普通字符串和表情字符串
     *
     * @param string
     */
    private static ArrayList SplitString(String string) {
        String zhengze = "\\[[^\\]]+\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = sinaPatten.matcher(string);
        int start = 0, end = 0;
        ArrayList<String> strings = new ArrayList<String>();
        while (matcher.find()) {
            final String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < 0) {
                continue;
            }
            int strsize = strings.size();
            if (strsize > 0 && isHavaString2) {
                strings.remove(strsize - 1);
            }

            start = string.indexOf(key);
            end = start + key.length();

            if (start != 0)
                isHavaString1 = true;
            else
                isHavaString1 = false;
            if (end != string.length())
                isHavaString2 = true;
            else
                isHavaString2 = false;

            if (isHavaString1) {
                // "1_"、"2_"用以区分普通字符与表情字符
                strings.add("1_" + string.substring(0, start));
                strings.add("2_" + string.substring(start, end));
            } else
                strings.add("2_" + string.substring(start, end));
            if (isHavaString2) {
                strings.add("1_" + string.substring(end).trim());
            }
            string = string.substring(end).trim();
        }

        return strings;
    }

}
