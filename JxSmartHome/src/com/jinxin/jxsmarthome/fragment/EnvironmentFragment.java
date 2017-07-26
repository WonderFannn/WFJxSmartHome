package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.device.EnvironmentLoader;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.Environment;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.ui.adapter.EnvListAdapter;
import com.jinxin.jxsmarthome.util.AppUtil;

/**
 * 环境监测
 * @author TangLong
 * @company 金鑫智慧
 */
public class EnvironmentFragment extends Fragment {
	private final static int GET_DATA_SUCCESS = 1;
	private final static int GET_ENV_LEVEL_SUCCESS = 2;
	private final static int GET_ENV_LEVEL_FAIL = 3;
	
	private ListView mListView;
	private EnvListAdapter mAdapter;
	private List<ProductFun> mListData;
	private FunDetail funDetail;
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case GET_DATA_SUCCESS :
				mAdapter.notifyDataSetChanged();
				loadEnvironmentStateInListView();
				break;
			case GET_ENV_LEVEL_SUCCESS :
				break;
			case GET_ENV_LEVEL_FAIL :
				break;
			}
			return true;
		}
	});
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParam();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_environment, container, false);
		initView(view);
		initData();
		return view;
	}
	
	private void initParam() {
		mListData = new ArrayList<ProductFun>();
	}
	
	private void initView(View view) {
		mListView = (ListView) view.findViewById(R.id.environment_list);
		mAdapter = new EnvListAdapter(getActivity(), mListData);
		mListView.setAdapter(mAdapter);
	}
	
	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mListData.clear();
				mListData = getEnvironmentMonitors();
				mAdapter.setList(mListData);
				mHandler.sendEmptyMessage(GET_DATA_SUCCESS);
			}
		}).start();
	}
	
	// 获取飞碟环境的位置
	int loadIndex = 0;
	
	/**
	 * 获取listview中curtain的状态
	 */
	private void loadEnvironmentStateInListView() {
		loadEnvironmentState(loadIndex);
	}
	
	/**
	 * 递归获取curtain的状态
	 */
	private void loadEnvironmentState(final int index) {
		
		if(mListData.size() < 1 && index < 0) return;
		final ProductFun pf = mListData.get(index);
		funDetail = AppUtil.getFunDetailByFunType(getActivity(), pf.getFunType());
		new EnvironmentLoader(getActivity(), new EnvironmentLoader.OnDataLoadListener() {
			@Override
			public void onDataLoaded(Environment data) {
				StringBuilder sb = new StringBuilder(); 
				if(!TextUtils.isEmpty(data.getKq())){
					sb.append(data.getKq());
					sb.append(",");
				}
				if(!TextUtils.isEmpty(data.getWd())) {
					sb.append(data.getWd());
					sb.append(",");
				}
				if(!TextUtils.isEmpty(data.getSd())) {
					sb.append(data.getSd());
				}
				pf.setState(sb.toString());
				mAdapter.notifyDataSetChanged();
				loadIndex += 1;
				if(loadIndex < mListData.size()) loadEnvironmentState(loadIndex);
			}
		}).loadData(pf, funDetail); 
	}
	
	private List<ProductFun> getEnvironmentMonitors() {
		return AppUtil.getProductFunByFunType(getActivity(), ProductConstants.FUN_TYPE_ENV);
	}
}
