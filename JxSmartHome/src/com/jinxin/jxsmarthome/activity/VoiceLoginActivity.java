package com.jinxin.jxsmarthome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.offline.util.OfflineToOnline;
import com.jinxin.record.SharedDB;

public class VoiceLoginActivity extends BaseActionBarActivity implements OnClickListener{

	// 声纹识别对象
	private SpeakerVerifier mVerify;
	// 声纹AuthId
	private String mAuthId = "";
	private Context context;
	// 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
	private int pwdType = 3;
	private boolean isOpen = false;
	public static String ACTION_LOGINED = "action_logined";
	public static String ACTION_LOGIN_FAIL = "action_login_fail";

	
	private TextView showPwdTextView, showMsgTextView, showRegFbkTextView;
	private ImageView  ivReverify;
	
	private BroadcastReceiver mLoginedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_LOGINED.equals(intent.getAction())) {
				Intent SetIntent = new Intent(context, MainActivity.class);
				SetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(SetIntent);
				finish();
			} else if (ACTION_LOGIN_FAIL.equals(intent.getAction())) {
				Intent SetIntent = new Intent(context, LoginActivity.class);
				SetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(SetIntent);
				finish();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {
		context = this;
		this.mAuthId = getIntent().getStringExtra("account");
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_LOGINED);
		myIntentFilter.addAction(ACTION_LOGIN_FAIL);
		registerReceiver(mLoginedReceiver, myIntentFilter);
		//采用单例模式，在Application中做初始化
		this.mVerify = JxshApp.instance.getVerifier();
		// 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
//		mVerify = SpeakerVerifier.createVerifier(this, new InitListener() {
//
//			@Override
//			public void onInit(int errorCode) {
//				if (ErrorCode.SUCCESS == errorCode) {
//					JxshApp.showToast(context, "引擎初始化成功");
//				} else {
//					JxshApp.showToast(context, "引擎初始化失败，错误码：" + errorCode);
//				}
//			}
//		});
		
		if (!TextUtils.isEmpty(mAuthId)) {
			// 清空参数
			mVerify.setParameter(SpeechConstant.PARAMS, null);
			mVerify.setParameter(SpeechConstant.ISV_AUDIO_PATH,
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
//			mVerify = SpeakerVerifier.getVerifier();
			mVerify = JxshApp.instance.getVerifier();
			// 设置业务类型为验证
			mVerify.setParameter(SpeechConstant.ISV_SST, "verify");
			// 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
			mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
			// 数字密码注册需要传入密码
			String verifyPwd = mVerify.generatePassword(8);
			mVerify.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
			showPwdTextView.setText(verifyPwd);
			// 设置语音采样频率
			mVerify.setParameter(SpeechConstant.VAD_BOS, "5000");
			mVerify.setParameter(SpeechConstant.VAD_EOS, "1000");
			mVerify.setParameter(SpeechConstant.AUTH_ID, mAuthId);
			mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
			// 开始验证
			mVerify.startListening(mVerifyListener);
			
		}
	}

	private void initView() {
		setContentView(R.layout.activity_voice_login_layout);
		this.showPwdTextView = (TextView) findViewById(R.id.voice_login_pwd_tv);
		this.showMsgTextView = (TextView) findViewById(R.id.show_msg_tv);
		this.showRegFbkTextView = (TextView) findViewById(R.id.show_fbk_tv);
		this.ivReverify =  (ImageView) findViewById(R.id.iv_reverify);
		ivReverify.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mLoginedReceiver);
		if (null != mVerify) {
			mVerify.stopListening();
			mVerify = null;
//			mVerify.destroy();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportActionBar().setTitle(getResources().getString(R.string.voice_lock_login));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	/**
	 * 声纹验证监听
	 */
	VerifierListener mVerifyListener = new VerifierListener() {


		@Override
		public void onResult(VerifierResult result) {
			showMsgTextView.setText(result.source);
			
			if (result.ret == 0) {
				// 验证通过
				showMsgTextView.setText("验证成功,正在登录...");
				if (!TextUtils.isEmpty(mAuthId)) {
					String secretkey = EncryptUtil.getPassword(mAuthId);
					OfflineToOnline.changeToOnline(mAuthId, secretkey, VoiceLoginActivity.this);
				}
			}
			else{
				// 验证不通过
				switch (result.err) {
				case VerifierResult.MSS_ERROR_IVP_GENERAL:
					showMsgTextView.setText("内核异常");
					break;
				case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
					showMsgTextView.setText("出现截幅");
					break;
				case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
					showMsgTextView.setText("太多噪音");
					break;
				case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
					showMsgTextView.setText("录音太短");
					break;
				case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
					showMsgTextView.setText("验证不通过，您所读的文本不一致");
					break;
				case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
					showMsgTextView.setText("音量太低");
					break;
				
				default:
					showMsgTextView.setText("验证失败，请点击下方按钮重新验证");
					break;
				}
				if (mVerify != null) {
					String verifyPwd = mVerify.generatePassword(8);
					mVerify.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
					showPwdTextView.setText(verifyPwd);
					mVerify.setParameter(SpeechConstant.AUTH_ID, mAuthId);
					mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
					// 开始验证
					mVerify.startListening(mVerifyListener);
				}
			}
		}
		// 保留方法，暂不用
		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onError(SpeechError error) {
			
			switch (error.getErrorCode()) {
			case ErrorCode.MSP_ERROR_NOT_FOUND:
				showMsgTextView.setText("声音模型不存在，请先设置");
				break;

			default:
				JxshApp.showToast(context, error.getPlainDescription(true)+",点击下方按钮重新验证");
				break;
			}
		}

		@Override
		public void onEndOfSpeech() {
			JxshApp.showToast(context,"录音结束，正在验证");
		}

		@Override
		public void onBeginOfSpeech() {
			JxshApp.showToast(context,"开始录音");
		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			JxshApp.showToast(context,"正在录音，音量大小：" + arg0);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_reverify:
			if (mVerify.isListening()) {
				mVerify.stopListening();
			}
			if (mVerify != null) {
				String verifyPwd = mVerify.generatePassword(8);
				mVerify.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
				showPwdTextView.setText(verifyPwd);
				mVerify.setParameter(SpeechConstant.AUTH_ID, mAuthId);
				mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
				// 开始验证
				mVerify.startListening(mVerifyListener);
			}
			break;

		default:
			break;
		}
	}
}
