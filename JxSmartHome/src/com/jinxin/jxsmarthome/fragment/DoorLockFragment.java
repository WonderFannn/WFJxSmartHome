package com.jinxin.jxsmarthome.fragment;

import java.io.UnsupportedEncodingException;
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

import com.jinxin.jxsmarthome.util.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.MyDeviceActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 智能门锁控制
 * @author HuangBinHua
 * @company 金鑫智慧
 */
public class DoorLockFragment extends DialogFragment implements OnClickListener {
	private static final int OPERATE_OPEN = 1002;
	private static final int OPERATE_CLOSE = 1001;
	private static final int SEARCE_STATE = 1003;
	
	private View view = null;
	private Button doorOpen, doorClose, doorState;

	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private ProductState productState = null;
	private ProductStateDaoImpl psdImpl = null;
	
	private String statuStr = "";
	private Context context;
	private Map<String, Object> params = null;
	private static final int FAIL = 400;
	private UIHandler uiHandler = new UIHandler();

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = CommDefines.getSkinManager().view(R.layout.control_door_lock_layout, container);
		initData();
		initView(view);
		return view;
	}

	private void initData() {
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		psdImpl = new ProductStateDaoImpl(context);
		
		if (productFun != null) {
			productState = getStateByProductFun(productFun);
			if (productState != null) {
				statuStr = productState.getState();
			}
		}
	}

	private void initView(View view) {
		this.doorOpen = (Button) view.findViewById(R.id.doorOpen);
		this.doorClose = (Button) view.findViewById(R.id.doorClose);
		this.doorState = (Button) view.findViewById(R.id.doorState);

		doorOpen.setOnClickListener(this);
		doorClose.setOnClickListener(this);
		doorState.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		params = new HashMap<String, Object>();
		switch (v.getId()) {
		case R.id.doorOpen:
			productFun.setFunType(ProductConstants.FUN_TYPE_AUTO_LOCK);
			productFun.setOpen(false);
			doorLockControl(context, productFun, funDetail, null);
			break;
		case R.id.doorClose:
			productFun.setFunType(ProductConstants.FUN_TYPE_AUTO_LOCK);
			productFun.setOpen(true);
			doorLockControl(context, productFun, funDetail, null);
			break;
		case R.id.doorState:
			productFun.setFunType(ProductConstants.FUN_TYPE_AUTO_LOCK_SEARCH);
			doorLockControl(context, productFun, funDetail, null);
			break;

		default:
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
			Map<String, Object> params) {
		List<byte[]> cmdList = CommonMethod.productFunToCMD(context, productFun, funDetail, params);
		if (cmdList == null || cmdList.size() < 1) {
			return;
		}
		byte[] cc = cmdList.get(0);
		try {
			Logger.debug("Huang",new String(cc,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] cmd = cmdList.get(0);
		CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(context, cmd, false);
		cdcbsTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, CommDefines.getSkinManager().string(R.string.qing_qiu_chu_li_zhong));
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
							JxshApp.showToast(DoorLockFragment.this.getActivity(),
									"网关离线", Toast.LENGTH_SHORT);
							return;
						}

						Message msg = uiHandler.obtainMessage();
						if (!TextUtils.isEmpty(result)) {
							String searceState = "";
							String state = result.substring(3,4);
							if (state.equals("1")) {
								msg.what = OPERATE_OPEN;
								searceState = "门锁已开";
								productState.setState("1");
								psdImpl.update(productState);
							}else if(state.equals("0")){
								msg.what = OPERATE_CLOSE;
								searceState = "门锁已关";
								productState.setState("0");
								psdImpl.update(productState);
							}
							msg.what = SEARCE_STATE;
							msg.obj = searceState;
							
							Logger.debug(null, result);
						} else {
							msg.what = FAIL;
						}
//						uiHandler.sendMessage(msg);
						msg.sendToTarget();
						BroadcastManager.sendBroadcast(
								BroadcastManager.MESSAGE_RECEIVED_ACTION, null);
					}
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {

			}
		});
		cdcbsTask.start();
	}
	
	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPERATE_OPEN:
				JxshApp.showToast(context, "操作成功");
				break;
			case OPERATE_CLOSE:
				JxshApp.showToast(context, "操作成功");
				break;
			case FAIL:
				JxshApp.showToast(context, "操作失败");
				break;
			case SEARCE_STATE:
				String searceState = (String) msg.obj;
				JxshApp.showToast(context, searceState);
				break;
			}
			super.handleMessage(msg);
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
			ps.setState("0");
			ps.setFunId(productFun.getFunId());
			psdImpl.insert(ps, false);
		}
		return ps;
	}
	
}
