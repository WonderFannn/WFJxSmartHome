package com.jinxin.jxsmarthome.activity;

import java.lang.ref.WeakReference;

import android.app.Activity;
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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.main.JxshActivity;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.password.pattern.PatternActivity;
import com.jinxin.jxsmarthome.password.pattern.UIMonitor;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.offline.util.NetTool;
import com.jinxin.offline.util.OfflineToOnline;
import com.jinxin.record.SharedDB;
import com.umeng.analytics.MobclickAgent;
/**
 * activity基类
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public abstract class BaseActivity extends Activity {
	protected BasicHandler mUIHander = null;
	protected Handler mDownLoadHandler;

	private RelativeLayout ll_title = null;
	private LinearLayout ll_view = null;
	public Button button_back = null;
	public Button button_save = null;
	private TextView textView_title = null;
	
	private MessageBox _mBox = null;//在线提示框
	private MessageBox mb = null;//离线提示框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(CommDefines.getSkinManager().view(
				R.layout.base_layout));
		BroadcastManager.registerBoradcastReceiver1(mExitBroadcastReceiver,
				BroadcastManager.ACTION_EXIT_MESSAGE,
				BroadcastManager.ACTION_EXIT_MESSAGE_RELOGIN,
				BroadcastManager.ACTION_CHANGE_ON_LINE_MODE,
				BroadcastManager.ACTION_CHANGE_OFF_LINE_MODE);
		this.init();
		creHandler();
		this.mUIHander = new BasicHandler(this) {

			@Override
			protected void basicHandleMessage(Message msg) {
				// super.handleMessage(msg);
				uiHandlerData(msg);
			}
		};
		
		BroadcastManager.registerBoradcastReceiver1(mScreenReceiver,
				Intent.ACTION_SCREEN_OFF);
		
	}

	private void init() {
		this.ll_title = (RelativeLayout)this.findViewById(R.id.ll_title);
		this.ll_view = (LinearLayout)this.findViewById(R.id.ll_view);
		this.button_back = (Button)this.findViewById(R.id.button_back);
		this.button_save = (Button)this.findViewById(R.id.button_save);
		this.textView_title = (TextView)this.findViewById(R.id.textView_title);
	}
	/**
	 * 设置标题（可以传""）
	 * @param title
	 */
	public void setTitle(String title){
		if(this.textView_title != null && title != null){
			this.ll_title.setVisibility(View.VISIBLE);
			this.textView_title.setVisibility(View.VISIBLE);
			this.textView_title.setText(title);
		}
	}
	/**
	 * 返回事件
	 * @param listener
	 */
	public void setOnBackListener(OnClickListener listener){
		if(listener == null)return;
		this.button_back.setVisibility(View.VISIBLE);
		this.button_back.setOnClickListener(listener);
	}
    public void setContentView(int layoutResID) {
        new Exception(
                "BasicActivity setContentView() Exception : 继承此类不能使用  setContentView(),请用addLeftLayout() 与addRightLayout()")
                .printStackTrace();
    }
	/**
	 * 设置显示区
	 * @param layoutResID
	 */
	public void setView(int layoutResID){
		if(layoutResID == -1)return;
		this.ll_view.removeAllViews();
		View view = CommDefines.getSkinManager().view(layoutResID);
		 LayoutParams lp = (LayoutParams) view.getLayoutParams();
	        if (lp == null)
	            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	        lp.width = LayoutParams.MATCH_PARENT;
	        lp.height = LayoutParams.MATCH_PARENT;
	        view.setLayoutParams(lp);
	        this.ll_view.addView(view);
	}
	/**
	 * 保存事件
	 * @param listener
	 */
	public void setOnSaveListener(OnClickListener listener){
		if(listener == null)return;
		this.button_save.setVisibility(View.VISIBLE);
		this.button_save.setOnClickListener(listener);
	}
	
	public void hideSaveButton(boolean b) {
		if(b) {
			this.button_save.setVisibility(View.INVISIBLE);
		}else {
			this.button_save.setVisibility(View.VISIBLE);
		}
		
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
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		setActiveTime();
		return super.dispatchKeyEvent(event);
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
	protected void onResume() {
		String _account = CommUtil.getCurrentLoginAccount();
    	boolean enableNine = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_PATTERN, false);
		if (enableNine) {
			if (UIMonitor.isUiBackgrounded() && enableNine && isLogin()) {
				startActivity(PatternActivity.getVerifyIntent(this));
			}
		} 
		UIMonitor.setUiBackgrounded(false);
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	private boolean isLogin() {
		if(this instanceof LoginActivity) {
			return false;
		}
		if(this instanceof JxshActivity) {
			return false;
		}
		return true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Intent intent = new Intent(UIMonitor.ACTION_ACTIVITY_STOP);
		sendBroadcast(intent);
	}
	
	/**
	 * 界面handler回调
	 * 
	 * @param msg
	 */
	public abstract void uiHandlerData(Message msg);

	/**
	 * 开启广播（会在onDestroy自动关闭）
	 * 
	 * @param broadcastReceiver
	 * @param action
	 *            接收的广播事件
	 */
	protected void openBoradcastReceiver(BroadcastReceiver broadcastReceiver,
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
	 * 退出的广播事件处理
	 */
	private BroadcastReceiver mExitBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastManager.ACTION_EXIT_MESSAGE)) {
				// 退出
				BaseActivity.this.finish();
			}else if(action.equals(BroadcastManager.ACTION_EXIT_MESSAGE_RELOGIN)){
				// 退出
				BaseActivity.this.finish();
				//打开登录界面
//				startActivity(new Intent(context, LoginActivity.class));
			}else if(BroadcastManager.ACTION_CHANGE_ON_LINE_MODE.equals(intent.getAction())){
				final Context con = context;
				Bundle bundle = intent.getExtras();
				String _titleOn = bundle.getString("modeOn");
				if (_mBox == null) {
					_mBox = new MessageBox(BaseActivity.this, _titleOn,"网络连接变化，是否切换在线模式？",
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
							OfflineToOnline.changeToOnline(_account, secretkey, BaseActivity.this);
							break;
						}
					}
				});
			}else if(BroadcastManager.ACTION_CHANGE_OFF_LINE_MODE.equals(intent.getAction())){
				final Context ctx = context;
				if (!NetworkModeSwitcher.useOfflineMode(ctx)) {
					Bundle bundle = intent.getExtras();
					String _titleOff = bundle.getString("modeOff");
					if (mb == null) {
						mb = new MessageBox(BaseActivity.this, _titleOff,
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
										null);
								final EditText pwdText = (EditText) dialogView
										.findViewById(R.id.pwd_confirm_text);
								Button btnSure = (Button) dialogView
										.findViewById(R.id.btn_sure);
								Button btnCancle = (Button) dialogView
										.findViewById(R.id.btn_cancel);
								final Dialog changDialog = new Dialog(
										BaseActivity.this);
								changDialog.setContentView(dialogView);
								changDialog
										.setTitle(R.string.confirm_secretkey);
								btnSure.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										String mAuthId = JxshApp.lastInputID;
										String secretkey = EncryptUtil.getPassword(mAuthId);
										if (!TextUtils.isEmpty(pwdText
												.getText().toString())) {
											String pwd = pwdText.getText()
													.toString();
											if (secretkey.equals(pwd)) {
												NetTool netTool = new NetTool(
														BaseActivity.this);
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
		}
	};
	/**
	 * 孩子的广播事件
	 */
	private BroadcastReceiver mBroadcastReceiver;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// CommUtil.setAllowChange(true);
		// 注销广播接收器
		BroadcastManager.unregisterBoradcastReceiver(mExitBroadcastReceiver);
		BroadcastManager.unregisterBoradcastReceiver(mBroadcastReceiver);
		BroadcastManager.unregisterBoradcastReceiver(mScreenReceiver);
		mExitBroadcastReceiver = null;
		mBroadcastReceiver = null;
		mScreenReceiver = null;
		// //
		mDownLoadHandler.removeCallbacksAndMessages(null);
		mDownLoadHandler.getLooper().quit();
		mDownLoadHandler = null;
		
	}

	/**
	 * 弱引用该界面的handler
	 * 
	 * @author JackeyZhang
	 * @company 金鑫智慧
	 */
	public abstract class BasicHandler extends Handler {
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
