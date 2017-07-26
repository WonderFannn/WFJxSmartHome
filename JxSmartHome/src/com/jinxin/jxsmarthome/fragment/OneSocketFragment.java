package com.jinxin.jxsmarthome.fragment;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class OneSocketFragment extends DialogFragment {

	public static final int UPDATE_UI = 1;

	/**
	 * 产品类
	 */
	private ProductFun productFun;
	/**
	 * 设备类
	 */
	private FunDetail funDetail;
	/**
	 * 命令参数
	 */
	private Map<String, Object> params;
	/**
	 * 开关图标
	 */
	private ImageView ivSocket;
	/**
	 * 开关状态
	 */
	private boolean isOpen;
	/**
	 * 命令监听器
	 */
	private TaskListener<ITask> listener;
	
	private boolean isClickable = true;

	private UIHandler mHandler = new UIHandler(this);
	
	private ProductStateDaoImpl psdImpl;
	private ProductState ps;

	static class UIHandler extends Handler {
		WeakReference<OneSocketFragment> wrFragment;

		UIHandler(OneSocketFragment df) {
			wrFragment = new WeakReference<OneSocketFragment>(df);
		}

		@Override
		public void handleMessage(Message msg) {
			OneSocketFragment fragment = wrFragment.get();
			switch (msg.what) {
			case UPDATE_UI:
				fragment.ivSocket
						.setImageResource(fragment.ps.getState().equals("01") ? R.drawable.icon_light_open : R.drawable.icon_light_close);
				break;
			}
		}
	}

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = CommDefines.getSkinManager().view(R.layout.fragment_one_socket_layout, container);
		initData();
		initView(view);
		return view;
	}

	protected void initData() {
		productFun = (ProductFun) getArguments().get("productFun");
		psdImpl = new ProductStateDaoImpl(getActivity());
		funDetail = (FunDetail) getArguments().get("funDetail");
		params = new HashMap<String, Object>();
		initState();
		listener = new TaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {

			}

			@Override
			public void onCanceled(ITask task, Object arg) {

			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				isClickable = true;
				if (arg[0] != null) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					JxshApp.showToast(getActivity(), resultObj.validResultInfo);
				} else {
					JxshApp.showToast(getActivity(), "操作失败");
				}
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				isClickable = true;
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				if ("-1".equals(resultObj.validResultInfo)) {
					JxshApp.showToast(getActivity(), getActivity().getString(R.string.mode_contorl_fail));
					return;
				}
				JxshApp.showToast(getActivity(), "操作成功");
				isOpen = !isOpen;
				ps.setState(ps.getState().equals("01")?"00":"01");
				psdImpl.update(ps);
				mHandler.obtainMessage(UPDATE_UI).sendToTarget();
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {

			}
		};
		mHandler.obtainMessage(UPDATE_UI).sendToTarget();
	}

	protected void initView(View view) {
		ivSocket = (ImageView) view.findViewById(R.id.iv_one_socket);
		ivSocket.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isClickable) {
					JxshApp.showToast(getActivity(), getActivity().getString(R.string.operate_invalid_work));
					return;
				}
				operateWirelessSocket(productFun, isOpen);
			}
		});
	}

	/**
	 * 单路开关指令发送
	 * 
	 * @param productFun
	 * @param isOpen
	 */
	private void operateWirelessSocket(ProductFun productFun, boolean isOpen) {
		if (productFun == null)
			return;
		isClickable = false;
		params.put("src", "0x01");
		params.put("dst", "0x01");
		String type = isOpen ? "off" : "on";

		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			if (localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(getActivity(), "未找到对应网关", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), localHost + ":3333", cmdList,
					true, false);
			offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 0, false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	protected void initState(){
		List<ProductState> psList = psdImpl.find(null, "funId = ?", new String[]{String.valueOf(productFun.getFunId())}, null, null, null, null);
		if(psList!=null&&psList.size()>0)
			ps = psList.get(0);
		else{
			ps = new ProductState(productFun.getFunId(), "00");
			psdImpl.insert(ps, false);
		}
		isOpen =ps.getState().equals("01");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		BroadcastManager.sendBroadcast(
				BroadcastManager.MESSAGE_RECEIVED_ACTION,null);
	}
}
