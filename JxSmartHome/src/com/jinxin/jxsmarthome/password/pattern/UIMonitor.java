package com.jinxin.jxsmarthome.password.pattern;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class UIMonitor extends BroadcastReceiver {

	public static final String ACTION_ACTIVITY_STOP = "com.jinxin.jxsmarthome.activity_stop";
	private static volatile boolean uiVisible = false;//应用UI是否可见（含锁屏状态）
	
	public static boolean isUiBackgrounded() {
		return uiVisible;
	}
	
	public static void setUiBackgrounded(boolean uiVisible) {
		UIMonitor.uiVisible = uiVisible;
	}
	
	/**
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_ACTIVITY_STOP)) {
			if (isApplicationBroughtToBackground(context)) {
				setUiBackgrounded(true);
			} else {
				setUiBackgrounded(false);
			}
		} 

	}
	
	/**
	 * 应用是否可见
	 * @param context
	 * @return
	 */
	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isBackground(Context context) {

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

}
