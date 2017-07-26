package com.jinxin.jxsmarthome.fragment;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jinxin.datan.net.command.MusicListGetTask;
import com.jinxin.datan.net.command.SearchMusicTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.MusicLibDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.CloudMusicActivity;
import com.jinxin.jxsmarthome.activity.MainMusicActivity;
import com.jinxin.jxsmarthome.activity.MusicSearchActivity;
import com.jinxin.jxsmarthome.entity.BaiduMusic;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.HotMusicListAdapter;
import com.jinxin.jxsmarthome.ui.widget.pullToRefresh.PullToRefreshListView;
import com.jinxin.jxsmarthome.ui.widget.pullToRefresh.PullToRefreshListView.IXListViewListener;

/**
 * 热门歌手界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class HotMusicFragment extends Fragment implements OnClickListener,IXListViewListener{
	
    private PullToRefreshListView listView;
    private MusicListGetTask cmlTask = null;
    private List<MusicLib> cMusicList = null;
    private List<MusicLib> musicList = null;
    
    private HotMusicListAdapter adapter = null;
    
    private String isHot = "1";//1= 是，0=否
    private String singerName ="";
    private int pagePos = 1;//当前页
    private int pageRows = 10;//每页显示数
    
    private static final int REFRESH = 0;
    private static  final int LOAD_MORE = 1;
    private static  final int SHOW_TIPS = 2;

    @SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH:
				cMusicList.addAll(musicList);
				adapter.notifyDataSetChanged();
				break;
			case LOAD_MORE:
				adapter = new HotMusicListAdapter(getActivity(), cMusicList);
				listView.setAdapter(adapter);
				break;
			case SHOW_TIPS:
				JxshApp.showToast(getActivity(), "暂时没有更多歌曲了");
				break;
			}
		}
    	
    };
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music_ist_layout, container, false);
		initData();
		initView(view);
		return view;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.main_music_menu);
		inflater.inflate(R.menu.menu_music_search, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("音乐");
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search_baidu_music_btn:
			getActivity().startActivity(new Intent(getActivity(),MusicSearchActivity.class));
			break;
		}
		return true;
	}
	
	/**
	 * 初始化View
	 * @param view
	 */
	private void initView(View view){
		this.listView = (PullToRefreshListView) view.findViewById(R.id.fav_music_lv);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(true);
		adapter = new HotMusicListAdapter(getActivity(), cMusicList);
		listView.setAdapter(adapter);
		listView.setXListViewListener(this);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MusicLib music = cMusicList.get(position-1);
				if (music != null) {
					searchTask(music.getName(), music.getSinger());
				}
			}
		});
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		String sName = getArguments().getString("singerName");
		if (!TextUtils.isEmpty(sName)) {
			singerName = sName;
		}
		cMusicList = new ArrayList<MusicLib>();
		getMusicList();
	}

	/**
	 * 获取热门音乐列表请求
	 */
	private void getMusicList(){
		cmlTask = new MusicListGetTask(getActivity(), singerName, isHot, "",
				"", "", "", "", pagePos, pageRows);
		cmlTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length >0) {
					musicList = (List<MusicLib>) arg[0];
					if (musicList != null && musicList.size() > 0) {
						handler.sendEmptyMessage(REFRESH);
					}else{
						handler.sendEmptyMessage(SHOW_TIPS);
					}
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		cmlTask.start();
	}
	
	/**
	 * 搜索歌曲任务
	 */
	private void searchTask(final String songName,final String singerName){
		
		SearchMusicTask smTask = new SearchMusicTask(getActivity(), 4, songName, singerName);
		
		smTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(getActivity(), "正在搜索歌曲...");
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
	        		MainMusicActivity.updateMusicHisList(getActivity(), musicLib);
	        		//从数据库查询出带ID的Music对象
	        		MusicLibDaoImpl mlDaoImpl = new MusicLibDaoImpl(getActivity());
	        		List<MusicLib> tempList = mlDaoImpl.find(null, "name=? and singer=?",
	        				new String[]{songName,singerName}, null, null, null, null);
	        		if (tempList != null && tempList.size() > 0) {
	        			musicLib = tempList.get(0);
	        			JxshApp.instance.setMusic(musicLib);
	        			Intent _intent = new Intent(getActivity(),CloudMusicActivity.class);
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
	
	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("音乐");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		
	}

	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("刚刚");
	}
	
	@Override
	public void onRefresh() {
		handler.sendEmptyMessage(LOAD_MORE);
		onLoad();
		
	}
	@Override
	public void onLoadMore() {
		pagePos++;
		getMusicList();
		onLoad();
	}

}
