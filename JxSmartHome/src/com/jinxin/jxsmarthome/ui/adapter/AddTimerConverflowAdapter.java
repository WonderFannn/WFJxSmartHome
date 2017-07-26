package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.converflow.ImageUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;

public class AddTimerConverflowAdapter extends BaseAdapter{


	private int width;

	private int height;
	
	private List<FunDetail> funDetailList = null;
	private Context context;
	private Handler mDownLoadHandler;
	private Handler mUIHander;


	public AddTimerConverflowAdapter(Context context,List<FunDetail> list,int modeWidth, int modeHeight) {
		// width = (int) (HeroDetailActivity.this.getResources().getDisplayMetrics().density * 80);
		// height = (int) (HeroDetailActivity.this.getResources().getDisplayMetrics().density * 80);
		width = modeWidth;// 308;
		height = modeHeight;// 560;
		this.funDetailList = list;
		this.context = context;
		mUIHander = new Handler(){
			@Override
			public void handleMessage(Message msg) {
					if(funDetailList != null){
						notifyDataSetChanged();
					}
			}
		};
	}
	/**
	 * 设置DownLoadHandler(取activity的下载handler)
	 * 
	 * @param objects
	 */
	public void setDownLoadHandler(Handler handler) {
		this.mDownLoadHandler = handler;
	}
	public Bitmap createReflectedImages(Bitmap bitmap) {
		// The gap we want between the reflection and the original image
		Bitmap originalImage = ImageUtil.changeImage(context, bitmap, width, height, true);
		return originalImage;
	}

	@Override
	public int getCount() {
		int count = funDetailList.size()+1;
		return funDetailList == null ? 0 : count;
	}

	@Override
	public Object getItem(int position) {
		if (position==0) {
			return 0;
		}else{
			return funDetailList == null ? null : funDetailList.get(position-1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		ImageView imageView = null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = new ImageView(context);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position < 1) {
			viewHolder.imageView.setId(position);
			viewHolder.imageView.setLayoutParams(new LayoutParams(width, height));
			viewHolder.imageView.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
		}else{
			viewHolder.imageView.setId(position);
			viewHolder.imageView.setLayoutParams(new LayoutParams(width, height));
			viewHolder.imageView.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
			
			final FunDetail funDetail = funDetailList.get(position-1);
			Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
			JxshApp.instance.getFinalBitmap().display(viewHolder.imageView, FileManager.instance().
					createImageUrl(funDetail.getIcon()), _defaultBitmap,_defaultBitmap);
			
		}

		return convertView;
	}

}
