package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerPatternCMD;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 用户模式指令数据库表
 * @deprecated
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerPatternCMDDaoImpl extends BaseDaoImpl<CustomerPatternCMD> {
	public CustomerPatternCMDDaoImpl(Context context) {
		super(new DBHelper(context),CustomerPatternCMD.class);
	}
}
