
package com.jinxin.jxsmarthome.ui.widget;


import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 多Button 提示框
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class MessageBox extends Dialog {
	public static final int MB_NONE = 0;
	public static final int MB_OK = 1;
	public static final int MB_CANCEL = 2;
	private int result = MB_NONE;
	
	private Button button_ok;
	private Button button_cancel;
	
	public MessageBox(Context context, String title, String content, int type) {
		super(context, R.style.dialog);
		setContentView(CommDefines.getSkinManager().view(R.layout.update_info_layout));
		
		TextView textview_title = (TextView)findViewById(R.id.textview_title);
		textview_title.setText(title.trim());
		
		TextView textview_content = (TextView)findViewById(R.id.textview_content);
		textview_content.setText(content.trim());
		
		this.button_ok = (Button)findViewById(R.id.button_ok);
		button_ok.setVisibility((type & MB_OK) == MB_OK ? View.VISIBLE : View.GONE);
		button_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				result = MB_OK;
				dismiss();
			}
		});
		this.button_cancel = (Button)findViewById(R.id.button_cancel);
		button_cancel.setVisibility((type & MB_CANCEL) == MB_CANCEL ? View.VISIBLE : View.GONE);
		button_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				result = MB_CANCEL;
				dismiss();
			}
		});
	}
	/**
	 * 设置左右键文字
	 * @param ok
	 * @param cancel
	 */
	public void setButtonText(String ok,String cancel){
		if(!TextUtils.isEmpty(ok)){
			this.button_ok.setText(ok);
		}
		if(!TextUtils.isEmpty(cancel)){
			this.button_cancel.setText(cancel);
		}
	}
	public int getResult(){
		return result;
	}
}
