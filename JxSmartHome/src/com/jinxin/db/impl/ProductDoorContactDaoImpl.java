package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductDoorContact;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class ProductDoorContactDaoImpl extends BaseDaoImpl<ProductDoorContact> {
	public ProductDoorContactDaoImpl(Context context) {
		super(new DBHelper(context), ProductDoorContact.class);
	}
}
