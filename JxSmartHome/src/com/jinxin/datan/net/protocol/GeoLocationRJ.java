package com.jinxin.datan.net.protocol;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 客户详情解析
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class GeoLocationRJ extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public GeoLocationRJ(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
			String response = "";
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel())
						return;
//					String _processTime = this.getJsonString(jsonObject,
//							"processTime");
					String _account = CommUtil.getCurrentLoginAccount();
					
					JSONObject _jo = jsonObject.getJSONObject("serviceContent");
					if(_jo.length() > 0)
						response = _jo.toString();
					String _processTime = this.getJsonString(_jo,
							"jsonGps");//最新位置的时间
					isSuccess = true;
					SharedDB.saveStrDB(_account,
							ControlDefine.KEY_GEO_LOCATION,
							_processTime);
				}
			} catch (JSONException e) {
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(response);
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
