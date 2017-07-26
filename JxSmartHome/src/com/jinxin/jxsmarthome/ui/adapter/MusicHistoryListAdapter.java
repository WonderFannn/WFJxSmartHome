package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

public class MusicHistoryListAdapter extends BaseAdapter {
	private List<MusicLib> list;
	private Context context;
	
	public void setList(List<MusicLib> list) {
		this.list = list;
	}
	
	public MusicHistoryListAdapter(Context context, List<MusicLib> list) {
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
			convertView = CommDefines.getSkinManager().view(R.layout.item_music_info);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		
		
		final MusicLib music = list.get(position);
	
		holder.songName.setText(music.getName());
		holder.singerName.setText(music.getSinger());
		return convertView;
	}
	
	class Holder{
		TextView songName;
		TextView singerName;
		
		public Holder(View view){
			songName=(TextView) view.findViewById(R.id.fav_music_name);
			singerName=(TextView) view.findViewById(R.id.fav_singer_name);
		}
	}
	
}
