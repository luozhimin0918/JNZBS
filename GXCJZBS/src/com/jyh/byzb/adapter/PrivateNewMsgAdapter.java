package com.jyh.byzb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.byzb.R;

import java.util.List;

/**
 * 项目名:GXCJZBS
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/11.
 */

public class PrivateNewMsgAdapter extends RecyclerView.Adapter<PrivateNewMsgAdapter.ViewHolder> {

    private Context context;
    private List list;
    private ViewGroup viewGroup;

    public PrivateNewMsgAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.viewGroup=viewGroup;
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_tv, viewGroup, false);
        inflate.getLayoutParams().height = viewGroup.getHeight();
        inflate.getLayoutParams().width = viewGroup.getWidth();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void add() {
        list.add("");
        notifyItemInserted(0);
    }

    public void remove() {
        list.clear();
        notifyItemRemoved(0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
