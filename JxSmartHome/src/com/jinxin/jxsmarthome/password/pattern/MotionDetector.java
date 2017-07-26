package com.jinxin.jxsmarthome.password.pattern;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.telnet.TelnetClient;

import xgzx.VeinUnlock.VeinLogin;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.LoginActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.main.JxshActivity;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * @author Huang
 * @company 金鑫智慧
 */
public final class MotionDetector extends Service {

	private static final int FIXED_THREAD_NUM = 2;
	private static final int CHECK_CONNECTION_TO_SERVER_DURATION = 15;
	private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(FIXED_THREAD_NUM);
	private static final int NOT_CONNECTABLE_THRESHOLD = 3;

	private int notConnectableCount = 0;
	private boolean isRunning = false;

	@Override
	public void onCreate() {
		super.onCreate();
		isRunning = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRunning) {
					String _account = CommUtil.getCurrentLoginAccount();
					CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(MotionDetector.this);
					List<CloudSetting> csList;
					int peroid = parsePeroid(ControlDefine.DEFAULT_NINE_DELAY);
					csList = csDaoImpl.find(null, "customer_id=? and items='lineLockDelay'", new String[] { _account },
							null, null, null, null);
					if (csList != null && csList.size() > 0) {
						peroid = parsePeroid(csList.get(0).getParams());
					}
					try {
						Thread.sleep(1000 * peroid);
					} catch (InterruptedException e) {
					}

					long lastTouch = SharedDB.loadLongFromDB(_account, ControlDefine.KEY_LATEST_TOUCH_TIME);
					long now = System.currentTimeMillis();
					boolean enable = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_PATTERN,
							false);
					if (isLogin(MotionDetector.this) && enable
							&& !UIMonitor.isApplicationBroughtToBackground(MotionDetector.this)
							&& (lastTouch + peroid * 1000) < now) {
						startActivity(PatternActivity.getVerifyIntent(MotionDetector.this));
					}
				}
			}
		}).start();

		// check connectable to server
		threadPool.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				boolean connectable = checkConnectableToServer();
				if (connectable) {
					resetNotConnectableCount();
				} else if (++notConnectableCount == NOT_CONNECTABLE_THRESHOLD) {
					notifyUserSwitchToOfflineMode();
					resetNotConnectableCount();
				}
			}
		}, 15, CHECK_CONNECTION_TO_SERVER_DURATION, TimeUnit.SECONDS);
	}

	private boolean checkConnectableToServer() {
		boolean connectable = false;

		TelnetClient telnet = new TelnetClient();
		String[] array = DatanAgentConnectResource.HOST.split("\\:");
		if (array.length == 2) {
			try {
				telnet.setConnectTimeout(3000);
				telnet.connect(array[0], Integer.parseInt(array[1]));
				telnet.disconnect();
				connectable = true;
			} catch (Exception e) {
				return connectable;
			}
		}

		return connectable;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public boolean isLogin(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getClassName().equals(LoginActivity.class.getName())
					&& !topActivity.getClassName().equals(JxshActivity.class.getName())
					// &&
					// !topActivity.getClassName().equals(VeinEnroll.class.getName())
					&& !topActivity.getClassName().equals(VeinLogin.class.getName())) {
				return true;
			}
		}
		return false;

	}

	private int parsePeroid(String fTime) {
		String minute = getResources().getString(R.string.unit_minute);
		String second = getResources().getString(R.string.unit_second);
		if (fTime.endsWith(minute)) {
			int val = Integer.parseInt(fTime.replace(minute, "").replace(" ", ""));
			val = val * 60;
			return val;
		} else {
			int val = Integer.parseInt(fTime.replace(second, "").replace(" ", ""));
			return val;
		}
	}

	private void resetNotConnectableCount() {
		notConnectableCount = 0;
	}

	private void notifyUserSwitchToOfflineMode() {
		String account = CommUtil.getCurrentLoginAccount();
		boolean isLogin = false;
		boolean isOffMode = false;
		boolean isNotFirst = false;
		if (account != null && account != "") {
			isLogin = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_ISLOADING, false);
			isOffMode = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_OFF_LINE_MODE, false);
			isNotFirst = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_NOT_FIRST_LOGING, false);
		}
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getApplicationContext());
		boolean isEnableOfflineSwitch;
		List<CloudSetting> csList = csDaoImpl.find(null, "items = ?",
				new String[]{StaticConstant.PARAM_OFFLINE_SWITCH}, null, null, null, null);
		if (csList!= null && csList.size() > 0) {
			CloudSetting cs = csList.get(0);
			isEnableOfflineSwitch =  Boolean.valueOf(cs.getParams());
		}else{
			isEnableOfflineSwitch = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_ENABLE_OFFLINE_MODE, true);
		}
		if(isEnableOfflineSwitch) {
			if (isLogin && !isOffMode) {//登录之后 弹出提示
				//发送消息到主线程弹出提示对话框
				Bundle bundle = new Bundle();
				bundle.putString("modeOff", "启用离线模式");
				BroadcastManager.sendBroadcast(BroadcastManager.ACTION_CHANGE_OFF_LINE_MODE, bundle);
			}else if (!isLogin && isNotFirst) {
				List<String> list = JxshApp.getHistoryUserList();
				if (list != null && list.size() > 0) {
					for (String string : list) {
						if (string.equals(JxshApp.lastInputID)) {
							//登录界面 弹出提示
							Bundle bundle = new Bundle();
							bundle.putString("modeOff", "启用离线模式");
							Logger.debug(null, "sendBroadcast ACTION_CHANGE_OFF_LINE_MODE");
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		isRunning = false;
		threadPool.shutdownNow();
		super.onDestroy();
	}
	
}
