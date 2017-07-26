package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 产品功能列表配置参数数据库表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class FunDetailConfigDaoImpl extends BaseDaoImpl<FunDetailConfig> {
	public FunDetailConfigDaoImpl(Context context) {
		super(new DBHelper(context),FunDetailConfig.class);
	}
}
