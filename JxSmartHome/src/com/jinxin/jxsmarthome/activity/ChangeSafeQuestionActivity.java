package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.jinxin.datan.net.command.ChangeSafeQuestionTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.popupwindow.ListPopupWindow;

/**
 * 修改密保问题界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ChangeSafeQuestionActivity extends BaseActionBarActivity implements OnClickListener{
	
	private Context context;
	private Button question1Btn = null;
	private Button question2Btn = null;
	private EditText etAnswer1 = null;
	private EditText etAnswer2 = null;
	private Button _btnSure = null;
	
	private String _question1 = null;
	private String _question2 = null;
	private String _answer1 = null;
	private String _answer2 = null;
	
	private List<String> strQuestion = null;
	
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
		getSupportActionBar().setTitle("修改密保问题");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ChangeSafeQuestionActivity.this.finish();
			break;
		}
		return true;
	}

	/**
	 * 初始化
	 */
	private void initView() {
		this.context = this;
		this.setContentView(R.layout.change_safe_questions_layout);
		
		this.question1Btn = (Button) findViewById(R.id.question1_btn);
		this.question2Btn = (Button) findViewById(R.id.question2_btn);
		this.etAnswer1 = (EditText) findViewById(R.id.et_safe_question1);
		this.etAnswer2 = (EditText) findViewById(R.id.et_safe_question2);
		this._btnSure = (Button) findViewById(R.id.btn_change_sure);
		this.question1Btn.setOnClickListener(this);
		this.question2Btn.setOnClickListener(this);
		this._btnSure.setOnClickListener(this);
		
	}
	
	/**
	 * 初始化显示数据(在initView之后)
	 */
	private void initData() {
		this.strQuestion = new ArrayList<String>();
		strQuestion.add("你父亲的生日？");
		strQuestion.add("你母亲的生日？");
		strQuestion.add("你的生日？");
		strQuestion.add("你的小学名称？");
		strQuestion.add("你的职业？");
	}

	@Override
	public void uiHandlerData(Message msg) {
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_back:
			this.onBackPressed();
			break;
		case R.id.btn_change_sure:
			this._answer1 = etAnswer1.getText().toString();
			this._answer2 = etAnswer2.getText().toString();
			this._question1 = question1Btn.getText().toString();
			this._question2 = question2Btn.getText().toString();
			if (TextUtils.isEmpty(this._answer1)) {
				this.etAnswer1.requestFocus();
				JxshApp.showToast(context,getString(R.string.question_answer_not_null));
			}else if(TextUtils.isEmpty(this._answer2)){
				this.etAnswer2.requestFocus();
				JxshApp.showToast(context,getString(R.string.question_answer_not_null));
			}else if(TextUtils.isEmpty(this._question1)||
					this._question1.equals("请选择密保问题")){
				this.question1Btn.requestFocus();
				JxshApp.showToast(context,getString(R.string.question_not_null));
			}else if(TextUtils.isEmpty(this._question2)||
					this._question1.equals("请选择密保问题")){
				this.question2Btn.requestFocus();
				JxshApp.showToast(context,getString(R.string.question_not_null));
			}else{
				//修改密保问题请求
				ChangeSafeQuestionTask csqTask = new ChangeSafeQuestionTask(context, _question1, _question2, _answer1, _answer2);
				csqTask.addListener(new ITaskListener<ITask>() {

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
						JxshApp.showToast(context,
								CommDefines.getSkinManager().
								string(R.string.safe_question_change_success));
						finish();
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
						
					}
				});
				csqTask.start();
			}
			break;
		case R.id.question1_btn:
			if (this.strQuestion != null && this.strQuestion.size() > 0) {
				final ListPopupWindow listPopupWindow = new ListPopupWindow(
						ChangeSafeQuestionActivity.this, strQuestion, question1Btn,
						question1Btn.getWidth(), strQuestion.get(0));
				listPopupWindow
						.setOnDismissListener(new PopupWindow.OnDismissListener() {
							@Override
							public void onDismiss() {
								question1Btn.setSelected(false);
								if (!TextUtils.isEmpty(listPopupWindow
										.getSelectStr()))
									if (listPopupWindow.getSelectIndex() != -1) {
										question1Btn.setText(listPopupWindow
												.getSelectStr());
									}
							}
						});
				listPopupWindow.show();
			}
			break;
		case R.id.question2_btn:
			if (this.strQuestion != null && this.strQuestion.size() > 0) {
				final ListPopupWindow listPopupWindow = new ListPopupWindow(
						ChangeSafeQuestionActivity.this, strQuestion, question2Btn,
						question2Btn.getWidth(), strQuestion.get(0));
				listPopupWindow
						.setOnDismissListener(new PopupWindow.OnDismissListener() {
							@Override
							public void onDismiss() {
								question2Btn.setSelected(false);
								if (!TextUtils.isEmpty(listPopupWindow
										.getSelectStr()))
									if (listPopupWindow.getSelectIndex() != -1) {
										question2Btn.setText(listPopupWindow
												.getSelectStr());
									}
							}
						});
				listPopupWindow.show();
			}
			break;
		}
	}

}
