package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 用户详情数据库表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerDaoImpl extends BaseDaoImpl<Customer> {
	public CustomerDaoImpl(Context context) {
		super(new DBHelper(context),Customer.class);
	}
}
