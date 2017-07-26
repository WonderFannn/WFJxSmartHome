package com.jinxin.datan.net.command;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.SearchMusicRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;

/**搜索百度音乐请求任务
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class SearchMusicTask extends InternetTask {
	
	private int type = 0;
	private String songName;
	private String singerName;
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	
	public SearchMusicTask(Context context,int type, String songName,String singerName ) {
		this.type = type;
		this.songName = songName;
		this.singerName = singerName;
		init();
		setHttpType((byte)1);//设置get请求
		setNetRequest(context, this.request, null);
	}
	
	private void init() {
		byte[] requestBytes = null;
		String url = null;
		try {
			// String url = DatanAgentConnectResource.HTTP_SEARCHPATH_ALL + type+"/"+songName+"/"+singerName;
			StringBuffer _buff = new StringBuffer();
			_buff.append(DatanAgentConnectResource.HTTP_SEARCHPATH_ALL).append(
					type);
			_buff.append("/");
			if (!TextUtils.isEmpty(songName)) {
				_buff.append(Uri.encode(songName, "utf-8"));
			}
			_buff.append("/");
			if (!TextUtils.isEmpty(singerName)) {
				_buff.append(Uri.encode(singerName, "utf-8"));
			}
			url = _buff.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(url,new SearchMusicRJ(this, requestBytes));

	}
	
}
