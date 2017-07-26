package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 产品模式操作列表数据库表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ProductPatternOperationDaoImpl extends BaseDaoImpl<ProductPatternOperation> {
	public ProductPatternOperationDaoImpl(Context context) {
		super(new DBHelper(context),ProductPatternOperation.class);
	}
}
