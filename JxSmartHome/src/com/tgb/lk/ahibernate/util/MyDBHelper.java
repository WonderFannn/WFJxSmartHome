package com.tgb.lk.ahibernate.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jinxin.db.strategy.ClearDBupdateStrategy;
import com.jinxin.db.strategy.From25To26DBupdateStrategy;
import com.jinxin.db.strategy.From26To27DBupdateStrategy;
import com.jinxin.db.strategy.From27To28DBupdateStrategy;
import com.jinxin.jxsmarthome.util.Logger;

public class MyDBHelper extends SQLiteOpenHelper {
	private Class<?>[] modelClasses;
	private DBHelperListener mDBHelperListener = null;
	private final static byte[] WRITE_LOCK = new byte[0];

	public MyDBHelper(Context context, String databaseName,
			SQLiteDatabase.CursorFactory factory, int databaseVersion,
			Class<?>[] modelClasses) {
		super(context, databaseName, factory, databaseVersion);
		this.modelClasses = modelClasses;
	}
	@Override
	public SQLiteDatabase getWritableDatabase() {
		// TODO Auto-generated method stub
		synchronized (WRITE_LOCK) {
			return super.getWritableDatabase();
		}
	}
	public void onCreate(SQLiteDatabase db) {
		TableHelper.createTablesByClasses(db, this.modelClasses);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(this.mDBHelperListener != null){
			this.mDBHelperListener.onUpgrade(db,oldVersion, newVersion);
		}
		int upgradeVersion  = oldVersion;
//		if(11 == upgradeVersion){//11升12策略
//			new From11To12DBupdateStrategy().updateDBStrategy(db);
//			upgradeVersion = 12;
//		}
//		if(12 == upgradeVersion){//12升13策略
//			upgradeVersion = 13;
//		}
		if (25 == upgradeVersion) {//数据库版本25升26策略
			Logger.debug("YANG", "upgradeVersion");
			new From25To26DBupdateStrategy().updateDBStrategy(db);
			upgradeVersion = 26;
		}
		if (26 == upgradeVersion) {
			new From26To27DBupdateStrategy().updateDBStrategy(db);
			upgradeVersion = 27;
		}
		if (27 == upgradeVersion) {
			new From27To28DBupdateStrategy().updateDBStrategy(db);
			upgradeVersion = 28;
		}
		
		if (upgradeVersion != newVersion) { //不符合升级策略，清空数据库表 
			new ClearDBupdateStrategy().updateDBStrategy(db);
	    }  
//		TableHelper.dropTablesByClasses(db, this.modelClasses);
//		onCreate(db);
	}
	public void addDBHelperListener(DBHelperListener mDBHelperListener){
		this.mDBHelperListener = mDBHelperListener;
	}
	public interface DBHelperListener{
		/**
		 * 数据库版本升级更新监听
		 */
		public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion);
	}
}