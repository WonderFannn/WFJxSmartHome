package com.jinxin.datan.net.protocol;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
/**
 * 新增意见反馈信息
 * @author BinhuaHuang
 * @company 金鑫智慧
 */
public class AddSuggestFeedback extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;
	
	public AddSuggestFeedback(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}
	
	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
			int taskId = -1;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);
				System.out.println("AddSuggestFeedback:" + jsonObject.toString());
				
				_resultInfo = this
						.readResultInfo(jsonObject);
				
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					isSuccess = true;
				}
			} catch(JSONException e){
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			}
			finally {
				 if(isSuccess){
			            this.task.callback(Integer.toString(taskId));
			        }else{
			        	this.task.onError(_resultInfo.validResultInfo);
			        }
				this.closeInputStream(in);//必须关闭流
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

}
