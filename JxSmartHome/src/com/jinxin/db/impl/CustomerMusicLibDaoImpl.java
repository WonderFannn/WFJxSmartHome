package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.CustomerMusicLib;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class CustomerMusicLibDaoImpl extends BaseDaoImpl<CustomerMusicLib>{

	public CustomerMusicLibDaoImpl(Context context) {
		super(new DBHelper(context),CustomerMusicLib.class);
	}

}
