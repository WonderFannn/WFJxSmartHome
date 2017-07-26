package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.ProductVoiceType;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;

/**
 * 语音助手主页显示类别的Adapter
 * @author TangLong
 * @company 金鑫智慧
 */
public class VoiceHelperGridAdapter extends BaseAdapter{

	private Context context;
	private List<ProductVoiceType> typeList = null;
	
	public VoiceHelperGridAdapter(Context context, List<ProductVoiceType> typeList) {
		this.context = context;
		this.typeList = typeList;
	}
	
	@Override
	public int getCount() {
		return this.typeList != null ? typeList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return typeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		Bitmap _bitmap = null;
		
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(R.layout.item_voice_type_grid);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		
		ProductVoiceType  voiceType = typeList.get(position);
		if (voiceType != null) {
			holder.mName.setText(voiceType.getName());
			
			String imgPath = voiceType.getIcon();
			if (!TextUtils.isEmpty(imgPath)){
				Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.ic_user));
				JxshApp.instance.getFinalBitmap().display(holder.mIcon,
						FileManager.instance().createImageUrl(imgPath), _defaultBitmap,_defaultBitmap);
				
//				String localPath = FileManager.instance().getImagePath(imgPath, 0, true);
//				_bitmap = FileUtil.getImage(localPath);
//				if (_bitmap == null) {
//					new ImageLoader().loadImage(context, holder.mIcon, imgPath);
//				}else{
//					holder.mIcon.setImageBitmap(_bitmap);
//				}
			}else{
				if (position == 0) {
					holder.mIcon.setImageResource(R.drawable.icon_voice_conposel);
				}else if(position == 1){
					holder.mIcon.setImageResource(R.drawable.icon_speak_control);
				}else if(position == 2){
					holder.mIcon.setImageResource(R.drawable.icon_music_control);
				}else{
					holder.mIcon.setImageResource(R.drawable.ic_user);
				}
			}
		}

		return convertView;
	}

	class Holder {
		ImageView mIcon;
		TextView mName;
		public Holder(View view) {
			mIcon = (ImageView) view.findViewById(R.id.hot_singer_item_image);
			mName = (TextView) view.findViewById(R.id.hot_singer_item_name);
		}
	}


}
