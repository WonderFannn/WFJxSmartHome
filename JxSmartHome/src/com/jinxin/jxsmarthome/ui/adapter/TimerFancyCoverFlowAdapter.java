/*
 * Copyright 2013 David Schreiber
 *           2013 John Paul Nalog
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlowAdapter;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;

public class TimerFancyCoverFlowAdapter extends FancyCoverFlowAdapter {

	private int width;
	private int height;
	private List<FunDetail> funDetailList = null;
	private Context context;
    
    public TimerFancyCoverFlowAdapter(Context context,List<FunDetail> list, int modeWidth, int modeHeight) {
    	this.funDetailList = list;
		this.context = context;
		width = modeWidth;
		height = modeHeight;
    }

    @Override
    public int getCount() {
        return funDetailList == null ? 0 : funDetailList.size()+1;
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
    public long getItemId(int i) {
        return i;
    }
    class ViewHolder {
		ImageView imageView = null;
	}
    
    @SuppressWarnings("deprecation")
	@Override
    public View getCoverFlowItem(int position, View convertView, ViewGroup viewGroup) {
        ImageView imageView = null;

//        if (reuseableView != null) {
//            imageView = (ImageView) reuseableView;
//        } else {
//            imageView = new ImageView(viewGroup.getContext());
//            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(300, 400));
//
//        }
        
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
		if (position < 1) {
			viewHolder.imageView.setId(position);
			viewHolder.imageView.setLayoutParams(new LayoutParams(width, height));
			viewHolder.imageView.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
		}else{
			viewHolder.imageView.setId(position);
			viewHolder.imageView.setLayoutParams(new LayoutParams(width, height));
			viewHolder.imageView.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ic_launcher));
			
			final FunDetail funDetail = funDetailList.get(position-1);
			if(funDetail != null){
				//设置icon
				Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
				JxshApp.instance.getFinalBitmap().display(viewHolder.imageView, 
						FileManager.instance().createImageUrl(funDetail.getIcon()), _defaultBitmap,_defaultBitmap);
			}
		}

        return convertView;
    }
}
