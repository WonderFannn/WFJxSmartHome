package com.jinxin.infrared.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InfraredCodeLibraryUtil {
	private String DB_PATH = "/data/data/com.jinxin.jxsmarthome/databases/";
	private String DB_NAME = "IRLibaray.db";
	private SQLiteDatabase db = null;

	public InfraredCodeLibraryUtil(Context context) {
		packDataBase(context);
	}

	public List<byte[]> getCodeListByDeviceInfo(int type, String brand,
			String model) {
		List<byte[]> proList = new ArrayList<byte[]>();
		String typeString = InfraredCodeLibraryConstant.DataBase.TABLENAME[type];
		if (model.equals("智能匹配")) {
			model = "%";
		}
		String sqlString = "select CODE from " + typeString
				+ " where BRAND_CN = '" + brand + "' and MODEL like '" + model
				+ "' group by CODE";
		Log.d("wangfan", sqlString);
		try {
			db = SQLiteDatabase.openDatabase(new String(DB_PATH + DB_NAME),
					null, SQLiteDatabase.OPEN_READONLY);
			Cursor cursor = db.rawQuery(sqlString, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					byte[] code = cursor.getBlob(cursor.getColumnIndex("CODE"));
					proList.add(code);
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != db) {
				db.close();
			}
		}
		return proList;
	}

	public List<String> getBrandtByDeviceType(int type) {
		List<String> brandList = new ArrayList<String>();
		String typeString = InfraredCodeLibraryConstant.DataBase.TABLENAME[type];

		String sqlString = "select BRAND_CN from " + typeString
				+ " group by BRAND_CN";
		Log.d("wangfan", sqlString);
		try {
			db = SQLiteDatabase.openDatabase(new String(DB_PATH + DB_NAME),
					null, SQLiteDatabase.OPEN_READONLY);
			Cursor cursor = db.rawQuery(sqlString, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					String brand = cursor.getString(cursor
							.getColumnIndex("BRAND_CN"));
					brandList.add(brand);
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != db) {
				db.close();
			}
		}
		return brandList;
	}

	public List<String> getModelByDeviceTypeAndBrand(int type, String brand) {
		List<String> modeList = new ArrayList<String>();
		String typeString = InfraredCodeLibraryConstant.DataBase.TABLENAME[type];
		String sqlString = "select MODEL from " + typeString
				+ " where BRAND_CN = '" + brand + "' and MODEL != 'no_model'";
		Log.d("wangfan", sqlString);
		try {
			db = SQLiteDatabase.openDatabase(new String(DB_PATH + DB_NAME),
					null, SQLiteDatabase.OPEN_READONLY);
			Cursor cursor = db.rawQuery(sqlString, null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					String model = cursor.getString(cursor
							.getColumnIndex("MODEL"));
					modeList.add(model);
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != db) {
				db.close();
			}
		}
		return modeList;
	}

	public void packDataBase(Context context) {
		// 检查 SQLite 数据库文件是否存在
		if (!(new File(DB_PATH + DB_NAME)).exists()) {
			// 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
			File f = new File(DB_PATH);
			// 如 database 目录不存在，新建该目录
			if (!f.exists()) {
				f.mkdir();
			}
			try {
				// 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
				InputStream is = context.getAssets().open(DB_NAME);
				// 输出流,在指定路径下生成db文件
				OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
				// 文件写入
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				// 关闭文件流
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
			}
		}
	}

	public byte[] IntToByteArray(int n) {
		byte[] b = new byte[2];
		b[0] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n & 0xff);
		return b;
	}

	private byte[] byteMerge(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
}
