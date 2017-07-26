package com.jinxin.db.strategy;

import com.jinxin.db.util.ADBupdateStrategy;
import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.record.SharedDB;
import com.tgb.lk.ahibernate.util.TableHelper;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库更新策略--清空所有表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ClearDBupdateStrategy extends ADBupdateStrategy{

	@Override
	public void updateDBStrategy(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try  
	    {  
	        db.beginTransaction(); 
	        
	        /*********核心处理****************************/
	        final String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,"");
			final String secretkey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,ControlDefine.KEY_SECRETKEY,"");
			final String nickyName = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,ControlDefine.KEY_NICKNAME,"");
	        SharedDB.removeAct(JxshApp.getContext(), _account);
			SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
					ControlDefine.KEY_ACCOUNT, _account);
			SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
					ControlDefine.KEY_SECRETKEY,secretkey);
			// save key to personal property
			SharedDB.saveStrDB(_account,
					ControlDefine.KEY_SECRETKEY,secretkey);
			//存入别名：
			SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
					ControlDefine.KEY_NICKNAME,nickyName);
	        TableHelper.dropTablesByClasses(db, DBHelper.clazz);
	        TableHelper.createTablesByClasses(db, DBHelper.clazz);
	        /*************************************/
	        
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
