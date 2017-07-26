package com.jinxin.jxsmarthome.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.NumberSeekBar;
import com.jinxin.jxsmarthome.util.AppUtil;

/**
 * 窗帘详细控制
 * @author YangJijun
 * @company 金鑫智慧
 */
public class CurtainFragment extends DialogFragment implements OnClickListener{
	
	private View view = null;
	private ImageView curtainUp, curtainDown, curtainStop;
	private ImageView curtainMode1,curtainMode2,curtainMode3,curtainMode4;
	private NumberSeekBar serkBar;
	private Button btnCurtain, btnPosition;
	private LinearLayout curtainLayout, positionLayout;
	
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private Context context;
	private static String type = "";
	private Map<String, Object> params = null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
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
		params = new HashMap<String, Object>();
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
				setType("offset");
				params.put(StaticConstant.PARAM_TEXT, seekBar.getProgress());
				curtainControl(context, productFun, funDetail,params);
				
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
		switch (v.getId()) {
		case R.id.curtain_up:
			setType("up");
			productFun.setOpen(false);
			curtainControl(context, productFun, funDetail,null);
			break;
		case R.id.curtain_down:
			setType("down");
			productFun.setOpen(true);
			curtainControl(context, productFun, funDetail,null);
			break;
		case R.id.curtain_stop:
			setType("stop");
			curtainControl(context, productFun, funDetail,null);
			break;
		case R.id.curtain_mode_1://窗帘控制 1/3
			setType("offset");
			params.put(StaticConstant.PARAM_TEXT, 33);
			curtainControl(context, productFun, funDetail,params);
			break;
		case R.id.curtain_mode_2://窗帘控制 1/2
			setType("offset");
			params.put(StaticConstant.PARAM_TEXT, 50);
			curtainControl(context, productFun, funDetail,params);
			break;
		case R.id.curtain_mode_3://窗帘控制 2/3
			setType("offset");
			params.put(StaticConstant.PARAM_TEXT, 66);
			curtainControl(context, productFun, funDetail,params);
			break;
		case R.id.curtain_mode_4://窗帘控制 3/4
			setType("offset");
			params.put(StaticConstant.PARAM_TEXT, 75);
			curtainControl(context, productFun, funDetail,params);
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
	
	public static String setType(String tem){
		type = tem;
		return type;
	}
	
	public static String getType(){
		return type;
	}
	
	/**
	 * 窗帘控制 发送指令
	 * @param context
	 * @param productFun
	 * @param funDetail
	 */
	public void curtainControl(final Context context,
			final ProductFun productFun,FunDetail funDetail,Map<String, Object> params){
		List<byte[]> cmdList = CommonMethod.
				productFunToCMD(context, productFun, funDetail, params);
		if(cmdList == null || cmdList.size() < 1) {
			return;
		}
		byte[] cmd = cmdList.get(0);
		
//		CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(context, cmd,false);
//		cdcbsTask.addListener(new ITaskListener<ITask>() {
//			
//			@Override
//			public void onStarted(ITask task, Object arg) {
//				JxshApp.showLoading(context, CommDefines.getSkinManager().string(R.string.qing_qiu_chu_li_zhong));
//			}
//			
//			@Override
//			public void onCanceled(ITask task, Object arg) {
//				JxshApp.closeLoading();
//			}
//			
//			@Override
//			public void onFail(ITask task, Object[] arg) {
//				JxshApp.closeLoading();
//			}
//			
//			@Override
//			public void onSuccess(ITask task, Object[] arg) {
//				JxshApp.closeLoading();
////				productFun.setOpen(!productFun.isOpen());
//				//刷新
//			}
//			
//			@Override
//			public void onProcess(ITask task, Object[] arg) {
//				
//			}
//		});
//		cdcbsTask.start();
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			String localWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), localWhId);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdList, true,false);
			offlineSender.send();
		}else{
			OnlineCmdSenderLong ocsTask = new OnlineCmdSenderLong(getActivity(), cmd);
			ocsTask.send();
		}
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
