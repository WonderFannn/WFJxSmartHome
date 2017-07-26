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
import com.jinxin.jxsmarthome.entity.Feedback;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;
/**
 * 意见反馈
 * @company 金鑫智慧
 */
public class FeedbackListRJ extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;
	
	public FeedbackListRJ(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}
	
	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
			
			List<Feedback> feedbackEntry = new ArrayList<Feedback>();
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);
								
				_resultInfo = this
						.readResultInfo(jsonObject);
				
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					
					String _processTime = this.getJsonString(jsonObject,
							"processTime");
					JSONArray jsonArray = jsonObject.getJSONArray("serviceContent");
					for(int i =0;i<jsonArray.length();i++) {
						JSONObject js = jsonArray.getJSONObject(i);
						Feedback fb = new Feedback();
						fb.setContent(this.getJsonString(js, "content"));
						fb.setReply(this.getJsonString(js, "reply"));
						fb.setMessageId(this.getJsonInt(js, "id"));
						fb.setResult(this.getJsonString(js, "result"));
						fb.setMessageType(this.getJsonInt(js, "messageType")>0?this.getJsonInt(js, "messageType"):1);
						fb.setReplyTime(this.getJsonString(js, "replyTime"));
						fb.setCreTime(Long.valueOf(this.getJsonString(js, "createTime")));
						fb.setImageUrl1(this.getJsonString(js, "imageUrl1"));
						fb.setImageUrl2(this.getJsonString(js, "imageUrl2"));
						
						feedbackEntry.add(fb);
					}
					///////////////////////////////////////
					isSuccess = true;
					String _account = CommUtil.getCurrentLoginAccount();
					SharedDB.saveStrDB(_account,
							ControlDefine.KEY_FEEDBACK_LIST,
							_processTime);
				}
			} catch(JSONException e){
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			}
			finally {
				 if(isSuccess){
			            this.task.callback(feedbackEntry);
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
