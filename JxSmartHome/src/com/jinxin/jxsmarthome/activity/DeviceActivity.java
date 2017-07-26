package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.ProductStateListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerAreaDaoImpl;
import com.jinxin.db.impl.CustomerProductAreaDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.device.WirelessCurtainLoader;
import com.jinxin.jxsmarthome.cmd.entity.ZigbeeResponse;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.fragment.DeviceFragment;
import com.jinxin.jxsmarthome.fragment.EnvironmentFragment;
import com.jinxin.jxsmarthome.fragment.MainDeviceFragment;
import com.jinxin.jxsmarthome.fragment.RemoteTVControlFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 场景操作
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class DeviceActivity extends BaseScrollableTabActivity {

	private CustomerAreaDaoImpl cadImpl = null;
	private List<CustomerArea> cAreaList = null;
	private FunDetail funDetail = null;
	private List<ProductFun> productFunList = null;
	private ProductStateDaoImpl psdImpl = null;

	public static String DEVICE_KEY = "device";// key值
	public static String ALL_DEVICE = "0000";// 所有设备
	public static String FUNDETAIL = "funDetail";// 所有模式
	private HashMap<String, OnRefreshListener> listenerMap = new HashMap<String, DeviceActivity.OnRefreshListener>();
	private List<String> selectList = new ArrayList<String>();
	private int loadIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		if (ProductConstants.FUN_TYPE_TFT_LIGHT.equals(funDetail.getFunType()) || ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW.equals(funDetail.getFunType())) {// 触摸屏、人体感应不能修改名字，没状态返回
			getMenuInflater().inflate(R.menu.menu_tft, menu);
		} else {
			getMenuInflater().inflate(R.menu.menu_device_state, menu);
		}

		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("设备控制");

		return true;
	}

	/**
	 * 初始数据
	 */
	public void initData() {
		Intent intent = getIntent();
		funDetail = (FunDetail) intent.getExtras().get(MainDeviceFragment.FUNDETAIL);
		psdImpl = new ProductStateDaoImpl(this);

		if (funDetail != null) {
			ProductFunDaoImpl pfImpl = new ProductFunDaoImpl(DeviceActivity.this);
			productFunList = pfImpl.find(null, "funType=? and enable=?", 
					new String[] {funDetail.getFunType(),Integer.toString(1)}, null, null, null, null);
			if (!NetworkModeSwitcher.useOfflineMode(this)) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// 获取一次设备状态
						getStatusFromInternet();
//						if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN)) {
//							loadIndex = 0;
//							loadCurtainState(loadIndex);
//						}
					}
				}).start();
			}
		}
	}

	@Override
	public void initTabsAndFragment() {
		initData();

		cadImpl = new CustomerAreaDaoImpl(DeviceActivity.this);
		cAreaList = cadImpl.find(null, "status=?", new String[] { Integer.toString(1) }, null, null, "areaOrder", null);

		if (ProductConstants.FUN_TYPE_ENV.equals(funDetail.getFunType())) {
			EnvironmentFragment fragment = new EnvironmentFragment();
			addTab("环境监测", fragment);
		} else if (ProductConstants.FUN_TYPE_UFO1.equals(funDetail.getFunType())) {
			Fragment fragment = new RemoteTVControlFragment();
			addTab("遥控控制", fragment);
		} else if (cAreaList != null) {
			newTabFragment();
		}
	}

	/**
	 * 判断分组下是否有设备，构建Tab选项Fragment
	 */
	private void newTabFragment() {
		DeviceFragment sfAll = new DeviceFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(FUNDETAIL, funDetail);
		bundle.putString(DEVICE_KEY, ALL_DEVICE);
		sfAll.setArguments(bundle);
		addTab("全部", sfAll);

		CustomerProductAreaDaoImpl _cpaDaoImpl = new CustomerProductAreaDaoImpl(DeviceActivity.this);
		for (CustomerArea _cArea : cAreaList) {
			List<CustomerProductArea> _cpaList = _cpaDaoImpl.find(null, "areaId=?", new String[] { Integer.toString(_cArea.getId()) }, null, null, null, null);
			if (_cpaList != null && _cpaList.size() > 0) {
				boolean flag = false;
				for (CustomerProductArea _cpArea : _cpaList) {// 遍历此区域下所有设备
					if (flag) {
						break;
					} else {
						for (ProductFun _pf : productFunList) {// 遍历当前选中设备
							if (_cpArea.getFunId() == _pf.getFunId()) { // 此区域包含此设备则New此区域
								DeviceFragment _sf = new DeviceFragment();
								Bundle _bundle = new Bundle();
								_bundle.putString(DEVICE_KEY, Integer.toString(_cArea.getId()));
								_bundle.putSerializable(FUNDETAIL, funDetail);
								_sf.setArguments(_bundle);
								addTab(_cArea.getAreaName(), _sf);
								flag = true;
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 获取设备状态
	 */
	private void getStatusFromInternet() {
		ProductStateListTask psTask = new ProductStateListTask(DeviceActivity.this, funDetail.getFunType());
		psTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(DeviceActivity.this, CommDefines.getSkinManager().string(R.string.qing_qiu_chu_li_zhong));
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
				if (arg != null && arg.length > 0) {
					@SuppressWarnings("unchecked")
					List<ProductState> _proStateList = (List<ProductState>) arg[0];
					CommonMethod.updateProductStateList(DeviceActivity.this, _proStateList);
					mUIHander.sendEmptyMessage(1);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {

			}
		});
		psTask.start();
	}

	/**
	 * 递归获取curtain的状态
	 */
	private void loadCurtainState(final int index) {
		if (index < 0 || productFunList.size() < 1)
			return;

		final ProductFun pf = productFunList.get(index);
		final List<ProductState> psList = psdImpl.find(null, "funId=?", new String[] { Integer.toString(pf.getFunId()) }, null, null, null, null);
		new WirelessCurtainLoader(DeviceActivity.this, new WirelessCurtainLoader.OnDataLoadListener() {
			@Override
			public void onDataLoaded(ZigbeeResponse data) {
				String payload = ((ZigbeeResponse) data).getPayload();
				Logger.debug(null, "payload:" + payload);
				if (!TextUtils.isEmpty(payload)) {
					String[] items = payload.split(" ");
					Logger.debug(null, "length:" + items.length);
					if (items.length > 2) {
						String state = items[items.length - 1];
						Logger.debug(null, "state:" + state);
						ProductState _ps = new ProductState();
						// pf.setState(Integer.parseInt(state , 16 ) + "%");
						if (psList != null && psList.size() > 0) {
							_ps = psList.get(0);
							if (TextUtils.isDigitsOnly(state)) {
								_ps.setState(state);
							} else {
								pf.setState("获取失败");
							}
							psdImpl.update(_ps);
						} else {
							_ps.setFunId(pf.getFunId());
							if (TextUtils.isDigitsOnly(state)) {
								_ps.setState(state);
							} else {
								pf.setState("获取失败");
							}
							psdImpl.insert(_ps, false);
						}
					}
				}
				loadIndex += 1;
				if (loadIndex < productFunList.size())
					loadCurtainState(loadIndex);
			}
		}).loadData(pf, funDetail);
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	/**
	 * 添加界面刷新Listener,存入HashMap
	 * 
	 * @param key
	 * @param listener
	 */
	public void addRefreshListener(String key, OnRefreshListener listener) {
		if (TextUtils.isEmpty(key) || listener == null)
			return;

		listenerMap.put(key, listener);
	}

	public void removeListener(String key) {
		if (listenerMap != null) {
			listenerMap.remove(key);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 1:
			Iterator iter = listenerMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				OnRefreshListener val = (OnRefreshListener) entry.getValue();
				val.onRefresh();
			}
			break;

		default:
			break;
		}
	}

	public void addSelected(Map<String, Boolean> selected) {
		Set<String> keys = selected.keySet();
		for (String key : keys) {
			if(selectList.contains(key)) {
				selectList.remove(key);
			}
			if(selected.get(key).booleanValue() == true) {
				selectList.add(key);
			}
		}
	}

	public List<String> getSelected() {
		return selectList;
	}

	@Override
	protected void refreshFragmente(int position) {
		DeviceFragment df = (DeviceFragment)getFragment(position);
		df.refresh();
	}
}
