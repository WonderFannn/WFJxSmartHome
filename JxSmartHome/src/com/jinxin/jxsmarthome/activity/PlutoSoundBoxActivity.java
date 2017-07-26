package com.jinxin.jxsmarthome.activity;

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
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.fragment.RadioListDialogFragment;
import com.jinxin.jxsmarthome.fragment.RadioListDialogFragment.OnItemClickListener;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlutoSoundBoxActivity extends BaseActionBarActivity implements OnClickListener {

	public static final int PLUTO_TURN_ON = 0;
	public static final int PLUTO_TURN_OFF = 1;
	public static final int PLUTO_QEURY_STATUS = 2;
	public static final int PLUTO_UNMUTE = 3;
	public static final int PLUTO_MUTE = 4;
	public static final int PLUTO_VOLUME_ADD = 5;
	public static final int PLUTO_VOLUME_SUB = 6;
	public static final int PLUTO_SET_INPUT_WIFI = 7;
	public static final int PLUTO_SET_INPUT_BLUETOOTH = 8;
	public static final int PLUTO_SET_INPUT_CD = 9;
	public static final int PLUTO_SET_INPUT_PC = 10;
	public static final int PLUTO_SET_INPUT_AV = 11;
	public static final int PLUTO_SHOW_STATUS = 12;
	public static final int PLUTO_SET_INPUT = 13;

	public static final String[] PLUTO_INPUT_STATUS = { "00", "01", "02", "03", "04" };
	public static final String[] PLUTO_INPUT_CMD = { "06", "07", "08", "09", "0A", "0B" };

	/**
	 * 开机按钮
	 */
	private Button btnTurnOn;
	/**
	 * 关机按钮
	 */
	private Button btnTurnOff;
	/**
	 * 查询按钮
	 */
	private Button btnQueryStatus;

	/**
	 * 静音按钮
	 */
	private ImageView ivMute;
	/**
	 * 取消静音按钮
	 */
	private ImageView ivUnmute;
	/**
	 * 音量增大按钮
	 */
	private ImageView ivVolumeAdd;
	/**
	 * 音量减小按钮
	 */
	private ImageView ivVolumeSub;

	/**
	 * 状态显示文本
	 */
	private TextView tvStatus;

	/**
	 * 产品类
	 */
	private ProductFun productFun = null;
	/**
	 * 产品详情类
	 */
	private FunDetail funDetail = null;

	/**
	 * 单选对话框
	 */
	private RadioListDialogFragment inputFragment;

	/**
	 * 上下文
	 */
	private Context mContext = null;

	/**
	 * 命令参数
	 */
	private Map<String, Object> params = null;

	/**
	 * 状态查询按钮是否处于工作状态
	 */
	private boolean status_btn_state = false;

	/**
	 * 是否处于静音状态
	 */
	private boolean status_mute = false;

	static class PlutoHandler extends Handler {
		WeakReference<PlutoSoundBoxActivity> mPlutoActivity;

		PlutoHandler(PlutoSoundBoxActivity activity) {
			mPlutoActivity = new WeakReference<PlutoSoundBoxActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			PlutoSoundBoxActivity activity = mPlutoActivity.get();
			switch (msg.what) {
			// 开机
			case PLUTO_TURN_ON:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "00");
				break;
			// 关机
			case PLUTO_TURN_OFF:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "01");
				break;
			// 查询状态
			case PLUTO_QEURY_STATUS:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "0B");
				break;
			// 取消静音
			case PLUTO_UNMUTE:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "05");
				break;
			// 静音
			case PLUTO_MUTE:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "04");
				break;
			// 增大音量
			case PLUTO_VOLUME_ADD:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "02");
				break;
			// 减小音量
			case PLUTO_VOLUME_SUB:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "03");
				break;
			// 设置输入通道为WIFI
			case PLUTO_SET_INPUT_WIFI:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "06");
				break;
			// 设置输入通道为Bluetooth
			case PLUTO_SET_INPUT_BLUETOOTH:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "07");
				break;
			// 设置输入通道为CD
			case PLUTO_SET_INPUT_CD:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "08");
				break;
			// 设置输入通道为PC
			case PLUTO_SET_INPUT_PC:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "09");
				break;
			// 设置输入通道为AV
			case PLUTO_SET_INPUT_AV:
				activity.plutoSoundBoxControl(activity.productFun, activity.funDetail, "0A");
				break;
			// 显示设备状态
			case PLUTO_SHOW_STATUS:
				activity.tvStatus.setText((String) msg.obj);
				break;
			// 选择输入源
			case PLUTO_SET_INPUT:
				activity.choosePlutoInput();
				break;
			default:
				break;
			}
		}
	}

	private PlutoHandler mHandler = new PlutoHandler(PlutoSoundBoxActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pluto_sound_box_control);
		initData();
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_pluto, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.pluto_input:
			choosePlutoInput();
			break;
		}
		return false;
	}

	/**
	 * 初始化视图
	 */
	protected void initView() {
		getWindow().setBackgroundDrawableResource(R.drawable.music_bg);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mContext.getString(R.string.sound_box_control));

		btnTurnOn = (Button) findViewById(R.id.btn_pluto_sound_box_turn_on);
		btnTurnOff = (Button) findViewById(R.id.btn_pluto_sound_box_turn_off);
		btnQueryStatus = (Button) findViewById(R.id.btn_pluto_sound_box_query);

		ivUnmute = (ImageView) findViewById(R.id.iv_pluto_sound_box_unmute);
		ivMute = (ImageView) findViewById(R.id.iv_pluto_sound_box_mute);
		ivVolumeAdd = (ImageView) findViewById(R.id.iv_pluto_sound_box_volume_add);
		ivVolumeSub = (ImageView) findViewById(R.id.iv_pluto_sound_box_volume_sub);

		tvStatus = (TextView) findViewById(R.id.tv_pluto_sound_box_status);

		btnTurnOn.setOnClickListener(this);
		btnTurnOff.setOnClickListener(this);
		btnQueryStatus.setOnClickListener(this);

		ivUnmute.setOnClickListener(this);
		ivMute.setOnClickListener(this);
		ivVolumeAdd.setOnClickListener(this);
		ivVolumeSub.setOnClickListener(this);
	}

	/**
	 * 初始化参数
	 */
	protected void initData() {
		JxshApp.instance.isClinkable = true;

		productFun = (ProductFun) getIntent().getSerializableExtra("productFun");
		funDetail = (FunDetail) getIntent().getSerializableExtra("funDetail");

		params = new HashMap<String, Object>();
		mContext = this;
	}

	@Override
	public void onClick(View v) {
		if (JxshApp.instance.isClinkable) {
			JxshApp.showToast(mContext, mContext.getString(R.string.operate_invalid_work));
			return;
		}
		switch (v.getId()) {
		case R.id.btn_pluto_sound_box_turn_on:
			mHandler.obtainMessage(PLUTO_TURN_ON).sendToTarget();
			break;
		case R.id.btn_pluto_sound_box_turn_off:
			mHandler.obtainMessage(PLUTO_TURN_OFF).sendToTarget();
			break;
		case R.id.btn_pluto_sound_box_query:
			if (!status_btn_state) {
				btnQueryStatus.setBackgroundResource(R.drawable.btn_hover);
			} else {
				btnQueryStatus.setBackgroundResource(R.drawable.btn);
			}
			status_btn_state = !status_btn_state;
			sendQueryCmd();
			break;
		case R.id.iv_pluto_sound_box_unmute:
			mHandler.obtainMessage(PLUTO_UNMUTE).sendToTarget();
			break;
		case R.id.iv_pluto_sound_box_mute:
			mHandler.obtainMessage(PLUTO_MUTE).sendToTarget();
			break;
		case R.id.iv_pluto_sound_box_volume_add:
			if (!status_mute) {
				mHandler.obtainMessage(PLUTO_VOLUME_ADD).sendToTarget();
			} else {
				JxshApp.showToast(mContext, mContext.getString(R.string.mute_invalid));
			}
			break;
		case R.id.iv_pluto_sound_box_volume_sub:
			if (!status_mute) {
				mHandler.obtainMessage(PLUTO_VOLUME_SUB).sendToTarget();
			} else {
				JxshApp.showToast(mContext, mContext.getString(R.string.mute_invalid));
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 应答监听
	 */
	TaskListener<ITask> listener = new TaskListener<ITask>() {

		@Override
		public void onFail(ITask task, Object[] arg) {
			if (arg == null) {
				JxshApp.showToast(mContext, mContext.getString(R.string.mode_contorl_fail));
			} else {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				JxshApp.showToast(mContext, resultObj.validResultInfo);
			}
			JxshApp.instance.isClinkable = false;
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			JxshApp.instance.isClinkable = false;
			RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
			if ("-1".equals(resultObj.validResultInfo)){
				JxshApp.showToast(mContext, mContext.getString(R.string.mode_contorl_fail));
				return;
			}
			JxshApp.showToast(mContext, mContext.getString(R.string.mode_contorl_success));
			String resultStr = resultObj.validResultInfo;
			if (params.get("op").equals("0B")) {
				updatePlutoStatus(resultStr);
			} else {
				sendQueryCmd();
			}
			if (params.get("op").equals("04")) {
				status_mute = true;
			}
			if (params.get("op").equals("05")) {
				status_mute = false;
			}
		}

	};

	// 只要查询按钮处于工作状态，就发送查询命令
	protected void sendQueryCmd() {
		if (status_btn_state) {
			mHandler.obtainMessage(PLUTO_QEURY_STATUS).sendToTarget();
		} else {
			mHandler.obtainMessage(PLUTO_SHOW_STATUS, "").sendToTarget();
		}
	}

	/**
	 * 单选列表对话框点击事件
	 */
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(int position, String itemName, String tag) {
			switch (position) {
			case 0:
				mHandler.obtainMessage(PLUTO_SET_INPUT_WIFI).sendToTarget();
				break;
			case 1:
				mHandler.obtainMessage(PLUTO_SET_INPUT_BLUETOOTH).sendToTarget();
				break;
			case 2:
				mHandler.obtainMessage(PLUTO_SET_INPUT_CD).sendToTarget();
				break;
			case 3:
				mHandler.obtainMessage(PLUTO_SET_INPUT_PC).sendToTarget();
				break;
			case 4:
				mHandler.obtainMessage(PLUTO_SET_INPUT_AV).sendToTarget();
				break;
			default:
				break;
			}
			inputFragment.dismiss();
		}
	};

	/**
	 * 发送控制命令
	 * 
	 * @param productFun 产品
	 * @param funDetail 产品详情
	 * @param cmdStr 命令
	 */
	public void plutoSoundBoxControl(ProductFun productFun, FunDetail funDetail, String cmdStr) {
		JxshApp.instance.isClinkable = true;

		params.put("type", "00 36");
		params.put("src", "0x01");
		params.put("dst", "0x01");
		params.put("op", cmdStr);
		params.put(StaticConstant.PARAM_TEXT, "");

		String type = StaticConstant.OPERATE_COMMON_CMD;

		if (NetworkModeSwitcher.useOfflineMode(this)) {
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(this, productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(this, zegbingWhId);
			if (localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(this, "未找到对应网关", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(this, productFun, funDetail, params, type);

			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(this, localHost + ":3333", cmdList, true,
					false);
			offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(this, productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(this, DatanAgentConnectResource.HOST_ZEGBING,
					true, cmdList, true, 0, false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}

	/**
	 * 更新状态
	 * 
	 * @param result 应答字符串
	 */
	protected void updatePlutoStatus(String result) {
		String statusStr = getPlutoStatus(result);
		mHandler.obtainMessage(PLUTO_SHOW_STATUS, statusStr).sendToTarget();
	}

	/**
	 * 获取状态字符串
	 * 
	 * @param result 应答字符串
	 * @return string 状态字符
	 */
	protected String getPlutoStatus(String result) {
		if (result == null)
			return null;

		StringBuilder stb = new StringBuilder();

		int length = result.length();
		String statusCode = result.substring(length - 10, length - 1);
		String[] code = statusCode.split(" ");

		// 开关状态
		if (code[0].equals("00")) {
			stb.append(mContext.getString(R.string.pluto_off));
		} else if (code[0].equals("01")) {
			stb.append(mContext.getString(R.string.pluto_on));
		}

		// 音量状态
		if (code[1].equals("00")) {
			stb.append(", " + mContext.getString(R.string.music_mute));
		} else {
			stb.append(", " + mContext.getString(R.string.setting_music_volume) + " "
					+ StringUtils.HexStringToDec(code[1]));
		}

		// 输入源
		String[] inputStrs = mContext.getResources().getStringArray(R.array.pluto_input);
		String input = "";
		int lengthStatus = PLUTO_INPUT_STATUS.length;
		for (int i = 0; i < lengthStatus; i++) {
			if (code[2].equals(PLUTO_INPUT_STATUS[i])) {
				input = inputStrs[i];
				// 存储配置
				// if (save_input_flag && input_flag)
				// SharedDB.saveIntDB(MusicSetting.PLUTO_SP_NAME,
				// MusicSetting.PLUTO_INPUT, i);
			}
		}
		stb.append(", " + mContext.getString(R.string.music_choose_input) + " " + input);

		return stb.toString();
	}

	/**
	 * 选择输入源对话框
	 */
	protected void choosePlutoInput() {
		inputFragment = RadioListDialogFragment.newInstance(R.string.music_choose_input, R.array.pluto_input,
				itemClickListener, "input");
		// int i = SharedDB.loadIntFromDB(MusicSetting.PLUTO_SP_NAME,
		// MusicSetting.PLUTO_INPUT, 0);
		inputFragment.setSelectedPosition(-1);
		inputFragment.setShowsDialog(false);
		getSupportFragmentManager().beginTransaction().add(inputFragment, "inputFragment").addToBackStack(null)
				.commitAllowingStateLoss();
	}

}
