package com.jyh.gxcjzbs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.jyh.gxcjzbs.R;
import com.jyh.gxcjzbs.bean.ChatEmoji_New;
import com.jyh.gxcjzbs.common.utils.volleyutil.BitmapCache;

import java.util.List;


/**
 *
 ******************************************
 * @文件名称 : FaceAdapter.java
 * @文件描述 : 表情填充器
 ******************************************
 */
public class FaceAdapter_New extends BaseAdapter {

	private List<ChatEmoji_New> data;

	private LayoutInflater inflater;

	private int size = 0;

	private ImageLoader loader;

	private boolean num;

	public FaceAdapter_New(Context context, List<ChatEmoji_New> list, boolean num) {
		this.inflater = LayoutInflater.from(context);
		this.data = list;
		this.size = list.size();
		this.num = num;
		loader = new ImageLoader(Volley.newRequestQueue(context), new BitmapCache());

	}

	@Override
	public int getCount() {
		return this.size;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatEmoji_New emoji = data.get(position);
		ViewHolder viewHolder = null;
		if (num) {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.item_caitiao, null);
				viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if ("R.drawable.face_del_icon".equals(emoji.getImage())) {
				convertView.setBackgroundDrawable(null);
				viewHolder.iv_face.setImageResource(R.drawable.face_del_icon);
			} else if (TextUtils.isEmpty(emoji.getImage())) {
				convertView.setBackgroundDrawable(null);
				viewHolder.iv_face.setImageDrawable(null);
			} else {
				viewHolder.iv_face.setTag(emoji);
				// viewHolder.iv_face.setImageResource(emoji.getId());
				setImage(viewHolder.iv_face, emoji, viewHolder);
			}
		} else {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.item_emoji, null);
				viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if ("R.drawable.face_del_icon".equals(emoji.getImage())) {
				convertView.setBackgroundDrawable(null);
				viewHolder.iv_face.setImageResource(R.drawable.face_del_icon);
			} else if (TextUtils.isEmpty(emoji.getImage())) {
				convertView.setBackgroundDrawable(null);
				viewHolder.iv_face.setImageDrawable(null);
			} else {
				viewHolder.iv_face.setTag(emoji);
				setImage(viewHolder.iv_face, emoji, viewHolder);
			}
		}
		return convertView;
	}

	private void setImage(ImageView iv_face, ChatEmoji_New emoji, ViewHolder viewHolder) {
		Bitmap bitmap = BitmapFactory.decodeFile(emoji.getPath());
		if (bitmap == null) {
			loader.get(emoji.getImage(), loader.getImageListener(iv_face, android.R.color.transparent, android.R.color.transparent));
		} else
			iv_face.setImageBitmap(bitmap);
	}

	class ViewHolder {

		public ImageView iv_face;
	}

}