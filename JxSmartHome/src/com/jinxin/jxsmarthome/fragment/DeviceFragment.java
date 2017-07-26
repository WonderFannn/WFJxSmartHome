package com.jinxin.jxsmarthome.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.command.ProductDoorContactListTask;
import com.jinxin.datan.net.command.ProductStateListTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerProductAreaDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.DeviceActivity;
import com.jinxin.jxsmarthome.activity.DeviceActivity.OnRefreshListener;
import com.jinxin.jxsmarthome.activity.GeoCoderActivity;
import com.jinxin.jxsmarthome.activity.InfraredTranspondActivity;
import com.jinxin.jxsmarthome.activity.PlutoSoundBoxActivity;
import com.jinxin.jxsmarthome.activity.WirelessAirConditionOutletActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderShort;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.device.WirelessCurtainLoader;
import com.jinxin.jxsmarthome.cmd.entity.ZigbeeResponse;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductDoorContact;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.img.BitmapTools;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.DeviceControlAdapter;
import com.jinxin.jxsmarthome.ui.popupwindow.ShowSuggestWindows;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.FileManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceFragment extends Fragment implements OnClickListener{
	
	private static final int REFRESH = 1;
	private static final int LOADING = 2;
	
	private ListView listView = null;
	private Bitmap openIcon = null;
	private Bitmap closeIcon = null;
	private FunDetail funDetail = null;
	private List<ProductFun> productFunList = null;
	private DeviceControlAdapter adapter = null;
	private ProductStateDaoImpl psdImpl;
	private LinearLayout lightsLayout = null;
	private Button lightsBtn = null;
	
	private List<ProductState> psList;
	private String lightColor = "";
	private ArrayList<String> lightsId = null;
	private TaskListener<ITask> listener;
	private List<ProductDoorContact> doorContacts = null;
	private DeviceActivity activity = null;
	private int currId = 0;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH:
				if (adapter != null) {
					List<String> selected = activity.getSelected();
					if (null != selected) {
						adapter.setCheckedItem(selected);
					}
					adapter.notifyDataSetChanged();
				}
				break;
			case LOADING:
				if (productFunList == null || funDetail ==null || productFunList.size() < 1) {
					return;
				}
				if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT)
						|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)
						|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)
						|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) {
					if (!lightsLayout.isShown()) {
						lightsLayout.setVisibility(View.VISIBLE);
					}
				}else{
					lightsLayout.setVisibility(View.GONE);
				}
				getStates();
				adapter = new DeviceControlAdapter(getActivity(), listView,
						productFunList, funDetail,psList, openIcon, closeIcon);
				if(ProductConstants.FUN_TYPE_DOOR_CONTACT.equals(funDetail.getFunType()) ||
						ProductConstants.FUN_TYPE_INFRARED.equals(funDetail.getFunType()) ||
						ProductConstants.FUN_TYPE_SMOKE_SENSE.equals(funDetail.getFunType()) ||
						ProductConstants.FUN_TYPE_GAS_SENSE.equals(funDetail.getFunType())){
					if (doorContacts != null && doorContacts.size() > 0) {
						adapter.setDoorContactList(doorContacts);
					}
				}
				List<String> selected = activity.getSelected();
				if (null != selected) {
					adapter.setCheckedItem(selected);
				}
				listView.setAdapter(adapter);
				listView.setSelection(currId);
				break;
			}
		}
	};
	
	/* 自定义广播，用于接受推送信息的改变 */
	private BroadcastReceiver mShowBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BroadcastManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				handler.sendEmptyMessageDelayed(LOADING, 300);
			}else if(BroadcastManager.MESSAGE_WARN_ERROR.equals(intent.getAction())){
				String msg = intent.getStringExtra("msg");
				String whId = intent.getStringExtra("whId");
				ProductFun _pf = AppUtil.getSingleProductFunByWhId(context, whId);
				if (_pf != null) {
					ProductState _ps = getProductState(_pf);
					_ps.setState("00");
					psdImpl.update(_ps);
					refresh();
				}
				
			}
		}
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((DeviceActivity) activity).addRefreshListener(DeviceFragment.this.toString(),new OnRefreshListener(){

			@Override
			public void onRefresh() {
				handler.sendEmptyMessage(LOADING);
			}
		});
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		activity = (DeviceActivity) this.getActivity();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastManager.MESSAGE_RECEIVED_ACTION);
		filter.addAction(BroadcastManager.MESSAGE_WARN_ERROR);
		// 刷新设备状态
		getActivity().registerReceiver(mShowBroadcastReceiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (DeviceActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_device_contorl, null);
		initData();
		initView(view);
	
		itemListener();
		
		//新手引导
		((BaseActionBarActivity)this.getActivity()).suggestWindow = new ShowSuggestWindows(getActivity(), R.drawable.bg_guide_device1, "");
		((BaseActionBarActivity)this.getActivity()).suggestWindow.showFullWindows("DeviceFragment",R.drawable.bg_guide_device2);
		
		return view;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
			break;
		case R.id.device_state_btn:
			if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
				JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
						.string(R.string.li_xian_cao_zuo_tips));
				return false;
			}
			//平台获取设备状态
			getStatusFromInternet();
			//获取无线窗帘状态
//			if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funDetail.getFunType())) {
//				loadCurtainStateInListView();
//			}
			break;
		case R.id.device_demend_name:
			if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
				JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
						.string(R.string.li_xian_cao_zuo_tips));
				return false;
			}
			if (adapter != null) {
				if (adapter.isSave()) {
					adapter.setSave(false);
				} else {
					adapter.setSave(true);
				}
				handler.sendEmptyMessage(REFRESH);
			}
			break;
		}
		return true;
	}

	public void initView(View view){
		
		this.lightsLayout = (LinearLayout) view.findViewById(R.id.lights_control);
		this.listView = (ListView) view.findViewById(R.id.deveic_control_list);
		this.lightsBtn = (Button) view.findViewById(R.id.lights_btn);
		lightsBtn.setOnClickListener(this);
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Logger.info("funtype", funDetail.getFunType()+","+funDetail.getFunName());
				Fragment fragment;
				ProductFun productFun = productFunList.get(arg2);
				if (funDetail.getFunType().equals(
						ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN)) {//有线窗帘
					fragment = new CurtainFragment();
					if (productFun != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
					}
					addDialogFragment(fragment);
				} if (funDetail.getFunType().equals(
						ProductConstants.FUN_TYPE_WIRELESS_CURTAIN)) {//无线窗帘
					fragment = new WirelessCurtainFragment();
					if (productFun != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
					}
					addDialogFragment(fragment);
				}else if (funDetail.getFunType().equals(
						ProductConstants.FUN_TYPE_COLOR_LIGHT)) {//普通彩灯
					fragment = new LightsColorFragment();
					if (productFunList != null) {
						Bundle bundle = new Bundle();
						ArrayList<String> whIdList = new ArrayList<String>();
						whIdList.add(productFun.getWhId());
						bundle.putStringArrayList(
								ControlDefine.KEY_COLOR_LIGHT_LIST, whIdList);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
					}
					addDialogFragment(fragment);
				}else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) //无线球灯泡
						|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)//水晶灯
						|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)//无线射灯
						|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)) {//吸顶灯
					fragment = new PopLightFragment();
					if (productFunList != null) {
						Bundle bundle = new Bundle();
						ArrayList<String> whIdList = new ArrayList<String>();
						whIdList.add(productFun.getWhId());
						bundle.putStringArrayList(
								ControlDefine.KEY_COLOR_LIGHT_LIST, whIdList);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
					}
					addDialogFragment(fragment);
				}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)){//灯带
					fragment = new LightBeltFragment();
					if (productFunList != null) {
						Bundle bundle = new Bundle();
						ArrayList<String> whIdList = new ArrayList<String>();
						whIdList.add(productFun.getWhId());
						bundle.putStringArrayList(
								ControlDefine.KEY_COLOR_LIGHT_LIST, whIdList);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
					}
					addDialogFragment(fragment);
				}else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_AUTO_LOCK)) {//普通锁
					fragment = new DoorLockFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				} else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)){//双路开关
					fragment = new DoubleSocketFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)||
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)||
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)||
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)){//三路开关
					fragment = new ThreeSocketFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)||
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)||
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)||
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)||
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)||
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)){//多路开关
					fragment = new MulitipleSocketFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				}else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_GATEWAY)){//无线网关
					fragment = new GateWayFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				} else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_DOOR_CONTACT) ||//门磁
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_INFRARED) || //人体感应
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_SMOKE_SENSE)|| //烟感
						funDetail.getFunType().equals(ProductConstants.FUN_TYPE_GAS_SENSE)){//气体感应
					fragment = new DoorMagnetFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun", productFun);
						bundle.putSerializable("funDetail", funDetail);
						if (doorContacts != null && doorContacts.size() > 0) {
							bundle.putSerializable("productDoorContact",
									doorContacts.get(arg2));
						}
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_AIRCONDITION)){//无线空调
					fragment = new AirConditionControlFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				} else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_ZG_LOCK)){//ZG 锁
					fragment = new  WirelessLockFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				} else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WATCH)){//智能手表
					Intent in = new Intent(getActivity(),GeoCoderActivity.class);
					in.putExtra("productFun",productFun);
					startActivity(in);
				} else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_PLUTO_SOUND_BOX)) {// Pluto音箱
					Intent in = new Intent(getActivity(),PlutoSoundBoxActivity.class);
					in.putExtra("productFun", productFun);
					in.putExtra("funDetail", funDetail);
					startActivity(in);
				} else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)){//五路开关
					fragment = new FiveSwitchFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET)){//无线智能空调插头
					Intent intent=new Intent(getActivity(),WirelessAirConditionOutletActivity.class);
					intent.putExtra("productFun", productFun);
					intent.putExtra("funDetail", funDetail);
					startActivity(intent);
				}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND)){//无线红外转发
					Intent intent=new Intent(getActivity(),InfraredTranspondActivity.class);
					intent.putExtra("productFun", productFun);
					intent.putExtra("funDetail", funDetail);
					startActivity(intent);
				} else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_ONE_SWITCH)){//双路开关
					fragment = new OneSocketFragment();
					if (productFun != null && funDetail != null) {
						Bundle bundle = new Bundle();
						bundle.putSerializable("productFun",productFun);
						bundle.putSerializable("funDetail", funDetail);
						fragment.setArguments(bundle);
						addDialogFragment(fragment);
					}
				}
				
				return true;
			}
		});
		
	}
	
	/**
	 * 添加Fragment
	 * @param Fragment
	 */
	private void addDialogFragment(Fragment fragment){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		//生成DialogFragment对象
        Fragment childFragment = getChildFragmentManager().findFragmentByTag("dialog");
        if (childFragment != null) {
            ft.remove(childFragment);  
        }  
        ft.addToBackStack(null);
        DialogFragment dialogFragment = (DialogFragment) fragment;
        dialogFragment.show(ft, "dialog");
	}

	/**
	 * 同步设备状态
	 */
	private void getStatusFromInternet(){
		ProductStateListTask psTask = new ProductStateListTask(getActivity(),funDetail.getFunType());
		psTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(getActivity(),
						CommDefines.getSkinManager().string(
								R.string.qing_qiu_chu_li_zhong));
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
					ProductFunDaoImpl _pfImpl = new ProductFunDaoImpl(getActivity());
					List<ProductFun> _pfLists = _pfImpl.find();
					List<ProductState> tempList = new ArrayList<ProductState>();
					if (_pfLists != null && _proStateList !=null) {
						for (ProductState _ps : _proStateList) {
							for (ProductFun _pf : _pfLists) {
								if (_ps.getFunId()==_pf.getFunId()) {
									tempList.add(_ps);
								}
							}
						}
					}
					CommonMethod.updateProductStateList(getActivity(), tempList);
					handler.sendEmptyMessage(LOADING);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		psTask.start();
	}
	
	/**
	 * 从缓存获取设备状态列表
	 */
	public void getStates(){
		if (psList != null && psList.size() > 0) {
			psList.clear();
		}
		if (funDetail.getFunType() != null) {
			ProductFunDaoImpl pfdImpl = new ProductFunDaoImpl(getActivity());
			psdImpl = new ProductStateDaoImpl(getActivity());
			psList = new ArrayList<ProductState>();
			List<ProductFun> _pfList = pfdImpl.find(null, "funType=?",
					new String[] { this.funDetail.getFunType() }, null, null,
					null, null);
			List<ProductState> pStates = psdImpl.find();
			if (_pfList != null && pStates != null) {
				for (ProductFun productFun : _pfList) {
					for (ProductState productState : pStates) {
						if (productState.getFunId() == productFun.getFunId()) {
							psList.add(productState);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 初始化数据
	 */
	public void initData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String areaId = getArguments().getString(DeviceActivity.DEVICE_KEY);
				funDetail = (FunDetail) getArguments().get(DeviceActivity.FUNDETAIL);
				if (funDetail == null) {
					return;
				}
				getStates();
				
				if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_DOOR_CONTACT)||
						ProductConstants.FUN_TYPE_INFRARED.equals(funDetail.getFunType()) ||
						ProductConstants.FUN_TYPE_SMOKE_SENSE.equals(funDetail.getFunType()) ||
						ProductConstants.FUN_TYPE_GAS_SENSE.equals(funDetail.getFunType())) {
					getDoorContactStatus();
				}
				
//				openIcon = FileUtil.getImage(funDetail.getPath());
//				Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.ico2));
//				JxshApp.instance.getFinalBitmap().display(viewHolder.imageView, 
//						FileManager.instance().createImageUrl(funDetail.getIcon()), _defaultBitmap,_defaultBitmap);
				// 从缓存（内存缓存和磁盘缓存）中直接获取bitmap
				if (!TextUtils.isEmpty(funDetail.getIcon())) {
					openIcon = JxshApp.instance.getFinalBitmap().getBitmapFromCache(
							FileManager.instance().createImageUrl(funDetail.getIcon()));
				}
				if (openIcon != null) {
					closeIcon = BitmapTools.toGrayscale(openIcon);
				}
				ProductFunDaoImpl impl = new ProductFunDaoImpl(getActivity());
				productFunList = impl.find(null, "funType=? and enable=?", 
						new String[]{funDetail.getFunType(),Integer.toString(1)}, null, null, null, null);
				if (!areaId.equals(DeviceActivity.ALL_DEVICE)) {
					CustomerProductAreaDaoImpl _cpaDaoImpl = new CustomerProductAreaDaoImpl(getActivity());
					List<ProductFun> pfList = new ArrayList<ProductFun>();
					List<CustomerProductArea> _cpaList = _cpaDaoImpl.find(null,
							"areaId=?", new String[]{areaId}, null, null, null, null);
					//根据点击选项areaId获取对应分组下的所有设备
					if (_cpaList != null && _cpaList.size() > 0) {
						for (CustomerProductArea _cpArea : _cpaList) {
							List<ProductFun> tempPfList = impl.find(null, "funId=? and enable=?", 
									new String[]{Integer.toString(_cpArea.getFunId()),Integer.toString(1)}, null, null, null, null);
							if (tempPfList != null && tempPfList.size() > 0) {
								String funType = tempPfList.get(0).getFunType();
								if (funType.equals(funDetail.getFunType())) {//根据funType判断是否为当前选中设备
									pfList.add(tempPfList.get(0));
								}
							}
						}
						if (pfList.size() > 0) {
							productFunList = pfList;
						}else{
							productFunList.clear();
						}
					}
				}
				handler.sendEmptyMessage(LOADING);
			}

			
		}).start();
//		adapter = new DeviceControlAdapter(getActivity(),
//				productFunList, funDetail, psList,bitmap);
//		listView.setAdapter(adapter);
//		getStatusFromInternet();
	}
	
	/**
	 * ListView 单击事件
	 */
	public void itemListener(){
		if (listView == null) {
			return;
		}
		this.listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Logger.debug(null, "---> "+JxshApp.instance.isClinkable);
				if (JxshApp.instance.isClinkable) {
					return;
				}
				
				final int pos = arg2;
				final ProductFun productFun = productFunList.get(arg2);
				final ProductState _pProductState = getProductState(productFun);
				if (productFun == null || funDetail==null) {
					return;
				}
				
				//控制监听
				listener = new TaskListener<ITask>() {
					@Override
					public void onAllSuccess(ITask task, Object[] arg) {
						JxshApp.closeLoading();
						RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo)arg[0];
						if("-1".equals(resultObj.validResultInfo))  return;
						productFun.setOpen(!productFun.isOpen());
						
						if (_pProductState != null) {
							if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT) ||
									funDetail.getFunType().equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO) ||
										funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_SOCKET)) {
								_pProductState.setState(productFun.isOpen() ? "1" : "0");
								psdImpl.update(_pProductState);
								handler.sendEmptyMessage(REFRESH);
							}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN)){
								_pProductState.setState(productFun.isOpen() ? "00" : "01");
								psdImpl.update(_pProductState);
								handler.sendEmptyMessage(REFRESH);
							}else if(ProductConstants.FUN_TYPE_POP_LIGHT.equals(funDetail.getFunType()) ||
									ProductConstants.FUN_TYPE_CRYSTAL_LIGHT.equals(funDetail.getFunType())||
									ProductConstants.FUN_TYPE_CEILING_LIGHT.equals(funDetail.getFunType())|| 
									funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)){
								_pProductState.setState(productFun.isOpen() ? "1" : "0");
								psdImpl.update(_pProductState);
								currId = pos;
								handler.sendEmptyMessage(LOADING);
							}
						}
					}
				};
				
				if (funDetail.getFunType().equals(
						ProductConstants.FUN_TYPE_COLOR_LIGHT)) {// 彩灯单路控制
					ProductFunDaoImpl pfdImpl = new ProductFunDaoImpl(getActivity());
					ArrayList<ProductFun> proFunList = new ArrayList<ProductFun>();
					ProductFun _pf = new ProductFun();
					proFunList = (ArrayList<ProductFun>) pfdImpl.find(null,
							"whId=?", new String[] { productFun.getWhId() },
							null, null, null, null);
					if (proFunList != null && proFunList.size() > 0) {
						_pf = proFunList.get(0);
					}
					
					if (_pProductState != null) {
						if (_pProductState.getState().equals("1")) {
							productFun.setOpen(true);
						} else {
							productFun.setOpen(false);
						}
					}
					
					if (_pf != null) {
						if (productFun.isOpen()) {// 关
							lightColor = "000000000000";
							_pf.setBrightness(0);
						} else {
							if (_pf.getLightColor().equals("")) {
								lightColor = "255255255000";
								_pf.setLightColor(lightColor);
								_pf.setBrightness(255);
							} else {
								lightColor = _pf.getLightColor();
							}
						}
					}

					// ------------------------ Offline ---------------------
					List<byte[]> cmdList = CommonMethod.lightFunToCMD(getActivity(), _pf,
							funDetail, lightColor);
					if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
						String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
						String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
//						OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(
//								getActivity(), cmdList);
						OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
								localHost + ":3333", cmdList, true,false);
						offlineSender.send();
						offlineSender.addListener(listener);
						return;
					}
					// ------------------------ end --------------------------
					if (cmdList == null || cmdList.size() < 1) {
						return;
					}
					byte[] cmd = cmdList.get(0);
					OnlineCmdSenderLong ocsTask = new OnlineCmdSenderLong(getActivity(), cmd);
					ocsTask.addListener(listener);
					ocsTask.send();
//					CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(
//							getActivity(), cmd, false);
//					cdcbsTask.addListener(listener);
//					cdcbsTask.start();
				} else if(ProductConstants.FUN_TYPE_POP_LIGHT.equals(funDetail.getFunType())) {
					operatePopLight(productFun, _pProductState);
				} else if(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT.equals(funDetail.getFunType())) {
					operatePopLight(productFun, _pProductState);
				} else if(ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funDetail.getFunType())) {
					operateWirelessSocket(productFun, _pProductState);
				} else if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_FIVE_SWITCH.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_THREE_SWITCH_THREE.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_THREE_SWITCH_SIX.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_ONE_SWITCH.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_WATCH.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_AIRCONDITION.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_GAS_SENSE.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_DOOR_CONTACT.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_INFRARED.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_SMOKE_SENSE.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_WIRELESS_GATEWAY.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(funDetail.getFunType())||
						ProductConstants.FUN_TYPE_PLUTO_SOUND_BOX.equals(funDetail.getFunType())) {
//					operateWirelessSocket(productFun, _pProductState);
					JxshApp.showToast(getActivity(), "请长按进入详细操作界面");
				} else if(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT.equals(funDetail.getFunType())) {
					operatePopLight(productFun, _pProductState);
				} else if(ProductConstants.FUN_TYPE_CEILING_LIGHT.equals(funDetail.getFunType())) {
					operatePopLight(productFun, _pProductState);
				} else if(ProductConstants.FUN_TYPE_LIGHT_BELT.equals(funDetail.getFunType())){
					operatePopLight(productFun, _pProductState);
				}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funDetail.getFunType())) {
					operateWirelessCurtain(productFun, _pProductState);
				}else {
					//***************离线禁用锁********//
					if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
						if (funDetail.getFunType().equals(
						ProductConstants.FUN_TYPE_AUTO_LOCK)) {
							JxshApp.showToast(getActivity(), CommDefines.getSkinManager().
									string(R.string.li_xian_cao_zuo_error));
							return;
						}
					}
					//*******************************//
					
					if (_pProductState != null) {
						if (_pProductState.getState().equals("1") ||
								"0001".equals(_pProductState.getState())) {
							productFun.setOpen(true);
						} else {
							productFun.setOpen(false);
						}
					}
					List<byte[]> listCmd = CommonMethod.productFunToCMD(getActivity(),
							productFun, funDetail, null);
					if (listCmd == null || listCmd.size() < 1) {
						return;
					}
					byte[] cmd = listCmd.get(0);

					TaskListener<ITask> listener2 = new TaskListener<ITask>() {
						@Override
						public void onAllSuccess(ITask task, Object[] arg) {
							JxshApp.closeLoading();
							productFun.setOpen(!productFun.isOpen());
							
							if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
								if (_pProductState != null) {
									_pProductState.setState(productFun.isOpen() ? "1" : "0");
									psdImpl.update(_pProductState);
										handler.sendEmptyMessage(REFRESH);
								}
								return;
							}
							
							if (funDetail.getFunType()
									.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT) ||
								funDetail.getFunType()
									.equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) {
									if (_pProductState != null) {
										_pProductState.setState(productFun.isOpen() ? "1" : "0");
										psdImpl.update(_pProductState);
										handler.sendEmptyMessage(REFRESH);
									}
							}else if(funDetail.getFunType()
									.equals(ProductConstants.FUN_TYPE_CURTAIN)){
								if (_pProductState != null) {
									_pProductState.setState(productFun.isOpen() ? "0001" : "0002");
									psdImpl.update(_pProductState);
									handler.sendEmptyMessage(REFRESH);
								}
							}
						}
					};
					
					if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
						String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
						String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
//						OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(
//								getActivity(), cmdList);
						OfflineCmdSenderShort offlineSender = new OfflineCmdSenderShort(getActivity(), 
								localHost + ":3333", listCmd, true, false);
						offlineSender.addListener(listener2);
						offlineSender.send();
						return;
					}
					
					OnlineCmdSenderLong ocsTask = new OnlineCmdSenderLong(getActivity(), cmd);
					ocsTask.addListener(listener2);
					ocsTask.send();
//					CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(
//							getActivity(), cmd, false);
//					cdcbsTask.addListener(listener2);
//					cdcbsTask.start();
				}
				//防止多次发送相同指令
				JxshApp.instance.isClinkable = true;
			}
		});
	}
	
	/**
	 * 获取
	 */
	private void getDoorContactStatus() {
		if (funDetail ==  null ) return;
		
		ProductDoorContactListTask pdcTask = new ProductDoorContactListTask(getActivity(),funDetail.getCode());
		pdcTask.addListener(new ITaskListener<ITask>() {

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
					doorContacts  = (List<ProductDoorContact>) arg[0];
					CommonMethod.updateProductDoorContactList(getActivity(), doorContacts);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
			
		});
		pdcTask.start();
	}
	
	/**
	 * 获取有状态返回的设备
	 * @param pf
	 * @return
	 */
	public ProductState getProductState(ProductFun pf) {
		if (pf == null || psList == null) {
			return null;
		}
		ProductState tempState = null;
		for (ProductState _ps : psList) {
			if (pf.getFunId() == _ps.getFunId()) {
				tempState = _ps;
			}
		}
		//如果数据库中没有当前操作设备的状态则新建
		if (tempState == null) {
			tempState = new ProductState(pf.getFunId(), "0");
			psdImpl.insert(tempState, false);
		}
		return tempState;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		handler.sendEmptyMessageDelayed(LOADING, 300);
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroyView() {
		((DeviceActivity) activity).removeListener(DeviceFragment.this.toString());
		if(mShowBroadcastReceiver != null) {
			getActivity().unregisterReceiver(mShowBroadcastReceiver);
		}
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lights_btn:
			Fragment fragment = null;
			lightsId = adapter.getCheckedItem();
			if (lightsId.size() < 1) {
				JxshApp.showToast(
						getActivity(),
						CommDefines.getSkinManager().string(
								R.string.please_select_color_light));
			}else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT)
					|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)
					|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)
					|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)){
				
				if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) {
					fragment = new LightsColorFragment();
				} else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT)
						|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)
						|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)) {
					fragment = new PopLightFragment();
				}
				
				if (productFunList != null) {
					Bundle bundle = new Bundle();
					bundle.putStringArrayList(ControlDefine.KEY_COLOR_LIGHT_LIST, lightsId);
					bundle.putSerializable("funDetail", funDetail);
					fragment.setArguments(bundle);
				}
				
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				Fragment childFragment = getChildFragmentManager().findFragmentByTag("dialog");
				if (childFragment != null) { 
					ft.remove(childFragment);
				}
				ft.addToBackStack(null);
				DialogFragment dialogFragment = (DialogFragment) fragment;
				dialogFragment.show(ft, "dialog");
			}
			break;
		}
	}
	
	// 获取窗帘状态的位置
	int loadIndex = 0;
	
	/**
	 * 获取listview中curtain的状态
	 */
	private void loadCurtainStateInListView() {
		loadIndex = 0;
		loadCurtainState(loadIndex);
	}
	
	/**
	 * 递归获取curtain的状态
	 */
	private void loadCurtainState(final int index) {
		if(index < 0 || productFunList.size() < 1) return;
		
		final ProductFun pf = productFunList.get(index);
		new WirelessCurtainLoader(getActivity(), new WirelessCurtainLoader.OnDataLoadListener() {
			@Override
			public void onDataLoaded(ZigbeeResponse data) {
				String payload = ((ZigbeeResponse)data).getPayload();
				Logger.debug(null, "payload:" + payload);
				if(!TextUtils.isEmpty(payload)) {
					String[] items = payload.split(" ");
					Logger.debug(null, "length:" + items.length);
					if(items.length > 2) {
						String state = items[items.length - 1];
						Logger.debug(null, "state:" + state);
						if(TextUtils.isDigitsOnly(state)) {
							pf.setState(Integer.parseInt(state , 16 ) + "%");
						}else {
							pf.setState("获取失败");
						}
					}
				}
				adapter.notifyDataSetChanged();
				loadIndex += 1;
				if(loadIndex < productFunList.size()) loadCurtainState(loadIndex);
			}
		}).loadData(pf, funDetail); 
	}
	
	/**
	 * 无线彩灯 指令发送
	 * @param productFun
	 * @param productState
	 */
	private void operatePopLight(ProductFun productFun, ProductState productState) {
		if(productFun == null) {
			Logger.error(null, "paramter error.");
			return;
		}
		
		if(productState != null) {
			if (productState.getState().equals("1")) productFun.setOpen(true);
			else  productFun.setOpen(false);
		}
		
		Logger.warn(null, productFun.isOpen() + "");
		
		/* 球泡等命令参数说明： 1.类型(type) close-关闭 / mvToLevel-亮度调节 / hueandsat-设置颜色  
		 * 				 2.参数：light-亮度 / dstColor-设备目标地址 / mode-类型1【彩灯】2【白灯】/ time-时间 / src-设备源地址 / dst-设备目标地址
		 * */
		String type = null;
		Map<String, Object> params = new HashMap<String, Object>();
		if (productFun.isOpen()) {
			/* 操作类型(关灯) */
			type = "mvToLevel";
			/* 亮度 */
			params.put("light",  StringUtils.integerToHexString(0));
			/* 关白灯 */
			params.put("mode", StringUtils.integerToHexString(2));
			/* 关彩灯 */
			params.put("dst", "0x01");
		} else {
			/* 操作类型(调节亮度 )*/
			type = "mvToLevel";
			/* 亮度 */
			params.put("light",  StringUtils.integerToHexString(100));
			/* 设备开的目标（彩灯）*/
			params.put("dst", StringUtils.integerToHexString(1));
			/* 设备关的目标（白灯）*/
			params.put("dstColor", "0x02");
			/* 亮度关*/
			params.put("lightColor", StringUtils.integerToHexString(0));
			/* 关的时间 */
			params.put("timeColor", StringUtils.integerToHexString(1));
		}
		/* 操作时间  */
		params.put("time",  StringUtils.integerToHexString(1));
		/* 设备目标地址  */
		params.put("src", "0x01");
		
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
//			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
			//通过MAC去匹配对应网关IP
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			if(localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(getActivity(), "网关离线", Toast.LENGTH_SHORT).show();
				return;
			}
			System.out.println("localHost:"+localHost);
			System.out.println("type:" + type);
			System.out.println("params:" + params.toString());
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdList, true, false);
			offlineSender.addListener(listener);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), DatanAgentConnectResource.HOST_ZEGBING, 
					cmdList, true);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	/**
	 * 无线窗帘指令发送
	 * @param productFun
	 * @param productState
	 */
	private void operateWirelessCurtain(ProductFun productFun, ProductState productState) {
		if(productFun == null ) return;
		Logger.debug("DeviceFragment", "无线窗帘命令？");
		String type = productFun.isOpen() ? "down" : "open";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("src", "0x01");
		params.put("dst", StringUtils.integerToHexString(Integer.parseInt(productFun.getFunUnit())));
		
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
//			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
			//通过MAC去匹配对应网关IP
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			if(localHost == null || "".equals(localHost)) {
				Toast.makeText(getActivity(), "网关离线", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdList, true, false);
			offlineSender.addListener(listener);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), 
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 0,false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	/**
	 * 无线插座指令发送
	 * @param productFun
	 * @param productState
	 */
	private void operateWirelessSocket(ProductFun productFun, ProductState productState) {
		if(productFun == null ) return;
		
		if(productState != null) {
			if (productState.getState().equals("1")) productFun.setOpen(true);
			else  productFun.setOpen(false);
		}
		String type = productFun.isOpen() ? "off" : "on";
		
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, null, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdList, true,false);
			offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, null, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 1,false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	public void refresh() {
		handler.sendEmptyMessage(REFRESH);
	}
	
	@Override
	public void onDetach() {
	    super.onDetach();

	    try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	
}
