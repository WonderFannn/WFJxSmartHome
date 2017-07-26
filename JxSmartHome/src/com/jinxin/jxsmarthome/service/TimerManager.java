package com.jinxin.jxsmarthome.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.TimeUtils;

import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.entity.CustomerVoiceConfig;
import com.jinxin.jxsmarthome.util.DateUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 定时器管理类
 * 		定时功能的管理，如：设置和取消定时任务,获取下一次执行的时间
 * @author TangLong
 * @company 金鑫智慧
 */
public class TimerManager {
	public static final String ACTION_TIMER_CANCEL = "com.jinxin.jxsmarthome.ACTION_TIMER_CANCEL";
	public static final String ACTION_TIMER_ADD = "com.jinxin.jxsmarthome.ACTION_TIMER_ADD";
	public static final String ACTION_TIMER_MODIFY = "com.jinxin.jxsmarthome.ACTION_TIMER_MODIFY";
	public static final int REPEAT_TYPE_WEEK = 0;
	public static final int REPEAT_TYPE_WORK_DAY = 1;
	public static final int REPEAT_TYPE_TODAY = 2;
	public static final int REPEAT_TYPE_DEFINE = 3;
	public static final int REPEAT_TYPE_TEST = 4;
	public static final String DATE_FORMAT_TINY = "HH:mm:ss";
	
	/**
	 * 发送定时器任务删除的广播
	 * @param context
	 */
	public static void sendTimerCancelBroadcast(Context context, CustomerTimer ct) {
		Logger.debug(null, "sendTimerCancelBroadcast");
		Intent intent = new Intent(ACTION_TIMER_CANCEL);
		intent.putExtra("timer", ct);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发送定时任务增加的广播
	 * @param context
	 * @param ct
	 */
	public static void sendTimerAddBroadcast(Context context, CustomerTimer ct) {
		Logger.debug(null, "sendTimerAddBroadcast");
		Intent intent = new Intent(ACTION_TIMER_ADD);
		intent.putExtra("timer", ct);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发送定时任务修改的广播
	 * @param context
	 * @param ct
	 */
	public static void sendTimerModifyBroadcast(Context context, CustomerTimer ct) {
		Logger.debug(null, "sendTimerModifyBroadcast");
		Intent intent = new Intent(ACTION_TIMER_MODIFY);
		intent.putExtra("timer", ct);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发送语音定时任务删除的广播
	 * @param context
	 */
	public static void sendVoiceTimerCancelBroadcast(Context context, CustomerVoiceConfig cvc){
		Logger.debug(null, "sendVoiceTimerCancelBroadcast");
		Intent intent = new Intent(BroadcastManager.ACTION_VOICE_TIMER_CANCLE);
		intent.putExtra("voiceTimer", cvc);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发送语音定时任务增加的广播
	 * @param context
	 * @param ct
	 */
	public static void sendVoiceTimerAddBroadcast(Context context, CustomerVoiceConfig cvc) {
		Logger.debug(null, "sendVoiceTimerAddBroadcast");
		Intent intent = new Intent(BroadcastManager.ACTION_VOICE_TIMER_ADD);
		intent.putExtra("voiceTimer", cvc);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发送语音定时任务修改的广播
	 * @param context
	 * @param ct
	 */
	public static void sendVoiceTimerModifyBroadcast(Context context, CustomerVoiceConfig cvc) {
		Logger.debug(null, "sendTimerModifyBroadcast");
		Intent intent = new Intent(BroadcastManager.ACTION_VOICE_TIMER_AMEND);
		intent.putExtra("voiceTimer", cvc);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 得到下一次定时任务执行的时间
	 * @param repeatType		定时周期类型：REPEAT_TYPE_WEEK(周)
	 * @param dateValue			定时任务执行日期：0, 1, 2(周日   - 周六：0-6)
	 * @param startTime			定时任务开始日期：09:00
	 * @return					任务执行的时间
	 */
	@SuppressWarnings("deprecation")
	public static long getNextExecuteTime(int repeatType, String dateValue, String startTime) {
		if(dateValue == null || startTime == null) {
			Logger.error("TimerManager", "paramters format error");
			return -1;
		}
		
		final Calendar cal = Calendar.getInstance();
		final long nowTime = System.currentTimeMillis();
		
		Date fmtStartTime = parseTime(startTime);
		
		if(fmtStartTime == null) {
//			Logger.error("TimerManager", "startTime format error");
			return -1;
		}
		
		long nextTime = 0;
		
		if(REPEAT_TYPE_WEEK == repeatType || REPEAT_TYPE_WORK_DAY == repeatType
				|| REPEAT_TYPE_DEFINE == repeatType) {
			int days[] = parseWeekDays(dateValue);
			
			if(days != null) {
				for(int day : days) {
					cal.set(Calendar.DAY_OF_WEEK, day);
					cal.set(Calendar.HOUR_OF_DAY, fmtStartTime.getHours());
					cal.set(Calendar.MINUTE, fmtStartTime.getMinutes());
					cal.set(Calendar.SECOND, fmtStartTime.getSeconds());
					long triggerTime = cal.getTimeInMillis();
					
					
					if(nowTime >= triggerTime) {
						triggerTime += AlarmManager.INTERVAL_DAY * 7;
					}
					if(nextTime == 0) {
						nextTime = triggerTime;
					}else {
						nextTime = Math.min(triggerTime, nextTime);
					}
					
				}
			}
		}else if(REPEAT_TYPE_TODAY == repeatType) {
			String date = dateValue + " " + startTime;
			nextTime = parseStrToLong(date);
			// Add by Huang for HOMEAPP-328
			if (nextTime + 59 * 1000 < System.currentTimeMillis()) {
				nextTime = -1;
			}
		}else if(REPEAT_TYPE_TEST == repeatType) {
			
		}
		
		return nextTime;
	}
	
	/**
	 * 设置定时任务
	 * @param context
	 * @param requestCode
	 * @param triggerTime
	 * @param ct
	 */
	public static void setTimeTask(Context context, int requestCode, long triggerTime, CustomerTimer ct) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent();
		intent.setAction("com.jinxin.jxsmarthome.ACTION_TIMER");
		intent.putExtra("timer", ct);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 
				requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Logger.debug(null, "TimerManager:"+DateUtil.convertLongToStr1(triggerTime));
		
		am.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
	}
	
	/**
	 * 删除定时任务
	 * @param context
	 * @param requestCode
	 */
	public static void cancelTimeTask(Context context, int requestCode) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent();
		intent.setAction("com.jinxin.jxsmarthome.ACTION_TIMER");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 
				requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pendingIntent);
	}
	
	/**
	 * 设置语音定时任务
	 * @param context
	 * @param requestCode
	 * @param triggerTime
	 * @param cvc
	 */
	public static void setVoiceTimeTask(Context context,int requestCode, long triggerTime,CustomerVoiceConfig cvc){
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent();
		intent.setAction(BroadcastManager.ACTION_DO_VOICE_TIMER);
		intent.putExtra("voiceTimer", cvc);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 
				requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Logger.debug(null, DateUtil.convertLongToStr1(triggerTime));
		
		am.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
	}
	
	/**
	 * 取消语音定时任务
	 * @param context
	 * @param requestCode
	 */
	public static void cancleVoiceTimerTask(Context context, int requestCode){
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent();
		intent.setAction(BroadcastManager.ACTION_DO_VOICE_TIMER);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 
				requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pendingIntent);
	}
	
	/**
	 * 转换定时器中设置的开始时间
	 * @param startTime		待转换的日期
	 * @return				转换后的Date对象
	 */
	public static Date parseTime(String startTime) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_TINY, Locale.US);
		try {
			return dateFormat.parse(startTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 转换定时器中设置的重复模式为周的日期
	 * @param weekDays		待转换的日期
	 * @return				转换后的日期
	 */
	public static int[] parseWeekDays(String weekDays) {
		int days[] = null;
		if(weekDays != null) {
			String[] items = weekDays.split(",");
			days = new int[items.length];
			int i = 0;
			for(String s : items) {
				days[i] = Integer.valueOf(s);
				i++;
			}
		}
		return days;
	}
	
	/**
	 * 转换格式为：2004-09-20 09:00:00的字符串为long类型的日期
	 * @param date		2004-09-20 09:00:00 格式日期字符串
	 * @return			转换后的long日期
	 */
	@SuppressLint("SimpleDateFormat")
	public static long parseStrToLong(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = formatter.parse(date);
		} catch (ParseException e) {
			return -1;
		}
		return d.getTime();
	}
	
	/**
	 * 转换long类型的日期为2004-09-20 09:00:00格式的字符串为
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String parseLongToStr(long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date(date);
		return formatter.format(d);
	}
}
