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
public class AmendTimerSelectDaysAdapter extends BaseAdapter {
	
	private int type = -1;
	private String[] array = null;
	private String selectStr = "";
	private String expression = "";
	private String[] cornArr = null;
	private List<Boolean> checkStatusList;
	
	public AmendTimerSelectDaysAdapter(Context context,int type,String expression,String[] array) {
		if (array == null) { return;}
		checkStatusList = new ArrayList<Boolean>();
		this.type = type;
		this.array = array;
		this.expression = expression;
		if (!TextUtils.isEmpty(expression)) {
			cornArr = expression.split(",");
			if (cornArr.length>0) {
				for (int i = 0; i < cornArr.length; i++) {
					selectStr += cornArr[i];
				}
			}
		}
		for (int i = 1; i < array.length+1; i++) {
			if (selectStr.contains(Integer.toString(i))) {
				checkStatusList.add(true);
			}else{
				checkStatusList.add(false);
			}
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
	
	public String getString(){
		if (!TextUtils.isEmpty(selectStr)) {
			return selectStr;
		}
		return "";
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final int pos = position;
		int target = position+1;
		final String str = Integer.toString(target);
		if(convertView==null){
			convertView=CommDefines.getSkinManager().view(R.layout.customer_add_days_item);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.repeatText.setText(array[pos]);
		holder.imgBtn.setChecked(checkStatusList.get(pos));
		
		holder.imgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkStatusList.get(pos) != holder.imgBtn.isChecked()) {
					checkStatusList.set(pos, holder.imgBtn.isChecked());
					if (checkStatusList.get(pos)) {
						if (!selectStr.contains(str)) {
							selectStr += str;
						}
					} else {
						if (selectStr.contains(str)) {
							selectStr = selectStr.replace(str, "");
						}
					}
				}
			}
		});
		
//		holder.imgBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(checkStatusList.get(pos) != isChecked) {
//					checkStatusList.set(pos, isChecked);
//					if (checkStatusList.get(pos)) {
//						if (!selectStr.contains(str)) {
//							selectStr += str;
//							System.out.println("selectStr isChecked:"+selectStr);
//						}
//					}else{
//						if (selectStr.contains(str)) {
//							System.out.println("str:"+str);
//							selectStr.replace(str, "");
//							System.out.println("selectStr:"+selectStr);
//						}
//					}
//				}
//			}
//		});
		
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
