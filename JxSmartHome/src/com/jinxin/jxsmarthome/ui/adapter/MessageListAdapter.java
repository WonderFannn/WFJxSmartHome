package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.db.impl.CustomerMessageDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerMeassage;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

public class MessageListAdapter extends BaseAdapter {
	private List<CustomerMeassage> list;
	private Context context;
	private CustomerMessageDaoImpl cmDaoImpl = null;
	
	
	public MessageListAdapter(Context context, List<CustomerMeassage> list) {
		this.list = list;
		this.context = context;
		cmDaoImpl = new CustomerMessageDaoImpl(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		
		if(convertView == null) {
			convertView = CommDefines.getSkinManager().view(R.layout.list_item_message);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		
		CustomerMeassage cMeassage = list.get(position);
		String[] arr = cMeassage.getMessage().split(":");
		if (arr != null && arr.length > 0) {
			holder.deviceName.setText(arr[0]);
		}
		holder.message.setText(cMeassage.getTime()+" "+cMeassage.getMessage());
		if (cMeassage.getIsReaded() == 0) {
			holder.isRead.setVisibility(View.VISIBLE);
			cMeassage.setIsReaded(1);
			cmDaoImpl.update(cMeassage);
		}else{
			holder.isRead.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	class Holder{
		TextView deviceName;
		TextView message;
		ImageView isRead;
		
		public Holder(View view){
			deviceName=(TextView) view.findViewById(R.id.tv_door_name);
			message=(TextView) view.findViewById(R.id.tv_door_message);
			isRead = (ImageView) view.findViewById(R.id.iv_isreading);
		}
	}
	
}
