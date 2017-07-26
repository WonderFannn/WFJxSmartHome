package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

/**
 * 重复日期选择Adapter
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class AddTimerSelectDaysAdapter extends BaseAdapter {
	
	private int type = -1;
	private String[] array = null;
	private String selectId = "";
	private List<Boolean> checkStatusList;
	
	public AddTimerSelectDaysAdapter(Context context,int type,String[] array) {
		if (array == null) { return;}
		checkStatusList = new ArrayList<Boolean>();
		this.type = type;
		this.array = array;
		for (int i = 0; i < array.length; i++) {
			checkStatusList.add(false);
		}
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
	
	/**
	 * @return 已选择的重复日期 ID
	 */
	public String getIdString(){
		if (!TextUtils.isEmpty(selectId)) {
			return selectId;
		}
		return "";
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final int pos = position;
		final String str = Integer.toString(pos+1);
		if(convertView==null){
			convertView=CommDefines.getSkinManager().view(R.layout.customer_add_days_item);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.repeatText.setText(array[pos]);
		holder.imgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkStatusList.get(pos) != holder.imgBtn.isChecked()) {
					checkStatusList.set(pos, holder.imgBtn.isChecked());
					if (checkStatusList.get(pos)) {
						if (!selectId.contains(str)) {
							selectId += str;
						}
					} else {
						if (selectId.contains(str)) {
							selectId = selectId.replace(str, "");
						}
					}
				}
			}
		});
		
		return convertView;
	}
	
	class ViewHolder{
		CheckBox imgBtn;
		TextView repeatText;
		public ViewHolder(View view) {
			imgBtn = (CheckBox) view.findViewById(R.id.time_select_cb);
			repeatText = (TextView) view.findViewById(R.id.time_repeat_text);
		}
	}

}
