package com.jinxin.jxsmarthome.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.record.SharedDB;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 常用方法
 * 
 * @author zj
 */
public class CommUtil {
	private static boolean isAllowChange = true;

	public static boolean isAllowChange() {
		return isAllowChange;
	}

	public static void setAllowChange(boolean isAllowChange) {
		CommUtil.isAllowChange = isAllowChange;
	}

	/**
	 * activity跳转
	 * 
	 * @param activity
	 * @param cls
	 * @param mBundle
	 *            传参
	 * @param doNotSave
	 *            是否不保存上个界面（true不保存，false保存）
	 * @param screenState
	 *            界面管理处理状态
	 */
	public static void changeActivity(Activity activity, Class<?> cls,
			Bundle mBundle, boolean doNotSave, int screenState) {
		if (!isAllowChange())
			return;
		setAllowChange(false);
		Intent intent = new Intent(activity, cls);
		if (mBundle != null) {
			intent.putExtras(mBundle);
		}
		// ////////////////////zj20131011停用loading
		// if (EddhcApp.getPd() != null && EddhcApp.getPd().isShowing()) {
		// EddhcApp.getPd().dismiss();
		// EddhcApp.setPd(null);
		// }
		// //////////////////
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		activity.finish();
		activity.overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		switch (screenState) {
		case ScreenManager.ADD_NEW_SCREEN:
			if (!doNotSave) {
				CommDefines.getScreenManager().pushActivityClass(
						activity.getClass());
			}
			break;
		case ScreenManager.CLEAR_ALL_SCREEN:
			CommDefines.getScreenManager().popAllActivityClass();
			if (!doNotSave) {
				CommDefines.getScreenManager().pushActivityClass(
						activity.getClass());
			}
			break;
		case ScreenManager.CHECK_CURRENT_SCREEN:
			if (CommDefines.getScreenManager().isExist(cls)) {
				CommDefines.getScreenManager().popAllActivityIncludeOne(cls);
				// ScreenManager.getScreenManager().removeActivity(ScreenManager.getScreenManager().currentActivity());
				// if (!isFinish) {
				// ScreenManager.getScreenManager().insertActivity(activity,
				// ScreenManager.getScreenManager().getStackSize() - 1);
				// }
			} else {
				if (!doNotSave) {
					CommDefines.getScreenManager().pushActivityClass(
							activity.getClass());
				}
			}
			break;
		case ScreenManager.CHECK_SAVING_SCREEN:
			if (CommDefines.getScreenManager().isExist(activity.getClass())) {
				CommDefines.getScreenManager().popAllActivityExceptOne(
						activity.getClass());
				// ScreenManager.getScreenManager().removeActivity(ScreenManager.getScreenManager().currentActivity());
				if (doNotSave) {
					CommDefines.getScreenManager().popActivityClass(
							activity.getClass());
				}
			} else {
				if (!doNotSave) {
					CommDefines.getScreenManager().pushActivityClass(
							activity.getClass());
				}
			}
			break;
		}
	}

	/**
	 * activity跳转
	 * 
	 * @param activity
	 * @param cls
	 * @param doNotSave
	 *            是否不保存上个界面（true不保存，false保存）
	 * @param screenState
	 *            界面管理处理状态
	 */
	public static void changeActivity(Activity activity, Class<?> cls,
			boolean doNotSave, int screenState) {
		changeActivity(activity, cls, null, doNotSave, screenState);
	}

	/**
	 * 清除指定保留数量上面的所有Activity历史
	 * 
	 * @param num
	 *            保留的历史数量
	 */
	public static void clearActivityHistoryAfterNum(int num) {
		CommDefines.getScreenManager().popAllActivityExceptNum(num);
	}

	/**
	 * 回退activity（当前activity不被保存，且记录器删除该回退activity.class轨迹）
	 * 
	 * @param activity
	 *            当前activity
	 */
	public static void goBackActivity(Activity activity) {
		Class<?> cls = CommDefines.getScreenManager().currentActivityClass();
		if (cls != null) {
			setAllowChange(true);
			changeActivity(activity, cls, true, ScreenManager.DEFAULT);
			CommDefines.getScreenManager().popActivityClass(cls);
		} else {
			// 返回首页（360等清空内存等情况）
			setAllowChange(true);
			changeActivity(activity, null/* HappyHouseActivity.class */, true,
					ScreenManager.CLEAR_ALL_SCREEN);
		}
	}

	/**
	 * 从Activity跳转到WebView
	 * 
	 * @param context
	 *            当前Activity
	 * @param url
	 *            Web地址
	 */
	// public static void changeToWebView(Activity context, String url, boolean
	// isFinish) {
	// ControllerDefines.currentUrl = url;
	// changeActivity(context, WebViewActivity.class, isFinish);
	// }

	/**
	 * 确认是否退出软件
	 */
	// public static void exit(Context context) {
	// final Context _context = context;
	// final MessageBox messageBox = new MessageBox(context, "提示消息", "是否退出系统",
	// MessageBox.MB_OK | MessageBox.MB_CANCEL);
	// messageBox.setOnDismissListener(new OnDismissListener() {
	// @Override
	// public void onDismiss(DialogInterface dialog) {
	// int result = messageBox.getResult();
	// if (result == MessageBox.MB_OK) {
	// kill(_context);
	// }
	// }
	// });
	// messageBox.show();
	// }
	public static void kill(final Context context) {
		// EddhcApp.instance.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * 保存成功dialog (无界面跳转的)
	 * 
	 * @param context
	 * @param content
	 *            提示语
	 */
	// public static void SucceedDialog(Context context, String content) {
	// csbd = new ConfirmSingleButtonDialog(context, content, new
	// OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// csbd.dismiss();
	// }
	//
	// });
	// csbd.show();
	// }

	// private static ConfirmSingleButtonDialog csbd = null;

	/**
	 * dialog(有界面跳转的)
	 * 
	 * @param context
	 * @param content
	 *            提示语
	 * @param cls
	 *            跳转的Activity
	 * @param url
	 *            跳转的URL
	 */
	// public static void SucceedDialog(final Activity context, String content,
	// final Class<?> cls, final String url) {
	// // ConfirmSingleButtonDialog _csbd = null;
	// csbd = new ConfirmSingleButtonDialog(context, content, new
	// OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// csbd.dismiss();
	// if (cls != null) {
	// changeActivity(context, cls, true);
	// } else if (url != null) {
	// changeToWebView(context, url, true);
	// }
	//
	// }
	// });
	// csbd.show();
	// }

	private static int autoIndex = 0;

	/**
	 * 设置累加器初始值
	 * 
	 * @param index
	 */
	public static void setAutoAddition(int index) {
		autoIndex = index;
	}

	/**
	 * 获取累加器当前值
	 * 
	 * @return
	 */
	public static int getAutoAddition() {
		return autoIndex++;
	}

	/**
	 * 判断字符串是否是整数
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 */
	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			if (value.contains("."))
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		return isInteger(value) || isDouble(value);
	}

	/**
	 * 字符串数字转整形
	 * 
	 * @param str
	 * @return 转换失败返回-1
	 */
	public static int StringToInt(String str) {
		if (str == null)
			return -1;
		String _temp = str.trim();
		if (_temp == null || _temp.equals(""))
			return -1;
		if (isNumber(_temp))
			return Integer.parseInt(_temp);
		else {
			return -1;
		}
	}

	/**
	 * 动态更新listView高度（保证listView子项全展开）
	 * 
	 * @param mListView
	 * @param adapter
	 */
	public static void updataListViewHeight(ListView mListView,
			SimpleAdapter adapter) {
		if (adapter == null || mListView == null)
			return;
		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View listItem = adapter.getView(i, null, mListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = mListView.getLayoutParams();
		params.height = totalHeight
				+ (mListView.getDividerHeight() * (adapter.getCount() - 1));
		// params.height += 5;//if without this statement,the listview will
		// be a little short
		mListView.setLayoutParams(params);

	}

	/**
	 * 展开listview,主要用于listview与ScallView冲突的情况
	 * 
	 * @param adapter
	 * @param listView
	 */
	public static void expandListView(BaseAdapter adapter, ListView listView) {
		// 统计高度
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1))
				+ listView.getPaddingTop() + listView.getPaddingBottom();
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/** 
	 * 获取当前在前台的activity
	 */  
	public static String getRunningActivityName(){            
		ActivityManager activityManager =
				(ActivityManager)JxshApp.instance.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);    
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();    
		return runningActivity;                   
	}    

	/**
	 * 展开listview,主要用于listview与ScallView冲突的情况
	 * 
	 * @param adapter
	 * @param listView
	 */
	public static void expandListView(BaseAdapter adapter, ListView listView,
			int height) {
		// 统计高度
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1))
				+ listView.getPaddingTop() + listView.getPaddingBottom()
				+ height;
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 展开GridView,主要用于GridVview与ScallView冲突的情况
	 * 
	 * @param adapter
	 * @param girdView
	 * @param numColumns
	 */
	public static void expandGridView(BaseAdapter adapter, GridView girdView,
			int numColumns) {
		int row = (adapter.getCount() + numColumns - 1) / numColumns;
		if (row != 0) {
			LayoutParams lp = girdView.getLayoutParams();
			View view = adapter.getView(0, null, girdView);
			view.measure(0, 0);
			lp.height = row * view.getMeasuredHeight();
			girdView.setLayoutParams(lp);
		}
	}

	/**
	 * 获取年份
	 * 
	 * @return
	 */
	public static int getYear() {
		Calendar ca = Calendar.getInstance();
		return ca.get(Calendar.YEAR);// 获取年份
	}

	/**
	 * 获取月份
	 * 
	 * @return
	 */
	public static int getMonth() {
		Calendar ca = Calendar.getInstance();
		return ca.get(Calendar.MONTH);// 获取月份
	}

	/**
	 * 获取日
	 * 
	 * @return
	 */
	public static int getDate() {
		Calendar ca = Calendar.getInstance();
		return ca.get(Calendar.DATE);// 获取日
	}

	/**
	 * 获取分
	 * 
	 * @return
	 */
	public static int getMinute() {
		Calendar ca = Calendar.getInstance();
		return ca.get(Calendar.MINUTE);// 获取分
	}

	/**
	 * 获取时
	 * 
	 * @return
	 */
	public static int getHour() {
		Calendar ca = Calendar.getInstance();
		return ca.get(Calendar.HOUR);// 获取时
	}

	/**
	 * 获取秒
	 * 
	 * @return
	 */
	public static int getSecond() {
		Calendar ca = Calendar.getInstance();
		return ca.get(Calendar.SECOND);// 获取秒
	}

	public static int[] getIntegerArrayFromJSONArray(String jsonarrayString)
			throws JSONException {
		if (jsonarrayString == null) {
			throw new JSONException("Null json array string!");
		}

		JSONArray jsarray = new JSONArray(jsonarrayString);
		int[] result = new int[jsarray.length()];
		for (int i = 0; i < jsarray.length(); ++i) {
			result[i] = jsarray.getInt(i);
		}

		return result;
	}

	public static String[] getStringArrayFromJSONArray(String jsonarrayString)
			throws JSONException {
		if (jsonarrayString == null) {
			throw new JSONException("Null json array string!");
		}

		JSONArray jsarray = new JSONArray(jsonarrayString);
		String[] result = new String[jsarray.length()];
		for (int i = 0; i < jsarray.length(); ++i) {
			result[i] = jsarray.getString(i);
		}

		return result;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 取屏幕宽度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getDisplayWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels; // 得到宽度
	}

	/**
	 * 取屏幕高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getDisplayHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels; // 得到高度
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		if (str == null || str.trim().length() <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查是否为null，若为空返回“”
	 * 
	 * @param str
	 * @return
	 */
	public static String checkNull(String str) {
		return isNull(str) ? "" : str.trim();
	}

	/**
	 * 将字符串转化为整数
	 * 
	 * @param str
	 *            要转的字符窜
	 * @return 如过正常则返回正常数据，出现异常返回-1；
	 */
	public static int changeToInteger(String str) {
		int i = -1;
		try {
			i = Integer.parseInt(str);
		} catch (Exception e) {
			Logger.error("字符串转化为整数出错", "字符串转化为整数出错");
		}

		return i;
	}

	/**
	 * APK安装
	 * 
	 * @param path
	 *            apk路径
	 */
	public static void installAPK(Context context, String path) {
		if (path == null || path.length() < 0)
			return;
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 判断对象o实现的所有接口中是否有szInterface 2008-08-07修正多继承中判断接口的功能， 以及修正接口继承后的判断功能
	 * packagetest;
	 * 
	 * publicinterfaceITestextendsSerializable publicclassTest1implementsITest
	 * publicclassTest2extendsTest1 publicclassTest3extendsTest2
	 * 
	 * isInterface(Test3.class,"java.io.Serializable")=true
	 * isInterface(Test3.class,"test.ITest")=true
	 * 
	 * @paramc
	 * @paramszInterface
	 * @return
	 */
	public static boolean isInterface(Class c, String szInterface) {
		Class[] face = c.getInterfaces();
		for (int i = 0, j = face.length; i < j; i++) {
			if (face[i].getName().equals(szInterface)) {
				return true;
			} else {
				Class[] face1 = face[i].getInterfaces();
				for (int x = 0; x < face1.length; x++) {
					if (face1[x].getName().equals(szInterface)) {
						return true;
					} else if (isInterface(face1[x], szInterface)) {
						return true;
					}
				}
			}
		}
		if (null != c.getSuperclass()) {
			return isInterface(c.getSuperclass(), szInterface);
		}
		return false;
	}

	/**
	 * 拨打电话
	 * 
	 * @param phone电话号码
	 */
	public static void call(Context con, String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_CALL, uri);
		// it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		con.startActivity(it);
	}

	/**
	 * 发送短信
	 * 
	 * @param phone电话号码
	 */
	public static void sendMsg(Context con, String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SENDTO);
		// 需要发短息的号码
		intent.setData(Uri.parse("smsto:" + phone));
		con.startActivity(intent);
	}

	/**
	 * 发送邮件
	 * 
	 * @param con
	 * @param adress
	 *            邮件地址
	 */
	public static void sendMail(Context con, String adress) {
		try {
			Uri uri = Uri.parse("mailto:" + adress);
			Intent MymailIntent = new Intent(Intent.ACTION_SEND, uri);
			MymailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			con.startActivity(MymailIntent);
		} catch (Exception ex) {
			Intent intent = new Intent();
			intent.setData(Uri
					.parse("content:/ui.email.android.com/view/mailbox"));
			con.startActivity(intent);
		}
	}

	/**
	 * 时间转换
	 * 
	 * @param time
	 * @return
	 */
	public static String parseTime(long time) {
		long hour = time / 3600;
		long min = time / 60;
		long sec = time % 60;
		String h = "";
		String m = "";
		String s = "";
		if (hour < 10) {
			h = "0" + hour;
		} else {
			h = hour + "";
		}
		if (min < 10) {
			m = "0" + min;
		} else {
			m = min + "";
		}

		if (sec < 10) {
			s = "0" + sec;
		} else {
			s = sec + "";
		}
		return h + ":" + m + ":" + s;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @param context
	 * @param packageName
	 *            包名
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context, String packageName) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		return info;
	}

	/**
	 * 检查网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean CheckNetwork(final Context context) {
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager.getActiveNetworkInfo() != null)
			flag = cwjManager.getActiveNetworkInfo().isAvailable();
		if (!flag) {
			Builder b = new AlertDialog.Builder(context).setTitle("状态")
					.setMessage("异常");
			b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent mIntent = new Intent(
							"android.settings.WIRELESS_SETTINGS");
					context.startActivity(mIntent);
				}
			}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			}).create();
			b.show();
		}

		return flag;
	}

	////////////////////////////////////////////////////////////////////////
	// add by tangl
	////////////////////////////////////////////////////////////////////////
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if(str == null || str.length() < 1) {
			return true;
		}
		return false;
	}

	/**
	 * 判断数组是否为空
	 * @param arr
	 * @return
	 */
	public static boolean isEmpty(Object[] arr) {
		if(arr == null || arr.length < 1) {
			return true;
		}
		return false;
	}

	/**
	 * 判断集合是否为空
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List<?> list) {
		if(list == null || list.size() < 1) {
			return true;
		}
		return false;
	}

	/**
	 * 根据用户名得到sp的名称
	 * @param prefix	前缀，在ProductConstants中配置
	 * @return			sp的名称，如果用户名为null，直接返回null
	 */
	public static String getSPName4Music() {
		String spName = null;
		String account = CommUtil.getCurrentLoginAccount();

		if(!isEmpty(account)) {
			spName = ProductConstants.SP_MUSIC + "_" + account;
		}

		return spName;
	}

	/**
	 * convert array to arraylist
	 */
	public static ArrayList<String> convertArrayToList(String[] arr) {
		ArrayList<String> list = new ArrayList<String>();
		if(arr != null) {
			for(String s : arr) {
				list.add(s);
			}
		}
		return list;
	}

	/**
	 * 获取当前登录账号
	 * @return 账号或子账号
	 */
	public static String getCurrentLoginAccount(){
		String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT, "");
		String _subAccount = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_SUNACCOUNT, "");
		if (TextUtils.isEmpty(_subAccount)) {
			if (TextUtils.isEmpty(_account)){
				return "";
			}else{ 
				return _account;
			}
		}else{
			return _subAccount;
		}
	}

	/**
	 * 获取主账号
	 * @return 主账号
	 */
	public static String getMainAccount(){
		String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT, "");
		if (TextUtils.isEmpty(_account)){
			return "";
		}else{ 
			return _account;
		}
	}

	/**
	 * 判断当前登录账号是否是子账号
	 * @return
	 */
	public static boolean isSubaccount(){
		boolean flag = false;
		String _subAccount = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_SUNACCOUNT, "");
		if (TextUtils.isEmpty(_subAccount)) {
			flag = false;
		}else{
			flag = true;
		}
		return flag;
	}
	/**
	 * drawable转Bitamp
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitamp(Drawable drawable) {
		if (drawable == null) return null;
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}
}
