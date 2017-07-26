package xgzx.VeinUnlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.jinxin.datan.net.command.VersionUpgradeTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.LoginActivity;
import com.jinxin.jxsmarthome.activity.MainActivity;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.VersionVO;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.DownloadAPKDialog;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.offline.util.OfflineToOnline;
import com.jinxin.record.SharedDB;
import com.jinxin.veinunlock.camera.ImageCaptureCallback;

public class VeinLogin extends Activity {
	/** Called when the activity is first created. */
	private Preview mPreview;
	private Context mContext;
	private PowerManager.WakeLock mWakeLock;///设置手机一直处于亮光状态
	private  Vibrator mVibrator;
	private boolean isOnPause;
	private BroadcastReceiver mLoginedReceiver;
	public static String ACTION_LOGINED = "action_logined";
	public static String ACTION_LOGIN_FAIL = "action_login_fail";
	
	private VersionUpgradeTask upgradeTask = null;
	protected Handler mUIHander = null;
	private boolean forceLatestVersion = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Logger.debug(ACTIVITY_SERVICE, "VeinLogin.........!");
		super.onCreate(savedInstanceState);
		mContext = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);////设置全屏显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mVibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);

		mPreview = new Preview(this);
		setContentView(mPreview);

		////设置手持设备一直处于亮光状态
		PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE); 
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "XYTEST"); 
		mWakeLock.acquire(); 
		
		mLoginedReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (ACTION_LOGINED.equals(intent.getAction())) {
					Intent SetIntent = new Intent(VeinLogin.this, MainActivity.class);
					SetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(SetIntent);
					finish();
				} else if (ACTION_LOGIN_FAIL.equals(intent.getAction())) {
					Intent SetIntent = new Intent(VeinLogin.this, LoginActivity.class);
					SetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("selectUser", JxshApp.m_VeinCore.getUser());
					SetIntent.putExtras(bundle);
					startActivity(SetIntent);
					finish();
				}
			}
		};
		
		this.mUIHander = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 0:
					Bundle _bundle = (Bundle)msg.obj;
					boolean isLoading = _bundle.getBoolean("isLoading");
					int stringID = _bundle.getInt("stringID");
					if(isLoading){
//						button_login.setVisibility(View.GONE);
//						loadingView.setVisibility(View.VISIBLE);
//						if(stringID != -1){
//							textViewLoading.setText(CommDefines.getSkinManager().string(stringID));
//						}
//						}else{
//							button_login.setVisibility(View.VISIBLE);
//							loadingView.setVisibility(View.GONE);
//						}
					break;
					}
				}
				super.handleMessage(msg);
			}
			
		};
		
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_LOGINED);
		myIntentFilter.addAction(ACTION_LOGIN_FAIL);
		registerReceiver(mLoginedReceiver, myIntentFilter);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onStart() {
		super.onStart();
	}
	///预览，不断的返回一个byte数组，称为回调函数

	class Preview extends SurfaceView implements SurfaceHolder.Callback {
		SurfaceHolder mHolder;
		ImageCaptureCallback iccb = null;
		private AutoFocusCallback afcb = null;
		Resources res = this.getResources();

		int m_iDispW = 0;
		int m_iDispH = 0;

		Preview(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			setFocusable(true); // Enable Focus in order to obtain Key events 
			setFocusableInTouchMode(true); // Ditto (in order to sense BACK-key as well)
		}

		public class AutoFocusCallback implements Camera.AutoFocusCallback {
			public void onAutoFocus(boolean success, Camera camera)
			{
				Logger.error("AutoFocus", "callback...");
				iccb.m_bFocusOk = true;
			}
		}
		
		public void surfaceCreated(SurfaceHolder holder) {
			if(isOnPause){
				return;
			}
			m_iDispW = getWidth();
			m_iDispH = getHeight();
			afcb = new AutoFocusCallback();
			iccb = new ImageCaptureCallback(mHolder,mContext,(View)getParent(),
					mHandler,afcb,VeinCore.MODE_AUTO_AUTH, 10);
			iccb.initDisplayData(m_iDispW, m_iDispH);
			iccb.m_bFocusOk = true;//验证的时候不启动自动对焦
		}
		
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (null != iccb) {
				iccb.destoryImageWindow();
			}
			afcb = null;
			mHolder = null;
			res = null;
		}
		
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			if(isOnPause){
				return;
			}
		}
	}
	int retry = 0;
	private final Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VeinCore.MSG_AUTH_OK:
				Toast.makeText(mContext, "静脉认证成功！", Toast.LENGTH_SHORT).show();
				doUpgradeTask();
//				AuthOkExit();
				break;
			case VeinCore.MSG_TIME_OUT:
			case VeinCore.MSG_AUTH_FAILURE:
			case VeinCore.MSG_FINISH_EXIT:
				String _account = JxshApp.m_VeinCore.getUser();
				boolean veinOnly = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_VEIN_ONLY, false);
				if(veinOnly) {
					if(++retry >= 3) {
						Toast.makeText(mContext, "静脉认证失败！", Toast.LENGTH_SHORT).show();
						SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
						JxshApp.instance.onDestroy();
						finish();
					} else {
						final MessageBox mb2 = new MessageBox(
								VeinLogin.this,CommDefines.getSkinManager().string(R.string.vein_retry_alert),"静脉认证失败,你还有" + (3 - retry) + "次机会",MessageBox.MB_OK|MessageBox.MB_CANCEL);
						mb2.setOnDismissListener(new OnDismissListener() {
							
							@Override
							public void onDismiss(DialogInterface dialog) {
								switch(mb2.getResult()){
								case MessageBox.MB_OK:
									mPreview = null;
									mPreview = new Preview(mContext);
									setContentView(mPreview);
									break;
								case MessageBox.MB_CANCEL:
									String _account = JxshApp.m_VeinCore.getUser();
									SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
									JxshApp.instance.onDestroy();
									finish();
									break;
								}
							}
						});
						mb2.setButtonText("确定","取消");
						mb2.show();
					}
				} else {
					PasswordDlg();
				}
				break;
			}
		};
	};
	private void AuthOkExit(){
		
		if(mVibrator != null){
			mVibrator.vibrate(30);
		}
		String _account = JxshApp.m_VeinCore.getUser();
		String secretkey = EncryptUtil.getPassword(_account);
		OfflineToOnline.changeToOnline(_account, secretkey, VeinLogin.this);
	}
	
	public void downloadCancelCallback() {
		if(forceLatestVersion) {
			String _account = JxshApp.m_VeinCore.getUser();
			SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
			JxshApp.instance.onDestroy();
			super.onBackPressed();
		} else {
			AuthOkExit();
		}
	}
	
	@Override
	protected void onPause() {
		isOnPause = true;
		if(mPreview != null && mPreview.mHolder != null){
			mPreview.surfaceDestroyed(mPreview.mHolder);
		}
		super.onPause();
		finish();
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mLoginedReceiver);
		mLoginedReceiver = null;
		mWakeLock.release();
		mWakeLock = null;
		mPreview.mHolder = null;
		mPreview = null;
		mContext = null;
	}
	@Override
	public void onBackPressed() {
		OfflineToOnline.stopLogin();
		finish();
		super.onBackPressed();
	}
	
	/*
	 * 切换密码登录
	 */
	private void PasswordDlg ()
	{
		AlertDialog password_dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater layout = null;
		layout = LayoutInflater.from(mContext);
		final View dlg_v = layout.inflate(R.layout.password, null);
		builder.setTitle("提示信息");
		builder.setView(dlg_v);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				Intent intent = new Intent(VeinLogin.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		builder.setNeutralButton("重试", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				mPreview = null;
				mPreview = new Preview(mContext);
				setContentView(mPreview);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				finish();
			}
		});
		password_dialog = builder.create();
		password_dialog.setCancelable(true);
		password_dialog.show();
	}
	
	private void doUpgradeTask() {
		String version = CommUtil.getPackageInfo(getApplicationContext(), getPackageName()).versionName;
		this.upgradeTask = new VersionUpgradeTask(VeinLogin.this, version,ControlDefine.APP_TYPE_ANDROID_PHONE);
		this.upgradeTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				System.out.println("onStarted");
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				System.out.println("onCanceled");
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				System.out.println("onFail");
				mUIHander.post(new Runnable() {
					
					@Override
					public void run() {
						AuthOkExit();
					}
				});
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				System.out.println("onSuccess");
				if(arg != null&& arg.length > 0){
					final VersionVO _vo = (VersionVO) arg[0];
					if(_vo != null){
						switch(_vo.getStatus()){
						case 1://普通下载
							mUIHander.post(new Runnable() {
								
								@Override
								public void run() {
									final MessageBox mb = new MessageBox(
											VeinLogin.this,CommDefines.getSkinManager().string(R.string.ban_ben_geng_xin),_vo.getComments(),MessageBox.MB_OK | MessageBox.MB_CANCEL);
									mb.setOnDismissListener(new OnDismissListener() {
										
										@Override
										public void onDismiss(DialogInterface dialog) {
											switch(mb.getResult()){
											case MessageBox.MB_OK://开启下载
												DownloadAPKDialog downloadAPKDialog = new DownloadAPKDialog(VeinLogin.this, CommDefines.getSkinManager().string(R.string.ban_ben_geng_xin), _vo.getAppPath(), _vo.getSize());
												downloadAPKDialog.show();
												break;
											case MessageBox.MB_CANCEL:
												AuthOkExit();
												break;
											}
										}
									});
									mb.show();
								}
							});
						
						break;
						case 2://强制下载
							mUIHander.post(new Runnable() {
								@Override
								public void run() {
									final MessageBox mb2 = new MessageBox(
											VeinLogin.this,CommDefines.getSkinManager().string(R.string.ban_ben_geng_xin),_vo.getComments(),MessageBox.MB_OK);
									mb2.setOnDismissListener(new OnDismissListener() {
										
										@Override
										public void onDismiss(DialogInterface dialog) {
											switch(mb2.getResult()){
											case MessageBox.MB_OK://开启下载
												DownloadAPKDialog downloadAPKDialog = new DownloadAPKDialog(VeinLogin.this, CommDefines.getSkinManager().string(R.string.ban_ben_geng_xin), _vo.getAppPath(), _vo.getSize());
												downloadAPKDialog.show();
												break;
											}
										}
									});
									mb2.show();
								}
							});
							forceLatestVersion = true;
							
							break;
						}
					}
					
				} else {
					mUIHander.post(new Runnable() {
						
						@Override
						public void run() {
							AuthOkExit();
						}
					});
					
				}
				
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				System.out.println("onProcess");
			}
			
		});
		this.upgradeTask.start();
	}
	
}