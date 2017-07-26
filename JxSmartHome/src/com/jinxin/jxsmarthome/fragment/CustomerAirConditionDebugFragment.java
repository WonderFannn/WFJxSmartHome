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
import android.text.TextUtils;
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

/**
 * 电视按键调试
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerAirConditionDebugFragment extends Fragment implements OnClickListener{

	private Context context;
	private Button mOpenBtn, mModeBtn, mLevelBtn, mDirectionBtn;
	private ImageView iv1, iv2, iv3, iv4;
	
	private int type = 1;//1—码库  2—学习
	private MyHandler mHandler;
	private CodeLibraryTask cLibraryTask = null;
	private boolean isUse = false;
	private static final int SHOW_DIALOG = 100;
	private RemoteBrandsType brand = null;
	private String mCode;
	private CustomerProduct currUFO = null;
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
		View view = CommDefines.getSkinManager().view(R.layout.fragment_aircondition_debug_layout, container);
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
		this.mModeBtn = (Button) view.findViewById(R.id.btn_device_mode);
		this.mLevelBtn = (Button) view.findViewById(R.id.btn_wind_level);
		this.mDirectionBtn = (Button) view.findViewById(R.id.btn_wind_direction);
		
		this.iv1 = (ImageView) view.findViewById(R.id.iv_usable1);
		this.iv2 = (ImageView) view.findViewById(R.id.iv_usable2);
		this.iv3 = (ImageView) view.findViewById(R.id.iv_usable3);
		this.iv4 = (ImageView) view.findViewById(R.id.iv_usable4);
		
		mOpenBtn.setOnClickListener(this);
		mModeBtn.setOnClickListener(this);
		mLevelBtn.setOnClickListener(this);
		mDirectionBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_open_swich:
			sendCmd(1);
			break;
		case R.id.btn_device_mode:
			sendCmd(2);
			break;
		case R.id.btn_wind_level:
			sendCmd(3);
			break;
		case R.id.btn_wind_direction:
			sendCmd(4);
			break;
		}
	}
	
	private void sendCmd(final int position){
		if (brand == null || TextUtils.isEmpty(mCode) || currUFO == null) 	{
			JxshApp.showToast(context, "brand or mode or ufo is null");
			return;
		}
		
		currPos = position;
		String keyName = getKeyname(position);
		//TODO
		cLibraryTask = new CodeLibraryTask(getActivity(), mCode, currUFO.getWhId(),
				keyName, brand.getDeviceId(), brand.getId() , currUFO.getAddress485(), type,"",-1);
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
	
	private String getKeyname(int pos){
		JSONObject _jb = new JSONObject();
		try {
			if (pos == 1) {
				_jb.put("cOnoff", 0);
				_jb.put("cMode", 0);
				_jb.put("cWind", 0);
				_jb.put("cWinddir", 0);
				_jb.put("cTemp", 16);
			}else if(pos == 2){
				_jb.put("cOnoff", 0);
				_jb.put("cMode", 1);
				_jb.put("cWind", 0);
				_jb.put("cWinddir", 0);
				_jb.put("cTemp", 16);
			}else if(pos == 3){
				_jb.put("cOnoff", 0);
				_jb.put("cMode", 0);
				_jb.put("cWind", 1);
				_jb.put("cWinddir", 0);
				_jb.put("cTemp", 16);
			}else if(pos == 4){
				_jb.put("cOnoff", 0);
				_jb.put("cMode", 0);
				_jb.put("cWind", 0);
				_jb.put("cWinddir", 1);
				_jb.put("cTemp", 16);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return _jb.toString();
	}
	
	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
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
			case SHOW_DIALOG:
				showUsableDialog(currPos);
				break;
			}
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
