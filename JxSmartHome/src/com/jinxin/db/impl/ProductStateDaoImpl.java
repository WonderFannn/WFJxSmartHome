package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 设备状态数据库表
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ProductStateDaoImpl extends BaseDaoImpl<ProductState> {
	public ProductStateDaoImpl(Context context) {
		super(new DBHelper(context),ProductState.class);
	}
}
