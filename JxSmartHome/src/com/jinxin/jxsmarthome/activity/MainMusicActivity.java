package com.jinxin.jxsmarthome.activity;


import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.datan.net.command.SearchMusicTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.MusicLibDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.aidl.MusicServiceAidl;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.entity.BaiduMusic;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.fragment.HotMusicFragment;
import com.jinxin.jxsmarthome.fragment.HotSingerFragment;
import com.jinxin.jxsmarthome.fragment.MusicHistoryListFragment;
import com.jinxin.jxsmarthome.fragment.MyFavMusicFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.service.MusicService;
import com.jinxin.jxsmarthome.ui.popupwindow.ShowSuggestWindows;

public class MainMusicActivity extends BaseActionBarActivity implements OnClickListener{
	
	private EditText et_singer;
	private EditText et_song;
	private Button searchBtn;
	private ImageView musicIcon;
	private ImageView playBtn;//底部播放按钮
	private TextView tv_singer, tv_song;
	private RelativeLayout bottomLayout;
	private RelativeLayout musicListLayout, hotSingerLayout, hotSongsLayotu, myFavLayout;
	
	private MusicLib musicLib;
	private String singerName, songName;
	private Context context;
	private boolean isRunning = false;
	private boolean isPlayying = false;
	private String NOW_STATUS = "";//当前播放状态
	private List<MusicLib> musicList;//音乐播放列表
	
	private MusicHistoryListFragment historyFragment;
	private HotMusicFragment musicFragment;
	private HotSingerFragment singerFragment;
	private MyFavMusicFragment favMusicFragment;
	
	private static final int PLAYING = 1;
	private static final int PAUSE = 2;
	
	private MusicServiceAidl serviceBinder = null;
    
    
    private ServiceConnection mConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        serviceBinder = MusicServiceAidl.Stub.asInterface(service);
	    }
	    public void onServiceDisconnected(ComponentName className) {
	    	serviceBinder = null;  
	    }  
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_music_layout);
		Intent intent = new Intent(MainMusicActivity.this,MusicService.class);
		bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
		
		initData();
		initView();
	}

	/**
	 * 初始化页面
	 */
	private void initView(){
		context = this;
		
		this.et_singer = (EditText) findViewById(R.id.et_singer_name);
		this.et_song = (EditText) findViewById(R.id.et_song_name);
		this.searchBtn = (Button) findViewById(R.id.search_music_btn);
		this.bottomLayout = (RelativeLayout) findViewById(R.id.bottom_paly_layout);
		this.musicIcon = (ImageView) findViewById(R.id.music_icon);
		this.playBtn = (ImageView) findViewById(R.id.main_music_play);
		this.tv_singer = (TextView) findViewById(R.id.tv_singer_name);
		this.tv_song = (TextView) findViewById(R.id.tv_song_name);
		this.musicListLayout = (RelativeLayout) findViewById(R.id.music_list_layout);
		this.hotSingerLayout = (RelativeLayout) findViewById(R.id.hot_singer_layout);
		this.hotSongsLayotu = (RelativeLayout) findViewById(R.id.hot_songs_layout);
		this.myFavLayout = (RelativeLayout) findViewById(R.id.my_fav_layout);
		
		searchBtn.setOnClickListener(this);
		bottomLayout.setOnClickListener(this);
		musicListLayout.setOnClickListener(this);
		hotSingerLayout.setOnClickListener(this);
		hotSongsLayotu.setOnClickListener(this);
		myFavLayout.setOnClickListener(this);
		playBtn.setOnClickListener(this);
		
		//新手引导
		suggestWindow = new ShowSuggestWindows(MainMusicActivity.this, R.drawable.bg_guide_main_music, "");
		suggestWindow.showFullWindows("MainMusicActivity",R.drawable.bg_guide_main_music);
		
	}
	
	/**
	 * 初始数据
	 */
	private void initData(){
		//开启线程监听播放状态 改变底部播放界面状态
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(isRunning){
					try {
					if (serviceBinder != null) {
						NOW_STATUS = serviceBinder.getPlayStatus();
						musicLib = JxshApp.instance.getMusicLib();
						if (NOW_STATUS.equals(MusicService.PLAYSTATUS.PLAYING.toString())) {
							mUIHander.sendEmptyMessage(PLAYING);
						}else{
							mUIHander.sendEmptyMessage(PAUSE);
						}
					}
					Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getSupportActionBar().setTitle("云音乐");
		isRunning = true;
		musicList = getMusicListFromDB();
		if (musicList == null || musicList.size() < 1) {
			MusicLibDaoImpl mlDaoImpl = new MusicLibDaoImpl(MainMusicActivity.this);
			musicList = mlDaoImpl.find();
		}
	}
	
	/**
	 * 获取本地数据库音乐列表
	 * @return
	 */
	private List<MusicLib> getMusicListFromDB(){
		MusicLibDaoImpl mlDaoImpl = new MusicLibDaoImpl(getApplicationContext());
		List<MusicLib> musicList = mlDaoImpl.find();
		return musicList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main_music, menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("云音乐");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.search_baidu_music_btn:
			startActivity(new Intent(MainMusicActivity.this,MusicSearchActivity.class));
			break;
		case R.id.main_music_menu:
			startActivity(new Intent(MainMusicActivity.this,MusicActivity.class));
			break;
		}
		return false;
	}
	@Override
	public void uiHandlerData(Message msg) {
		super.uiHandlerData(msg);
		switch (msg.what) {
		case 0:
			tv_singer.setText(singerName);
			tv_song.setText(songName);
			break;
		case PLAYING:
			isPlayying = true;
			if (musicLib != null) {
				tv_song.setText(musicLib.getName());
				tv_singer.setText(musicLib.getSinger());
			}
			playBtn.setImageResource(R.drawable.btn_bottom_pause);
			break;
		case PAUSE:
			isPlayying = false;
			if (musicLib != null) {
				tv_song.setText(musicLib.getName());
				tv_singer.setText(musicLib.getSinger());
			}
			playBtn.setImageResource(R.drawable.btn_bottom_play);
			break;

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_music_btn://搜索
			singerName = et_singer.getText().toString();
			songName = et_song.getText().toString();
			if (TextUtils.isEmpty(singerName) || TextUtils.isEmpty(songName)) {
				JxshApp.showToast(MainMusicActivity.this, "歌手名或歌曲名不能为空");
				return;
			}
			searchTask(songName,singerName);
			break;
		case R.id.bottom_paly_layout://跳转到播放界面
//			if (musicLib == null) {
//				musicLib = new MusicLib();
//			}
			Intent intent = new Intent(MainMusicActivity.this,CloudMusicActivity.class);
//			intent.putExtra("musicLib", musicLib);
			startActivity(intent);
			break;
		case R.id.music_list_layout://音乐列表界面
			historyFragment = new MusicHistoryListFragment();
			addFragment(historyFragment, true);
			break;
		case R.id.hot_singer_layout://热门歌手界面
			singerFragment = new HotSingerFragment();
			addFragment(singerFragment, true);
			break;
		case R.id.hot_songs_layout://热门歌曲界面
			musicFragment = new HotMusicFragment();
			Bundle bundle = new Bundle();
			bundle.putString("singerName", "");
			musicFragment.setArguments(bundle);
			addFragment(musicFragment, true);
			break;
		case R.id.my_fav_layout://我的收藏界面
			favMusicFragment = new MyFavMusicFragment();
			addFragment(favMusicFragment, true);
			break;
		case R.id.main_music_play:
			if (isPlayying) {
				isPlayying = false;
				BroadcastManager.sendBroadcast(BroadcastManager.ACTION_MUSIC_PLAY_PAUSE, null);
				playBtn.setImageResource(R.drawable.btn_bottom_play);
			}else{
				isPlayying = true;
				playMusic(musicLib);
				playBtn.setImageResource(R.drawable.btn_bottom_pause);
			}
			break;

		}
	}
	
	/**
	 * 播放音乐
	 */
	private void playMusic(MusicLib musicLib){
		if (musicLib == null){
			if (musicList == null || musicList.size() < 1) {
				JxshApp.showToast(context, "播放列表为空");
				return;
			}else{
				musicLib = musicList.get(0);
			}
		}
		int musicId = musicLib.getId();
		String musicUrl = musicLib.getNetUrl();
		if (TextUtils.isEmpty(musicUrl)) {
			JxshApp.showToast(context, "歌曲地址为空,请重新搜索歌曲");
		}else{
			Intent intent = new Intent();
			intent.putExtra("musicId", musicId);
			intent.putExtra("musicUrl", musicUrl);
			intent.setAction(BroadcastManager.ACTION_MUSIC_PLAY_CONTROL);
			sendBroadcast(intent);
			JxshApp.instance.setMusic(musicLib);
		}
	}
	
	/**
	 * 搜索歌曲任务
	 */
	private void searchTask(final String songName,final String singerName){
		
		SearchMusicTask smTask = new SearchMusicTask(context, 4, songName, singerName);
		
		smTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, "正在搜索歌曲...");
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
	        		updateMusicHisList(context, musicLib);
	        		
	        		//从数据库查询出带ID的Music对象
	        		MusicLibDaoImpl mlDaoImpl = new MusicLibDaoImpl(MainMusicActivity.this);
	        		List<MusicLib> tempList = mlDaoImpl.find(null, "name=? and singer=?",
	        				new String[]{songName,singerName}, null, null, null, null);
	        		if (tempList != null && tempList.size() > 0) {
	        			musicLib = tempList.get(0);
	        			JxshApp.instance.setMusic(musicLib);//在Application里设置当前播放的音乐对象
	        			Intent _intent = new Intent(MainMusicActivity.this,CloudMusicActivity.class);
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
	
	/**
	 * 更新播放历史音乐
	 * @param context
	 * @param musicLib
	 */
	public static void updateMusicHisList(Context context,MusicLib musicLib){
		if (context == null || musicLib == null) {
			return;
		}
		MusicLibDaoImpl mlDaoImpl = new MusicLibDaoImpl(context);
		if (mlDaoImpl != null) {
			String spl = "select id from music_lib where songName='"
					+ musicLib.getName() + "' "+
					"and singerName='"+musicLib.getSinger()+"'";
			System.out.println(spl);
			if (mlDaoImpl.isExist(
					"select id from music_lib where name='"+ musicLib.getName() +
					"' "+"and singer='"+musicLib.getSinger()+"'", null)) {
				mlDaoImpl.update(musicLib);
			}else {
				mlDaoImpl.insert(musicLib,true);
			}
		}
		
	}
	
	/**
	 * 添加fragment
	 * @param fragment
	 * @param addToStack
	 */
	private void addFragment(Fragment fragment, boolean addToStack) {
		if (fragment != null && addToStack) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_music_fragmlayout, fragment)
					.addToBackStack(null).commit();
		} else if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_music_fragmlayout, fragment).commit();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mConnection != null) {
			unbindService(mConnection);
		}
		isRunning = false;
	}
	
	
}
