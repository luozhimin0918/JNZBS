package com.jyh.gxcjzbs.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;

import com.gensee.common.ServiceType;
import com.gensee.entity.InitParam;
import com.gotye.live.core.Code;
import com.gotye.live.core.GLCore;
import com.gotye.live.core.model.AuthToken;
import com.gotye.live.core.model.RoomIdType;
import com.jyh.gxcjzbs.GenseeActivity;
import com.jyh.gxcjzbs.GotyeLiveActivity;
import com.jyh.gxcjzbs.Login_One;
import com.jyh.gxcjzbs.R;
import com.jyh.gxcjzbs.WebActivity;
import com.jyh.gxcjzbs.adapter.MarketGridAdapter;
import com.jyh.gxcjzbs.bean.KXTApplication;
import com.jyh.gxcjzbs.bean.NavIndextEntity;
import com.jyh.gxcjzbs.common.constant.SpConstant;
import com.jyh.gxcjzbs.common.constant.UrlConstant;
import com.jyh.gxcjzbs.common.utils.LoginInfoUtils;
import com.jyh.gxcjzbs.common.utils.NetworkCenter;
import com.jyh.gxcjzbs.common.utils.SPUtils;
import com.jyh.gxcjzbs.common.utils.SystemUtil;
import com.jyh.gxcjzbs.common.utils.ToastView;
import com.jyh.gxcjzbs.common.utils.dialogutils.BaseAnimatorSet;
import com.jyh.gxcjzbs.common.utils.dialogutils.BounceTopEnter;
import com.jyh.gxcjzbs.common.utils.dialogutils.NormalDialog;
import com.jyh.gxcjzbs.common.my_interface.OnBtnClickL;
import com.jyh.gxcjzbs.common.utils.dialogutils.SlideBottomExit;
import com.jyh.gxcjzbs.view.AlertDialog;
import com.jyh.gxcjzbs.view.PageLoadLayout;
import com.jyh.gxcjzbs.view.RollDotViewPager;
import com.jyh.gxcjzbs.view.RollViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT;

/**
 * @author Administrator
 */
public class fragment_zb extends Fragment implements OnClickListener {

    private Intent intent2;

    // Gotye视频所需参数
    private boolean isCancel = false;
    private LoginThread mLoginThread;
    private ProgressDialog loginDialog;

    private Bitmap bitmap;
    private View view;
    ConvenientBanner convenientBanner;
    RollDotViewPager rollDotViewpager;
    PageLoadLayout pageLoadLayout;
    LinearLayout rollLiner;
    ImageView  playBtn,playBigBtn;
    private RequestQueue queue;
    private KXTApplication application;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        application = (KXTApplication) getActivity().getApplication();
        queue = application.getQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.fragment_zb_new, null);
        findView(view);
        pageLoadLayout.setOnAfreshLoadListener(new PageLoadLayout.OnAfreshLoadListener() {
            @Override
            public void OnAfreshLoad() {
                netOk();
            }
        });
        pageLoadLayout.startLoading();
        netOk();

        playBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                joinLive();
            }
        });
        playBigBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                joinLive();
            }
        });
        return view;
    }

    private static List<NavIndextEntity.DataBean.SlideshowBean> slideShow;
    private List<NavIndextEntity.DataBean.ButtonBean> buttonShow;
    private void netOk() {
        if(!NetworkCenter.isNetworkConnected(getContext())){
            pageLoadLayout.loadError("请检查网络连接");
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(UrlConstant.URL_NAV_INDEX, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    NavIndextEntity  navinEntity=JSON.parseObject(jsonObject.toString(),NavIndextEntity.class);
                    if(navinEntity!=null&&navinEntity.getCode()==200){
                        slideShow=navinEntity.getData().getSlideshow();
                        buttonShow=navinEntity.getData().getButton();
                        if(slideShow==null&&buttonShow==null||slideShow.size()==0&&buttonShow.size()==0){
                            playBigBtn.setVisibility(View.VISIBLE);
                        }else if(slideShow!=null&&slideShow.size()>0){
                            optionView();
                        }else{
                            convenientBanner.setVisibility(View.GONE);
                        }
                        if(buttonShow!=null&&buttonShow.size()>0){
                            optionViewTwo();
                        }
                        pageLoadLayout.loadSuccess();
                        Log.d("zb___",jsonObject.toString());
                    }else{
                        Log.d("zb___","数据请求失败");
                        pageLoadLayout.loadNoData("暂无数据");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    pageLoadLayout.loadNoData("暂无数据");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pageLoadLayout.loadError("数据请求失败");
            }
        });
        queue.add(request);
    }
    private String[] images = {"http://img2.imgtn.bdimg.com/it/u=3093785514,1341050958&fm=21&gp=0.jpg",
            "http://img2.3lian.com/2014/f2/37/d/40.jpg",
            "http://img2.3lian.com/2014/f2/37/d/39.jpg",
    };
    private List<String> imageList=new ArrayList<>();
    private void optionView() {
        imageList.clear();
        for(NavIndextEntity.DataBean.SlideshowBean jj:slideShow){
            imageList.add(jj.getImage());

        }
        if(imageList!=null&&imageList.size()==1){
            convenientBanner.setCanLoop(false);
        }
        convenientBanner.startTurning(4000);
//        convenientBanner.setPageTransformer(new AccordionTransformer());
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },imageList)
        .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        List<String>  banAccessRole =slideShow.get(position).getBan_access_role();
                        String loginRid = SPUtils.getString(getContext(), SpConstant.USERINFO_LOGIN_RID);//
                        if(banAccessRole!=null&&banAccessRole.size()>0&&banAccessRole.contains(loginRid)){
                            new AlertDialog(getContext())
                                    .builder()
                                    .setCancelable(true)
                                    .setTitle("温馨提醒")
                                    .setMsg(""+slideShow.get(position).getBan_access_msg())
                                    .setPositiveButton("确定", new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                        }
                                    })
                                    .show();


                        }else{
                            if(!TextUtils.isEmpty(slideShow.get(position).getUrl())){
                                intent2.putExtra("url", slideShow.get(position).getUrl());
                                intent2.putExtra("from", "main");
                                intent2.putExtra("title", slideShow.get(position).getTitle());
                                startActivity(intent2);
                            }

                        }

                    }
                });;


    }

    private void optionViewTwo() {
       /* for(int i=0;i<3;i++){
            NavIndextEntity.DataBean.ButtonBean  buttonBean=new NavIndextEntity.DataBean.ButtonBean();
            buttonBean.setImage("http://cdn0.108tec.com/gxsp/Uploads/Picture/2017-08-07/598801f1a584d.png");
            buttonBean.setTitle("wotu"+i);
            buttonBean.setUrl("wwww.baidu.com");
            buttonShow.add(buttonBean);
        }*/
        rollDotViewpager=new RollDotViewPager(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                SystemUtil.dp2px(getContext(), 115));

        rollDotViewpager.setLayoutParams(lp);

        rollDotViewpager.setShowPaddingLine(false);
        RollViewPager recommendView = rollDotViewpager.getRollViewPager();
        recommendView
                .setGridMaxCount(4)
                .setDataList(buttonShow)
                .setGridViewItemData(new RollViewPager.GridViewItemData() {
                    @Override
                    public void itemData(List dataSubList, GridView gridView) {
                        MarketGridAdapter adapter = new MarketGridAdapter(getContext(),dataSubList);
                        gridView.setAdapter(adapter);
                    }
                });
        rollDotViewpager.build();
        rollLiner.addView(rollDotViewpager);

    }
    public class NetworkImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }
        @Override
        public void UpdateUI(Context context, int position, String data) {
            imageView.setImageResource(R.drawable.ic_default_adimage);
            ImageLoader.getInstance().displayImage(data,imageView);
        }
    }

    /**
     * 设置背景图
     */


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            bitmap.recycle();
            bitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findView(View view) {
        // TODO Auto-generated method stub
        convenientBanner= (ConvenientBanner) view.findViewById(R.id.convenientBanner);
        pageLoadLayout=(PageLoadLayout)view.findViewById(R.id.page_load);
        rollLiner= (LinearLayout) view.findViewById(R.id.rollLiner);
        playBtn= (ImageView) view.findViewById(R.id.playBtn);
        playBigBtn= (ImageView) view.findViewById(R.id.playBigBtn);
        intent2 = new Intent(getActivity(), WebActivity.class);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
         /*   case R.id.img_jianjie:
                intent2.putExtra(
                        "url",
                        SPUtils.getString(getContext(), SpConstant.APPINFO_SUMMARY_URL));
                intent2.putExtra("from", "main");
                intent2.putExtra("title", "直播室简介");
                startActivity(intent2);
                break;
            case R.id.img_kefu:
                intent2.putExtra(
                        "url",
                        SPUtils.getString(getContext(), SpConstant.APPINFO_KEFU_URL));
                intent2.putExtra("from", "main");
                intent2.putExtra("title", "联系客服");
                startActivity(intent2);
                break;*/
            default:
                break;
        }
    }

    /**
     * 进入直播间
     */
    private void joinLive() {
        if (LoginInfoUtils.isCanJoin(getContext())) {
            String type = SPUtils.getString(getContext(), SpConstant.VIDEO_TYPE);
            Log.i("type1", type);
            if (type != null) {
                if ("live_gensee".equals(type)) {
                    initGensee();
                } else
                    attemptLogin();
            }
        } else {
            showLoginDialog("zb");
        }
    }

    private void initGensee() {
        loginDialog = new ProgressDialog(getActivity());
        loginDialog.setMessage("进入直播室。。。");
        loginDialog.setCancelable(true);
        loginDialog.setCanceledOnTouchOutside(true);
        loginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KXTApplication.initParam = null;
            }
        });

        handleProgress(true);
        KXTApplication.initParam = new InitParam();
        // domain
        KXTApplication.initParam.setDomain(SPUtils.getString(getContext(),SpConstant.VIDEO_GENSEE_SITE));
        // 编号（直播间号）,如果没有编号却有直播id的情况请使用setLiveId("此处直播id或课堂id");
        KXTApplication.initParam.setNumber(SPUtils.getString(getContext(),SpConstant.VIDEO_GENSEE_ID));
        KXTApplication.initParam.setLiveId(SPUtils.getString(getContext(),SpConstant.VIDEO_GENSEE_ROOMID));
        // 站点认证帐号，根据情况可以填""
        KXTApplication.initParam.setLoginAccount("");
        // 站点认证密码，根据情况可以填""
        KXTApplication.initParam.setLoginPwd("");
        // 昵称，供显示用
        KXTApplication.initParam.setNickName("");
        // 加入口令，没有则填""
        KXTApplication.initParam.setJoinPwd(SPUtils.getString(getContext(),SpConstant.VIDEO_GENSEE_PWD));

        // 判断serviceType类型
        // 站点类型ServiceType.ST_CASTLINE
        // 直播webcast，ServiceType.ST_MEETING
        // 会议meeting，ServiceType.ST_TRAINING 培训
        ServiceType serviceType = null;
        switch (SPUtils.getString(getContext(),SpConstant.VIDEO_GENSEE_CTXZ)) {
            case "webcast":
                serviceType = ServiceType.ST_CASTLINE;
                break;
            case "meeting":
                serviceType = ServiceType.ST_MEETING;
                break;
            case "training":
                serviceType = ServiceType.ST_TRAINING;
                break;
        }
        KXTApplication.initParam.setServiceType(serviceType);
        Intent intent = new Intent(getActivity(), GenseeActivity.class);
        intent.setFlags(FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        KXTApplication.IsOut = true;
        handleProgress(false);
        startActivity(intent);
    }

    /**
     * 显示登录Dialog
     */
    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;

    private void showLoginDialog(final String fromeTo) {
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();
        final NormalDialog dialog = new NormalDialog(getContext());
        dialog.isTitleShow(true)
                // 设置背景颜色
                .bgColor(Color.parseColor("#383838"))
                // 设置dialog角度
                .cornerRadius(5)
                // 设置内容
                .content("您好,您的权限不够,请先登录").title("温馨提示")
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
                if (!LoginInfoUtils.isLogin(getContext())) {
                    Intent intent = new Intent(getContext(), Login_One.class);
                    if(fromeTo.equals("zb")){
                        intent.putExtra("from", "zb");
                    }else{
                        intent.putExtra("from", "null");
                    }


                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void reJoin() {
        joinLive();
    }

    private void attemptLogin() {

        KXTApplication.player.stop();
        KXTApplication.core.clearAuth();
        KXTApplication.IsOut = false;

        KXTApplication.isFirst = true;
        mLoginThread = null;
        // Store values at the time of the login attempt.
        // String roomId = "100030";
        // String password = "000000";
        // String roomId = "101639";
        // String password = "333333";
        String roomId = SPUtils.getString(getContext(), SpConstant.VIDEO_GOTYEROOMID);
        String password = SPUtils.getString(getContext(), SpConstant.VIDEO_GOTYEPASSWORD);
        String nickname = "111";

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            cancel = true;
        }

        if (TextUtils.isEmpty(roomId)) {
            cancel = true;
        }

        if (TextUtils.isEmpty(nickname)) {
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginDialog = new ProgressDialog(getActivity());
            loginDialog.setMessage("进入直播室。。。");
            loginDialog.setCancelable(true);
            loginDialog.setCanceledOnTouchOutside(true);
            loginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mLoginThread != null) {
                        mLoginThread.cancel();
                        mLoginThread = null;

                        handleProgress(false);
                        KXTApplication.core.clearAuth();
                        return;
                    }
                }
            });
            handleProgress(true);

            mLoginThread = new LoginThread(roomId, password, nickname, RoomIdType.GOTYE);
            mLoginThread.start();
        }
    }

    private class LoginThread extends Thread {

        String roomId, password, nickaname;
        RoomIdType type;

        public LoginThread(String roomId, String password, String nickname, RoomIdType type) {
            this.roomId = roomId;
            this.password = password;
            this.nickaname = nickname;
            this.type = type;
            isCancel = false;
        }

        @Override
        public void run() {
            super.run();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 如果在登录时取消，则退出session
                    if (isCancel) {
                        KXTApplication.core.clearAuth();
                        mLoginThread = null;
                        handleProgress(false);
                        return;
                    }
                    // 首先到服务器验证session取得accessToken和role等信息
                    KXTApplication.core.auth(roomId, password, null, nickaname, type, new GLCore.Callback<AuthToken>() {
                        @Override
                        public void onCallback(int i, final AuthToken authToken) {
                            if (isCancel || i != Code.SUCCESS) {
                                // session验证失败
                                KXTApplication.core.clearAuth();
                                mLoginThread = null;
                                handleProgress(false);
                                ToastView.makeText(getActivity(), "session验证失败");
                                return;
                            }
                            handleProgress(false);
                            Intent intent = new Intent(getActivity(), GotyeLiveActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            startActivity(intent);
                        }
                    });

                }
            });
        }

        public void cancel() {
            isCancel = true;
            KXTApplication.core.clearAuth();
        }
    }

    private boolean isPasswordValid(String password) {
        // TODO: Replace this with your own logic
        return password.length() > 1;
    }

    private void handleProgress(final boolean show) {
        if (show) {
            loginDialog.show();
        } else {
            loginDialog.dismiss();
        }
    }
}
