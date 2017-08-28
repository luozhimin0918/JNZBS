package com.jyh.byzb.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.byzb.Login_One;
import com.jyh.byzb.R;
import com.jyh.byzb.UserActivity;
import com.jyh.byzb.bean.KXTApplication;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.utils.SystemUtils;
import com.jyh.byzb.common.utils.imageutils.ImageDownLoader;
import com.jyh.byzb.common.utils.imageutils.ImageDownLoader.AsyncImageLoaderListener;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.ToastView;
import com.jyh.byzb.common.utils.dialogutils.BaseAnimatorSet;
import com.jyh.byzb.common.utils.dialogutils.BounceTopEnter;
import com.jyh.byzb.common.utils.dialogutils.NormalDialog;
import com.jyh.byzb.common.my_interface.OnBtnClickL;
import com.jyh.byzb.common.utils.dialogutils.SlideBottomExit;
import com.jyh.byzb.common.utils.NetworkCenter;
import com.jyh.byzb.common.utils.VersionManager;

import java.lang.ref.WeakReference;

public class fragment_self extends Fragment implements OnClickListener {
    private LinearLayout self_ll_clear, self_ll_about, self_ll_out, self_ll_tj, self_user;
    private KXTApplication application;
    private ImageView self_userimg;
    private TextView self_username;
    protected WeakReference<View> mRootView;
    private Context context;
    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        context.setTheme(R.style.BrowserThemeDefault);
        if (mRootView == null || mRootView.get() == null) {
            View view = inflater.inflate(R.layout.fragment_self, container, false);
            InitFind(view);
            Init();
            mRootView = new WeakReference<>(view);
        } else {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
        }
        return mRootView.get();
    }

    private void Init() {
        self_ll_about.setOnClickListener(this);
        self_ll_clear.setOnClickListener(this);
        self_ll_out.setOnClickListener(this);
        self_ll_tj.setOnClickListener(this);
        self_user.setOnClickListener(this);
    }

    private void InitFind(View view) {
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();
        self_ll_about = (LinearLayout) view.findViewById(R.id.self_ll_about);
        self_ll_clear = (LinearLayout) view.findViewById(R.id.self_ll_clear);
        self_ll_out = (LinearLayout) view.findViewById(R.id.self_ll_out);
        self_ll_tj = (LinearLayout) view.findViewById(R.id.self_ll_tj);
        self_user = (LinearLayout) view.findViewById(R.id.self_user);
        self_userimg = (ImageView) view.findViewById(R.id.self_userimg);
        self_username = (TextView) view.findViewById(R.id.self_username);
        application = (KXTApplication) ((Activity) context).getApplication();
    }

    private void setuserinfo() {
        if (LoginInfoUtils.isLogin(getContext())) {
            // 登录有效
            self_username.setText(SPUtils.getString(getContext(), SpConstant.USERINFO_LOGIN_NAME));
        } else {
            self_username.setText(SPUtils.getString(getContext(), SpConstant.USERINFO_NAME));
        }
        String imageUrl = SPUtils.getString(getContext(), SpConstant.USERINFO_IMAGE);
        Bitmap bm = new ImageDownLoader(getActivity()).getBitmapCache(imageUrl);
        if (bm != null) {
            self_userimg.setImageBitmap(bm);
        } else {
            new ImageDownLoader(getActivity()).loadImage(imageUrl, new AsyncImageLoaderListener() {

                @Override
                public void onImageLoader(Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    self_userimg.setImageBitmap(bitmap);
                }
            });
        }
    }

    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setuserinfo();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 30:
                    ToastView.makeText(context, "版本信息获取异常,请稍后再试");
                    break;
                case 60:
                    String description = SPUtils.getString(getContext(), SpConstant.VERSION_DESCRIPTION);
                    final String versionurl = SPUtils.getString(getContext(), SpConstant.VERSION_URL);
                    new AlertDialog.Builder(getActivity()).setTitle("更新提示").setMessage(description)
                            .setPositiveButton("是", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(versionurl));
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }).setNegativeButton("否", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    }).show();
                    break;
                case 70:
                    ToastView.makeText(getActivity(), "已经是最高版本");
                    break;
                case 100:
                    if (ImageDownLoader.cacheFileDir.exists()) {
                        ToastView.makeText(application, "清理缓存");
                        handler.sendEmptyMessageDelayed(101, 2 * 1000);
                        SystemUtils.delFile(ImageDownLoader.cacheFileDir, true);
                    } else {
                        handler.sendEmptyMessageDelayed(102, 2 * 1000);
                    }
                    break;
                case 101:
                    ToastView.makeText(application, "清理成功");
                    break;
                case 102:
                    ToastView.makeText(application, "没有缓存");
                    break;
                case 103:
                    VersionManager manager = VersionManager.getInstance();
                    manager.checkVersion(getActivity(), handler);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View arg0) {
        Intent intent;
        switch (arg0.getId()) {
            case R.id.self_ll_about:
                if (NetworkCenter.checkNetworkConnection(getActivity())) {
                    ToastView.makeText(getActivity(), "版本检测中。。。");
                    handler.sendEmptyMessageDelayed(103, 2 * 1000);
                } else {
                    ToastView.makeText(getActivity(), "网络异常");
                }
                break;
            case R.id.self_ll_clear:
                ToastView.makeText(application, "检测缓存中");
                handler.sendEmptyMessage(100);
                break;
            case R.id.self_ll_out:
                final NormalDialog dialog = new NormalDialog(context);
                dialog.isTitleShow(false)
                        // 设置背景颜色
                        .bgColor(Color.parseColor("#383838"))
                        // 设置dialog角度
                        .cornerRadius(5)
                        // 设置内容
                        .content("是否确定退出程序?")
                        // 设置居中
                        .contentGravity(Gravity.CENTER)
                        // 设置内容字体颜色
                        .contentTextColor(Color.parseColor("#ffffff"))
                        // 设置线的颜色
                        .dividerColor(Color.parseColor("#222222"))
                        // 设置字体
                        .btnTextSize(15.5f, 15.5f)
                        // 设置取消确定颜色
                        .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                        .btnPressColor(Color.parseColor("#2B2B2B"))//
                        .widthScale(0.85f)//
                        .showAnim(bas_in)//
                        .dismissAnim(bas_out)//
                        .show();

                dialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        application.exitAppAll();
                    }
                });
                break;
            case R.id.self_user:
                boolean isLogin=LoginInfoUtils.isLogin(getContext());
                if (isLogin) {
                    intent = new Intent(getActivity(), UserActivity.class);
                    intent.putExtra("isLogin", isLogin);
                    startActivityForResult(intent, 50);
                } else {
                    intent = new Intent(getActivity(), Login_One.class);
                    intent.putExtra("from", "self");
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    public interface OnFragmentListener {
        void onFragmentAction();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50 && resultCode == 666) {
            self_username.setText(SPUtils.getString(getContext(), SpConstant.USERINFO_NAME));
        }
    }
}
