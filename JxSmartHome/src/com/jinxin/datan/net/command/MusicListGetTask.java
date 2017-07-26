package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.MusicLibListRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 获取平台歌曲列表请求
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class MusicListGetTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String singer = "";
	private String hot = "";
	private String area = "";
	private String genre = "";
	private String category = "";
	private String scence = "";
	private String feeling = "";
	private int page = -1;
	private int rows = -1;
	
	/**
	 * 
	 * @param context
	 */
	public MusicListGetTask(Context context,String singer, String hot, String area,
			String genre, String category, String scence, String feeling,
			int page, int rows) {
		this.singer = singer;
		this.hot = hot;
		this.area = area;
		this.genre = genre;
		this.category = category;
		this.scence = scence;
		this.feeling = feeling;
		this.page = page;
		this.rows = rows;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_CLOUD_MUSIC_SINGER,
					ControlDefine.TRD_CLOUD_MUSIC, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			requestBytes = getRequestByte(header, createServiceContent());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new MusicLibListRJ(this,
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
		String _updateTime = SharedDB.loadStrFromDB(_account, ControlDefine.KEY_CLOUD_MUSIC_LIST,ControlDefine.DEFAULT_UPDATE_TIME);
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("updateTime", ControlDefine.DEFAULT_UPDATE_TIME);
		serviceContent.put("singer", singer);
		serviceContent.put("hot", hot);
		serviceContent.put("area", area);
		serviceContent.put("genre", genre);
		serviceContent.put("category", category);
		serviceContent.put("scence", scence);
		serviceContent.put("feeling", feeling);
		serviceContent.put("page", page);
		serviceContent.put("rows", rows);
		return serviceContent;
	}
}
