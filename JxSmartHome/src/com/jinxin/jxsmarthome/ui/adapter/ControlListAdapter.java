package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.ProductFun;

public class ControlListAdapter extends BaseAdapter {
	private Context context;
	private List<ProductFun> controlList;
	
	public ControlListAdapter(Context context, List<ProductFun> controlList) {
		this.context = context;
		this.controlList = controlList;
	}

	@Override
	public int getCount() {
		return controlList.size();
	}

	@Override
	public Object getItem(int position) {
		return controlList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.listview_controls_item, null);
			convertView = view;
		}
		
		TextView mController = (TextView)convertView.findViewById(R.id.listview_control_dialog_controller);
		mController.setText(controlList.get(position).getFunName());
		
		return convertView;
	}

}
