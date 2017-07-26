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
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.ProductVoiceType;
import com.jinxin.jxsmarthome.entity.SingerLib;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 客户模式列表解析
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class VoiceTypeList extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public VoiceTypeList(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			List<ProductVoiceType> typeList = null;
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
					typeList = new ArrayList<ProductVoiceType>();
					for (int i = 0; i < jsonArray.length(); i++) {
						if (this.task.ismTryCancel())
							return;
						JSONObject _jo = (JSONObject) jsonArray.get(i);
						typeList.add(new ProductVoiceType(getJsonInt(_jo, "id"),
								getJsonString(_jo, "name"), getJsonInt(_jo, "type"),
								getJsonString(_jo, "describe"), getJsonString(_jo, "createUser"),
								getJsonString(_jo, "createTime"), getJsonString(_jo, "updateTime"),
								getJsonInt(_jo, "ordinal"), getJsonString(_jo, "icon")
								, getJsonInt(_jo, "status"),getJsonInt(_jo, "category")));
					}

					isSuccess = true;
					SharedDB.saveStrDB(_account,ControlDefine.KEY_VOICE_TYPE,_processTime);
				}
			} catch (JSONException e) {
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(typeList);
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
