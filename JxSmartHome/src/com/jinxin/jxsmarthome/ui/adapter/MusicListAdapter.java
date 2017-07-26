package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.Music;

public class MusicListAdapter extends BaseAdapter {
//	private static final String TAG = "MusicListAdapter";
	private List<Music> list;
	private Context context;
	private int selectIndex = -1;
	
	public void setList(List<Music> list) {
		this.list = list;
	}
	
	public MusicListAdapter(List<Music> list, Context context) {
		this.list = list;
		this.context = context;
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
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.music_list_item, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		
		holder.vStub.setVisibility(View.GONE);
		
		final Music music = list.get(position);
	
		holder.mItemNo.setText(String.valueOf(position + 1));
		holder.mItemTitle.setText(music.getTitle());
		if (selectIndex == music.getNo()) {
			holder.mItem.setBackgroundResource(R.color.half_transparent);
//			holder.mItemTitle.setTextColor(R.color.text_green);
		}else{
//			holder.mItemTitle.setTextColor(Color.WHITE);
			holder.mItem.setBackgroundResource(R.color.transparent);
		}
		return convertView;
	}
	
	class Holder{
		TextView mItemNo;
		TextView mItemTitle;
		Button mBtnMore;
		View vStub;
		RelativeLayout mItem;
		
		public Holder(View view){
			mItemNo=(TextView) view.findViewById(R.id.musiclist_item_no);
			mItemTitle=(TextView) view.findViewById(R.id.musiclist_item_title);
			mBtnMore=(Button) view.findViewById(R.id.musiclist_item_more);
			vStub = ((ViewStub)view.findViewById(R.id.music_list_item_vs)).inflate();
			mItem = (RelativeLayout) view.findViewById(R.id.rl_music_item);
		}
	}
	
	public void setSelectIndex(int i){
		selectIndex = i;
	}
	
}
