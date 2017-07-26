package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 用户模式数据库表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerProductAreaDaoImpl extends BaseDaoImpl<CustomerProductArea> {
	public CustomerProductAreaDaoImpl(Context context) {
		super(new DBHelper(context),CustomerProductArea.class);
	}
}
