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
import com.jinxin.jxsmarthome.entity.RemoteDeviceType;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;

/**
 * 遥控设备类型选择列表Adapter
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class RemoteDeviceTypeAdapter extends BaseAdapter {
	
	private List<RemoteDeviceType> typeList = null;
	private Context context = null;
	
	public RemoteDeviceTypeAdapter(Context context,List<RemoteDeviceType> typeList) {
		this.context = context;
		this.typeList = typeList;
	}

	@Override
	public int getCount() {
		return typeList == null ? 0 : typeList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.typeList == null ? 0 : typeList.size();
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
					R.layout.item_remote_device);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.itemText.setText(typeList.get(position).getDeviceName());
		Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
//		JxshApp.instance.getFinalBitmap().display(holder.itemImg,
//				FileManager.instance().createImageUrl(funDetail.getIcon()), _defaultBitmap,_defaultBitmap);		
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView itemImg;
		TextView itemText;
		public ViewHolder(View view) {
			itemImg = (ImageView) view.findViewById(R.id.iv_brand);
			itemText = (TextView) view.findViewById(R.id.tv_brand_name);
		}
		
	}
	

}
