package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerVoiceConfig;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class CustomerVoiceConfigDaoImpl extends BaseDaoImpl<CustomerVoiceConfig> {
	public CustomerVoiceConfigDaoImpl(Context context) {
		super(new DBHelper(context), CustomerVoiceConfig.class);
	}
}
