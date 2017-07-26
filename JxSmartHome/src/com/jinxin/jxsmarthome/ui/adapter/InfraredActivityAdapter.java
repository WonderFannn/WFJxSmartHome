package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;

/**
 * 新版设备列表Adapter
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class InfraredActivityAdapter extends BaseAdapter {
	
	private String[] strings;
	private List<Integer> ids;
	
	public InfraredActivityAdapter(Context context,String[] strings,List<Integer> ids) {
		this.strings = strings;
		this.ids = ids;
	}

	@Override
	public int getCount() {
		return ids.size();
	}

	@Override
	public Object getItem(int position) {
		return this.ids == null ? 0 : ids.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Bitmap _bitmap;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(
					R.layout.activity_infrared_control_gridview_adapter);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.itemText.setText(strings[position]);
		holder.itemImg.setBackgroundResource(ids.get(position));
		return convertView;
	}
	
	class ViewHolder{
		ImageView itemImg;
		TextView itemText;
		public ViewHolder(View view) {
			itemImg = (ImageView) view.findViewById(R.id.infrared_control_gridview_img);
			itemText = (TextView) view.findViewById(R.id.infrared_control_gridview_text);
		}
		
	}
	

}
