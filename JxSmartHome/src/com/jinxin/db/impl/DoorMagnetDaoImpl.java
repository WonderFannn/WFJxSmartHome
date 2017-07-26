package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductDoorContact;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 产品功能列表数据库表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class DoorMagnetDaoImpl extends BaseDaoImpl<ProductDoorContact> {
	public DoorMagnetDaoImpl(Context context) {
		super(new DBHelper(context),ProductDoorContact.class);
	}
}
