package com.jinxin.jxsmarthome.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.command.InfraredTranspondTask;
import com.jinxin.datan.net.command.VoiceIdentifyTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.EnvironmentActivity;
import com.jinxin.jxsmarthome.activity.GeoCoderActivity;
import com.jinxin.jxsmarthome.activity.MainMusicActivity;
import com.jinxin.jxsmarthome.activity.RemoteControlActivity;
import com.jinxin.jxsmarthome.activity.VoiceHelperActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OfflineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.device.GestureIndentifyManager;
import com.jinxin.jxsmarthome.cmd.device.OpenVoiceIndentifyManager;
import com.jinxin.jxsmarthome.cmd.device.Text2VoiceManager;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.BaiduMusic;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.entity.WeatherInfo;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.HomeDeviceGridAdapter;
import com.jinxin.jxsmarthome.ui.adapter.HomePatternGridAdapter;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.ui.widget.ExtendAnimatePathMenu;
import com.jinxin.jxsmarthome.ui.widget.ExtendAnimatePathMenu.OnButtonClickListener;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

/**
 * 主页面
 * @author TangLong
 * @company 金鑫智慧
 */
public class HomeFragment extends Fragment implements OnClickListener, OnItemClickListener, OnButtonClickListener {
	private final static int GET_DATA_SUCCESS = 1;
	private final static int OPERATE_SUCCESS = 2;
	private final int OPERATE_REFRESH = 3;
	private final static int GET_WEATHERINFO_SUCCESS = 4;
	private static String TAG = "HomeFragment";
	private ImageView mLights;
	private ImageView mLock;
	private ImageView mSecurity;
	private ImageView mSocket;
	private GridView mCommDeviceGridView;
	private GridView mCommPatternGridView;
	private HomeDeviceGridAdapter mCommDeviceGridAdapter;
	private HomePatternGridAdapter mCommPatternGridAdapter;
	private List<ProductFun> mCommDeviceGridData;
	private List<CustomerPattern> mCommPatternGridData;
	private String lightColor = "";
	private ProductState currentPS;
//	private ViewflipperAD mAdView;
	private ExtendAnimatePathMenu mPathMenu;
	
	private String textToSpeakBefore;
	private String textToSpeakEnd;
	
	/***百度定位用的****/
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	// 天气详情用到的控件
	private FrameLayout fl_weather_bg;
	private TextView tv_location_city;
	private TextView tv_weather;
	private TextView tv_wendu;
	private ImageView iv_weather_icon;
	private TextView tv_city_name;
	private TextView tv_wind;
	private TextView tv_real_time_temperature;
	private static String curr_city_name = null;
	private String location_city_name = null;
	private int currentHour = 0;
	private int currentMinute = 0;
	private View rootView;//缓存Fragment view  
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case GET_DATA_SUCCESS:
				updateHomeGridView();
				break;
			case OPERATE_SUCCESS:
				updateDeviceGrid();
				break;
			case OPERATE_REFRESH:
				updateLightInfo(getDeviceToggleInfo(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT));
		        updateDoorInfo(getDeviceToggleInfo(ProductConstants.FUN_TYPE_AUTO_LOCK));
		        updateSecurityInfo(getDeviceToggleInfo(ProductConstants.FUN_TYPE_SECUTIRY));
		        updateSocketInfo(getDeviceToggleInfo(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO));
		        break;
			case GET_WEATHERINFO_SUCCESS:
				List<WeatherInfo> weatherInfoList = (List<WeatherInfo>) msg.obj;
				if(weatherInfoList != null && weatherInfoList.size() > 0){
					updateWeatherInfoView(weatherInfoList.get(0));
				}
				break;
			}
			return true;
		}

	});
	
	/* 自定义广播，用于接受推送信息的改变 */
	private BroadcastReceiver mShowBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BroadcastManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
	            updateLightInfo(getDeviceToggleInfo(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT));
	            updateDoorInfo(getDeviceToggleInfo(ProductConstants.FUN_TYPE_AUTO_LOCK));
	            updateSecurityInfo(getDeviceToggleInfo(ProductConstants.FUN_TYPE_SECUTIRY));
	            updateSocketInfo(getDeviceToggleInfo(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO));
			}
		}
	};
	
	private TaskListener<ITask> taskListener = new TaskListener<ITask>() {
		public void onSuccess(ITask task, Object[] arg) {
			JxshApp.closeLoading();
			mHandler.sendEmptyMessage(OPERATE_SUCCESS);
		};
		public void onFail(ITask task, Object[] arg) {
			JxshApp.closeLoading();
			if (arg[0] != null) {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				JxshApp.showToast(getActivity(), resultObj.validResultInfo);
			} else {
				JxshApp.showToast(getActivity(), "操作失败");
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParam();
		JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
				.string(R.string.wake_app_tips));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView==null){  
			rootView = inflater.inflate(R.layout.fragment_home, container, false);	
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);  
	       }   
		initView(rootView);
		initData();
		return rootView;
	}
	
	@Override
	public void onResume() {
//		mAdView.startAutoLoop();
		super.onResume();
//		mHandler.sendEmptyMessage(OPERATE_REFRESH);
		mHandler.sendEmptyMessageDelayed(OPERATE_REFRESH, 2000);
		 
	}
	
	// 更新天气信息
	private void updateWeatherInfoView(WeatherInfo weather) {
		if(currentHour > 8 && currentHour < 19 ){ // 白天
			String dayPictureUrl = weather.getDayPictureUrl();
			if(!TextUtils.isEmpty(dayPictureUrl)){
				String dayPicture = dayPictureUrl.substring(dayPictureUrl.lastIndexOf("/")+1);
				iv_weather_icon.setImageBitmap(CommDefines.getSkinManager()
						.getAssetsBitmap("images/weather/day/"+dayPicture));
			}
		}else{ // 晚上
			String nightPictureUrl = weather.getNightPictureUrl();
			if(!TextUtils.isEmpty(nightPictureUrl)){
				String nightPicture = nightPictureUrl.substring(nightPictureUrl.lastIndexOf("/")+1);
				iv_weather_icon.setImageBitmap(CommDefines.getSkinManager()
						.getAssetsBitmap("images/weather/night/"+nightPicture));
			}
		}
		
		
		tv_weather.setText(weather.getWeather());
		tv_wendu.setText(weather.getTemperature());
		tv_city_name.setText(curr_city_name);
		tv_wind.setText(weather.getWind());
		// 周二 06月21日 (实时：29℃)
		String date = weather.getDate();
		date = date.substring(date.length()-4,date.length());
		String date2 = date.substring(0, date.length() - 1);
		tv_real_time_temperature.setText(date2);
	}
	
	@Override
	public void onPause() {
//		mAdView.stopAutoLoop();
		super.onPause();
	}

	@Override
	public void onDestroyView() {
//		mAdView.stopAutoLoop();
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		if(mShowBroadcastReceiver != null) {
			getActivity().unregisterReceiver(mShowBroadcastReceiver);
		}
		if (mLocationClient != null)
			mLocationClient.stop();// 停止定位
		super.onDestroy();
	}
	
	private void initParam() {
		mCommDeviceGridData = new ArrayList<ProductFun>();
		mCommPatternGridData = new ArrayList<CustomerPattern>();
		// 获取首页设备开关状态
		getActivity().registerReceiver(mShowBroadcastReceiver, new IntentFilter(BroadcastManager.MESSAGE_RECEIVED_ACTION));
		
	}
	
	private void initView(View view) {
		mLights = (ImageView)view.findViewById(R.id.home_ib_lights);
		mLock = (ImageView)view.findViewById(R.id.home_ib_door);
		mSecurity = (ImageView)view.findViewById(R.id.home_ib_security);
		mSocket = (ImageView)view.findViewById(R.id.home_ib_socket);
		mCommDeviceGridView = (GridView)view.findViewById(R.id.home_common_device_grid);
		mCommPatternGridView = (GridView)view.findViewById(R.id.home_common_pattern_grid);
//		mAdView = (ViewflipperAD)view.findViewById(R.id.home_viewflipperAD1);
		mPathMenu = (ExtendAnimatePathMenu) view.findViewById(R.id.home_path_menu);
		
		tv_location_city = (TextView)view.findViewById(R.id.tv_location_city);
		tv_weather = (TextView)view.findViewById(R.id.tv_weather);
		tv_wendu = (TextView)view.findViewById(R.id.tv_wendu);
		iv_weather_icon = (ImageView)view.findViewById(R.id.iv_weather_icon);
		tv_city_name = (TextView)view.findViewById(R.id.tv_city_name);
		tv_wind = (TextView)view.findViewById(R.id.tv_wind);
		tv_real_time_temperature = (TextView)view.findViewById(R.id.tv_real_time_temperature);
		fl_weather_bg = (FrameLayout)view.findViewById(R.id.fl_weather_bg);
		Calendar calendar = Calendar.getInstance();
	    currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		currentMinute = calendar.get(Calendar.MINUTE);
		if(currentHour > 8 && currentHour < 19 ){ // 白天
			fl_weather_bg.setBackgroundResource(R.drawable.day_weather_background);
		}else{ // 晚上
			fl_weather_bg.setBackgroundResource(R.drawable.night_weather_background);
		}
		
		mCommDeviceGridAdapter = new HomeDeviceGridAdapter(getActivity(), 
				mCommDeviceGridData, mCommDeviceGridView);
		mCommPatternGridAdapter = new HomePatternGridAdapter(getActivity(), 
				mCommPatternGridData, mCommPatternGridView);
		
		mCommDeviceGridView.setAdapter(mCommDeviceGridAdapter);
		mCommPatternGridView.setAdapter(mCommPatternGridAdapter);
		
		mLights.setOnClickListener(this);
		mLock.setOnClickListener(this);
		mSecurity.setOnClickListener(this);
		mCommDeviceGridView.setOnItemClickListener(this);
		mCommPatternGridView.setOnItemClickListener(this);
		mPathMenu.setOnButtonClickListener(this);
		tv_location_city.setOnClickListener(this);
	}
	
	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mCommDeviceGridData = getDevicesMostInUse();
				mCommPatternGridData = getPatternsMostInUse();
				mCommDeviceGridAdapter.bindData(mCommDeviceGridData);
				mCommPatternGridAdapter.bindData(mCommPatternGridData);
				mHandler.sendEmptyMessage(GET_DATA_SUCCESS);
			}
		}).start();
		
		// 初始化百度地图，注册事件监听
		mLocationClient = new LocationClient(getActivity());
		mLocationClient.registerLocationListener(myListener);
        setLocationOption();//设定定位参数
		mLocationClient.start();//开始定位
		if (mLocationClient != null && mLocationClient.isStarted()){
			mLocationClient.requestLocation();
		}else{
			Log.d("msg", "locClient is null or not started");
		}
		
		ProductFunDaoImpl funDaoImpl=new ProductFunDaoImpl(getActivity());
		List<ProductFun> _pfList=funDaoImpl.find(null, "funType=?",
				new String[] { "032" }, null,
				null, null, null);
		for (int m = 0; m < _pfList.size(); m++) {
			ProductFun productFun=_pfList.get(m);
			if (productFun.getFunType().equals("032")) {
				InfraredTranspondTask upgradeTask = new InfraredTranspondTask(null,productFun.getFunId());
				upgradeTask.addListener(new ITaskListener<ITask>() {

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
//								changeLoadingStatus(true,R.string.hong_wai_xue_xi_ji_lu);
						List<WHproductUnfrared> wHproductUnfrareds=(List<WHproductUnfrared>)arg[0];
						CommonMethod.updateWHproductUnfraredList(
								getActivity(),
								wHproductUnfrareds);
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
					}
					
				});
				upgradeTask.start();
			}
		}
	}
	
	//设置相关参数
		private void setLocationOption(){
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);
			option.setAddrType("all"); // 返回的定位结果包含地址信息
			option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
			option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
			 
//			option.disableCache(true); // 禁止启用缓存定位
//			option.setPoiNumber(5);    // 最多返回POI个数   
//			option.setPoiDistance(1000);  // poi查询距离        
//			option.setPoiExtraInfo(true);  // 是否需要POI的电话和地址等详细信息        
			mLocationClient.setLocOption(option);
			
		} 

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_location_city:
			String curr_city = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.CURR_CITY, "");
			if(!TextUtils.isEmpty(curr_city) && tv_location_city.getText().equals("归属地位置")){
				tv_location_city.setText("实时位置");
				curr_city_name= curr_city;
				getWeatherTask(curr_city);
			}else if(tv_location_city.getText().equals("实时位置")){
				tv_location_city.setText("归属地位置");
				curr_city_name= location_city_name;
				getWeatherTask(curr_city_name);
			}
			
			 break;

		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (JxshApp.instance.isClinkable) {
			return;
		}
		
		if(parent.getId() == R.id.home_common_device_grid) {
			ProductFun pf = mCommDeviceGridData.get(position);
			FunDetail fd = AppUtil.getFunDetailByFunType(getActivity(), pf.getFunType());
			ProductState ps = AppUtil.getProductStateByFunId(getActivity(), pf.getFunId());
			if(ps == null) {
				ps = new ProductState(pf.getFunId(),"0");
				ProductStateDaoImpl psDaoImpl = new ProductStateDaoImpl(getActivity());
				psDaoImpl.insert(ps);
				pf.setOpen(false);
			}
			currentPS = ps;
			operateDeviceDirectly(pf, fd, ps);
		}else if(parent.getId() == R.id.home_common_pattern_grid) {
			CustomerPattern cp = mCommPatternGridData.get(position);
			operatePatternDirectly(cp);
			//防止多次发送相同指令
			JxshApp.instance.isClinkable = true;
		}
	}
	
	@Override
	public void onButtonClick(View view, int selectedItem) {
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
					.string(R.string.li_xian_cao_zuo_tips));
			return;
		}
		switch (selectedItem) {
		case 0:
			openActivity(VoiceHelperActivity.class);
//			if(AppUtil.getProductFunByFunType(getActivity(), 
//					ProductConstants.FUN_TYPE_POWER_AMPLIFIER).size() > 0) {
//				openActivity(VoiceHelperActivity.class);
//			}else {
//				JxshApp.showToast(getActivity(), "无可用的音乐设备!");
//			}
			break;
		case 1:
			if(AppUtil.getProductFunByFunType(getActivity(), 
					ProductConstants.FUN_TYPE_POWER_AMPLIFIER).size() > 0) {
				openActivity(MainMusicActivity.class);
			}else {
				JxshApp.showToast(getActivity(), "无可用的音乐设备!");
			}
			break;
		case 2:
			if(AppUtil.getProductFunByFunType(getActivity(), 
					ProductConstants.FUN_TYPE_UFO1).size() > 0) {
				openActivity(RemoteControlActivity.class);
			}else {
				JxshApp.showToast(getActivity(), "无可用的飞碟设备!");
			}
			break;
		case 3:
			if(AppUtil.getProductFunByFunType(getActivity(), 
					ProductConstants.FUN_TYPE_ENV).size() > 0) {
				openActivity(EnvironmentActivity.class);
			}else {
				JxshApp.showToast(getActivity(), "无可用的环境监测设备!");
			}
			break;
		case 4:
			if(AppUtil.getProductFunByFunType(getActivity(), 
					ProductConstants.FUN_TYPE_UFO1).size() > 0) {
				showSendVoiceDialog();
			}else {
				JxshApp.showToast(getActivity(), "无可用的飞碟设备!");
			}
			break;
		}
	}
	
	/**
	 * 直接操作设备
	 */
	private void operateDeviceDirectly(final ProductFun productFun, FunDetail funDetail, ProductState ps) {
		if(productFun == null || funDetail == null) {
			Toast.makeText(getActivity(), R.string.home_error_device, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (JxshApp.instance.isClinkable) {
			return;
		}
		
		if (ProductConstants.FUN_TYPE_COLOR_LIGHT.equals(
				funDetail.getFunType())) {// 彩灯控制
			// 彩灯单路控制
			ProductFunDaoImpl pfdImpl = new ProductFunDaoImpl(getActivity());
			ArrayList<ProductFun> proFunList = new ArrayList<ProductFun>();
			ProductFun _pf = new ProductFun();
			proFunList = (ArrayList<ProductFun>) pfdImpl.find(null,
					"whId=?", new String[] { productFun.getWhId() },
					null, null, null, null);
			if (proFunList != null && proFunList.size() > 0) {
				_pf = proFunList.get(0);
			}
			
			if (ps != null) {
				if ("1".equals(ps.getState()) || "0001".equals(ps.getState())) {
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
				OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(
						getActivity(), cmdList);
				offlineSender.send();
				return;
			}
			// ------------------------ end --------------------------
			if (cmdList == null || cmdList.size() < 1) {
				return;
			}
			OnlineCmdSenderLong onlineCmdSender = new OnlineCmdSenderLong(getActivity(), cmdList);
			onlineCmdSender.addListener(taskListener);
			onlineCmdSender.send();
		
		} else if(ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(
				funDetail.getFunType())) {//无线插座
			operateWirelessSocket(productFun,ps,funDetail);
		} else if(ProductConstants.FUN_TYPE_ONE_SWITCH.equals(
				funDetail.getFunType())) {//单路
			operateOneSocket(productFun,ps,funDetail);
		}  else if(ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(
				funDetail.getFunType())) {//智能插座
			operateWirelessOutlet(productFun,ps,funDetail);
		} else if(ProductConstants.FUN_TYPE_POP_LIGHT.equals(funDetail.getFunType())
				|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)
				|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)
				|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)) {//球泡灯
			operatePopLight(productFun,ps,funDetail);
		} else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funDetail.getFunType())){
			operateWirelessCurtain(productFun,funDetail);
		}else {
			
			if(!canBeUse(productFun.getFunType())) {
				Toast.makeText(getActivity(), R.string.li_xian_cao_zuo_error, Toast.LENGTH_SHORT).show();
				return;
			} 
			
			if (ps != null) {
				if ("1".equals(ps.getState()) || "0001".equals(ps.getState())) {
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

			// offline 
			if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
				OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(
						getActivity(), listCmd);
				offlineSender.addListener(taskListener);
				offlineSender.send();
			
			// online
			}else {
				OnlineCmdSenderLong onlineCmdSender = new OnlineCmdSenderLong(getActivity(), listCmd);
				onlineCmdSender.addListener(taskListener);
				onlineCmdSender.send();
			}
		}
		
		//防止多次发送相同指令
		JxshApp.instance.isClinkable = true;
	}
	
	private void operatePopLight(ProductFun productFun, ProductState productState, FunDetail funDetail) {
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
			type = "close";
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
			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdList, true,false);
			offlineSender.addListener(taskListener);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), DatanAgentConnectResource.HOST_ZEGBING, 
					true, cmdList, true, 1,false);
			onlineSender.addListener(taskListener);
			onlineSender.send();
		}
	}
	
	/**
	 * 无线插座指令发送
	 * @param productFun
	 * @param productState
	 * @param funDetail
	 */
	private void operateWirelessSocket(ProductFun productFun, ProductState productState, FunDetail funDetail) {
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
			offlineSender.addListener(taskListener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, null, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 1,false);
			onlineSender.addListener(taskListener);
			onlineSender.send();
		}
	}
	
	private void operateOneSocket(ProductFun productFun,ProductState productState, FunDetail funDetail){
		if(productFun == null ) return;
		
		HashMap<String,Object> params = new HashMap<String, Object>();
		params.put("src", "0x01");
		params.put("dst", "0x01");
		
		if(productState != null) {
			if (productState.getState().equals("01")) productFun.setOpen(true);
			else  productFun.setOpen(false);
		}
		String type = productFun.isOpen() ? "off" : "on";
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			if (localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(getActivity(), "未找到对应网关", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), localHost + ":3333", cmdList,
					true, false);
			offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 0, false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	private void operateWirelessOutlet(ProductFun productFun,ProductState productState, FunDetail funDetail){
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("src", "0x01");
		map.put("dst", "0x01");
		
		if(productState != null) {
			if (productState.getState().equals("01")) productFun.setOpen(true);
			else  productFun.setOpen(false);
		}
		String type = productFun.isOpen() ? "off" : "on";
		
		List<byte[]> cmdAll = new ArrayList<byte[]>();
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			if(localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(getActivity(), "网关离线", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, map, type);
			cmdAll.addAll(cmdList);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdAll, true, false);
			offlineSender.addListener(taskListener);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, map, type);
			cmdAll.addAll(cmdList);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), 
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdAll, true, 0, false);
			onlineSender.addListener(taskListener);
			onlineSender.send();
		}
	}
	
	/**
	 * 无线窗帘指令发送
	 * @param productFun
	 * @param productState
	 */
	private void operateWirelessCurtain(ProductFun productFun, FunDetail funDetail) {
		if(productFun == null ) return;
		
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
			offlineSender.addListener(taskListener);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), 
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 0,false);
			onlineSender.addListener(taskListener);
			onlineSender.send();
		}
	}
	
	/**
	 * 模式控制
	 */
	private void operatePatternDirectly(final CustomerPattern cp) {
		
		ProductPatternOperationDaoImpl _ppoDaoImpl = new ProductPatternOperationDaoImpl(
				getActivity());
		final List<ProductPatternOperation> ppsList = _ppoDaoImpl.find(
				null,
				"patternId=?",
				new String[] { Integer.toString(cp.getPatternId()) }, 
				null, null, null, null);
		
		textToSpeakBefore = ttsTextToString(cp.getTtsStart().replaceAll("；；", ";;"));
		textToSpeakEnd = ttsTextToString(cp.getTtsEnd().replaceAll("；；", ";;"));
		
		if (TextUtils.isEmpty(textToSpeakBefore) || NetworkModeSwitcher.useOfflineMode(getActivity()) ) {
			operatePattern(ppsList);
		}else{
			setVoiceIdentifySwitch(textToSpeakBefore, ppsList);//发送模式开始语音
		}
		
	}
	
	/**
	 * 发送指令
	 * @param ppsList
	 */
	TaskListener<ITask> listener;
	private void operatePattern(final List<ProductPatternOperation> ppsList) {
		
		listener = new TaskListener<ITask>(){

			@Override
			public void onAllSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "mode onAllSuccess");
				if (TextUtils.isEmpty(textToSpeakEnd) || NetworkModeSwitcher.useOfflineMode(getActivity())) {
					return;
				}
				setVoiceIdentifySwitch(textToSpeakEnd, null);//发送模式结束语音
			}
			
		};
		// offline
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			Logger.debug(null, "offline operation pattern");
			AsyncExecutor asyncExecutor = new AsyncExecutor();
			asyncExecutor.execute(new AsyncExecutor.AsyncTask<List<Command>>() {
				@Override
				public List<Command> doInBackground() {
					OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
					List<Command> listCmd = cmdGenerator.generateProductPatternOperationCommand(getActivity(), ppsList, null);
					return listCmd;
				}
				public void onPostExecute(List<Command> data) {
					OfflineMulitGatewayCmdSender cmdSender = new OfflineMulitGatewayCmdSender(getActivity(), data);
					cmdSender.send();
				};
			});
		// online
		}else {
			AsyncExecutor asyncExecutor = new AsyncExecutor();
			asyncExecutor.execute(new AsyncExecutor.AsyncTask<List<Command>>() {
				@Override
				public List<Command> doInBackground() {
					OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
					List<Command> listCmd = cmdGenerator.generateProductPatternOperationCommand(getActivity(), ppsList, null);
					return listCmd;
				}
				public void onPostExecute(List<Command> data) {
					OnlineMulitGatewayCmdSender cmdSender = new OnlineMulitGatewayCmdSender(getActivity(), data);
					cmdSender.addListener(listener);
					cmdSender.send();
				};
			});
		}
	}
	
	/**
	 * 发送文本，通过语音播放
	 * @param sendText 发送文本
	 */
	private void sendVoiceCmd(String sendText, final List<ProductPatternOperation> _ppoList) {
		TaskListener<ITask> _listener = new TaskListener<ITask>(){
			
			@Override
			public void onFinish() {
				Logger.debug(null, "voice onFinish");
				if (_ppoList != null && !JxshApp.instance.isClinkable) {
					operatePattern(_ppoList);
					//防止多次发送相同指令
					JxshApp.instance.isClinkable = true;
				}
			}

		};
		ProductFun productFun = AppUtil.getSingleProductFunByFunType(getActivity(),
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		FunDetail funDetail = AppUtil.getFunDetailByFunType(getActivity(),
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		
		if (productFun == null || funDetail == null || sendText == null) {
			Logger.warn(null, "paramter error, cancel send voice");
			return;
		}
		
		String[] inputAndAddr = getInput4Voice();
		String input = inputAndAddr[0] == null ? "input3" : inputAndAddr[0];
		String addr = TextUtils.isEmpty(inputAndAddr[1]) ? "" : inputAndAddr[1];
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_TEXT, sendText);
		params.put(StaticConstant.OPERATE_INPUT_SET, input);
		params.put(StaticConstant.PARAM_ADDR, addr);
		
		//productFun表中午对应网关设备，此处将FunType设置为网关设备
		productFun.setFunType(ProductConstants.FUN_TYPE_GATEWAY);
		
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun,
				funDetail, params);
			return;
		}
		
		List<byte[]> cmds = CommonMethod.productFunToCMD(getActivity(), productFun,
				funDetail, params);
		
		if (cmds == null || cmds.size() < 1) return;
		
		OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(getActivity(), cmds);
		cmdSender.addListener(_listener);
		cmdSender.send();
	}
	
	/**
	 * 发送语音前的准备
	 */
	private void setVoiceIdentifySwitch(final String txtToSpeech, final List<ProductPatternOperation> _ppoList) {
		String amplifierWhId = getAmplifierWhId();
		
		if(amplifierWhId != null) {
			ProductRegister productRegister = AppUtil.getProductRegisterByWhId(getActivity(), amplifierWhId);
			if(productRegister != null) {
				VoiceIdentifyTask task = new VoiceIdentifyTask(getActivity(), "02", productRegister.getGatewayWhId());
				task.addListener(new TaskListener<ITask>() {
					@Override
					public void onSuccess(ITask task, Object[] arg) {
						super.onSuccess(task, arg);
						sendVoiceCmd(txtToSpeech, _ppoList);
					}
					@Override
					public void onFail(ITask task, Object[] arg) {
						super.onFail(task, arg);
						sendVoiceCmd(txtToSpeech, _ppoList);
					}
				});
				task.start();
			}
		}
	}
	
	/**
	 * 得到最常用的设备
	 */
	private List<ProductFun> getDevicesMostInUse() {
		ProductFunDaoImpl pfDao = new ProductFunDaoImpl(getActivity());
		List<ProductFun> list = pfDao.find(null, "enable=?", new String[]{Integer.toString(1)}, null, null, "beHomepage DESC", "0,4");
		if(list == null || list.size() < 1) {
			list = pfDao.find(null, "funType=?", new String[]{ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT}, 
					null, null, null, "0,1");
		}
		return list;
	}
	
	/**
	 * 得到最常用的模式
	 */
	private List<CustomerPattern> getPatternsMostInUse() {
		CustomerPatternDaoImpl cpDao = new CustomerPatternDaoImpl(getActivity());
		return cpDao.find(null, null, null, null, null, "stayTop DESC, clickCount DESC", "0,4");
	}

	/**
	 * 更新grideview
	 */
	private void updateHomeGridView() {
		if(mCommDeviceGridData != null && mCommDeviceGridData.size() > 0) {
			mCommDeviceGridAdapter.notifyDataSetChanged();
		}
		if(mCommPatternGridData != null && mCommPatternGridData.size() > 0) {
			mCommPatternGridAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 操作成功后，对常用设备的状态做改变
	 */
	private void updateDeviceGrid() {
		if(currentPS != null) {
			ProductFun productFun = AppUtil.getSingleProductFunByFunId(getActivity(), String.valueOf(currentPS.getFunId()));
			if(productFun != null) {
				if(ProductConstants.FUN_TYPE_CURTAIN.equals(productFun.getFunType())) {
					if("0001".equals(currentPS.getState())) {
						currentPS.setState("0002");
					}else if("0002".equals(currentPS.getState())) {
						currentPS.setState("0001");
					}
				}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(productFun.getFunType())){
					if ("0".equals(currentPS.getState())||"00".equals(currentPS.getState())) {
						currentPS.setState("100");
					}else{
						currentPS.setState("0");
					}
				}else if(ProductConstants.FUN_TYPE_ONE_SWITCH.equals(productFun.getFunType())||
						ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(productFun.getFunType())){
					if ("01".equals(currentPS.getState())) {
						currentPS.setState("00");
					}else{
						currentPS.setState("01");
					}
				}else {
					if("1".equals(currentPS.getState())) {
						currentPS.setState("0");
					}else {
						currentPS.setState("1");
					}
				}
				ProductStateDaoImpl psDaoImpl = new ProductStateDaoImpl(getActivity());
				psDaoImpl.update(currentPS);
			}
			
			productFun.setOpen(!productFun.isOpen());

		}
		mCommDeviceGridAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 设备在离线模式下是否可用
	 */
	private boolean canBeUse(String funType) {
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			if(ProductConstants.FUN_TYPE_AUTO_LOCK.equals(funType)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取推送后设备的开关状态信息 
	 * @param funType	
	 * 			设备的funType
	 * @return 开关状态
	 */
	private boolean getDeviceToggleInfo(String funType) {
		boolean result = false;
		ProductFunDaoImpl productFunDao = new ProductFunDaoImpl(getActivity());
		ProductStateDaoImpl psdImpl = new ProductStateDaoImpl(getActivity());
		List<ProductFun> list = productFunDao.find(null, "funType=?", new String[] {funType}, null, null, null, null);
		if(list != null && list.size() > 0) {
			for (ProductFun productFun : list) {
				List<ProductState> psList = psdImpl.find(null, "funId=?", new String[]{productFun.getFunId()+""}, null, null, null, null);
				if (psList!=null&&psList.size()>0) {
					if (psList.get(0).getState().equals("1")) {
						return true;
					}
				}
			}
		}
		return result;
	}
	
	/* 更新推送后的灯的状态信息 */
	private void updateLightInfo(boolean boo) {
		if (mLights == null) return;
		
		if (boo)
			mLights.setImageResource(R.drawable.icon_dengguang_hover);
		else
			mLights.setImageResource(R.drawable.icon_dengguang);

	}
	
	/* 更新推送后的灯的状态信息 */
	private void updateSocketInfo(boolean boo) {
		if (mSocket == null) return;
		
		if (boo)
			mSocket.setImageResource(R.drawable.icon_chazuo1);
		else
			mSocket.setImageResource(R.drawable.icon_chazuo2);

	}
	
	/* 更新推送后的门禁的状态信息 */
	private void updateDoorInfo(boolean boo) {
		if (mLock == null) return;
		
		if(boo) {
			mLock.setImageResource(R.drawable.icon_menjing_hover);
		} else {
			mLock.setImageResource(R.drawable.icon_menjing);
		}
	}
	
	/* 更新推送后的安防的状态信息 */
	private void updateSecurityInfo(boolean boo) {
		if (mSecurity == null) return;
		
		if(boo) {
			mSecurity.setImageResource(R.drawable.icon_anfang_hover);
		} else {
			mSecurity.setImageResource(R.drawable.icon_anfang);
		}
	}
	
	/**
	 * 显示发送文本转化为语音的窗口
	 */
	private void showSendTextToVoiceDialog() {
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			Toast.makeText(getActivity(), "离线时此功能不可用", Toast.LENGTH_SHORT).show();;
			return;
		}
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.custom_dialog_send_voice, new LinearLayout(getActivity()), false);
		final EditText mSendText = (EditText)dialogView.findViewById(R.id.send_voice_text);
		Button mCancel = (Button)dialogView.findViewById(R.id.send_voice_cancel);
		Button mSend = (Button)dialogView.findViewById(R.id.send_voice_send);
		final Dialog dialog = BottomDialogHelper.showDialogInBottom(getActivity(), dialogView, null);
		mCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		mSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sendText = mSendText.getText().toString();
				if(sendText != null && sendText.length() > 0) {
					sendText = sendText.length() > 140 ? sendText.substring(0, 140) : sendText;
					Text2VoiceManager manager = new Text2VoiceManager(getActivity());
					manager.switchAndSend(mSendText.getText().toString());
					dialog.cancel();
				}
			}
		});
	}
	
	private void openActivity(Class<?> clazz) {
		startActivity(new Intent(getActivity(), clazz));
	}
	
	/**
	 * 显示发送语音窗口
	 */
	private void showSendVoiceDialog() {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.custom_dialog_open_voice, new LinearLayout(getActivity()), false);
		Button mCancel = (Button)dialogView.findViewById(R.id.send_voice_cancel);
		Button mOpen = (Button)dialogView.findViewById(R.id.send_voice_open);
		Button mGOpen = (Button)dialogView.findViewById(R.id.send_gesture_open);
		Button mGClose = (Button)dialogView.findViewById(R.id.send_gesture_close);
		final Dialog dialog = BottomDialogHelper.showDialogInBottom(getActivity(), dialogView, null);
		mCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		mOpen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendVoiceOpenCmd();
			}
		});
		mGOpen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GestureIndentifyManager(getActivity()).switchAndSend(StaticConstant.OPERATE_OPEN_HAND);
				dialog.cancel();
			}
		});
		mGClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GestureIndentifyManager(getActivity()).switchAndSend(StaticConstant.OPERATE_CLOSE_HAND);
				dialog.cancel();
			}
		});
	}
	
	/**
	 * 打开智能语音
	 */
	private void sendVoiceOpenCmd() {
		new OpenVoiceIndentifyManager(getActivity()).switchAndSend();
	}
	
	/**
	 * 获取功放设备的whid（优先重音乐设置中获取，如果音乐中未设置，取任意一个功放设备的whid）
	 * @return
	 */
	private String getAmplifierWhId() {
		String whid = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_WHID, null);
		if(whid == null) {
			ProductFun amplifierDevice = AppUtil.getSingleProductFunByFunType(getActivity(), 
					ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
			if(amplifierDevice != null) {
				whid = amplifierDevice.getWhId();
			}
		}
		return whid;
	}
	
	/**
	 * 获取合成语音的输入源设置
	 * @return
	 */
	private String[] getInput4Voice() {
		String[] ret = new String[2];
		ProductRegister pr = AppUtil.getProductRegisterByWhId(getActivity(), getAmplifierWhId());
		FunDetailConfigDaoImpl dao = new FunDetailConfigDaoImpl(getActivity());
		Logger.debug(null, "whId=? and funType=?" + pr.getGatewayWhId());
		List<FunDetailConfig> fdcs = dao.find(null, "whId=? and funType=?", 
				new String[]{pr.getGatewayWhId(), ProductConstants.FUN_TYPE_GATEWAY}, 
				null, null, null, null);
		if(fdcs.size() > 0) {
			String jsonStr = fdcs.get(0).getParams();
			if(jsonStr != null) {
				try {
					JSONObject jb = new JSONObject(jsonStr);
					ret[0] = jb.getString("input");
					ret[1] = jb.getString("addr");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}
		return ret;
	}
	
	/**
	 * 多条语音文字合成一条
	 * @param speakText
	 * @return
	 */
	private String ttsTextToString(String speakText){
		String text = "";
		if (TextUtils.isEmpty(speakText)) {
			return text;
		}
		String[] arr = speakText.split(";;");
		if (arr.length > 1) {
			StringBuffer sb = new StringBuffer("#" + arr.length + "#");
			for (int i = 0; i < arr.length; i++) {
				try {
					sb.append(intToString(arr[i].getBytes("utf-8").length));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < arr.length; i++) {
				sb.append(arr[i]);
			}
			text = sb.toString();
		}else {
			text = speakText;
		}
		return text;
	}
	
	private String intToString(int length){
		String _r = "";
		if (length < 10) {
			_r = "00" + length;
		} else if (length >= 10 && length < 100) {
			_r = "0" + length;
		} else if (length >= 100 && length < 1000) {
			_r = length + "";
		}
		return _r;
	}

 
	   /**
     * 百度基站定位错误返回码
     */
//  61 ： GPS定位结果
//  62 ： 扫描整合定位依据失败。此时定位结果无效。
//  63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
//  65 ： 定位缓存的结果。
//  66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
//  67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
//  68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
//  161： 表示网络定位结果
//  162~167： 服务端定位失败。

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		//接收位置信息
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				/**
				 * 格式化显示地址信息
				 */
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ncity : ");
				sb.append(location.getCity());
			}
			sb.append("\nsdk version : ");
			sb.append(mLocationClient.getVersion());
			sb.append("\nisCellChangeFlag : ");
			sb.append(location.isCellChangeFlag());
//			System.out.println("-------MyLocationListenner------>"+sb.toString());
			location_city_name = location.getCity();
			if(TextUtils.isEmpty(curr_city_name)){
				curr_city_name = location.getCity();
				getWeatherTask(location.getCity());
			}else{
				getWeatherTask(curr_city_name);
			}
			
		}
		//接收POI信息函数，我不需要POI，所以我没有做处理
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	/**
	 * 获取天气详情
	 * @param cityName
	 */
	private void getWeatherTask(final String cityName){
		StringBuffer _buff = new StringBuffer();
		_buff.append("http://api.map.baidu.com/telematics/v3/weather?location=");
		if (!TextUtils.isEmpty(cityName)) {
			_buff.append(Uri.encode(cityName, "utf-8"));
		}
		_buff.append("&output=json&ak=UjymBYD3091SXF9ZW5FwpDlG");
		String url = _buff.toString();
//		System.out.println("----url---->"+url);
		
		FinalHttp fh = new FinalHttp();
		fh.get(url, new AjaxCallBack(){

            @Override
            public void onSuccess(Object t) {
                try {
					JSONObject jsonObject = new JSONObject(t.toString());
//					System.out.println("----GetWeatherInfoRJ---->"+jsonObject.toString());
					String date = jsonObject.getString("date");
					String status = jsonObject.getString("status");
//					System.out.println("-----status----->"+status);
					String error = jsonObject.getString("error");
					List<WeatherInfo> weatherInfoList = new ArrayList<WeatherInfo>();
					if ("success".equals(status)){ 
						JSONArray resultsArray = jsonObject.getJSONArray("results");
						for (int i = 0; i < resultsArray.length(); i++) {
							JSONObject _json = (JSONObject) resultsArray.get(i);
							String currentCity = _json.getString("currentCity");
							curr_city_name= currentCity;
//							System.out.println("-----currentCity----->"+currentCity);
							JSONArray jsonArray = _json.getJSONArray("weather_data");
							for (int j = 0; j < jsonArray.length(); j++) {
								JSONObject _jo = (JSONObject) jsonArray.get(j);
								weatherInfoList.add(new WeatherInfo(
										_jo.getString("date"),
										_jo.getString("dayPictureUrl"),
										_jo.getString("nightPictureUrl"),
										_jo.getString("weather"),
										_jo.getString("wind"),
										_jo.getString("temperature")));
							}
						}
					}
					 
						Message message = mHandler.obtainMessage();
						message.what = GET_WEATHERINFO_SUCCESS;
						message.obj = weatherInfoList;
						message.sendToTarget();
					    mHandler.obtainMessage();
					    
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
            }

        });
		 
	}
       
	
}
