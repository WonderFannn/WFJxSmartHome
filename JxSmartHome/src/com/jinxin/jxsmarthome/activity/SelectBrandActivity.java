package com.jinxin.jxsmarthome.activity;

import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SelectBrandActivity extends BaseActionBarActivity implements OnItemClickListener{
	
	private ListView lv;
	private String[] strs;
	private int deviceType;
	
	private CustomerProduct  currUFO = null;
	
	private FunDetail funDetail;
	private ProductFun productFun;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();

	}
	private void initView() {
		setContentView(R.layout.activity_select_brand);
		lv = (ListView) findViewById(R.id.lv_select_brand);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/ 
	}
	private void initData() {
		Intent intent = getIntent();
		deviceType = intent.getIntExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, 0);
		funDetail = (FunDetail) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL);
		productFun = (ProductFun) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN);
		Resources res =getResources();
		switch (deviceType) {
		case InfraredCodeLibraryConstant.DeviceType.AirCleaner:
			strs = res.getStringArray(R.array.air_cleaner_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.AirCondition:
			strs = res.getStringArray(R.array.air_condition_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Projector:
			strs = res.getStringArray(R.array.projector_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Fan:
			strs = res.getStringArray(R.array.fan_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.TvBox:
			strs = res.getStringArray(R.array.tv_box_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Tv:
			strs = res.getStringArray(R.array.tv_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.InternetTv:
			strs = res.getStringArray(R.array.internet_tv_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Dvd:
			strs = res.getStringArray(R.array.dvd_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Calorifier:
			strs = res.getStringArray(R.array.calorifier_brand);
			break;
		case InfraredCodeLibraryConstant.DeviceType.Camera:
			strs = res.getStringArray(R.array.camera_brand);
			break;

		default:
			strs = new String[1];
			strs[0] = "error";
			break;
		}
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs));
		lv.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent selectModelIntent = new Intent(this,SelectModelActivity.class);
		selectModelIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, deviceType);
		//将括号内英文品牌名发到下个界面
		selectModelIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.BRAND, strs[position].substring(strs[position].indexOf("(")+1, strs[position].indexOf(")")));
		selectModelIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL, funDetail);
		selectModelIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN, productFun);
		startActivity(selectModelIntent);
	}
}
