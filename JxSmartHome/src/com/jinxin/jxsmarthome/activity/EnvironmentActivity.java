package com.jinxin.jxsmarthome.activity;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.fragment.EnvironmentFragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

public class EnvironmentActivity extends BaseActionBarActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	private void initView() {
		setContentView(R.layout.activity_environment);
		
		getSupportActionBar().setTitle("环境监测");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Fragment environmentFragment = new EnvironmentFragment();
		
		getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_layout, environmentFragment).commit();
	}

	@Override
	public void uiHandlerData(Message msg) {
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
