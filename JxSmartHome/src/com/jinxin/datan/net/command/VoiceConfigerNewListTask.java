package com.jinxin.datan.net.command;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.VoiceConfigList;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.db.impl.ProductVoiceConfigDaoImpl;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.DateUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 获取新增语音配置请求
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class VoiceConfigerNewListTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private int typeId = -1;
	private Context context = null;
	/**
	 * 
	 * @param context
	 */
	public VoiceConfigerNewListTask(Context context, int typeId) {
		this.typeId = typeId;
		this.context = context;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_UPDATE_PRODUCT_VOICE_CONFIGER,
					ControlDefine.TRD_SET_VOICE_IDENTIFY, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			requestBytes = getRequestByte(header, createServiceContent());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new VoiceConfigList(this,
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
		String _updateTime = SharedDB.loadStrFromDB(_account, ControlDefine.KEY_VOICE_CONFIG_NEW,ControlDefine.DEFAULT_UPDATE_TIME);
		ProductVoiceConfigDaoImpl pvcDaoImpl = new ProductVoiceConfigDaoImpl(context);
		List<ProductVoiceConfig> list = pvcDaoImpl.find(
				null, "typeId=?", new String[]{Integer.toString(typeId)}, null, null, "updateTime DESC", null);
		if (list != null && list.size() > 0) {
			_updateTime = list.get(0).getUpdateTime();
			_updateTime = DateUtil.convertLongToStr1(Long.parseLong(_updateTime));
			_updateTime = _updateTime.replace("-", "");
			_updateTime = _updateTime.replace(" ", "");
			_updateTime = _updateTime.replace(":", "");
		}
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("updateTime", _updateTime);
		serviceContent.put("typeId", typeId);
		return serviceContent;
	}
}
