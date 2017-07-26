package com.jinxin.jxsmarthome.cmd.device;

import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.Environment;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;

public class EnvironmentLoader {
	private Context context;
	private OnDataLoadListener loadListener;
	
	public EnvironmentLoader(Context context, OnDataLoadListener loadListener) {
		this.context = context;
		this.loadListener = loadListener;
	}
	
	private TaskListener<ITask> listener = new TaskListener<ITask>() {

		@Override
		public void onStarted(ITask task, Object arg) {
			
		}

		@Override
		public void onCanceled(ITask task, Object arg) {
			
		}

		@Override
		public void onFail(ITask task, Object[] arg) {
			final Environment data = new Environment();
			data.setKq("无");
			data.setWd("无");
			data.setSd("无");
			((Activity) context).runOnUiThread( new Runnable() {
				@Override
				public void run() {
					loadListener.onDataLoaded(data);
				}
			});
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			if (arg != null && arg.length > 0) {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo)arg[0];
				Logger.debug(null, resultObj.toString());
				
				if(resultObj.validResultCode.equals("0000")) {
					String result = resultObj.validResultInfo;
					if (result != null) {
						final Environment data = parseEnvironmentData(result);
						((Activity) context).runOnUiThread( new Runnable() {
							@Override
							public void run() {
								loadListener.onDataLoaded(data);
							}
						});
					} 
					
				}
			}
		}

		@Override
		public void onProcess(ITask task, Object[] arg) {
			
		}
	};
	
	public void loadData(ProductFun productFun, FunDetail funDetail) {
		productFun.setFunType(ProductConstants.FUN_TYPE_UFO1_TEMP_HUMI);
		final List<byte[]> cmds = CommonMethod.productFunToCMD(context, productFun, funDetail, null);
		if(NetworkModeSwitcher.useOfflineMode(context)) {
			OfflineCmdSenderLong offlineCmdSender = new OfflineCmdSenderLong(context, cmds);
			offlineCmdSender.addListener(listener);
			offlineCmdSender.send();
		}else {
			OnlineCmdSenderLong onlineCmdSender = new OnlineCmdSenderLong(context, cmds);
			onlineCmdSender.addListener(listener);
			onlineCmdSender.send();
		}
	}
	
	private Environment parseEnvironmentData(String result) {
		Environment en = new Environment();
		if(!StringUtils.isEmpty(result) && result.length() >= 11) {
			
			String airQuality = result.substring(5, 7);
			String temperature = result.substring(7, 9);
			String humidity = result.substring(9, 11);
			
			StringBuilder sb = new StringBuilder();
			try {
				if(Integer.parseInt(airQuality) < 50) {
					sb.append("优");
				}else if(Integer.parseInt(airQuality) < 100) {
					sb.append("良好");
				}else if(Integer.parseInt(airQuality) < 200) {
					sb.append("轻度污染");
				}else if(Integer.parseInt(airQuality) < 300) {
					sb.append("中度污染");
				}else if(Integer.parseInt(airQuality) > 300)  {
					sb.append("重度污染");
				}
				sb.append(airQuality);
				en.setKq(sb.toString());
				en.setWd(temperature + "℃");
				en.setSd(humidity + "%");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return en;
	}
	
	public interface OnDataLoadListener {
		public void onDataLoaded(Environment data);
	}
	
}
