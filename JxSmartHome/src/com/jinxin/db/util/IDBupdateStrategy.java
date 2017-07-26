package com.jinxin.db.util;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库升级策略接口
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public interface IDBupdateStrategy {
	void updateDBStrategy(SQLiteDatabase db);
}
