package com.jinxin.jxsmarthome.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.fragment.SettingsFragment;

public class SettingsActivity extends BaseActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_settings);

		getSupportActionBar().setTitle(getResources().getString(R.string.title_system_setting));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initFragment();
	}

	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.setting_fragment, new SettingsFragment()).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return false;
	}

}
