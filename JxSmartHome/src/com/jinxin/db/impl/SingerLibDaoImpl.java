package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.SingerLib;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class SingerLibDaoImpl extends BaseDaoImpl<SingerLib>{

	public SingerLibDaoImpl(Context context) {
		super(new DBHelper(context),SingerLib.class);
	}

}
