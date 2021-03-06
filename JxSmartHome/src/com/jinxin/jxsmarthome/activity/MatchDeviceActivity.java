package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.command.CodeLibraryTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.infrared.model.InfraredCodeCMDUtil;
import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.infrared.model.InfraredCodeLibraryUtil;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.ClassMemberUtil;
import com.jinxin.jxsmarthome.util.Logger;

import android.R.integer;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MatchDeviceActivity extends BaseActionBarActivity implements
		OnClickListener {

	private static final String TAG = "MatchDeviceActivity";
	private TextView tvInfraredcodeShow;
	private LinearLayout llSwitcher;
	private ImageView ivBtnInfraredSwitchBack;
	private ImageView ivBtnCodetest1;
	private ImageView ivBtnCodetest3;
	private ImageView ivBtnCodetest2;
	private ImageView ivBtnCodetest4;
	private ImageView ivBtnInfraredSwitchNext;
	private TextView tvBtnBack;
	private TextView tvBtnNextStep;

	private int deviceType;
	private String brand;
	private String model;

	private int[] codeTestResource;
	private int codeIndex = 1;
	private int codeMax = 1;

	private FunDetail funDetail;
	private ProductFun productFun;
	private InfraredCodeLibraryUtil mInfraredCodeLibraryUtil;
	private InfraredCodeCMDUtil mInfraredCodeCMDUtil;

	private List<byte[]> codeList;
	private byte[] mCode;

	private TaskListener<ITask> listener;

	private void initViews() {
		tvInfraredcodeShow = (TextView) findViewById(R.id.tv_infraredcode_show);
		setCodeIndex(codeIndex);
		llSwitcher = (LinearLayout) findViewById(R.id.ll_switcher);
		ivBtnCodetest1 = (ImageView) findViewById(R.id.iv_btn_codetest_1);
		ivBtnCodetest1.setImageResource(codeTestResource[0]);
		ivBtnCodetest1.setOnClickListener(this);
		ivBtnCodetest2 = (ImageView) findViewById(R.id.iv_btn_codetest_2);
		ivBtnCodetest2.setImageResource(codeTestResource[1]);
		ivBtnCodetest2.setOnClickListener(this);
		ivBtnCodetest3 = (ImageView) findViewById(R.id.iv_btn_codetest_3);
		ivBtnCodetest3.setImageResource(codeTestResource[2]);
		ivBtnCodetest3.setOnClickListener(this);
		ivBtnCodetest4 = (ImageView) findViewById(R.id.iv_btn_codetest_4);
		ivBtnCodetest4.setImageResource(codeTestResource[3]);
		ivBtnCodetest4.setOnClickListener(this);
		ivBtnInfraredSwitchBack = (ImageView) findViewById(R.id.iv_btn_infrared_switch_back);
		ivBtnInfraredSwitchBack.setOnClickListener(this);
		ivBtnInfraredSwitchNext = (ImageView) findViewById(R.id.iv_btn_infrared_switch_next);
		ivBtnInfraredSwitchNext.setOnClickListener(this);
		tvBtnBack = (TextView) findViewById(R.id.tv_btn_back);
		tvBtnBack.setOnClickListener(this);
		tvBtnNextStep = (TextView) findViewById(R.id.tv_btn_next_step);
		tvBtnNextStep.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_device);
		setTitle("遥控匹配");
		initData();
		initViews();
	}

	private void initData() {
		Intent intent = getIntent();
		deviceType = intent.getIntExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, 0);
		brand = intent.getStringExtra(InfraredCodeLibraryConstant.IntentTag.BRAND);
		model = intent.getStringExtra(InfraredCodeLibraryConstant.IntentTag.MODEL);
		funDetail = (FunDetail) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL);
		productFun = (ProductFun) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN);
		
		listener = new TaskListener<ITask>() {
			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		};
		mInfraredCodeLibraryUtil = new InfraredCodeLibraryUtil(this);
		codeList = mInfraredCodeLibraryUtil.getCodeListByDeviceInfo(deviceType,
				brand, model);
		codeMax = codeList.size();

		switch (deviceType) {
		case InfraredCodeLibraryConstant.DeviceType.AirCleaner:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.AirCleaner());
			break;
		case InfraredCodeLibraryConstant.DeviceType.AirCondition:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.AirCondition());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Projector:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.Projector());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Fan:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.Fan());
			break;
		case InfraredCodeLibraryConstant.DeviceType.TvBox:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.TvBox());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Tv:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.Tv());
			break;
		case InfraredCodeLibraryConstant.DeviceType.InternetTv:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.InternetTv());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Dvd:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.Dvd());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Calorifier:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.Calorifier());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Camera:
			codeTestResource = ClassMemberUtil
					.getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.Camera());
			break;

		default:
			break;
		}
		Log.d(TAG, "1:" + codeTestResource[0] + "\n2:" + codeTestResource[1]
				+ "\n3:" + codeTestResource[2] + "\n4:" + codeTestResource[3]);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_btn_infrared_switch_back:
			setCodeIndex(codeIndex - 1);
			break;
		case R.id.iv_btn_infrared_switch_next:
			setCodeIndex(codeIndex + 1);
			break;
		case R.id.tv_btn_back:
			finish();
			break;
		case R.id.tv_btn_next_step:
			Intent intent = new Intent(this, SaveRemoteControllerActivity.class);
			intent.putExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, deviceType);
			intent.putExtra(InfraredCodeLibraryConstant.IntentTag.BRAND, brand); 
			intent.putExtra(InfraredCodeLibraryConstant.IntentTag.CODE, mCode); 
			intent.putExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL, funDetail);
			intent.putExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN, productFun);
			startActivity(intent);
			break;
		case R.id.iv_btn_codetest_1:
		case R.id.iv_btn_codetest_2:
		case R.id.iv_btn_codetest_3:
		case R.id.iv_btn_codetest_4:
			sendCmd(matchCodeGetCMD(deviceType, v.getId()));
			break;
		default:
			break;
		}
	}

	private String matchCodeGetCMD(int deviceType, int id) {
		int index1 = deviceType;
		int index2;
		switch (id) {
		case R.id.iv_btn_codetest_1:
			index2 = 0;
			break;
		case R.id.iv_btn_codetest_2:
			index2 = 1;
			break;
		case R.id.iv_btn_codetest_3:
			index2 = 2;
			break;
		case R.id.iv_btn_codetest_4:
			index2 = 3;
			break;
		default:
			index2 = 0;
			break;
		}

		String[][] matchCodeCMDArray = {
				{ "AIR_Power", "AIR_Timer", "AIR_Mute", "AIR_Mode" }, // 空气净化器
				{ "ARC_Power", "ARC_Vol", "ARC_tmpAdd", "ARC_Mode" },// 空调
				{ "PJT_On", "PJT_Off", "PJT_Mute", "PJT_MCD" }, // 投影仪
				{ "FAN_Power", "FAN_Timer", "FAN_Shake", "FAN_MiddleSpeed" },// 风扇
				{ "TVBOX_Wait", "TVBOX_Menu", "TVBOX_ChAdd", "TVBOX_VolAdd" }, // 机顶盒
				{ "TV_Power", "TV_Menu", "TV_ChAdd", "TV_VolAdd" }, // 电视
				{ "IPTV_Power", "IPTV_Mute", "IPTV_ChAdd", "IPTV_VolAdd" },// 网络电视
				{ "DVD_Power", "DVD_Menu", "DVD_Format", "DVD_Mute" }, // DVD
				{ "WH_Power", "WH_Timer", "WH_TemAdd", "WH_Mode" }, // 热水器
				{ "SLR_TPIC", "SLR_TPIC", "SLR_TPIC", "SLR_TPIC" } // 相机
		};

		return matchCodeCMDArray[index1][index2];
	}

	private void setCodeIndex(int i) {
		if (i > 0 && i <= codeMax) {
			codeIndex = i;
			tvInfraredcodeShow.setText(codeIndex + "/" + codeMax);
			mCode = codeList.get(codeIndex-1);
		}
	}

	private void sendCmd(String cmdtype) {

		mCode = codeList.get(codeIndex - 1);
		mInfraredCodeCMDUtil = new InfraredCodeCMDUtil(deviceType, cmdtype,
				mCode);
		byte[] opCmd = mInfraredCodeCMDUtil.getCMD();

		Map<String, Object> map = null;
		map = new HashMap<String, Object>();
		map.put("src", "0x01");
		map.put("dst", "0x01");

		map.put("text", "");
		map.put("op", opCmd);
		map.put("type", "00 29");
		String type = StaticConstant.OPERATE_COMMON_CMD;

		List<byte[]> cmdAll = new ArrayList<byte[]>();
		if (NetworkModeSwitcher.useOfflineMode(this)) {
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(this,
					productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(this,
					zegbingWhId);
			if (localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(this, "网关离线", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(this, productFun,
					funDetail, map, type);
			cmdAll.addAll(cmdList);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(this,
					localHost + ":3333", cmdAll, true, false);
			offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(this, productFun,
					funDetail, map, type);
			cmdAll.addAll(cmdList);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(this,
					DatanAgentConnectResource.HOST_ZEGBING, true, cmdAll, true,
					0, false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
}
