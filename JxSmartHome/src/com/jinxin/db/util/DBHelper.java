package com.jinxin.db.util;

import android.content.Context;
import android.text.TextUtils;

import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.CustomerMeassage;
import com.jinxin.jxsmarthome.entity.CustomerMusicLib;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.CustomerPatternCMD;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.jinxin.jxsmarthome.entity.CustomerProductCMD;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.entity.CustomerVoiceConfig;
import com.jinxin.jxsmarthome.entity.Feedback;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.MessageTimer;
import com.jinxin.jxsmarthome.entity.Music;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.entity.OEMVersion;
import com.jinxin.jxsmarthome.entity.OffLineContent;
import com.jinxin.jxsmarthome.entity.ProductConnection;
import com.jinxin.jxsmarthome.entity.ProductDoorContact;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;
import com.jinxin.jxsmarthome.entity.ProductVoiceType;
import com.jinxin.jxsmarthome.entity.SingerLib;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.entity.TimerTaskOperation;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;
import com.tgb.lk.ahibernate.util.MyDBHelper;

/**
 * 数据库初始化
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class DBHelper extends MyDBHelper {
//	private static final String DBNAME = "jxsmarthome.db";// 数据库名
	private static String dbName = "jxsmarthome.db";// 数据库名
	private static final int DBVERSION = 28;//对应APK版本2.4.1
	public static final Class<?>[] clazz = { CustomerPattern.class, CustomerProduct.class,
				CustomerProductCMD.class, CustomerPatternCMD.class,
				ProductFun.class, ProductPatternOperation.class,
				FunDetail.class, Customer.class, FunDetailConfig.class,
				Music.class, ProductState.class, SysUser.class,
				CloudSetting.class,OffLineContent.class,
				CustomerTimer.class,TimerTaskOperation.class,
				CustomerArea.class, CustomerProductArea.class,
				ProductRegister.class, ProductRemoteConfigFun.class,
				ProductRemoteConfig.class,MusicLib.class,
				SingerLib.class,CustomerMusicLib.class,
				ProductVoiceType.class,ProductVoiceConfig.class,
				CustomerVoiceConfig.class,ProductDoorContact.class,
				CustomerMeassage.class, MessageTimer.class,
				CustomerBrands.class,ProductConnection.class,
				OEMVersion.class,Feedback.class,WHproductUnfrared.class};// 要初始化的表

	public DBHelper(Context context) {
		super(context, getDbName(), null, DBVERSION, clazz);
		SharedDB.saveDB(SharedDB.ORDINARY_CONSTANTS,
					ControlDefine.KEY_DB_NUM, DBVERSION);
	}

	public static boolean deleteDatabase(Context context){
		Logger.debug(null, "DBHelper deleteDatabase:"+getDbName());
		return context.deleteDatabase(getDbName());
	}
	/**
	 * 设定数据库名
	 * @param db_name 不带后缀
	 */
	private static void setDbName(String db_name){
		dbName = db_name+".db";
	}
	
	public static String getDbName() {
		String userID = CommUtil.getCurrentLoginAccount();
		if(!TextUtils.isEmpty(userID)) {
			DBHelper.setDbName("jxsmarthome_"+userID);
		}
		return dbName;
	}
}
