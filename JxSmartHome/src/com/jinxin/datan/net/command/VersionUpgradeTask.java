package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.VersionRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.StringUtils;


/**
 * APP更新
 * @author BinhuaHuang
 * @company 金鑫智慧
 */
public class VersionUpgradeTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String m_appVersion = null;
	private Integer m_appType = null;
	/**
	 * 
	 * @param context
	 */
	public VersionUpgradeTask(Context context, String _appVersion , Integer _appType) {
		this.m_appVersion = _appVersion ;
		this.m_appType = _appType;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_UPGRADE_MANAGER,
					ControlDefine.TRD_USER_DEVICE, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new VersionRJ(this,
				requestBytes));
	}

	/**
	 * 创建serviceContent JSON数据体
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createServiceContent() throws JSONException {
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("appVersion", this.m_appVersion);
		serviceContent.put("appType", this.m_appType);
		return serviceContent;
	}
}
