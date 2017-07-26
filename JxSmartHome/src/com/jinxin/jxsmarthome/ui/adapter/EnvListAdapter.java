package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;

public class EnvListAdapter extends BaseAdapter {
	private Context context;
	private List<ProductFun> list;
	private FunDetailConfigDaoImpl configDao;
	
	public EnvListAdapter(Context context, List<ProductFun> list) {
		configDao = new FunDetailConfigDaoImpl(context);
		this.context = context;
		this.list = list;
	}
	
	public void setList(List<ProductFun> list) {
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
		final Holder holder;
		final ProductFun productFun = list.get(position);
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_env, parent, false);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		}else {
			holder=(Holder) convertView.getTag();
		}
		
		holder.mName.setText(getAlias(list.get(position).getWhId()));
		
		if(!TextUtils.isEmpty(productFun.getState())) {
			String[] items = productFun.getState().split(",");
			if(items.length == 3) {
				String kqStr = items[0];
				if(kqStr != null) {
					if(kqStr.contains("优")) {
						holder.mThumb.setImageResource(R.drawable.ico_you);
					}else if(kqStr.contains("良")) {
						holder.mThumb.setImageResource(R.drawable.ico_liang);
					}else if(kqStr.contains("污染")) {
						holder.mThumb.setImageResource(R.drawable.ico_cha);
					}
				}
				holder.mDetailKq.setText(kqStr);
				String wdStr = items[1];
				holder.mDetailWd.setText(wdStr);
				String sdStr = items[2];
				holder.mDetailSd.setText(sdStr);
			}
		}
		
		return convertView;
	}
	
	private String getAlias(String whId) {
		List<FunDetailConfig> config = configDao.find(null, 
				"whid=?", new String[]{whId}, null, null, null, null);
		if(config != null && config.size() > 0) {
			return config.get(0).getAlias();
		}
		return "";
	}
	
	class Holder{
		ImageView mThumb;
		TextView mName;
		TextView mDetailWd;
		TextView mDetailKq;
		TextView mDetailSd;
		
		public Holder(View view){
			mThumb = (ImageView)view.findViewById(R.id.item_thumb);
			mName = (TextView)view.findViewById(R.id.item_name);
			mDetailWd = (TextView)view.findViewById(R.id.item_detail_temp);
			mDetailKq = (TextView)view.findViewById(R.id.item_detail_api);
			mDetailSd = (TextView)view.findViewById(R.id.item_detail_wet);
		}
	}

}
