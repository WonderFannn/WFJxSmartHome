package com.jinxin.jxsmarthome.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 无线锁控制
 * @author YangJijun
 * @company 金鑫智慧
 */
public class WirelessLockFragment extends DialogFragment implements OnClickListener{
	private Context context;
	private View view = null;
	private TextView mPower, mWarring, mState;
	private ImageView stateImg;
	private Button btnSwitch, btnCheakState;
	
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private Map<String, Object> params;
	private ProductStateDaoImpl psdImpl = null;
	private ProductState productState = null;
	private UIHandler uiHandler = new UIHandler();
	
	private static final int OPERATE_OPEN = 1002;
	private static final int OPERATE_CLOSE = 1001;
	private static final int SUCCESS = 200;
	private static final int FAIL = 400;
	private static final int LOCK_WARRING = 404;
	private static final int LOW_POWER = 600;
	private static final int NORMOL_POWER = 800;
	
	private boolean isClosed = true;//是否已关闭
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
		setHasOptionsMenu(true);
	}
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = CommDefines.getSkinManager().view(R.layout.fragment_wirless_lock_control, container);
		initData();
		initView(view);
		return view;
	}

	private void initData() {
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		psdImpl = new ProductStateDaoImpl(getActivity());
		params = new HashMap<String, Object>();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastManager.MESSAGE_WARN_LOW_POWER);
		filter.addAction(BroadcastManager.MESSAGE_WARN_ERROR);
		// 刷新设备状态
		getActivity().registerReceiver(mShowBroadcastReceiver, filter);
		
		if (productFun != null) {
			productState = getStateByProductFun(productFun);
		}
	}

	private void initView(View view) {
		mPower = (TextView) this.view.findViewById(R.id.tv_low_power);
		mWarring = (TextView) this.view.findViewById(R.id.tv_safety_warring);
		mState = (TextView) this.view.findViewById(R.id.tv_lock_state);
		stateImg = (ImageView) this.view.findViewById(R.id.iv_state_backgroind);
		btnSwitch = (Button) this.view.findViewById(R.id.btn_switch);
		btnCheakState = (Button) this.view.findViewById(R.id.btn_check_state);
		
		btnSwitch.setOnClickListener(this);
		btnCheakState.setOnClickListener(this);
		
		//低电量显示
		if (productState != null) {
			if ("FF".equals(productState.getState())) {
				mPower.setVisibility(View.VISIBLE);
			}else{
				mPower.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		params.clear();
		switch (v.getId()) {
		case R.id.btn_switch://开锁,锁会自动关，只发开锁指令
			params.put("src", "0x01");
			params.put("dst", "0x01");
			doorLockControl(context, productFun, funDetail, params, "open");
			break;
		case R.id.btn_check_state://查询状态
			params.put("src", "0x01");
			params.put("dst", "0x01");
			params.put("op", "C2");
			doorLockControl(context, productFun, funDetail, params, "readStatus");
			break;
		}
	}
	
	/**
	 * 门锁控制 发送指令
	 * @param context
	 * @param productFun
	 * @param funDetail
	 */
	public void doorLockControl(final Context context, final ProductFun productFun, FunDetail funDetail,
			Map<String, Object> params, String type) {

		if (productFun == null || funDetail == null)
			return;

		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			// String zegbingWhId =
			// AppUtil.getGetwayWhIdByProductWhId(getActivity(),
			// productFun.getWhId());
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(
					getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(
					getActivity(), zegbingWhId);
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(),
					productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(
					getActivity(), localHost + ":3333", cmdList, true, false);
			// offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(),
					productFun, funDetail, params, type);
			// OnlineCmdSenderLong onlineSender = new
			// OnlineCmdSenderLong(getActivity(),
			// DatanAgentConnectResource.HOST_ZEGBING,true, cmdList, true, 0,
			// false);
			// onlineSender.addListener(listener);
			// onlineSender.send();

			if (cmdList == null || cmdList.size() <1) {
				Logger.debug(null, "cmd is null");
				return;
			}
			CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(
					context, cmdList.get(0), false);
			cdcbsTask.setRequestHost(DatanAgentConnectResource.HOST_ZEGBING, 3);
			cdcbsTask.addListener(new ITaskListener<ITask>() {

				@Override
				public void onStarted(ITask task, Object arg) {
					JxshApp.showLoading(context, CommDefines.getSkinManager()
							.string(R.string.qing_qiu_chu_li_zhong));
				}

				@Override
				public void onCanceled(ITask task, Object arg) {
					JxshApp.closeLoading();
				}

				@Override
				public void onFail(ITask task, Object[] arg) {
					JxshApp.closeLoading();
				}

				@Override
				public void onSuccess(ITask task, Object[] arg) {
					JxshApp.closeLoading();
					if (arg != null && arg.length > 0) {
						RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];

						if (resultObj.validResultCode.equals("0000")) {
							String result = resultObj.validResultInfo;

							if ("01".equals(result) || "02".equals(result)) {
								JxshApp.showToast(
										WirelessLockFragment.this.getActivity(),
										"网关离线", Toast.LENGTH_SHORT);
								return;
							}

							Message msg = uiHandler.obtainMessage();
							if (!TextUtils.isEmpty(result)) {
								Logger.debug(null, result);
								String[] resultArr = result.split(" ");
								if (resultArr != null && resultArr.length > 4) {
									Logger.debug(null, "length = "+resultArr.length);
									if("C2".equals(resultArr[3])){
										if (resultArr.length == 10) {//状态查询
											String state = resultArr[7];
											Logger.debug(null, "state = "+state);
											if ("00".equals(state)) {
												msg.what = OPERATE_OPEN;
											}else{
												msg.what = OPERATE_CLOSE;
											}
										}else if(resultArr.length == 6){//开锁
											String state = resultArr[5];
											if ("00".equals(state)) {
												msg.what = SUCCESS;
											}else{
												msg.what = FAIL;
											}
										}
									}
								}else {
									msg.what = FAIL;
								}
							} else {
								msg.what = FAIL;
							}
							// uiHandler.sendMessage(msg);
							msg.sendToTarget();
							BroadcastManager.sendBroadcast(
									BroadcastManager.MESSAGE_RECEIVED_ACTION,null);
						}
					}
				}

				@Override
				public void onProcess(ITask task, Object[] arg) {
				}
			});
			cdcbsTask.start();
		}
	}
	
	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPERATE_OPEN:
				if (!isClosed)  return;
				
				autoCloseLock();
				JxshApp.showToast(context, "门锁已开");
				mState.setText("当前状态：开");
				stateImg.setImageResource(R.drawable.ico_lock_open);
				break;
			case OPERATE_CLOSE:
				JxshApp.showToast(context, "门锁已关");
				mState.setText("当前状态：关");
				stateImg.setImageResource(R.drawable.ico_lock_close);
				isClosed = true;
				break;
			case LOW_POWER:
				mPower.setVisibility(View.VISIBLE);
				break;
			case SUCCESS:
				if (!isClosed)  return;
				
				autoCloseLock();
				JxshApp.showToast(context, "门锁已开");
				stateImg.setImageResource(R.drawable.ico_lock_open);
				mState.setText("当前状态：开");
				break;
			case FAIL:
				JxshApp.showToast(context, "操作失败");
				break;
			case LOCK_WARRING:
				mWarring.setVisibility(View.VISIBLE);
				mWarring.setText((String)msg.obj);
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	/**
	 * 门锁自动关闭
	 */
	private void autoCloseLock(){
		isClosed = false;
		uiHandler.sendEmptyMessageDelayed(OPERATE_CLOSE, 5000);
	}
	
	/* 自定义广播，用于接受推送信息的改变 */
	private BroadcastReceiver mShowBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BroadcastManager.MESSAGE_WARN_LOW_POWER.equals(intent.getAction())) {
				String power = intent.getStringExtra("power");
				String whId = intent.getStringExtra("whId");
				if (!TextUtils.isEmpty(whId) && whId.equals(productFun.getWhId())) {
					productState.setState(power);
					if ("FF".equals(power)) {
						uiHandler.sendEmptyMessage(LOW_POWER);
					}else{
						uiHandler.sendEmptyMessage(NORMOL_POWER);
					}
				}
			}else if(BroadcastManager.MESSAGE_WARN_ERROR.equals(intent.getAction())){
				
				String msg = intent.getStringExtra("msg");
				String whId = intent.getStringExtra("whId");
				if (!TextUtils.isEmpty(whId)) {
					if (whId.equals(productFun.getWhId()) && !TextUtils.isEmpty(msg)) {
						Message message = uiHandler.obtainMessage();
						if (msg.endsWith("开")) {
//							message.what = OPERATE_OPEN;
						}else{
							message.what = LOCK_WARRING;
						}
						message.obj = msg;
						message.sendToTarget();
					}
				}
			}
		}
	};
	
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
			ps.setState("64");
			ps.setFunId(productFun.getFunId());
			psdImpl.insert(ps, false);
		}
		return ps;
	}
	
}
