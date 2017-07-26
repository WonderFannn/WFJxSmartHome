package com.jinxin.jxsmarthome.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;

/**
 * 自定义Loading
 * @author TangLong
 * @company 金鑫智慧
 * @warn
 * 		建议使用CustomProgressDialog类
 */
public class CustomProgressDialogCreater {
	private static Dialog mDialog;
	
	public static Dialog showLoadingDialog(Context context, String msg) {
		if(mDialog == null) {
			mDialog = createLoadingDialog(context, msg);
		}else {
			((TextView)mDialog.findViewById(R.id.custom_dialog_loading_text)).setText(msg);
		}
		return mDialog;
	}
	
	public static Dialog createLoadingDialog(Context context, String msg) {
		Dialog loadingDialog = new Dialog(context, R.style.custom_dialog_loading);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.custom_dialog_loading, null);
		
		TextView loadingText = (TextView)view.findViewById(R.id.custom_dialog_loading_text);
		ImageView loadingImg = (ImageView)view.findViewById(R.id.custom_dialog_loading_img);
		
		loadingText.setText(msg);
		
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_loading);
		loadingImg.startAnimation(animation);
		
		loadingDialog.setCancelable(true);
		loadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		
		return loadingDialog;
	}
}
