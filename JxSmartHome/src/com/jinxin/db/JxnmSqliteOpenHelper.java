package com.jinxin.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class JxnmSqliteOpenHelper extends SQLiteOpenHelper {

	public JxnmSqliteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		create_table(db);
	}

	private void create_table(SQLiteDatabase db) {
		db.execSQL(CreTableSQL.CRE_TB_SYS_AREA);
		CreTableSQL.insert_tb_sys_area_data(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
