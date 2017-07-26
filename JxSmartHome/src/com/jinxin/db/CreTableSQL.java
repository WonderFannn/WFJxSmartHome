package com.jinxin.db;

import android.database.sqlite.SQLiteDatabase;

public class CreTableSQL {
	public final static String TB_SYS_AREA = "sys_area";

	public final static String CRE_TB_SYS_AREA = "Create table" + TB_SYS_AREA
			+ "(" + "AREA_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "AREA_NAME VARCHAR," + "AREA_CODE VARCHAR,"
			+ "NODE_PATH VARCHAR," + "PARENT_ID INTEGER,"
			+ "ORDER_NO DECIMAL(10, 0)," + "LEVEL_NO INTEGER,"
			+ "ISLEAF INTEGER," + "PROVINCE_NAME VARCHAR,"
			+ "CITY_NAME VARCHAR," + "COUNTY_NAME VARCHAR,"
			+ "ZIP_CODE VARCHAR," + "TYPE INTEGER," + "FULL_PATH VARCHAR,"
			+ "ISSYS INTEGER," + "ZJM_CODE INTEGER," + "ENABLE INTEGER,"
			+ "REMARK VARCHAR," + "CREATOR_ID INTEGER,"
			+ "CREATED_TIME timestamp," + "EDITOR_ID VARCHAR,"
			+ "EDITED_TIME timestamp" + ")";

	public static void insert_tb_sys_area_data(SQLiteDatabase db) {
		insert_tb_sys_area(
				db,
				"0, '根节点', '', '根节点_根节点_根节点', -1, 0, 1, 0, '根节点', '根节点', '根节点', '', 0, '根节点_根节点_根节点', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL");
		insert_tb_sys_area(
				db,
				"1, '东城区', '10', '北京市_北京市_东城区', 2970, 0, 4, 1, '北京市', '北京市', '东城区', '100744', 3, '北京市_北京市_东城区', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL");
		insert_tb_sys_area(
				db,
				"2, '西城区', '10', '北京市_北京市_西城区', 2970, 1, 4, 1, '北京市', '北京市', '西城区', '100744', 3, '北京市_北京市_西城区', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL");
	}

	private static void insert_tb_sys_area(SQLiteDatabase db, String values) {
		db.execSQL("insert into " + TB_SYS_AREA + " values(" + values + ")");
	}
}
