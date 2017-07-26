package com.jinxin.jxsmarthome.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.test.AndroidTestCase;

import com.jinxin.jxsmarthome.service.TimerManager;
import com.jinxin.jxsmarthome.util.DateUtil;

public class TimerTest extends AndroidTestCase {
	public void testWeekparse() {
		String weeks = "1,2,3,4";
		int[] ws = TimerManager.parseWeekDays(weeks);
		for(int i : ws) {
			System.out.println("-->" + i );
		}
	}
	
	public void testTimeparse() {
		String startTime = "13:08";
		Date d = TimerManager.parseTime(startTime);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
		String dateString = formatter.format(d);
		System.out.println("-->" + dateString);
	}
	
	public void testGetNextExecuteTime() {
		int repeatType = TimerManager.REPEAT_TYPE_WEEK;
		String dateValue = "0,1,2,3,4,5,6";
		String startTime = "13:08:00";
		
		long triggerTime = TimerManager.getNextExecuteTime(repeatType, dateValue, startTime);
		
		System.out.println("-->" + triggerTime);
		
		Date date = new Date(triggerTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		String dateString = formatter.format(date);
		System.out.println("-->" + dateString);
	}
	
	public void testTime(){
		String currentDay = "2014-06-01 12:12:12";
		System.out.println("Time:"+DateUtil.convertStrToLongNew(currentDay));
	}
	
}
