
package com.jinxin.jxsmarthome.ui.dialog;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.util.CommUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * List 类型选择Dialog
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ListDialog extends Dialog {

    private LinearLayout linearLayout = null;

    private String[] strs;

    private Context mContext;

    private ListDialogOnItemClickListener listener = null;

    /**
     * List 类型选择Dialog
     * 
     * @param context Context
     * @param strs 内容文字
     * @param iconIds 内容图片ID
     */
    public ListDialog(Context context, String[] strs) {
        super(context, R.style.dialog);
        // TODO Auto-generated constructor stub
        this.strs = strs;
        mContext = context;
        init();
        this.setCanceledOnTouchOutside(true);
    }

    /**
     * List 类型选择Dialog
     * 
     * @param context Context
     * @param strs 内容文字
     * @param iconIds 内容图片ID
     */
    public ListDialog(Context context, int arrayId) {
        super(context, R.style.dialog);
        // TODO Auto-generated constructor stub
        this.strs = context.getResources().getStringArray(arrayId);
        mContext = context;
        init();
        this.setCanceledOnTouchOutside(true);
    }

    private void init() {
        linearLayout = new LinearLayout(mContext);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
//        layoutParams.width = CommUtil.getDisplayWidth((Activity)mContext);
//        layoutParams.height = CommUtil.getDisplayHeight((Activity)mContext);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.bg_title_pop);
        linearLayout.setGravity(Gravity.CENTER);
        initItems();
        this.setContentView(linearLayout);
    }

    /**
     * 初始化内容
     */
    private void initItems() {
        for (int i = 0; i < strs.length; i++) {
        	View view = null;
        	if (i == strs.length -2) {
        		view = LayoutInflater.from(mContext).inflate(
						R.layout.list_dialog_item_for_cancle, null);
			}else{
				view = LayoutInflater.from(mContext).inflate(
						R.layout.list_dialog_item, null);
			}
        	

            Button button = (Button) view.findViewById(R.id.list_dialog_button);
            button.setText(strs[i]);

            button.setId(i);
            button.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (listener != null) {
                        listener.onItemClick(ListDialog.this, v, v.getId());
                    }
                }
            });
            linearLayout.addView(view);
        }
    }

    /**
     * Set Listener
     * 
     * @param listener Listener
     */
    public void setOnItemClickListener(ListDialogOnItemClickListener listener) {
        this.listener = listener;
    }
}
