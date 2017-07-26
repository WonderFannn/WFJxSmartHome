package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 定时模式数据库表
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class CustomerTimerTaskDaoImpl extends BaseDaoImpl<CustomerTimer> {
	public CustomerTimerTaskDaoImpl(Context context) {
		super(new DBHelper(context),CustomerTimer.class);
	}
}
