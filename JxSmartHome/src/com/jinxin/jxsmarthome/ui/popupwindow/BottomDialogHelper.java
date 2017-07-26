package com.jinxin.jxsmarthome.ui.popupwindow;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.jinxin.jxsmarthome.R;

public class BottomDialogHelper {
	public static Dialog showDialogInBottom(Context context, View contentView, Dialog d) {
		if(d != null && d.isShowing()) {
			return d;
		}
		
		Dialog dialog = new Dialog(context, R.style.BottomDialog);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wLayoutParms = new WindowManager.LayoutParams();
		
		int wh[] = getDeviceWH(context);
		wLayoutParms.x = 0;
		wLayoutParms.y = wh[1];
		
		window.setAttributes(wLayoutParms);
		window.setBackgroundDrawableResource(R.color.white);
		
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(contentView);
		
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		dialog.show();
		return dialog;
	}
	
	public static Dialog showDialogInBottom(Context context, Dialog dialog) {
		if(dialog != null && dialog.isShowing()) {
			return dialog;
		}
		
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wLayoutParms = new WindowManager.LayoutParams();
		
		int wh[] = getDeviceWH(context);
		wLayoutParms.x = 0;
		wLayoutParms.y = wh[1];
		
		window.setAttributes(wLayoutParms);
		window.setBackgroundDrawableResource(R.drawable.ico_menjin_bg_03);
		
		dialog.setCanceledOnTouchOutside(true);
		
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		dialog.show();
		return dialog;
	}
	
	public static int[] getDeviceWH(Context context) {
		int wh[] = new int[2];
		int w = 0;
		int h = 0;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels;
		h = dm.heightPixels;
		wh[0] = w;
		wh[1] = h;
		return wh;
	}
}	

