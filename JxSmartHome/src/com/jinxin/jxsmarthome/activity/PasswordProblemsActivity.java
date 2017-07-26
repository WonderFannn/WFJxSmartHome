package com.jinxin.jxsmarthome.activity;

import com.jinxin.jxsmarthome.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
/**
 * 密码修改问题回答界面
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class PasswordProblemsActivity extends BaseActionBarActivity implements OnClickListener{
	
	private EditText editText_account = null;
	private EditText editText_password = null;
	private Button button_login = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
//		getMenuInflater().inflate(R.menu.menu_main, menu);
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("设备控制");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			break;
		}
		return true;
	}
	
    /**
     * 初始化
     */
    private void initView(){
    	this.setContentView(R.layout.login_layout);
    	this.editText_account = (EditText)this.findViewById(R.id.editText_account);
    	this.editText_password = (EditText)this.findViewById(R.id.editText_password);
    	this.button_login = (Button)this.findViewById(R.id.button_login);
    	this.button_login.setOnClickListener(this);
    }
	@Override
	public void uiHandlerData(Message msg) {

	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_login:
			Intent _intent = new Intent(PasswordProblemsActivity.this,MainActivity.class);
			startActivity(_intent);
			this.finish();
			break;
		}
	}
	
}
