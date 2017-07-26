package com.jinxin.jxsmarthome.fragment;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.FlowRadioGroup;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 空调控制
 * @author YangJijun
 * @company 金鑫智慧
 */
public class AirConditionControlFragment extends DialogFragment implements OnClickListener{
	
	private View view = null;
	private ImageView ivWindLevel,ivWindMode;
	private RadioButton openWind, closeWind;
	private RadioButton noneWind, lowWind, midWind, highWind;
	private ToggleButton toggleSwitch;
	private FlowRadioGroup radioGroup;//风速开关
	private FlowRadioGroup modeGroup;//制冷/制热开关
	
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private ProductState productState = null;
	private Context context;
	private ProductStateDaoImpl psdImpl = null;
	private Map<String, Object> params =  null;
	
	private static String WIND_ALLOPEN = "allOpen";
	private static String WIND_ALLCLOSE = "allClose";
	private static String WIND_LEVEL = "windLevel";
	private static String LEVEL_NONE = "00";
	private static String LEVEL_LOW = "01";
	private static String LEVEL_MID = "02";
	private static String LEVEL_HIGH = "03";
	
	private String modeState = "off";//模式状态：制冷、制热
	private String levelState = LEVEL_NONE;//当前风速
	private TaskListener<ITask> listener;
	private boolean isSend = false;//防止RadioGroup多次触发点击事件，多次发送重复指令
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: //根据控制状态 改变背景
				changBagroundByState(modeState, levelState);
				break;
			case 1: //失败还原按钮选中状态
//				if (productState != null) {
//					modeGroup.check(productState.getState().substring(0, 2).equals("00") ? R.id.rb_open : R.id.rb_close);
//					String level = productState.getState().substring(2, 4);
//					if (level.equals("00")) {
//						((RadioButton)radioGroup.findViewById(R.id.btn_wind_none)).setChecked(true);
//					}else if(level.equals("01")){
//						((RadioButton)radioGroup.findViewById(R.id.btn_wind_low)).setChecked(true);
//					}else if(level.equals("02")){
//						((RadioButton)radioGroup.findViewById(R.id.btn_wind_mid)).setChecked(true);
//					}else if(level.equals("03")){
//						((RadioButton)radioGroup.findViewById(R.id.btn_wind_high)).setChecked(true);
//					}
//				}
				radioGroup.clearCheck();
				modeGroup.clearCheck();
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
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view= CommDefines.getSkinManager().view(R.layout.fragment_aircondition_control, container);
		initData();
		initView(view);
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("空调控制");
	}


	private void initView(View view) {
		toggleSwitch = (ToggleButton) view.findViewById(R.id.tg_switch_btn);
		ivWindLevel = (ImageView) view.findViewById(R.id.iv_wind_level);
		ivWindMode = (ImageView) view.findViewById(R.id.iv_wind_mode);
		radioGroup = (FlowRadioGroup) view.findViewById(R.id.radio_group);
		modeGroup = (FlowRadioGroup) view.findViewById(R.id.rg_switch_btn);
		
		openWind = (RadioButton) view.findViewById(R.id.rb_open);
		closeWind = (RadioButton) view.findViewById(R.id.rb_close);
		noneWind = (RadioButton) view.findViewById(R.id.btn_wind_none);
		lowWind = (RadioButton) view.findViewById(R.id.btn_wind_low);
		midWind = (RadioButton) view.findViewById(R.id.btn_wind_mid);
		highWind = (RadioButton) view.findViewById(R.id.btn_wind_high);
		
		//初始化选中状态
		if (productState != null) {
			modeState = productState.getState().subSequence(0, 2).equals("00") ? "off" : "on";
			levelState = productState.getState().subSequence(2, 4).toString();
			changBagroundByState(modeState, levelState);
		}

		modeGroup.setOnCheckedChangeListener(new FlowRadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(FlowRadioGroup group,
					int checkedId) {
				if (isSend) {
					return;
				}
				
				if (checkedId == openWind.getId()) {
					String type =  "on";
					modeState = "on";
					operateAircondition(productFun, type, type);
				}else if(checkedId == closeWind.getId()){
					String type = "off";
					modeState = "off";
					operateAircondition(productFun, type, type);
				}
			}
		});
		
		radioGroup.setOnCheckedChangeListener(new FlowRadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(FlowRadioGroup group,
					int checkedId) {
				if (isSend) {
					return;
				}
				
				switch (checkedId) {
				case R.id.btn_wind_none:
					levelState = LEVEL_NONE;
					operateAircondition(productFun, WIND_LEVEL, LEVEL_NONE);
					break;
				case R.id.btn_wind_low:
					levelState = LEVEL_LOW;
					operateAircondition(productFun, WIND_LEVEL,LEVEL_LOW);
					break;
				case R.id.btn_wind_mid:
					levelState = LEVEL_MID;
					operateAircondition(productFun, WIND_LEVEL, LEVEL_MID);
					break;
				case R.id.btn_wind_high:
					levelState = LEVEL_HIGH;
					operateAircondition(productFun, WIND_LEVEL, LEVEL_HIGH);
					break;
				}
			}
		});
		
		toggleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String type = isChecked ? WIND_ALLOPEN : WIND_ALLCLOSE;
				operateAircondition(productFun, type, type);
			}
		});
	}
	
	private void initData() {
		psdImpl = new ProductStateDaoImpl(context);
		
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		if (productFun != null) {
			productState = getStateByProductFun(productFun);
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
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				String state = modeState.equals("off") ? "00":"01" + levelState;
				if (!TextUtils.isEmpty(state)) {
					productState.setState(state);
					psdImpl.update(productState);
					handler.sendEmptyMessage(0);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
	
	/**
	 * 无线插座指令发送
	 * @param productFun
	 * @param productState
	 */
	private void operateAircondition(ProductFun productFun, String type, String text) {
		Logger.debug(null, "operateAircondition");
		if(productFun == null || TextUtils.isEmpty(type)) return;
		Map<String, Object> map = null;
		if (WIND_ALLOPEN.equals(type)) {
			isSend = true;
			map = new HashMap<String, Object>();
			text = "on";
			type = "on";
			modeState = "on";
			levelState = LEVEL_MID;
			map.put("text", LEVEL_MID);
			map.put("src", "0x01");
			map.put("dst", "0x01");
		}else if(WIND_ALLCLOSE.equals(type)){
			isSend = true;
			map = new HashMap<String, Object>();
			text = "off";
			type = "off";
			modeState = "off";
			levelState = LEVEL_NONE;
			map.put("text", LEVEL_NONE);
			map.put("src", "0x01");
			map.put("dst", "0x01");
		}else{
			params = new HashMap<String, Object>();
			params.put("text", text);
			params.put("src", "0x01");
			params.put("dst", "0x01");
		}
		
		List<byte[]> cmdAll = new ArrayList<byte[]>();
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
//			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), productFun.getWhId());
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
			if(localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(getActivity(), "网关离线", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			cmdAll.addAll(cmdList);
			//生成中/关风速指令，仅全开/关时
			if (map != null) {
				List<byte[]> levelList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, map, WIND_LEVEL);
				cmdAll.addAll(levelList);
			}
			
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
					localHost + ":3333", cmdAll, true, false);
			offlineSender.addListener(listener);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			cmdAll.addAll(cmdList);
			//生成中/关风速指令，仅全开/关时
			if (map != null) {
				List<byte[]> levelList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, map, WIND_LEVEL);
				cmdAll.addAll(levelList);
			}
			
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), 
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdAll, true, 0, false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	private void changBagroundByState(String modeState, String levelState){
		if (TextUtils.isEmpty(modeState)) {
			return;
		}
		
		if (modeState.equals("off")) {
//			modeGroup.check(R.id.rb_close);
			((RadioButton)modeGroup.findViewById(R.id.rb_close)).setChecked(true);
			ivWindMode.setImageResource(R.drawable.bg_aircondition_mode_close);
		}else if(modeState.equals("on")){
//			modeGroup.check(l);
			((RadioButton)modeGroup.findViewById(R.id.rb_open)).setChecked(true);
			ivWindMode.setImageResource(R.drawable.bg_aircondition_mode_open);
		}
		
		if (TextUtils.isEmpty(levelState)) {
			return;
		}
		if (levelState.equals(LEVEL_NONE)) {
//			radioGroup.check(R.id.btn_wind_none);
			((RadioButton)radioGroup.findViewById(R.id.btn_wind_none)).setChecked(true);
			ivWindLevel.setImageResource(R.drawable.bg_aircondition_no_wind);
		}else if(levelState.equals(LEVEL_LOW)){
//			radioGroup.check(R.id.btn_wind_low);
			((RadioButton)radioGroup.findViewById(R.id.btn_wind_low)).setChecked(true);
			ivWindLevel.setImageResource(R.drawable.bg_aircondition_low_wind);
		}else if(levelState.equals(LEVEL_MID)){
//			radioGroup.check(R.id.btn_wind_mid);
			((RadioButton)radioGroup.findViewById(R.id.btn_wind_mid)).setChecked(true);
			ivWindLevel.setImageResource(R.drawable.bg_aircondition_mid_wind);
		}else if(levelState.equals(LEVEL_HIGH)){
//			radioGroup.check(R.id.btn_wind_high);
			((RadioButton)radioGroup.findViewById(R.id.btn_wind_high)).setChecked(true);
			ivWindLevel.setImageResource(R.drawable.bg_aircondition_high_wind);
		}
		
		if (isSend) {
			isSend = false;
		}
	}
	
	/**
	 * 获取设备状态
	 * @param productFun
	 * @return
	 */
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
			
		}else{
			if (ps.getState().equals("00")) {
				ps.setState("0000");
			}else{
				ps.setState("0100");
			}
		}
		ps.setFunId(productFun.getFunId());
		psdImpl.insert(ps, false);
		return ps;
	}
	
}
