package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.record.SharedDB;

public class RadioListAdapter extends BaseListAdapter<String> {
	private int selectedPosition;
	private boolean isUsbAvailable;
	private boolean isSDAvailable;
	
	public RadioListAdapter(Context context, List<String> listData) {
		super(context, listData);
		isUsbAvailable = SharedDB.
				loadBooleanFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_USB_AVAILABLE, true);
		isSDAvailable = SharedDB.
				loadBooleanFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_SD_AVAILABLE, true);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_radio, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		final int pos = position;
		
		String inputName = listData.get(position);
		if (!isUsbAvailable && pos == 0) {
			holder.rlItem.setBackgroundColor(Color.LTGRAY);
		} else if(!isSDAvailable && pos == 1){
			holder.rlItem.setBackgroundColor(Color.LTGRAY);
		} else {
			holder.rlItem.setBackgroundColor(Color.TRANSPARENT);
		}
		
		holder.mName.setText(inputName);
		if(getSelectedPosition() == pos) {
			holder.mThumb.setVisibility(View.VISIBLE);
		}else {
			holder.mThumb.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	
	
	
	@Override
	public boolean isEnabled(int position) {
		
		if (!isUsbAvailable && position == 0) {
			return false;
		}
		if (!isSDAvailable && position == 1) {
			return false;
		}
		return super.isEnabled(position);
	}



	class Holder {
		ImageView mThumb;
		TextView mName;
		RelativeLayout rlItem;
		public Holder(View view) {
			mThumb = (ImageView) view.findViewById(R.id.radio_check);
			mName = (TextView) view.findViewById(R.id.radio_text);
			rlItem = (RelativeLayout) view.findViewById(R.id.rl_item_input);
		}
	}
	
	public void setSelectedPosition(int position) {
		this.selectedPosition = position;
	}
	
	public int getSelectedPosition() {
		return this.selectedPosition;
	}
	
}
