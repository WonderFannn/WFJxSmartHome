package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.img.BitmapTools;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.FileManager;

/**
 * 主页常用设备数据加载的Adapter
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class HomeDeviceGridAdapter extends BaseListAdapter<ProductFun> {
	private GridView mGridView;

	public HomeDeviceGridAdapter(Context context, List<ProductFun> listData) {
		super(context, listData);
	}

	public HomeDeviceGridAdapter(Context context, List<ProductFun> listData,
			GridView gridView) {
		this(context, listData);
		this.mGridView = gridView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		ProductState productState = null;
		ProductFun productFun =  listData.size() > 0 ? (ProductFun)listData.get(position) : null;
		FunDetail funDetail = AppUtil.getFunDetailByFunType(context,
				productFun.getFunType());
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_grid_home, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		if (productFun != null) {
			productState = (ProductState) AppUtil.getProductStateByFunId(context, 
					productFun.getFunId());
			// 设置名称
			holder.mName.setText(productFun.getFunName());
		}
		
		// 设置图标
		if (funDetail != null) {
			Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
			JxshApp.instance.getFinalBitmap().display(holder.mThumb,FileManager.instance().
					createImageUrl(funDetail.getIcon()), _defaultBitmap,_defaultBitmap);
			Bitmap openIcon = JxshApp.instance.getFinalBitmap().getBitmapFromCache(
					FileManager.instance().createImageUrl(funDetail.getIcon()));
			Bitmap closeIcon = null;
			if (openIcon != null) {
				closeIcon = BitmapTools.toGrayscale(openIcon);
			}else{
				closeIcon = BitmapTools.toGrayscale(_defaultBitmap);
			}
			if (productState != null) {
				holder.mThumb.setImageBitmap(productState.getState().equals("1")|| //普通设备
						productState.getState().equals("0001") || //普通窗帘
						productState.getState().equals("01") ||//单路、智能插座
						productState.getState().equals("100") ? openIcon : closeIcon);//无线窗帘
			}else{
				holder.mThumb.setImageBitmap(closeIcon);
			}
		} 

		AbsListView.LayoutParams param = new AbsListView.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				mGridView.getHeight() / 2);
		convertView.setLayoutParams(param);

		return convertView;
	}

	class Holder {
		ImageView mThumb;
		TextView mName;
		public Holder(View view) {
			mThumb = (ImageView) view.findViewById(R.id.item_thumb);
			mName = (TextView) view.findViewById(R.id.item_name);
		}
	}

}
