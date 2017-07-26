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

import com.jinxin.datan.net.command.ConfirmSafeQuestionTask;
import com.jinxin.datan.net.command.GetSafeQuestionTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

/**密码修改问题回答界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class SecretSafeQuestionActivity extends BaseActionBarActivity implements OnClickListener{
	
	private Context context = null;
	private TextView tvQuestion_1 = null;
	private TextView tvQuestion_2 = null;
	private EditText etAnswer_1 = null;
	private EditText etAnswer_2 = null;
	private Button btnSure = null;
	
	private String _answer1 = null;
	private String _answer2 = null;
	private SysUser user = null;
	private Intent intent = null;
	private String toWhichAct = null;
	
	private GetSafeQuestionTask gsqTask = null;
	
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
		getSupportActionBar().setTitle("密保问题");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			SecretSafeQuestionActivity.this.finish();
			break;
		}
		return true;
	}
	
	/**
	 * 初始化
	 */
	private void initView() {
		this.context = this;
		this.setContentView(R.layout.find_secretkey_layout);
		this.tvQuestion_1 = (TextView) findViewById(R.id.tv_secret_question1);
		this.tvQuestion_2 = (TextView) findViewById(R.id.tv_secret_question2);
		this.etAnswer_1 = (EditText) findViewById(R.id.et_secretkey_question1);
		this.etAnswer_2 = (EditText) findViewById(R.id.et_secretkey_question2);
		this.btnSure = (Button) findViewById(R.id.btn_find_sure);
		btnSure.setOnClickListener(this);
	}
	
	/**
	 * 初始化显示数据(在initView之后)
	 */
	private void initData() {
		intent = getIntent();
		toWhichAct = intent.getStringExtra(SafeCenteractivity.SAFE_KEY);
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		gsqTask = new GetSafeQuestionTask(context,null);
		gsqTask.addListener(new ITaskListener<ITask>() {

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
				if(arg != null && arg.length > 0){
					user = (SysUser) arg[0];
					if (TextUtils.isEmpty(user.getQuestion1()) || TextUtils.isEmpty(user.getQuestion2())) {
						mUIHander.sendEmptyMessage(1);
					}else{
						mUIHander.sendEmptyMessage(0);
					}
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
					if (!TextUtils.isEmpty(user.getQuestion1()) &&
							!TextUtils.isEmpty(user.getQuestion2())) {
						tvQuestion_1.setText(user.getQuestion1());	
						tvQuestion_2.setText(user.getQuestion2());
					}else{
						tvQuestion_1.setText("");	
						tvQuestion_2.setText(user.getQuestion2());
						JxshApp.showToast(context, CommDefines.getSkinManager().string(
								R.string.safe_question_is_null));
						this.finish();
					}
				}
			break;
			case 1:
				JxshApp.showToast(context,getString(R.string.safe_question_is_null));
				Intent _intent = new Intent(SecretSafeQuestionActivity.this, SecretKeyChangeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("answer1", "");
				bundle.putString("answer2", "");
				_intent.putExtras(bundle);
				startActivity(_intent);
				this.finish();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		this._answer1 = etAnswer_1.getText().toString();
		this._answer2 = etAnswer_2.getText().toString();
		switch (v.getId()) {
		case R.id.button_back:
			this.onBackPressed();
			break;
		case R.id.btn_find_sure:
			if (TextUtils.isEmpty(this._answer1)) {
				this.etAnswer_1.requestFocus();
				JxshApp.showToast(context,getString(R.string.question_answer_not_null));
			}else if(TextUtils.isEmpty(this._answer2)){
				this.etAnswer_2.requestFocus();
				JxshApp.showToast(context,getString(R.string.question_answer_not_null));
			}else{
				//验证密保问题
				ConfirmSafeQuestionTask csqTask = new ConfirmSafeQuestionTask(context, _answer1, _answer2);
				csqTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						JxshApp.showToast(context, CommDefines.getSkinManager().
								string(R.string.safe_question_conmfir_error));
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						Intent _intent = new Intent();
						if (!TextUtils.isEmpty(toWhichAct)) {
							if (toWhichAct.equals(SafeCenteractivity.TO_NICKYNAME)) {
								_intent.setClass(context,ChangNickynameAcitvity.class);
								startActivity(_intent);
							}else if(toWhichAct.equals(SafeCenteractivity.TO_PASSWORD)){
								_intent.setClass(context, SecretKeyChangeActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("answer1", _answer1);
								bundle.putString("answer2", _answer2);
								_intent.putExtras(bundle);
								startActivity(_intent);
							}else if(toWhichAct.equals(SafeCenteractivity.TO_QUESTION)){
								_intent.setClass(context,ChangeSafeQuestionActivity.class);
								startActivity(_intent);
							}
						}
						finish();
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
						
					}
				});
				csqTask.start();
			}
			break;

		default:
			break;
		}
	}
	
}
