package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import com.jinxin.jxsmarthome.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class InputsAdapter extends BaseAdapter {
	private List<String> data;
	private LayoutInflater inflater;
	
	public InputsAdapter(Context context, List<String> data) {
		this.data = data;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.speaker_choose_dialog_list_item, null);
			holder.mName = (TextView) convertView.findViewById(R.id.speaker_choose_item_name);
			holder.mSelector = (CheckBox) convertView.findViewById(R.id.speaker_choose_item_ck);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mName.setTextColor(Color.BLACK);
		holder.mName.setText(data.get(position));
		holder.mSelector.setSelected(false);
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView mName;
		CheckBox mSelector;
	}

}
