package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.jinxin.datan.net.command.cb.DownLoadImageTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.converflow.ImageUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;
import com.jinxin.record.FileUtil;

public class ConverflowAdapter extends BaseAdapter{


	private int width;

	private int height;
	
	private List<FunDetail> funDetailList = null;
	private Context context;
	private Handler mDownLoadHandler;
	private Handler mUIHander;


	public ConverflowAdapter(Context context,List<FunDetail> list,int modeWidth, int modeHeight) {
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
		// TODO Auto-generated method stub
		return funDetailList == null ? 0 : funDetailList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return funDetailList == null ? null : funDetailList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class ViewHolder {
		ImageView imageView = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		// ImageView view = new ImageView(HeroDetailActivity.this);
		if (convertView == null) {
			convertView = new ImageView(context);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.imageView.setId(position);
		viewHolder.imageView.setLayoutParams(new LayoutParams(width, height));
		viewHolder.imageView.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ic_launcher));

		final FunDetail funDetail = funDetailList.get(position);
		if(funDetail != null){
		//设置icon
			Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
			JxshApp.instance.getFinalBitmap().display(viewHolder.imageView, 
					FileManager.instance().createImageUrl(funDetail.getIcon()), _defaultBitmap,_defaultBitmap);
		/*if(!TextUtils.isEmpty(funDetail.getPath())){
			//本地已缓存，直接加载
			 Bitmap _bitmap = FileUtil.getImage(funDetail.getPath());
			 if(_bitmap == null){
				 //本地缓存图片丢失
				 funDetail.setPath(null);
			 }else{
				 viewHolder.imageView.setImageBitmap(createReflectedImages(_bitmap));
			 }
		}else{
			//网上取
			if(!funDetail.isLoading()){
				DownLoadImageTask _dliTask = new DownLoadImageTask(context, funDetail.getIcon(), 0, true);
				_dliTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
						// TODO Auto-generated method stub
						funDetail.setLoading(true);
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
						// TODO Auto-generated method stub
						funDetail.setLoading(false);
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						// TODO Auto-generated method stub
						//注意：下载图片失败，不再反复联网取图
//						customerProduct.setLoading(false);
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						// TODO Auto-generated method stub
						funDetail.setLoading(false);
						if(arg != null && arg.length > 0){
							funDetail.setPath((String)arg[0]);
							//更新数据库
							FunDetailDaoImpl _dpgdImpl = new FunDetailDaoImpl(context);
							_dpgdImpl.update(funDetail);
							//刷新
							mUIHander.sendEmptyMessage(0);
						}
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
						// TODO Auto-generated method stub
						
					}
				});
				if(mDownLoadHandler != null){
					mDownLoadHandler.post(_dliTask);
				}
			}
		}*/
	}

		return convertView;
	}

}
