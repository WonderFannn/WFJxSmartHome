package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.b;

import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.infrared.model.InfraredCodeLibraryUtil;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;

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
	private FunDetail funDetail;
	private ProductFun productFun;
	
	private InfraredCodeLibraryUtil mInfraredCodeLibraryUtil;
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
		funDetail = (FunDetail) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL);
		productFun = (ProductFun) getIntent().getSerializableExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN);
		Log.d("wangfan", brand);
		mInfraredCodeLibraryUtil = new InfraredCodeLibraryUtil(this);
		List<String> list = mInfraredCodeLibraryUtil.getModelByDeviceTypeAndBrand(deviceType, brand);
		list.add(0, "智能匹配");
		strs = list.toArray(new String[list.size()]);
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs));
		lv.setOnItemClickListener(this);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, MatchDeviceActivity.class);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, deviceType);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.BRAND, brand);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.MODEL, strs[position]);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL, funDetail);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN, productFun);
		startActivity(intent);
	}
}
