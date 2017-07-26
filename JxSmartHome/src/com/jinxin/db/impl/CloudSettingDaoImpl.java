package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class CloudSettingDaoImpl extends BaseDaoImpl<CloudSetting>{

	public CloudSettingDaoImpl(Context context) {
		super(new DBHelper(context),CloudSetting.class);
	}

}
