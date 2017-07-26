package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.OEMVersion;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class OEMVersionDaoImpl extends BaseDaoImpl<OEMVersion> {
	public OEMVersionDaoImpl(Context context) {
		super(new DBHelper(context), OEMVersion.class);
	}
}
