package com.jinxin.jxsmarthome.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.fragment.SpeakerChooseFragment4ClounSetting;

public class SpeakerSettingsActivity extends BaseActionBarActivity {
	
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
		fm.beginTransaction()
				.replace(R.id.setting_fragment,
						new SpeakerChooseFragment4ClounSetting(),
						"setting_fragment").commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		SpeakerChooseFragment4ClounSetting fragment = 
				(SpeakerChooseFragment4ClounSetting) getSupportFragmentManager().findFragmentByTag("setting_fragment");
		Bundle data = new Bundle();
		data.putSerializable("speakerSetting", fragment.getCloudSetting());
		setResult(Activity.RESULT_OK, getIntent().putExtras(data));
		finish();
	}
	
}
