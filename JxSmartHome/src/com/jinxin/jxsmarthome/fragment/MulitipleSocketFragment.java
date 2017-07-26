package com.jinxin.jxsmarthome.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
import android.widget.ListView;
import android.widget.Toast;

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
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.fragment.FiveSwitchFragment.SwitchHandler;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.FiveSwitchListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.FiveSwitchListAdapter.OnSwitchCheckBoxClickListener;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 多路开关
 * @author YangJijun
 * @company 金鑫智慧
 */
public class MulitipleSocketFragment extends DialogFragment implements OnSwitchCheckBoxClickListener{
	
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
	
	/**
	 * 开关列表
	 */
	private ListView lvFiveSwitch;

	/**
	 * 开关列表适配器
	 */
	private FiveSwitchListAdapter switchListAdapter;
	/**
	 * 命令字符集
	 */
	public static String[] cmds = {"0x01", "0x02", "0x03", "0x04", "0x05", "0x06"};
	/**
	 * 多路开关
	 */
	/*public static final String FUN_TYPE_MULITIPLE_SWITCH_SIX = "026";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_FIVE = "038";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_FOUR = "039";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_THREE = "040";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_TWO = "041";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_ONE = "042";*/
	/*public static final String[] FUN_TYPE_MULITIPLE_SWITCHES = {
			FUN_TYPE_MULITIPLE_SWITCH_ONE,FUN_TYPE_MULITIPLE_SWITCH_TWO,
			FUN_TYPE_MULITIPLE_SWITCH_THREE,FUN_TYPE_MULITIPLE_SWITCH_FOUR, 
			FUN_TYPE_MULITIPLE_SWITCH_FIVE,FUN_TYPE_MULITIPLE_SWITCH_SIX};*/
	/**
	 * 开关状态列表
	 */
	private boolean[] switchStatus = null;
	public static final int UPDATE_VIEW = 1;
	private TaskListener<ITask> listener;
	private int leftOrRight = -1;
	
	static class SwitchHandler extends Handler {
		WeakReference<MulitipleSocketFragment> mFragment;

		public SwitchHandler(MulitipleSocketFragment fiveSwitchFragment) {
			mFragment = new WeakReference<MulitipleSocketFragment>(fiveSwitchFragment);
		}

		public void handleMessage(Message msg) {
			MulitipleSocketFragment fragment = mFragment.get();
			switch (msg.what) {
			// 更新列表数据
			case UPDATE_VIEW:
				fragment.switchListAdapter.notifyDataSetChanged(fragment.switchStatus);
				break;
			default:
				break;
			}
		};
	}

	/**
	 * UI Handler
	 */
	private SwitchHandler mHandler = new SwitchHandler(this);
	
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
		view=CommDefines.getSkinManager().view(R.layout.fragment_five_switch, container);
		initData();
		initView(view);
		return view;
	}

	private void initData() {
		params = new HashMap<String, Object>();
		psdImpl = new ProductStateDaoImpl(context);
		
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		for (int i = 0; i < ProductConstants.FUN_TYPE_MULITIPLE_SWITCHES.length; i++) {
			if (productFun.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCHES[i])) {
				switchStatus = new boolean[i+1];
			}
		}
		
		System.out.println(productFun.toString()+"/n"+funDetail.toString());
		
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
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				if ("-1".equals(resultObj.validResultInfo))
					return;
				JxshApp.showToast(getActivity(), getActivity().getString(R.string.mode_contorl_success));
				updateSwitchStatus();
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		};
	}

	/**
	 * 更新列表视图
	 */
	protected void updateSwitchStatus() {
		for (int i = 0; i < cmds.length; i++) {
			if (params.get("dst").equals(cmds[i]))
				switchStatus[i] = !switchStatus[i];
		}
		mHandler.obtainMessage(UPDATE_VIEW).sendToTarget();
	}
	
	private void initView(View view) {
		lvFiveSwitch = (ListView) view.findViewById(R.id.lv_five_switch);
		switchListAdapter = new FiveSwitchListAdapter(this,funDetail, switchStatus);
		lvFiveSwitch.setAdapter(switchListAdapter);
	}
	

	
	/**
	 * 开关控制
	 * 
	 * @param productFun
	 *            产品
	 * @param cmdStr
	 *            命令
	 * @param open
	 *            状态
	 */
	protected void switchControl(ProductFun productFun, String cmdStr, boolean open) {
		params.put("src", "0x01");
		params.put("dst", cmdStr);

		String type = open ? "off" : "on";

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
	


	@Override
	public void onSwitchCheckBoxClick(int pos) {
		// TODO Auto-generated method stub
		switch (pos) {
		case 0:
			switchControl(productFun, cmds[pos], switchStatus[pos]);
			break;
		case 1:
			switchControl(productFun, cmds[pos], switchStatus[pos]);
			break;
		case 2:
			switchControl(productFun, cmds[pos], switchStatus[pos]);
			break;
		case 3:
			switchControl(productFun, cmds[pos], switchStatus[pos]);
			break;
		case 4:
			switchControl(productFun, cmds[pos], switchStatus[pos]);
			break;
		case 5:
			switchControl(productFun, cmds[pos], switchStatus[pos]);
			break;
		}
	}
	
}
