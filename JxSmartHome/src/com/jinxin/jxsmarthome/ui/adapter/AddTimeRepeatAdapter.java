package com.jinxin.jxsmarthome.ui.adapter;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 任务重复选择
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class AddTimeRepeatAdapter extends BaseAdapter {
	
	private int type = -1;
	private String[] array = null;
	
	public AddTimeRepeatAdapter(Context context,int type,String[] array) {
		if (array == null) { return;}
		this.type = type;
		this.array = array;
	}

	@Override
	public int getCount() {
		return array == null ? 0 : array.length;
	}

	@Override
	public Object getItem(int position) {
		return array[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final int pos = position;
		if(convertView==null){
			convertView=CommDefines.getSkinManager().view(R.layout.customer_add_timer_item);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		if (type == pos) {
			holder.imgBtn.setImageResource(R.drawable.ico_checkbox_checked);
		}else{
			holder.imgBtn.setImageResource(R.drawable.ico_checkbox_unchecked);
		}
		holder.repeatText.setText(array[pos]);
		return convertView;
	}
	
	class ViewHolder{
		ImageView imgBtn;
		TextView repeatText;
		public ViewHolder(View view) {
			imgBtn = (ImageView) view.findViewById(R.id.time_select_btn);
			repeatText = (TextView) view.findViewById(R.id.time_repeat_text);
		}
	}

}
