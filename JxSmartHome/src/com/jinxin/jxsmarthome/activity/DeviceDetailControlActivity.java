package com.jinxin.jxsmarthome.activity;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.fragment.CurtainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Menu;

public class DeviceDetailControlActivity extends BaseActionBarActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_detail_control);
		
		loadFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_scene, menu);
		
		//返回按钮
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("设备控制");
		
		return true;
	}

	@Override
	public void uiHandlerData(Message msg) {
		
	}
	
	private void loadFragment() {
		Bundle data = getIntent().getExtras();
		FunDetail funDetail = (FunDetail) data.getSerializable("funDetail");
		ProductFun productFun = (ProductFun) data.getSerializable("productFun");
		if(funDetail != null) {
			Fragment fragment = null;
			if (funDetail.getFunType().equals(
					ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN)) {
				fragment = new CurtainFragment();
				if (productFun != null) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("productFun",productFun);
					bundle.putSerializable("funDetail", funDetail);
					fragment.setArguments(bundle);
				}
				getSupportFragmentManager().beginTransaction()
					.add(R.id.device_detail_control, fragment)
					.addToBackStack(null)
					.commit();
			} else if (funDetail.getFunType().equals(
					ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT)) {
			} else if (funDetail.getFunType().equals(
					ProductConstants.FUN_TYPE_POWER_AMPLIFIER)) {
				Intent intent = new Intent(this, MusicActivity.class);
				startActivity(intent);
			} else if (funDetail.getFunType().equals(
					ProductConstants.FUN_TYPE_HUMAN_CAPTURE)) {
			} else if (funDetail.getFunType().equals(
					ProductConstants.FUN_TYPE_COLOR_LIGHT)) {
			}
		}
	}

}
