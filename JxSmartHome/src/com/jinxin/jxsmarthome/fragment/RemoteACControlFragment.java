package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerProductAreaDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;

public class RemoteACControlFragment extends Fragment implements OnClickListener {
	private List<Integer> funIds = new ArrayList<Integer>();
	private TextView mSwitcher;
	private TextView mSwitcherOn;
	private LinearLayout mHot;
	private LinearLayout mCold;
	private LinearLayout mWet;
	private LinearLayout mAir;
	private TextView mLight;
	private TextView mSleep;
	private TextView mSave;
	private TextView mTempSub;
	private TextView mTempAdd;
	private TextView mWind1;
	private TextView mWind2;
	private TextView mWind3;
	private TextView mTimeSub;
	private TextView mTimeAdd;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_controller_kt, container, false);
		initView(view);
		initData();
		return view;
	}
	
	private void initView(View view) {
		mSwitcher = (TextView) view.findViewById(R.id.kt_switcher);
		mSwitcherOn= (TextView) view.findViewById(R.id.custom_controller_on);
		mHot = (LinearLayout) view.findViewById(R.id.custom_controller_hot);
		mCold = (LinearLayout) view.findViewById(R.id.custom_controller_clod);
		mWet = (LinearLayout) view.findViewById(R.id.custom_controller_wet);
		mAir = (LinearLayout) view.findViewById(R.id.custom_controller_air);
		mLight = (TextView) view.findViewById(R.id.kt_light);
		mSleep = (TextView) view.findViewById(R.id.kt_sleep);
		mSave = (TextView) view.findViewById(R.id.kt_save);
		mTempSub = (TextView) view.findViewById(R.id.wd_sub);
		mTempAdd = (TextView) view.findViewById(R.id.wd_add);
		mWind1 = (TextView) view.findViewById(R.id.fs_v1);
		mWind2 = (TextView) view.findViewById(R.id.fs_v2);
		mWind3 = (TextView) view.findViewById(R.id.fs_v3);
		mTimeSub = (TextView) view.findViewById(R.id.ds_sub);
		mTimeAdd = (TextView) view.findViewById(R.id.ds_add);
		
		mSwitcher.setOnClickListener(this);
		mSwitcherOn.setOnClickListener(this);
		mHot.setOnClickListener(this);
		mCold.setOnClickListener(this);
		mWet.setOnClickListener(this);
		mAir.setOnClickListener(this);
		mLight.setOnClickListener(this);
		mSleep.setOnClickListener(this);
		mSave.setOnClickListener(this);
		mTempAdd.setOnClickListener(this);
		mTempSub.setOnClickListener(this);
		mWind1.setOnClickListener(this);
		mWind2.setOnClickListener(this);
		mWind3.setOnClickListener(this);
		mTimeAdd.setOnClickListener(this);
		mTimeSub.setOnClickListener(this);
	}
	
	private void initData() {
		Bundle data = getArguments();
		if(data != null) {
			int caId = (Integer) data.get("customeAreaId");
			List<CustomerProductArea> cpaList = getCustomerProductAreaByAreaId(caId);
			StringBuilder sb = new StringBuilder();
			for(CustomerProductArea cpa : cpaList) {
				sb.append(cpa.getFunId() + ",");
				funIds.add(cpa.getFunId());
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.kt_switcher:
			sendUFOCmd("033");
			break;
		case R.id.custom_controller_on:
			sendUFOCmd("018");
			break;
		case R.id.custom_controller_hot:
			sendUFOCmd("019");
			break;
		case R.id.custom_controller_clod:
			sendUFOCmd("020");
			break;
		case R.id.custom_controller_wet:
			sendUFOCmd("021");
			break;
		case R.id.custom_controller_air:
			sendUFOCmd("022");
			break;
		case R.id.kt_light:
			sendUFOCmd("030");
			break;
		case R.id.kt_sleep:
			sendUFOCmd("031");
			break;
		case R.id.kt_save:
			sendUFOCmd("032");
			break;
		case R.id.wd_sub:
			sendUFOCmd("023");
			break;
		case R.id.wd_add:
			sendUFOCmd("024");
		case R.id.fs_v1:
			sendUFOCmd("025");
			break;
		case R.id.fs_v2:
			sendUFOCmd("026");
			break;
		case R.id.fs_v3:
			sendUFOCmd("027");
		case R.id.ds_sub:
			sendUFOCmd("028");
			break;
		case R.id.ds_add:
			sendUFOCmd("029");
			break;
		}
	}
	
	private List<CustomerProductArea> getCustomerProductAreaByAreaId(int areaId) {
		CustomerProductAreaDaoImpl cpaDao = new CustomerProductAreaDaoImpl(getActivity());
		return cpaDao.find(null, "areaId=?", 
				new String[]{String.valueOf(areaId)}, null, null, null, null);
	}

	private void sendUFOCmd(String code) {
		ProductFun productFun = getProductFunByCode(code);
		FunDetail funDetail = AppUtil.getFunDetailByFunType(getActivity(),ProductConstants.FUN_TYPE_UFO1);
		if (productFun == null || funDetail == null) {
			return;
		}

		productFun.setFunType(ProductConstants.FUN_TYPE_UFO1);

		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(StaticConstant.PARAM_INDEX, productFun.getFunUnit());
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun,
					funDetail, params);
		} else {

			List<byte[]> cmds = CommonMethod.productFunToCMD(getActivity(),
					productFun, funDetail, null);

			if (cmds == null || cmds.size() < 1) {
				return;
			}

			Logger.debug("tangl.play", "发送操作指令数量" + cmds.size());

			Stack<byte[]> ppos = new Stack<byte[]>();
			for (int i = 0; i < cmds.size(); i++) {
				ppos.push(cmds.get(i));
			}

			patternOperationSend(ppos);
		}
	}

	private ProductFun getProductFunByCode(String code) {
		ProductRemoteConfig prc = getProductRemoteConfigByCode(code);
		if (prc != null) {
			ProductRemoteConfigFun prcf = getProductRemoteConfigFunById(String
					.valueOf(prc.getId()));
			if (prcf != null) {
				ProductFun pf = AppUtil.getSingleProductFunByFunId(
						getActivity(), prcf.getFunId());
				return pf;
			}
		}
		return null;
	}

	private ProductRemoteConfig getProductRemoteConfigByCode(String code) {
		ProductRemoteConfigDaoImpl prfDao = new ProductRemoteConfigDaoImpl(
				getActivity());
		List<ProductRemoteConfig> prfList = prfDao.find(null, "code=?",
				new String[] { code }, null, null, null, null);
		if (prfList.size() > 0) {
			return prfList.get(0);
		}
		return null;
	}

	private ProductRemoteConfigFun getProductRemoteConfigFunById(String id) {
		ProductRemoteConfigFunDaoImpl prcfDao = new ProductRemoteConfigFunDaoImpl(
				getActivity());
		List<ProductRemoteConfigFun> prcfList = prcfDao.find(null,
				"configId=?", new String[] { id }, null, null, null, null);
		if (prcfList.size() > 0) {
			for(ProductRemoteConfigFun prcf : prcfList) {
				if(funIds.contains(Integer.parseInt(prcf.getFunId()))) {
					return prcf;
				}
			}
		}
		return null;
	}

	/**
	 * 递归发送指令
	 * 
	 * @param _ppos
	 */
	private void patternOperationSend(final Stack<byte[]> _ppos) {
		Logger.warn("tangl", "" + _ppos.size());
		if (_ppos == null || _ppos.empty()) {
			return;
		}

		byte[] _ppo = _ppos.pop();

		if (_ppo != null) {
			byte[] cmd = _ppo;

			final CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(
					getActivity(), cmd, true);

			if (_ppos == null || _ppos.empty()) {
				if (cdcbsTask != null) {
					cdcbsTask.setcloseSocketLongAfterTaskFinish(true);
					Logger.warn("tangl", "关闭连接");
				}
			}
			
			TaskListener<ITask> listener = new TaskListener<ITask>(){
				@Override
				public void onStarted(ITask task, Object arg) {
					Logger.debug(null, "onstart...");
					JxshApp.showLoading(getActivity(), getResources().getString(R.string.comm_loading_send_cmd_text));
				}
				
				@Override
				public void onFail(ITask task, Object[] arg) {
					Logger.debug(null, "onfail...");
					JxshApp.closeLoading();
				}
				
				@Override
				public void onAllSuccess(ITask task, Object[] arg) {
					Logger.debug(null, "onSuccess...");
					JxshApp.closeLoading();
					patternOperationSend(_ppos);
				}
				
			};
			
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(null, cmd);
			cmdSender.addListener(listener);
			cmdSender.send();

//			cdcbsTask.addListener(new ITaskListener<ITask>() {
//
//				public void onStarted(ITask task, Object arg) {
//					Logger.debug("tangl.home", "onstart...");
//				}
//
//				@Override
//				public void onCanceled(ITask task, Object arg) {
//					Logger.debug("tangl.home", "oncancel...");
//				}
//
//				@Override
//				public void onFail(ITask task, Object[] arg) {
//					Logger.debug("tangl.home", "onfail...");
//				}
//
//				@Override
//				public void onSuccess(ITask task, Object[] arg) {
//					Logger.debug("tangl.home", "onSuccess...");
//					patternOperationSend(_ppos);
//				}
//
//				@Override
//				public void onProcess(ITask task, Object[] arg) {
//
//				}
//			});
//			JxshApp.getDatanAgentHandler().postDelayed(cdcbsTask, 100);
		}
	}
	
}
