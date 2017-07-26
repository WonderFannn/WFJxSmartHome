package com.jinxin.db.strategy;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jinxin.db.util.ADBupdateStrategy;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.tgb.lk.ahibernate.util.TableHelper;

/**
 * ********************************test test test test test test test test test*********************
 * 数据库更新策略11->12--为“CloudSetting”表增加“man”列
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class From11To12DBupdateStrategy extends ADBupdateStrategy{

	@Override
	public void updateDBStrategy(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try  
	    {  
	        db.beginTransaction();  
	        /***********************************/
	        //重命名老表为临时表
	        TableHelper.renameTableToTempByClasses(db, new Class[]{CloudSetting.class});
	        //创建新结构表
	        TableHelper.createTablesByClasses(db, new Class[]{CloudSetting.class});
	        //导入数据（！！！务必按照数据库中的新表结构顺序填写！！！）
	        TableHelper.copyTableFromTempByClasses(db, new Class[]{CloudSetting.class}, new String[][]{
	        		{"id","\"\"","create_time","server_id","category","items","params","update_time","customer_id"}
	        });
	        //删除临时表
	        TableHelper.dropTablesToTempByClasses(db, new Class[]{CloudSetting.class});
	        /***********************************/
	        db.setTransactionSuccessful();  
	    }catch (SQLException e)  
	    {  
	        e.printStackTrace();  
	    }  
	    catch (Exception e)  
	    {  
	        e.printStackTrace();  
	    }  
	    finally  
	    {  
	        db.endTransaction();  
	    }  
	}

}
