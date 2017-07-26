package com.jinxin.db.strategy;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jinxin.db.util.ADBupdateStrategy;
import com.jinxin.jxsmarthome.entity.MessageTimer;
import com.tgb.lk.ahibernate.util.TableHelper;

/**
 * ********************************test test test test test test test test test*********************
 * 数据库更新策略11->12--为“CloudSetting”表增加“man”列
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class From25To26DBupdateStrategy extends ADBupdateStrategy{

	@Override
	public void updateDBStrategy(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try  
	    {  
	        db.beginTransaction();  
	        //创建新结构表
	        TableHelper.createTablesByClasses(db, new Class[]{MessageTimer.class});
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
