package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.entity.SingerLib;
import com.jinxin.jxsmarthome.fragment.HotMusicFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;

/**
 * 主页常用设备数据加载的Adapter
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class SingerGridAdapter extends BaseAdapter{

	private Context context;
	private List<SingerLib> singerLsit;
	private  FragmentTransaction fTransaction;
	
	private List<Integer> imgList = new ArrayList<Integer>();
	
	public SingerGridAdapter(Context context, List<SingerLib> singerLsit) {
		this.context = context;
		this.singerLsit = singerLsit;
		imgList.add(R.drawable.ic_user);
		imgList.add(R.drawable.ic_user);
		imgList.add(R.drawable.ic_user);
	}
	
	public void setSingerList(List<SingerLib> singerLsit){
		this.singerLsit = singerLsit;
	}

	@Override
	public int getCount() {
		return singerLsit == null ? 3 : singerLsit.size();
	}

	@Override
	public Object getItem(int position) {
		if (singerLsit != null) {
			return singerLsit.get(position);
		}
		return imgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		Bitmap _bitmap;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(R.layout.item_singer_grid);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		if (singerLsit != null) {
			final SingerLib singer = singerLsit.get(position);
			if (singer != null) {
				String imgPath = singer.getIcon();
				Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.ic_user));
				JxshApp.instance.getFinalBitmap().display(holder.mThumb,
						FileManager.instance().createImageUrl(imgPath), _defaultBitmap,_defaultBitmap);	
//				if (!TextUtils.isEmpty(imgPath)){
//					String localPath = FileManager.instance().getImagePath(imgPath, 0, true);
//					_bitmap = FileUtil.getImage(localPath);
//					if (_bitmap == null) {
//						new ImageLoader().loadImage(context, holder.mThumb, imgPath);
//					}else{
//						holder.mThumb.setImageBitmap(_bitmap);
//					}
//				}else{
//					holder.mThumb.setImageResource(R.drawable.ic_user);
//				}
				holder.mName.setText(singer.getSinger());
				
				holder.mThumb.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						fTransaction =((BaseActionBarActivity)context).
						getSupportFragmentManager().beginTransaction();
						Bundle bundle = new Bundle();
						HotMusicFragment fragment = new HotMusicFragment();
						fragment.setArguments(bundle);
						bundle.putString("singerName", singer.getSinger());
						fTransaction.add(R.id.main_music_fragmlayout, fragment)
						.addToBackStack(null).commit();
					}
				});
			}
		}else{//测试
			holder.mThumb.setImageResource(R.drawable.ic_user);
			holder.mName.setText("测试数据");
		}

		return convertView;
	}

	class Holder {
		ImageView mThumb;
		TextView mName;
		public Holder(View view) {
			mThumb = (ImageView) view.findViewById(R.id.hot_singer_item_image);
			mName = (TextView) view.findViewById(R.id.hot_singer_item_name);
		}
	}


}
