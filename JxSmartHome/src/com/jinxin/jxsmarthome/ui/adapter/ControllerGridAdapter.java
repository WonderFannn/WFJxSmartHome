package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.jinxin.jxsmarthome.R;

public class ControllerGridAdapter extends BaseAdapter {
	private List<String> listData;
	private LayoutInflater inflater;
	
	public ControllerGridAdapter(Context context, List<String> listData) {
		this.listData = listData;
		this.inflater = (LayoutInflater)context.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			View view = inflater.inflate(R.layout.custom_controller_item, parent, false);
			final Button mFunction = (Button)view.findViewById(R.id.custom_controller_item_number);
			final String functionName = listData.get(position);
			mFunction.setText(functionName);
			convertView = view;
		}
		return convertView;
	}
}
