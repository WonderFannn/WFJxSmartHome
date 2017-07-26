package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class ProductRemoteConfigFunDaoImpl extends BaseDaoImpl<ProductRemoteConfigFun>{

	public ProductRemoteConfigFunDaoImpl(Context context) {
		super(new DBHelper(context), ProductRemoteConfigFun.class);
	}

}
