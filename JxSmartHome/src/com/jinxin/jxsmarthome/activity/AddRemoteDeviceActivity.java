package com.jinxin.jxsmarthome.activity;

import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerProduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class AddRemoteDeviceActivity extends BaseActionBarActivity implements OnClickListener{
	
	private LinearLayout btnAirCleaner;
	private LinearLayout btnAirCondition;
	private LinearLayout btnProjector;
	private LinearLayout btnFan;
	private LinearLayout btnTvBox;
	private LinearLayout btnTv;
	private LinearLayout btnInternetTv;
	private LinearLayout btnDvd;
	private LinearLayout btnCalorifier;
	private LinearLayout btnCamera;
	
	private CustomerProduct  currUFO = null;

	/**
	 * Find the Views in the layout<br />
	 * <br />
	 * Auto-created on 2017-06-23 11:20:54 by Android Layout Finder
	 * (http://www.buzzingandroid.com/tools/android-layout-finder)
	 */
	private void findViews() {
		btnAirCleaner = (LinearLayout)findViewById( R.id.btn_air_cleaner );
		btnAirCleaner.setOnClickListener(this);
		btnAirCondition = (LinearLayout)findViewById( R.id.btn_air_condition );
		btnAirCondition.setOnClickListener(this);
		btnProjector = (LinearLayout)findViewById( R.id.btn_projector );
		btnProjector.setOnClickListener(this);
		btnFan = (LinearLayout)findViewById( R.id.btn_fan );
		btnFan.setOnClickListener(this);
		btnTvBox = (LinearLayout)findViewById( R.id.btn_tv_box );
		btnTvBox.setOnClickListener(this);
		btnTv = (LinearLayout)findViewById( R.id.btn_tv );
		btnTv.setOnClickListener(this);
		btnInternetTv = (LinearLayout)findViewById( R.id.btn_internet_tv );
		btnInternetTv.setOnClickListener(this);
		btnDvd = (LinearLayout)findViewById( R.id.btn_dvd );
		btnDvd.setOnClickListener(this);
		btnCalorifier = (LinearLayout)findViewById( R.id.btn_calorifier );
		btnCalorifier.setOnClickListener(this);
		btnCamera = (LinearLayout)findViewById( R.id.btn_camera );
		btnCamera.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
		
	}

	private void initData() {
		currUFO = (CustomerProduct) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.UFO);
		
	}

	private void initView() {
		setContentView(R.layout.activity_add_remote_device);
		findViews();
		
	}

	@Override
	public void onClick(View v) {
		int type;
		switch (v.getId()) {
		case R.id.btn_air_cleaner:
			type = InfraredCodeLibraryConstant.DeviceType.AirCleaner;
			break;
		case R.id.btn_air_condition:
			type = InfraredCodeLibraryConstant.DeviceType.AirCondition;
			break;
		case R.id.btn_projector:
			type = InfraredCodeLibraryConstant.DeviceType.Projector;
			break;
		case R.id.btn_fan:
			type = InfraredCodeLibraryConstant.DeviceType.Fan;
			break;
		case R.id.btn_tv_box:
			type = InfraredCodeLibraryConstant.DeviceType.TvBox;
			break;
		case R.id.btn_tv:
			type = InfraredCodeLibraryConstant.DeviceType.Tv;
			break;
		case R.id.btn_internet_tv:
			type = InfraredCodeLibraryConstant.DeviceType.InternetTv;
			break;
		case R.id.btn_dvd:
			type = InfraredCodeLibraryConstant.DeviceType.Dvd;
			break;
		case R.id.btn_calorifier:
			type = InfraredCodeLibraryConstant.DeviceType.Calorifier;
			break;
		case R.id.btn_camera:
			type = InfraredCodeLibraryConstant.DeviceType.Camera;
			break;
		default:
			type = 0;
			break;
		}
		Intent selectIntent = new Intent(this,SelectBrandActivity.class);
		selectIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE,type);
		selectIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.UFO, currUFO);
		startActivity(selectIntent);
	}
}
