package com.jinxin.datan.net.command;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.CloudSettingParser;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**
 * 处理云同步设置的网络请求
 * @author  TangLong
 * @company 金鑫智慧
 */
public class SyncCloudSettingTask extends InternetTask {

	private NetRequest request = null;
	private String updateType = null;		// 固定为：get:获取云设置, sync：同步云设置 
	private String items;
	private String params;
	private List<CloudSetting> list;

	public SyncCloudSettingTask(Context context, String updateType, String items, String params, List<CloudSetting> list) {
		this.updateType = updateType;
		this.items = items;
		this.params = params;
		this.list = list;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = null;
			if("get".equals(updateType)) {
				header = new Header(ControlDefine.BS_SYNC_CLOUD_SETTING,
						ControlDefine.TRD_GET_USER_CLOUND_SETTING, ControlDefine.ACTION_REQUEST,
						StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			}else if("sync".equals(updateType)) {
				header = new Header(ControlDefine.BS_SYNC_CLOUD_SETTING,
						ControlDefine.TRD_SYNC_USER_CLOUND_SETTING, ControlDefine.ACTION_REQUEST,
						StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			}else {
				// 暂时不处理
			}
//			
			Logger.debug(null, createServiceContent().toString());
			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new CloudSettingParser(this,
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
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("account", _account);
		serviceContent.put("secretKey", _secretKey);
		
		if(list != null) {
			JSONArray jsonArray = new JSONArray();
			for(CloudSetting cs : list) {
				JSONObject _jo = new JSONObject();
				_jo.put("customerId", cs.getCustomerId());
				_jo.put("items", cs.getItems());
				_jo.put("category", cs.getCategory());
				_jo.put("params", cs.getParams());
				jsonArray.put(_jo);
			}
			serviceContent.put("settingList", jsonArray);
		}
		serviceContent.put("items", items);
		serviceContent.put("params", params);
		Logger.error(null, "-->"+serviceContent.toString());
		return serviceContent;
	}

}
