package com.jyh.gxcjzbs.sqlte;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SCDataSqlte extends SQLiteOpenHelper {

	public SCDataSqlte(Context context) {
		super(context, "data", null, 5);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		// private String id;
		// private String name;//身份
		// private String type;
		// private String limit_chat_time;
		// private String power_whisper;
		// private String limit_colorbar_time;
		// private String power_upload_pic;
		// private String limit_account_time;
		// private String status;
		// private String sort;
		// private String power_visit_room;
		// private String style_chat_text;
		// private String image;
		arg0.execSQL("CREATE TABLE IF NOT EXISTS roomrole (_id integer primary key autoincrement, id text, name text, type text,"
				+ " limit_chat_time text,power_whisper text,limit_colorbar_time text,power_upload_pic text,limit_account_time text,status text"
				+ ",sort text,power_visit_room text,style_chat_text text,image text);");
		arg0.execSQL("CREATE TABLE IF NOT EXISTS emoji ( _id integer primary key autoincrement,name text,type text,image text,path text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		if (arg1 < arg2) {
			arg0.execSQL("DROP TABLE IF EXISTS roomrole");
			onCreate(arg0);
		}
	}

}
