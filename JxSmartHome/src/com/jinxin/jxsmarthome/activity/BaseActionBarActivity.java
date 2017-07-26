package com.jinxin.jxsmarthome.activity;

import java.lang.ref.WeakReference;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.password.pattern.PatternActivity;
import com.jinxin.jxsmarthome.password.pattern.UIMonitor;
import com.jinxin.jxsmarthome.ui.popupwindow.ShowSuggestWindows;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.offline.util.NetTool;
import com.jinxin.offline.util.OfflineToOnline;
import com.jinxin.record.SharedDB;
import com.umeng.analytics.MobclickAgent;

/**
 * 带actionbar的activity基类,主要保留原来的BaseActivity的功能{@link BaseActivity}
 * @author TangLong
 * @company 金鑫智慧
 */
public abstract class BaseActionBarActivity extends ActionBarActivity {
	protected BasicHandler mUIHander;		// 主界面的视图handler
	protected Handler mDownLoadHandler;		// 子线程和主线程交互的handler
	private MessageBox _mBox;				// 在线提示框
	private MessageBox mb;					// 离线提示框
	public ShowSuggestWindows suggestWindow;
	
	private BroadcastReceiver mBroadcastReceiver;
	
	/**
	 * 退出的广播事件处理
	 */
	private BroadcastReceiver mExitBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastManager.ACTION_EXIT_MESSAGE)) {
				// 退出
				finish();
			}else if(action.equals(BroadcastManager.ACTION_EXIT_MESSAGE_RELOGIN)){
				//打开登录界面
				startActivity(new Intent(context, LoginActivity.class));
				// 退出
				finish();
			}else if(BroadcastManager.ACTION_CHANGE_ON_LINE_MODE.equals(intent.getAction())){
				// 切换到在线
				switchToOnLineMode(intent, context);
			}else if(BroadcastManager.ACTION_CHANGE_OFF_LINE_MODE.equals(intent.getAction())){
				// 切换到离线
				switchToOfflineMode(intent, context);
			}
		}
	};
	
	/**
	 * 弱引用该界面的handler
	 * 
	 * @author JackeyZhang
	 * @company 金鑫智慧
	 */
	public static abstract class BasicHandler extends Handler {
		WeakReference<Object> objectRef = null;

		public BasicHandler(Object object) {
			objectRef = new WeakReference<Object>(object);
		}

		protected abstract void basicHandleMessage(Message msg);

		public void handleMessage(Message msg) {
			Object object = objectRef.get();
			if (object != null) {
				basicHandleMessage(msg);
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BroadcastManager.registerBoradcastReceiver1(mExitBroadcastReceiver,
				BroadcastManager.ACTION_EXIT_MESSAGE,
				BroadcastManager.ACTION_EXIT_MESSAGE_RELOGIN,
				BroadcastManager.ACTION_CHANGE_ON_LINE_MODE,
				BroadcastManager.ACTION_CHANGE_OFF_LINE_MODE);
		creHandler();
		this.mUIHander = new BasicHandler(this) {

			@Override
			protected void basicHandleMessage(Message msg) {
				uiHandlerData(msg);
			}
		};
		
		BroadcastManager.registerBoradcastReceiver1(mScreenReceiver,
				Intent.ACTION_SCREEN_OFF);
	}
	
	@Override
	protected void onResume() {
		String _account = CommUtil.getCurrentLoginAccount();
    	boolean enableNine = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_PATTERN, false);
		if (enableNine) {
			if (UIMonitor.isUiBackgrounded() && enableNine) {
				startActivity(PatternActivity.getVerifyIntent(this));
			}
		}
		UIMonitor.setUiBackgrounded(false);
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		setActiveTime();
		return super.dispatchTouchEvent(ev);
	}

	public static void setActiveTime() {
		String _account = CommUtil.getCurrentLoginAccount();
		SharedDB.saveDB(_account, ControlDefine.KEY_LATEST_TOUCH_TIME,System.currentTimeMillis());
	}
	
	@Override
	protected void onPause() {
		if (suggestWindow != null) {
			suggestWindow.removeView();
		}
//		ShowSuggestWindows.instance.removeView();
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
		Intent intent = new Intent(UIMonitor.ACTION_ACTIVITY_STOP);
		sendBroadcast(intent);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收器
		BroadcastManager.unregisterBoradcastReceiver(mExitBroadcastReceiver);
		BroadcastManager.unregisterBoradcastReceiver(mBroadcastReceiver);
		BroadcastManager.unregisterBoradcastReceiver(mScreenReceiver);
		mExitBroadcastReceiver = null;
		mBroadcastReceiver = null;
		mScreenReceiver = null;
		mDownLoadHandler.removeCallbacksAndMessages(null);
		mDownLoadHandler.getLooper().quit();
		mDownLoadHandler = null;
	};
	
	/**
	 * 开启广播（会在onDestroy自动关闭）
	 * 
	 * @param broadcastReceiver
	 * @param action
	 *            接收的广播事件
	 */
	public void openBoradcastReceiver(BroadcastReceiver broadcastReceiver,
			String... action) {
		if (broadcastReceiver == null || action == null)
			return;
		// 如果广播已存在，则先关闭原广播
		if (this.mBroadcastReceiver != null) {
			BroadcastManager.unregisterBoradcastReceiver(mBroadcastReceiver);
			this.mBroadcastReceiver = null;
		}
		this.mBroadcastReceiver = broadcastReceiver;
		BroadcastManager.registerBoradcastReceiver2(this.mBroadcastReceiver,
				action);
	}
	
	/**
	 * 启动该界面的下载队列
	 */
	private void creHandler() {
		HandlerThread ht = new HandlerThread("download");
		ht.start();
		mDownLoadHandler = new Handler(ht.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};
	}
	
	/**
	 * 界面handler回调
	 * 
	 * @param msg
	 */
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			if (suggestWindow != null) {
				suggestWindow.removeView();
			}
//			ShowSuggestWindows.instance.removeView();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 切换到离线
	 */
	private void switchToOfflineMode(Intent intent, final Context ctx) {
		if (!NetworkModeSwitcher.useOfflineMode(ctx)) {
			Bundle bundle = intent.getExtras();
			String _titleOff = bundle.getString("modeOff");
			if (mb == null) {
				mb = new MessageBox(BaseActionBarActivity.this, _titleOff,
						"与服务器通信异常，是否启用离线模式", MessageBox.MB_OK
								| MessageBox.MB_CANCEL);
				mb.setButtonText("立即启用", null);
			} else {
				if (ctx != null && !mb.isShowing()) {
					mb.show();
				}
			}
			mb.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					switch (mb.getResult()) {
					case MessageBox.MB_OK://点击消失
						//							BroadcastManager.sendBroadcast(BroadcastManager.ACTION_CHANGE_ON_LINE_MODE, null);
						mb.dismiss();
						LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View dialogView = inflater.inflate(
								R.layout.custom_dialog_pwd_confirm,
								new LinearLayout(BaseActionBarActivity.this), false);
						final EditText pwdText = (EditText) dialogView
								.findViewById(R.id.pwd_confirm_text);
						Button btnSure = (Button) dialogView
								.findViewById(R.id.btn_sure);
						Button btnCancle = (Button) dialogView
								.findViewById(R.id.btn_cancel);
						final Dialog changDialog = new Dialog(
								BaseActionBarActivity.this);
						changDialog.setContentView(dialogView);
						changDialog
								.setTitle(R.string.confirm_secretkey);
						btnSure.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (JxshApp.m_VeinCore != null) {
									String mAuthId = JxshApp.lastInputID;
									String secretkey = EncryptUtil.getPassword(mAuthId);
									String pwd = pwdText.getText().toString();
									if (!TextUtils.isEmpty(pwd)) {
										if (secretkey.equals(pwd)) {
											NetTool netTool = new NetTool(
													BaseActionBarActivity.this);
											netTool.scan1();
											changDialog.dismiss();
										} else {
											JxshApp.showToast(
													ctx,
													CommDefines
															.getSkinManager()
															.string(R.string.li_xian_mo_shi_mi_ma_error));
										}
									}
								}
							}
						});
						btnCancle
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										changDialog.dismiss();
									}
								});
						changDialog.show();
					}
				}
			});
		}
	}
	
	/**
	 * 切换到在线
	 */
	private void switchToOnLineMode(Intent intent, Context con) {
		Bundle bundle = intent.getExtras();
		String _titleOn = bundle.getString("modeOn");
		if (_mBox == null) {
			_mBox = new MessageBox(BaseActionBarActivity.this, _titleOn,"网络连接变化，是否切换在线模式？",
					MessageBox.MB_OK | MessageBox.MB_CANCEL);
			_mBox.setButtonText("立即切换", null);
		}else{
			if(con != null && !_mBox.isShowing()){
				_mBox.show();
			}
		}
		_mBox.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				switch(_mBox.getResult()){
				case MessageBox.MB_OK://点击消失
					_mBox.dismiss();
					String _account = CommUtil.getCurrentLoginAccount();
					String secretkey = EncryptUtil.getPassword(_account);
					OfflineToOnline.changeToOnline(_account, secretkey, BaseActionBarActivity.this);
					break;
				}
			}
		});
	}
	
	/**
	 * 得到主线程中的downloadHandler
	 */
	public Handler getmDownLoadHandler() {
		return mDownLoadHandler;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_HOME:
		case KeyEvent.KEYCODE_BACK:
        case KeyEvent.KEYCODE_MENU:
			mUIHander.sendEmptyMessage(0);
			break;

		default:
			break;
		}
		return super.dispatchKeyEvent(event);
	}
	
	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
    			UIMonitor.setUiBackgrounded(true);
            }
        }
	};
}
