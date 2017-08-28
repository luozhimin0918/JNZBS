package com.jyh.gxcjzbs.adapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.gxcjzbs.PrivateChatActivity;
import com.jyh.gxcjzbs.R;
import com.jyh.gxcjzbs.bean.UserBean;
import com.jyh.gxcjzbs.common.constant.SpConstant;
import com.jyh.gxcjzbs.common.utils.LoginInfoUtils;
import com.jyh.gxcjzbs.common.utils.PrivateChatUtils;
import com.jyh.gxcjzbs.sqlte.SCDataSqlte;

import java.util.HashMap;
import java.util.List;

/**
 * 项目名:GXCJZBS
 * 类描述:私聊用户Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/5.
 */

public class PrivateChatUserAdapter extends BaseAdapter {

    private final HashMap<String, String> hashMap;
    private PrivateChatActivity context;
    private List<UserBean> users;
    private SCDataSqlte dataSqlte;

    public PrivateChatUserAdapter(PrivateChatActivity context, List<UserBean> users) {
        this.context = context;
        this.users = users;
        hashMap = new HashMap<>();
        dataSqlte = new SCDataSqlte(context);
        InitSqlData();
    }

    private void InitSqlData() {

        SQLiteDatabase db = dataSqlte.getReadableDatabase();
        if (db == null) {
            return;
        }
        Cursor cursor = db.query("roomrole", null, null, null, null, null, null);
        String roomroleid = null;
        String image = null;
        while (cursor.moveToNext()) {
            roomroleid = cursor.getString(cursor.getColumnIndex("id"));
            image = cursor.getString(cursor.getColumnIndex("image"));
            hashMap.put(roomroleid, image);
        }
        cursor.close();
        db.close();
    }

    @Override
    public int getCount() {
        return users == null ? 0 : users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_private_user, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.photoIv = (ImageView) convertView.findViewById(R.id.user_photo);
            viewHolder.rootView = convertView.findViewById(R.id.rootView);
            viewHolder.pointView = convertView.findViewById(R.id.red_point);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final UserBean userBean = users.get(position);
        viewHolder.nameTv.setText(userBean.getName());
        try {
            Glide.with(context).load(hashMap.get(userBean.getRid())).error(R.drawable.icon_17yk).placeholder(R.drawable.icon_17yk).into
                    (viewHolder.photoIv);
        } catch (Exception e) {
            e.printStackTrace();
            Glide.with(context).load(R.drawable.icon_17yk).into(viewHolder.photoIv);
        }
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivateChatUtils.savePrivateChatNewPoint(context, userBean.getUid(), PrivateChatUtils.POINT_HINT);
                notifyDataSetChanged();
                context.setQuit(false);
                context.getTUserInfo(userBean);
            }
        });

        if (PrivateChatUtils.isShowPoint(context, userBean.getUid()))
            viewHolder.pointView.setVisibility(View.VISIBLE);
        else
            viewHolder.pointView.setVisibility(View.GONE);

        return convertView;
    }

    public void setUsers(List userList) {
        this.users.clear();
        this.users.addAll(userList);
    }

    class ViewHolder {
        private TextView nameTv;
        private ImageView photoIv;
        private View rootView;
        private View pointView;

        public View getPointView() {
            return pointView;
        }

        public void setPointView(View pointView) {
            this.pointView = pointView;
        }

        public View getRootView() {
            return rootView;
        }

        public void setRootView(View rootView) {
            this.rootView = rootView;
        }

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
