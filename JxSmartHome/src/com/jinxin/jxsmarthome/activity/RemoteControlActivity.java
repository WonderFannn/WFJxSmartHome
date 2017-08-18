package com.jinxin.jxsmarthome.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.fragment.CustomControllerFragment;
import com.jinxin.jxsmarthome.fragment.CustomTVControllerFragment;
import com.jinxin.jxsmarthome.fragment.RemoteControlFragment;
import com.jinxin.jxsmarthome.util.AppUtil;

/**
 * 遥控控制界面
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class RemoteControlActivity extends BaseActionBarActivity implements
		OnItemSelectedListener, CustomTVControllerFragment.OnItemClickListener, OnClickListener {
	private String currentType;
	public static int areaId;
	private ImageView btn_addDevice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_control);
		getSupportActionBar().setTitle("遥控控制");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		btn_addDevice = (ImageView) findViewById(R.id.btn_add_device);
		btn_addDevice.setOnClickListener(this);
		initConrollers("1");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_control, menu);
		MenuItem spinerItem = menu.findItem(R.id.action_control_category);
		Spinner spinner = (Spinner) MenuItemCompat.getActionView(spinerItem).findViewById(R.id.spinner);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.spinner_item, 
				getResources().getStringArray(R.array.remote_control_category));
		adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.action_custom:
			showCustomTVController();
			break;
		}
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			initConrollers("1");
		} else if (position == 1) {
			initConrollers("2");
		} else if (position == 2) {
			initCustomConroller();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// do nothing...
	}

	public void initConrollers(String type) {
		currentType = type;
		Fragment fragment = new RemoteControlFragment();
		Bundle args = new Bundle();
		args.putString("type", type);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.remote_control_fragment, fragment).commit();
		
	}

	public void initCustomConroller() {
		currentType = "3";
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.remote_control_fragment,
						new CustomControllerFragment()).commit();
	}
	
	private void showCustomTVController() {
		if("1".equals(currentType)) {
			CustomTVControllerFragment tvFragment = CustomTVControllerFragment
					.newInstance(areaId, this, "tvFragment");
			if(getSupportFragmentManager().findFragmentByTag("tvFragment") == null) {
				getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_out, 
								R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_out)
						.add(R.id.remote_control_fragment, tvFragment, "tvFragment")
						.addToBackStack(null).commit();
			}
		}
	}

	@Override
	public void onItemClick(int position, Object item, String tag) {
		sendUFOCmd((ProductFun)item);
	}
	
	private void sendUFOCmd(ProductFun productFun) {
		FunDetail funDetail = AppUtil.getFunDetailByFunType(this,
				ProductConstants.FUN_TYPE_UFO1);
		if (productFun == null || funDetail == null) {
			return;
		}

		productFun.setFunType(ProductConstants.FUN_TYPE_UFO1);

		if (NetworkModeSwitcher.useOfflineMode(this)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(StaticConstant.PARAM_INDEX, productFun.getFunUnit());
			OfflineCmdManager.generateCmdAndSend(this, productFun,
					funDetail, params);
		} else {

			List<byte[]> cmds = CommonMethod.productFunToCMD(this,
					productFun, funDetail, null);

			if (cmds == null || cmds.size() < 1) {
				return;
			}

			new OnlineCmdSenderLong(this, cmds).send();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btn_addDevice) {
			Intent intent = new Intent(this,SelectUFOActivity.class);
			startActivity(intent);
		}
	}

}
