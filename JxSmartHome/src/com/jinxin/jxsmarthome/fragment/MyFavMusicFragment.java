package com.jinxin.jxsmarthome.fragment;


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

import com.jinxin.datan.net.command.CustomerMusicLibListTask;
import com.jinxin.datan.net.command.SearchMusicTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerMusicLibDaoImpl;
import com.jinxin.db.impl.MusicLibDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.CloudMusicActivity;
import com.jinxin.jxsmarthome.activity.MainMusicActivity;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.BaiduMusic;
import com.jinxin.jxsmarthome.entity.CustomerMusicLib;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.MyMusicListAdapter;
import com.jinxin.jxsmarthome.ui.widget.pullToRefresh.PullToRefreshListView;
import com.jinxin.jxsmarthome.ui.widget.pullToRefresh.PullToRefreshListView.IXListViewListener;

/**
 * 收藏音乐界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class MyFavMusicFragment extends Fragment implements OnClickListener,IXListViewListener{
	
    private PullToRefreshListView listView;
    private CustomerMusicLibDaoImpl cmDaoImpl = null;
    private CustomerMusicLibListTask cmlTask = null;
    private List<CustomerMusicLib> cMusicList = null;
    private List<CustomerMusicLib> musicList = null;
    
    private MyMusicListAdapter adapter = null;
    
    private int pagePos = 1;
    private int pageRows = 10;
    
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
				adapter.setList(cMusicList);
				adapter.notifyDataSetChanged();
				break;
			case LOAD_MORE:
				adapter = new MyMusicListAdapter(getActivity(), cMusicList);
				listView.setAdapter(adapter);
				break;
			case SHOW_TIPS:
				JxshApp.showToast(getActivity(), "没有更多收藏歌曲了");
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
		super.onCreateOptionsMenu(menu, inflater);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("我收藏的音乐");
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().onBackPressed();
		}
		return true;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("我收藏的音乐");
	}

	/**
	 * 初始化View
	 * @param view
	 */
	private void initView(View view){
		this.listView = (PullToRefreshListView) view.findViewById(R.id.fav_music_lv);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(true);
		this.adapter = new MyMusicListAdapter(getActivity(), cMusicList);
		listView.setAdapter(adapter);
		listView.setXListViewListener(this);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CustomerMusicLib cMusic = cMusicList.get(position-1);
				if (cMusic != null) {
					searchTask(cMusic.getMusicName(), cMusic.getSinger());
				}
			}
		});
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		this.cmDaoImpl = new CustomerMusicLibDaoImpl(getActivity());
		this.cMusicList = cmDaoImpl.find();
		getMusicList();
	}

	/**
	 * 获取收藏的音乐请求
	 */
	private void getMusicList(){
		cmlTask  = new CustomerMusicLibListTask(getActivity(), pagePos, pageRows);
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
				if (arg != null && arg.length > 0) {
					musicList = (List<CustomerMusicLib>) arg[0];
					if (musicList != null && musicList.size() > 0) {
						CommonMethod.updateMusicList(getActivity(),cMusicList);
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
		JxshApp.getDatanAgentHandler().postDelayed(cmlTask, 10);
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
					}
	        		Intent _intent = new Intent(getActivity(),CloudMusicActivity.class);
//	        		_intent.putExtra("musicLib", musicLib);
	        		startActivity(_intent);
	        	}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		JxshApp.getDatanAgentHandler().postDelayed(smTask, 10);
		
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
