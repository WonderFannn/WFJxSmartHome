package com.jinxin.jxsmarthome.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.command.GatewayStateTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.Logger;

public class GateWayFragment extends DialogFragment implements OnClickListener{

	private Context context = null;
	private ProductFun productFun;
	private FunDetail funDetail;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=CommDefines.getSkinManager().view(R.layout.gateway_fragment, container);
		initData();
		initView(view);
		return view;
	}
	
	private void initData() {
		this.productFun = (ProductFun) getArguments().getSerializable("productFun");
		this.funDetail = (FunDetail) getArguments().getSerializable("funDetail");
	}

	private void initView(View view) {
		view.findViewById(R.id.build_gateway_btn).setOnClickListener(this);
		view.findViewById(R.id.gateway_state_btn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.build_gateway_btn://组网
			if (productFun == null || funDetail == null) {
				Logger.debug(null, "productFun || funDetail is null");
				return;
			}
			
			Logger.error("=====", productFun.toString());
			Logger.error("=====", funDetail.toString());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("text", "0x3c");//十六进制时间:0x3c = 60s
			String type= "networking";
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(context, productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(context, 
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 0,false);
			onlineSender.addListener(new WirelessTaskListener());
			onlineSender.send();
			break;
		case R.id.gateway_state_btn://查询网关状态
			if (productFun != null){
				GatewayStateTask gsTask = new GatewayStateTask(context, productFun.getWhId());
				gsTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						JxshApp.showToast(context, "无法获取网关状态");
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						if (arg != null && arg.length > 0) {
							JxshApp.showToast(context, "网关在线");
						}
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
					}
				});
				gsTask.start();
			}
			break;
		}
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
			JxshApp.showToast(context, "处理超时");
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			if (arg != null && arg.length > 0) {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo)arg[0];
				Logger.debug(null, resultObj.toString());
				
				// "0000":正常的返回   "-1":结果不需要做解析
				if(resultObj.validResultCode.equals("0000") && !"-1".equals(resultObj.validResultInfo)) {
//					String result = resultObj.validResultInfo;
					JxshApp.showToast(context, "组网成功");
				}
			}
		}

		@Override
		public void onProcess(ITask task, Object[] arg) {
			
		}
	};
	
	
}
