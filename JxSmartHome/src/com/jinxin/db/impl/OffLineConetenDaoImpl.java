package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.OffLineContent;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
/**
 * 离线网关搜索返回信息表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class OffLineConetenDaoImpl extends BaseDaoImpl<OffLineContent> {
	public OffLineConetenDaoImpl(Context context) {
		super(new DBHelper(context),OffLineContent.class);
	}
}
