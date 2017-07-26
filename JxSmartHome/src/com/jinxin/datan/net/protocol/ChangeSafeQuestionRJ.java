package com.jinxin.datan.net.protocol;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;

/**修改密保问题及答案返回数据解析
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ChangeSafeQuestionRJ extends ResponseJson {
	
	private Task task = null;
	private byte[] requestBytes;
	
	public ChangeSafeQuestionRJ(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {

		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
			
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);
								
				_resultInfo = this
						.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel()) {
						this.task.onCancel("取消反馈");
						return;
					}
					isSuccess = true;
				}
			} catch(JSONException e){
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			}
			finally {
				 if(isSuccess){
			            this.task.callback("修改密保信息成功");
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
