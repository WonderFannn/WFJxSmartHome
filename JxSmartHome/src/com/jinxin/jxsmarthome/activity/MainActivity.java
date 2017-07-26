package com.jinxin.jxsmarthome.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.display.SimpleDisplayer;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.db.impl.CustomerMessageDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.OEMVersionDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.CustomerMeassage;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.OEMVersion;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.fragment.HomeFragment;
import com.jinxin.jxsmarthome.fragment.MainDeviceFragment;
import com.jinxin.jxsmarthome.fragment.MainSceneFragment;
import com.jinxin.jxsmarthome.fragment.MyAccountFragment;
import com.jinxin.jxsmarthome.fragment.TimerFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.ui.popupwindow.ShowSuggestWindows;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Downloader;
import com.jinxin.jxsmarthome.util.Downloader.DownloadListener;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;
import com.jinxin.zxing.activity.ScanActivity;

/**
 * 首页界面
 * @author TangLong
 * @company 金鑫智慧
 */
public class MainActivity extends BaseActionBarActivity implements OnTabChangeListener {
	private static final int DOWNLOAD_SUCCESS = 10;
	private static final int DOWNLOAD_FAIL = -11;
	private static final int DOWNLOAD_PROCESS = 11;
	private static final int DOWNLOAD_START = 12;

	private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;
	private String[] tabNames;
	private int[] tabDrawables;
	private Class<?>[] tabFragments;

	private ProgressDialog downloadDialog;
	private BroadcastReceiver cameraAppInstalledReceiver;
	private boolean hasReceiveInstallCameraApp;
	private boolean hasReceiveInstallBellApp;
	private boolean hasReceiveInstallKitApp;

	private int indicator = 0;
	private int exitCount = 0;		
	private long exitTouchTime = 0;

	private MenuItem menuItem = null;
	private MenuItem messageItem = null;

	private CustomerMessageDaoImpl cmDaoImpl = null;
	private List<CustomerMeassage> messageList = null;//未读消息
	private boolean hasNewMsg = false;
	private ImageView ivLogo;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (menuItem != null) {
					menuItem.setIcon(R.drawable.ico_set_new_button);
					messageItem.setIcon(R.drawable.ico_pop_new_message);
				}
				break;
			}
		}

	};

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.registerReceiver(WarnMessageBroadcast, 
				new IntentFilter(BroadcastManager.MESSAGE_WARN_RECEIVED_ACTION));
		//TODO SP判断使用哪种唤醒方式
		Boolean isCustomWake = SharedDB.loadBooleanFromDB(CommUtil.getCurrentLoginAccount(),
				ControlDefine.KEY_WAKE_MODE, false);
		if(isCustomWake){
			//自定义唤醒
			this.bindService(new Intent(
					"com.jinxin.jxsmarthome.service.CustomWakeService"),
					this.serviceConnection, BIND_AUTO_CREATE);
			Log.i("TAG", "此时是自定义唤醒方式");
		}else {
			//官方唤醒
			this.bindService(new Intent(
					"com.jinxin.jxsmarthome.service.OfficialWakeService"),
					this.serviceConnection, BIND_AUTO_CREATE);
			Log.i("TAG", "此时是官方唤醒方式");
		}
		initParam();
		initView();

		Intent intent = new Intent(SplashActivity.ACTION_FINISHED_LOAD);
		sendBroadcast(intent);

		// 注册应用安装成功的广播接受
		registerApplicationInstalledReceiver();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 检测是否是camera软件安装后的返回，如果是，打开camer软件
		if(hasReceiveInstallCameraApp) {
			startCameraApp();
			hasReceiveInstallCameraApp = false;
		} else if(hasReceiveInstallBellApp) {
			JxshApp.instance.checkBindBellService();
			hasReceiveInstallBellApp = false;
		} /*else if(hasReceiveInstallKitApp){
			JxshApp.instance.checkBindKitService();
			hasReceiveInstallKitApp = false;
		}*/
		doFetchLogo();
	}

	private void doFetchLogo() {
		OEMVersion data = null;
		OEMVersionDaoImpl daoImpl = new OEMVersionDaoImpl(this);
		List<OEMVersion> list = daoImpl.find(null, "account=?", new String[] { CommUtil.getCurrentLoginAccount() }, null, null, null, null);
		if (list != null && list.size() > 0) {
			data = list.get(0);
		}

		if (data != null) {
			ivLogo.setTag("logo");
			final FinalBitmap finalBitmap = JxshApp.instance.getFinalBitmap();
			finalBitmap.configRecycleImmediately(true);
			finalBitmap.configDisplayer(new SimpleDisplayer(){
				@Override
				public void loadCompletedisplay(View imageView, Bitmap bitmap, BitmapDisplayConfig config) {

					if("logo".equals(imageView.getTag())) {
						getSupportActionBar().setLogo(new BitmapDrawable(getResources(), bitmap));
						getSupportActionBar().setTitle("");
					} else {
						super.loadCompletedisplay(imageView, bitmap, config);
					}
				}
			});
			finalBitmap.display(ivLogo, DatanAgentConnectResource.HTTP_ICON_PATH + data.getAppLog().substring(1));
			System.out.println(DatanAgentConnectResource.HTTP_ICON_PATH + data.getAppLog().substring(1));
			getSupportActionBar().setLogo(ivLogo.getDrawable());
			getSupportActionBar().setTitle("");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(indicator == 0) {
			getMenuInflater().inflate(R.menu.menu_main, menu);
			menuItem = menu.findItem(R.id.main_button);
			messageItem = menu.findItem(R.id.action_message);
			if (hasNewMsg) {
				handler.sendEmptyMessage(1);
			}
		} else if (indicator == 1)  {
			getMenuInflater().inflate(R.menu.menu_scene, menu);
		} else if (indicator == 2)  {
			getMenuInflater().inflate(R.menu.menu_device, menu);
		} else if (indicator == 3)  {
			getMenuInflater().inflate(R.menu.menu_timer, menu);
		} else if (indicator == 4)  {
			getMenuInflater().inflate(R.menu.menu_my, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_camera :
			openCamera();
			break;
		case R.id.action_door :
			doDoorOperation();
			break;
		case R.id.action_doorbell:
			doDoorbellOperation();
			break;
		case R.id.action_kit:
			doKitOperation();
			break;
		case R.id.action_scan:
			Intent intent = new Intent(this, ScanActivity.class);
			startActivity(intent);
			break;
		case R.id.action_message:
			if (hasNewMsg) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (menuItem != null) {
							menuItem.setIcon(R.drawable.ico_set_button);
							messageItem.setIcon(R.drawable.ico_pop_message);
						}
					}
				});
			}
			startActivity(new Intent(MainActivity.this,MessageListActivity.class));
			break;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if(this.exitTouchTime == 0){//初始化退出计数
			this.exitCount = 0;
			this.exitTouchTime = System.currentTimeMillis();
		}else{
			if(System.currentTimeMillis() - this.exitTouchTime >= 10000){//10s 重置计数
				this.exitCount = 0;
				this.exitTouchTime = System.currentTimeMillis();
			}
		}
		if (this.exitCount <= 0) {
			this.exitCount++;
			JxshApp.showToast(getApplicationContext(), CommDefines.getSkinManager()
					.string(R.string.exit_program));
		} else {
			String _account = CommUtil.getCurrentLoginAccount();
			SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
			JxshApp.instance.onDestroy();
			super.onBackPressed();
		}
	}

	@Override
	public void uiHandlerData(Message msg) {
		switch(msg.what) {
		case DOWNLOAD_SUCCESS:
			if(downloadDialog != null) {
				downloadDialog.dismiss();
			}

			Bundle data2 = msg.getData();
			String savePath = null;
			if(data2 != null) {
				savePath = data2.getString("savePath");
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(savePath)),"application/vnd.android.package-archive");
				startActivity(intent);
			}
			break;
		case DOWNLOAD_FAIL:
			if(downloadDialog != null) {
				downloadDialog.dismiss();
			}
			break;
		case DOWNLOAD_START:
			downloadDialog = ProgressDialog.show(this, "提示", "正在下载 ");
			downloadDialog.setCancelable(true);
			downloadDialog.show();
			break;
		case DOWNLOAD_PROCESS:
			Bundle data1 = msg.getData();
			int downloadSize = 0;
			int totleSize = 0;
			if(data1 != null) {
				downloadSize = data1.getInt("downloadSize");
				totleSize = data1.getInt("totleSize");
				if(downloadSize > 0 && totleSize > 0) {
					double x = downloadSize * 1.0;
					double y = totleSize * 1.0;
					double fen = x / y;

					DecimalFormat df1 = new DecimalFormat("#0.00%");
					downloadDialog.setMessage("下载完成 " + df1.format(fen));
				}
			}
			break;
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		for(int i = 0; i < tabNames.length; i++) {
			if(tabNames[i].equals(tabId)) {
				indicator = i;
				//TODO
				showGuide(i);
			}
		}
		ActivityCompat.invalidateOptionsMenu(this);
	}

	private void initParam() {
		tabNames = getResources().getStringArray(R.array.tab_home_names);
		tabDrawables = new int[] {
				R.drawable.selector_tab_home, 
				R.drawable.selector_tab_scence,
				R.drawable.selector_tab_device,
				R.drawable.selector_tab_timer,
				R.drawable.selector_tab_my};
		tabFragments = new Class<?>[]{
			HomeFragment.class, 
			MainSceneFragment.class, 
			MainDeviceFragment.class, 
			TimerFragment.class, 
			MyAccountFragment.class};
			layoutInflater = LayoutInflater.from(this);

			cmDaoImpl = new CustomerMessageDaoImpl(MainActivity.this);
			messageList = cmDaoImpl.find(null, "isReaded=?", new String[]{String.valueOf(0)}, null, null, null, null);
			if (messageList != null && messageList.size() > 0) {
				hasNewMsg = true;
			}
	}

	private void initView() {
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.setOnTabChangedListener(this);
		Logger.debug(null, "hasNewMsg:"+hasNewMsg);
		int tabLength = tabNames.length;
		for(int i = 0; i < tabLength; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(tabNames[i]).setIndicator(getTabItemView(i));  
			mTabHost.addTab(tabSpec, tabFragments[i], null); 
			//            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_item); 
		}
		ivLogo = new ImageView(this);
	}

	/** 
	 * 给Tab按钮设置图标和文字 
	 */  
	private View getTabItemView(int index){
		View view = layoutInflater.inflate(R.layout.item_tab_main, null);  

		TextView tabText = (TextView)view.findViewById(R.id.tab_mian_item);

		tabText.setText(tabNames[index]);
		tabText.setCompoundDrawablesWithIntrinsicBounds(0, tabDrawables[index], 0, 0);

		return view;  
	}

	/**
	 * 新用户引导
	 * @param position
	 */
	private void showGuide(int position){
		switch (position) {
		case 0://首页
			suggestWindow = new ShowSuggestWindows(MainActivity.this, R.drawable.bg_guide_homepage, "");
			suggestWindow.showFullWindows("HomeFragment",R.drawable.bg_guide_homepage);
			break;
		case 1://模式
			suggestWindow = new ShowSuggestWindows(MainActivity.this, R.drawable.bg_guide_mode1, "");
			suggestWindow.showFullWindows("SenceFragment",R.drawable.bg_guide_mode1);
			break;
		case 2://设备
			//			new ShowSuggestWindows(MainActivity.this, R.drawable.bg_guide_device1, "").
			//			showFullWindows("DeviceFragment",R.drawable.bg_guide_device1);
			break;
		case 3://定时
			suggestWindow = new ShowSuggestWindows(MainActivity.this, R.drawable.bg_guide_timer, "");
			suggestWindow.showFullWindows("TimerFragment",R.drawable.bg_guide_timer);
			break;
		case 4://我的
			suggestWindow = new ShowSuggestWindows(MainActivity.this, R.drawable.bg_guide_my_info, "");
			suggestWindow.showFullWindows("MyInfoFragment",R.drawable.bg_guide_my_info);
			break;
		default:
			break;
		}
	}

	/**
	 * 打开摄像头
	 */
	private void openCamera() {
		if(NetworkModeSwitcher.useOfflineMode(getApplicationContext())) {
			showDiableToast();
		}else {
			try {
				PackageInfo pi = this.getPackageManager().getPackageInfo("com.jinxin.jxsmartmonitor", 
						PackageManager.GET_ACTIVITIES);
				if(pi.versionCode < 5) {
					final MessageBox mb2 = new MessageBox(
							this,CommDefines.getSkinManager().string(R.string.vein_retry_alert),"视频监控当前版本过低，确定升级",MessageBox.MB_OK);
					mb2.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							switch(mb2.getResult()){
							case MessageBox.MB_OK:
								downloadDialog = ProgressDialog.show(MainActivity.this, "提示", "正在下载 ");
								downloadDialog.setCancelable(true);
								downloadDialog.show();
								downloadDoorbellAPK();

								break;
							case MessageBox.MB_CANCEL:

								break;
							}
						}
					});
					mb2.setButtonText("确定","取消");
					mb2.show();
					return;
				}

				Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
				resolveIntent.setPackage(pi.packageName);

				List<ResolveInfo> apps = this.getPackageManager().queryIntentActivities(resolveIntent, 
						0);

				ResolveInfo ri = apps.iterator().next();
				if(ri != null) {
					String packageName = ri.activityInfo.packageName;
					String className = ri.activityInfo.name;

					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ComponentName cn = new ComponentName(packageName, className);
					intent.setComponent(cn);

					String _account = CommUtil.getCurrentLoginAccount();
					String monitorList = SharedDB.loadStrFromDB(_account,
							ControlDefine.KEY_MONITOR_LIST, "");
					Bundle data = new Bundle();
					data.putString(ControlDefine.KEY_MONITOR_LIST, monitorList);
					data.putString(ControlDefine.KEY_LOGIN, CommUtil.getCurrentLoginAccount());
					data.putString(ControlDefine.KEY_ACCOUNT, SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,""));
					data.putString(ControlDefine.KEY_SECRETKEY, SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_SECRETKEY,""));
					data.putString(ControlDefine.KEY_SUNACCOUNT, SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_SUNACCOUNT,""));
					data.putString(ControlDefine.KEY_MOST_USE_PATTERN, getPatternIdsMostInUse());
					intent.putExtras(data);

					this.startActivity(intent);
				}
			} catch (NameNotFoundException e) {
				// 异常情况下，说明没有安装需要的组件，进行下载软件和安装
				Logger.warn("DeviceControlGridAdapter", "download camera application");

				final MessageBox mb2 = new MessageBox(
						this,CommDefines.getSkinManager().string(R.string.vein_retry_alert),"视频监控没有安装，请确认下载安装",MessageBox.MB_OK|MessageBox.MB_CANCEL);
				mb2.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						switch(mb2.getResult()){
						case MessageBox.MB_OK:
							downloadDialog = ProgressDialog.show(MainActivity.this, "提示", "正在下载 ");
							downloadDialog.setCancelable(true);
							downloadDialog.show();
							downloadCameraAPK();
							break;
						case MessageBox.MB_CANCEL:

							break;
						}
					}
				});
				mb2.setButtonText("确定","取消");
				mb2.show();
			}
		}
	}

	/**
	 * 得到最常用的模式
	 */
	private String getPatternIdsMostInUse() {
		CustomerPatternDaoImpl cpDao = new CustomerPatternDaoImpl(this);
		List<CustomerPattern> _cps = cpDao.find(null, null, null, null, null, "stayTop DESC, clickCount DESC", "0,4");
		if(_cps !=null && _cps.size() > 0) {
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < _cps
					.size(); i++) {
				CustomerPattern _cp = _cps
						.get(i);
				if (_cp != null) {
					if (i < _cps.size() - 1) {
						ids.append(
								_cp.getPatternId())
						.append(",");
					} else {
						ids.append(_cp
								.getPatternId());
					}
				}
			}
			return ids.toString();
		}
		return "";
	}

	/**
	 * 离线禁用提示
	 */
	private void showDiableToast() {
		Toast.makeText(this, "此功能在离线模式下不可用", Toast.LENGTH_LONG).show();;
	}

	/**
	 * 门禁操作
	 */
	private void doDoorOperation() {
		if(NetworkModeSwitcher.useOfflineMode(getApplicationContext())) {
			showDiableToast();
		}else {
			showDoorInfoDialog();
		}
	}
	/**
	 * 电子门铃操作
	 */
	private void doDoorbellOperation() {
		if (NetworkModeSwitcher.useOfflineMode(getApplicationContext())) {
			showDiableToast();
		} else {
			try {
				PackageInfo pi = this.getPackageManager().getPackageInfo("com.jinxin.jxsmartdoorbell",
						PackageManager.GET_ACTIVITIES);
				if(pi.versionCode < 5) {
					final MessageBox mb2 = new MessageBox(
							this,CommDefines.getSkinManager().string(R.string.vein_retry_alert),"门铃当前版本过低，确定升级",MessageBox.MB_OK);
					mb2.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							switch(mb2.getResult()){
							case MessageBox.MB_OK:
								downloadDialog = ProgressDialog.show(MainActivity.this, "提示", "正在下载 ");
								downloadDialog.setCancelable(true);
								downloadDialog.show();
								downloadDoorbellAPK();

								break;
							case MessageBox.MB_CANCEL:

								break;
							}
						}
					});
					mb2.setButtonText("确定","取消");
					mb2.show();
					return;
				}
				Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
				resolveIntent.setPackage(pi.packageName);

				List<ResolveInfo> apps = this.getPackageManager().queryIntentActivities(resolveIntent, 0);

				ResolveInfo ri = apps.iterator().next();
				if (ri != null) {
					JxshApp.instance.initBellController();

					String packageName = ri.activityInfo.packageName;
					String className = ri.activityInfo.name;

					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ComponentName cn = new ComponentName(packageName, className);
					intent.setComponent(cn);
					this.startActivity(intent);
				}
			} catch (NameNotFoundException e) {
				// 异常情况下，说明没有安装需要的组件，进行下载软件和安装
				Logger.warn("DeviceControlGridAdapter", "download camera application");

				final MessageBox mb2 = new MessageBox(
						this,CommDefines.getSkinManager().string(R.string.vein_retry_alert),"电子门铃没有安装，请确认下载安装",MessageBox.MB_OK|MessageBox.MB_CANCEL);
				mb2.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						switch(mb2.getResult()){
						case MessageBox.MB_OK:
							downloadDialog = ProgressDialog.show(MainActivity.this, "提示", "正在下载 ");
							downloadDialog.setCancelable(true);
							downloadDialog.show();
							downloadDoorbellAPK();

							break;
						case MessageBox.MB_CANCEL:

							break;
						}
					}
				});
				mb2.setButtonText("确定","取消");
				mb2.show();
			}
		}

	}
	
	/**
	 * 电子门铃操作
	 */
	private void doKitOperation() {
		if (NetworkModeSwitcher.useOfflineMode(getApplicationContext())) {
			showDiableToast();
		} else {
			try {
				PackageInfo pi = this.getPackageManager().getPackageInfo("com.jinxin.jxsmartkit",
						PackageManager.GET_ACTIVITIES);
//				if(pi.versionCode < 5) {
//					final MessageBox mb2 = new MessageBox(
//							this,CommDefines.getSkinManager().string(R.string.vein_retry_alert),"猫眼当前版本过低，确定升级",MessageBox.MB_OK);
//					mb2.setOnDismissListener(new OnDismissListener() {
//
//						@Override
//						public void onDismiss(DialogInterface dialog) {
//							switch(mb2.getResult()){
//							case MessageBox.MB_OK:
//								downloadDialog = ProgressDialog.show(MainActivity.this, "提示", "正在下载 ");
//								downloadDialog.setCancelable(true);
//								downloadDialog.show();
//								downloadKitAPK();
//
//								break;
//							case MessageBox.MB_CANCEL:
//
//								break;
//							}
//						}
//					});
//					mb2.setButtonText("确定","取消");
//					mb2.show();
//					return;
//				}
//				Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
//				resolveIntent.setPackage(pi.packageName);
//
//				List<ResolveInfo> apps = this.getPackageManager().queryIntentActivities(resolveIntent, 0);
//
//				ResolveInfo ri = apps.iterator().next();
//				if (ri != null) {
//					JxshApp.instance.checkBindKitService();

//					String packageName = ri.activityInfo.packageName;
//					String className = ri.activityInfo.name;

					AppUtil.startKit();
//				}
			} catch (NameNotFoundException e) {
				// 异常情况下，说明没有安装需要的组件，进行下载软件和安装
				Logger.warn("DeviceControlGridAdapter", "download camera application");

				final MessageBox mb2 = new MessageBox(
						this,CommDefines.getSkinManager().string(R.string.vein_retry_alert),"电子猫眼没有安装，请确认下载安装",MessageBox.MB_OK|MessageBox.MB_CANCEL);
				mb2.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						switch(mb2.getResult()){
						case MessageBox.MB_OK:
							downloadDialog = ProgressDialog.show(MainActivity.this, "提示", "正在下载 ");
							downloadDialog.setCancelable(true);
							downloadDialog.show();
							downloadKitAPK();

							break;
						case MessageBox.MB_CANCEL:

							break;
						}
					}
				});
				mb2.setButtonText("确定","取消");
				mb2.show();
			}
		}

	}

	/**
	 * 显示门禁信息的弹出窗口
	 */
	private void showDoorInfoDialog() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(
				R.layout.custom_common_info_dialog_door, null);
		Button mUpdate = (Button) dialogView.findViewById(R.id.door_update);
		Button mOpen = (Button) dialogView.findViewById(R.id.door_open);
		Button mClose = (Button)dialogView.findViewById(R.id.door_close);
		Button mCancel = (Button) dialogView.findViewById(R.id.btn_no);
		final Dialog mDialog = BottomDialogHelper.showDialogInBottom(this,
				dialogView, null);
		mCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.cancel();
			}
		});

		mOpen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendDoorOperationCmd("open");
				mDialog.cancel();
			}
		});

		mClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendDoorOperationCmd("close");
				mDialog.cancel();
			}
		});

		mUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendDoorOperationCmd("get");
				mDialog.cancel();
			}
		});
	}

	/**
	 * 发送获取门禁状态命令
	 */
	private void sendDoorOperationCmd(String operation) {
		ProductFun productFun = null;

		List<ProductFun> productFuns = getProductFun(ProductConstants.FUN_TYPE_AUTO_LOCK);
		if(productFuns.size() < 1) {
			return;
		}else {
			productFun = productFuns.get(0);
		}

		FunDetail funDetail = getFunDetail(ProductConstants.FUN_TYPE_AUTO_LOCK);
		if (productFun == null || funDetail == null) {
			return;
		}

		if("close".equals(operation)) {
			productFun.setFunType(ProductConstants.FUN_TYPE_AUTO_LOCK_CLOSE);
		}else if("open".equals(operation)) {
			productFun.setFunType(ProductConstants.FUN_TYPE_AUTO_LOCK_OPEN);
		}else if("get".equals(operation)) {
			productFun.setFunType(ProductConstants.FUN_TYPE_AUTO_LOCK_SEARCH);
		}else {
			Logger.error(null, "wrong operaiton");
			return;
		}

		List<byte[]> cmds = CommonMethod.productFunToCMD(this, productFun,
				funDetail,null);

		if (cmds == null || cmds.size() < 1) {
			return;
		}

		// --------------------------- offline -------------------------
		if(NetworkModeSwitcher.useOfflineMode(this)) {
			OfflineCmdSenderLong offlineCmdSender = new OfflineCmdSenderLong(this, cmds);
			offlineCmdSender.send();
			return;
		}
		// ------------------------------ end --------------------------

		new OnlineCmdSenderLong(this, cmds).send();
	}

	/**
	 * 获取设备的FunDetail
	 */
	private FunDetail getFunDetail(String type) {
		FunDetailDaoImpl funDetailDaoImpl = new FunDetailDaoImpl(this);
		ArrayList<FunDetail> list_detail = new ArrayList<FunDetail>();
		list_detail = (ArrayList<FunDetail>) funDetailDaoImpl.find(null,
				"funType = ?", new String[] {type}, null, null, null, null);
		if(list_detail != null && list_detail.size() > 0) {
			return list_detail.get(0);
		}
		return null;
	}

	/**
	 * 获取设备的ProductFun
	 */
	private List<ProductFun> getProductFun(String type) {
		ProductFunDaoImpl proDaoImpl = new ProductFunDaoImpl(this);
		ArrayList<ProductFun> list = new ArrayList<ProductFun>();
		list = (ArrayList<ProductFun>) proDaoImpl.find(null,
				"funType = ?", new String[] {type}, null, null, null, null);

		return list;
	}

	// ----------------------------------------------- camera ------------------------------------------
	/* download camera app */
	private void downloadCameraAPK() {
		Downloader downloader = new Downloader(this, DatanAgentConnectResource.DOWNLOAD_CAMERA_APP,
				null, null);
		new Thread(downloader).start();

		downloader.addDownloadListener(new DownloadListener() {
			@Override
			public void onStart() {
				Message msg = Message.obtain(mUIHander, DOWNLOAD_START);
				msg.sendToTarget();
			}

			@Override
			public void onProcess(int downloadSize, int totleSize) {
				Message msg = Message.obtain(mUIHander, DOWNLOAD_PROCESS);
				Bundle data = new Bundle();
				data.putInt("downloadSize", downloadSize);
				data.putInt("totleSize", totleSize);
				msg.setData(data);
				msg.sendToTarget();
			}

			@Override
			public void onFail() {
				mUIHander.sendEmptyMessage(DOWNLOAD_FAIL);
			}

			@Override
			public void onFinish(String savePath) {
				Message msg = Message.obtain(mUIHander, DOWNLOAD_SUCCESS);
				Bundle data = new Bundle();
				data.putString("savePath", savePath);
				msg.setData(data);
				msg.sendToTarget();
			}
		});
	}

	/* download camera app */
	private void downloadDoorbellAPK() {
		Downloader downloader = new Downloader(this, DatanAgentConnectResource.DOWNLOAD_DOORBELL_APP, null, null);

		downloader.addDownloadListener(new DownloadListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onProcess(int downloadSize, int totleSize) {
				Message msg = Message.obtain(mUIHander, DOWNLOAD_PROCESS);
				Bundle data = new Bundle();
				data.putInt("downloadSize", downloadSize);
				data.putInt("totleSize", totleSize);
				msg.setData(data);
				msg.sendToTarget();
			}

			@Override
			public void onFail() {
				mUIHander.sendEmptyMessage(DOWNLOAD_FAIL);
			}

			@Override
			public void onFinish(String savePath) {
				Message msg = Message.obtain(mUIHander, DOWNLOAD_SUCCESS);
				Bundle data = new Bundle();
				data.putString("savePath", savePath);
				msg.setData(data);
				msg.sendToTarget();
			}
		});

		new Thread(downloader).start();
	}
	
	private void downloadKitAPK() {
		Downloader downloader = new Downloader(this, DatanAgentConnectResource.DOWNLOAD_KIT_APP, null, null);

		downloader.addDownloadListener(new DownloadListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onProcess(int downloadSize, int totleSize) {
				Message msg = Message.obtain(mUIHander, DOWNLOAD_PROCESS);
				Bundle data = new Bundle();
				data.putInt("downloadSize", downloadSize);
				data.putInt("totleSize", totleSize);
				msg.setData(data);
				msg.sendToTarget();
			}

			@Override
			public void onFail() {
				mUIHander.sendEmptyMessage(DOWNLOAD_FAIL);
			}

			@Override
			public void onFinish(String savePath) {
				Message msg = Message.obtain(mUIHander, DOWNLOAD_SUCCESS);
				Bundle data = new Bundle();
				data.putString("savePath", savePath);
				msg.setData(data);
				msg.sendToTarget();
			}
		});

		new Thread(downloader).start();
	}
	
	/*register app has been installed receiver, for if the camera app is installed, start it.*/
	private void registerApplicationInstalledReceiver() {
		cameraAppInstalledReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent != null) {
					String packageName = intent.getData().getSchemeSpecificPart();
					Logger.warn(null, packageName);
					if("com.jinxin.jxsmartmonitor".equals(packageName)) {
						hasReceiveInstallCameraApp = true;
					} else if("com.jinxin.jxsmartdoorbell".equals(packageName)) {
						hasReceiveInstallBellApp = true;
						JxshApp.instance.initBellController();
					} /*else if("com.jinxin.jxsmartkit".equals(packageName)){
						hasReceiveInstallKitApp = true;
						JxshApp.instance.checkBindKitService();
					}*/
				}
			}
		};
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);  
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addDataScheme("package");
		registerReceiver(cameraAppInstalledReceiver, filter);  
	}

	/* open camera app */
	private void startCameraApp() {
		try {
			PackageInfo pi = this.getPackageManager().getPackageInfo("com.tutk.P2PCam264", 
					PackageManager.GET_ACTIVITIES);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = this.getPackageManager().queryIntentActivities(resolveIntent, 
					0);

			ResolveInfo ri = apps.iterator().next();
			if(ri != null) {
				String packageName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(packageName, className);
				intent.setComponent(cn);
				this.startActivity(intent);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private BroadcastReceiver WarnMessageBroadcast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastManager.MESSAGE_WARN_RECEIVED_ACTION)) {
				hasNewMsg = true;
				handler.sendEmptyMessage(1);
			}
		}
	};

	@Override
	protected void onDestroy() {
		this.unbindService(serviceConnection);
		this.unregisterReceiver(WarnMessageBroadcast);
		this.unregisterReceiver(cameraAppInstalledReceiver);

		if (downloadDialog !=null && downloadDialog.isShowing()) {
			downloadDialog.dismiss();
			downloadDialog = null;
		}
		super.onDestroy();
	}}
