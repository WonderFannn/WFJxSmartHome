package com.jinxin.datan.local.util;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jinxin.db.impl.OffLineConetenDaoImpl;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.OffLineContent;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 离线/在线网络模式切换的判断
 * @author  TangLong
 * @company 金鑫智慧
 */
public class NetworkModeSwitcher {
	/**
	 * 判断是否使用离线模式：
	 * 		判断策略：
	 * @param context
	 * @return
	 */
	public static boolean useOfflineMode(Context context) {
		boolean ret = false;
		String account = CommUtil.getCurrentLoginAccount();
		if (account!=null && account!= "") {
			ret = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_OFF_LINE_MODE, false);
		}
		return ret;
	}
	
	private static String getSPNameOfSettings() {
		String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,"");
		return _account + "_" + ProductConstants.SP_SETTINGS;
	}
	
	public static void setUseOfflineMode(boolean b, Context context) {
		SharedPreferences sp = context.getSharedPreferences(getSPNameOfSettings(), Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		if(b) {
			editor.putBoolean(ProductConstants.SP_SETTINGS_OFFLINE, b);
		}
		editor.commit();
	}
	
	
	/**
	 * 获取网关的信息
	 * @param context
	 * @return 			网关的信息
	 */
	public static List<OffLineContent> getLocalGatwayIP(Context context){
		OffLineConetenDaoImpl olcImpl = new OffLineConetenDaoImpl(context);
		List<OffLineContent>  olcList = olcImpl.find();
		if (olcList!=null && olcList.size()>0) {
			return olcList;
		}
		return null;
	}
	
}
