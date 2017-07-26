package com.jinxin.jxsmarthome.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.fragment.RemoteAirconditionLearnFragment;
import com.jinxin.jxsmarthome.fragment.RemoteTVLearnFragment;

public class CustomerRemoteLearnActivity extends BaseActionBarActivity {
	private CustomerBrands cBarnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_control);
		cBarnd = (CustomerBrands) getIntent().getSerializableExtra(RemoteBrandsTypeActivity.BRAND);
		if (cBarnd != null) {
			Fragment fragment = null;
			Bundle bundle = new Bundle();
			bundle.putSerializable(RemoteBrandsTypeActivity.BRAND, cBarnd);
			if (cBarnd.getDeviceId() == 1) {
				//空调
				fragment = new RemoteAirconditionLearnFragment();
			}else if(cBarnd.getDeviceId() == 2){
				//电视
				fragment = new RemoteTVLearnFragment();
			}
			if (fragment != null) {
				fragment.setArguments(bundle);
				addFragment(fragment, false);
			}
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
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
