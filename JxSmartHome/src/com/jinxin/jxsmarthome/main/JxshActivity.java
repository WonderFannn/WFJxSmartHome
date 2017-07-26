package com.jinxin.jxsmarthome.main;



import java.util.List;

import xgzx.VeinUnlock.VeinLogin;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActivity;
import com.jinxin.jxsmarthome.activity.LoginActivity;
import com.jinxin.jxsmarthome.activity.SelectUserActivity;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;
/**
 * 入口类
 * @author JackeyZhang
 * @company 金鑫智慧
 */

public class JxshActivity extends BaseActivity implements OnClickListener{
	
	private ImageView iv_image;

	private int delay_second = 3000;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
		initData();
        
//        setContentView(R.layout.main);
//        Intent _itent = new Intent(JxshActivity.this,LoginActivity.class);
//        startActivity(_itent);
//        this.finish();
//        CommonDeviceControlByServerTask task = new CommonDeviceControlByServerTask(JxshActivity.this);
//        task.addListener(new ITaskListener<ITask>() {
//
//			@Override
//			public void onStarted(ITask task, Object arg) {
//				// TODO Auto-generated method stub
//				JxshApp.instance.showLoading(JxshActivity.this);
//				Logger.error("--->", "onStarted");
//			}
//
//			@Override
//			public void onCanceled(ITask task, Object arg) {
//				// TODO Auto-generated method stub
//				JxshApp.instance.closeLoading();
//				Logger.error("--->", "onCanceled");
//			}
//
//			@Override
//			public void onFail(ITask task, Object[] arg) {
//				// TODO Auto-generated method stub
//				JxshApp.instance.closeLoading();
//				if(arg != null && arg.length > 0){
//					JxshApp.instance.showToast(JxshActivity.this, (String)arg[0]);
//				}
//				Logger.error("--->", "onFail");
//
//			}
//
//			@Override
//			public void onSuccess(ITask task, Object[] arg) {
//				// TODO Auto-generated method stub
//				JxshApp.instance.closeLoading();
//				if(arg != null && arg.length > 0){
//					JxshApp.instance.showToast(JxshActivity.this, (String)arg[0]);
//				}
//				Logger.error("--->", "onSuccess");
//
//			}
//		});
//        task.start();
    }
    
	/**
	 * 初始化
	 */
	private void initView() {
		this.setView(R.layout.welcome_layout);
		this.iv_image = (ImageView) findViewById(R.id.welcome_icon);
		this.iv_image.setOnClickListener(this);
	}
	
	/**
	 * 跳转
	 */
	private void redirectTo() {
		if (JxshApp.isVeinOpen(CommUtil.getCurrentLoginAccount())) {
			if(JxshApp.getHistoryUserList().size() == 1) {//TODO:暂定
				if(!JxshApp.setVeinUser(CommUtil.getCurrentLoginAccount())) {
					Toast.makeText(this, getString(R.string.veinLoadFail), Toast.LENGTH_SHORT).show();
					startActivity(new Intent(JxshActivity.this,LoginActivity.class));
				} else {
					startActivity(new Intent(JxshActivity.this,VeinLogin.class));
				}
			} else {
				startActivity(new Intent(JxshActivity.this,SelectUserActivity.class));
			}
		} else {
			startActivity(new Intent(JxshActivity.this,LoginActivity.class));
		}
		finish();
	}
	
	/**
	 * 初始化显示数据(在initView之后)
	 */
	private void initData() {
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showMainActivity();
		JPushInterface.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
	
	private void showMainActivity() {
		mUIHander.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				redirectTo();
			}
		}, delay_second);
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			delay_second = 0;
//			showMainActivity();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.welcome_icon:
			mUIHander.sendEmptyMessage(0);
			break;
		}
	}
}
