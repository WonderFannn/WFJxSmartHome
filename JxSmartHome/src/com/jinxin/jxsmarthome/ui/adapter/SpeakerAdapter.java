package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.Speaker;

public class SpeakerAdapter extends BaseAdapter {
	private List<Speaker> data;
	private LayoutInflater inflater;
	
	public SpeakerAdapter(Context context, List<Speaker> data) {
		this.data = data;
		this.inflater = LayoutInflater.from(context);
	}
	
	public String getSpeakerSelection() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < data.size(); i++) {
			Speaker s = data.get(i);
			if(s.isChecked()) {
				sb.append("1");
			}else {
				sb.append("0");
			}
		}
		return sb.toString();
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
		final int pos = position;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.speaker_choose_dialog_list_item, parent, false);
			holder.mName = (TextView) convertView.findViewById(R.id.speaker_choose_item_name);
			holder.mSelector = (CheckBox) convertView.findViewById(R.id.speaker_choose_item_ck);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mName.setText(data.get(position).getName());
		
		if(parent.getChildCount() == position) {
			holder.mSelector.setChecked(data.get(position).isChecked());
		}
		
		holder.mSelector.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				data.get(pos).setChecked(isChecked);
			}
		});
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView mName;
		CheckBox mSelector;
	}

}
