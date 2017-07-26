package com.jinxin.datan.net.protocol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Music;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 歌曲列表数据解析
 * 
 * @author  TangLong
 * @company 金鑫智慧
 */
public class MusicListParser extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public MusicListParser(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			List<Music> musicList = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel()) {
						return;
					}
					
					//////////////////////具体解析区////////////////////////
					JSONArray jsonArray = jsonObject.getJSONArray("serviceContent");
					musicList = new ArrayList<Music>();
					
					Logger.error(null, jsonArray.toString());
					
//					JSONObject musicJson = jsonObject.getJSONObject("serviceContent");
//					Iterator<?> keys = musicJson.keys();
//					
//					while(keys.hasNext()) {
//						String key = (String) keys.next();
//					}
					
					for (int i = 0; i < jsonArray.length(); i++) {
						if (this.task.ismTryCancel()) {
							return;
						}
						JSONObject _jo = (JSONObject) jsonArray.get(i);
						
						Music m = new Music(
								getJsonString(_jo, "musicName"), 
								null,
								getJsonString(_jo, "playTime"),
								getJsonString(_jo, "source"),
								getJsonInt(_jo, "playNo"),
								getJsonString(_jo, "iyric"),
								getJsonString(_jo, "createTime"));
						
						Logger.error("tangl", m.toString());
						musicList.add(m);
					}
					//////////////////////具体解析完成////////////////////////
					
					isSuccess = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				isSuccess = false;
			} catch (Exception e) {
				e.printStackTrace();
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(musicList);
				} else {
					this.task.onError(_resultInfo.validResultInfo);
				}
				this.closeInputStream(in);
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

}
