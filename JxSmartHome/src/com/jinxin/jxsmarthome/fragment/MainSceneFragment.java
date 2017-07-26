package com.jinxin.jxsmarthome.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.db.impl.CustomerAreaDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.AddNewModeActivity;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;

/*
 *  场景界面
 */
public class MainSceneFragment extends BaseScrollableTabFragment {
	private List<CustomerArea> cAreaList = null;
	private List<CustomerPattern> cPatternList = null;
	
	public static String SCENE_KEY = "customerPattern";//key值
	public static String ALL_SCENE = "0000";//所有模式
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void initTabsAndFragment() {
		
		SceneFragment sfAll = new SceneFragment();
		Bundle bundle = new Bundle();
		bundle.putString(SCENE_KEY, ALL_SCENE);
		sfAll.setArguments(bundle);
		addTab("全部", sfAll);
		
		cAreaList = getCustomerAreas();
		cPatternList = getCustomerPattern();
		
		if (cAreaList != null) {
			for (CustomerArea _ca : cAreaList) {
				if (cPatternList != null) {
					for (CustomerPattern _cp : cPatternList) {
						if (_cp.getPatternGroupId() == _ca.getId()) {
							SceneFragment _sf = new SceneFragment();
							Bundle _bundle = new Bundle();
							_bundle.putString(SCENE_KEY,Integer.toString(_ca.getId()));
							_sf.setArguments(_bundle);
							addTab(_ca.getAreaName(), _sf);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 缓存获取所有区域分组
	 * @return
	 */
	private List<CustomerArea> getCustomerAreas() {
		CustomerAreaDaoImpl cadImpl = new CustomerAreaDaoImpl(getActivity());
		return cadImpl.find(null, "status=?",
				new String[]{Integer.toString(1)}, null, null, "areaOrder", null);
	}
	
	/**
	 * 缓存获取所有模式
	 * @return
	 */
	private List<CustomerPattern> getCustomerPattern(){
		CustomerPatternDaoImpl cpdImpl = new CustomerPatternDaoImpl(getActivity());
		return cpdImpl.find();
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_mode_btn:
			if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
				JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
						.string(R.string.li_xian_cao_zuo_tips));
			}else if(CommUtil.isSubaccount()){
				JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
						.string(R.string.subaccount_cao_zuo_error_tips));
			}else{
				startActivity(new Intent(getActivity(), AddNewModeActivity.class));
			}
			break;
		case android.R.id.home:
			break;
		default:
			break;
		}
		return true;
	}
	
}
