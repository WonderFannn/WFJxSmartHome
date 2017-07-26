package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.ProductFun;

public class CustomTVControllerListAdapter extends BaseListAdapter<ProductFun> {
	private int selectedPosition;
	
	public CustomTVControllerListAdapter(Context context, List<ProductFun> listData) {
		super(context, listData);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_text, parent, false);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		ProductFun pf = listData.get(position);
		
		holder.mName.setText(pf.getFunName());
		
		return convertView;
	}
	
	class Holder {
		ImageView mThumb;
		TextView mName;
		public Holder(View view) {
			mThumb = (ImageView) view.findViewById(R.id.radio_check);
			mName = (TextView) view.findViewById(R.id.radio_text);
		}
	}
	
	public void setSelectedPosition(int position) {
		this.selectedPosition = position;
	}
	
	public int getSelectedPosition() {
		return this.selectedPosition;
	}
	
}
