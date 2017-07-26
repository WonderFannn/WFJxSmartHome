package com.jinxin.jxsmarthome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.fragment.SplashClockFragment;
import com.jinxin.jxsmarthome.main.JxshApp;

public class SplashActivity extends FragmentActivity {

	public static String ACTION_FINISHED_LOAD = "load_data_finished";
	public static String ACTION_LOAD_FAIL = "load_fail";
	BroadcastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_FINISHED_LOAD);
		filter.addAction(ACTION_LOAD_FAIL);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(ACTION_FINISHED_LOAD)) {
					finish();
				} else if(intent.getAction().equals(ACTION_LOAD_FAIL)) {
					JxshApp.showToast(getApplicationContext(),getString(R.string.mode_contorl_fail_net));
					finish();
				}
			}
			
		};
		registerReceiver(receiver, filter);
	}

	private void initView() {
		setContentView(R.layout.activity_splash);

		initFragment();
	}

	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.splash_fragment, new SplashClockFragment()).commit();
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
//			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}
