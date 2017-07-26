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
public class DeviceFragmentAdapter extends BaseAdapter {
	
	private List<FunDetail> funList = null;
	private Context context = null;
	
	public DeviceFragmentAdapter(Context context,List<FunDetail> funList) {
		this.context = context;
		this.funList = funList;
	}

	@Override
	public int getCount() {
		return funList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.funList == null ? 0 : funList.size();
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
					R.layout.fragment_device_item);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final FunDetail funDetail = funList.get(position);
		holder.itemText.setText(funDetail.getFunName());
		Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
		JxshApp.instance.getFinalBitmap().display(holder.itemImg,
				FileManager.instance().createImageUrl(funDetail.getIcon()), _defaultBitmap,_defaultBitmap);		
		/*String imgPath = funDetail.getPath();
		_bitmap = FileUtil.getImage(imgPath);
		if (!TextUtils.isEmpty(imgPath)){
			_bitmap = FileUtil.getImage(imgPath);
			if (_bitmap == null) {
				if(!funDetail.isLoading())
//				new ImageLoader().loadImage(context, holder.itemImg, imgPath);
				new ImageLoader().loadImage(context, funDetail, holder.itemImg);
			}else{
				holder.itemImg.setImageBitmap(_bitmap);
			}
		}else{
			if(!funDetail.isLoading())
			new ImageLoader().loadImage(context, funDetail, holder.itemImg);
		}*/
		
		return convertView;
	}
	
	class ViewHolder{
		LinearLayout llLayout;
		ImageView itemImg;
		TextView itemText;
		public ViewHolder(View view) {
			llLayout = (LinearLayout) view.findViewById(R.id.fun_layout);
			itemImg = (ImageView) view.findViewById(R.id.fun_img);
			itemText = (TextView) view.findViewById(R.id.fun_text);
		}
		
	}
	

}
