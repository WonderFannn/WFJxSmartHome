package com.jinxin.jxsmarthome.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.BuildConfig;

/**
 * 字符串操作工具包
 * 
 * @author hujunyu
 * @version 1.0
 * @created 2013-8-22
 */
public class StringUtils {
	private static final String HEX_STRING_FLAG = "0x";
	private static final String OCT_STRING_0 = "0";
	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	// private final static SimpleDateFormat dateFormater = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// private final static SimpleDateFormat dateFormater2 = new
	// SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * RGB转HSB
	 * @param rgbR
	 * @param rgbG 范围 0-255
	 * @param rgbB 范围 0-255
	 * @return
	 */
	public static float[] rgb2hsb(int rgbR, int rgbG, int rgbB) {  
		if(0 <= rgbR && rgbR <= 255 && 0 <= rgbG && rgbG <= 255 && 0 <= rgbB && rgbB <= 255) {
		    int[] rgb = new int[] { rgbR, rgbG, rgbB };  
		    Arrays.sort(rgb);  
		    int max = rgb[2];  
		    int min = rgb[0];  
		  
		    float hsbB = max / 255.0f;  
		    float hsbS = max == 0 ? 0 : (max - min) / (float) max;  
		  
		    float hsbH = 0;  
		    if (max == rgbR && rgbG >= rgbB) {  
		        hsbH = (rgbG - rgbB) * 60f / (max - min) + 0;  
		    } else if (max == rgbR && rgbG < rgbB) {  
		        hsbH = (rgbG - rgbB) * 60f / (max - min) + 360;  
		    } else if (max == rgbG) {  
		        hsbH = (rgbB - rgbR) * 60f / (max - min) + 120;  
		    } else if (max == rgbB) {  
		        hsbH = (rgbR - rgbG) * 60f / (max - min) + 240;  
		    }  
		    return new float[] { hsbH/360*254, hsbS*254/100, hsbB*255/100 };
		}
		return new float[] {0, 0, 0};
	}
	
//	public static float[] rgb2hsb(int rgbR, int rgbG, int rgbB) {  
//	    assert 0 <= rgbR && rgbR <= 255;  
//	    assert 0 <= rgbG && rgbG <= 255;  
//	    assert 0 <= rgbB && rgbB <= 255;  
//	    int[] rgb = new int[] { rgbR, rgbG, rgbB };  
//	    Arrays.sort(rgb);  
//	    int max = rgb[2];  
//	    int min = rgb[0];  
//	  
//	    float hsbB = max / 255.0f;  
//	    float hsbS = max == 0 ? 0 : (max - min) / (float) max;  
//	  
//	    float hsbH = 0;  
//	    if (max == rgbR && rgbG >= rgbB) {  
//	        hsbH = (rgbG - rgbB) * 60f / (max - min) + 0;  
//	    } else if (max == rgbR && rgbG < rgbB) {  
//	        hsbH = (rgbG - rgbB) * 60f / (max - min) + 360;  
//	    } else if (max == rgbG) {  
//	        hsbH = (rgbB - rgbR) * 60f / (max - min) + 120;  
//	    } else if (max == rgbB) {  
//	        hsbH = (rgbR - rgbG) * 60f / (max - min) + 240;  
//	    }  
//	  
//	    return new float[] { hsbH, hsbS, hsbB };  
//	}  
	
	public static String rgbToHsvJson(int rgbR, int rgbG, int rgbB){
		String color = intToString(rgbR) + 
				intToString(rgbG) +intToString(rgbB);
		float[] hsv = rgb2hsb(rgbR, rgbG, rgbB);
		JSONObject  jb = new JSONObject();
		try {
			jb.put("hue", Math.round(hsv[0]));
			jb.put("sat", Math.round((hsv[1]*100)));
			jb.put("light", Math.round((hsv[2]*100)));
			jb.put("color", color);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jb.toString();
	}
	
	/**
	 * 炫彩灯光参数设置
	 * @return
	 */
	public static String rgbToHsvJsonForAutoMode(){
		JSONObject  jb = new JSONObject();
		try {
			jb.put("hue", 0);
			jb.put("sat", 254);
			jb.put("light", 1);
			jb.put("time", 5);
			jb.put("step", 45);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jb.toString();
	}
	
	private static String intToString(int rgb){
		String str = "";
		if (rgb < 10) {
			str = "00"+rgb;
		}else if(rgb < 100){
			str = "0" + rgb;
		}else{
			str = Integer.toString(rgb);
		}
		return str;
	}
	  
	public static int[] hsb2rgb(float h, float s, float v) {  
	    if(Float.compare(h, 0.0f) >= 0 && Float.compare(h, 360.0f) <= 0  
	    		&& Float.compare(s, 0.0f) >= 0 && Float.compare(s, 1.0f) <= 0 
	    		&& Float.compare(v, 0.0f) >= 0 && Float.compare(v, 1.0f) <= 0) {  
	  
	    float r = 0, g = 0, b = 0;  
	    int i = (int) ((h / 60) % 6);  
	    float f = (h / 60) - i;  
	    float p = v * (1 - s);  
	    float q = v * (1 - f * s);  
	    float t = v * (1 - (1 - f) * s);  
	    switch (i) {  
	    case 0:  
	        r = v;  
	        g = t;  
	        b = p;  
	        break;  
	    case 1:  
	        r = q;  
	        g = v;  
	        b = p;  
	        break;  
	    case 2:  
	        r = p;  
	        g = v;  
	        b = t;  
	        break;  
	    case 3:  
	        r = p;  
	        g = q;  
	        b = v;  
	        break;  
	    case 4:  
	        r = t;  
	        g = p;  
	        b = v;  
	        break;  
	    case 5:  
	        r = v;  
	        g = p;  
	        b = q;  
	        break;  
	    default:  
	        break;  
	    }  
	    return new int[] { (int) (r * 255.0), (int) (g * 255.0),  
	            (int) (b * 255.0) }; 
	    }
	    return new int[] {0, 0, 0};
	}  
	
	public static String integerToHexString( Integer i ){
		String temp = Integer.toHexString( i );
		return HEX_STRING_FLAG
				+ ((temp.length() < 2) ? (OCT_STRING_0 + temp) : temp);
	}

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMddHHmmss");
		}
	};

	//
	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return format 'yyyyMMddHHmmss'
	 */
	// public static String toString3(String sdate) {
	// try {
	// return dateFormater3.get().parse(sdate).toString();
	// } catch (ParseException e) {
	// return null;
	// }
	// }

	public static String getSystemCurrentTime() {
		Calendar rightNow = Calendar.getInstance();
		return dateFormater3.get().format(rightNow.getTime());
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		String paramDate = dateFormater2.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = dateFormater2.get().format(time);
		}
		return ftime;
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.get().format(today);
			String timeDate = dateFormater2.get().format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static void printHexString(String hint, byte[] b) {
		System.out.print(hint);
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println("");
	}

	/**
	 * 十六进制字符串转十进制字符串
	 * @param hexStr
	 * @return string
	 */
	public static String HexStringToDec(String hexStr){
		return Integer.valueOf(hexStr, 16).toString();
	}
}
