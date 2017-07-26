package com.jinxin.jxsmarthome.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.VoiceHelperActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.device.Text2VoiceManager;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.JsonParser;
import com.jinxin.record.FileManager;
import com.jinxin.record.SharedDB;

/**
 * 发送语音到功放
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class Text2VoiceFragment extends Fragment implements OnClickListener{
	
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog iatDialog;
	
	private EditText mResultText; //语音识别结果文字
	private Button recBtn;
	
	private int ret = 0;// 函数调用返回值
	private VoiceWakeuper mIvw;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		((VoiceHelperActivity) getActivity()).changeMenu(1);
		if (mIvw != null) {
			// 停止语音唤醒监听，否则无法语音识别
			mIvw.stopListening();
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE, null);
		super.onPause();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.menu_text2voice, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("语音合成");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_text2voice_layout, container, false);
		initView(view);
		initData();
		return view;
	}
	
	/**
	 * 初始化布局
	 * @param view
	 */
	private void initView(View view){
		this.mResultText = (EditText) view.findViewById(R.id.iat_text);
		this.recBtn = (Button) view.findViewById(R.id.iat_recognize);
		mResultText.setOnClickListener(this);
		recBtn.setOnClickListener(this);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		// 初始化识别对象
		mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		iatDialog = new RecognizerDialog(getActivity(),mInitListener);
		
		//获取语音唤醒对象
		mIvw = VoiceWakeuper.getWakeuper();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.send_text:
			//发送文字 
			Text2VoiceManager manager = new Text2VoiceManager(getActivity());
			manager.switchAndSend(mResultText.getText().toString());
			break;
		}
		return true;
	}
	
	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Logger.debug(null, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		JxshApp.showToast(getActivity(), "初始化失败,错误码："+code);
        	}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//开始语音识别
		case R.id.iat_recognize:
//			mResultText.setText(null);// 清空显示内容
			// 设置参数
			setParam();
			boolean isShowDialog = true;
			if (isShowDialog) {
				// 显示听写对话框
				iatDialog.setListener(recognizerDialogListener);
				iatDialog.show();
				JxshApp.showToast(getActivity(), getString(R.string.text_begin));
			} else {
				// 不显示听写对话框
				ret = mIat.startListening(recognizerListener);
				if(ret != ErrorCode.SUCCESS){
					JxshApp.showToast(getActivity(), "听写失败,错误码：" + ret);
				}else {
					JxshApp.showToast(getActivity(), getString(R.string.text_begin));
				}
			}
			break;
		}
	}
	
	/**
	 * 听写监听器。
	 */
	private RecognizerListener recognizerListener=new RecognizerListener(){

		@Override
		public void onBeginOfSpeech() {
			JxshApp.showToast(getActivity(), "开始说话");
		}


		@Override
		public void onError(SpeechError error) {
			JxshApp.showToast(getActivity(), error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			JxshApp.showToast(getActivity(), "结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			mResultText.append(text);
			mResultText.setSelection(mResultText.length());
			if(isLast) {
				//TODO 最后的结果
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			
		}


		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			JxshApp.showToast(getActivity(), "当前正在说话，音量大小：" + arg0);
		}
	};

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			mResultText.append(text);
			mResultText.setSelection(mResultText.length());
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			JxshApp.showToast(getActivity(),error.getPlainDescription(true));
		}
	};
	
	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	public void setParam(){
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
//		String lag = mSharedPreferences.getString("iat_language_preference", "mandarin");
		String lag = "zh_cn";
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		}else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT,lag);
		}
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS, SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, "iat_vadbos_preference", "4000"));
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS, SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,"iat_vadeos_preference", "1000"));
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT, SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,"iat_punc_preference", "1"));
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, new FileManager().getVoicePath());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//重置ActionBar
		((VoiceHelperActivity) getActivity()).changeMenu(0);
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
	}
}
