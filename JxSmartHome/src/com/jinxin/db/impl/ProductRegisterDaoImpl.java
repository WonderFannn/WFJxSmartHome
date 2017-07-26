package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class ProductRegisterDaoImpl extends BaseDaoImpl<ProductRegister>{

	public ProductRegisterDaoImpl(Context context) {
		super(new DBHelper(context), ProductRegister.class);
	}

}
