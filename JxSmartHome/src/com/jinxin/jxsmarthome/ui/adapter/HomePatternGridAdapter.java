package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomePatternGridAdapter extends BaseListAdapter<CustomerPattern> {
	private GridView mGridView;
	
	public HomePatternGridAdapter(Context context, List<CustomerPattern> listData) {
		super(context, listData);
	}
	
	public HomePatternGridAdapter(Context context, List<CustomerPattern> listData, GridView gridView) {
		this(context, listData);
		this.mGridView = gridView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		final CustomerPattern customerPattern = (CustomerPattern)listData.get(position);
		
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.item_grid_home, null);
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(
	                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
	                mGridView.getHeight()/2);
			convertView.setLayoutParams(param);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		holder.mStop.setVisibility(customerPattern.getStatus()
					.equals("0") ? View.VISIBLE : View.INVISIBLE);
		holder.llButton.setFocusable(customerPattern.getStatus()
				.equals("0") ? true : false);
		holder.mName.setText(customerPattern.getPaternName());
		holder.mDesc.setVisibility(View.VISIBLE);
		String desc = customerPattern.getMemo();
		if(TextUtils.isEmpty(desc)) {
			holder.mDesc.setText(CommDefines.getSkinManager().string(R.string.comments_is_null));
		}else {
			holder.mDesc.setText(desc);
		}
		
		Bitmap bitmap = CommDefines.getSkinManager()
				.getAssetsBitmap(customerPattern.getIcon());
		if (bitmap != null) {
			holder.mThumb.setImageBitmap(bitmap);
		}else{
			holder.mThumb.setImageBitmap(CommDefines.getSkinManager()
					.getAssetsBitmap("images/img/upload/default.png"));
		}
		
		
		AbsListView.LayoutParams param = new AbsListView.LayoutParams(
		        android.view.ViewGroup.LayoutParams.MATCH_PARENT, mGridView.getHeight()/2);
		convertView.setLayoutParams(param);
				
		return convertView;
	}
	
	class Holder{
		ImageView mThumb;
		ImageView mStop;
		TextView mName;
		TextView mDesc;
		LinearLayout llButton;
		
		public Holder(View view){
			mThumb = (ImageView)view.findViewById(R.id.item_thumb);
			mStop = (ImageView)view.findViewById(R.id.imageView_stop);
			mName = (TextView)view.findViewById(R.id.item_name);
			mDesc = (TextView)view.findViewById(R.id.item_desc);
			llButton = (LinearLayout) view.findViewById(R.id.item_background);
		}
	}
}
