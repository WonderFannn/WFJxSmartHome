package com.jinxin.jxsmarthome.activity;

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
import android.widget.TextView;

import com.jinxin.datan.net.command.ChangeNickyNameTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.SysUserDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.main.JxshApp;

public class ChangNickynameAcitvity extends BaseActionBarActivity implements OnClickListener{
	
	private Context context;
	private EditText nickyName = null;
	private Button btnSure = null;
	private TextView oldNickyName = null;
	private SysUserDaoImpl suImpl;
	private List<SysUser> users;
	private String nickyname;
	
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
		getSupportActionBar().setTitle("修改昵称");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ChangNickynameAcitvity.this.finish();
			break;
		}
		return true;
	}

	private void initView() {
		this.context = this;
		this.setContentView(R.layout.change_nickyname_layout);
		this.nickyName = (EditText) findViewById(R.id.nickyname_et);
		this.oldNickyName = (TextView) findViewById(R.id.tv_nickyname);
		this.btnSure = (Button) findViewById(R.id.btn_change_sure);
		this.nickyName.setOnClickListener(this);
		this.btnSure.setOnClickListener(this);
	}
	
	private void initData() {
		suImpl= new SysUserDaoImpl(context);
		users = suImpl.find();
		if (users!=null&&users.size()>0) {
			oldNickyName.setText(users.get(0).getNickyName());
		}
	}
	
	@Override
	public void uiHandlerData(Message msg) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			this.onBackPressed();
			break;
		case R.id.btn_change_sure:
			nickyname = nickyName.getText().toString();
			if(TextUtils.isEmpty(nickyname) && nickyname.length()>=4){
				this.nickyName.setFocusable(true);
				JxshApp.showToast(context, getString(R.string.set_nickyname_string_error));
			}else{
				ChangeNickyNameTask cnnTask = new ChangeNickyNameTask(context,nickyname);
				cnnTask.addListener(new ITaskListener<ITask>() {

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
						JxshApp.showToast(context, getString(R.string.set_nickyname_success));
						if (users!=null&&users.size()>0) {
							users.get(0).setNickyName(nickyname);
							suImpl.update(users.get(0));
						}
						finish();
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
						
					}
				});
				cnnTask.start();
			}
			break;
		}
	}
}
