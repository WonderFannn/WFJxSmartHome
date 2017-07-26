package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerProductCMD;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 用户设备指令数据库表
 * @deprecated
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerProductCMDDaoImpl extends BaseDaoImpl<CustomerProductCMD> {
	public CustomerProductCMDDaoImpl(Context context) {
		super(new DBHelper(context),CustomerProductCMD.class);
	}
}
