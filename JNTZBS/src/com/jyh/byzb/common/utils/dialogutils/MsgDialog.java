package com.jyh.byzb.common.utils.dialogutils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jyh.byzb.R;

public class MsgDialog extends Dialog implements android.view.View.OnClickListener {
	private String text;

	public MsgDialog(Context context, String text) {
		super(context, R.style.changename_dialog_style);
		this.text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_changename);
		TextView txt = (TextView) findViewById(R.id.dialog_cn_title);
		txt.setText(text);
		Button confirm = (Button) findViewById(R.id.dialog_cn_btn);
		confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		MsgDialog.this.dismiss();
	}

}
