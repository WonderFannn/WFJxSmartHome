package com.jinxin.jxsmarthome.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jinxin.datan.net.command.SearchMusicTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.MusicLibDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.BaiduMusic;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.main.JxshApp;

public class MusicSearchActivity extends BaseActivity implements OnClickListener{
	
	private ImageView btnBack;
	private EditText et_singerName;
	private EditText et_songName;
	private Button btnSearch;
	private String songName,singerName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		
	}
	
	/**
	 * 初始化布局
	 */
	private void initView() {
		this.setView(R.layout.fragment_music_search_layout);
		this.btnBack = (ImageView) findViewById(R.id.btn_search_back);
		this.et_singerName = (EditText) findViewById(R.id.et_search_singer_name);
		this.et_songName = (EditText) findViewById(R.id.et_search_song_name);
		this.btnSearch = (Button) findViewById(R.id.search_music_btn);
		
		btnBack.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search_back:
			this.finish();
			break;
		case R.id.search_music_btn:
			singerName = et_singerName.getText().toString();
			songName = et_songName.getText().toString();
			if (TextUtils.isEmpty(singerName) || TextUtils.isEmpty(songName)) {
				JxshApp.showToast(MusicSearchActivity.this, "歌手名或歌曲不能为空");
				return;
			}
			searchTask(songName, singerName);
			break;

		default:
			break;
		}
		
	}
	
	/**
	 * 搜索歌曲任务
	 */
	private void searchTask(final String songName,final String singerName){
		
		SearchMusicTask smTask = new SearchMusicTask(MusicSearchActivity.this, 4, songName, singerName);
		
		smTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(MusicSearchActivity.this, "正在搜索歌曲...");
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				JxshApp.closeLoading();
	        	if (arg != null && arg.length >0) {
	        		BaiduMusic baiduMusic = (BaiduMusic) arg[0];
	        		MusicLib musicLib = new MusicLib();
	        		musicLib.setName(songName);
	        		musicLib.setSinger(singerName);
	        		musicLib.setLrcUrl(baiduMusic.getLrcid());
	        		if (!TextUtils.isEmpty(baiduMusic.getP2pUrl())) {
	        			musicLib.setNetUrl(baiduMusic.getP2pUrl());
					}else{
						String p2pUrl = baiduMusic.getUrlEncode().substring(0,
								baiduMusic.getUrlEncode().lastIndexOf("/")+1)+baiduMusic.getUrlDecode();
						musicLib.setNetUrl(p2pUrl);
					}
	        		MainMusicActivity.updateMusicHisList(MusicSearchActivity.this, musicLib);
	        		//从数据库查询出带ID的Music对象
	        		MusicLibDaoImpl mlDaoImpl = new MusicLibDaoImpl(MusicSearchActivity.this);
	        		List<MusicLib> tempList = mlDaoImpl.find(null, "name=? and singer=?",
	        				new String[]{songName,singerName}, null, null, null, null);
	        		if (tempList != null && tempList.size() > 0) {
	        			musicLib = tempList.get(0);
	        			JxshApp.instance.setMusic(musicLib);
	        			Intent _intent = new Intent(MusicSearchActivity.this,CloudMusicActivity.class);
//	        			_intent.putExtra("musicLib", musicLib);
	        			startActivity(_intent);
					}
	        	}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		JxshApp.getDatanAgentHandler().postDelayed(smTask, 10);
		
	}

	@Override
	public void uiHandlerData(Message msg) {
		
	}

}
