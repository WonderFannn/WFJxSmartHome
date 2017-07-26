package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jinxin.datan.net.command.SingerLibListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.SingerLibDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.MainMusicActivity;
import com.jinxin.jxsmarthome.activity.MusicSearchActivity;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.SingerLib;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.HotSingerListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.SingerGridAdapter;
import com.jinxin.jxsmarthome.ui.adapter.ViewPagerAdapter;

/**
 * 热门歌手界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class HotSingerFragment extends Fragment implements OnClickListener, OnPageChangeListener{
	
	private ViewPager singerPager;
	private LinearLayout itemGroup;
	private ListView singerListview;
	private ViewPagerAdapter pagerAdapter;
	private HotSingerListAdapter iistAdapter;
	private SingerGridAdapter gridAdapter1;
	private SingerGridAdapter gridAdapter2;
	private SingerGridAdapter gridAdapter3;
	private SingerGridAdapter gridAdapter4;
	private GridView singerGrid1,singerGrid2,singerGrid3,singerGrid4;
	
	private List<SingerLib> singerList;
	private List<SingerLib> hotSingerList;
	private List<SingerLib> hotList1 = new ArrayList<SingerLib>();
	private List<SingerLib> hotList2 = new ArrayList<SingerLib>();;
	private List<SingerLib> hotList3 = new ArrayList<SingerLib>();;
	private List<SingerLib> hotList4 = new ArrayList<SingerLib>();;
	private ImageView[] pointArr;//装点点的ImageView数组
    private ArrayList<View> viewList;
    
    private SingerLibDaoImpl slDaoImpl = null;
    

    @SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				iistAdapter.notifyDataSetChanged();
				pagerAdapter.notifyDataSetChanged();
				gridAdapter1.notifyDataSetChanged();
				gridAdapter2.notifyDataSetChanged();
				gridAdapter3.notifyDataSetChanged();
				gridAdapter4.notifyDataSetChanged();
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
		View view = inflater.inflate(R.layout.hot_singer_layout, container, false);
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
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("热门歌手");
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
	
	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("热门歌手");
	}

	/**
	 * 初始化View
	 * @param view
	 */
	private void initView(View view){
		this.singerPager = (ViewPager) view.findViewById(R.id.singer_view_pager);
		this.itemGroup = (LinearLayout) view.findViewById(R.id.singer_view_group);
		this.singerListview = (ListView) view.findViewById(R.id.singer_listview);
		
		this.iistAdapter = new HotSingerListAdapter(getActivity(), singerList);
		singerListview.setAdapter(iistAdapter);
		
		this.gridAdapter1 = new SingerGridAdapter(getActivity(), hotList1);
		this.gridAdapter2 = new SingerGridAdapter(getActivity(), hotList2);
		this.gridAdapter3 = new SingerGridAdapter(getActivity(), hotList3);
		this.gridAdapter4 = new SingerGridAdapter(getActivity(), hotList4);
		initPage();
		singerListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SingerLib singerLib = singerList.get(position);
				if (singerLib != null) {
					FragmentTransaction fTransaction = ((MainMusicActivity)getActivity()).
							getSupportFragmentManager().beginTransaction();
					Bundle bundle = new Bundle();
					HotMusicFragment fragment = new HotMusicFragment();
					bundle.putString("singerName", singerLib.getSinger());
					fragment.setArguments(bundle);
					fTransaction.add(R.id.main_music_fragmlayout, fragment)
					.addToBackStack(null).commit();
				}
			}
		});
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		slDaoImpl = new SingerLibDaoImpl(getActivity());
		singerList = slDaoImpl.find();
		hotSingerList = slDaoImpl.find(null, "hot=?",
				new String[]{"1"}, null, null, null, "12");
		getHotSingerTask();
		getSingerList();
	}

	/**
	 * 初始热门歌手滑动的ViewPager
	 */
	@SuppressWarnings("static-access")
	private void initPage(){
		splitList(hotSingerList);
		// 填充滚动歌手列表的ViewPager
		viewList = new ArrayList<View>();
		LayoutInflater lf = getLayoutInflater(getArguments()).from(getActivity());
		View view1 = lf.inflate(R.layout.hot_singer_grid_layout, null);
		View view2 = lf.inflate(R.layout.hot_singer_grid_layout, null);
		View view3 = lf.inflate(R.layout.hot_singer_grid_layout, null);
		View view4 = lf.inflate(R.layout.hot_singer_grid_layout, null);
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);
		viewList.add(view4);
		singerGrid1 = (GridView) view1.findViewById(R.id.hot_singer_gridview);
		singerGrid2 = (GridView) view2.findViewById(R.id.hot_singer_gridview);
		singerGrid3 = (GridView) view3.findViewById(R.id.hot_singer_gridview);
		singerGrid4 = (GridView) view4.findViewById(R.id.hot_singer_gridview);
		
		singerGrid1.setAdapter(gridAdapter1);
		singerGrid2.setAdapter(gridAdapter2);
		singerGrid3.setAdapter(gridAdapter3);
		singerGrid4.setAdapter(gridAdapter4);
		
		pagerAdapter = new ViewPagerAdapter(viewList);
		singerPager.setAdapter(pagerAdapter);
		singerPager.setOnPageChangeListener(this);
		
		// 添加ViewPager选项标识
		this.pointArr = new ImageView[viewList.size()];
		for (int i = 0; i < pointArr.length; i++) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new LayoutParams(20, 20));
			pointArr[i] = imageView;
			if (i == 0) {
				pointArr[i].setBackgroundResource(R.drawable.icon_switch_white);
			} else {
				pointArr[i].setBackgroundResource(R.drawable.icon_switch_black);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			itemGroup.addView(imageView, layoutParams);
			
		}
		
	}
	
	private void splitList(List<SingerLib> singerList){
		if (singerList != null && singerList.size() >= 12) {
			hotList1.clear();
			hotList2.clear();
			hotList3.clear();
			hotList4.clear();
			for (int i = 0; i < singerList.size(); i++) {
				if (i < 3) {
					hotList1.add(singerList.get(i));
				}else if(i < 6){
					hotList2.add(singerList.get(i));
				}else if(i < 9){
					hotList3.add(singerList.get(i));
				}else if(i < 12){
					hotList4.add(singerList.get(i));
				}
			}
			gridAdapter1.setSingerList(hotList1);
			gridAdapter2.setSingerList(hotList2);
			gridAdapter3.setSingerList(hotList3);
			gridAdapter4.setSingerList(hotList4);
		}
	}
	
	/**
	 * 获取所有歌手请求
	 */
	private void getSingerList(){
		SingerLibListTask sllTask = new SingerLibListTask(getActivity(), "", "", "");
		sllTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					singerList = (List<SingerLib>) arg[0];
//					CommonMethod.updateSingerList(getActivity(), singerList);
					iistAdapter.setList(singerList);
					handler.sendEmptyMessageDelayed(0, 500);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		JxshApp.getDatanAgentHandler().postDelayed(sllTask, 10);
	}
	
	/**
	 * 获取热门歌手
	 */
	private void getHotSingerTask(){
		SingerLibListTask hotSingerTask = new SingerLibListTask(getActivity(), "", "1", "");
		hotSingerTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					hotSingerList = (List<SingerLib>) arg[0];
					CommonMethod.updateSingerList(getActivity(), hotSingerList);
					splitList(hotSingerList);
					handler.sendEmptyMessageDelayed(0, 500);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		JxshApp.getDatanAgentHandler().postDelayed(hotSingerTask, 10);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		setImageBackground(arg0);
	}
	
	/** 
     * 设置选中的tip的背景
     * @param selectItems
     */  
    private void setImageBackground(int selectItems){
        for(int i=0; i<pointArr.length; i++){  
            if(i == selectItems){  
                pointArr[i].setBackgroundResource(R.drawable.icon_switch_white);  
            }else{  
                pointArr[i].setBackgroundResource(R.drawable.icon_switch_black);  
            }  
        }  
    }
}
