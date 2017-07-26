package com.jinxin.jxsmarthome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

/**
 * 通过账号找回密码界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ConfirmAccountActivity extends BaseActionBarActivity implements OnClickListener{
	
	private Context context = null;
	private EditText et_account = null;
	private Button btnSure = null;
	private String _account = "";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initView() {
		this.context = this;
		this.setContentView(R.layout.confirm_account_layout);
		this.setTitle(CommDefines.getSkinManager().string(
				R.string.forget_secret));
		this.et_account = (EditText) findViewById(R.id.et_account);
		this.btnSure = (Button) findViewById(R.id.btn_forget_sure);
		this.btnSure.setOnClickListener(this);
		
		getSupportActionBar().setTitle(getResources().getString(R.string.forget_secret));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void initData() {
		
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

	@Override
	public void uiHandlerData(Message msg) {
		
	}

	@Override
	public void onClick(View v) {
		this._account = et_account.getText().toString();
		switch (v.getId()) {
		case R.id.button_back:
			this.onBackPressed();
			break;
		
		case R.id.btn_forget_sure:
			if (TextUtils.isEmpty(_account)) {
				et_account.requestFocus();
			}else{
				Intent intent = new Intent(context,ForgetPasswordActivity.class);
				intent.putExtra("account", _account);
				startActivity(intent);
			}
			break;
		}
	}

}
