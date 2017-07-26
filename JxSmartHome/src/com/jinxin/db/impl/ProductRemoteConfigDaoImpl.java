package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class ProductRemoteConfigDaoImpl extends BaseDaoImpl<ProductRemoteConfig> {

	public ProductRemoteConfigDaoImpl(Context context) {
		super(new DBHelper(context), ProductRemoteConfig.class);
	}

}
