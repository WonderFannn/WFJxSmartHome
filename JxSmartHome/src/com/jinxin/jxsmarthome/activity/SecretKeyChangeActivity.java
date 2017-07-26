package com.jinxin.jxsmarthome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.jinxin.datan.net.command.ChangeSecretKeyTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

/**密码修改界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class SecretKeyChangeActivity extends BaseActionBarActivity implements OnClickListener{
	
	private Context context;
	private Intent _intent = null;
	private EditText et_oldKey = null;
	private EditText et_newKey = null;
	private EditText et_confirmKey = null;
	private Button _btnSure = null;
	
	private String _answer1 = null;
	private String _answer2 = null;
	
	private ChangeSecretKeyTask _cskTask = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
//		getMenuInflater().inflate(R.menu.menu_main, menu);
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("修改密码");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			SecretKeyChangeActivity.this.finish();
			break;
		}
		return true;
	}

	/**
	 * 初始化
	 */
	private void initView() {
		this.context = this;
		this.setContentView(R.layout.change_secretkey_layout);
		this.et_oldKey = (EditText) findViewById(R.id.et_secretkey_old);
		this.et_newKey = (EditText) findViewById(R.id.et_secretkey_new);
		this.et_confirmKey = (EditText) findViewById(R.id.et_secretkey_confirm);
		this._btnSure = (Button) findViewById(R.id.btn_change_sure);
		this._btnSure.setOnClickListener(this);
	}
	
	/**
	 * 初始化显示数据(在initView之后)
	 */
	private void initData() {
		_intent = getIntent();
		Bundle bundle = new Bundle();
		bundle = _intent.getExtras();
		_answer1 = bundle.getString("answer1");
		_answer2 = bundle.getString("answer2");
	}

	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0://修改成功，跳转到登录界面
			JxshApp.showToast(
					SecretKeyChangeActivity.this,
					CommDefines.getSkinManager().string(
							R.string.change_key_success));
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		case 1:
			JxshApp.showToast(
					SecretKeyChangeActivity.this,
					CommDefines.getSkinManager().string(
							R.string.safe_question_error));
			Intent _intent2 = new Intent(context,SecretSafeQuestionActivity.class);
			_intent2.putExtra(SafeCenteractivity.SAFE_KEY, SafeCenteractivity.TO_PASSWORD);
			startActivity(_intent2);
			finish();
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			this.onBackPressed();
			break;
		case R.id.btn_change_sure:
			String _oldPwd = et_oldKey.getText().toString();
			String _newPwd = et_newKey.getText().toString();
			String _confirmPwd = et_confirmKey.getText().toString();
			if (_oldPwd.length()<6 || _oldPwd.length()>16) {
				// 旧密码位数错误
				this.et_oldKey.requestFocus();
				this.et_oldKey
						.setError(getString(R.string.string_input_password_err));
			}else if (_newPwd.length()<6 || _newPwd.length()>16) {
				//新密码输入有误
				this.et_newKey.requestFocus();
				this.et_newKey.setError(getString(R.string.string_input_password_err));
			}else if (_confirmPwd.length()<6 || _confirmPwd.length()>16) {
				//确认密码输入有误
				this.et_confirmKey.requestFocus();
				this.et_confirmKey.setError(getString(R.string.string_input_password_err));
			}else if (_newPwd.length()!=_confirmPwd.length() || 
					!_newPwd.equals(_confirmPwd)) {
				this.et_confirmKey.requestFocus();
				this.et_confirmKey.setError(getString(R.string.et_pwd_diff));
			}else{
				//确认提交
				_cskTask = new ChangeSecretKeyTask(context, _oldPwd, _newPwd, _answer1, _answer2);
				_cskTask.addListener(new ITaskListener<ITask>(){

					@Override
					public void onStarted(ITask task, Object arg) {
						
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
						JxshApp.closeLoading();
						
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						JxshApp.closeLoading();
						mUIHander.sendEmptyMessage(1);
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						//修改成功
						mUIHander.sendEmptyMessage(0);
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
						
					}
					
				});
				_cskTask.start();
			}
			break;
		}
	}

}
