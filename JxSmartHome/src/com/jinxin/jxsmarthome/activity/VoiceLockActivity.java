package com.jinxin.jxsmarthome.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import com.iflytek.cloud.VoiceWakeuper;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.fragment.SetVoiceLockFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 声纹锁注册
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class VoiceLockActivity extends BaseActionBarActivity implements
		OnClickListener {

	private static final int PWD_TYPE_TEXT = 1;
	private static final int PWD_TYPE_FREE = 2;
	private static final int PWD_TYPE_NUM = 3;
	// 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
	private int pwdType = PWD_TYPE_NUM;

	private TextView switchTips;
	private TextView setVoicePwd;
	private TextView confirmVoicePwd;
	private ImageButton switchButton;
	private Context context;
	private TextView showPwdTextView, showMsgTextView, showRegFbkTextView;
	
	// 声纹识别对象
	private SpeakerVerifier mVerify;
	// 声纹AuthId
	private String mAuthId = "";
	// 文本声纹密码
	private String mTextPwd = "";
	// 数字声纹密码
	private String mNumPwd = "";
	// 数字声纹密码段，默认有5段
	private String[] mNumPwdSegs;
	
	private boolean isRegisted = false;
	private boolean isOpen = false;
	private VoiceWakeuper mIvw;//语音唤醒
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
	}

	private void initData() {
		context = this;
		this.mAuthId = CommUtil.getCurrentLoginAccount();
		//采用单例模式，在Application中做初始化
		this.mVerify = JxshApp.instance.getVerifier();
		
		//获取语音唤醒对象
		mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null && mIvw.isListening()) {
			mIvw.stopListening();
		}
		
		// 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
//		mVerify = SpeakerVerifier.createVerifier(this, new InitListener() {
//
//			@Override
//			public void onInit(int errorCode) {
//				if (ErrorCode.SUCCESS == errorCode) {
//					Logger.debug(null, "引擎初始化成功");
//				} else {
//					Logger.debug(null, "引擎初始化失败，错误码：" + errorCode);
//				}
//			}
//		});
		
		//查询当前账号是否注册过声纹
		if (!TextUtils.isEmpty(mAuthId) && mVerify != null) {
			performModelOperation("que", mModelOperationListener);
		}
	}
	
	private void initView() {
		setContentView(R.layout.activity_voice_lock_layout);
		this.switchTips = (TextView) findViewById(R.id.voice_lock_tips_tv);
		this.setVoicePwd = (TextView) findViewById(R.id.reset_voice_lock_tv);
		this.confirmVoicePwd = (TextView) findViewById(R.id.confirm_voice_lock_tv);
		this.switchButton = (ImageButton) findViewById(R.id.switch_button);
		this.showPwdTextView = (TextView) findViewById(R.id.showPwd);
		this.showMsgTextView = (TextView) findViewById(R.id.showMsg);
		this.showRegFbkTextView = (TextView) findViewById(R.id.showRegFbk);
		
		this.setVoicePwd.setOnClickListener(this);
		this.confirmVoicePwd.setOnClickListener(this);
		this.switchButton.setOnClickListener(this);
		
	}
	
	// 初始化TextView
	private void initTextView() {
		mNumPwd = "";
		showPwdTextView.setText("");
		showMsgTextView.setText("");
		showRegFbkTextView.setText("");
	}
	
	@Override
	protected void onResume() {
		changeSwitchBackground();
		super.onResume();
	}
	
	/**
	 * 改变开关状态
	 * @param isOpen
	 */
	public void changeSwitchBackground(){
		//查询当前账号是否注册过声纹
		if (!TextUtils.isEmpty(mAuthId) && mVerify != null) {
			performModelOperation("que", mModelOperationListener);
		}
		isOpen = SharedDB.loadBooleanFromDB(mAuthId, ControlDefine.KEY_VOICE_LOCK, false);
		Logger.debug(null, "isOpen = "+isOpen);
		//设置开关背景
		switchButton.setImageDrawable(CommDefines.getSkinManager().drawable(
				isOpen ? R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
		switchTips.setText(CommDefines.getSkinManager().string(
				isOpen ? R.string.voice_lock_open : R.string.voice_lock_close));
	}

	// 执行模型操作
	private void performModelOperation(String operation, SpeechListener listener) {
		// 清空参数
		mVerify.setParameter(SpeechConstant.PARAMS, null);
		mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);

		if (pwdType == PWD_TYPE_TEXT) {
			// 文本密码删除需要传入密码
			if (TextUtils.isEmpty(mTextPwd)) {
				JxshApp.showToast(context, "请获取密码后进行操作");
				return;
			}
			mVerify.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
		} else if (pwdType == PWD_TYPE_FREE) {
			mVerify.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
		} else if (pwdType == PWD_TYPE_NUM) {

		}
		mVerify.sendRequest(operation, mAuthId, listener);
	}
	
	/**
	 * 声音模型查询、删除监听
	 */
	SpeechListener mModelOperationListener = new SpeechListener() {
		
		@Override
		public void onEvent(int eventType, Bundle params) {
		}
		
		@Override
		public void onBufferReceived(byte[] buffer) {
			
			String result = new String(buffer);
			try {
				JSONObject object = new JSONObject(result);
				String cmd = object.getString("cmd");
				int ret = object.getInt("ret");
				
				if ("del".equals(cmd)) {
					isRegisted = false;
					if (ret == ErrorCode.SUCCESS) {
						JxshApp.showToast(context, "删除成功");
						SharedDB.saveBooleanDB(mAuthId, ControlDefine.KEY_VOICE_LOCK, false);
						changeSwitchBackground();
						Fragment fragment = new SetVoiceLockFragment();
						addFragment(fragment, true);
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						JxshApp.showToast(context, "删除失败，未注册声音");
					}
				} else if ("que".equals(cmd)) {
					if (ret == ErrorCode.SUCCESS) {
						Logger.debug(null, "声纹模型存在");
						isRegisted = true;
//						JxshApp.showToast(context, getResources().getString(R.string.voice_lock_reg_tips));
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						isRegisted = false;
						Logger.debug(null, "声纹模型不存在");
//						JxshApp.showToast(context, getResources().getString(R.string.open_voice_unreg_tips));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onCompleted(SpeechError error) {
			
			if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
				JxshApp.showToast(context, error.getPlainDescription(true));
			}
		}
	};

	/**
	 * 声纹验证监听
	 */
	VerifierListener mVerifyListener = new VerifierListener() {

		@Override
		public void onResult(VerifierResult result) {
			showMsgTextView.setText(result.source);
			
			if (result.ret == 0) {
				// 验证通过
				showMsgTextView.setText("验证成功");
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
					showMsgTextView.setText("验证失败，请重新验证");
					break;
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
				JxshApp.showToast(context, error.getPlainDescription(true));
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
			JxshApp.showToast(context,"当前正在说话，音量大小：" + arg0);
			
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportActionBar().setTitle(getResources().getString(R.string.voice_lock));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		//查询当前账号是否注册过声纹
		if (!TextUtils.isEmpty(mAuthId) && mVerify != null) {
			performModelOperation("que", mModelOperationListener);
		}
		switch (v.getId()) {
		case R.id.switch_button:// 开启关闭
			if (!isRegisted) {//未注册
				JxshApp.showToast(context, "请先注册声音");
				return;
			}
			isOpen = !isOpen;
			SharedDB.saveBooleanDB(mAuthId, ControlDefine.KEY_VOICE_LOCK, isOpen);
			changeSwitchBackground();
			break;
		case R.id.reset_voice_lock_tv:// 重设声纹
			if (isRegisted) {
				JxshApp.showToast(context, "您已设置声纹锁，是否删除重设");
				final MessageBox mb = new MessageBox(context, "删除声音", "删除声音？",
						MessageBox.MB_OK | MessageBox.MB_CANCEL);
				mb.setButtonText("确定", "取消");
				mb.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						switch(mb.getResult()){
						case MessageBox.MB_OK://删除
							performModelOperation("del", mModelOperationListener);
							break;
						case MessageBox.MB_CANCEL:
							mb.dismiss();
							break;
						}
					}
				});
				mb.show();
			}else{
				Fragment fragment = new SetVoiceLockFragment();
				addFragment(fragment, true);
			}
			break;
		case R.id.confirm_voice_lock_tv:// 验证声纹
			// 清空提示信息
			initTextView();
			if (isRegisted) {
				// 清空参数
				mVerify.setParameter(SpeechConstant.PARAMS, null);
				mVerify.setParameter(SpeechConstant.ISV_AUDIO_PATH,
						Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
//							mVerify = SpeakerVerifier.getVerifier();
				mVerify = JxshApp.instance.getVerifier();
				// 设置业务类型为验证
				mVerify.setParameter(SpeechConstant.ISV_SST, "verify");
				// 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
				mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
				if (pwdType == PWD_TYPE_NUM) {
					// 数字密码注册需要传入密码
					String verifyPwd = mVerify.generatePassword(8);
					mVerify.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
					showPwdTextView.setText("请匀速读出："+ verifyPwd);
				}
				mVerify.setParameter(SpeechConstant.AUTH_ID, mAuthId);
				mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
				// 开始验证
				mVerify.startListening(mVerifyListener);
			}else{
				JxshApp.showToast(context, "声音模型不存在，请先录入声音");
			}
			
			break;

		default:
			break;
		}
	}
	
	/**
	 * 添加fragment
	 * @param fragment
	 * @param addToStack
	 */
	private void addFragment(Fragment fragment, boolean addToStack) {
		if (fragment != null && addToStack) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.voice_lock_fragmentlayout, fragment)
					.addToBackStack(null).commitAllowingStateLoss();
		} else if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.voice_lock_fragmentlayout, fragment).commitAllowingStateLoss();
		}
	}

	@Override
	protected void onDestroy() {
		if (mVerify != null) {
			mVerify.stopListening();
//			mVerify.destroy();
		}
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE, null);
		super.onDestroy();
	}

}
