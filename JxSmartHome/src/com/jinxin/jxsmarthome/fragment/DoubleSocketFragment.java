package com.jinxin.jxsmarthome.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 双向插座
 * @author YangJijun
 * @company 金鑫智慧
 */
public class DoubleSocketFragment extends DialogFragment implements OnClickListener{
	
	private View view = null;
	private ImageView leftSwitch, rightSwitch;
	
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private ProductState productState = null;
	private Context context;
	private Map<String, Object> params = null;
	private String statuStr = "0000";//前2个字符“00”代表左开关状态，后二个代表右开关状态 ,"AA"代表设备禁用
	private ProductStateDaoImpl psdImpl = null;
	
	private String leftState = "";
	private String rightState = "";
	private boolean isLeftOpen = false;
	private boolean isRightOpen = false;
	
	private TaskListener<ITask> listener;
	private int leftOrRight = -1;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				leftSwitch.setImageResource(isLeftOpen ? R.drawable.icon_light_open : R.drawable.icon_light_close);
				rightSwitch.setImageResource(isRightOpen? R.drawable.icon_light_open : R.drawable.icon_light_close);
				BroadcastManager.sendBroadcast(
						BroadcastManager.MESSAGE_RECEIVED_ACTION, null);
				break;

			default:
				break;
			}
			
			super.handleMessage(msg);
		}
		
	};
	
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
		view=CommDefines.getSkinManager().view(R.layout.fragment_double_socket_layout, container);
		initData();
		initView(view);
		return view;
	}

	private void initData() {
		params = new HashMap<String, Object>();
		psdImpl = new ProductStateDaoImpl(context);
		
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		if (productFun != null) {
			productState = getStateByProductFun(productFun);
			if (productState != null) {
				statuStr = productState.getState();
				if (!TextUtils.isEmpty(statuStr) && statuStr.length() > 1) {
					leftState = statuStr.substring(0,2);
					rightState = statuStr.substring(2);
					
				}else{
					leftState = "00";
					rightState = "00";
				}
				isLeftOpen = leftState.equals("01");
				isRightOpen = rightState.equals("01");
			}
		}
		
		listener = new TaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.showToast(getActivity(), "操作失败");
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (leftOrRight == 0) {//控制左边
					isLeftOpen = !isLeftOpen;
					leftState = isLeftOpen ? "01" : "00";//改左边状态
				}else{//控制右边
					isRightOpen = !isRightOpen;
					rightState = isRightOpen ? "01" : "00";//改右边状态
				}
				statuStr = leftState + rightState;
				productState.setState(statuStr);
				psdImpl.update(productState);
				handler.sendEmptyMessageDelayed(0, 300);
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		};
	}

	private void initView(View view) {
		this.leftSwitch = (ImageView) view.findViewById(R.id.iv_left_button);
		this.rightSwitch = (ImageView) view.findViewById(R.id.iv_right_button);
		
		leftSwitch.setImageResource(isLeftOpen ? R.drawable.icon_light_open : R.drawable.icon_light_close);
		rightSwitch.setImageResource(isRightOpen? R.drawable.icon_light_open : R.drawable.icon_light_close);
		
		this.leftSwitch.setOnClickListener(this);
		this.rightSwitch.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_left_button://左开关
			leftOrRight = 0;
			operateWirelessSocket(productFun, isLeftOpen, leftOrRight);
			break;
		case R.id.iv_right_button://右开关
			leftOrRight = 1;
			operateWirelessSocket(productFun, isRightOpen, leftOrRight);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 无线插座指令发送
	 * @param productFun
	 * @param productState
	 */
	private void operateWirelessSocket(ProductFun productFun, boolean isOpen, int leftOrRight) {
		if(productFun == null ) return;
		
		if (leftOrRight == 0) {//控制左
			params.put("dst", "0x01");
		}else{//控制右
			params.put("dst", "0x02");
		}
		params.put("src", "0x01");
//		String type = productFun.isOpen() ? "off" : "on";
		String type = isOpen ? "off" : "on";
		
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
//			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			if(localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(getActivity(), "未找到对应网关", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdList, true, false);
			offlineSender.addListener(listener);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), 
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 0, false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	private ProductState getStateByProductFun(ProductFun productFun){
		if (productFun == null) {
			return null;
		}
		ProductState ps = null;
		List<ProductState> list = psdImpl.find(null, "funId=?", new String[]{
					Integer.toString(productFun.getFunId())}, null, null, null, null);
		if (list != null && list.size() > 0) {
			ps = list.get(0);
		}
		
		if (ps == null) {
			ps = new ProductState();
			ps.setState("0000");
			ps.setFunId(productFun.getFunId());
			psdImpl.insert(ps, false);
		}
		return ps;
	}
	
}
