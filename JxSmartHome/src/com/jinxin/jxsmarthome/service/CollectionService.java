package com.jinxin.jxsmarthome.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.datan.local.util.NetworkUtil;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 后台数据更新用service
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CollectionService extends Service {
	public static boolean threadDisable;// 线程停止
	private ServiceBinder serviceBinder = new ServiceBinder();
	// ////////////////////////////////
	private String systemTime = null;
	// ///////////////////////////////
	private boolean isLogin = false;

	private ConnectivityManager connectivityManager;
	private NetworkInfo info;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final Context con = context;
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Logger.debug("mark", "网络状态已经改变");
				connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					String name = info.getTypeName();
					Logger.debug("mark", "当前网络名称：" + name);
					if (NetworkUtil.isMobileAvailable(context)
							|| NetworkUtil.isWifiAvailable(context)) {
						if (NetworkUtil.isMobileConnected(context)
								|| NetworkUtil.isWifiConnected(context)) {
							if (NetworkUtil.isMobileConnected(context)) {
								Logger.debug("mark","移动网络已连接");
							}
							if (NetworkUtil.isConnected(context)) {
								boolean isOffMode = false;
								String account = SharedDB.loadStrFromDB(
										SharedDB.ORDINARY_CONSTANTS,
										ControlDefine.KEY_ACCOUNT, "");
								if (account != null && account != "") {
									isLogin = SharedDB.loadBooleanFromDB(
											account,
											ControlDefine.KEY_ISLOADING, false);
									isOffMode = SharedDB.loadBooleanFromDB(
											account,
											ControlDefine.KEY_OFF_LINE_MODE,
											false);
								}
								if (isOffMode) {
									Logger.debug("mark","isMobileConnected");
									// 发送消息到主线程弹出提示对话框
									// Message msg = new Message();
									// msg.obj = context;
									Bundle bundle = new Bundle();
									bundle.putString("modeOn", "切换在线模式");
									// msg.setData(bundle);
									// msg.what = JxshApp.CHANGE_ONLINE_MODE;
									// JxshApp.getHandler().sendMessage(msg);
									BroadcastManager
											.sendBroadcast(
													BroadcastManager.ACTION_CHANGE_ON_LINE_MODE,
													bundle);
								}
							}
						} else {
							Logger.debug("mark", "wifi或移动网络不可用");
						}
					}
				} else {
					Logger.debug("mark", "没有可用网络");
				}
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!threadDisable) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					systemTime = getSystemTime();
					Logger.verbose("CountService", "Count is ");
				}
			}
		}).start();
		
		Logger.debug(null, "collection service started.");
	}

	@Override
	public void onDestroy() {
		Logger.debug(null, "collection service destoryed.");
		super.onDestroy();
		Logger.verbose("CountService", "Service onDestroy");
		this.threadDisable = true;
		unregisterReceiver(mReceiver);

		String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT, "");
		SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
		SharedDB.saveBooleanDB(_account, ControlDefine.KEY_OFF_LINE_MODE, false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return serviceBinder;
	}

	public class ServiceBinder extends Binder implements IDataService {

		@Override
		public String getSystemTime() {
			// TODO Auto-generated method stub
			return systemTime;
		}

		@Override
		public int getSignal() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getBatteryCharge() {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	// //////////////////////
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss");
	Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

	/**
	 * 获取系统日期时间
	 * 
	 * @return
	 */
	private String getSystemTime() {
		curDate.setTime(System.currentTimeMillis());
		return formatter.format(curDate);
	}
	
}
