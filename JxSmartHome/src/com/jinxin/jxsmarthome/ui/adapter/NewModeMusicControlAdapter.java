package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.Speaker;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 新模式下音乐控制适配器
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class NewModeMusicControlAdapter extends BaseAdapter {
	
	private List<String> mData = new ArrayList<String>();
	private LayoutInflater inflater;
	private int selectedPosition;
	
	private Context context;
	
	public NewModeMusicControlAdapter(Context context, List<String> mData) {
		this.context = context;
		this.mData = mData;
		this.inflater = LayoutInflater.from(context);
	}
	
	
 
	 
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final int pos = position;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.new_mode_music_control_list_item, parent, false);
			holder.mName = (TextView) convertView.findViewById(R.id.speaker_choose_item_name);
			holder.radio_check = (ImageView) convertView.findViewById(R.id.radio_check);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		String musicControl = mData.get(position);
		holder.mName.setText(musicControl);
		if (getSelectedPosition() == position) {
			holder.radio_check.setVisibility(View.VISIBLE);
		} else {
			holder.radio_check.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView mName;
		ImageView radio_check;
	}

 
	public void setSelectedPosition(int position) {
		this.selectedPosition = position;
	}
	
	public int getSelectedPosition() {
		return this.selectedPosition;
	}
	
	
	 
}
