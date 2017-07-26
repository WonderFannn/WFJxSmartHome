package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.jinxin.db.impl.CustomerAreaDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.MainMenuItem;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.ModeFragmentGridAdapter;
import com.jinxin.jxsmarthome.ui.widget.MyGalleryLayout;
import com.jinxin.jxsmarthome.ui.widget.MyGalleryLayout.MyGalleryItemOnClickListener;
import com.jinxin.jxsmarthome.util.CommUtil;

/**
 * 首页-模式
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ModeFragment extends Fragment{
	private Context context;
	
	private List<CustomerPattern> customerPatternList;
	private ModeFragmentGridAdapter adapter = null;
	private GridView gridView = null;
	private CustomerPatternDaoImpl modeDaoImpl = null;
	
	private MyGalleryLayout gallery = null;
	
	private List<MainMenuItem> modeGroupList = null;//模式分组List
	private List<CustomerArea> cAreaList = null;
	
	private String SELECT_All = "0000";//全部分组选中ID
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view= CommDefines.getSkinManager().view(R.layout.mode_fragment_layout, container);
		this.initData();
		this.initView(view);
		return view;
	}
	
	private void initData(){
		if(this.customerPatternList == null){
			this.customerPatternList = new ArrayList<CustomerPattern>();
		}
		//从缓存取模式
		CustomerPatternDaoImpl modeDaoImpl = new CustomerPatternDaoImpl(this.getActivity());
		if(modeDaoImpl != null){
			this.customerPatternList = modeDaoImpl.find(null, "status=?", new String[]{"1"}, null, null, null, null);
		}
	}
	
	private void initView(View view){
		if (modeGroupList == null) {
			modeGroupList = new ArrayList<MainMenuItem>();
		}
		modeGroupList = initGroupData();
		/***测试模式分组假数据*****************/
//		MainMenuItem item1  = new MainMenuItem("1", R.drawable.ic_launcher,"test");
//		MainMenuItem item2  = new MainMenuItem("2", R.drawable.ic_launcher,"test");
//		MainMenuItem item3  = new MainMenuItem("3", R.drawable.ic_launcher,"test");
//		MainMenuItem item4  = new MainMenuItem("4", R.drawable.ic_launcher,"test");
//		modeGroupList.add(item1);
//		modeGroupList.add(item2);
//		modeGroupList.add(item3);
//		modeGroupList.add(item4);
		/************************************/
		
		this.gallery = (MyGalleryLayout) view.findViewById(R.id.my_gallery_layout);
		for (MainMenuItem mItem : modeGroupList) {
			mItem.addOnClickListener(new MyListener(gallery, mItem));
		}
		
		this.gallery.setPadding(CommUtil.dip2px(context, 3), 0, CommUtil.dip2px(context, 3), 0);
		this.gallery.setTextPadding(CommUtil.dip2px(context, 20), 20, CommUtil.dip2px(context, 20), 20);
		this.gallery.setAllowSelected(true);
		if (modeGroupList.size() > 0) {
			this.gallery.setView(this.modeGroupList);
			this.gallery.checkScrollArrowView( this.modeGroupList.size(), 4);
		}
		
		this.gridView = (GridView)view.findViewById(R.id.gridView_mode);
		this.adapter = new ModeFragmentGridAdapter(context, customerPatternList);
		this.adapter.setDownLoadHandler(JxshApp.getDatanAgentHandler());
		this.gridView.setAdapter(adapter);
	}
	
	/**
	 * 填充模式分组数据
	 */
	private List<MainMenuItem> initGroupData(){
		List<MainMenuItem> list = new ArrayList<MainMenuItem>();
		//初始第一项显示全部设备
		MainMenuItem mmItem = new MainMenuItem(SELECT_All, -1,"全部");
		mmItem.setSelected(true);
		list.add(mmItem);
		
		CustomerAreaDaoImpl cadImpl = new CustomerAreaDaoImpl(context);
		cAreaList = cadImpl.find(null, "status=?",
				new String[]{Integer.toString(1)}, null, null, "areaOrder", null);
		if (cAreaList == null) {
			return list;
		}
		for (CustomerArea _cArea : cAreaList) {
			MainMenuItem item = new MainMenuItem(
					Integer.toString(_cArea.getId()), -1,
					_cArea.getAreaName());
			list.add(item);
		}
		return list;
	}
	
	/**
	 * 模式分组监听
	 */
	class MyListener extends MyGalleryItemOnClickListener{
		private MainMenuItem item = null;
		public MyListener(MyGalleryLayout myGalleryLayout, MainMenuItem item) {
			myGalleryLayout.super(item);
			this.item = item;
		}

		@Override
		public void doEvent() {
			//从缓存取模式
			if (item != null) {
				modeDaoImpl = new CustomerPatternDaoImpl(context);
				if (item.getId().equals(SELECT_All)) {
					customerPatternList = modeDaoImpl.find();
				}else{
					String patternGroupId = item.getId();
					customerPatternList = modeDaoImpl.find(null,
							"patternGroupId=?", new String[]{patternGroupId}, null, null, null, null);
				}
				if (customerPatternList != null) {
					adapter = new ModeFragmentGridAdapter(context, customerPatternList);
					adapter.setDownLoadHandler(JxshApp.getDatanAgentHandler());
					gridView.setAdapter(adapter);
					handler.sendEmptyMessage(0);
				}
			}
		}
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context=activity;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
}
