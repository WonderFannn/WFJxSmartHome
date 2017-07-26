package com.jinxin.jxsmarthome.activity;

import u.aly.de;

import com.jinxin.jxsmarthome.R;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_remote_controller);
		
		initData();
		initViews();
		
	}

	private void initData() {
		// TODO Auto-generated method stub
		
	}

	private void initViews() {
		etDeviceName = (EditText)findViewById( R.id.et_device_name );
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
			//todo
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
