package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class WhProductUnfraredDaoImpl extends BaseDaoImpl<WHproductUnfrared>{

	public WhProductUnfraredDaoImpl(Context context) {
		super(new DBHelper(context),WHproductUnfrared.class);
	}

}
