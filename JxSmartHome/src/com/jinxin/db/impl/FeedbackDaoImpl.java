package com.jinxin.db.impl;

import android.content.Context;

import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.entity.Feedback;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class FeedbackDaoImpl extends BaseDaoImpl<Feedback>{

	public FeedbackDaoImpl(Context context) {
		super(new DBHelper(context),Feedback.class);
	}

}
