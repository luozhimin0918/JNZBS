package com.jyh.byzb.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jyh.byzb.Login_One;
import com.jyh.byzb.MineActivity;
import com.jyh.byzb.R;
import com.jyh.byzb.Register_One;
import com.jyh.byzb.FunctionActivity;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.my_interface.LiveFunctionBtnChange;
import com.jyh.byzb.common.utils.SystemUtils;
import com.jyh.byzb.common.utils.LoginInfoUtils;
import com.jyh.byzb.common.utils.SPUtils;

import java.util.ArrayList;

/**
 * @author beginner
 * @version 1.0
 * @date 创建时间：2015年7月23日 下午2:47:19
 */
public class Fragment_function extends Fragment implements OnItemClickListener {

    private GridView gridView;
    private Intent intent2, intent3, intent4, intent5;
    private LiveFunctionBtnChange listener;
    private MyAdapter myAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBtnData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_function, null);
        gridView = (GridView) view.findViewById(R.id.gridViewId);

        myAdapter = new MyAdapter(getActivity(), imgs);
        gridView.setAdapter(myAdapter);
        gridView.setOnItemClickListener(this);
        intent5 = new Intent(getActivity(), FunctionActivity.class);
        return view;
    }

    /**
     * 初始化按钮
     */
    // 图片数组
    private ArrayList<Integer> imgs = new ArrayList<>();

    private void initBtnData() {


    }

    // 自定义适配器
    class MyAdapter extends BaseAdapter {
        // 上下文对象
        private Context context;
        private ArrayList<Integer> imgs;

        MyAdapter(Context context, ArrayList<Integer> imgs) {
            this.context = context;
            this.imgs = imgs;
        }

        public int getCount() {
            return imgs.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        // 创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_function, null);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    (int) ((gridView.getHeight() - 10 * SystemUtils.getDpi(getActivity())) / 3));
            imageView = (ImageView) convertView.findViewById(R.id.img);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);// 设置刻度的类型
            convertView.setLayoutParams(params);
            imageView.setImageResource(imgs.get(position));// 为ImageView设置图片资源
            return convertView;
        }

        public void setData(ArrayList<Integer> data) {
            this.imgs = data;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (imgs.get(position)) {
            case R.drawable.function4:
                // 快讯
                listener.isChange(true);
                intent5.putExtra("type", 1);
                intent5.putExtra("from", "live");
                startActivity(intent5);
                break;
            case R.drawable.function1:
                // 行情
                listener.isChange(true);
                intent5.putExtra("type", 2);
                intent5.putExtra("from", "live");
                startActivity(intent5);
                break;
            case R.drawable.function3:
                // 日历
                listener.isChange(true);
                intent5.putExtra("type", 3);
                intent5.putExtra("from", "live");
                startActivity(intent5);
                break;
            case R.drawable.function2:
                // 数据
                listener.isChange(true);
                intent5.putExtra("type", 4);
                intent5.putExtra("from", "live");
                startActivity(intent5);
                break;
            case R.drawable.function5:
                //课程
                listener.isChange(true);
                intent5.putExtra("type", 5);
                intent5.putExtra("from", "live");
                startActivity(intent5);
                break;
            case R.drawable.function6:
                // 公告
                listener.isChange(true);
                intent5.putExtra("type", 6);
                intent5.putExtra("from", "live");
                startActivity(intent5);
                break;
            case R.drawable.function8:
                // 登录
                listener.isChange(true);
                intent4 = new Intent(getActivity(), MineActivity.class);
                startActivity(intent4);

                break;
            case R.drawable.function9:
                // 注册
//                listener.isChange(true);
                intent2 = new Intent(getActivity(), Register_One.class);
                intent2.putExtra("from", "live");
                startActivity(intent2);
                break;
            case R.drawable.function7:
                //退出
                listener.isChange(false);
                getActivity().finish();
                break;
            case R.drawable.function10:
                //登录
//                listener.isChange(true);
                intent3 = new Intent(getActivity(), Login_One.class);
                intent3.putExtra("from", "live");
                startActivity(intent3);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        imgs.clear();
        if (!TextUtils.isEmpty(SPUtils.getString(getContext(), SpConstant.APPINFO_ALTERS_URL)))
            imgs.add(R.drawable.function4);
        if (!TextUtils.isEmpty(SPUtils.getString(getContext(), SpConstant.APPINFO_HQ_URL)))
            imgs.add(R.drawable.function1);
        if (!TextUtils.isEmpty(SPUtils.getString(getContext(), SpConstant.APPINFO_CJRL_URL)))
            imgs.add(R.drawable.function3);
        if (!TextUtils.isEmpty(SPUtils.getString(getContext(), SpConstant.APPINFO_FN_NAV_URL)))
            imgs.add(R.drawable.function2);
        if (!TextUtils.isEmpty(SPUtils.getString(getContext(), SpConstant.APPINFO_COURSE_URL)))
            imgs.add(R.drawable.function5);
        if (!TextUtils.isEmpty(SPUtils.getString(getContext(), SpConstant.APPINFO_BULLETIN_URL)))
            imgs.add(R.drawable.function6);

        if (LoginInfoUtils.isLogin(getContext())) {
            imgs.add(R.drawable.function8);
        } else {
//            if (!TextUtils.isEmpty(SPUtils.getString(getContext(), SpConstant.APPINFO_REGISTER_URL)))
            imgs.add(R.drawable.function9);
            imgs.add(R.drawable.function10);
        }
        imgs.add(R.drawable.function7);
        if (myAdapter != null) {
            myAdapter.setData(imgs);
        } else {
            myAdapter = new MyAdapter(getActivity(), imgs);
            gridView.setAdapter(myAdapter);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (LiveFunctionBtnChange) activity;
        } catch (Exception e) {
        }
    }
}
