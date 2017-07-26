package com.jinxin.jxsmarthome.fragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.datan.net.command.VersionUpgradeTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.SysUserDaoImpl;
import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.ConnectionControlActivity;
import com.jinxin.jxsmarthome.activity.FeedbackListActivity;
import com.jinxin.jxsmarthome.activity.MessageNotifyActivity;
import com.jinxin.jxsmarthome.activity.MyDeviceActivity;
import com.jinxin.jxsmarthome.activity.PersonalCenterInfo;
import com.jinxin.jxsmarthome.activity.RelativeAccountActivity;
import com.jinxin.jxsmarthome.activity.SafeCenteractivity;
import com.jinxin.jxsmarthome.activity.SettingsActivity;
import com.jinxin.jxsmarthome.activity.SubAccountActivity;
import com.jinxin.jxsmarthome.activity.VersionInfo;
import com.jinxin.jxsmarthome.activity.VoiceLockActivity;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.entity.VersionVO;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.record.FileManager;
import com.jinxin.record.FileUtil;
import com.jinxin.record.SharedDB;

public class MyAccountFragment extends Fragment implements OnClickListener {
	
	private LinearLayout itemPersonalInfo = null;
	private RelativeLayout itemMyDevice = null;
	private RelativeLayout itemSubAccout = null;
	private RelativeLayout itemSafeCenter = null;
	private RelativeLayout itemCloudSetting = null;
	private RelativeLayout itemCurrentVersion = null;
	private RelativeLayout itemFeedback = null;
	private RelativeLayout itemCleanCache = null;
	private RelativeLayout itemRelativeAccount = null;
	private RelativeLayout itemConnectionDevice = null;
	
	private TextView tvCustomerName = null;
	private TextView tvAgentId = null;
	private CheckBox cbNotice = null;
	private Button btnUserLogout = null;
	private ImageView ivNewVersion;
	
	private int versionNew = 0;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				//是否显示新版本提醒
				if (versionNew < 0) {
					ivNewVersion.setVisibility(View.VISIBLE);
				}else{
					ivNewVersion.setVisibility(View.INVISIBLE);
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_account_layout, null);
		
		initView(view);
		initData();
		return view;
	}
	
	private void initView(View view) {
		itemPersonalInfo = (LinearLayout) view.findViewById(R.id.itemPersonalInfo);
		itemMyDevice = (RelativeLayout) view.findViewById(R.id.itemMyDevice);
		itemSubAccout = (RelativeLayout) view.findViewById(R.id.itemSubAccout);
		itemSafeCenter = (RelativeLayout) view.findViewById(R.id.itemSafeCenter);
		itemCloudSetting = (RelativeLayout) view.findViewById(R.id.itemCloudSetting);
		itemCurrentVersion = (RelativeLayout) view.findViewById(R.id.itemCurrentVersion);
		itemFeedback = (RelativeLayout) view.findViewById(R.id.itemFeedback);
		itemCleanCache = (RelativeLayout) view.findViewById(R.id.itemCleanCache);
		itemRelativeAccount = (RelativeLayout) view.findViewById(R.id.itemRelativeAccount);
		itemConnectionDevice = (RelativeLayout) view.findViewById(R.id.itemConnectionDevice);
		
		cbNotice = (CheckBox) view.findViewById(R.id.cb_notice_switch);
		ivNewVersion = (ImageView) view.findViewById(R.id.iv_version_new);
		
		tvCustomerName = (TextView) view.findViewById(R.id.my_account_name);
		tvAgentId = (TextView) view.findViewById(R.id.my_account_id);
		btnUserLogout = (Button) view.findViewById(R.id.user_logout);
		
		itemPersonalInfo.setOnClickListener(this);
		itemMyDevice.setOnClickListener(this);
		itemSubAccout.setOnClickListener(this);
		itemSafeCenter.setOnClickListener(this);
		itemCloudSetting.setOnClickListener(this);
		itemCurrentVersion.setOnClickListener(this);
		itemFeedback.setOnClickListener(this);
		itemCleanCache.setOnClickListener(this);
		btnUserLogout.setOnClickListener(this);
		itemRelativeAccount.setOnClickListener(this);
		itemConnectionDevice.setOnClickListener(this);
		
		view.findViewById(R.id.rl_message_receive_set).setOnClickListener(this);
		
		//如果是子账号登录，隐藏该布局
		if (CommUtil.isSubaccount()) {
			itemSubAccout.setVisibility(View.GONE);
		}
		
		
		final String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT, "");
		cbNotice.setChecked(SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_NOTICE_ON_OFF, true));
		
		cbNotice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
						ControlDefine.KEY_ACCOUNT, "");
				if (!TextUtils.isEmpty(_account)){
					SharedDB.saveBooleanDB(_account, ControlDefine.KEY_NOTICE_ON_OFF, isChecked);
				}
				if (isChecked) {
					startActivity(MessageNotifyActivity.class);
				}
			}
		});
	}
	
	private void initData() {
		SysUserDaoImpl suImpl= new SysUserDaoImpl(getActivity());
		List<SysUser> _customers = suImpl.find();
		if (_customers != null) {
			for (SysUser cus : _customers) {
				tvCustomerName.setText(cus.getUserName());
				tvAgentId.setText("ID:"+cus.getAccount());
			}
		}
		
		//检测版本号
		checkVersion();
	}

	// 版本检测
	private void checkVersion() {
		final String version = CommUtil.getPackageInfo(getActivity(),
				getActivity().getPackageName()).versionName;
		VersionUpgradeTask upgradeTask = new VersionUpgradeTask(getActivity(),
				version, ControlDefine.APP_TYPE_ANDROID_PHONE);
		upgradeTask.addListener(new ITaskListener<ITask>() {

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
					versionNew = compareVersion(version, _vo.getAppVersion());
					handler.sendEmptyMessage(0);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		upgradeTask.start();
	}
	
	private void startActivity(Class<?> clasz) {
		Intent intent = new Intent(getActivity(), clasz);
		startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.itemPersonalInfo:// 个人信息
			startActivity(PersonalCenterInfo.class);
			break;
		case R.id.itemMyDevice:// 我的设备
			startActivity(MyDeviceActivity.class);
			break;
		case R.id.itemSafeCenter:// 安全中心
			startActivity(SafeCenteractivity.class);
			break;
		case R.id.itemCloudSetting:// 云设置
			startActivity(SettingsActivity.class);
			break;
		case R.id.itemRelativeAccount://亲情账号
			startActivity(RelativeAccountActivity.class);
			break;
		case R.id.itemCurrentVersion:// 当前版本
			startActivity(VersionInfo.class);
			break;
		case R.id.itemSubAccout:// 子账号
			startActivity(SubAccountActivity.class);
			break;
		case R.id.itemConnectionDevice:// 我的关联
			startActivity(ConnectionControlActivity.class);
			break;
		case R.id.itemFeedback:// 意见反馈
			startActivity(FeedbackListActivity.class);
			break;
		case R.id.user_logout:// 退出账号
			JxshApp.instance.logout();
			getActivity().finish();
			break;
		case R.id.itemCleanCache:// 清理缓存
			final MessageBox mb = new MessageBox(getActivity(),
					"清除缓存", "清除本地图片和数据缓存，确定要清除吗？",
					MessageBox.MB_OK | MessageBox.MB_CANCEL);
			mb.setButtonText("确定", null);
			mb.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					switch(mb.getResult()){
					case MessageBox.MB_OK://点击消失
						mb.dismiss();
						JxshApp.showLoading(getActivity(), "正在清理...");
						delectCache();
						
						//发送消息到主线程弹出提示对话框
						Message msg = JxshApp.getHandler().obtainMessage();
						msg.obj = getActivity();
						Bundle bundle = new Bundle();
						bundle.putString("error", "同步数据，请重新登录");
						msg.setData(bundle);
						msg.what = JxshApp.EXIT_MESSAGE_RELOGIN;
//						JxshApp.getHandler().sendMessage(msg);
						msg.sendToTarget();
						break;
					}
				}
			});
			mb.show();
			break;
		case R.id.rl_message_receive_set://定时消息接收
			if (cbNotice.isChecked()) {
				startActivity(MessageNotifyActivity.class);
			}else{
				JxshApp.showToast(getActivity(), "请先打开消息提醒");
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 删除本地缓存文件和数据
	 */
	private void delectCache(){
		Map<String, Object> veinMap = backUpVeinSettings();
		
		SharedDB.removeAct(getActivity(), CommUtil.getCurrentLoginAccount());//删除配置表
		DBHelper.deleteDatabase(getActivity());//删除本地数据库
		FileManager _fm = new FileManager();
		if (_fm.getSDPath() != null) {
			final String filePath =_fm.getSDPath() + FileManager.PROJECT_NAME +FileManager.CACHE;
			FileUtil.checkDirectory(filePath);
			try {//清文件缓存
				JxshApp.instance.getFinalBitmap().clearCache();//清图片缓存
				JxshApp.instance.getFinalBitmap().clearMemoryCache();
				JxshApp.instance.getFinalBitmap().clearDiskCache();
				FileUtil.delFiles(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			JxshApp.closeLoading();
			
		}
		JxshApp.showToast(getActivity(), "缓存已清除");
		
		restoreVeinSettings(veinMap);
	}
	
	private Map<String,Object> backUpVeinSettings() {
		String _account = CommUtil.getCurrentLoginAccount();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put(ControlDefine.KEY_ENABLE_PASSWORD_VEIN, SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_VEIN, false));
		map.put(ControlDefine.KEY_VEIN_ONLY, SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_VEIN_ONLY, false));
		map.put(ControlDefine.KEY_PASSWORD, SharedDB.loadStrFromDB(_account, ControlDefine.KEY_PASSWORD, ""));
		return map;
	}
	
	private void restoreVeinSettings(Map<String,Object> map) {
		String _account = CommUtil.getCurrentLoginAccount();
		SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_VEIN, (Boolean) map.get(ControlDefine.KEY_ENABLE_PASSWORD_VEIN));
		SharedDB.saveBooleanDB(_account, ControlDefine.KEY_VEIN_ONLY, (Boolean) map.get(ControlDefine.KEY_VEIN_ONLY));
		SharedDB.saveStrDB(_account, ControlDefine.KEY_PASSWORD, (String) map.get(ControlDefine.KEY_PASSWORD));
	}
	
	/**
	 * 版本号比较
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2) {
		if (version1.equals(version2)) {
			return 0;
		}
		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");
		int index = 0;
		int minLen = Math.min(version1Array.length, version2Array.length);
		int diff = 0;
		while (index < minLen
				&& (diff = Integer.parseInt(version1Array[index])
						- Integer.parseInt(version2Array[index])) == 0) {
			index++;
		}
		if (diff == 0) {
			for (int i = index; i < version1Array.length; i++) {
				if (Integer.parseInt(version1Array[i]) > 0) {
					return 1;
				}
			}
			for (int i = index; i < version2Array.length; i++) {
				if (Integer.parseInt(version2Array[i]) > 0) {
					return -1;
				}
			}
			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}
}
