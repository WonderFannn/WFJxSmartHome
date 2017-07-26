package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import com.jinxin.jxsmarthome.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PopupMenuAdapter extends BaseAdapter {
	private List<String> list;
	private Context context;
	
	public PopupMenuAdapter(Context context, List<String> list) {
		this.context = context;
		this.list = list;
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.pop_menu_item, null);
			holder = new ViewHolder();
			convertView.setTag(holder);

			holder.groupItem = (TextView) convertView
					.findViewById(R.id.textview);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.groupItem.setText(list.get(position));

		return convertView;
	}
	
	private final class ViewHolder {
		TextView groupItem;
	}
}
