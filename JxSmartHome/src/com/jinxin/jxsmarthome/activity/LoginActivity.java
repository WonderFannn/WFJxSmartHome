package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.command.CustomerDetailTask;
import com.jinxin.datan.net.command.CustomerPatternListTask;
import com.jinxin.datan.net.command.CustomerProductListTask;
import com.jinxin.datan.net.command.CustomerTimerListForLoginTask;
import com.jinxin.datan.net.command.DoorbellListTask;
import com.jinxin.datan.net.command.FeedbackListTask;
import com.jinxin.datan.net.command.FunDetailConfigTask;
import com.jinxin.datan.net.command.FunDetailListTask;
import com.jinxin.datan.net.command.LoginTask;
import com.jinxin.datan.net.command.MonitorListTask;
import com.jinxin.datan.net.command.OEMVersionTask;
import com.jinxin.datan.net.command.ProductFunListTask;
import com.jinxin.datan.net.command.ProductPatternOperationListTask;
import com.jinxin.datan.net.command.SyncCloudSettingTask;
import com.jinxin.datan.net.command.SysUserDetailTask;
import com.jinxin.datan.net.command.TimerTaskOperationListForLoginTask;
import com.jinxin.datan.net.command.UpdateCustomerAreaTask;
import com.jinxin.datan.net.command.UpdateCustomerDataSyncTask;
import com.jinxin.datan.net.command.UpdateCustomerProductAreaTask;
import com.jinxin.datan.net.command.UpdateProductRegisterTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigFunTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigTask;
import com.jinxin.datan.net.command.VersionUpgradeTask;
import com.jinxin.datan.net.command.WakeWordListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerAreaDaoImpl;
import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.CustomerProductAreaDaoImpl;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.db.impl.OEMVersionDaoImpl;
import com.jinxin.db.impl.ProductRegisterDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.db.impl.SysUserDaoImpl;
import com.jinxin.db.util.CustomerDataSyncHelper;
import com.jinxin.db.util.DBHelper;
import com.jinxin.jpush.BroadPushMessage;
import com.jinxin.jpush.ExampleUtil;
import com.jinxin.jpush.MessageQueue2;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.CommonData;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.entity.CustomerDataSync;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.entity.Feedback;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.OEMVersion;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.entity.TimerTaskOperation;
import com.jinxin.jxsmarthome.entity.VersionVO;
import com.jinxin.jxsmarthome.entity.WakeWord;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.ui.widget.DownloadAPKDialog;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.ui.widget.vfad.ADManager;
import com.jinxin.jxsmarthome.ui.widget.vfad.Advertising;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登陆界面
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	private Context context;
	private EditText editText_account = null;
	private EditText editText_password = null;
	private EditText editip, editport;
	private Button button_login = null;
	private TextView textViewLoading = null;
	private TextView forget_password = null;
	// private TextView switch_user = null;
	private LinearLayout loginView = null;
	private LinearLayout loginMain = null;
	private LinearLayout loadingView = null;
	private TextView textViewMsg = null;
	private ImageView imageViewLogo = null;
	private Button voiceLogin = null;
	private TextView tvMore, setVoiceLogin, setPwdLogin;
	private View vLine = null;

	private int cancelTaskCount = 0;
	private int failTaskCount = 0;
	private int succesTaskCount = 0;
	private LoginTask loginTask = null;
	private VersionUpgradeTask upgradeTask = null;

	private Animation animationMsg = null;

	private ArrayAdapter<String> accountAdapter = null;


	private boolean forceLatestVersion = false;
	private boolean isOpenVoiceLogin = false;// 是否开启声纹登录
	private boolean isPwdLogin = true;// 是否是使用密码登录
	private String mAuthId;

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;
	private static final int ACCOUNT_UNABLE = 1004;
	private static final String TAG = "JPush";

	private String QUE_SPEAKER = "que";
	// private Handler mHandler,mhandler;
	private Timer timer;
	Looper looper = Looper.getMainLooper();
	// 声纹识别对象
	private SpeakerVerifier mVerify;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		// 采用单例模式，在Application中做初始化
		this.mVerify = JxshApp.instance.getVerifier();
		// mhandler = new Handler(looper);
		timer = new Timer(true);
		setTimerTask();
		initView();
		doUpgradeTask();
		// 每分钟推送消息给大网关去获得大网关的ip和端口 add by rp
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		try {
			//防止程序在后台crash
			//程序第一次安装运行不能在后台运行，结束程序后，之后的使用可以在后台运行。
			Thread.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 初始化
	 */
	private void initView() {
		this.setView(R.layout.login_layout);
		animationMsg = AnimationUtils.loadAnimation(this, R.anim.login_msg);
		this.textViewMsg = (TextView) this.findViewById(R.id.textViewMsg);
		this.loginMain = (LinearLayout) this.findViewById(R.id.linearLayoutMain);
		this.editText_account = (EditText) this.findViewById(R.id.editText_account);
		this.editText_password = (EditText) this.findViewById(R.id.editText_password);
		// editip=(EditText)findViewById(R.id.ip_choose);
		// editport=(EditText)findViewById(R.id.gateway_port);
		this.loginView = (LinearLayout) this.findViewById(R.id.loginView);
		this.textViewLoading = (TextView) this.findViewById(R.id.textViewLoading);
		this.loadingView = (LinearLayout) this.findViewById(R.id.loadingView);
		this.button_login = (Button) this.findViewById(R.id.button_login);
		// this.voiceLogin = (Button)
		// this.findViewById(R.id.voice_button_login);
		this.forget_password = (TextView) findViewById(R.id.forget_password);
		// this.switch_user= (TextView) findViewById(R.id.switch_user);
		this.imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
		this.setVoiceLogin = (TextView) findViewById(R.id.tv_set_voice_login);
		this.setPwdLogin = (TextView) findViewById(R.id.tv_set_pwd_login);
		this.vLine = findViewById(R.id.v_line);

		this.button_login.setOnClickListener(this);
		this.forget_password.setOnClickListener(this);
		// this.switch_user.setOnClickListener(this);
		// this.voiceLogin.setOnClickListener(this);
		this.setVoiceLogin.setOnClickListener(this);
		this.setPwdLogin.setOnClickListener(this);
		findViewById(R.id.tv_more).setOnClickListener(this);

		this.initData();
		// initIpData();
		this.changeLoadingStatus(false, -1);

		// if(JxshApp.getHistoryUserList().size() > 1) {
		// this.switch_user.setVisibility(View.VISIBLE);
		// }
		doFetchLogo();

	}

	/**
	 * 根据是否打开声纹登录，改变UI布局
	 * 
	 * @param isOpen
	 */
	private void changeLoginUI(boolean isOpen) {
		if (isOpen) {
			button_login.setText(CommDefines.getSkinManager().string(R.string.voice_lock_login));
			editText_password.setVisibility(View.GONE);
			vLine.setVisibility(View.GONE);
			setVoiceLogin.setVisibility(View.GONE);
			setPwdLogin.setVisibility(View.VISIBLE);
			isPwdLogin = false;
		} else {
			button_login.setText(CommDefines.getSkinManager().string(R.string.login));
			editText_password.setVisibility(View.VISIBLE);
			vLine.setVisibility(View.VISIBLE);
			setVoiceLogin.setVisibility(View.VISIBLE);
			setPwdLogin.setVisibility(View.GONE);
			isPwdLogin = true;
		}
	}

	private void doFetchLogo() {
		OEMVersion data = null;
		OEMVersionDaoImpl daoImpl = new OEMVersionDaoImpl(this);
		List<OEMVersion> list = daoImpl.find(null, "account=?", new String[] { CommUtil.getCurrentLoginAccount() },
				null, null, null, null);
		if (list != null && list.size() > 0) {
			data = list.get(0);
		}

		if (data != null) {
			JxshApp.instance.getFinalBitmap().display(imageViewLogo,
					DatanAgentConnectResource.HTTP_ICON_PATH + data.getAppLog().substring(1));
		}
	}

	/**
	 * 初始化显示数据(在initView之后)
	 */
	private void initData() {
		String select = getIntent().getStringExtra("selectUser");
		if (select != null) {
			this.editText_account.setText(select);
			if (JxshApp.m_VeinCore != null) {
				mAuthId = JxshApp.m_VeinCore.getUser();
			}
		} else {
			this.editText_account.setText(CommUtil.getCurrentLoginAccount());
			this.loginMain.setAnimation(AnimationUtils.loadAnimation(this, R.anim.login_in));
			mAuthId = CommUtil.getCurrentLoginAccount();
		}

		if (!TextUtils.isEmpty(mAuthId)) {
			isOpenVoiceLogin = SharedDB.loadBooleanFromDB(mAuthId, ControlDefine.KEY_VOICE_LOCK, false);
		}

		changeLoginUI(isOpenVoiceLogin);
	}

	/**
	 * 初始化ip,port 数据
	 */

	private void initIpData() {
		// SharedPreferences sharePref = getSharedPreferences("default_ip",
		// MODE_PRIVATE);
		//
		// String serverIP = sharePref.getString("gatewayIP", editip.getText()
		// .toString());
		String serverIP = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_GATEWAYIP, "");
		String serverport = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_GATEWAYPORT, "");
		// int serverPort = sharePref.getInt("gatewayPort",
		// Integer.parseInt(editport.getText().toString()));
		if (!serverIP.equals("") && !serverport.equals("")) {
			int serverPort = Integer.parseInt(serverport);
			editip.setText(serverIP);

			editport.setText(serverPort + "");
		} else {

			editip.setText(editip.getText().toString());
			editport.setText(editport.getText().toString());
		}

	}

	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			Bundle _bundle = (Bundle) msg.obj;
			boolean isLoading = _bundle.getBoolean("isLoading");
			int stringID = _bundle.getInt("stringID");
			if (isLoading) {
				button_login.setVisibility(View.GONE);
				loadingView.setVisibility(View.VISIBLE);
				if (stringID != -1) {
					textViewLoading.setText(CommDefines.getSkinManager().string(stringID));
				}
			} else {
				button_login.setVisibility(View.VISIBLE);
				loadingView.setVisibility(View.GONE);
			}
			break;
		case 101:// 温馨提示
			String _msg = (String) msg.obj;
			if (!TextUtils.isEmpty(_msg)) {
				textViewMsg.setText(_msg);
				textViewMsg.startAnimation(animationMsg);
			}
			if (isShowing) {
				this.showMsg(false);
			} else {
				textViewMsg.setText("");
			}
			break;
		case MSG_SET_TAGS:
			Logger.debug(TAG, "Set tag in handler.");
			JPushInterface.setAliasAndTags(this, null, (Set<String>) msg.obj, mTagsCallback);
			break;
		case MSG_SET_ALIAS:
			Logger.debug(TAG, "Set alias in handler.");
			JPushInterface.setAliasAndTags(this, (String) msg.obj, null, mAliasCallback);
			break;
		case ACCOUNT_UNABLE:
			JxshApp.showToast(LoginActivity.this, getString(R.string.account_is_not_enable));
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (this.loginTask == null) {
			JxshApp.instance.onDestroy();
			super.onBackPressed();
		} else {
			this.loginTask.cancel();
			this.loginTask = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.cancelTaskCount = 0;
		this.failTaskCount = 0;
		this.succesTaskCount = 0;
		isShowing = false;
	}

	private void doUpgradeTask() {
		String version = CommUtil.getPackageInfo(getApplicationContext(), getPackageName()).versionName;
		this.upgradeTask = new VersionUpgradeTask(null, version, ControlDefine.APP_TYPE_ANDROID_PHONE);
		this.upgradeTask.addListener(new ITaskListener<ITask>() {

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
				if (arg != null && arg.length > 0) {
					final VersionVO _vo = (VersionVO) arg[0];
					if (_vo != null) {
						switch (_vo.getStatus()) {
						case 1:// 普通下载
							mUIHander.post(new Runnable() {

								@Override
								public void run() {
									final MessageBox mb = new MessageBox(LoginActivity.this,
											CommDefines.getSkinManager().string(R.string.ban_ben_geng_xin),
											_vo.getComments(), MessageBox.MB_OK | MessageBox.MB_CANCEL);
									mb.setOnDismissListener(new OnDismissListener() {

										@Override
										public void onDismiss(DialogInterface dialog) {
											switch (mb.getResult()) {
											case MessageBox.MB_OK:// 开启下载
												DownloadAPKDialog downloadAPKDialog = new DownloadAPKDialog(
														LoginActivity.this,
														CommDefines.getSkinManager().string(R.string.ban_ben_geng_xin),
														_vo.getAppPath(), _vo.getSize());
												downloadAPKDialog.show();
												break;
											}
										}
									});
									// if (LoginActivity.this.isDestroyed()) {
									// return;
									// }
									mb.show();
								}
							});

							break;
						case 2:// 强制下载
							mUIHander.post(new Runnable() {

								@Override
								public void run() {
									final MessageBox mb2 = new MessageBox(LoginActivity.this,
											CommDefines.getSkinManager().string(R.string.ban_ben_geng_xin),
											_vo.getComments(), MessageBox.MB_OK);
									mb2.setOnDismissListener(new OnDismissListener() {

										@Override
										public void onDismiss(DialogInterface dialog) {
											switch (mb2.getResult()) {
											case MessageBox.MB_OK:// 开启下载
												DownloadAPKDialog downloadAPKDialog = new DownloadAPKDialog(
														LoginActivity.this,
														CommDefines.getSkinManager().string(R.string.ban_ben_geng_xin),
														_vo.getAppPath(), _vo.getSize());
												downloadAPKDialog.show();
												break;
											}
										}
									});
									// if (LoginActivity.this.isDestroyed()) {
									// return;
									// }
									mb2.show();
								}
							});
							forceLatestVersion = true;

							break;
						}
					}

				}

			}

			@Override
			public void onProcess(ITask task, Object[] arg) {

			}

		});
		this.upgradeTask.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//		case R.id.forget_password:
		//			// 跳转找回密码界面
		//			Intent _intent = new Intent(LoginActivity.this, ConfirmAccountActivity.class);
		//			startActivity(_intent);
		//			break;
		//		case R.id.switch_user:
		//			Intent _intentSU = new Intent(LoginActivity.this, SelectUserActivity.class);
		//			startActivity(_intentSU);
		//			finish();
		//			break;
		case R.id.button_login:
			if (!isPwdLogin) {//声纹登录
				if (TextUtils.isEmpty(editText_account.getText().toString())) {
					JxshApp.showToast(context,"帐号不能为空");
					return;
				}
				String account = editText_account.getText().toString();
				JxshApp.lastInputID = account;
				//				if (!TextUtils.isEmpty(mAuthId)) {
				//					if (!mAuthId.equals(account)) {
				//						JxshApp.showToast(context, "请使用密码登录该帐号,声纹登录仅支持上次成功登录的账户");
				//						return;
				//					}
				//				}
				performModelOperation(QUE_SPEAKER, mModelOperationListener, account);

			}else{//密码登录
				if (LoginActivity.this.getCurrentFocus() != null) {
					((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).
					hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(),0);   
				}
				this.cancelTaskCount = 0;
				this.failTaskCount = 0;
				this.succesTaskCount = 0;//重新计数登录接口数量

				if(forceLatestVersion) {
					doUpgradeTask();
					return;
				}
				final String account = this.editText_account.getText().toString();
				final String password = this.editText_password.getText().toString();
				//				final String gatewayip=this.editip.getText().toString();
				//				final String gatewayport=this.editport.getText().toString();
				if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password) /*|| StringUtils.isEmpty(gatewayip) || StringUtils.isEmpty(gatewayport)*/) {
					JxshApp.showToast(
							LoginActivity.this,
							CommDefines.getSkinManager().string(
									R.string.zhang_hu_mi_ma_bu_neng_wei_null));
				} else {
					JxshApp.lastInputID = account;
					//登陆
					this.loginTask = new LoginTask(LoginActivity.this, account,
							password);

					this.loginTask.addListener(new ITaskListener<ITask>() {
						@Override
						public void onStarted(ITask task, Object arg) {
							changeLoadingStatus(true,R.string.zhang_hu_deng_lu);
						}

						@Override
						public void onCanceled(ITask task, Object arg) {
							changeLoadingStatus(false,-1);
						}

						@Override
						public void onFail(ITask task, Object[] arg) {
							changeLoadingStatus(false,-1);
							if(arg != null && arg.length > 0){
								JxshApp.showToast(LoginActivity.this, getString(R.string.yong_hu_mi_ma_error));
							}
						}

						@Override
						public void onSuccess(ITask task, Object[] arg) {
							//						taskCount+=1;
							//save gateway ip and port
							//							saveGatewayData();
							//							//先推送消息到大网关
							//							String id_account = CommUtil.getCurrentLoginAccount();
							////							String id_account="C28001000500";
							//							String serverIP=editip.getText().toString();
							//							String serverPort=editport.getText().toString();
							//							String msg = serverIP+serverPort+"(4100)";
							//							MessageQueue2.getInstance().offer(new BroadPushMessage(msg,id_account));	 
							//							Log.i("pushmsg","--------------------------"+msg);
							//save ip and port of big gateway
							//							SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
							//									ControlDefine.KEY_GATEWAYIP, editip.getText().toString());	
							//							SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
							//									ControlDefine.KEY_GATEWAYPORT, editport.getText().toString());	

							String _lastUser = CommUtil.getCurrentLoginAccount();
							if (arg != null && arg.length > 0){
								SysUser sysUser = (SysUser) arg[0];
								String account = sysUser.getAccount();
								String secretkey = sysUser.getPassword();
								String nickyName = sysUser.getNickyName();
								String subAccount = sysUser.getSubAccunt();
								//								String pwd = MD5Util.getURLEncode(password);
								if (TextUtils.isEmpty(subAccount)) {//空为账户登录
									SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
											ControlDefine.KEY_ACCOUNT, account);
									SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
											ControlDefine.KEY_SUNACCOUNT, "");
									// save account list and key
									JxshApp.saveHistoryUserList(account, password);
									//加密
									EncryptUtil.setPassword(password);
								}else{//子账户登录
									SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
											ControlDefine.KEY_ACCOUNT, account);
									SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
											ControlDefine.KEY_SUNACCOUNT, subAccount);
									// save account list and key
									JxshApp.saveHistoryUserList(subAccount, password);
									//加密
									EncryptUtil.setPassword(password);
								}
								SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
										ControlDefine.KEY_SECRETKEY,secretkey);
								SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
										ControlDefine.KEY_NICKNAME,nickyName);

								//	SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
								//	ControlDefine.KEY_PASSWORD,password);
								//给密码加密存入ordinary配置表
								EncryptUtil.setPasswordForOrdinary(password);
							}

							String _loginUser = CommUtil.getCurrentLoginAccount();
							if(!TextUtils.isEmpty(_lastUser) && !_lastUser.equals(_loginUser)) {
								JxshApp.instance.killBell();
							}
							CrashReport.setUserId(CommUtil.getCurrentLoginAccount());
							MobclickAgent.onProfileSignIn(CommUtil.getCurrentLoginAccount());//Umeng 帐号统计

							//=============唤醒词查看请求===================//
							WakeWordListTask wakeWordListTask = new WakeWordListTask(context, account);
							wakeWordListTask.addListener(new ITaskListener<ITask>() {

								@Override
								public void onStarted(ITask task, Object arg) {
								}

								@Override
								public void onCanceled(ITask task, Object arg) {
								}

								@Override
								public void onFail(ITask task, Object[] arg) {
									Log.i("TAG", "onFail======>");
								}

								@Override
								public void onSuccess(ITask task, Object[] arg) {
									if(arg != null && arg.length > 0) {
										List<WakeWord> wakeWordList = new ArrayList<WakeWord>();
										wakeWordList.addAll((List<WakeWord>)arg[0]);
										//										CommonMethod.updateFeedback(getApplicationContext(),wakeWordList);
										//清空数据
										if(!CommonData.wake_words.isEmpty()){
											CommonData.wake_words.clear();
										}
										//遍历添加
										for(int i=0; i<wakeWordList.size(); i++){
											String word = wakeWordList.get(i).getWakeUpWord();
											CommonData.wake_words.add(word);
										}
									}
								}

								@Override
								public void onProcess(ITask task, Object[] arg) {
								}
							});
							wakeWordListTask.start();

							
							DoorbellListTask doorbellTask = new DoorbellListTask(null, "01b");
							doorbellTask.addListener(new ITaskListener<ITask>() {

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
									String _account = CommUtil.getCurrentLoginAccount();
									String doorbelList = SharedDB.loadStrFromDB(_account,
											ControlDefine.KEY_DOORBELL_LIST, "");
									Intent intent = new Intent("com.jinxin.jxsmarthome.action.DOORBELL");
									Bundle data = new Bundle();
									data.putString(ControlDefine.KEY_DOORBELL_LIST, doorbelList);
									intent.putExtras(data);
									LoginActivity.this.sendBroadcast(intent);

								}

								@Override
								public void onProcess(ITask task, Object[] arg) {

								}
							});
							doorbellTask.start();


							MonitorListTask momitorTask = new MonitorListTask(null, "01d");
							momitorTask.addListener(new ITaskListener<ITask>() {

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
									String _account = CommUtil.getCurrentLoginAccount();
									String monitorList = SharedDB.loadStrFromDB(_account,
											ControlDefine.KEY_MONITOR_LIST, "");
									Intent intent = new Intent("com.jinxin.jxsmarthome.action.MONITOR");
									Bundle data = new Bundle();
									data.putString(ControlDefine.KEY_MONITOR_LIST, monitorList);
									intent.putExtras(data);
									LoginActivity.this.sendBroadcast(intent);
								}

								@Override
								public void onProcess(ITask task, Object[] arg) {

								}
							});
							momitorTask.start();

							FeedbackListTask feedbackTask = new FeedbackListTask(null);
							feedbackTask.addListener(new ITaskListener<ITask>() {

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
									if(arg != null && arg.length > 0) {
										List<Feedback> list = new ArrayList<Feedback>();
										list.addAll((List<Feedback>)arg[0]);
										CommonMethod.updateFeedback(getApplicationContext(),list);
									}
								}

								@Override
								public void onProcess(ITask task, Object[] arg) {

								}

							});
							feedbackTask.start();

							OEMVersionTask oemVTask = new OEMVersionTask(null);
							oemVTask.addListener(new ITaskListener<ITask>() {

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
									if (arg != null && arg.length > 0) {
										final OEMVersion oem = (OEMVersion) arg[0];
										OEMVersionDaoImpl daoImpl = new OEMVersionDaoImpl(LoginActivity.this);
										List<OEMVersion> list = daoImpl.find(null, "account=?", new String[] { CommUtil.getCurrentLoginAccount() },

												null, null, null, null);
										if (list != null && list.size() > 0) {
											daoImpl.delete(list.get(0).getId());
										}
										daoImpl.insert(oem, true);
									}
								}

								@Override
								public void onProcess(ITask task, Object[] arg) {

								}
							});
							oemVTask.start();
							// 选择静脉纹文件
							JxshApp.setVeinUser(CommUtil.getCurrentLoginAccount());

							//数据库版本升级检测
							checkDBUpdate();

							/*****************************************/
							String _accountTag = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,"");
							Logger.debug("AccountTag:",_accountTag);
							setTag(_accountTag);//设置推送Tag

							/*****************************************/
							isShowing = true;
							showMsg(true);//启动温馨提示
							//首次加载
							final String defaultTime = "-1";
							final String _account = CommUtil.getCurrentLoginAccount();

							//用户详情更新SysUser Info
							SysUserDetailTask sddTask = new SysUserDetailTask(null);
							sddTask.addListener(new ITaskListener<ITask>() {

								@Override
								public void onStarted(ITask task, Object arg) {

								}

								@Override
								public void onCanceled(ITask task, Object arg) {
									cancelTaskCount += 1;
									isShowing = false;
									goToHome();
								}

								@Override
								public void onFail(ITask task, Object[] arg) {
									changeLoadingStatus(true,R.string.loading_exception);
									failTaskCount += 1;
									isShowing = false;
									goToHome();
								}

								@Override
								public void onSuccess(ITask task, Object[] arg) {
									changeLoadingStatus(true,R.string.yong_hu_xin_xi_geng_xin);
									if(arg != null && arg.length > 0){
										SysUserDaoImpl sudImpl = new SysUserDaoImpl(LoginActivity.this);
										SysUser sysUser = (SysUser)arg[0];
										if (sysUser != null) {
											int enable = sysUser.getEnable();
											if (enable == 0) {
												changeLoadingStatus(false,-1);
												mUIHander.sendEmptyMessage(ACCOUNT_UNABLE);
												return;
											}else{
												sudImpl.clear();
												sudImpl.insert((SysUser)arg[0],true);
											}
										}
									}

									String _updateTime = SharedDB.loadStrFromDB(_account, ControlDefine.KEY_CUSTOMER_DATA_SYNC,defaultTime);
									if(defaultTime.equals(_updateTime)) {
										Intent intent = new Intent(LoginActivity.this,SplashActivity.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
									}

									// 跳转首页    10代表所有接口数据都已获取成功 
									succesTaskCount+=1;
									goToHome();

									/*************** customer data sync *************************/
									UpdateCustomerDataSyncTask ucdsTask = new UpdateCustomerDataSyncTask(null);
									final CustomerDataSyncHelper ucdsHelper = new CustomerDataSyncHelper(LoginActivity.this);
									ucdsTask.addListener(new ITaskListener<ITask>() {

										@Override
										public void onStarted(ITask task, Object arg) {
										}

										@Override
										public void onCanceled(ITask task, Object arg) {
											changeLoadingStatus(false,-1);
										}

										@Override
										public void onFail(ITask task, Object[] arg) {
											changeLoadingStatus(false,-1);
										}

										@Override
										public void onSuccess(ITask task, Object[] arg) {
											if(arg != null && arg.length > 0){
												List<CustomerDataSync> dataList = (List<CustomerDataSync>)arg[0];
												for(CustomerDataSync cds : dataList) {
													String model = cds.getModel();
													String whereClause = parseWhereClause(cds.getActionFieldName());
													String[] whereArgs = parseWhereArgs(cds.getActionId());
													if(isWhereClauseAndArgsMatch(cds.getActionFieldName(), cds.getActionId()) &&
															!StringUtils.isEmpty(whereClause) && !CommUtil.isEmpty(whereArgs) ) {
														ucdsHelper.doCustomerDataSyncTask(model, whereClause, whereArgs);
													}
												}
												ucdsHelper.close();
											}

											//删除数据完成后执行并发

											//用户资料更新Customer Info
											CustomerDetailTask cdTask = new CustomerDetailTask(null);
											cdTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(ITask task, Object arg) {
													//												JxshApp.showLoading(
													//														LoginActivity.this,
													//														CommDefines.getSkinManager().string(
													//																R.string.yong_hu_xin_xi_geng_xin));
												}

												@Override
												public void onCanceled(ITask task, Object arg) {
													//												JxshApp.closeLoading();
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													//												JxshApp.closeLoading();
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task, Object[] arg) {
													//												JxshApp.closeLoading();
													changeLoadingStatus(true,R.string.yong_hu_xin_xi_geng_xin);
													/****** 用户详情更新 *********/
													if(arg != null && arg.length > 0){
														CustomerDaoImpl cdImpl = new CustomerDaoImpl(LoginActivity.this);
														cdImpl.clear();
														cdImpl.insert((Customer)arg[0],true);
													}
													// 跳转首页    10代表所有接口数据都已获取成功 
													succesTaskCount +=1;

													goToHome();

												}

												@Override
												public void onProcess(ITask task, Object[] arg) {

												}
											});
											cdTask.start();

											/*****************************************/
											// 更新设备列表
											CustomerProductListTask cplTask = new CustomerProductListTask(
													null);
											cplTask.addListener(new ITaskListener<ITask>() {
												@Override
												public void onStarted(ITask task, Object arg) {
													//												JxshApp.showLoading(
													//														LoginActivity.this,
													//														CommDefines
													//																.getSkinManager()
													//																.string(R.string.she_bei_geng_xin_jian_ce));
												}

												@Override
												public void onCanceled(ITask task, Object arg) {
													//												JxshApp.closeLoading();
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													//												JxshApp.closeLoading();
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task, Object[] arg) {
													changeLoadingStatus(true,R.string.she_bei_geng_xin_jian_ce);
													/****** 设备信息更新 *********/
													if (arg != null && arg.length > 0) {
														List<CustomerProduct> customerProductList = (List<CustomerProduct>) arg[0];
														CommonMethod.updateCustomerProduct(
																LoginActivity.this,
																customerProductList);
													}
													// 跳转首页    10代表所有接口数据都已获取成功 
													succesTaskCount+=1;

													goToHome();

													/***************/
													JxshApp.closeLoading();

												}

												@Override
												public void onProcess(ITask task,
														Object[] arg) {

												}
											});
											cplTask.start();

											/*****************************************/
											// 更新产品功能列表
											ProductFunListTask pflTask = new ProductFunListTask(
													null);
											pflTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(ITask task, Object arg) {
													//												JxshApp.showLoading(
													//														LoginActivity.this,
													//														CommDefines
													//																.getSkinManager()
													//																.string(R.string.she_bei_gong_neng_geng_xin_jian_ce));
												}

												@Override
												public void onCanceled(ITask task,
														Object arg) {
													//												JxshApp.closeLoading();
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													//												JxshApp.closeLoading();
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task,
														Object[] arg) {
													changeLoadingStatus(true,R.string.she_bei_gong_neng_geng_xin_jian_ce);
													/****** 产品功能更新 *********/
													if (arg != null && arg.length > 0) {
														List<ProductFun> productFunList = (List<ProductFun>) arg[0];
														CommonMethod.updateProductFunList(
																LoginActivity.this,
																productFunList);
													}
													// 跳转首页    10代表所有接口数据都已获取成功 
													succesTaskCount+=1;

													goToHome();


												}

												@Override
												public void onProcess(ITask task,
														Object[] arg) {

												}
											});
											pflTask.start();

											/*****************************************/
											// 更新模式列表
											CustomerPatternListTask _cplTask = new CustomerPatternListTask(
													null);
											_cplTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(ITask task,
														Object arg) {
													// stub
													//												JxshApp.showLoading(
													//														LoginActivity.this,
													//														CommDefines
													//																.getSkinManager()
													//																.string(R.string.yong_hu_mo_shi_geng_xin_jian_ce));
												}

												@Override
												public void onCanceled(ITask task,
														Object arg) {
													// stub
													//												JxshApp.closeLoading();
													cancelTaskCount += 2;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task,
														Object[] arg) {
													// stub
													//												JxshApp.closeLoading();
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 2;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task,
														Object[] arg) {
													// stub
													changeLoadingStatus(true,R.string.yong_hu_mo_shi_geng_xin_jian_ce);
													/****** 模式更新 *********/
													if (arg != null
															&& arg.length > 0) {
														List<CustomerPattern> customerPatternList = (List<CustomerPattern>) arg[0];
														CommonMethod
														.updateCustomerPattern(
																LoginActivity.this,
																customerPatternList);
													}
													// 跳转首页    10代表所有接口数据都已获取成功 
													succesTaskCount+=1;

													//													goToHome();


													/*****************************************/
													// 更新产品模式操作
													StringBuffer ids = null;
													CustomerPatternDaoImpl _cpDaoImpl = new CustomerPatternDaoImpl(
															LoginActivity.this);
													List<CustomerPattern> _cps = _cpDaoImpl
															.find();
													if (_cps != null) {
														ids = new StringBuffer();
														for (int i = 0; i < _cps
																.size(); i++) {
															CustomerPattern _cp = _cps
																	.get(i);
															if (_cp != null) {
																if (i < _cps.size() - 1) {
																	ids.append(
																			_cp.getPatternId())
																	.append(",");
																} else {
																	ids.append(_cp
																			.getPatternId());
																}
															}
														}
													}

													ProductPatternOperationListTask ppolTask = new ProductPatternOperationListTask(
															null, ids
															.toString());
													ppolTask.addListener(new ITaskListener<ITask>() {

														@Override
														public void onStarted(
																ITask task,
																Object arg) {
															// method stub
															//														JxshApp.showLoading(
															//																LoginActivity.this,
															//																CommDefines
															//																		.getSkinManager()
															//																		.string(R.string.mo_shi_gong_neng_geng_xin_jian_ce));
														}

														@Override
														public void onCanceled(
																ITask task,
																Object arg) {
															// method stub
															//														JxshApp.closeLoading();
															cancelTaskCount += 1;
															isShowing = false;
															goToHome();
														}

														@Override
														public void onFail(
																ITask task,
																Object[] arg) {
															// method stub
															//														JxshApp.closeLoading();
															changeLoadingStatus(true,R.string.loading_exception);
															failTaskCount += 1;
															isShowing = false;
															goToHome();
														}

														@Override
														public void onSuccess(
																ITask task,
																Object[] arg) {
															changeLoadingStatus(true,R.string.mo_shi_gong_neng_geng_xin_jian_ce);
															// method stub
															/****** 模式操作功能更新 *********/
															if (arg != null
																	&& arg.length > 0) {
																List<ProductPatternOperation> productPatternOperationList = (List<ProductPatternOperation>) arg[0];
																CommonMethod
																.updateProductPatternOperationList(
																		LoginActivity.this,
																		productPatternOperationList);
															}
															// 跳转首页    10代表所有接口数据都已获取成功 
															succesTaskCount+=1;

															goToHome();

															//														JxshApp.closeLoading();

														}

														@Override
														public void onProcess(
																ITask task,
																Object[] arg) {

														}
													});
													ppolTask.start();

												}

												@Override
												public void onProcess(
														ITask task,
														Object[] arg) {

												}
											});
											_cplTask.start();

											/*****************************************/
											//更新产品功能明细
											FunDetailListTask fdlTask = new FunDetailListTask(null);
											fdlTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(
														ITask task,
														Object arg) {

												}

												@Override
												public void onCanceled(
														ITask task,
														Object arg) {
													//												JxshApp.closeLoading();
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(
														ITask task,
														Object[] arg) {
													//												JxshApp.closeLoading();
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(
														ITask task,
														Object[] arg) {
													//												JxshApp.closeLoading();
													changeLoadingStatus(true,R.string.geng_xin_gong_neng_ming_xi);
													if (arg != null && arg.length > 0) {
														List<FunDetail> funDetailList = (List<FunDetail>)arg[0];
														CommonMethod.updateFunDetailList(LoginActivity.this, funDetailList);
													}
													// 跳转首页    10代表所有接口数据都已获取成功 
													succesTaskCount+=1;

													goToHome();


												}

												@Override
												public void onProcess(
														ITask task,
														Object[] arg) {

												}
											});
											fdlTask.start();

											//产品功能明细配置参数列表
											FunDetailConfigTask fdcTask = new FunDetailConfigTask(null);
											fdcTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(
														ITask task,
														Object arg) {
												}

												@Override
												public void onCanceled(ITask task,Object arg) {
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(
														ITask task,
														Object[] arg) {
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(
														ITask task,
														Object[] arg) {
													changeLoadingStatus(true,R.string.gong_neng_ming_xin_pei_zhi_jian_ce);
													if (arg != null && arg.length > 0) {
														List<FunDetailConfig> funDetailConfigList = (List<FunDetailConfig>)arg[0];

														for(FunDetailConfig fc : funDetailConfigList) {
															Logger.error("tangl", fc.toString());
														}

														CommonMethod.updateFunDetailConfigList(LoginActivity.this, funDetailConfigList);
													}
													// 跳转首页    10代表所有接口数据都已获取成功 
													succesTaskCount+=1;

													goToHome();

												}

												@Override
												public void onProcess(
														ITask task,
														Object[] arg) {

												}
											});
											fdcTask.start();

											//										/*****************************************/
											// 更新云设置
											SyncCloudSettingTask csTask = new SyncCloudSettingTask(null, "get", null, null, null);
											csTask.addListener(new ITaskListener<ITask>() {
												@Override
												public void onStarted(ITask task, Object arg) {
													Logger.debug(null, "onstart...");
												}

												@Override
												public void onCanceled(ITask task, Object arg) {
													Logger.debug(null, "onCanceled...");
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													Logger.debug(null, "onFail...");
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task, Object[] arg) {
													Logger.debug(null, "onSuccess...");
													if (arg != null && arg.length > 0) {
														try {
															List<CloudSetting> csList = (List<CloudSetting>) arg[0];
															for (CloudSetting cloudSetting : csList) {
															}
															CommonMethod.updateCloudSettings(LoginActivity.this, csList);
														} catch (Exception e) {
															Logger.error("loginactivity.tangl", "class cast error");
															e.printStackTrace();
														}
													}

													// 跳转首页    10代表所有接口数据都已获取成功 
													succesTaskCount+=1;

													goToHome();

												}

												@Override
												public void onProcess(ITask task, Object[] arg) {
													Logger.debug(null, "onProcess...");
												}
											});
											csTask.start();
											/*****************************************/
											//更新定时任务列表
											CustomerTimerListForLoginTask ctlTask = new CustomerTimerListForLoginTask(null);
											ctlTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(ITask task, Object arg) {
													Logger.debug("Yang", "task list");
												}

												@Override
												public void onCanceled(ITask task, Object arg) {
													Logger.debug(null, "onCanceled...");
													cancelTaskCount += 2;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 2;
													isShowing = false;
													goToHome();
													Logger.debug("Yang", "task onFail...");
												}
												@Override
												public void onSuccess(ITask task, Object[] arg) {
													changeLoadingStatus(true,R.string.geng_xin_ding_shi_list);
													//////////////
													if (arg != null && arg.length > 0) {
														List<CustomerTimer> ctList = (List<CustomerTimer>) arg[0];
														CommonMethod.updateCustomerTimer(LoginActivity.this, ctList);
													}
													succesTaskCount += 1;
													/*****************************************/
													//更新定时任务操作列表
													StringBuffer taskIds = null;
													CustomerTimerTaskDaoImpl _cttDaoImpl = new CustomerTimerTaskDaoImpl(LoginActivity.this);
													List<CustomerTimer> _ctList = _cttDaoImpl.find();
													if (_ctList != null) {
														taskIds = new StringBuffer();
														for (int i = 0; i < _ctList.size(); i++) {
															CustomerTimer _ct = _ctList.get(i);
															if (_ct != null) {
																if (i<_ctList.size() - 1) {
																	taskIds.append(_ct.getTaskId()).append(",");
																}else{
																	taskIds.append(_ct.getTaskId());
																}
															}
														}
													}
													TimerTaskOperationListForLoginTask ttoplTask = new 
															TimerTaskOperationListForLoginTask(
																	null, "0");
													ttoplTask.addListener(new ITaskListener<ITask>() {

														@Override
														public void onStarted(ITask task, Object arg) {
															Logger.debug("Yang", "Operation list");
														}

														@Override
														public void onCanceled(ITask task,
																Object arg) {
															cancelTaskCount += 1;
															isShowing = false;
															goToHome();
														}

														@Override
														public void onFail(ITask task, Object[] arg) {
															changeLoadingStatus(true,R.string.loading_exception);
															failTaskCount += 1;
															isShowing = false;
															goToHome();
															Logger.debug("Yang", "Operation onFail...");
														}

														@Override
														public void onSuccess(ITask task,
																Object[] arg) {
															changeLoadingStatus(true,R.string.geng_xin_ding_shi_cao_zuo_list);

															if (arg != null&& arg.length > 0) {
																List<TimerTaskOperation> ttoList = (List<TimerTaskOperation>) arg[0];
																CommonMethod.updateTimerTaskOperationList(
																		LoginActivity.this, ttoList);
															}
															// 跳转首页    10代表所有接口数据都已获取成功 
															succesTaskCount += 1;

															goToHome();

														}

														@Override
														public void onProcess(ITask task,
																Object[] arg) {

														}
													});
													ttoplTask.start();
												}

												@Override
												public void onProcess(ITask task, Object[] arg) {

												}
											});
											ctlTask.start();

											/*************** update customer area *************************/
											Logger.warn(null, "更新用户区域设置");
											UpdateCustomerAreaTask ucaTask = new UpdateCustomerAreaTask(null, -1);
											final CustomerAreaDaoImpl caDao = new CustomerAreaDaoImpl(getApplicationContext());
											ucaTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(ITask task, Object arg) {
												}

												@Override
												public void onCanceled(ITask task, Object arg) {
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task, Object[] arg) {
													changeLoadingStatus(true,R.string.mode_group_list);
													if(arg != null && arg.length > 0){
														List<CustomerArea> cas = (List<CustomerArea>)arg[0];
														for(CustomerArea ca : cas) {
															try {
																caDao.saveOrUpdate(ca);
															} catch (Exception e) {
																e.printStackTrace();
															}
														}
													}
													succesTaskCount += 1;

													goToHome();

												}

												@Override
												public void onProcess(ITask task, Object[] arg) {
												}
											});
											ucaTask.start();
											/*************** update customer area end *************************/

											/*************** update customer product area *************************/
											Logger.warn(null, "更新设备用户区域设置");
											UpdateCustomerProductAreaTask upcaTask = new UpdateCustomerProductAreaTask(null);
											final CustomerProductAreaDaoImpl cpaDao = new CustomerProductAreaDaoImpl(getApplicationContext());
											upcaTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(ITask task, Object arg) {
												}

												@Override
												public void onCanceled(ITask task, Object arg) {
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task, Object[] arg) {
													if(arg != null && arg.length > 0){
														List<CustomerProductArea> cpas = (List<CustomerProductArea>)arg[0];
														for(CustomerProductArea cpa : cpas) {
															try {
																cpaDao.saveOrUpdate(cpa);
															} catch (Exception e) {
																e.printStackTrace();
															}
														}
													}
													succesTaskCount += 1;

													goToHome();

												}

												@Override
												public void onProcess(ITask task, Object[] arg) {
												}
											});
											upcaTask.start();
											/*************** update customer product area end *************************/

											Logger.warn(null, "更新产品注册信息");
											UpdateProductRegisterTask uprTask = new UpdateProductRegisterTask(null);
											final ProductRegisterDaoImpl uprDao = new ProductRegisterDaoImpl(getApplicationContext());
											uprTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(ITask task, Object arg) {
												}

												@Override
												public void onCanceled(ITask task, Object arg) {
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task, Object[] arg) {
													if(arg != null && arg.length > 0){
														List<ProductRegister> prs = (List<ProductRegister>)arg[0];
														for(ProductRegister pr : prs) {
															try {
																uprDao.saveOrUpdate(pr);
															} catch (Exception e) {
																e.printStackTrace();
															}
														}
													}
													succesTaskCount += 1;

													goToHome();

												}

												@Override
												public void onProcess(ITask task, Object[] arg) {
												}
											});
											uprTask.start();

											Logger.warn(null, "更新遥控配置信息2");
											UpdateProductRemoteConfigTask uprcTask = new UpdateProductRemoteConfigTask(null);
											final ProductRemoteConfigDaoImpl uprcDao = new ProductRemoteConfigDaoImpl(getApplicationContext());
											uprcTask.addListener(new TaskListener<ITask>() {

												@Override
												public void onCanceled(ITask task, Object arg) {
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													Logger.warn(null, "onFail");
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task, Object[] arg) {
													Logger.warn(null, "onSuccess");
													if(arg != null && arg.length > 0){
														List<ProductRemoteConfig> prs = (List<ProductRemoteConfig>)arg[0];
														for(ProductRemoteConfig pr : prs) {
															try {
																uprcDao.saveOrUpdate(pr);
															} catch (Exception e) {
																e.printStackTrace();
															}
														}
													}
													succesTaskCount += 1;

													goToHome();

												}

												@Override
												public void onProcess(ITask task, Object[] arg) {
												}
											});
											uprcTask.start();

											Logger.warn(null, "更新遥控配置信息");
											UpdateProductRemoteConfigFunTask uprcfTask = new UpdateProductRemoteConfigFunTask(null);
											final ProductRemoteConfigFunDaoImpl uprcfDao = new ProductRemoteConfigFunDaoImpl(getApplicationContext());
											uprcfTask.addListener(new TaskListener<ITask>() {

												@Override
												public void onCanceled(ITask task, Object arg) {
													cancelTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onFail(ITask task, Object[] arg) {
													changeLoadingStatus(true,R.string.loading_exception);
													failTaskCount += 1;
													isShowing = false;
													goToHome();
												}

												@Override
												public void onSuccess(ITask task, Object[] arg) {
													if(arg != null && arg.length > 0){
														List<ProductRemoteConfigFun> prs = (List<ProductRemoteConfigFun>)arg[0];
														for(ProductRemoteConfigFun pr : prs) {
															try {
																uprcfDao.saveOrUpdate(pr);
															} catch (Exception e) {
																e.printStackTrace();
															}
														}
													}
													succesTaskCount += 1;

													goToHome();

												}
											});
											uprcfTask.start();
										}


										@Override
										public void onProcess(ITask task, Object[] arg) {
										}
									});
									ucdsTask.start();
									/*************** customer data sync end *************************/

								}

								@Override
								public void onProcess(ITask task, Object[] arg) {
								}
							});
							sddTask.start();
							/*****************************************/

						}

						@Override
						public void onProcess(ITask task, Object[] arg) {

						}
					});
					this.loginTask.start();//开始登录
				}
			}

			//			goToHome();
			break;
		case R.id.tv_set_voice_login://设置使用声纹登录
			if (TextUtils.isEmpty(mAuthId)) {
				isOpenVoiceLogin = SharedDB.loadBooleanFromDB(mAuthId, ControlDefine.KEY_VOICE_LOCK, false);
			}
			if (isOpenVoiceLogin) {
				changeLoginUI(true);
			}else{
				JxshApp.showToast(LoginActivity.this, "当前帐号未开启声纹登录，请使用密码登录");
			}
			break;
		case R.id.tv_set_pwd_login://设置使用密码登录
			if (!TextUtils.isEmpty(mAuthId)) {
				isOpenVoiceLogin = SharedDB.loadBooleanFromDB(mAuthId, ControlDefine.KEY_VOICE_LOCK, false);
			}
			changeLoginUI(false);
			break;
		case R.id.tv_more:
			showMoreDialog();
			break;
		}
	}

	/**
	 * 改变loading 效果
	 * 
	 * @param isLoading
	 */
	private void changeLoadingStatus(boolean isLoading, int stringID) {
		Message message = new Message();
		message.what = 0;
		Bundle _bundle = new Bundle();
		_bundle.putBoolean("isLoading", isLoading);
		_bundle.putInt("stringID", stringID);
		message.obj = _bundle;
		this.mUIHander.sendMessage(message);
	}

	/**
	 * 温馨提示配置
	 */
	final static List MSG_LIST = Arrays.asList("提示：定期修改密码和密保，可以使我们的账号更加安全。", "提示：设置九宫格可以降低应用被盗用的风险。",
			"提示：首页中\"常用模式\"为经常使用或场景中的置顶项。", "提示：第一次听音乐需要先选择功放设备，音源和扬声器。", "提示：定时功能可以同时配置模式和设备哟。", "提示：调色灯可以进行勾选批量控制。",
			"小技巧：在设备列表中可以选择最多4个设备放到首页\"常用设备\"中。", "小技巧：按照自己的使用习惯修改设备名字，控制设备更惬意。", "小技巧：在云平台设置区域分组之后，将更容易找到想要使用的设备。");

	private void showMoreDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.customer_login_dialog, new LinearLayout(context), false);
		TextView switchUser = (TextView) dialogView.findViewById(R.id.tv_switch_user);
		TextView forgetPwd = (TextView) dialogView.findViewById(R.id.tv_forget_password);
		TextView tvCancle = (TextView) dialogView.findViewById(R.id.tv_cancle);
		final Dialog dialog = BottomDialogHelper.showDialogInBottom(context, dialogView, null);
		if (JxshApp.getHistoryUserList().size() > 1) {
			switchUser.setVisibility(View.VISIBLE);
		}

		switchUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent _intentSU = new Intent(LoginActivity.this, SelectUserActivity.class);
				startActivity(_intentSU);
				finish();
				dialog.dismiss();
			}
		});

		forgetPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转找回密码界面
				Intent _intent = new Intent(LoginActivity.this, ConfirmAccountActivity.class);
				startActivity(_intent);
				dialog.dismiss();
			}
		});
		tvCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 是否正在显示温馨提示
	 */
	private boolean isShowing = false;

	/**
	 * 显示温馨提示
	 * 
	 * @param isFirst
	 *            是否第一次提示
	 */
	private void showMsg(boolean isFirst) {
		Random _rd = new Random();
		int _index = _rd.nextInt(MSG_LIST.size());
		Message message = new Message();
		message.what = 101;
		message.obj = MSG_LIST.get(_index);
		if (isFirst)
			this.mUIHander.sendMessageDelayed(message, 600);
		else
			this.mUIHander.sendMessageDelayed(message, 15000);
	}

	/**
	 * 跳转首页
	 */
	private void goToHome() {
		// //初始化设备类型
		// JxshApp.showLoading(
		// LoginActivity.this,
		// CommDefines
		// .getSkinManager()
		// .string(R.string.she_bei_chu_shi_hua));
		// CommonMethod.initTypeDevice(LoginActivity.this);
		// JxshApp.closeLoading();
		/*** 跳转策略 **********/
		if (cancelTaskCount + failTaskCount + succesTaskCount != 16) {// 未达到16项
			return;
		} else if (cancelTaskCount > 0 || failTaskCount > 0) {
			// 载入失败
			Intent intent = new Intent(SplashActivity.ACTION_LOAD_FAIL);
			sendBroadcast(intent);
			changeLoadingStatus(false, -1);
			return;
		}
		/***************/
		SysUserDaoImpl sudImpl = new SysUserDaoImpl(LoginActivity.this);
		List<SysUser> userList = sudImpl.find();
		SysUser _sUser = new SysUser();
		if (userList != null && userList.size() > 0) {
			_sUser = userList.get(0);
		}
		String _subAccount = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_SUNACCOUNT, "");
		if (TextUtils.isEmpty(_sUser.getQuestion1()) && // 主账号密保为空时强制设置密保
				TextUtils.isEmpty(_sUser.getQuestion2()) && TextUtils.isEmpty(_subAccount)) {
			JxshApp.showToast(LoginActivity.this, getString(R.string.set_safe_question_tips));
			Intent intent = new Intent(LoginActivity.this, ChangeSafeQuestionActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			changeLoadingStatus(false, -1);
		} else {
			changeLoadingStatus(true, R.string.she_bei_chu_shi_hua);
			String account = CommUtil.getCurrentLoginAccount();
			if (!TextUtils.isEmpty(account)) {
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_ISLOADING, true);
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_OFF_LINE_MODE, false);
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_NOT_FIRST_LOGING, true);
			}
			/*** zj:广告假数据测试 ********/
			List<Advertising> adList = new ArrayList<Advertising>();
			Advertising _ad = new Advertising();
			_ad.setPath("ad/ad/pic1.png");
			adList.add(_ad);
			_ad = new Advertising();
			_ad.setPath("ad/ad/pic2.png");
			adList.add(_ad);
			_ad = new Advertising();
			_ad.setPath("ad/ad/pic3.png");
			adList.add(_ad);
			ADManager.instance().setAdvertisingList(adList);
			/***************************/

			JxshApp.getHandler().postDelayed(new Runnable() {

				@Override
				public void run() {
					JxshApp.instance.initBellController();// 启动门铃
				}
			}, 1500);

			// 跳转首页
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 设置推送组Tag
	 */
	private void setTag(String account) {
		if (StringUtils.isEmpty(account)) {
			return;
		}
		if (JPushInterface.isPushStopped(getApplicationContext())) {
			JPushInterface.resumePush(getApplicationContext());// stopPush只后必须重新开启Jpush
		}
		// 设置Alias
		String alias = account;
		mUIHander.sendMessage(mUIHander.obtainMessage(MSG_SET_ALIAS, alias));
		// 设置Tag
		String tag = account;
		// ","隔开的多个 转换成 Set
		String[] sArray = tag.split(",");
		Set<String> tagSet = new LinkedHashSet<String>();
		for (String sTagItme : sArray) {
			if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
				return;
			}
			tagSet.add(sTagItme);
		}
		// 调用JPush API设置Tag
		mUIHander.sendMessage(mUIHander.obtainMessage(MSG_SET_TAGS, tagSet));

	}

	// 设置Alias的回调方法
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Logger.info(TAG, logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Logger.info(TAG, logs);
				if (ExampleUtil.isConnected(getApplicationContext())) {
					mUIHander.sendMessageDelayed(mUIHander.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
				} else {
					Logger.info(TAG, "No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Logger.error(TAG, logs);
			}

			// ExampleUtil.showToast(logs, getApplicationContext());
		}

	};
	// 设置Tag的回调方法
	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Logger.info(TAG, logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Logger.info(TAG, logs);
				if (ExampleUtil.isConnected(getApplicationContext())) {
					mUIHander.sendMessageDelayed(mUIHander.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
				} else {
					Logger.info(TAG, "No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Logger.error(TAG, logs);
			}

			// ExampleUtil.showToast(logs, getApplicationContext());
		}

	};
	///////////////////////////////////

	private String parseWhereClause(String whereStr) {
		if (!StringUtils.isEmpty(whereStr)) {
			String[] wheres = whereStr.split(",");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < wheres.length; i++) {
				if (i == wheres.length - 1) {
					sb.append(" " + wheres[i] + "=? ");
				} else {
					sb.append(" " + wheres[i] + "=? and ");
				}
			}
			return sb.toString();
		}
		return "";
	}

	private String[] parseWhereArgs(String whereArgs) {
		if (!StringUtils.isEmpty(whereArgs)) {
			return whereArgs.split(",");
		}
		return null;
	}

	private boolean isWhereClauseAndArgsMatch(String WhereClause, String whereArgs) {
		if (!StringUtils.isEmpty(WhereClause) && !StringUtils.isEmpty(whereArgs)) {
			String[] cluses = WhereClause.split(",");
			String[] args = whereArgs.split(",");
			if (cluses != null && args != null && cluses.length == args.length) {
				return true;
			}
		}
		return false;
	}

	// 执行模型操作
	private void performModelOperation(String operation, SpeechListener listener, String account) {
		// 清空参数
		mVerify.setParameter(SpeechConstant.PARAMS, null);
		mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + 3);

		// if (pwdType == PWD_TYPE_TEXT) {
		// // 文本密码删除需要传入密码
		// if (TextUtils.isEmpty(mTextPwd)) {
		// JxshApp.showToast(context, "请获取密码后进行操作");
		// return;
		// }
		// mVerify.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
		// } else if (pwdType == PWD_TYPE_FREE) {
		// mVerify.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
		// } else if (pwdType == PWD_TYPE_NUM) {
		//
		// }
		mVerify.sendRequest(operation, account, listener);
	}

	/**
	 * 声音模型查询、删除监听
	 */
	SpeechListener mModelOperationListener = new SpeechListener() {

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {

			String result = new String(buffer);
			try {
				JSONObject object = new JSONObject(result);
				String cmd = object.getString("cmd");
				int ret = object.getInt("ret");

				if ("del".equals(cmd)) {
					if (ret == ErrorCode.SUCCESS) {
						JxshApp.showToast(context, "删除成功");
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						JxshApp.showToast(context, "删除失败，未注册声音");
					}
				} else if ("que".equals(cmd)) {
					if (ret == ErrorCode.SUCCESS) {
						Logger.debug(null, "声纹模型存在");
						Intent _in = new Intent(context, VoiceLoginActivity.class);
						_in.putExtra("account", JxshApp.lastInputID);
						startActivity(_in);
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						JxshApp.showLoading(context, "该帐号未开启声纹锁，请使用密码登录");
						// JxshApp.showToast(context,
						// getResources().getString(R.string.open_voice_unreg_tips));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onCompleted(SpeechError error) {

			if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
				JxshApp.showToast(context, "该帐号未开启声纹锁，请使用密码登录");
			}
		}
	};

	/**
	 * 数据库版本升级检测（跨版本升级策略）
	 */
	private void checkDBUpdate() {
		// final String _account =
		// SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
		// ControlDefine.KEY_ACCOUNT,"");
		// final String secretkey =
		// SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,ControlDefine.KEY_SECRETKEY,"");
		// final String nickyName =
		// SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,ControlDefine.KEY_NICKNAME,"");
		// DBHelper.setDbName(_account);
		Logger.debug("Yang", "checkDBUpdate");

		DBHelper dBHelper = new DBHelper(LoginActivity.this);
		// dBHelper.addDBHelperListener(new DBHelperListener() {
		// @Override
		// public void onUpgrade(int oldVersion, int newVersion) {
		// JxshApp.showToast(LoginActivity.this,
		// CommDefines.getSkinManager().string(R.string.update_db));
		// SharedDB.removeAct(JxshApp.getContext(), _account);
		// SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
		// ControlDefine.KEY_ACCOUNT, _account);
		// SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
		// ControlDefine.KEY_SECRETKEY,secretkey);
		// // save key to personal property
		// SharedDB.saveStrDB(_account,
		// ControlDefine.KEY_SECRETKEY,secretkey);
		// //存入别名：
		// SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
		// ControlDefine.KEY_NICKNAME,nickyName);
		// }
		//
		// });
		// dBHelper.addDBHelperListener(new DBHelperListener() {
		//
		// @Override
		// public void onUpgrade(SQLiteDatabase db, int oldVersion, int
		// newVersion) {
		// // TODO Auto-generated method stub
		// JxshApp.showToast(LoginActivity.this,
		// CommDefines.getSkinManager().string(R.string.update_db));
		// int upgradeVersion = oldVersion;
		// Logger.debug("Yang", "db onUpgrade");
		// Logger.debug("Yang", "upgradeVersion="+ upgradeVersion + "|"+
		// newVersion);
		//// if(11 == upgradeVersion){//11升12策略
		//// new From11To12DBupdateStrategy().updateDBStrategy(db);
		//// upgradeVersion = 12;
		//// }
		//// if(12 == upgradeVersion){//12升13策略
		//// upgradeVersion = 13;
		//// }
		// if (25 == upgradeVersion) {//数据库版本25升26策略
		// Logger.debug("YANG", "upgradeVersion");
		// new From25To26DBupdateStrategy().updateDBStrategy(db);
		// upgradeVersion = 26;
		// }
		// if (upgradeVersion != newVersion) { //不符合升级策略，清空数据库表
		// new ClearDBupdateStrategy().updateDBStrategy(db);
		// }
		// }
		// });
		dBHelper.getWritableDatabase().close();
	}

	// private void saveGatewayData(){
	// SharedPreferences sharedPref = getSharedPreferences("default_ip",
	// MODE_PRIVATE);
	// SharedPreferences.Editor editor = sharedPref.edit();
	// editor.putString("gatewayIP", editip.getText().toString());
	// editor.putInt("gatewayPort",Integer.parseInt(editport.getText().toString()));
	// editor.commit();
	//
	// }

	private void setTimerTask() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				doActionHandler.sendMessage(message);
			}
		}, 1000, 60000/* 表示1000毫秒之後，每隔60000毫秒,即每分钟執行一次 */);
	}

	/**
	 * do some action
	 */
	private Handler doActionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int msgId = msg.what;
			switch (msgId) {
			case 1:
				// do some action
				try {
					// String id_account = CommUtil.getCurrentLoginAccount();
					String id_account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,
							"");

					if (JxshApp.IpFlag == true && !id_account.equals("")) {
						// 先推送消息到大网关
						// String account = CommUtil.getCurrentLoginAccount();

						String account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,
								"");

						String msgconts = account + "getwayip" + "(3700)";
						MessageQueue2.getInstance().offer(new BroadPushMessage(msgconts, id_account));
						Log.i("pushmsg", "--------------------------" + msgconts);

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			default:
				break;
			}
		}
	};

}
