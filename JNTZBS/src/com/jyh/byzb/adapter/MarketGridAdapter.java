package com.jyh.byzb.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.jyh.byzb.Login_One;
import com.jyh.byzb.R;
import com.jyh.byzb.WebActivity;
import com.jyh.byzb.bean.NavIndextEntity;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.my_interface.OnBtnClickL;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.dialogutils.BaseAnimatorSet;
import com.jyh.byzb.common.utils.dialogutils.BounceTopEnter;
import com.jyh.byzb.common.utils.dialogutils.NormalDialog;
import com.jyh.byzb.common.utils.dialogutils.SlideBottomExit;
import com.jyh.byzb.view.AlertDialog;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/18.
 */

public class MarketGridAdapter extends BaseListAdapter<NavIndextEntity.DataBean.ButtonBean> {

    private Context mContext;
    private LayoutInflater mInflater;
    Intent intent2;
    public MarketGridAdapter(Context mContext, List<NavIndextEntity.DataBean.ButtonBean> dataList) {
        super(dataList);
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        intent2 = new Intent(mContext, WebActivity.class);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_home_header_btn, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.nameTv = (TextView) convertView.findViewById(R.id.tv);
            mViewHolder.photoIv = (ImageView) convertView.findViewById(R.id.iv);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        final NavIndextEntity.DataBean.ButtonBean userBean = dataList.get(position);
        mViewHolder.nameTv.setText(userBean.getTitle());
        try {
            Glide.with(mContext).load(userBean.getImage()).error(R.drawable.icon_default).placeholder(R.drawable.icon_default).into
                    (mViewHolder.photoIv);
        } catch (Exception e) {
            e.printStackTrace();
            Glide.with(mContext).load(R.drawable.icon_default).into(mViewHolder.photoIv);
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               List<String>  banAceessRole = dataList.get(position).getBan_access_role();
                String loginRid = SPUtils.getString(mContext, SpConstant.USERINFO_LOGIN_RID);//
                if(banAceessRole!=null&&banAceessRole.size()>0&&banAceessRole.contains(loginRid)){
                    new AlertDialog(mContext)
                            .builder()
                            .setCancelable(true)
                            .setTitle("温馨提醒")
                            .setMsg(""+dataList.get(position).getBan_access_msg())
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })
                            .show();

                }else {
                    if(!TextUtils.isEmpty(dataList.get(position).getUrl())){
                        intent2.putExtra("url", dataList.get(position).getUrl());
                        intent2.putExtra("from", "main");
                        intent2.putExtra("title", dataList.get(position).getTitle());
                        mContext.startActivity(intent2);
                    }

                }
            }
        });

        return convertView;
    }
    /**
     * 显示登录Dialog
     */
    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;

    private void showLoginDialog(final String fromeTo) {
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();
        final NormalDialog dialog = new NormalDialog(mContext);
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
                if (!LoginInfoUtils.isLogin(mContext)) {
                    Intent intent = new Intent(mContext, Login_One.class);
                    if(fromeTo.equals("zb")){
                        intent.putExtra("from", "zb");
                    }else{
                        intent.putExtra("from", "null");
                    }

                    mContext.startActivity(intent);
                }
            }
        });
    }
    class ViewHolder {
        private TextView nameTv;
        private ImageView photoIv;



        public TextView getNameTv() {
            return nameTv;
        }

        public void setNameTv(TextView nameTv) {
            this.nameTv = nameTv;
        }

        public ImageView getPhotoIv() {
            return photoIv;
        }

        public void setPhotoIv(ImageView photoIv) {
            this.photoIv = photoIv;
        }
    }

}
