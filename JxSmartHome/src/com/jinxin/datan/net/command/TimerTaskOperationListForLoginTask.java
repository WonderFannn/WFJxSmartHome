package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

import android.content.Context;

public class TimerTaskOperationListForLoginTask extends TimerTaskOperationListTask {

	public TimerTaskOperationListForLoginTask(Context context, String taskIds) {
		super(context, taskIds);
	}
	
	@Override
	protected JSONObject createServiceContent() throws JSONException {
		String _secretKey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_SECRETKEY,"");
		String _account = CommUtil.getCurrentLoginAccount();
		String _updateTime = ControlDefine.DEFAULT_UPDATE_TIME_1990;
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("updateTime", _updateTime);
		serviceContent.put("taskIds", taskIds);
		serviceContent.put("status", 4);
		return serviceContent;
	}

}
