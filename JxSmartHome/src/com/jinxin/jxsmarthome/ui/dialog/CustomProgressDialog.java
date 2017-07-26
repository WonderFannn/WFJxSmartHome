package com.jinxin.jxsmarthome.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;

/**
 * 自定义ProgressDialog
 * 使用方式：
 * 		CustomProgressDialog.createDialog(context).setMessage(msg).show();
 * @author TangLong
 * @company 金鑫智慧
 */
public class CustomProgressDialog extends Dialog {
	private Context context;
	private static CustomProgressDialog customDialog;
	
	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public static CustomProgressDialog createDialog(Context context) {
		if(customDialog == null) {
			customDialog = new CustomProgressDialog(context, R.style.custom_dialog_loading);
			customDialog.setContentView(R.layout.custom_dialog_loading);
			customDialog.getWindow().getAttributes().gravity = Gravity.CENTER; 
		}
		return customDialog;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(customDialog == null) {
			return;
		}
		ImageView imageView = (ImageView) customDialog.findViewById(R.id.custom_dialog_loading_img);  
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_loading);
		imageView.startAnimation(animation); 
	}
	
	public CustomProgressDialog setMessage(String strMessage){  
        TextView tvMsg = (TextView)customDialog.findViewById(R.id.custom_dialog_loading_text);  
          
        if (tvMsg != null){  
            tvMsg.setText(strMessage);  
        }  
          
        return customDialog;  
    } 
	
	public void dismissDailog(){
		customDialog = null;
		context = null;
	}
}
