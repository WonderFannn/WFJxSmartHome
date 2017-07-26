package com.jinxin.datan.net.protocol;

import org.json.JSONObject;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;

import java.io.InputStream;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Json方式通信接口解析示例@@@@@@@@@@@@@@@@@@@@@@@@@@@ 
 *                                                                            启动时联网解析
 * @author zj
 * 
 */
public class Startup extends ResponseJson {
	private Task task = null;

	private Startup(Task task) {
		this.task = task;
	}

	@Override
	public void response(InputStream in) throws Exception {
		// TODO Auto-generated method stub
		if (in != null) {
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);
				RemoteJsonResultInfo _resultInfo = this
						.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals("****")) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel()) {
						this.task.onCancel("取消下载需要返回给前端的数据");
						return;
					}
				}
			} finally {
				 if("解析成功" != null){
			            this.task.callback("成功回调给前端展示的数据信息");
			        }else{
			        	this.task.onError("失败回调给前端展示的数据信息");
			        }
				this.closeInputStream(in);//必须关闭流
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}
