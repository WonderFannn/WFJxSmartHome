package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class MusicLibDaoImpl extends BaseDaoImpl<MusicLib>{

	public MusicLibDaoImpl(Context context) {
		super(new DBHelper(context),MusicLib.class);
	}

}
