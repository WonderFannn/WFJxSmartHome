package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.Music;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class MusicDaoImpl extends BaseDaoImpl<Music> {
	public MusicDaoImpl(Context context) {
		super(new DBHelper(context), Music.class);
	}
}
