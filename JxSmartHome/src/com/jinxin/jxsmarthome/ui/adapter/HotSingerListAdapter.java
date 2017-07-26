package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.SingerLib;

public class HotSingerListAdapter extends BaseAdapter {
	private List<SingerLib> list;
	private Context context;
	
	public void setList(List<SingerLib> list) {
		this.list = list;
	}
	
	public HotSingerListAdapter(Context context, List<SingerLib> list) {
		this.list = list;
		this.context = context;
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
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_hot_singer, parent, false);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		
		holder.singerName.setText(list.get(position).getSinger());
		
		return convertView;
	}
	
	class Holder{
		TextView singerName;
		
		public Holder(View view){
			singerName = (TextView) view.findViewById(R.id.singer_name_tv);
		}
	}
	
}
