package com.jinxin.jxsmarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.fragment.RemoteAirconditionControlFragment;
import com.jinxin.jxsmarthome.fragment.RemoteTVBrandControlFragment;
import com.jinxin.jxsmarthome.util.Logger;

public class CustomerRemoteControlActivity extends BaseActionBarActivity {
	private CustomerBrands cBarnd;
	private int type;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Logger.debug(null, "onCreateOptionsMenu");
		// 溢出菜单
		if (type == 2) {
			getMenuInflater().inflate(R.menu.menu_remote_edit, menu);
		}
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.debug(null, "onCreate");
		setContentView(R.layout.activity_remote_control);
		cBarnd = (CustomerBrands) getIntent().getSerializableExtra(RemoteBrandsTypeActivity.BRAND);
		if (cBarnd != null) {
			Fragment fragment = null;
			Bundle bundle = new Bundle();
			bundle.putSerializable(RemoteBrandsTypeActivity.BRAND, cBarnd);
			if (cBarnd.getDeviceId() == 1) {
				//空调
				fragment = new RemoteAirconditionControlFragment();
			}else if(cBarnd.getDeviceId() == 2){
				//电视
				fragment = new RemoteTVBrandControlFragment();
			}
			if (fragment != null) {
				fragment.setArguments(bundle);
				addFragment(fragment, false);
			}
			
			type = cBarnd.getType();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.edit_button:
			Intent intent = new Intent(CustomerRemoteControlActivity.this,CustomerRemoteLearnActivity.class);
			intent.putExtra(RemoteBrandsTypeActivity.BRAND, cBarnd);
			startActivity(intent);
			break;
		}
		return false;
	}

	/**
	 * 添加fragment
	 * @param fragment
	 * @param addToStack
	 */
	private void addFragment(Fragment fragment, boolean addToStack) {
		if (fragment != null && addToStack) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.remote_control_fragment, fragment)
					.addToBackStack(null).commit();
		} else if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.remote_control_fragment, fragment).commit();
		}
	}

}
