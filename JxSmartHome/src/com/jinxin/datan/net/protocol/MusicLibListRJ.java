package com.jinxin.datan.net.protocol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 音乐列表解析
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class MusicLibListRJ extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public MusicLibListRJ(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			List<MusicLib> cMusicList = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel())
						return;
					// 取更新时间
					String _processTime = this.getJsonString(jsonObject,
							"processTime");
					String _account = CommUtil.getCurrentLoginAccount();
					
					// 取列表数据
					JSONArray jsonArray = jsonObject
							.getJSONArray("serviceContent");
					cMusicList = new ArrayList<MusicLib>();
					for (int i = 0; i < jsonArray.length(); i++) {
						if (this.task.ismTryCancel())
							return;
						JSONObject _jo = (JSONObject) jsonArray.get(i);
						cMusicList.add(new MusicLib(getJsonInt(_jo, "id"),
								getJsonString(_jo, "name"),getJsonString(_jo, "singer"),
								getJsonString(_jo, "format"), getJsonString(_jo, "album"),
								getJsonString(_jo, "pubTime"), getJsonInt(_jo, "hot"),
								getJsonString(_jo, "area"), getJsonString(_jo, "genre"),
								getJsonString(_jo, "category"), getJsonString(_jo, "scence"),
								getJsonString(_jo, "feeling"), getJsonInt(_jo, "updateUser"),
								getJsonInt(_jo, "updateTime"), getJsonInt(_jo, "isvalid")));
					}

					isSuccess = true;
					SharedDB.saveStrDB(_account,
							ControlDefine.KEY_CLOUD_MUSIC_LIST,
							_processTime);
				}
			} catch (JSONException e) {
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(cMusicList);
				} else {
					this.task.onError(_resultInfo.validResultInfo);
				}
				this.closeInputStream(in);// 必须关闭流
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

}
