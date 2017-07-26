package com.jinxin.jxsmarthome.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.VoiceLockActivity;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

public class SetVoiceLockFragment extends Fragment implements OnClickListener {

	private static final int PWD_TYPE_TEXT = 1;
	private static final int PWD_TYPE_FREE = 2;
	private static final int PWD_TYPE_NUM = 3;
	// 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
	private int pwdType = PWD_TYPE_NUM;//默认数字密码
	// 声纹识别对象
	private SpeakerVerifier mVerify;
	// 声纹AuthId
	private String mAuthId = "";
	// 数字声纹密码
	private String mNumPwd = "";
	// 数字声纹密码段，默认有5段
	private String[] mNumPwdSegs;

	private Context context;
	private EditText allPwdText;
	private Button getPwdBtn, regButton, cancleBtn, deleteBtn;
	private TextView showPwdTextView, showMsgTextView, showRegFbkTextView;
	
	private boolean isRegisted = false;//是否注册过
	private boolean isRegistting = false;//是否正在注册
	
	private VoiceWakeuper mIvw;//语音唤醒
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reset_voice_layout,
				container, false);
		initView(view);
		initData();
		return view;
	}

	/**
	 * 初始参数数据
	 */
	private void initData() {
		this.mAuthId = CommUtil.getCurrentLoginAccount();
		//采用单例模式，在Application中做初始化
		this.mVerify = JxshApp.instance.getVerifier();
		
		//获取语音唤醒对象
		mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null && mIvw.isListening()) {
			mIvw.stopListening();
		}
		
		// 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
//		mVerify = SpeakerVerifier.createVerifier(context, new InitListener() {
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
		
		//查询当前账号是否注册过声纹
		if (!TextUtils.isEmpty(mAuthId) && mVerify != null) {
			performModelOperation("que", mModelOperationListener);
		}
		
		// 获取密码之前要终止之前的注册过程
		mVerify.cancel(false);
		initTextView();
		// 清空参数
		mVerify.setParameter(SpeechConstant.PARAMS, null);
		mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
		mVerify.getPasswordList(mPwdListenter);
	}

	/**
	 * 初始化View
	 * @param view
	 */
	private void initView(View view) {
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("注册声纹");

		this.allPwdText = (EditText) view.findViewById(R.id.voice_reg_pwd_tv);
		this.getPwdBtn = (Button) view.findViewById(R.id.get_num_pwd_btn);
		this.regButton = (Button) view.findViewById(R.id.reg_voice_btn);
		this.cancleBtn = (Button) view.findViewById(R.id.cancle_reg_voice_btn);
		this.deleteBtn = (Button) view.findViewById(R.id.delete_voice_btn);
		this.showPwdTextView = (TextView) view.findViewById(R.id.showPwd);
		this.showMsgTextView = (TextView) view.findViewById(R.id.showMsg);
		this.showRegFbkTextView = (TextView) view.findViewById(R.id.showRegFbk);
		
		this.getPwdBtn.setOnClickListener(this);
		this.regButton.setOnClickListener(this);
		this.cancleBtn.setOnClickListener(this);
		this.deleteBtn.setOnClickListener(this);
	}

	// 初始化TextView
	private void initTextView() {
		showPwdTextView.setText("");
		showMsgTextView.setText("");
		showRegFbkTextView.setText("");
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			
			break;
		}
		return true;
	}

	// 执行声纹查询、删除
	private void performModelOperation(String operation, SpeechListener listener) {
		// 清空参数
		mVerify.setParameter(SpeechConstant.PARAMS, null);
		mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);

		mVerify.sendRequest(operation, mAuthId, listener);
	}

	@Override
	public void onDestroyView() {
		if (null != mVerify) {
			mVerify.stopListening();
			mVerify.cancel(false);
//			mVerify.destroy();
		}
		((VoiceLockActivity)getActivity()).changeSwitchBackground();
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_num_pwd_btn:// 获取密码
			// 获取密码之前要终止之前的注册过程
			mVerify.cancel(false);
			initTextView();
			// 清空参数
			mVerify.setParameter(SpeechConstant.PARAMS, null);
			mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
			mVerify.getPasswordList(mPwdListenter);
			
			break;
		case R.id.reg_voice_btn:// 注册
			if (isRegisted) {
				JxshApp.showToast(context, CommDefines.getSkinManager().string(R.string.voice_lock_reg_tips));
			}else{
				if (!isRegistting) {
					if (pwdType == PWD_TYPE_NUM) {
						// 数字密码注册需要传入密码
						if (TextUtils.isEmpty(mNumPwd)) {
							JxshApp.showToast(context, "请获取密码后进行操作");
							return;
						}
						mVerify.setParameter(SpeechConstant.ISV_PWD, mNumPwd);
						showPwdTextView.setText("请匀速读出："+ mNumPwd.substring(0, 8));
						showMsgTextView.setText("训练 第" + 1 + "遍，剩余4遍");
					}
					
					mVerify.setParameter(SpeechConstant.AUTH_ID, mAuthId);
					// 设置业务类型为注册
					mVerify.setParameter(SpeechConstant.ISV_SST, "train");
					// 设置声纹密码类型
					mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
					// 开始注册
					mVerify.startListening(mRegisterListener);
				}else{// 取消注册
					mVerify.stopListening();
					mVerify.cancel(false);
					initTextView();
					isRegistting = false;
					regButton.setText("注册声音");
				}
			}
			break;
		case R.id.cancle_reg_voice_btn:// 取消注册
			mVerify.stopListening();
			mVerify.cancel(false);
			initTextView();
			break;
		case R.id.delete_voice_btn:// 删除声音
			performModelOperation("del", mModelOperationListener);
			break;

		default:
			break;
		}
	}
	
	String[] items;
	/**
	 * 获取密码监听
	 */
	SpeechListener mPwdListenter = new SpeechListener() {
		@Override
		public void onEvent(int eventType, Bundle params) {
		}
		
		@Override
		public void onBufferReceived(byte[] buffer) {
			String result = new String(buffer);
			switch (pwdType) {
			case PWD_TYPE_NUM:
				StringBuffer numberString = new StringBuffer();
				try {
					JSONObject object = new JSONObject(result);
					if (!object.has("num_pwd")) {
						initTextView();
						return;
					}
					
					JSONArray pwdArray = object.optJSONArray("num_pwd");
					numberString.append(pwdArray.get(0));
					for (int i = 1; i < pwdArray.length(); i++) {
						numberString.append("-" + pwdArray.get(i));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mNumPwd = numberString.toString();
				mNumPwdSegs = mNumPwd.split("-");
				allPwdText.setText("您的密码：\n" + mNumPwd);
				break;
			default:
				break;
			}

		}

		@Override
		public void onCompleted(SpeechError error) {
			
			if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
				JxshApp.showToast(context, "获取失败：" + error.getErrorCode());
			}
		}
	};
	
	/**
	 * 注册监听
	 */
	VerifierListener mRegisterListener =new VerifierListener() {

		@Override
		public void onResult(VerifierResult result) {
			showMsgTextView.setText(result.source);
			if (result.ret == ErrorCode.SUCCESS) {
				switch (result.err) {
				case VerifierResult.MSS_ERROR_IVP_GENERAL:
					showMsgTextView.setText("内核异常");
					break;
				case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
					showRegFbkTextView.setText("训练达到最大次数");
					break;
				case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
					showRegFbkTextView.setText("出现截幅");
					break;
				case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
					showRegFbkTextView.setText("太多噪音");
					break;
				case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
					showRegFbkTextView.setText("录音太短");
					break;
				case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
					showRegFbkTextView.setText("训练失败，您所读的文本不一致");
					break;
				case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
					showRegFbkTextView.setText("音量太低");
					break;
				
				default:
					showRegFbkTextView.setText("");
					break;
				}
				
				if (result.suc == result.rgn) {
					showMsgTextView.setText("注册成功，您可以使用声音登录应用了");
					isRegisted = true;
					isRegistting = false;
					regButton.setText("注册声音");
					SharedDB.saveBooleanDB(mAuthId, ControlDefine.KEY_VOICE_LOCK, true);
					if (PWD_TYPE_TEXT == pwdType) {
						showPwdTextView.setText("您的文本密码声纹ID：\n" + result.vid);
					} else if (PWD_TYPE_FREE == pwdType) {
						showPwdTextView.setText("您的自由说声纹ID：\n" + result.vid);
					} else if (PWD_TYPE_NUM == pwdType) {
						showPwdTextView.setText("您的数字密码声纹ID：\n" + result.vid);
					}
				} else {
					int nowTimes = result.suc + 1;
					int leftTimes = result.rgn - nowTimes;
					showPwdTextView.setText("请匀速读出：" + mNumPwdSegs[nowTimes - 1]);
					showMsgTextView.setText("训练 第" + nowTimes + "遍，剩余" + leftTimes + "遍");
				}
			}else {
				showMsgTextView.setText("注册失败，请重新开始。");	
			}
		}
		// 保留方法，暂不用
		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onError(SpeechError error) {
			isRegistting = false;
			regButton.setText("注册声音");
			if (error.getErrorCode() == ErrorCode.MSP_ERROR_ALREADY_EXIST) {
				JxshApp.showToast(context, CommDefines.getSkinManager().string(R.string.voice_lock_reg_tips));
			} else {
				JxshApp.showToast(context, "onError Code：" + error.getErrorCode());
			}
		}

		@Override
		public void onEndOfSpeech() {
			JxshApp.showToast(context, "录音结束，正在验证");
		}

		@Override
		public void onBeginOfSpeech() {
			isRegistting = true;
			regButton.setText("取消注册");
			JxshApp.showToast(context, "开始录音");
		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			JxshApp.showToast(context, "当前正在说话，音量大小：" + arg0);
			
		}
	};
	
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
						SharedDB.saveBooleanDB(mAuthId, ControlDefine.KEY_VOICE_LOCK, false);
						JxshApp.showToast(context, "删除成功");
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						JxshApp.showToast(context, "删除失败，未注册声音");
					}
				} else if ("que".equals(cmd)) {
					if (ret == ErrorCode.SUCCESS) {
						Logger.debug(null, "声纹模型存在");
						isRegisted = true;
						JxshApp.showToast(context, getResources().getString(R.string.voice_lock_reg_tips));
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						isRegisted = false;
						Logger.debug(null, "声纹模型不存在");
						JxshApp.showToast(context, getResources().getString(R.string.open_voice_unreg_tips));
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

}
