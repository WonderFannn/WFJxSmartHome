package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductVoiceType;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class ProductVoiceTypeDaoImpl extends BaseDaoImpl<ProductVoiceType> {
	public ProductVoiceTypeDaoImpl(Context context) {
		super(new DBHelper(context), ProductVoiceType.class);
	}
}
