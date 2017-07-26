package com.jinxin.jxsmarthome.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

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
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.NumberSeekBar;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.StringUtils;

/**
 * 窗帘详细控制
 * @author YangJijun
 * @company 金鑫智慧
 */
public class WirelessCurtainFragment extends DialogFragment implements OnClickListener{
	
	private View view = null;
	private ImageView curtainUp, curtainDown, curtainStop;
	private ImageView curtainMode1,curtainMode2,curtainMode3,curtainMode4;
	private NumberSeekBar serkBar;
	private Button btnCurtain, btnPosition;
	private LinearLayout curtainLayout, positionLayout;
	
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private static String type = "";
	private Map<String, Object> params;
	private ProductStateDaoImpl psdImpl = null;
	private ProductState productState = null;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=CommDefines.getSkinManager().view(R.layout.curtain_up_down_layout, container);
		initData();
		initView(view);
		return view;
	}

	private void initData() {
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		psdImpl = new ProductStateDaoImpl(getActivity());
		params = new HashMap<String, Object>();
		if (productFun != null) {
			productState = getStateByProductFun(productFun);
		}
	}

	private void initView(View view) {
		this.curtainUp = (ImageView) view.findViewById(R.id.curtain_up);
		this.curtainDown = (ImageView) view.findViewById(R.id.curtain_down);
		this.curtainStop = (ImageView) view.findViewById(R.id.curtain_stop);
		this.curtainMode1 = (ImageView) view.findViewById(R.id.curtain_mode_1);
		this.curtainMode2 = (ImageView) view.findViewById(R.id.curtain_mode_2);
		this.curtainMode3 = (ImageView) view.findViewById(R.id.curtain_mode_3);
		this.curtainMode4 = (ImageView) view.findViewById(R.id.curtain_mode_4);
		this.serkBar = (NumberSeekBar) view.findViewById(R.id.bar0);
		this.btnCurtain = (Button) view.findViewById(R.id.tv_curtain_control);
		this.btnPosition = (Button) view.findViewById(R.id.tv_position_control);
		this.curtainLayout = (LinearLayout) view.findViewById(R.id.ll_curtain_control);
		this.positionLayout = (LinearLayout) view.findViewById(R.id.ll_position_control);
		
		serkBar.setTextSize(20);// 设置字体大小
		serkBar.setTextColor(Color.WHITE);// 颜色
		serkBar.setMyPadding(10, 10, 10, 10);// 设置padding 调用setpadding会无效
//		if (productState != null) {
//			serkBar.setProgress(0);
//		}
		
		btnCurtain.setOnClickListener(this);
		btnPosition.setOnClickListener(this);
		curtainUp.setOnClickListener(this);
		curtainDown.setOnClickListener(this);
		curtainStop.setOnClickListener(this);
		curtainMode1.setOnClickListener(this);
		curtainMode2.setOnClickListener(this);
		curtainMode3.setOnClickListener(this);
		curtainMode4.setOnClickListener(this);
		
		serkBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				type = "percent";
				int porgress = seekBar.getProgress();
				params.put(StaticConstant.PARAM_TEXT, 
						StringUtils.integerToHexString(porgress));
				operateWirelessCurtain(productFun, params, "percent");
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		
		params.put("src", "0x01");
		params.put("dst", StringUtils.integerToHexString(Integer.parseInt(productFun.getFunUnit())));
		switch (v.getId()) {
		case R.id.curtain_up:
			type = "open";
			operateWirelessCurtain(productFun, params, "open");
			break;
		case R.id.curtain_down:
			type = "down";
			operateWirelessCurtain(productFun, params, "down");
			break;
		case R.id.curtain_stop:
			type = "stop";
			operateWirelessCurtain(productFun, params, "stop");
			break;
		case R.id.curtain_mode_1://窗帘控制 1/3
			type = "percent";
			params.put("text", StringUtils.integerToHexString(33));
			operateWirelessCurtain(productFun, params, "percent");
			break;
		case R.id.curtain_mode_2://窗帘控制 1/2
			type = "percent";
			params.put("text", StringUtils.integerToHexString(50));
			operateWirelessCurtain(productFun, params, "percent");
			break;
		case R.id.curtain_mode_3://窗帘控制 2/3
			type = "percent";
			params.put("text", StringUtils.integerToHexString(66));
			operateWirelessCurtain(productFun, params, "percent");
			break;
		case R.id.curtain_mode_4://窗帘控制 3/4
			type = "percent";
			params.put("text", StringUtils.integerToHexString(75));
			operateWirelessCurtain(productFun, params, "percent");
			break;
		case R.id.tv_curtain_control://窗帘控制
			changBackground(0);
			break;
		case R.id.tv_position_control://一键定位
			changBackground(1);
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("rawtypes")
	TaskListener listener = new TaskListener<ITask>() {

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
			if ("-1".equals(resultObj.validResultInfo)) {
				JxshApp.showToast(getActivity(), getActivity().getString(R.string.mode_contorl_fail));
				return;
			}
			JxshApp.showToast(getActivity(), "操作成功");
			if (type.equals("down")) {
				productState.setState("01");
			}else{
				productState.setState("00");
			}
			psdImpl.update(productState);
			BroadcastManager.sendBroadcast(
					BroadcastManager.MESSAGE_RECEIVED_ACTION, null);
		}

		@Override
		public void onProcess(ITask task, Object[] arg) {
			
		}
	};
	
	@SuppressWarnings("unchecked")
	private void operateWirelessCurtain(ProductFun productFun, Map<String, Object> params, String type ) {
		if(productFun == null || funDetail == null) return;
		
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
//			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdList, true,false);
			offlineSender.addListener(listener);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, false, 0, false);
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
			ps.setState("00");
			ps.setFunId(productFun.getFunId());
			psdImpl.insert(ps, false);
		}
		return ps;
	}
	
	private void changBackground(int leftOrRight){
		if (leftOrRight == 0) {
			btnCurtain.setBackgroundResource(R.color.transparent);
			btnPosition.setBackgroundResource(R.drawable.btn_curtain_right_hover);
			curtainLayout.setVisibility(View.VISIBLE);
			positionLayout.setVisibility(View.GONE);
		}else{
			btnCurtain.setBackgroundResource(R.drawable.btn_curtain_left_hover);
			btnPosition.setBackgroundResource(R.color.transparent);
			curtainLayout.setVisibility(View.GONE);
			positionLayout.setVisibility(View.VISIBLE);
		}
	}
}
