package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.ProductFun;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomContorllerGridAdapter extends BaseListAdapter<ProductFun> {
	public CustomContorllerGridAdapter(Context context, List<ProductFun> listData) {
		super(context, listData);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		final ProductFun pf = (ProductFun)listData.get(position);
		
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.item_grid_control, parent, false);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		
		holder.mName.setText(pf.getFunName());
		
		return convertView;
	}
	
	class Holder{
		ImageView mThumb;
		TextView mName;
		TextView mDesc;
		
		public Holder(View view){
			mThumb = (ImageView)view.findViewById(R.id.item_thumb);
			mName = (TextView)view.findViewById(R.id.item_name);
			mDesc = (TextView)view.findViewById(R.id.item_desc);
		}
	}
}
