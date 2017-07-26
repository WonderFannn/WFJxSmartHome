package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.b;

import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerProduct;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SelectModelActivity extends BaseActionBarActivity implements OnItemClickListener{
	private ListView lv;
	private String[] strs;
	private int deviceType;
	private String brand;
	
	private CustomerProduct  currUFO = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	private void initView() {
		setContentView(R.layout.activity_select_brand);
		lv = (ListView) findViewById(R.id.lv_select_brand);
	}

	private void initData() {
		Intent intent = getIntent();
		deviceType = intent.getIntExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, 0);
		brand = intent.getStringExtra(InfraredCodeLibraryConstant.IntentTag.BRAND);
		currUFO = (CustomerProduct) intent.getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.UFO);

		Log.d("wangfan", brand);
		Resources res =getResources();
		switch (deviceType) {
		case InfraredCodeLibraryConstant.DeviceType.AirCleaner:
			strs = res.getStringArray(R.array.air_cleaner_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.AirCondition:
			strs = res.getStringArray(R.array.air_condition_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Projector:
			strs = res.getStringArray(R.array.projector_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Fan:
			strs = res.getStringArray(R.array.fan_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.TvBox:
			strs = res.getStringArray(R.array.tv_box_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Tv:
			strs = res.getStringArray(R.array.tv_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.InternetTv:
			strs = res.getStringArray(R.array.internet_tv_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Dvd:
			strs = res.getStringArray(R.array.dvd_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Calorifier:
			strs = res.getStringArray(R.array.calorifier_brand_model);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Camera:
			strs = res.getStringArray(R.array.camera_brand_model);
			break;

		default:
			strs = new String[1];
			strs[0] = "error";
			break;
		}
		strs = getModels(strs, brand);
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs));
		lv.setOnItemClickListener(this);
	}

	private String[] getModels(String[] strs, String brand) {
		
		List<String> list = new ArrayList<String>();
		list.add("智能匹配");
		for (String a : strs) {
			if (a.indexOf(brand) >= 0) {
				list.add(a);
			}
		}
		final int size =  list.size();
		String[] arr = (String[])list.toArray(new String[size]);
		return arr;
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, MatchDeviceActivity.class);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, deviceType);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.BRAND, brand);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.MODEL, strs[position]);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.UFO, currUFO);
		startActivity(intent);
	}
}
