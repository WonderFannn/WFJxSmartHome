package com.jinxin.jxsmarthome.cmd.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.entity.ZigbeeResponse;
import com.jinxin.jxsmarthome.cmd.response.RegexConstants;
import com.jinxin.jxsmarthome.cmd.response.ResponseParserFactory;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;

public class WirelessCurtainLoader {
	private Context context;
	private OnDataLoadListener loadListener;
	
	public WirelessCurtainLoader(Context context, OnDataLoadListener loadListener) {
		this.context = context;
		this.loadListener = loadListener;
	}
	
	class WirelessTaskListener extends TaskListener<ITask> {

		@Override
		public void onStarted(ITask task, Object arg) {
			
		}

		@Override
		public void onCanceled(ITask task, Object arg) {
			
		}

		@Override
		public void onFail(ITask task, Object[] arg) {
			((Activity) context).runOnUiThread( new Runnable() {
				@Override
				public void run() {
					ZigbeeResponse res = new ZigbeeResponse();
					res.setPayload("-1 -1  获取失败 ");
					loadListener.onDataLoaded(res);
				}
			});
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			if (arg != null && arg.length > 0) {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo)arg[0];
				Logger.debug(null, resultObj.toString());
				
				// "0000":正常的返回   "-1":结果不需要做解析
				if(resultObj.validResultCode.equals("0000") && !"-1".equals(resultObj.validResultInfo)) {
					String result = resultObj.validResultInfo;
					final ZigbeeResponse res = parseResponseData(result);
					Logger.debug(null, res.toString());
					((Activity) context).runOnUiThread( new Runnable() {
						@Override
						public void run() {
							loadListener.onDataLoaded(res);
						}
					});
				}
			}
		}

		@Override
		public void onProcess(ITask task, Object[] arg) {
			
		}
	};
	
	/**
	 * 获取窗帘状态
	 */
	public void loadData(ProductFun productFun, FunDetail funDetail) {
		Logger.debug(null, "load data...");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("src", "0x01");
		params.put("dst", StringUtils.integerToHexString(Integer.parseInt(productFun.getFunUnit())));
		System.out.println("dst:"+StringUtils.integerToHexString(Integer.parseInt(productFun.getFunUnit())));
		if (NetworkModeSwitcher.useOfflineMode(context)) {
			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(context, productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(context, zegbingWhId);
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(context, productFun, funDetail, params, "readPercent");
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(context, 
					localHost + ":3333", cmdList, true,false);
			offlineSender.addListener(new WirelessTaskListener());
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(context, productFun, funDetail, params, "readPercent");//Huang readPercent from kai.chen
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(context, 
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 1,false);
			onlineSender.addListener(new WirelessTaskListener());
			onlineSender.send();
		}
	}
	
	/**
	 * 解析结果
	 * 	返回数据模板：len=0x04 sender=0xCFB4 profile=0x0104 cluster=0x0102 dest=0x01 sour=0x02 payload[08 02 04 00 ]
	 */
	private ZigbeeResponse parseResponseData(String result) {
		Logger.debug(null, "result:" + result);
		return ResponseParserFactory.parseContent(result, RegexConstants.ZG_BASIC_CONTENT_REP);
	}
	
	public interface OnDataLoadListener {
		public void onDataLoaded(ZigbeeResponse data);
	}
	
}
 