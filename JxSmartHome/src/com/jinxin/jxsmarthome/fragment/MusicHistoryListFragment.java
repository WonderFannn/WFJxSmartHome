package com.jinxin.jxsmarthome.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jinxin.db.impl.MusicLibDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.CloudMusicActivity;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.MusicHistoryListAdapter;

public class MusicHistoryListFragment extends Fragment implements OnClickListener{

	private ListView listView;
	private MusicHistoryListAdapter adpter;
	
	private List<MusicLib> musicList = null;
	private MusicLibDaoImpl mlDapImpl = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.music_history_list_layout, container, false);
		initData();
		initView(view);
		
		return view;
	}
	
	private void initData(){
		mlDapImpl = new MusicLibDaoImpl(getActivity());
		musicList = mlDapImpl.find();
	}
	
	private void initView(View view){
		listView = (ListView) view.findViewById(R.id.musiclist_lv_songs_list);
		adpter  = new MusicHistoryListAdapter(getActivity(), musicList);
		listView.setAdapter(adpter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("Recycle")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (musicList != null && musicList.size() > 0) {
					MusicLib musicLib = musicList.get(position);
					Intent intent = new Intent(getActivity(),CloudMusicActivity.class);
//				intent.putExtra("musicLib", musicLib);
					JxshApp.instance.setMusic(musicLib);
					getActivity().startActivity(intent);
				}
				
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			
		}
		return true;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		
	}

}
