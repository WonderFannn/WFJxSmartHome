package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class ProductVoiceConfigDaoImpl extends BaseDaoImpl<ProductVoiceConfig> {
	public ProductVoiceConfigDaoImpl(Context context) {
		super(new DBHelper(context), ProductVoiceConfig.class);
	}
}
