package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 用户设备数据库表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerProductDaoImpl extends BaseDaoImpl<CustomerProduct> {
	public CustomerProductDaoImpl(Context context) {
		super(new DBHelper(context),CustomerProduct.class);
	}
}
