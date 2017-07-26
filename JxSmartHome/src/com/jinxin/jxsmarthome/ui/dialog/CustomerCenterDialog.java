package com.jinxin.jxsmarthome.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class CustomerCenterDialog extends Dialog {
	
	private Context context;

	public CustomerCenterDialog(Context context) {
		super(context);
	}

	@SuppressWarnings("deprecation")
	public CustomerCenterDialog(Context context, int theme, View view) {
		super(context, theme);
		this.context = context;
		setContentView(view);
		
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		this.getWindow().setGravity(Gravity.CENTER);
		lp.width = (int) (this.getWindow().getWindowManager().getDefaultDisplay().getWidth() * 0.85);
		this.getWindow().setAttributes(lp);
	}
	
	
}
