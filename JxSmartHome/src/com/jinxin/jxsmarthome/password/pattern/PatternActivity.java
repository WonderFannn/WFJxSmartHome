package com.jinxin.jxsmarthome.password.pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.BaseActivity;
import com.jinxin.jxsmarthome.activity.BaseFragmentActivity;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.jxsmarthome.util.MD5Util;
import com.jinxin.offline.util.NetTool;
import com.jinxin.record.SharedDB;

/**
 * launchMode:singleTask
 * @author HuangBinHua
 * @company 金鑫智慧
 */
public class PatternActivity extends BaseActionBarActivity implements OnClickListener {

	private NinePointLineView mNinePointLineView;
	private TextView mForgetPasswordView;
	private View mTopLineView;
	private int action = 0;
	public static final int ACTION_SET = 0;
	public static final int ACTION_VERIFY = 1;

	private NineLockListener mNineLockListener = new NineLockListener() {

		@Override
		public void showMessage(String msg) {
			Toast.makeText(PatternActivity.this, msg, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void finish() {
			BaseActivity.setActiveTime();
			PatternActivity.this.finish();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_pattern);
		initView();
		initData();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		BaseActivity.setActiveTime();
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		BaseActivity.setActiveTime();
		return super.dispatchTouchEvent(ev);
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
		return true;
	}

	public void initData() {
		WindowManager localWindowManager = (WindowManager) getSystemService("window");
		int width = localWindowManager.getDefaultDisplay().getWidth();
		int height = localWindowManager.getDefaultDisplay().getHeight();
		
		action = getIntent().getIntExtra("action", ACTION_SET);
		mNinePointLineView.setWindows(width, height, mNineLockListener, action);
		if(ACTION_SET == action) {
			getSupportActionBar().show();
			mForgetPasswordView.setVisibility(View.GONE);
			mTopLineView.setVisibility(View.VISIBLE);
		} else {
			getSupportActionBar().hide();
			mForgetPasswordView.setVisibility(View.VISIBLE);
			mTopLineView.setVisibility(View.GONE);
		}
	}

	private void initView() {
		getSupportActionBar().setTitle(getResources().getString(R.string.title_set_pattern_password));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mNinePointLineView = (NinePointLineView) findViewById(R.id.view);
		mForgetPasswordView = (TextView) findViewById(R.id.p_forget_password);
		mTopLineView = (View) findViewById(R.id.topLine);
		mForgetPasswordView.setOnClickListener(this);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static Intent getVerifyIntent(Context context) {
		Intent intent = new Intent(context,PatternActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("action", PatternActivity.ACTION_VERIFY);
		return intent;
	}
	
	public static Intent getIntent(Context context) {
		Intent intent = new Intent(context,PatternActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("action", PatternActivity.ACTION_SET);
		return intent;
	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_back:
			super.onBackPressed();
			break;
		case R.id.p_forget_password:
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View dialogView = inflater.inflate(R.layout.custom_dialog_pwd_unlock, null);
			final EditText pwdText = (EditText) dialogView.findViewById(R.id.pwd_confirm_text);
			Button btnSure = (Button) dialogView.findViewById(R.id.btn_sure);
			Button btnCancle = (Button) dialogView.findViewById(R.id.btn_cancel);
			final Dialog changDialog = new Dialog(PatternActivity.this);
			changDialog.setContentView(dialogView);
			changDialog.setTitle(R.string.enter_login_secretkey);
			btnSure.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String secretkey = EncryptUtil.getOrdinaryPassword();
					if(!TextUtils.isEmpty(pwdText.getText().toString())){
						String pwd =pwdText.getText().toString();
						if (secretkey.equals(pwd)) {
							changDialog.dismiss();
							mNineLockListener.finish();
						}else{
							JxshApp.showToast(PatternActivity.this,CommDefines.getSkinManager().string(R.string.li_xian_mo_shi_mi_ma_error));
						}
					}
				}
			});
			btnCancle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					changDialog.dismiss();
				}
			});
			changDialog.show();
			break;

		default:
			break;
		}
	}

}
