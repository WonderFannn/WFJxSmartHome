package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 用户模式数据库表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerPatternDaoImpl extends BaseDaoImpl<CustomerPattern> {
	public CustomerPatternDaoImpl(Context context) {
		super(new DBHelper(context),CustomerPattern.class);
	}
}
