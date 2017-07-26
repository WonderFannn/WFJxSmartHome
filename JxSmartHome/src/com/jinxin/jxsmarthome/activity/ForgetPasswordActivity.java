package com.jinxin.jxsmarthome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jinxin.datan.net.command.ForgetPasswordTask;
import com.jinxin.datan.net.command.GetSafeQuestionTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.main.JxshApp;

/**重置密码界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ForgetPasswordActivity extends BaseActionBarActivity implements OnClickListener{
	
	private Context context = null;
	private GetSafeQuestionTask gsqTask = null;
	private EditText etAnswer_1 = null;
	private EditText etAnswer_2 = null;
	private TextView tvQuestion_1 = null;
	private TextView tvQuestion_2 = null;
	private Button btnSure = null;
	private EditText etPassword = null;
	
	private String _account = "";
	private String _answer1 = "";
	private String _answer2 = "";
	private String _password = "";
	private SysUser user = null;
	
	
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
		getSupportActionBar().setTitle("重置密码");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return true;
	}

	/**
	 * 初始化
	 */
	private void initView() {
		this.context = this;
		this.setContentView(R.layout.password_forget_layout);
		this.tvQuestion_1 = (TextView) findViewById(R.id.tv_secret_question1);
		this.tvQuestion_2 = (TextView) findViewById(R.id.tv_secret_question2);
		this.etAnswer_1 = (EditText) findViewById(R.id.et_secretkey_question1);
		this.etAnswer_2 = (EditText) findViewById(R.id.et_secretkey_question2);
		this.etPassword = (EditText) findViewById(R.id.et_new_secretkey);
		this.btnSure = (Button) findViewById(R.id.btn_forget_sure);
		this.btnSure.setOnClickListener(this);
	}
	
	/**
	 * 初始化显示数据(在initView之后)
	 */
	private void initData() {
		Intent _intent = getIntent();
		this._account = _intent.getStringExtra("account");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		gsqTask = new GetSafeQuestionTask(context,_account);
		gsqTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, null);
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
				JxshApp.closeLoading();
				if(arg != null && arg.length > 0){
					user = (SysUser) arg[0];
					mUIHander.sendEmptyMessage(0);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		gsqTask.start();
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		switch(msg.what){
		case 0:
			if (user != null) {
				tvQuestion_1.setText(user.getQuestion1());	
				tvQuestion_2.setText(user.getQuestion2());
			}
		break;
		case 1:
			this.finish();
		break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			this.onBackPressed();
			break;
		case R.id.btn_forget_sure:
			this._answer1 = etAnswer_1.getText().toString();
			this._answer2 = etAnswer_2.getText().toString();
			this._password = etPassword.getText().toString();
			if (TextUtils.isEmpty(this._answer1)) {
				this.etAnswer_1.requestFocus();
				JxshApp.showToast(context,getString(R.string.question_answer_not_null));
			}else if(TextUtils.isEmpty(this._answer2)){
				this.etAnswer_2.requestFocus();
				JxshApp.showToast(context,getString(R.string.question_answer_not_null));
			}else if(TextUtils.isEmpty(this._password)){
				this.etPassword.requestFocus();
				JxshApp.showToast(context,getString(R.string.new_secret_not_null));
			}else{
				ForgetPasswordTask fpTask = new ForgetPasswordTask(context,_account
													,_password,_answer1,_answer2);
				fpTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						if(arg != null && arg.length > 0){
							JxshApp.showToast(context, getString(R.string.reset_password_success));
							BroadcastManager.sendBroadcast(BroadcastManager.ACTION_EXIT_MESSAGE_RELOGIN, null);
//							mUIHander.sendEmptyMessage(1);
						}
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
					}
				});
				fpTask.start();
			}
			break;
			
		}
	}
	
}
