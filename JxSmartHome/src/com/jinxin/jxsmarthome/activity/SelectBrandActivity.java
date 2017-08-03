package com.jinxin.jxsmarthome.activity;

import java.util.List;

import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.infrared.model.InfraredCodeLibraryUtil;
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

	private InfraredCodeLibraryUtil mInfraredCodeLibraryUtil;

	
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
		
		mInfraredCodeLibraryUtil = new InfraredCodeLibraryUtil(this);
		List<String> brandList = mInfraredCodeLibraryUtil.getBrandtByDeviceType(deviceType);
		strs = new String[brandList.size()];
		strs = brandList.toArray(strs);
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs));
		lv.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent selectModelIntent = new Intent(this,SelectModelActivity.class);
		selectModelIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.DEVICE_TYPE, deviceType);
		//将括号内中文品牌名发到下个界面
		selectModelIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.BRAND, strs[position]);                                                                                                  
		selectModelIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL, funDetail);
		selectModelIntent.putExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN, productFun);
		startActivity(selectModelIntent);
	}
}
