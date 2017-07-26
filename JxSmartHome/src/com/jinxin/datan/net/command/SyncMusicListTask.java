package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.MusicListParser;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**
 * 以更新方式获取歌曲播放列表
 * @author 	TangLong
 * @company 金鑫智慧
 */
public class SyncMusicListTask extends InternetTask {
	private NetRequest request = null;
	private String source = null;
	private String updateType = null;		// 固定为：get:获取歌曲列表, sync：同步歌曲列表 
	private String whId = null;
	
	public SyncMusicListTask(Context context, String source, String updateType, String whId) {
		this.source = source;
		this.updateType = updateType;
		this.whId = whId;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		
		try {
			Header header = null;
			if("get".equals(updateType)) {
				header = new Header(ControlDefine.BS_UPDATE_MUSIC_LIST,
						ControlDefine.TRD_UPDATE_MUSIC_LIST, ControlDefine.ACTION_REQUEST,
						StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			}else if("sync".equals(updateType)) {
				header = new Header(ControlDefine.BS_SYNC_MUSIC_LIST,
						ControlDefine.TRD_SYNC_MUSIC_LIST, ControlDefine.ACTION_REQUEST,
						StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			}else {
				// 暂时不处理
			}
			requestBytes = getRequestByte(header, createServiceContent());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new MusicListParser(this,
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
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("source", source);
		serviceContent.put("whId", whId);
		return serviceContent;
	}
} 
