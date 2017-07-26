package com.jinxin.jxsmarthome.util;

import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.record.FileUtil;
import com.jinxin.record.SharedDB;

public class EncryptUtil {

	public static void setPassword(String password) {
		try {
			String _account = CommUtil.getCurrentLoginAccount();
			DesUtils des = new DesUtils(_account);
			SharedDB.saveStrDB(_account, ControlDefine.KEY_PASSWORD, des.encrypt(password));
			FileUtil.saveKey(_account, des.encrypt(password));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getPassword(String _account) {
		try {
			DesUtils des = new DesUtils(_account);
			String secretPasswrod = SharedDB.loadStrFromDB(_account, ControlDefine.KEY_PASSWORD,"");
			return des.decrypt(secretPasswrod);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void setPasswordForOrdinary(String password){
		try {
			String _account = SharedDB.ORDINARY_CONSTANTS;
			DesUtils des = new DesUtils(_account);
			SharedDB.saveStrDB(_account, ControlDefine.KEY_PASSWORD, des.encrypt(password));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getOrdinaryPassword(){
		try {
			DesUtils des = new DesUtils(SharedDB.ORDINARY_CONSTANTS);
			String secretPasswrod = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_PASSWORD,"");
			return des.decrypt(secretPasswrod);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}