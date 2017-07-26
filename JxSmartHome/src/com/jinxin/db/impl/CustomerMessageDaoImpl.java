package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerMeassage;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class CustomerMessageDaoImpl extends BaseDaoImpl<CustomerMeassage> {
	public CustomerMessageDaoImpl(Context context) {
		super(new DBHelper(context), CustomerMeassage.class);
	}
}
