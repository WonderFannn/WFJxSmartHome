package com.jinxin.jxsmarthome.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.jinxin.datan.net.command.CodeLibraryTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.RemoteBrandsTypeActivity;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.RemoteBrandsType;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 电视按键调试
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerTVDebugFragment extends Fragment implements OnClickListener{

	private Context context;
	private Button mOpenBtn, mMuteBtn, mHomeBtn, mPlayBtn, mBackBtn, mSureBtn;
	private ImageView iv1, iv2, iv3, iv4, iv5,iv6;
	
	private int type = 1;//1—码库  2—学习
	private MyHandler mHandler;
	private CodeLibraryTask cLibraryTask = null;
	private boolean isUse = false;
	private static final int SHOW_DIALOG = 100;
	private RemoteBrandsType brand = null;
	private CustomerProduct  currUFO = null;
	private String mCode;
	private int currPos;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = CommDefines.getSkinManager().view(R.layout.fragment_tv_debug_layout, container);
		ininData();
		initView(view);
		return view;
	}

	private void ininData() {
		mHandler = new MyHandler();
		brand = (RemoteBrandsType) getArguments().getSerializable(RemoteBrandsTypeActivity.BRAND);
		currUFO = (CustomerProduct) getArguments().getSerializable("UFO");
		mCode = getArguments().getString("mCode");
	}

	private void initView(View view) {
		this.mOpenBtn = (Button) view.findViewById(R.id.btn_open_swich);
		this.mMuteBtn = (Button) view.findViewById(R.id.btn_device_mute);
		this.mHomeBtn = (Button) view.findViewById(R.id.btn_remote_home);
		this.mPlayBtn = (Button) view.findViewById(R.id.btn_device_start);
		this.mBackBtn = (Button) view.findViewById(R.id.btn_back);
		this.mSureBtn = (Button) view.findViewById(R.id.btn_ok);
		
		this.iv1 = (ImageView) view.findViewById(R.id.iv_usable1);
		this.iv2 = (ImageView) view.findViewById(R.id.iv_usable2);
		this.iv3 = (ImageView) view.findViewById(R.id.iv_usable3);
		this.iv4 = (ImageView) view.findViewById(R.id.iv_usable4);
		this.iv5 = (ImageView) view.findViewById(R.id.iv_usable5);
		this.iv6 = (ImageView) view.findViewById(R.id.iv_usable6);
		
		mOpenBtn.setOnClickListener(this);
		mMuteBtn.setOnClickListener(this);
		mHomeBtn.setOnClickListener(this);
		mPlayBtn.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
		mSureBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_open_swich:
			sendCmd(1);
			break;
		case R.id.btn_device_mute:
			sendCmd(2);
			break;
		case R.id.btn_remote_home:
			sendCmd(3);
			break;
		case R.id.btn_device_start:
			sendCmd(4);
			break;
		case R.id.btn_back:
			sendCmd(5);
			break;
		case R.id.btn_ok:
			sendCmd(6);
			break;
		}
	}
	
	private void sendCmd(final int position){
		if (currUFO == null || brand == null) {
			Logger.debug(null, "ufo or brand is null");
			return;
		}
		
		currPos = position;
		String keyName = getKeyname(position);
		//TODO
		cLibraryTask = new CodeLibraryTask(getActivity(), mCode, 
				currUFO.getWhId(), keyName, brand.getDeviceId(), brand.getId(), currUFO.getAddress485(), type,"",-1);
		cLibraryTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showToast(context, "指令已发送，请稍后...");
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				JxshApp.closeLoading();
				mHandler.sendEmptyMessage(SHOW_DIALOG);
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		cLibraryTask.start();
	}
	
	private String getKeyname(int positon){
		String code = "";
		switch (positon) {
		case 1:
			code = "0";
			break;
		case 2:
			code = "37";
			break;
		case 3:
			code = "29";
			break;
		case 4:
			code = "4";
			break;
		case 5:
			code = "17";
			break;
		case 6:
			code = "18";
			break;

		}
		JSONObject jb = new JSONObject();
		try {
			jb.put("code", code);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jb.toString();
	}
	
	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				iv1.setVisibility(View.VISIBLE);
				iv1.setImageResource(isUse ?  R.drawable.ico_usable:R.drawable.icon_unusable);
				break;
			case 2:
				iv2.setVisibility(View.VISIBLE);
				iv2.setImageResource(isUse ?  R.drawable.ico_usable:R.drawable.icon_unusable);
				break;
			case 3:
				iv3.setVisibility(View.VISIBLE);
				iv3.setImageResource(isUse ?  R.drawable.ico_usable:R.drawable.icon_unusable);
				break;
			case 4:
				iv4.setVisibility(View.VISIBLE);
				iv4.setImageResource(isUse ?  R.drawable.ico_usable:R.drawable.icon_unusable);
				break;
			case 5:
				iv5.setVisibility(View.VISIBLE);
				iv5.setImageResource(isUse ?  R.drawable.ico_usable:R.drawable.icon_unusable);
				break;
			case 6:
				iv6.setVisibility(View.VISIBLE);
				iv6.setImageResource(isUse ?  R.drawable.ico_usable:R.drawable.icon_unusable);
				break;
			case SHOW_DIALOG:
				showUsableDialog(currPos);
				break;
			}
			super.handleMessage(msg);
		}
		
	}
	
	/**
	 * 显示是否可用对话框
	 */
	private void showUsableDialog(final int position){
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.remote_usable_dialog_layout, null);
		final CustomerCenterDialog dialog = new CustomerCenterDialog(context, R.style.dialog, v);
		ImageView ivDismiss = (ImageView) v.findViewById(R.id.iv_dismiss);
		Button btnSure = (Button) v.findViewById(R.id.button_ok);
		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		
		
		btnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isUse = true;
				mHandler.sendEmptyMessage(position);
				dialog.dismiss();
			}
		});
		
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isUse = false;
				mHandler.sendEmptyMessage(position);
				dialog.dismiss();
			}
		});
		
		ivDismiss.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
}
