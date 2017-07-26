package com.jinxin.jxsmarthome.activity;

import java.util.HashMap;
import java.util.Map;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.jinxin.datan.net.command.VoiceToTextTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.service.CustomWakeService;
import com.jinxin.jxsmarthome.util.JsonParser;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.FileManager;
import com.jinxin.record.SharedDB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 发送语音控制模式，有点击
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class Voice2TextActivity extends BaseActionBarActivity implements OnClickListener {

	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog iatDialog;

	private EditText mResultText; // 语音识别结果文字
	private Button recBtn;
	private TextView tvTitle;

	private Context context;
	private int ret = 0;// 函数调用返回值
	private int type = 0;// 语音控制类型 1：模式切换 ，2：电视频道切换
	private VoiceToTextTask vttTask = null;

	private SoundPool soundPool = null;
	private Map<Integer, Integer> soundPoolMap;
	private AudioManager audioManager = null;

	private static String SUCCESS = "0000";// 平台匹配成功
	// private static String ERROR = "6001";//平台匹配失败
	private int sendCount = 0;// 控制发送次数

	private static final int EXIT = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.menu_text2voice, menu);
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getSupportActionBar().setTitle("语音识别");
		getSupportActionBar().hide();
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
		// 停止语音唤醒监听，否则无法语音识别
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE_CLOSE, null);
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		iatDialog = new RecognizerDialog(context,mInitListener);
		this.type = getIntent().getIntExtra("type", 0);
		changModeTitle(type);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// 退出时释放连接
		if(mIat!=null){
			mIat.cancel();
			mIat.destroy();
		}
		soundPool.release();
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE, null);
	}

	private void initView() {
		setContentView(R.layout.fragment_voice2text_layout);
		this.mResultText = (EditText) findViewById(R.id.iat_text);
		this.recBtn = (Button) findViewById(R.id.iat_recognize);
		this.tvTitle = (TextView) findViewById(R.id.tv_title);
		mResultText.setOnClickListener(this);
		recBtn.setOnClickListener(this);
	}

	@SuppressLint("UseSparseArrays")
	private void initData() {
		context = this;
		// this.type = getIntent().getIntExtra("type", 0);
		// changModeTitle(type);

		audioManager = (AudioManager) JxshApp.instance.getSystemService(Context.AUDIO_SERVICE);
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(1, soundPool.load(this, R.raw.ring_success, 1));
		soundPoolMap.put(2, soundPool.load(this, R.raw.ring_error, 1));

		// 初始化识别对象
		// mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		iatDialog = new RecognizerDialog(context,mInitListener);
		// 获取语音唤醒对象
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.send_text:
			// sendTextTask();
			break;
		}
		return false;
	}

	/**
	 * 发送文字任务
	 */
	private void sendTextTask() {
		String content = mResultText.getText().toString();
		vttTask = new VoiceToTextTask(context, content, type);
		vttTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, "正在发送...");
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.closeLoading();
				playSound(2);
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				JxshApp.closeLoading();
				if (arg != null && arg.length > 0) {
					String result = (String) arg[0];
					if (!TextUtils.isEmpty(result)) {
						if (result.equals(SUCCESS)) {
							playSound(1);
							JxshApp.showToast(context, "发送成功，正在执行操作...");
						} else {
							playSound(2);
							JxshApp.showToast(context, "匹配失败，请重新说话...");
						}
					} else {
						playSound(2);
						JxshApp.showToast(context, "匹配失败，请重新说话...");
					}
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		vttTask.start();
	}

	/**
	 * 播放提示音
	 * 
	 * @param id
	 */
	private void playSound(int id) {
		int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float voice = (float) 0.7 / max * current;
		soundPool.play(id, voice, voice, 0, 0, 1f);
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Logger.debug(null, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				JxshApp.showToast(context, "初始化失败,错误码：" + code);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 开始语音识别
		case R.id.iat_recognize:
			sendCount = 0;// 重置发送次数
			mResultText.setText(null);// 清空显示内容
			// 初始化识别对象
			mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
			// 设置参数
			setParam();
			boolean isShowDialog = true;
			if (isShowDialog) {
				// 显示听写对话框 TODO
				iatDialog.setListener(recognizerDialogListener);
				iatDialog.show();
				JxshApp.showToast(context, getString(R.string.text_begin));
			} else {
				// 不显示听写对话框
				ret = mIat.startListening(recognizerListener);
				if (ret != ErrorCode.SUCCESS) {
					JxshApp.showToast(context, "听写失败,错误码：" + ret);
				} else {
					JxshApp.showToast(context, getString(R.string.text_begin));
				}
			}
			break;
		}
	}

	/**
	 * 听写监听器。
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			JxshApp.showToast(context, "开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			JxshApp.showToast(context, error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			JxshApp.showToast(context, "结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			mResultText.append(text);
			mResultText.setSelection(mResultText.length());
			if (isLast) {
				// sendTextTask();
				if (CommDefines.getSkinManager().string(R.string.voice_control_type1)
						.equals(mResultText.getText().toString())
						|| CommDefines.getSkinManager().string(R.string.voice_control_type1_1)
						.equals(mResultText.getText().toString())) {
					type = 1;
					mResultText.setText(null);// 清空显示内容
					mUIHander.sendEmptyMessage(2);
					JxshApp.showToast(context,
							CommDefines.getSkinManager().string(R.string.voice_control_change_tips1));
				} else if (CommDefines.getSkinManager().string(R.string.voice_control_type2)
						.equals(mResultText.getText().toString())
						|| CommDefines.getSkinManager().string(R.string.voice_control_type2_1)
						.equals(mResultText.getText().toString())) {
					type = 2;
					mResultText.setText(null);// 清空显示内容
					mUIHander.sendEmptyMessage(2);
					JxshApp.showToast(context,
							CommDefines.getSkinManager().string(R.string.voice_control_change_tips2));
				} else if (CommDefines.getSkinManager().string(R.string.voice_control_type3)
						.equals(mResultText.getText().toString())
						|| CommDefines.getSkinManager().string(R.string.voice_control_type3_1)
						.equals(mResultText.getText().toString())) {
					type = 3;
					mResultText.setText(null);// 清空显示内容
					mUIHander.sendEmptyMessage(2);
					JxshApp.showToast(context,
							CommDefines.getSkinManager().string(R.string.voice_control_change_tips3));
				} else if (CommDefines.getSkinManager().string(R.string.btn_exit)
						.equals(mResultText.getText().toString())) {
					mUIHander.sendEmptyMessage(EXIT);
				} else {
					mUIHander.sendEmptyMessage(1);
				}
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			JxshApp.showToast(context, "当前正在说话，音量大小：" + arg0);

		}
	};

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			mResultText.append(text);
			mResultText.setSelection(mResultText.length());
			// sendTextTask();
			if (isLast) {
				if (CommDefines.getSkinManager().string(R.string.voice_control_type1)
						.equals(mResultText.getText().toString())
						|| CommDefines.getSkinManager().string(R.string.voice_control_type1_1)
						.equals(mResultText.getText().toString())) {
					type = 1;
					mResultText.setText(null);// 清空显示内容
					mUIHander.sendEmptyMessage(2);
					JxshApp.showToast(context,
							CommDefines.getSkinManager().string(R.string.voice_control_change_tips1));
				} else if (CommDefines.getSkinManager().string(R.string.voice_control_type2)
						.equals(mResultText.getText().toString())
						|| CommDefines.getSkinManager().string(R.string.voice_control_type2_1)
						.equals(mResultText.getText().toString())) {
					type = 2;
					mResultText.setText(null);// 清空显示内容
					mUIHander.sendEmptyMessage(2);
					JxshApp.showToast(context,
							CommDefines.getSkinManager().string(R.string.voice_control_change_tips2));
				} else if (CommDefines.getSkinManager().string(R.string.voice_control_type3)
						.equals(mResultText.getText().toString())
						|| CommDefines.getSkinManager().string(R.string.voice_control_type3_1)
						.equals(mResultText.getText().toString())) {
					type = 3;
					mResultText.setText(null);// 清空显示内容
					mUIHander.sendEmptyMessage(2);
					JxshApp.showToast(context,
							CommDefines.getSkinManager().string(R.string.voice_control_change_tips3));
				} else if (CommDefines.getSkinManager().string(R.string.btn_exit)
						.equals(mResultText.getText().toString())) {
					mUIHander.sendEmptyMessage(EXIT);
				} else {
					mUIHander.sendEmptyMessage(1);
				}
			}
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			JxshApp.showToast(context, error.getPlainDescription(true));
		}
	};

	// /**
	// * 获取功放设备的485地址
	// * @return
	// */
	// private String getAmplifiersAd1dress485() {
	// String address485 = "";
	// ProductFun _pf = null;
	// int amplifierFunId = SharedDB.loadIntFromDB(MusicSetting.SP_NAME,
	// MusicSetting.AMPLIFIER, -1);
	// List<ProductFun> amplifierList = AppUtil.getProductFunByFunType(this,
	// ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
	// if (amplifierList != null && amplifierList.size() > 0) {
	// if (amplifierFunId > 0) {
	// for (ProductFun productFun : amplifierList) {
	// if (productFun.getFunId() == amplifierFunId) {
	// _pf = productFun;
	// }
	// }
	// }else{
	// _pf = amplifierList.get(0);
	// }
	// }
	//
	// if (_pf != null) {
	// ProductRegister _pr = AppUtil.getProductRegisterByWhId(context,
	// _pf.getWhId());
	// if (_pr != null) {
	// address485 = _pr.getAddress485();
	// return address485;
	// }
	// }
	//
	// return "";
	// }

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// String lag = mSharedPreferences.getString("iat_language_preference",
		// "mandarin");
		String lag = "zh_cn";
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS,
				SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, "iat_vadbos_preference", "5000"));
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS,
				SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, "iat_vadeos_preference", "2000"));
		// 设置标点符号 "1": 带标点 "0"： 不带标点
		mIat.setParameter(SpeechConstant.ASR_PTT,
				SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, "iat_punc_preference", "0"));
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, new FileManager().getVoicePath());
	}

	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 1:
			if (sendCount == 0) {
				sendTextTask();
				sendCount++;
				if (mIat.isListening()) {
					mIat.cancel();
					iatDialog.dismiss();
				}
			}
			break;
		case 2:
			changModeTitle(type);
			break;
		case EXIT:
			Voice2TextActivity.this.finish();
			break;
		}
	}

	private void changModeTitle(int type) {
		switch (type) {
		case 1:// 设备小助手
			tvTitle.setText(CommDefines.getSkinManager().string(R.string.voice_control_type1));
			break;
		case 2:
			tvTitle.setText(CommDefines.getSkinManager().string(R.string.voice_control_type2));
			break;
		case 3:
			tvTitle.setText(CommDefines.getSkinManager().string(R.string.voice_control_type3));
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 退出时释放连接
//		if(mIat!=null){
//			mIat.cancel();
//			mIat.destroy();
//		}
//		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE, null);
//		soundPool.release();
	}

}
