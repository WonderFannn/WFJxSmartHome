package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.command.SyncCloudSettingTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.SpeakerSettingsActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonData;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.popupwindow.ShowSuggestWindows;
import com.jinxin.jxsmarthome.ui.widget.MapTextView;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 云设置模块
 * 		模块逻辑：
 * 		1.登录时同步数据到数据库
 * 		2.进入模块从数据库中读取设置，没有则设置默认值
 * 		3.点击同步时：保存数据到数据库，并同步到平台
 * @author  TangLong
 * @company 金鑫智慧
 */
public class SettingsFragment extends Fragment implements OnClickListener,
RadioListDialogFragment.OnItemClickListener,OnCheckedChangeListener {
	private List<CloudSetting> csList;
	private MapTextView mInput;
	private MapTextView mSpeaker;
	private MapTextView mNineDelay;
	private MapTextView mWakeWord;
	private CheckBox mOfflineSwitch;
	private CheckBox mVoiceSendSwitch;
	private CheckBox mApplySubSwitch;
	private CheckBox shakeCheck;
	private CheckBox mCustomWakeSwitch;
	private LinearLayout mLiApplySub;
	private BroadcastReceiver mReceiver;
	private MessageBox _mBox;//切换唤醒方式提示框
	//	private ArrayList<String> list = new ArrayList<>();
	private Boolean ManualInto;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container, false);
		ManualInto = true;
		_mBox = null;
		setHasOptionsMenu(true);
		initParam();
		initView(view);
		initData();
		initBroadcastReceiver();

		//新手引导
		if (CommUtil.isSubaccount()) {
			((BaseActionBarActivity)this.getActivity()).suggestWindow = new ShowSuggestWindows(getActivity(), R.drawable.bg_guide_cloud_setting, "");
			((BaseActionBarActivity)this.getActivity()).suggestWindow.showFullWindows("SettingsFragment",R.drawable.bg_guide_cloud_setting_for_sub);
		}else{
			((BaseActionBarActivity)this.getActivity()).suggestWindow = new ShowSuggestWindows(getActivity(), R.drawable.bg_guide_cloud_setting, "");
			((BaseActionBarActivity)this.getActivity()).suggestWindow.showFullWindows("SettingsFragment",R.drawable.bg_guide_cloud_setting);
		}

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_cloud_settings, menu);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 4 && resultCode == Activity.RESULT_OK && data != null) {
			resetSpeakerSetting(data.getExtras());
		}
	}

	private void resetSpeakerSetting(Bundle data) {
		if(data != null) {
			CloudSetting speakerSetting = (CloudSetting) data.getSerializable("speakerSetting");
			for(CloudSetting cs : csList) {
				if(cs.getCategory().equals(speakerSetting.getCategory()) && cs.getItems().equals(speakerSetting.getItems())) {
					cs.setParams(speakerSetting.getParams());
					return;
				}
			}
			csList.add(speakerSetting);
		}
	}

	/**
	 * 初始化参数 
	 */
	private void initParam() {
		csList = new ArrayList<CloudSetting>();
	}

	/**
	 * 初始化视图
	 */
	private void initView(View view) {
		mInput = (MapTextView)view.findViewById(R.id.setting_music_input);
		mSpeaker = (MapTextView)view.findViewById(R.id.setting_music_speaker);
		mWakeWord = (MapTextView) view.findViewById(R.id.setting_wake_word);
		mNineDelay = (MapTextView)view.findViewById(R.id.setting_nine_delay);
		mOfflineSwitch = (CheckBox)view.findViewById(R.id.setting_enable_offline_switch);
		mVoiceSendSwitch = (CheckBox)view.findViewById(R.id.setting_voice_send_switch);
		mApplySubSwitch = (CheckBox)view.findViewById(R.id.setting_apply_subuser_switch);
		shakeCheck = (CheckBox) view.findViewById(R.id.cb_shake_switch);
		mLiApplySub = (LinearLayout)view.findViewById(R.id.li_apply_sub);
		mCustomWakeSwitch = (CheckBox) view.findViewById(R.id.custom_wake_switch);

		mInput.setOnClickListener(this);
		mSpeaker.setOnClickListener(this);
		mNineDelay.setOnClickListener(this);
		mWakeWord.setOnClickListener(this);

		mSpeaker.hideValue(true);

		mOfflineSwitch.setOnCheckedChangeListener(this);
		mVoiceSendSwitch.setOnCheckedChangeListener(this);
		mApplySubSwitch.setOnCheckedChangeListener(this);
		shakeCheck.setOnCheckedChangeListener(this);
		mCustomWakeSwitch.setOnCheckedChangeListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		initCloudSettings();
		initSettings();
	}

	/**
	 * 用于fragment传递数据的监听
	 */
	private void initBroadcastReceiver() {
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(BroadcastManager.ACTION_MODE_MUSIC_CLOUD_MESSAGE)) {
					String speaker = intent.getStringExtra("speaker");
					mSpeaker.setTextValue(speaker);

					doUPdateCloudSettingList(ProductConstants.FUN_TYPE_POWER_AMPLIFIER, 
							StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
							speaker);
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastManager.ACTION_MODE_MUSIC_CLOUD_MESSAGE);
		getActivity().registerReceiver(mReceiver, filter);
	}

	/**
	 * 初始化设置
	 */
	private void initSettings() {
		if(csList != null && csList.size() > 0) {
			for(CloudSetting cs : csList) {
				String input = null;
				String speaker = null;
				String nineDelay = null;
				String offSwitch = null;
				String veinOnly = null;
				String sendSwitch = null;
				String applySubSwitch = null;
				String notDisturbSwitch = null;
				if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER.equals(cs.getCategory()) && 
						StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD.equals(cs.getItems())) {
					input = cs.getParams();
					if(input != null) {
						mInput.setTextValue(input.toUpperCase(Locale.CHINA));
					}else {
						mInput.setTextValue("usb".toUpperCase(Locale.CHINA));
					}
				} else if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER.equals(cs.getCategory()) && 
						StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT.equals(cs.getItems())) {
					speaker = cs.getParams();
					if(speaker != null) {
						mSpeaker.setTextValue(speaker);
					}else {
						mSpeaker.setTextValue("00000000");
					}
				} else if(StaticConstant.PARAM_NINE_LOCK_DELAY.equals(cs.getCategory()) && 
						StaticConstant.PARAM_NINE_LOCK_DELAY.equals(cs.getItems())) {
					nineDelay = cs.getParams();
					if(nineDelay != null) {
						mNineDelay.setTextValue(nineDelay);
					}else {
						mNineDelay.setTextValue(ControlDefine.DEFAULT_NINE_DELAY);
					}
				} else if(StaticConstant.PARAM_OFFLINE_SWITCH.equals(cs.getCategory()) && 
						StaticConstant.PARAM_OFFLINE_SWITCH.equals(cs.getItems())) {
					offSwitch = cs.getParams();
					if(offSwitch != null) {
						mOfflineSwitch.setChecked(Boolean.valueOf(offSwitch));
					}else {
						mOfflineSwitch.setChecked(true);
					}
				} else if(StaticConstant.PARAM_SEND_SWITCH.equals(cs.getCategory()) && 
						StaticConstant.PARAM_SEND_SWITCH.equals(cs.getItems())){
					sendSwitch = cs.getParams();
					if (sendSwitch != null) {
						mVoiceSendSwitch.setChecked(Boolean.valueOf(sendSwitch));
					}else{
						mVoiceSendSwitch.setChecked(false);
					}
				} else if(StaticConstant.PARAM_APPLY_SUB_SWITCH.equals(cs.getCategory()) && 
						StaticConstant.PARAM_APPLY_SUB_SWITCH.equals(cs.getItems())) {
					applySubSwitch = cs.getParams();
					if (applySubSwitch != null) {
						mApplySubSwitch.setChecked(applySubSwitch.equals("1")?true:false);
					}else{
						mApplySubSwitch.setChecked(false);
					}
				} else if(StaticConstant.PARAM_WARM_SHAKE_SWITCH.equals(cs.getCategory()) && 
						StaticConstant.PARAM_WARM_SHAKE_SWITCH.equals(cs.getItems())) {
					notDisturbSwitch = cs.getParams();
					if (notDisturbSwitch != null) {
						shakeCheck.setChecked(Boolean.valueOf(notDisturbSwitch));
					}else{
						shakeCheck.setChecked(false);
					}
				}
			}
		}else {
			mInput.setTextValue("usb".toUpperCase(Locale.CHINA));
			mSpeaker.setTextValue("00000000");
			mNineDelay.setTextValue(ControlDefine.DEFAULT_NINE_DELAY);
			mOfflineSwitch.setChecked(true);
			mVoiceSendSwitch.setChecked(false);
			shakeCheck.setChecked(false);
		}
		//SP获取唤醒方式
		Boolean isCustomWake = SharedDB.loadBooleanFromDB(CommUtil.getCurrentLoginAccount(),
				ControlDefine.KEY_WAKE_MODE, false);
		if(isCustomWake)
		{
			ManualInto=false;
		}
		mCustomWakeSwitch.setChecked(isCustomWake);

	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sync_cloud_settings:
			syncCloudSettings();
			break;
		default:
			break;
		}
		System.out.println(SettingsFragment.class.getName());
		return true;
	}

	@Override
	public void onResume() {
		if(!isMainAccount()) {
			mLiApplySub.setVisibility(View.GONE);
		}
		super.onResume();
	}

	private boolean isMainAccount() {
		if(CommUtil.getMainAccount().equals(CommUtil.getCurrentLoginAccount())) {
			return true;
		}
		return false;
	}

	private boolean isSyncSettings() {
		if(CommUtil.getMainAccount().equals(CommUtil.getCurrentLoginAccount())) {
			return true;
		}
		return false;
	}

	private boolean isApplyMainSettings() {
		if(!isMainAccount()) {
			CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getActivity());
			String _account = CommUtil.getMainAccount();
			Logger.debug(null, _account);
			List<CloudSetting> csList = csDaoImpl.find(null, "customer_id=?", new String[]{_account}, null, null, null, null);
			for (CloudSetting cs : csList) {
				if(StaticConstant.PARAM_APPLY_SUB_SWITCH.equals(cs.getCategory()) && 
						StaticConstant.PARAM_APPLY_SUB_SWITCH.equals(cs.getItems())) {
					String applySubSwitch = cs.getParams();
					if (applySubSwitch != null) {
						return applySubSwitch.equals("1")?true:false;
					}
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.setting_music_input :
			showInputsDialog();
			break;
		case R.id.setting_music_speaker :
			Intent intent = new Intent(getActivity(), SpeakerSettingsActivity.class);
			startActivityForResult(intent, 4);
			break;
		case R.id.setting_nine_delay :
			showPatternDetectPeroidDialog();
			break;
		case R.id.setting_wake_word :
			showWakeWordDialog();
			break;
		default :
			break;
		}
	}


	/**
	 * 同步云设置
	 */
	private void syncCloudSettings() {

		if (isSyncSettings()) {
			// 更新到平台
			String items = ProductConstants.FUN_TYPE_POWER_AMPLIFIER;
			String input = mInput.getTextValue();
			if (input == null || "".equals(input)) {
				Toast.makeText(getActivity(), "请先设置输入源", Toast.LENGTH_SHORT).show();
				return;
			}
			String speaker = mSpeaker.getTextValue();
			if (speaker == null || "".equals(speaker)) {
				Toast.makeText(getActivity(), "请先设置扬声器", Toast.LENGTH_SHORT).show();
				return;
			}

			JSONObject jb = new JSONObject();
			try {
				jb.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, mInput.getTextValue().toLowerCase(Locale.CHINA));
				jb.put(StaticConstant.PARAM_NINE_LOCK_DELAY, mNineDelay.getTextValue().toLowerCase(Locale.CHINA));
				jb.put(StaticConstant.PARAM_OFFLINE_SWITCH, Boolean.toString(mOfflineSwitch.isChecked()));
				jb.put(StaticConstant.PARAM_APPLY_SUB_SWITCH, Boolean.toString(mApplySubSwitch.isChecked()));
				jb.put(StaticConstant.PARAM_WARM_SHAKE_SWITCH, shakeCheck.isChecked()==true?"1":"2");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			SyncCloudSettingTask task = new SyncCloudSettingTask(getActivity(), "sync", items, jb.toString(), csList);
			task.addListener(new ITaskListener<ITask>() {
				@Override
				public void onStarted(ITask task, Object arg) {
					JxshApp.showLoading(getActivity(), "正在同步数据");
					Logger.debug(null, "onstart...");
				}

				@Override
				public void onCanceled(ITask task, Object arg) {
					JxshApp.closeLoading();
					Logger.debug(null, "onCanceled...");
				}

				@Override
				public void onFail(ITask task, Object[] arg) {
					JxshApp.closeLoading();
					Logger.debug(null, "onFail...");
				}

				@Override
				public void onSuccess(ITask task, Object[] arg) {
					JxshApp.closeLoading();
					Logger.debug(null, "onSuccess...");
					// 保存到数据库
					CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getActivity());
					for(CloudSetting cs : csList) {
						Logger.debug(null, "save to db:" + cs.toString());
						boolean b = csDaoImpl.isExist("select * from cloud_setting where id=" + cs.getId(), null);
						if(b) {
							csDaoImpl.update(cs);
						}else {
							csDaoImpl.insert(cs, true);
						}
					}

					SharedDB.saveBooleanDB(CommUtil.getCurrentLoginAccount(), 
							ControlDefine.KEY_ENABLE_OFFLINE_MODE,mOfflineSwitch.isChecked() );
				}

				@Override
				public void onProcess(ITask task, Object[] arg) {
				}
			});
			task.start();
		}
	}

	/**
	 * 进入页面时获取云设置
	 */
	private void initCloudSettings() {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getActivity());

		String _account = CommUtil.getCurrentLoginAccount();
		if(isApplyMainSettings()) {
			_account = CommUtil.getMainAccount();
		}
		Logger.debug(null, _account);
		csList.addAll(csDaoImpl.find(null, "customer_id=?", new String[]{_account}, null, null, null, null));
		Logger.debug(null, csList.size() + "");
	}

	/**
	 * 根据设置变化，更新csList中的数据，用于同步和保存数据库
	 */
	private void doUPdateCloudSettingList(String funType, String items, String params) {

		boolean exist = false;
		String _account = CommUtil.getMainAccount();
		if(!isSyncSettings()) {
			_account = CommUtil.getCurrentLoginAccount();
		}

		for(CloudSetting cs : csList) {
			if(funType.equals(cs.getCategory()) && items.equals(cs.getItems())) {
				exist = true;
				cs.setParams(params.toLowerCase(Locale.CHINA));
				System.out.println("item=" + cs.getItems() + " value=" + cs.getParams());
			}
		}

		if(!exist) {
			CloudSetting cloudSetting = new CloudSetting(null, _account, funType, items, params, null, null);
			csList.add(cloudSetting);
		}
	}

	@Override
	public void onItemClick(int position, String itemName, String tag) {
		mInput.setTextValue(itemName);
		doUPdateCloudSettingList(ProductConstants.FUN_TYPE_POWER_AMPLIFIER, 
				StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
				itemName);
		getActivity().getSupportFragmentManager().popBackStack();
	}

	/**
	 * Input source choose dialog 
	 */
	private void showInputsDialog() {

		RadioListDialogFragment inputFragment = RadioListDialogFragment.newInstance(
				R.string.music_choose_input, R.array.input_items, this, "input");
		int i = getSelectedInput();
		inputFragment.setSelectedPosition(i);
		inputFragment.setShowsDialog(false);
		getActivity().getSupportFragmentManager().beginTransaction()
		.add(inputFragment, "inputFragment").addToBackStack(null).commit();
	}
	/**
	 * 查看唤醒词
	 */
	private void showWakeWordDialog() {
		String[] words = (String[]) CommonData.wake_words.toArray(new String[CommonData.wake_words.size()]);
		WakeListDialogFragment wakeWordFragment = WakeListDialogFragment.newInstance(
				R.string.music_wake_word, words, null, "waked");
		wakeWordFragment.setShowsDialog(false);
		getActivity().getSupportFragmentManager().beginTransaction()
		.add(wakeWordFragment, "wakeWordFragment").addToBackStack(null).commit();
	}

	private int getSelectedInput() {
		String[] items = getResources().getStringArray(
				R.array.input_items);
		for(int i = 0; i < items.length; i++) {
			if(items[i].equalsIgnoreCase(mInput.getTextValue())) {
				return i;
			}
		}

		return 0;
	}

	private int getSelectedNineDelay() {
		String[] items = getResources().getStringArray(
				R.array.pattern_detect_peroid);
		for(int i = 0; i < items.length; i++) {
			if(items[i].equalsIgnoreCase(mNineDelay.getTextValue())) {
				return i;
			}
		}

		return 0;
	}

	/**
	 * Pattern password detect peroid choose dialog 
	 */
	private void showPatternDetectPeroidDialog() {

		RadioListDialogFragment inputFragment = RadioListDialogFragment.newInstance(
				R.string.setting_pattern_password_detect, R.array.pattern_detect_peroid, new NineDaleyOnItemClickListener(), "nine");
		int i = getSelectedNineDelay();
		inputFragment.setSelectedPosition(i);
		inputFragment.setShowsDialog(false);
		getActivity().getSupportFragmentManager().beginTransaction()
		.add(inputFragment, "nineDelayFragment").addToBackStack(null).commit();
	}

	private class NineDaleyOnItemClickListener implements RadioListDialogFragment.OnItemClickListener {

		@Override
		public void onItemClick(int position, String itemName, String tag) {
			mNineDelay.setTextValue(itemName);

			doUPdateCloudSettingList(StaticConstant.PARAM_NINE_LOCK_DELAY, 
					StaticConstant.PARAM_NINE_LOCK_DELAY,
					itemName);
			getActivity().getSupportFragmentManager().popBackStack();
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.setting_enable_offline_switch:
			//			if(isChecked) {
			//				String account = CommUtil.getCurrentLoginAccount();
			//				SharedDB.saveBooleanDB(account, ControlDefine.KEY_ENABLE_OFFLINE_MODE, true);
			//			}else {
			//				String account = CommUtil.getCurrentLoginAccount();
			//				SharedDB.saveBooleanDB(account, ControlDefine.KEY_ENABLE_OFFLINE_MODE, false);
			//			}

			doUPdateCloudSettingList(StaticConstant.PARAM_OFFLINE_SWITCH, 
					StaticConstant.PARAM_OFFLINE_SWITCH,
					Boolean.toString(isChecked));
			break;
		case R.id.setting_voice_send_switch://语音自动发送 or 点击发送
			if(isChecked) {
				String account = CommUtil.getCurrentLoginAccount();
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_VOICE_SEND_SWITCH, true);
			}else {
				String account = CommUtil.getCurrentLoginAccount();
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_VOICE_SEND_SWITCH, false);
			}
			doUPdateCloudSettingList(StaticConstant.PARAM_SEND_SWITCH, 
					StaticConstant.PARAM_SEND_SWITCH,
					Boolean.toString(isChecked));
			break;
		case R.id.setting_apply_subuser_switch://应用到子账号
			if(isChecked) {
				String account = CommUtil.getCurrentLoginAccount();
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_APPLY_SUB_SWITCH, true);
			}else {
				String account = CommUtil.getCurrentLoginAccount();
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_APPLY_SUB_SWITCH, false);
			}
			doUPdateCloudSettingList(StaticConstant.PARAM_APPLY_SUB_SWITCH, 
					StaticConstant.PARAM_APPLY_SUB_SWITCH,
					isChecked?"1":"2");
			break;
		case R.id.cb_shake_switch://免打扰
			if(isChecked) {
				String account = CommUtil.getCurrentLoginAccount();
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_WARN_SHAKE, true);
			}else {
				String account = CommUtil.getCurrentLoginAccount();
				SharedDB.saveBooleanDB(account, ControlDefine.KEY_WARN_SHAKE, false);
			}
			doUPdateCloudSettingList(StaticConstant.PARAM_WARM_SHAKE_SWITCH, 
					StaticConstant.PARAM_WARM_SHAKE_SWITCH,
					Boolean.toString(isChecked));
			break;
		case R.id.custom_wake_switch://切换唤醒方式，立即重启
			showReloginBox(isChecked);
			break;
		default:
			break;
		}

	}

	private void showReloginBox(boolean isChecked) {
		if(!ManualInto){
			ManualInto = true;
			return;
		}
		if(_mBox == null){
			_mBox = new MessageBox(getActivity(), "提示", "已切换唤醒方式，重启后生效。",
					MessageBox.MB_OK | MessageBox.MB_CANCEL);
			_mBox.setButtonText("立即重启", "稍后重启");
		}
		if(!_mBox.isShowing()){
			_mBox.show();
		}
		if(isChecked) {
			String account = CommUtil.getCurrentLoginAccount();
			SharedDB.saveBooleanDB(account, ControlDefine.KEY_WAKE_MODE, true);
		}else {
			String account = CommUtil.getCurrentLoginAccount();
			SharedDB.saveBooleanDB(account, ControlDefine.KEY_WAKE_MODE, false);
		}
		_mBox.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				switch(_mBox.getResult()){
				case MessageBox.MB_OK://点击消失
					_mBox.dismiss();
					//重新登录
					JxshApp.instance.logout();
					getActivity().finish();
					break;
				case MessageBox.MB_CANCEL:
					_mBox.dismiss();
				}
			}
		});
	}



}
