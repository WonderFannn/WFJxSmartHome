package com.jinxin.jxsmarthome.main;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.jinxin.broad.BroadPushServerService;
import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.jpush.MessagePushThread;
import com.jinxin.jxsmartdoorbell.service.IBellService;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.LoginActivity;
import com.jinxin.jxsmarthome.activity.SplashActivity;
import com.jinxin.jxsmarthome.aidl.MusicServiceAidl;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.password.pattern.MotionDetector;
import com.jinxin.jxsmarthome.service.CustomWakeService;
import com.jinxin.jxsmarthome.service.IDataService;
import com.jinxin.jxsmarthome.ui.dialog.CustomProgressDialogCreater;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.ui.widget.textviewex.TextSizeChangeMonitor;
import com.jinxin.jxsmarthome.ui.widget.textviewex.TextSizeChangeMonitorImpl;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.ScreenManager;
import com.jinxin.jxsmartkit.IBinderPool;
import com.jinxin.jxsmartkit.IKitControl;
import com.jinxin.notification.MyNotificationManager;
import com.jinxin.record.FileManager;
import com.jinxin.record.SharedDB;
import com.jinxin.skin.SkinManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import net.tsz.afinal.FinalBitmap;
import xgzx.VeinUnlock.VeinCore;


/**
 * 程序启动APP
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class JxshApp extends Application implements UncaughtExceptionHandler{

	public static JxshApp instance;
	private Dialog pd = null;
	private FinalBitmap finalBitmap;

	private IDataService iDataService = null;
	private IBellService iBellService = null;
	private MusicServiceAidl musicService = null;

	private Handler mHandler;
	private Toast toast;

	private HandlerThread handlerThread = null;
	private DatanAgentHandler datanAgentHandler = null;// 数据处理的独立handler
	
	public static VeinCore m_VeinCore = null;
	/**
	 * 当前要登录的帐号
	 */
	public static String lastInputID = null;
	
	public MusicLib musicLib ;//当前播放音乐
	private TextSizeChangeMonitor monitor = null;
	// 声纹识别对象
	private SpeakerVerifier mVerify = null;
	
	/**
	 * 点击按钮能否可发送指令
	 * true: 正在执行一次命令发送  false：发送完成或未开始发送指令
	 */
	public boolean isClinkable = false; 
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iDataService = (IDataService) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iDataService = null;
		}
	};
	private ServiceConnection bellServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBellService = IBellService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBellService = null;
		}
	};
	
	private ServiceConnection musicConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			musicService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			musicService = (MusicServiceAidl) service;
		}
	};
	
	// ////////////////////////////////////
	public static final int PROGRESS_DIALOG_SHOW = 0;// 显示进度条
	public static final int PROGRESS_DIALOG_STOP = 1;// 关闭进度条
	public static final int SHOW_MY_TOAST = 2;// 显示toast
	public static final int EXIT_MESSAGE_RELOGIN = 102;// 重新登录
	public static final int CHANGE_OFFLINE_MODE = 104;// 切换离线模式
	public static final int CHANGE_ONLINE_MODE = 111;// 切换现在模式
	public static final int BINDER_KIT = 1;
	
	private static final String TAG = "JPush";
	private static final String APPID = "900006758";
	
	public static  boolean IpFlag=true;
//	private MessageReceiver mMessageReceiver;
//	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
//	public static final String KEY_TITLE = "title";
//	public static final String KEY_MESSAGE = "message";
//	public static final String KEY_EXTRAS = "extras";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
		// 注册App异常崩溃处理器
//		Thread.setDefaultUncaughtExceptionHandler(this);
		Logger.debug(TAG, "[ExampleApplication] onCreate");
//		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        JPushInterface.resumePush(getApplicationContext());//stopPush只后必须重新开启Jpush
//        JPushInterface.stopPush(getApplicationContext());//停止推送
        
        // 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
 		// 设置你申请的应用讯飞appid
 		SpeechUtility.createUtility(JxshApp.this, "appid="+getString(R.string.app_id));
 		Setting.setShowLog(false);//是否显示讯飞LOG
 		
 		//设置Umeng APP_KEY
 		AnalyticsConfig.setAppkey(getApplicationContext(),getString(R.string.umeng_appkey));
 		/** 设置是否对日志信息进行加密, 默认false(不加密). */
 		AnalyticsConfig.enableEncrypt(true);
 		MobclickAgent.updateOnlineConfig(getApplicationContext());
// 		MobclickAgent.setDebugMode(true);//开启调试模式所产生的数据不记入统计,发布时设置为false;
// 		AppUtil.getDeviceInfo(getApplicationContext());
// 		Intent PushService = new Intent(this, BroadPushServerService.class);
//        startService(PushService);
		
 		CrashReport.initCrashReport(this, APPID, true);//bugly 初始化
 		
 		SDKInitializer.initialize(getApplicationContext());  
 		
 		// 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
		mVerify = SpeakerVerifier.createVerifier(this, new InitListener() {

			@Override
			public void onInit(int errorCode) {
				if (ErrorCode.SUCCESS == errorCode) {
					JxshApp.showToast(JxshApp.this, "声纹识别引擎初始化成功");
				} else {
					JxshApp.showToast(JxshApp.this, "声纹识别引擎初始化失败，错误码：" + errorCode);
				}
			}
		});
        
        //初始化字体大小设置对象
        monitor = new TextSizeChangeMonitorImpl();
        
      //创建图片缓存
  		getFinalBitmap();
        
  		//绑定服务
		this.bindService(new Intent(
				"com.jinxin.jxsmarthome.service.CollectionService"),
				this.serviceConnection, BIND_AUTO_CREATE);
		this.bindService(new Intent(
				"com.jinxin.jxsmarthome.service.MusicService"),
				this.musicConnection, BIND_AUTO_CREATE);
		this.bindService(new Intent(
				"com.jinxin.jxsmarthome.service.VoiceService"),
				this.musicConnection, BIND_AUTO_CREATE);
//		this.bindService(new Intent(
//				"com.jinxin.jxsmarthome.service.WakeService"),
//				this.serviceConnection, BIND_AUTO_CREATE);
		//大网关广播推送，接收端服务
		Intent PushService = new Intent(this, BroadPushServerService.class);
        startService(PushService);
		
        Thread thread = new Thread(new MessagePushThread());
		thread.start();
		
//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.binder_pool");
//		intent.setPackage("com.jinxin.jxsmartkit");
//		this.bindService(intent, kitServiceConnection, Context.BIND_AUTO_CREATE);
        
		this.bindService(new Intent("com.jinxin.jxsmardoorbell.action.START_COM"), this.bellServiceConnection,
				BIND_AUTO_CREATE);
		
		this.bindService(new Intent(
				"com.jinxin.jxsmarthome.service.MotionDetectorService"),
				this.musicConnection, BIND_AUTO_CREATE);
		
		this.handlerThread = new HandlerThread("DatanAgentRunnable");
		this.handlerThread.start();
		this.datanAgentHandler = new DatanAgentHandler(this.handlerThread.getLooper());
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case PROGRESS_DIALOG_SHOW:
					try {
						Activity a = (Activity) msg.obj;
						if (pd != null && pd.isShowing()) {
//							pd.dismiss();
//							pd = null;
							return;
						}
						if (!a.isFinishing()) {
							Bundle bundle = msg.getData();
							String _msg = "正在操作...";
							if(bundle != null){
								_msg = bundle.getString("msg");
							}
							
							// 更改加载提示为自定义的Dialog TangLong 2014.02.10
							pd = CustomProgressDialogCreater.createLoadingDialog((Context) msg.obj, _msg);
//							pd = CustomProgressDialog.createDialog((Context) msg.obj).setMessage(_msg);
							pd.show();
						}

						pd.setCanceledOnTouchOutside(false);
					} catch (Exception e) {

					}
					break;
				case PROGRESS_DIALOG_STOP:
					try {
						if (pd != null && pd.isShowing()) {
							pd.dismiss();
							pd = null;
						}
					} catch (Exception e) {

					}
					break;
				case SHOW_MY_TOAST:
//					MyToast.makeText((Context) msg.obj,
//							msg.getData().getString("error"),
//							MyToast.LENGTH_LONG).show();
					int time = msg.getData().getInt("time");
					if(toast ==null){
					toast = Toast.makeText((Context) msg.obj,
							msg.getData().getString("error"),
							time == 0 ? Toast.LENGTH_SHORT : time);
					}else{
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setText(msg.getData().getString("error"));
					}
				    toast.show();
					break;
				case EXIT_MESSAGE_RELOGIN:
					final Context _context = (Context) msg.obj;
    				if (!NetworkModeSwitcher.useOfflineMode(_context)) {
    					if (mb == null) {
    						String title = msg.getData().getString("error");
							mb = new MessageBox(_context, title, "请重新登录", MessageBox.MB_OK);
							mb.setButtonText("立即登录", null);
							mb.setOnDismissListener(new OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
									switch (mb.getResult()) {
									case MessageBox.MB_OK:// 点击消失
										mb.dismiss();
										mb = null;
										if(_context instanceof LoginActivity)
											break;
										BroadcastManager.sendBroadcast(BroadcastManager.ACTION_EXIT_MESSAGE_RELOGIN, null);
										break;
									}
								}
							});
							if (_context != null && _context instanceof Activity && !((Activity)_context).isFinishing()) {
								mb.show();
								Intent intent = new Intent(SplashActivity.ACTION_FINISHED_LOAD);
								sendBroadcast(intent);
							}
						}
						
					}
					break;
				}
			}

		};
		
	}
	
	MessageBox mb = null;
	/**
	  * 获得图片缓存操作对象
	  * @return-
	  */
	public FinalBitmap getFinalBitmap() {
		if (finalBitmap == null) {
			this.finalBitmap = FinalBitmap.create(getContext());
			this.finalBitmap.configRecycleImmediately(false);//TO FIX FEEDBACK LOAD THUMBNAIL
			this.finalBitmap.configDiskCachePath(FileManager.instance().getCacheImagePath(0));
		}
		return finalBitmap;
	}
	
	/**
	 * 判断APP是否运行在后台
	 * @param context
	 * @return
     */
	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
				Log.i(context.getPackageName(), "此appimportace ="
						+ appProcess.importance
						+ ",context.getClass().getName()="
						+ context.getClass().getName());
				if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.i(context.getPackageName(), "处于后台"
							+ appProcess.processName);
					return true;
				} else {
					Log.i(context.getPackageName(), "处于前台"
							+ appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * @return 声纹对象
	 */
	public SpeakerVerifier getVerifier(){
		return mVerify;
	}

	public void setMusic(MusicLib musicLib){
		instance.musicLib = musicLib;
	}
	
	public MusicLib getMusicLib(){
		return instance.musicLib;
	}
	
	public TextSizeChangeMonitor getMonitor(){
		return instance.monitor;
	}


	public IDataService getIdataService() {
		return iDataService;
	}

	public HandlerThread getHandlerThread() {
		return handlerThread;
	}

	/**
	 * 数据处理独立handler（区别于主线程handler）
	 * 
	 * @return
	 */
	public static DatanAgentHandler getDatanAgentHandler() {
		return instance.datanAgentHandler;
	}

	public static Dialog getPd() {
		return instance.pd;
	}

	public static void setPd(ProgressDialog pd) {
		instance.pd = pd;
	}

	public static Context getContext() {
		return instance.getApplicationContext();
	}

	/**
	 * 退出处理
	 */
	public void onDestroy() {
		Logger.debug("Yang", "App onDestroy");
		String _account = CommUtil.getCurrentLoginAccount();
		SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
		SharedDB.saveBooleanDB(_account, ControlDefine.KEY_OFF_LINE_MODE, false);
		this.unbindService(serviceConnection);
//		killKit();
//		this.unbindService(kitServiceConnection);
		this.unbindService(bellServiceConnection);
		// 退出程序停止语音唤醒监听服务
		VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null) {
			Logger.debug(null, ""+mIvw.isListening());
			mIvw.stopListening();
		}
		this.stopService(new Intent(getApplicationContext(),MotionDetector.class));
//		JPushInterface.stopPush(getApplicationContext());//停止推送
		this.handlerThread.quit();
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_EXIT_MESSAGE, null);
		CommUtil.kill(getContext());
	}
	
	/**
	 * 退出当前账号
	 */
	public void logout() {   
		Logger.debug("Yang", "App onDestroy");
		String _account = CommUtil.getCurrentLoginAccount();
		SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
		SharedDB.saveBooleanDB(_account, ControlDefine.KEY_OFF_LINE_MODE, false);
		// 停止语音唤醒监听服务
		VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null) {
			Logger.debug(null, ""+mIvw.isListening());
			mIvw.stopListening();
		}
		if(CustomWakeService.hearer != null){
			BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE_CLOSE, null);
		}
//		JPushInterface.stopPush(getApplicationContext());//停止推送
		System.gc();
		Intent intent = new Intent(this,LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	/**
	 * 弹提示公共方法
	 * @param context
	 * @param str
	 *            提示内容
	 */
	public static void showToast(Context context, String str) {
		if (context == null || str == null)
			return;
		Message msg = JxshApp.getHandler().obtainMessage();
		msg.obj = context;
		Bundle bundle = new Bundle();
		bundle.putString("error", str);
		msg.setData(bundle);
		msg.what = JxshApp.SHOW_MY_TOAST;
//		JxshApp.getHandler().sendMessage(msg);
		msg.sendToTarget();
	}
	/**
	 * 弹提示公共方法
	 * 
	 * @param context
	 * @param str
	 *            提示内容
	 */
	public static void showToast(Context context, String str,int time) {
		if (context == null || str == null)
			return;
		Message msg = JxshApp.getHandler().obtainMessage();
		msg.obj = context;
		Bundle bundle = new Bundle();
		bundle.putString("error", str);
		bundle.putInt("time", time);
		msg.setData(bundle);
		msg.what = JxshApp.SHOW_MY_TOAST;
//		JxshApp.getHandler().sendMessage(msg);
		msg.sendToTarget();
	}

	/**
	 * 弹loading效果
	 * 
	 * @param context
	 */
	public static void showLoading(Context context,String text) {
		if (context == null)
			return;
		Message msg = JxshApp.getHandler().obtainMessage();
		msg.obj = context;
		Bundle bundle = new Bundle();
		bundle.putString("msg", text);
		msg.setData(bundle);
		msg.what = JxshApp.PROGRESS_DIALOG_SHOW;
//		JxshApp.getHandler().sendMessage(msg);
		msg.sendToTarget();
	}

	/**
	 * 关闭loading效果
	 */
	public static void closeLoading() {
		Message msg = JxshApp.getHandler().obtainMessage();
		msg.what = JxshApp.PROGRESS_DIALOG_STOP;
//		JxshApp.getHandler().sendMessageDelayed(msg, 0);
		msg.sendToTarget();
	}

	// /////////////////////////////////////////////////////
	/**
	 * APP handler
	 * 
	 * @return
	 */
	public static Handler getHandler() {
		return instance.mHandler;
	}

	public class DatanAgentHandler extends Handler {
		public DatanAgentHandler() {

		}

		public DatanAgentHandler(Looper looper) {
			super(looper);
		}
	}
	
	/*****zj20131009:新增调用常用方法类******************/
	/**
	 * 常用参数
	 * @author JackeyZhang
	 *
	 */
	public static class CommDefines {
		/**
		 * 皮肤管理器
		 * 
		 * @return
		 */
		public static SkinManager getSkinManager() {
			return SkinManager.instance(getContext());
		}
		/**
		 * 界面路径管理器
		 * 
		 * @return
		 */
		public static ScreenManager getScreenManager() {
			return ScreenManager.instance();
		}
		/**
		 * 文件管理器
		 * 
		 * @return
		 */
		public static FileManager getFileManager() {
			return FileManager.instance();
		}
		/**
		 *  任务栏管理器
		 * 
		 * @return
		 */
		public static MyNotificationManager getNotificationManager() {
			return MyNotificationManager.instance(getContext());
		}
	}
	
	 @Override
	 public void uncaughtException(Thread thread, Throwable ex)
	 {
		 Logger.error("!!!!", "全局异常");
		 ex.printStackTrace();
		 CommDefines.getFileManager().writeExceptionLog(ex, false);
		 System.exit(0);
	 }

	/**
	 * 获取登录过用户列表
	 */
	public static List<String> getHistoryUserList() {
		return SharedDB.loadStrListFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT_HISTORY, "");
	}
	
	/**
	 * 保存登录用户列表
	 * @param account
	 * @param password
	 */
	public static void saveHistoryUserList(String account, String password) {
		SharedDB.saveStrListDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT_HISTORY, account);
	}

	private static boolean isFirstInstall() {
		return SharedDB.loadBooleanFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_FIRST_INSTALL, true);
	}
	/**
	 * 是否开启静脉纹
	 * @param account
	 * @return
	 */
	public static boolean isVeinOpen(String account) {
		return SharedDB.loadBooleanFromDB(account,
				ControlDefine.KEY_ENABLE_PASSWORD_VEIN, false);
	}

	/**
	 * 获取密码
	 * @param account
	 * @return
	 */
	public static String getPassword(String account) {
		String password = EncryptUtil.getPassword(account);;
		return password;
	}
	
	/**
	 * 設置Vein
	 * @param user
	 */
	public static boolean setVeinUser(String user) {
		m_VeinCore = new VeinCore();
		String veinPath = FileManager.instance().getVeinFilePath(user);
		if (veinPath != null) {
			int ret = m_VeinCore.CreateVein(android.os.Build.MODEL, veinPath);
			m_VeinCore.VeinSetLevel(2);
			Logger.debug(ACTIVITY_SERVICE, "VeinInit ret:" + ret);
			if (ret != 0 && ret != 0x80) {
				Toast.makeText(JxshApp.getContext(), R.string.msg_sdk_fail, Toast.LENGTH_LONG).show();
				System.exit(0);
			} else if(ret == 0x80){
				return false;
			}
			m_VeinCore.setUser(user);
			if(isFirstInstall()) {
				m_VeinCore.VeinDelEnroll(VeinCore.PART_WRIST);
				m_VeinCore.VeinDelEnroll(VeinCore.PART_BACKHAND_L);
				m_VeinCore.VeinDelEnroll(VeinCore.PART_PALM_L);
				
				SharedDB.saveBooleanDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_FIRST_INSTALL, false);
			}
			return true;
		}
		return false;
	}

	// 初始化门铃，连接门铃，注册报警接收器
	public void initBellController() {
		try {
			checkBindBellService();
			if (iBellService != null) {
				iBellService.init(CommUtil.getCurrentLoginAccount());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void killBell() {
		try {
			if (iBellService != null) {
				iBellService.logout();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	// 安装门铃后启动门铃服务
	public void checkBindBellService() {
		System.out.println("login check");
		if(iBellService == null) {
			this.bindService(new Intent("com.jinxin.jxsmardoorbell.action.START_COM"), this.bellServiceConnection,
					BIND_AUTO_CREATE);
		}
	}
	
	// 安装猫眼后启动门铃服务
//	public void checkBindKitService(){
//		if(iKitControl == null) {
//			Intent intent = new Intent();
//			intent.setAction("android.intent.action.binder_pool");
//			intent.setPackage("com.jinxin.jxsmartkit");
//			this.bindService(intent, kitServiceConnection, Context.BIND_AUTO_CREATE);
//		}
//	}
	
	/**
	 * 获取门铃状态
	 */
	public int getBellState() {
		if(iBellService == null) {
			try {
				return iBellService.isConnected();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	private IKitControl iKitControl;
//
//	private ServiceConnection kitServiceConnection = new ServiceConnection() {
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			IBinderPool iBinderPool = IBinderPool.Stub.asInterface(service);
//			try {
//				IBinder binder = iBinderPool.queryBinder(BINDER_KIT);
//				iKitControl = IKitControl.Stub.asInterface(binder);
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			iKitControl = null;
//		}
//	};
//	
//	public void loginKit(String account, String password, String appId, String appKey) {
//		try {
//			if (iKitControl == null) {
//				checkBindKitService();
//			}
//			iKitControl.login(account, password, appId, appKey);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void killKit() {
//		try {
//			if (iKitControl != null)
//				iKitControl.exit();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
}
