package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.DoorbellList;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 	用户指定设备类型的列表请求
 * @company 金鑫智慧
 */
public class DoorbellListTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	
	private String code = null;
	/**
	 * 
	 * @param context
	 */
	public DoorbellListTask(Context context,String code) {
		this.code = code;
		this.init();
		super.setNetRequest(null, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_USER_MANAGER,
					ControlDefine.TRD_CUSTOMER_PATTERN_LIST, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new DoorbellList(this,
				requestBytes));
	}

	/**
	 * 创建serviceContent JSON数据体
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createServiceContent() throws JSONException {
		String _secretKey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_SECRETKEY,"");
		String _account = CommUtil.getCurrentLoginAccount();
		String _updateTime = SharedDB.loadStrFromDB(_account, ControlDefine.KEY_DOORBELL_DEVICE,ControlDefine.DEFAULT_UPDATE_TIME);
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("account", _account);
		serviceContent.put("code", code);
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("updateTime", _updateTime);
		return serviceContent;
	}
}
