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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.DoorMagnetDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductDoorContact;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

/**
 * 门磁、烟感、红外详情界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class DoorMagnetFragment extends DialogFragment implements OnClickListener{

	private Context context;
	private Button outsideMode = null;
	private Button indoorMode = null;
	private Button deviceCheck = null;
	private ImageView powerImg = null;
	private ImageView warnImg = null;
	private ImageView gasImg = null;
	
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private ProductDoorContact doorMagnet = null;
	private String currPower = "";//当前电量 
	private int mWarn = -1;//模式
	
	private DoorMagnetDaoImpl dmDaoImpl = null;
	
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
		View view = CommDefines.getSkinManager().view(R.layout.fragment_doorlock_layout,
				container);
		initData();
		initView(view);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("门磁");
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		this.productFun = (ProductFun) getArguments().getSerializable("productFun");
		this.funDetail = (FunDetail) getArguments().getSerializable("funDetail");
//		this.doorMagnet = (ProductDoorContact) getArguments().getSerializable("productDoorContact");
		dmDaoImpl = new DoorMagnetDaoImpl(context);
		if (productFun != null) {
			List<ProductDoorContact> doorMagnets = dmDaoImpl.find(null, "whId=?",
					new String[]{productFun.getWhId()}, null, null, null, null);
			if (doorMagnets != null && doorMagnets.size() > 0) {
				doorMagnet = doorMagnets.get(0);
			}
		}
		
		if (doorMagnet != null) {
			currPower = doorMagnet.getElectric();
			mWarn = doorMagnet.getIsWarn();
		}
	}

	/**
	 * 初始布局
	 * @param view
	 */
	private void initView(View view) {
		this.outsideMode = (Button) view.findViewById(R.id.btn_doorlock_mode1);
		this.indoorMode = (Button) view.findViewById(R.id.btn_doorlock_mode2);
		this.deviceCheck = (Button) view.findViewById(R.id.btn_doorlock_check);
		this.powerImg = (ImageView) view.findViewById(R.id.iv_door_power);
		this.warnImg = (ImageView) view.findViewById(R.id.iv_power_warn);
		this.gasImg = (ImageView) view.findViewById(R.id.iv_gas_warn);
		outsideMode.setOnClickListener(this);
		indoorMode.setOnClickListener(this);
		deviceCheck.setOnClickListener(this);
		
		if (mWarn == 1) {
			outsideMode.setBackgroundResource(R.drawable.button_right_selected_bg);
			indoorMode.setBackgroundResource(R.drawable.button_left_unselect_bg);
		}else{
			outsideMode.setBackgroundResource(R.drawable.button_rigtht_unselect_bg);
			indoorMode.setBackgroundResource(R.drawable.button_left_selected_bg);
		}
		//气感无电量状态显示
		if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_GAS_SENSE)) {
			gasImg.setVisibility(View.VISIBLE);
			powerImg.setVisibility(View.INVISIBLE);
			warnImg.setVisibility(View.GONE);
		}
		//根据电量显示不同背景
		if (currPower.equals("02")) {
			powerImg.setImageResource(R.drawable.door_power_high_bg);
			warnImg.setVisibility(View.GONE);
		}else if(currPower.equals("03")){
			powerImg.setImageResource(R.drawable.door_power_mid_bg);
			warnImg.setVisibility(View.GONE);
		}else if(currPower.equals("04")){
			powerImg.setImageResource(R.drawable.door_power_low_bg);
			warnImg.setVisibility(View.VISIBLE);
			Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);//加载动画资源文件
			warnImg.startAnimation(shake);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_doorlock_mode1://户外模式
			if (doorMagnet != null) {
				doorMagnet.setIsWarn(1);
				dmDaoImpl.update(doorMagnet);
			}
			outsideMode.setBackgroundResource(R.drawable.button_right_selected_bg);
			indoorMode.setBackgroundResource(R.drawable.button_left_unselect_bg);
			break;
		case R.id.btn_doorlock_mode2://居家模式
			if (doorMagnet != null) {
				doorMagnet.setIsWarn(0);
				dmDaoImpl.update(doorMagnet);
			}
			outsideMode.setBackgroundResource(R.drawable.button_rigtht_unselect_bg);
			indoorMode.setBackgroundResource(R.drawable.button_left_selected_bg);
			break;
		case R.id.btn_doorlock_check:
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("src", "0x01");
			params.put("dst", "0x01");
			String type = StaticConstant.TYPE_DOOR_MAGNET;
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
//			List<byte[]> cmdList = cmdGenerator.generateCmd(context, productFun, funDetail, params, type);
			List<byte[]> cmdList = cmdGenerator.generateCmdForCheckDevice(context, productFun, funDetail, params, type);
//			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), 
//					DatanAgentConnectResource.HOST_ZEGBING, cmdList, true);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), 
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdList, true, 1, false);
			onlineSender.addListener(listener);
			onlineSender.send();
			break;
		}
	}

	TaskListener<ITask> listener = new TaskListener<ITask>() {
		
		@Override
		public void onStarted(ITask task, Object arg) {
			super.onStarted(task, arg);
			JxshApp.showLoading(getActivity(), "正在巡检...");
		}

		@Override
		public void onFail(ITask task, Object[] arg) {
			super.onFail(task, arg);
			JxshApp.closeLoading();
			JxshApp.showToast(context, "巡检失败");
		}

		@Override
		public void onAllSuccess(ITask task, Object[] arg) {
			RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo)arg[0];
			if("-1".equals(resultObj.validResultInfo))  return;
			JxshApp.closeLoading();
			JxshApp.showToast(context, "设备正常");
		}
		
	};
}
