package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.MessageTimer;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 用户详情数据库表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class MessageTimerDaoImpl extends BaseDaoImpl<MessageTimer> {
	public MessageTimerDaoImpl(Context context) {
		super(new DBHelper(context),MessageTimer.class);
	}
}
