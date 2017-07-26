package com.tgb.lk.ahibernate.util;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.jinxin.jxsmarthome.util.Logger;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Index;
import com.tgb.lk.ahibernate.annotation.Table;

public class TableHelper {
	private static final String TAG = "AHibernate";
	/**
	 * 创建表+索引
	 * @param db
	 * @param clazzs
	 */
	public static <T> void createTablesByClasses(SQLiteDatabase db,
			Class<?>[] clazzs) {
		for (Class<?> clazz : clazzs){
			createTable(db, clazz);
			createIndex(db, clazz);
		}
	}
	/**
	 * 重命名表为临时表
	 * @param db
	 * @param clazz
	 */
	public static<T> void renameTableToTempByClasses(SQLiteDatabase db, Class<?>[] clazzs){
		for (Class<?> clazz : clazzs)
			renameTableToTemp(db, clazz);
	}
	/**
	 * 重命名表为临时表
	 * @param db
	 * @param clazz
	 */
	public static<T> void renameTableToTemp(SQLiteDatabase db, Class<T> clazz){
		String tableName = "";
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz	.getAnnotation(Table.class);
			tableName = table.name();
		}
//		ALTER TABLE test RENAME TO _Test"
		StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(tableName);
		sb.append(" RENAME TO ");
		sb.append(tableName).append("_temp");
		String sql = sb.toString();
		db.execSQL(sql);
	}
	/**
	 * CREATE INDEX mycolumn_index ON mytable (myclumn)
	 * CREATE INDEX 索引名 ON 表名(字段名);
	 * @param db
	 * @param clazz
	 */
	public static <T> void createIndex(SQLiteDatabase db, Class<T> clazz) {
		String tableName = "";
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz	.getAnnotation(Table.class);
			tableName = table.name();
		}
		
		StringBuffer sb = new StringBuffer();
		
		
		List<Field> allFields = TableHelper
				.joinFields(clazz.getDeclaredFields(), clazz.getSuperclass()
						.getDeclaredFields());
		for (Field field : allFields) {
			if (!field.isAnnotationPresent(Index.class)) {
				continue;
			}
			
			sb.append("CREATE INDEX ");
			Index index = (Index) field.getAnnotation(Index.class);
			String _indexName = index.indexname();
			String _columnName = index.columnname();
			if(TextUtils.isEmpty(_indexName) || TextUtils.isEmpty(_columnName))return;
			sb.append(_indexName);
			sb.append(" ON ");
			sb.append(tableName);
			sb.append(" (");
			sb.append(_columnName);
			sb.append(")");
//			String columnType = "";
//			if (column.type().equals(""))
//				columnType = getColumnType(field.getType());
//			else {
//				columnType = column.type();
//			}
			
//			sb.append(column.name() + " " + columnType);
			
//			if (column.length() != 0) {
//				sb.append("(" + column.length() + ")");
//			}
			
//			if ((field.isAnnotationPresent(Id.class)) // update 2012-06-10 实体类定义为Integer类型后不能生成Id异常
//					&& ((field.getType() == Integer.TYPE) || (field.getType() == Integer.class)))
//				sb.append(" primary key autoincrement");
//			else if (field.isAnnotationPresent(Id.class)) {
//				sb.append(" primary key");
//			}
			
//			sb.append(", ");
		
		
//		sb.delete(sb.length() - 2, sb.length() - 1);
//		sb.append(")");
		
		String sql = sb.toString();
		
		Log.d(TAG, "crate table index[" + tableName + "->" + _columnName + "]: " + sql);
		
		db.execSQL(sql);
		sb.setLength(0);
		}
	}
	/**
	 * 拷贝临时表数据到新表（支持增加删除列）
	 * @param db
	 * @param clazzs
	 * @param rows 注意必须和新表列结构完全一致（新增""代替）
	 */
	public static <T> void copyTableFromTempByClasses(SQLiteDatabase db, Class<?>[] clazzs,String[][]rows){
		for(int i = 0;i < clazzs.length;i++){
			copyTableFromTemp(db,clazzs[i],rows[i]);
		}
	}
	/**
	 * 拷贝临时表数据到新表（支持增加删除列）
	 * @param db
	 * @param clazz
	 * @param rows 注意必须和新表列结构完全一致（新增""代替）
	 */
	public static <T> void copyTableFromTemp(SQLiteDatabase db, Class<T> clazz,String[]rows){
		String tableName = "";
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz	.getAnnotation(Table.class);
			tableName = table.name();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(tableName);
		sb.append(" SELECT ");
		for(String row : rows){
			sb.append(row).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length() - 1);
		sb.append(" FROM ");
		sb.append(tableName).append("_temp");
		String sql = sb.toString();
		db.execSQL(sql);
	}
/**
 * 删除临时表
 * @param db
 * @param clazzs
 */
	public static <T> void dropTablesToTempByClasses(SQLiteDatabase db,
			Class<?>[] clazzs) {
		for (Class<?> clazz : clazzs)
			dropTableToTemp(db, clazz);
	}
	public static <T> void dropTablesByClasses(SQLiteDatabase db,
			Class<?>[] clazzs) {
		for (Class<?> clazz : clazzs)
			dropTable(db, clazz);
	}
	public static <T> void dropTableToTemp(SQLiteDatabase db, Class<T> clazz) {
		String tableName = "";
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz.getAnnotation(Table.class);
			tableName = table.name();
		}
		String sql = "DROP TABLE IF EXISTS " + tableName+"_temp";
		Log.d(TAG, "dropTable[" + tableName + "]:" + sql);
		db.execSQL(sql);
	}
	public static <T> void createTable(SQLiteDatabase db, Class<T> clazz) {
		String tableName = "";
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz	.getAnnotation(Table.class);
			tableName = table.name();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(tableName).append(" (");

		List<Field> allFields = TableHelper
				.joinFields(clazz.getDeclaredFields(), clazz.getSuperclass()
						.getDeclaredFields());
		for (Field field : allFields) {
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			Column column = (Column) field.getAnnotation(Column.class);

			String columnType = "";
			if (column.type().equals(""))
				columnType = getColumnType(field.getType());
			else {
				columnType = column.type();
			}

			sb.append(column.name() + " " + columnType);

			if (column.length() != 0) {
				sb.append("(" + column.length() + ")");
			}

			if ((field.isAnnotationPresent(Id.class)) // update 2012-06-10 实体类定义为Integer类型后不能生成Id异常
					&& ((field.getType() == Integer.TYPE) || (field.getType() == Integer.class)))
				sb.append(" primary key autoincrement");
			else if (field.isAnnotationPresent(Id.class)) {
				sb.append(" primary key");
			}

			sb.append(", ");
		}

		sb.delete(sb.length() - 2, sb.length() - 1);
		sb.append(")");

		String sql = sb.toString();

		Logger.debug(TAG, "crate table [" + tableName + "]: " + sql);

		db.execSQL(sql);
	}

	public static <T> void dropTable(SQLiteDatabase db, Class<T> clazz) {
		String tableName = "";
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz.getAnnotation(Table.class);
			tableName = table.name();
		}
		String sql = "DROP TABLE IF EXISTS " + tableName;
		Logger.debug(TAG, "dropTable[" + tableName + "]:" + sql);
		db.execSQL(sql);
	}

	private static String getColumnType(Class<?> fieldType) {
		if (String.class == fieldType) {
			return "TEXT";
		}
		if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
			return "INTEGER";
		}
		if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
			return "BIGINT";
		}
		if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
			return "FLOAT";
		}
		if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
			return "INT";
		}
		if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
			return "DOUBLE";
		}
		if (Blob.class == fieldType) {
			return "BLOB";
		}

		return "TEXT";
	}

	// 合并Field数组并去重,并实现过滤掉非Column字段,和实现Id放在首字段位置功能
	public static List<Field> joinFields(Field[] fields1, Field[] fields2) {
		Map<String, Field> map = new LinkedHashMap<String, Field>();
		for (Field field : fields1) {
			// 过滤掉非Column定义的字段
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = (Column) field.getAnnotation(Column.class);
			map.put(column.name(), field);
		}
		for (Field field : fields2) {
			// 过滤掉非Column定义的字段
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = (Column) field.getAnnotation(Column.class);
			if (!map.containsKey(column.name())) {
				map.put(column.name(), field);
			}
		}
		List<Field> list = new ArrayList<Field>();
		for (String key : map.keySet()) {
			Field tempField = map.get(key);
			// 如果是Id则放在首位置.
			if (tempField.isAnnotationPresent(Id.class)) {
				list.add(0, tempField);
			} else {
				list.add(tempField);
			}
		}
		return list;
	}
}