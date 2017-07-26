package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

public class DlnaDeviceListAdapter extends BaseAdapter {
	private Context context;
	private List<String> deviceNameList;
	private int location = 0;
	
	public DlnaDeviceListAdapter(Context context, List<String> deviceNameList) {
		this.context = context;
		this.deviceNameList = deviceNameList;
	}

	@Override
	public int getCount() {
		return this.deviceNameList != null ? deviceNameList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return deviceNameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(
					R.layout.custom_dialog_dlan_item);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(deviceNameList.get(position));
		String account = CommUtil.getCurrentLoginAccount();
    	String deviceName = SharedDB.loadStrFromDB(account, ControlDefine.
					KEY_DLNA_NAME, "");
		if (deviceNameList.get(position).equals(deviceName)) {
			holder.image.setVisibility(View.VISIBLE);
		}else{
			holder.image.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	
	class ViewHolder{
		LinearLayout llButton;
		TextView name;
		ImageView image;
		public ViewHolder(View view) {
			llButton = (LinearLayout) view.findViewById(R.id.dlna_layout);
			name = (TextView) view.findViewById(R.id.dlna_device_name);
			image = (ImageView) view.findViewById(R.id.dlna_chose_btn);
		}
	}

}
