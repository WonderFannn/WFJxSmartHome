package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class InfraredTransportListViewAdapter extends BaseAdapter   {
	
	private List<WHproductUnfrared> data;
	private LayoutInflater inflater;
	private Context mContext;
	private Map<String, Object> params = null;
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private WHproductUnfrared wHproductUnfrared;
	
	
	public InfraredTransportListViewAdapter(Context context,
			List<WHproductUnfrared> data,ProductFun productFun,FunDetail funDetail) {
		this.data=data;
		this.mContext=context;
		this.funDetail=funDetail;
		this.productFun=productFun;
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
		ViewHolder holder ;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.infrared_transport_listvview_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.activity_infrared_transpond_device_name);
			holder.num = (TextView) convertView.findViewById(R.id.activity_infrared_transpond_device_num);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		wHproductUnfrared=data.get(position);
		holder.num.setText(wHproductUnfrared.getSerCode()+"");
		return convertView;
	}

	
	private class ViewHolder {
		TextView num;
		TextView name;
	}

	
	
}
