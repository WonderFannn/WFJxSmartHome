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
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.FiveSwitchListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.FiveSwitchListAdapter.OnSwitchCheckBoxClickListener;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 五路开关
 * 
 * @author Huangl
 * @company 金鑫智慧
 */
public class FiveSwitchFragment extends DialogFragment implements OnSwitchCheckBoxClickListener {

	public static final int UPDATE_VIEW = 1;

	/**
	 * 开关列表
	 */
	private ListView lvFiveSwitch;

	/**
	 * 开关列表适配器
	 */
	private FiveSwitchListAdapter switchListAdapter;

	/**
	 * 产品类
	 */
	private ProductFun productFun = null;

	/**
	 * 产品详情类
	 */
	private FunDetail funDetail = null;

	/**
	 * 命令参数
	 */
	private Map<String, Object> params = null;

	/**
	 * 开关状态列表
	 */
	private boolean[] switchStatus = null;

	/**
	 * 命令字符集
	 */
	public static String[] cmds = { "0x01", "0x02", "0x03", "0x04", "0x05" };

	/**
	 * UI线程
	 */
	static class SwitchHandler extends Handler {
		WeakReference<FiveSwitchFragment> mFragment;

		public SwitchHandler(FiveSwitchFragment fiveSwitchFragment) {
			mFragment = new WeakReference<FiveSwitchFragment>(fiveSwitchFragment);
		}

		public void handleMessage(Message msg) {
			FiveSwitchFragment fragment = mFragment.get();
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

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = CommDefines.getSkinManager().view(R.layout.fragment_five_switch, container);
		initData();
		if (productFun!=null) {
			initView(view);
		}
		return view;
	}

	/**
	 * 初始化数据
	 */
	protected void initData() {
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		params = new HashMap<String, Object>();
		switchStatus = new boolean[5];
	}

	/**
	 * 初始化视图
	 * 
	 * @param view
	 */
	protected void initView(View view) {
		lvFiveSwitch = (ListView) view.findViewById(R.id.lv_five_switch);
		switchListAdapter = new FiveSwitchListAdapter(this, funDetail,switchStatus);
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

	/**
	 * 应答监听
	 */
	TaskListener<ITask> listener = new TaskListener<ITask>() {

		@Override
		public void onFail(ITask task, Object[] arg) {
			if (arg == null) {
				JxshApp.showToast(getActivity(), getActivity().getString(R.string.mode_contorl_fail));
			} else {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				JxshApp.showToast(getActivity(), resultObj.validResultInfo);
			}
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
			if ("-1".equals(resultObj.validResultInfo)){
				JxshApp.showToast(getActivity(), getActivity().getString(R.string.mode_contorl_fail));
				return;
			}
			JxshApp.showToast(getActivity(), getActivity().getString(R.string.mode_contorl_success));
			updateSwitchStatus();
		}

	};

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

	@Override
	public void onSwitchCheckBoxClick(int pos) {
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
		}
	}

}
