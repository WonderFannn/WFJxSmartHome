package com.jinxin.jxsmarthome.fragment;

import java.util.List;

import com.jinxin.datan.net.command.InfraredTranspondTask;
import com.jinxin.datan.net.command.ProductPatternOperationListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.AmendModeNewActivity;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.LoginActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.ModeGridAdapter;
import com.jinxin.jxsmarthome.ui.widget.pinnerExpandListView.PinnedHeaderExpandableListView;
import com.jinxin.jxsmarthome.util.Logger;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class SceneFragment extends Fragment {
	
	private GridView sceneView = null;
	
	private CustomerPatternDaoImpl modeDaoImpl = null;
	private List<CustomerPattern> customerPatternList;
	private ModeGridAdapter adapter;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 1 :
				adapter = new ModeGridAdapter(getActivity(), customerPatternList);
				sceneView.setAdapter(adapter);
//				adapter.notifyDataSetChanged();
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.scene_layout, null);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerBoradcastReceiver();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mUpdateBroadcastReceiver != null) {
			try {
				getActivity().unregisterReceiver(mUpdateBroadcastReceiver);
			} catch (IllegalArgumentException e) {
				if (e.getMessage().contains("Receiver not registered")) {
					Logger.debug(null, "Receiver alredy unregister");
				} else {
					throw e;
				}
			}
		}
	}

	public void initView(View view){
		this.sceneView = (GridView) view.findViewById(R.id.scene_grid_view);
//		this.adapter = new ModeGridAdapter(getActivity(), customerPatternList);
//		this.adapter.setDownLoadHandler(((
//				BaseActionBarActivity) getActivity()).getmDownLoadHandler());
//		this.sceneView.setAdapter(adapter);
	}
	
	public void initData(){
		registerBoradcastReceiver();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//从缓存取模式
				modeDaoImpl = new CustomerPatternDaoImpl(getActivity());
				String areaId = getArguments().getString(MainSceneFragment.SCENE_KEY);
				if (areaId.equals(MainSceneFragment.ALL_SCENE)) {
					customerPatternList = modeDaoImpl.find();
				}else{
					customerPatternList = modeDaoImpl.find(null,"patternGroupId=?",
							new String[]{areaId}, null, null, null, null);
				}
				if (customerPatternList!= null && customerPatternList.size()>0) {
					mHandler.sendEmptyMessage(1);
				}
			}
		}).start();
		//从缓存取模式
//		modeDaoImpl = new CustomerPatternDaoImpl(getActivity());
//		String areaId = getArguments().getString(MainSceneFragment.SCENE_KEY);
//		if (areaId.equals(MainSceneFragment.ALL_SCENE)) {
//			customerPatternList = modeDaoImpl.find();
//		}else{
//			customerPatternList = modeDaoImpl.find(null,"patternGroupId=?",
//					new String[]{areaId}, null, null, null, null);
//		}
//		if (customerPatternList != null && customerPatternList.size()>0) {
//			this.adapter = new ModeGridAdapter(getActivity(), customerPatternList);
//			this.adapter.setDownLoadHandler(((
//					BaseActionBarActivity) getActivity()).getmDownLoadHandler());
//			this.sceneView.setAdapter(adapter);
//		}
		
		//获取红外记录
				
		
		
	}
	
	/**
	 * 更新模式的广播事件处理
	 */
	private BroadcastReceiver mUpdateBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastManager.ACTION_UPDATE_MODE_MESSAGE)) {
				// 更新模式
				initData();
				mHandler.sendEmptyMessage(1);
			}
		}
	};
	
	/**
	 * 注册广播
	 */
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(BroadcastManager.ACTION_UPDATE_MODE_MESSAGE);  
        //注册广播        
        getActivity().registerReceiver(mUpdateBroadcastReceiver, myIntentFilter); 
    }  
	
}
