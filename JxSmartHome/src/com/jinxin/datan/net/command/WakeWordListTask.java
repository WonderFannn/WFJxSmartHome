package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.WakeWordRj;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

import android.content.Context;

/**
 * 唤醒词查看请求
 * @author HeJingkai
 * @company 金鑫智慧
 */
public class WakeWordListTask extends InternetTask{
	
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String account = null;
	
	public WakeWordListTask(Context context, String account) {
		this.account = account;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		// TODO Auto-generated method stub
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_WAKE_WORD_BROWSE,
					ControlDefine.TRD_WAKE_WORD_BROWSE, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			requestBytes = getRequestByte(header, createServiceContent());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, 
				new WakeWordRj(this, requestBytes));
	}
	
	/**
	 * 创建serviceContent JSON数据体
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createServiceContent() throws JSONException {
		JSONObject serviceContent = new JSONObject();
		String _secretKey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_SECRETKEY,"");
		String _account = CommUtil.getCurrentLoginAccount();
		serviceContent.put("account", _account);
		serviceContent.put("secretKey", _secretKey);
		return serviceContent;
	}
	
}
