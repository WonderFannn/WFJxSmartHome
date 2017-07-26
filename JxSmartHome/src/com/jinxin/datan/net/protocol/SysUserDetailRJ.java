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
public class SysUserDetailRJ extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public SysUserDetailRJ(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			// List<LoginEntry> loginEntry = null;
			SysUser sysUser = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel())
						return;
					// ////具体解析区////////////////////////
					// 取更新时间
					String _processTime = this.getJsonString(jsonObject,
							"processTime");
					String _account = CommUtil.getCurrentLoginAccount();
					
					JSONObject _jo = jsonObject.getJSONObject("serviceContent");
					if(_jo.length() > 0)
						sysUser = new SysUser(getJsonInt(_jo, "userId"),
							getJsonString(_jo, "account"), "", getJsonString(_jo, "userName"),
							getJsonString(_jo, "idNo"), getJsonString(_jo,
									"nickyName"), getJsonInt(_jo, "userType"),
									getJsonInt(_jo, "areaId"), getJsonInt(_jo,
									"orgId"), getJsonInt(_jo, "enable"),
									getJsonInt(_jo, "sex"), getJsonInt(_jo,
									"age"), getJsonString(_jo,
									"avatar"), getJsonString(_jo,
									"inxCode"), getJsonString(_jo,
									"mobile"), getJsonString(_jo, "urgContacts"),
									getJsonString(_jo, "tel"), getJsonString(_jo,
									"email"), getJsonString(_jo,
									"address"),
									getJsonInt(_jo, "creatorId"), getJsonString(_jo,
									"createdTime"),
									getJsonInt(_jo, "editorId"), getJsonString(_jo,
											"editedTime"),
									getJsonString(_jo, "comments"), getJsonString(_jo,
													"question1"),
									getJsonString(_jo, "question2"), getJsonString(_jo,
															"answer1"),
									getJsonString(_jo,"answer2"));
					// /////////////////////////////////////
					isSuccess = true;
					SharedDB.saveStrDB(_account,
							ControlDefine.KEY_USER_DETAIL_LIST,
							_processTime);
				}
			} catch (JSONException e) {
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(sysUser);
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
