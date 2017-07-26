package com.jinxin.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.jxsmarthome.main.JxshApp;


/**
 * 配置文件存储器
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class SharedDB {
	/**
	 * 配置表
	 */
	public static final String CONFIGURATION = "configuration";
	/**
	 * 存放普通常量
	 */
	public static final String ORDINARY_CONSTANTS = "ordinary_constants";

	/**
	 * 保存字符串
	 * 
	 * @param name
	 *            存放配置文件名称
	 * @param key
	 * @param value
	 */
	public static void saveStrDB(String name, String key, String value) {
		saveStrDB(JxshApp.getContext(),name, key, value);
	}
	public static void saveIntDB(String name, String key, int value) {
		saveIntDB(JxshApp.getContext(),name, key, value);
	}
	
	public static void emptyStrDB(String name, String key) {
		saveStrDB(JxshApp.getContext(),name, key, "");
	}
	
	/**
	 * 保存字符串列表
	 * 
	 * @param name
	 *            存放配置文件名称
	 * @param key
	 * @param value
	 */
	public static void saveStrListDB(String name, String key, String value) {
		List<String> list = loadStrListFromDB(name,key,"");
		if(!list.contains(value)) {
			list.add(value);
			saveStrDB(JxshApp.getContext(),name, key, listToString(list,'|'));
		} 
	}

	/**
	 * 字符串化List
	 * @param list
	 * @param separator
	 * @return
	 */
	private static String listToString(List<String> list, char separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(separator);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	/**保存boolean型
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void saveBooleanDB(String name, String key, boolean value) {
		saveBooleanDB(JxshApp.getContext(),name, key, value);
	}
	
	/**
	 * 取字符串List
	 * 
	 * @param name
	 *            存放配置文件名称
	 * @param key
	 */
	public static List<String> loadStrListFromDB(String name, String key,String defaultValue) {
		String val = loadStrFromDB(JxshApp.getContext(), name, key, defaultValue);
		List<String> list = new ArrayList<String>();
		String[] array = val.split("\\|");
		for (String item : array) {
			if (item != null && !item.equals("")) {
				list.add(item);
			}
		}
		return list;
	}
	/**
	 * 取字符串存档
	 * 
	 * @param name
	 *            存放配置文件名称
	 * @param key
	 */
	public static String loadStrFromDB(String name, String key,String defaultValue) {
		return loadStrFromDB(JxshApp.getContext(), name, key,defaultValue);
	}

	public static boolean loadBooleanFromDB(String name, String key,boolean defaultValue) {
		return loadBooleanFromDB(JxshApp.getContext(), name, key,defaultValue);
	}
	
	public static int loadIntFromDB(String name, String key, int defaultValue) {
		return loadIntFromDB(JxshApp.getContext(), name, key,defaultValue);
	}
	
	/**
	 * 保存长整形
	 * 
	 * @param name
	 *            存放配置文件名称
	 * @param key
	 * @param value
	 */
	public static void saveDB(String name, String key, long value) {
		saveDB(JxshApp.getContext(), name, key, value);
	}
	
	/**
	 * 保存整形
	 * 
	 * @param name
	 *            存放配置文件名称
	 * @param key
	 * @param value
	 */
	public static void saveDB(String name, String key, int value) {
		saveDB(JxshApp.getContext(), name, key, value);
	}

	/**
	 * 取整形存档
	 * 
	 * @param name
	 *            存放配置文件名称
	 * @param key
	 */
	public static int loadFromDB(String name, String key) {
		return loadFromDB(JxshApp.getContext(), name, key);
	}
	
	/**
	 * 取长整形存档
	 * 
	 * @param name
	 *            存放配置文件名称
	 * @param key
	 */
	public static long loadLongFromDB(String name, String key) {
		return loadLongFromDB(JxshApp.getContext(), name, key);
	}

	private static void saveDB(Context context, String name, String key, long value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(name, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putLong(key, value);
			editor.commit();
			
		} catch (Exception ex) {
			Logger.error("SharedDB", ex.toString());
			
		} finally {
			try {
			} catch (Exception ex) {
				Logger.error("SharedDB", ex.toString());
			}
		}
	}
	
	private static void saveDB(Context context, String name, String key, int value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(name, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt(key, value);
			editor.commit();

		} catch (Exception ex) {
			Logger.error("SharedDB", ex.toString());

		} finally {
			try {
			} catch (Exception ex) {
				Logger.error("SharedDB", ex.toString());
			}
		}
	}

	private static int loadFromDB(Context context, String name, String key) {
		SharedPreferences sp = context.getSharedPreferences(name, 0);
		// String value = sp.getString(key, "0");
		if (sp == null)
			return 0;
		int value = sp.getInt(key, 0);
		// return Integer.parseInt(value);
		return value;

	}
	
	private static long loadLongFromDB(Context context, String name, String key) {
		SharedPreferences sp = context.getSharedPreferences(name, 0);
		// String value = sp.getString(key, "0");
		if (sp == null)
			return 0;
		long value = sp.getLong(key, 0);
		// return Integer.parseInt(value);
		return value;
		
	}

	private static void saveBooleanDB(Context context, String name, String key, boolean value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(name, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(key, value);
			editor.commit();

		} catch (Exception ex) {
			Logger.error("SharedDB", ex.toString());

		} finally {
			try {
			} catch (Exception ex) {
				Logger.error("SharedDB", ex.toString());
			}
		}
	}
	
	private static void saveStrDB(Context context, String name, String key, String value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(name, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(key, value);
			editor.commit();

		} catch (Exception ex) {
			Logger.error("SharedDB", ex.toString());

		} finally {
			try {
			} catch (Exception ex) {
				Logger.error("SharedDB", ex.toString());
			}
		}
	}
	private static void saveIntDB(Context context, String name, String key, int value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(name, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt(key, value);
			editor.commit();
			
		} catch (Exception ex) {
			Logger.error("SharedDB", ex.toString());
			
		} finally {
			try {
			} catch (Exception ex) {
				Logger.error("SharedDB", ex.toString());
			}
		}
	}

	/**取boolean型
	 * @param context
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static boolean loadBooleanFromDB(Context context, String name, String key,boolean defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(name, 0);
		// String value = sp.getString(key, "0");
		boolean value = sp.getBoolean(key, defaultValue);
		// return Integer.parseInt(value);
		return value;

	}
	
	private static String loadStrFromDB(Context context, String name, String key,String defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(name, 0);
		// String value = sp.getString(key, "0");
		String value = sp.getString(key, defaultValue);
		// return Integer.parseInt(value);
		return value;

	}
	
	private static int loadIntFromDB(Context context, String name, String key, int defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(name, 0);
		// String value = sp.getString(key, "0");
		if(sp != null) {
			return sp.getInt(key, defaultValue);
		}
		// return Integer.parseInt(value);
		return -1;

	}
	
	/**清除配置表
	 * @param context
	 * @param name
	 */
	public static void removeAct(Context context,String name){
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}
}
