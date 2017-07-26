package com.jinxin.jxsmarthome.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.main.JxshApp;

public class SelectUFOActivity extends BaseActionBarActivity implements OnItemClickListener {
	private ListView lv;
	private String[] strs;
	
	private List<CustomerProduct> cpList = null;
	private CustomerProduct  currUFO = null;
	private CustomerProductDaoImpl cpdImpl = null;

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
		
		cpdImpl = new CustomerProductDaoImpl(this);
		cpList = cpdImpl.find(null, "code=? or code=?", new String[]{"005","009"}, null, null, null, null);
		if (cpList != null && cpList.size() > 0) {
			currUFO = cpList.get(0);
			
			strs = new String[cpList.size()];
			for (int i = 0; i < cpList.size(); i++) {
				strs[i] = cpList.get(i).getWhId();
			}
			
			cpList.toArray();
			lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs));
		}
		
		lv.setOnItemClickListener(this);
		if (cpList.size() == 1) {
			Intent intent = new Intent(this, AddRemoteDeviceActivity.class);
			intent.putExtra(InfraredCodeLibraryConstant.IntentTag.UFO, currUFO);
			startActivity(intent);
			finish();
		}else if(cpList.size() == 0) {
			JxshApp.showToast(this, "无红外设备,请先购买!");
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		currUFO = cpList.get(position);
		Intent intent = new Intent(this, AddRemoteDeviceActivity.class);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.UFO, currUFO);

		startActivity(intent);
	}

	
	
}
