package com.jinxin.jxsmarthome.activity;

import java.util.List;

import u.aly.de;

import com.jinxin.infrared.model.InfraredCodeCMDUtil;
import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.infrared.model.InfraredCodeLibraryUtil;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SaveRemoteControllerActivity extends BaseActionBarActivity implements OnClickListener {
	
	private EditText etDeviceName;
	private FrameLayout btnScene;
	private FrameLayout btnOffice;
	private FrameLayout btnDrawingRoom;
	private FrameLayout btnDiningRoom;
	private FrameLayout btnKitchen;
	private FrameLayout btnBedroom;
	private TextView btnReturn;
	private TextView btnSave;

	private FrameLayout btnNowSelect;
	private String deviceName = "";
	
	private int deviceType;
	private String brand;
	private byte[] mCode;

	private FunDetail funDetail;
	private ProductFun productFun;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_remote_controller);
		
		initData();
		initViews();
		
	}

	private void initData() {
		Intent intent = getIntent();
		deviceType = intent.getIntExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, 0);
		brand = intent.getStringExtra(InfraredCodeLibraryConstant.IntentTag.BRAND);
		mCode = intent.getByteArrayExtra(InfraredCodeLibraryConstant.IntentTag.CODE);
		funDetail = (FunDetail) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL);
		productFun = (ProductFun) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN);
		
		deviceName = brand+InfraredCodeLibraryConstant.DEVICE_NAME_CN[deviceType];
	}

	private void initViews() {
		etDeviceName = (EditText)findViewById( R.id.et_device_name );
		etDeviceName.setText(deviceName);
		etDeviceName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				deviceName = s.toString();
			}
		});
		btnScene = (FrameLayout)findViewById( R.id.btn_scene );
		btnScene.setOnClickListener(this);
		btnOffice = (FrameLayout)findViewById( R.id.btn_office );
		btnOffice.setOnClickListener(this);
		btnDrawingRoom = (FrameLayout)findViewById( R.id.btn_drawing_room );
		btnDrawingRoom.setOnClickListener(this);
		btnDiningRoom = (FrameLayout)findViewById( R.id.btn_dining_room );
		btnDiningRoom.setOnClickListener(this);
		btnKitchen = (FrameLayout)findViewById( R.id.btn_kitchen );
		btnKitchen.setOnClickListener(this);
		btnBedroom = (FrameLayout)findViewById( R.id.btn_bedroom );
		btnBedroom.setOnClickListener(this);
		btnReturn = (TextView)findViewById( R.id.btn_return );
		btnReturn.setOnClickListener(this);
		btnSave = (TextView)findViewById( R.id.btn_save );
		btnSave.setOnClickListener(this);
		btnNowSelect = btnScene;
	}
	@Override
	public void onClick(View v) {
		if (v == btnSave) {
			//TODO 保存遥控器信息
			
		}else if (v == btnReturn) {
			finish();
		}else {
			setSceneSelect((FrameLayout) v);
		}
		
	}

	private void setSceneSelect(FrameLayout v) {
		btnNowSelect.setSelected(false);
		v.setSelected(true);
		btnNowSelect = v;
		String a = ((TextView) v.getChildAt(0)).getText().toString();
		deviceName = a + "的" + deviceName;
		etDeviceName.setText(deviceName);
		etDeviceName.setSelection(deviceName.length());
	}
}
