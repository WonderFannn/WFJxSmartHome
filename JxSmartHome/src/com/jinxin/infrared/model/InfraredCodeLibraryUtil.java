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
	
	public InfraredCodeLibraryUtil(Context context){
		packDataBase(context);
	}
	
	public List<Integer> getCodeListByDeviceInfo(int type,String brand,String model){
        List<Integer> proList = new ArrayList<Integer>();
        String typeString = InfraredCodeLibraryConstant.DataBase.TABLENAME[type];
        if (model.equals("智能匹配")) {
			model = "%";
		}
        String sqlString = "select SERIAL from "+typeString+" where brand_en = '"+brand+"' and model like '"+model+"' ";
        Log.d("wangfan", sqlString);
        try{
        	db = SQLiteDatabase.openDatabase(new String(DB_PATH + DB_NAME) , null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db.rawQuery(sqlString,null);
            if(null != cursor){
                while(cursor.moveToNext()){
                	int id = cursor.getInt(cursor.getColumnIndex("SERIAL"));
                    Integer serial = new Integer(id);
                    proList.add(serial);
                }
            }
            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(null != db){
                db.close();
            }
        }
        return proList;
    }
	
    public void packDataBase(Context context){
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

}
