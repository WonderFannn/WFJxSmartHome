package com.jinxin.db.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jinxin.jxsmarthome.util.Logger;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

/**
 * 删除平台同步后需要删除的数据的辅助类
 * @author TangLong
 * @company 金鑫智慧
 */
public class CustomerDataSyncHelper {
	private SQLiteDatabase sqliteDb;
	 
	public CustomerDataSyncHelper(Context context) {
		sqliteDb = new DBHelper(context).getWritableDatabase();
	}
	
	/**
	 * 根据条件删除表中的数据
	 * @param table			删除的表
	 * @param whereClause	条件
	 * @param whereArgs		条件值
	 */
	public void doCustomerDataSyncTask(String table, String whereClause, String[] whereArgs) {
		if(isEmpty(whereClause) || isEmpty(whereArgs)) {
			Logger.error(null, "conditions cannot be null");
			return;
		}
		
		try {
			sqliteDb.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			// 捕获由于表列名不存在引发的异常
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭数据库连接
	 */
	public void close() {
		if(sqliteDb != null) {
			sqliteDb.close();
		}
	}
	
	/**
	 * 利用反射得到数据库操作类(未使用)
	 */
	public BaseDaoImpl<?> getBaseDaoImpl(Class<?> t, Context context) {
		BaseDaoImpl<?> daoImplInstance = null;
		String daoClass = getClassSimpleName(t);
		Logger.debug(null, "-->" + daoClass);
		try {
			Class<?> daoClassInstance = Class.forName(daoClass);
			Constructor<?> cons[] = daoClassInstance.getConstructors();
			if(cons.length > 0) {
				daoImplInstance = (BaseDaoImpl<?>)cons[0].newInstance(context);
			}else {
				Logger.error(null, "the constructors of class is not legale, "
						+ "the must be at least one constructor which has"
						+ " parameter Context");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Logger.error(null, "cannot found class for name:" + daoClass);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return daoImplInstance;
	}
	
	private String getClassSimpleName(Class<?> t) {
		return "com.jinxin.db.impl." + t.getSimpleName() + "DaoImpl";
	}
	
	private boolean isEmpty(Object[] arr) {
		if(arr == null || arr.length < 1) {
			return true;
		}else {
			return false;
		}
	}
	
	private boolean isEmpty(String str) {
		if(str == null || str.length() < 1) {
			return true;
		}else {
			return false;
		}
	}
}
