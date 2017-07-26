package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.AddCustomerMusicRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 添加音乐收藏
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class AddCustomerMusicTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private int musicId = -1;
	private String musicName = null;
	private String singer = null;
	private int enjoy = -1;
	/**
	 * 
	 * @param context
	 */
	public AddCustomerMusicTask(Context context, int musicId,String singer, String musicName,int enjoy) {
		this.musicId = musicId;
		this.musicName = musicName;
		this.singer = singer;
		this.enjoy = enjoy;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_CLOUD_MUSIC_STORE,
					ControlDefine.TRD_CLOUD_MUSIC_ADD_FAV, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new AddCustomerMusicRJ(this,
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
//		String _updateTime = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_CUSTOMER_PRODUCT_CMD_LIST,ControlDefine.DEFAULT_UPDATE_TIME);
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("musicId", musicId);
		serviceContent.put("singer", singer);
		serviceContent.put("musicName", musicName);
		serviceContent.put("enjoy", enjoy);
//		serviceContent.put("updateTime", _updateTime);
		return serviceContent;
	}
}
