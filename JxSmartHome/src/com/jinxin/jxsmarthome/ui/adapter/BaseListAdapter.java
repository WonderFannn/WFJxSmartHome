package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class BaseListAdapter<T> extends BaseAdapter {
	protected List<T> listData;
	protected LayoutInflater inflater;
	public Context context;
	
	public void bindData(List<T> listData) {
		this.listData = listData;
	}
	
	public BaseListAdapter(Context context, List<T> listData) {
		this.context = context;
		this.listData = listData;
		this.inflater = LayoutInflater.from(context);
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

}
