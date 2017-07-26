package com.jinxin.jxsmarthome.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.MusicDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.Music;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.ui.popupwindow.ShowSuggestWindows;
import com.jinxin.jxsmarthome.ui.widget.UpdateMessageBox;
import com.jinxin.jxsmarthome.util.AnimateUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 音乐播放控制
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class MusicFragment extends Fragment implements View.OnClickListener {
	private final int REFRESH_PLAY = 100;
	private final int REFRESH_VERSION = 101;
	private final int REFRESH_ERROR = 102;
	
	private ImageView mAlbumCover;
	private ImageView mPrevous;
	private ImageView mNext;
	private ImageView mPlay;
//	private ImageView mMute;
//	private ImageView mSoundAdd;
//	private ImageView mSoundSub;
	private ImageView mRepeatPlay;
//	private ImageButton mHideControlSwitcher;
//	private ViewGroup mHideControl;
	private ImageView mVolume;
	private TextView musicName, tvVersion;
	private ImageView ivUpdate;

	private ProductFun productFun; // 产品对象
	private FunDetail funDetail; // 设备对象
	private boolean isPlaying = false; // 是否正在播放的标志
	private boolean isMute; // 是否为静音状态的标志
	private boolean isRepeatPlay; // 单曲循环标志
	private Map<String, Object> params = new HashMap<String, Object>(); // 发送指令的参数
	private String[] speakerNames; // 扬声器名字
	private String inputOrigionalSet;
	private String speakerOrigionalSet;
	private String applyAll = "2";
	private MusicDaoImpl musicDaoImpl;
	
	private int playIndex = -1;//当前播放歌曲index
	private int currVolume = -1;
	
	private String currVersion = "";//功放当前版本
	
	private boolean isUsbAvailable = true; 
	private boolean isSDAvailable = true; 
	private boolean isClicklable = true; 
	private Activity activity = null;
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				togglePlay();
				break;
			case REFRESH_PLAY:
				//如果当前选中输入源不可用 禁用点击事件
				String currInput = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, "");
				if (!TextUtils.isEmpty(currInput)) {
					if (currInput.equalsIgnoreCase("USB")) {
						isClicklable = isUsbAvailable;
					} else if(currInput.equalsIgnoreCase("SD")){
						isClicklable = isSDAvailable;
					} else{
						isClicklable = true;
					}
				}else{
					isClicklable = false;
				}
				
				isPlaying = SharedDB.loadBooleanFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, false);
				playIndex = SharedDB.loadIntFromDB(MusicSetting.SP_NAME, MusicSetting.CURRENT_PLAYING_SONG, -1);

				recoveryPlayState();
				
				musicDaoImpl = new MusicDaoImpl(getActivity());
				List<Music> list = musicDaoImpl.find(null,
						"no = ?", new String[]{String.valueOf(playIndex)}, null, null, null, null);
				if (list != null && list.size() > 0) {
					musicName.setText(list.get(0).getTitle());
				}
				if (activity != null) {
					MusicListFragment mFragment = (MusicListFragment) getActivity().getSupportFragmentManager().findFragmentByTag("musicList");
					if (mFragment != null) {
						mFragment.refreshItem();
					}
				}
				break;
				
			case REFRESH_VERSION:
				currVersion = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_CURR_MUSIC_VERSION, "");
				tvVersion.setText("版本号 "+currVersion);
				break;
			case REFRESH_ERROR:
				isPlaying = false;
				recoveryPlayState();
				musicName.setText("");
				break;
			}
		}
		
	};
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化成员变量
		initParams();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.music, container, false);

		// 初始化布局视图
		initView(view);

		// 初始化扬声器设置
		initSettings();

		//新手引导
		((BaseActionBarActivity)this.getActivity()).suggestWindow = new ShowSuggestWindows(getActivity(), R.drawable.bg_guide_local_music, "");
				((BaseActionBarActivity)this.getActivity()).suggestWindow.showFullWindows("MusicFragment",R.drawable.bg_guide_local_music);
		
				
//		handler.postDelayed(task,10*1000);//延迟调用 定时查询
        
		return view;
	}
	
	private Runnable task = new Runnable() {
        public void run() {   
            handler.postDelayed(this,15*1000);//设置延迟时间，此处是5秒
        	//需要执行的代码
        	sendPlayStatusCmd();
        }   
    };
	
	@Override
	public void onResume() {
		super.onResume();
		sendCheckLinkStatusCmd();//检查连接状态
		
		isPlaying = SharedDB.loadBooleanFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, false);
		isMute = SharedDB.loadBooleanFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, false);
		isRepeatPlay =  SharedDB.loadBooleanFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PATTERN_STATE, false);
		playIndex = SharedDB.loadIntFromDB(MusicSetting.SP_NAME, MusicSetting.CURRENT_PLAYING_SONG, -1);
		
		musicDaoImpl = new MusicDaoImpl(getActivity());
		List<Music> list = musicDaoImpl.find(null,
				"no = ?", new String[]{String.valueOf(playIndex)}, null, null, null, null);
		if (list != null && list.size() > 0) {
			musicName.setText(list.get(0).getTitle());
		}
		
		recoveryPlayState();
//		recoveryMuteState();
		recoveryRepeatPlay();
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
//		case R.id.music_hide_control_switcher:
//			toggleHideControllView();
//			break;
		case R.id.music_repeat_play:
			toggleRepeatPlay();
			sendCmdList();
			break;
//		case R.id.music_iv_mute:
//			if (isMute) {
//				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_UNMUTE); // 操作指令为“取消静音”
//			} else {
//				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE); // 操作指令为“静音”
//			}
//			toggleMute();
//			sendCmdList();
//			break;
//		case R.id.music_sound_add:
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_ADD); // 操作指令为“增加音量”
//			sendCmdList();
//			break;
//		case R.id.music_sound_sub:
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_SUB); // 操作指令为“减小音量”
//			sendCmdList();
//			break;
		case R.id.music_iv_pre:
			if (!isClicklable) {
				JxshApp.showToast(getActivity(), "未选择输入源或当前输入源不可用");
				return;
			}
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_PROVIOUS); // 操作指令为“上一曲”
			sendCmdList();
			break;
		case R.id.music_iv_next:
			if (!isClicklable) {
				JxshApp.showToast(getActivity(), "未选择输入源或当前输入源不可用");
				return;
			}
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_NEXT); // 操作指令为“下一曲”
			sendCmdList();
			break;
		case R.id.music_iv_play:
			if (!isClicklable) {
				JxshApp.showToast(getActivity(), "未选择输入源或当前输入源不可用");
				return;
			}
			doPlayOrPauseAction();
			break;
		case R.id.set_iv_volume://音量设置
			if (!isClicklable) {
				JxshApp.showToast(getActivity(), "未选择输入源或当前输入源不可用");
				return;
			}
			showVolumeDialog();
			break;
		case R.id.iv_music_repeat://播放顺序
			if (!isClicklable) {
				JxshApp.showToast(getActivity(), "未选择输入源或当前输入源不可用");
				return;
			}
			toggleRepeatPlay();
			sendCmdList();
			break;
		case R.id.iv_update_state:
//			sendPlayStatusCmd();
			sendCheckLinkStatusCmd();
			break;
		}
	}

	/**
	 * 初始化参数
	 */
	private void initParams() {
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		isPlaying = false;
		isMute = false;
		isRepeatPlay = false;
		
		currVersion = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_CURR_MUSIC_VERSION, "1.0");
	}

	/**
	 * 初始化布局
	 */
	private void initView(View view) {
		mAlbumCover = (ImageView)view.findViewById(R.id.music_iv_play_cover);
		mPrevous = (ImageView) view.findViewById(R.id.music_iv_pre);
		mNext = (ImageView) view.findViewById(R.id.music_iv_next);
		mPlay = (ImageView) view.findViewById(R.id.music_iv_play);
//		mMute = (ImageView) view.findViewById(R.id.music_iv_mute);
//		mSoundAdd = (ImageView) view.findViewById(R.id.music_sound_add);
//		mSoundSub = (ImageView) view.findViewById(R.id.music_sound_sub);
		mVolume = (ImageView) view.findViewById(R.id.set_iv_volume);
		mRepeatPlay = (ImageView) view.findViewById(R.id.iv_music_repeat);
		musicName = (TextView) view.findViewById(R.id.music_play_name);
		ivUpdate = (ImageView) view.findViewById(R.id.iv_update_state);
		tvVersion = (TextView) view.findViewById(R.id.tv_music_version);
//		mHideControlSwitcher = (ImageButton) view
//				.findViewById(R.id.music_hide_control_switcher);
//		mHideControl = (ViewGroup) view.findViewById(R.id.music_ll_center);

//		mHideControlSwitcher.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mNext.setOnClickListener(this);
		mPrevous.setOnClickListener(this);
//		mMute.setOnClickListener(this);
//		mSoundAdd.setOnClickListener(this);
//		mSoundSub.setOnClickListener(this);
		mRepeatPlay.setOnClickListener(this);
		mVolume.setOnClickListener(this);
		ivUpdate.setOnClickListener(this);
		
		tvVersion.setText("版本号 "+currVersion);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.activity = null;
	}
	
//	private void toggleHideControllView() {
//		if (mHideControl.getVisibility() == View.VISIBLE) {
//			ScaleAnimation gongAnimation = new ScaleAnimation(1, 1, 1, 0,
//					ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
//					ScaleAnimation.RELATIVE_TO_SELF, 0.0f);
//			gongAnimation.setDuration(500);
//			mHideControl.startAnimation(gongAnimation);
//			mHideControl.setVisibility(View.INVISIBLE);
//		} else {
//			ScaleAnimation showAnimation = new ScaleAnimation(1, 1, 0, 1,
//					ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
//					ScaleAnimation.RELATIVE_TO_SELF, 0.0f);
//			showAnimation.setDuration(500);
//			mHideControl.startAnimation(showAnimation);
//			mHideControl.setVisibility(View.VISIBLE);
//		}
//	}
	
	private void doPlayOrPauseAction() {
		if (!checkInputSourceSetting()) {
			return;
		}
		togglePlay();
		sendCmdList();
	}

	/**
	 * 初始化设置
	 */
	private void initSettings() {
		// 同步扬声器名称，从同步数据中获取
		int length = 0;
		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(
				getActivity());
		List<FunDetailConfig> fdc4Amplifiers = daoImpl.find(null, "funType=?",
				new String[] { ProductConstants.FUN_TYPE_POWER_AMPLIFIER },
				null, null, null, null);
		if (fdc4Amplifiers != null && fdc4Amplifiers.size() > 0) {
			FunDetailConfig fdc4Amplifier = fdc4Amplifiers.get(0);
			if (fdc4Amplifier != null
					&& !CommUtil.isEmpty(fdc4Amplifier.getParams())) {
				try {
					JSONObject jsonObj = new JSONObject(
							fdc4Amplifier.getParams());

					JSONArray jsonArr = jsonObj.getJSONArray("param");

					length = jsonArr.length();
					speakerNames = new String[length];
					for (int i = 0; i < jsonArr.length(); i++) {
						speakerNames[i] = (String) jsonArr.get(i);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		initSettingsFromCloud();
	}

	/**
	 * 初始化保存在数据库中的设置
	 */
	private void initSettingsFromCloud() {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getActivity());
		String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT, "");
		List<CloudSetting> csList = csDaoImpl.find(null,
				"customer_id=? and category=?", 
				new String[] { _account,ProductConstants.FUN_TYPE_POWER_AMPLIFIER }, 
				null,null, null, null);
		/************如果子账号登录，取出子账号的本地设置**************/
		List<CloudSetting> subList = null;
		if (CommUtil.isSubaccount()) {
			subList = csDaoImpl.find(null,
					"customer_id=? and category=?", 
					new String[] { CommUtil.getCurrentLoginAccount(),ProductConstants.FUN_TYPE_POWER_AMPLIFIER }, 
					null,null, null, null);
		}
		/***********************************************/
		
		if (isApplyMainSettings()) {//如果主账号设置为“应用主账号设置”
			if (csList != null && csList.size() > 0) {
				setDefaultSetting(csList);
			} else {
				initDefaultSettings();
			}
		}else{
			if (subList != null && subList.size() > 0) {
				setDefaultSetting(subList);
			} else {
				initDefaultSettings();
			}
		}
	}
	
	private boolean isApplyMainSettings() {
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
		return true;
	}

	/**
	 * 遍历List，设置输入源、扬声器
	 * @param csList
	 */
	private void setDefaultSetting(List<CloudSetting> csList){
		if (csList != null && csList.size() > 0) {
			for (CloudSetting cs : csList) {
				if (StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT.equals(cs.getItems())) {
					speakerOrigionalSet = cs.getParams();
					if (CommUtil.isEmpty(speakerOrigionalSet)) {
						speakerOrigionalSet = "00000000";
					}
					SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
							speakerOrigionalSet);
				} else if (StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD.equals(cs.getItems())) {
					inputOrigionalSet = cs.getParams();
					if (CommUtil.isEmpty(inputOrigionalSet)) {
						inputOrigionalSet = "usb";
					}
//				SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.INPUT,
//						inputOrigionalSet);
				}
			}
		}
	}
	
	private void initDefaultSettings() {
		inputOrigionalSet = "usb";
		speakerOrigionalSet = "00000000";
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
				speakerOrigionalSet);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
				inputOrigionalSet);
	}

	/**
	 * 发送控制命令
	 */
	private void sendCmdList() {
		params.clear();
		String input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, 
				StaticConstant.INPUT_TYPE_USB).toLowerCase(Locale.CHINA);
		String speaker = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				"00010000");
		
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speaker);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, input);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_INPUT, input);
		
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(),
				productFun, funDetail, params);
		}else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(),
					productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(null, cmd);
			cmdSender.send();
		}
	}
	
	/**
	 * 设置音量
	 * @param volume
	 */
	private void sendSetVolumeCmd(final int volume){
		String v = resetParam(volume);
		params.clear();
		String speaker = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				"00010000");
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speaker);
		params.put(StaticConstant.PARAM_TEXT, v);
		TaskListener<ITask> listener = new TaskListener<ITask>(){

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				super.onSuccess(task, arg);
				SharedDB.saveIntDB(MusicSetting.SP_NAME,
						MusicSetting.CURRENT_VOLUME, volume);
			}
			
		};
		
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(),
				productFun, funDetail, params);
		}else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(),
					productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(null, cmd);
			cmdSender.addListener(listener);
			cmdSender.send();
		}
	}
	
	private String resetParam(int volume){
		if (volume < 10) {
			return "00"+volume;
		}else if(volume > 10 && volume < 100){
			return "0"+volume;
		}
		return "100";
	}

	/**
	 * 获取当前音量
	 * @param bar
	 */
	private void sendGetVolumeCmd(final SeekBar bar){
		Map<String, Object> params = new HashMap<String, Object>();
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_GET_VOLUME);
		final String speaker = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER,"10000000");
		
		TaskListener<ITask> listener = new TaskListener<ITask>(){

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				JxshApp.closeLoading();
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (result.startsWith("T")) {
						try {
							int index = speaker.indexOf("1") + 1;
							if (index > 0) {
								currVolume = Integer.valueOf(result.substring(
										index * 3 - 2, index * 3 + 1).replaceFirst(
												"^0+", ""));
								currVolume = currVolume > 100 ? 100 : currVolume;
								SharedDB.saveIntDB(MusicSetting.SP_NAME,
										MusicSetting.CURRENT_VOLUME, 100-currVolume);
							}else{
								currVolume = 100;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (currVolume > -1) {
							getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									bar.setProgress(100-currVolume);
								}
							});
						}
					}else{
						JxshApp.showToast(getActivity(), "音量查询失败");
					}
				}
			}

		};
		
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun, funDetail,
					params);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(),
					productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(getActivity(), cmd);
			cmdSender.addListener(listener);
			cmdSender.send();
		}
	}
	
	/**
	 * 检查功放连接状态
	 */
	private void sendCheckLinkStatusCmd(){
		Map<String, Object> params = new HashMap<String, Object>();
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_DEVICE_LINK);
		
		TaskListener<ITask> listener = new TaskListener<ITask>(){
			
			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(getActivity(), null);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.showToast(getActivity(), "处理超时");
				JxshApp.closeLoading();
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				JxshApp.closeLoading();
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (result.startsWith("T")) {
						//正常连接时才发送获取播放状态指令
						sendPlayStatusCmd();
						
						if ("0".equals(String.valueOf(result.charAt(1)))) { //USB连接状态
							isUsbAvailable = false;
						}else{
							isUsbAvailable = true;
						}
						SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_USB_AVAILABLE, isUsbAvailable);
						if ("0".equals(String.valueOf(result.charAt(2)))) { //SD连接状态
							isSDAvailable = false;
						}else{
							isSDAvailable = true;
						}
					}else{
						JxshApp.showToast(getActivity(), "未检测到USB、SD卡连接并选中为当前输入源");
					}
				}
			}

		};
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun, funDetail,
					params);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(),
					productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(getActivity(), cmd);
			cmdSender.addListener(listener);
			cmdSender.send();
		}
	}
	
	/**
	 * 获取功放播放状态
	 */
	private void sendPlayStatusCmd(){
		Map<String, Object> params = new HashMap<String, Object>();
		String input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME,
				MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		params.put(StaticConstant.PARAM_INDEX,
				input.equalsIgnoreCase("USB") ? "1" : "2");
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_PLAY_STATUS);
		
		TaskListener<ITask> listener = new TaskListener<ITask>(){
			
			@Override
			public void onStarted(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (result.startsWith("T") && result.length() > 9) {
						decodePlayState(String.valueOf(result.charAt(2)));
						
						String playIndex = result.substring(3, 10);
						playIndex = playIndex.replaceFirst("^0+", "");
						if (TextUtils.isEmpty(playIndex)) {
							playIndex = "0";
						}
						SharedDB.saveIntDB(MusicSetting.SP_NAME,
								MusicSetting.CURRENT_PLAYING_SONG, Integer.valueOf(playIndex));
						handler.sendEmptyMessage(REFRESH_PLAY);
					}else{
						SharedDB.saveBooleanDB(MusicSetting.SP_NAME, 
								MusicSetting.AMPLIFIER_PALY_STATE, false);
						handler.sendEmptyMessage(REFRESH_ERROR);
					}
				}
			}

		};
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun, funDetail,
					params);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(),
					productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(getActivity(), cmd);
			cmdSender.addListener(listener);
			cmdSender.send();
		}
	}
	/**
	 * 解析播放状态
	 * @param state
	 */
	private void decodePlayState(String state){
		if ("1".equals(state)) {
			
		}else if("2".equals(state)){//播放状态
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, true);
		}else if("3".equals(state) || "5".equals(state)){//暂停或停止
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, false);
		}else if("4".equals(state)){//静音
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, false);
		}else{
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, false);
		}
	}

	/**
	 * 运行播放动画
	 */
	private void runAnimate() {
		if (getActivity() == null) {
			return;
		}
		AnimateUtil.addRotateAnimation(getActivity(),false, 10000, 0);
		AnimateUtil.setAttr(Animation.RESTART, Animation.INFINITE);
		AnimateUtil.run(mAlbumCover);
	}

	/**
	 * 停止播放动画
	 */
	private void stopAnimate() {
		mAlbumCover.clearAnimation();
	}

	/**
	 * 静音开关
	 */
//	private void toggleMute() {
//		if (isMute) {
//			mMute.setImageResource(R.drawable.selector_btn_music_unmute);
//			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, false);
//			isMute = false;
//		} else {
//			mMute.setImageResource(R.drawable.selector_btn_music_mute);
//			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, true);
//			isMute = true;
//		}
//	}
	
//	private void recoveryMuteState() {
//		if (isMute) {
//			mMute.setImageResource(R.drawable.selector_btn_music_mute);
//		} else {
//			mMute.setImageResource(R.drawable.selector_btn_music_unmute);
//		}
//	}

	/**
	 * 播放开关
	 */
	private void togglePlay() {
		if (isPlaying) {
			isPlaying = false;
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, false);
			mPlay.setImageResource(R.drawable.selecter_music_play);
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE); 
			stopAnimate();
		} else {
			isPlaying = true;
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, true);
			mPlay.setImageResource(R.drawable.selecter_music_pause);
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);
			runAnimate();
		}
	}
	
	public void refreshPlayStatus(){
		handler.sendEmptyMessage(REFRESH_PLAY);
	}
	
	/**
	 * 恢复播放状态
	 */
	private void recoveryPlayState() {
		if (isPlaying) {
			mPlay.setImageResource(R.drawable.selecter_music_pause);
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);
			runAnimate();
		} else {
			mPlay.setImageResource(R.drawable.selecter_music_play);
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE); 
			stopAnimate();
		}
	}

	/**
	 * 重复播放开关
	 */
	private void toggleRepeatPlay() {
		if (isRepeatPlay) {
			isRepeatPlay = false;
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PATTERN_STATE, false);
			mRepeatPlay.setImageResource(R.drawable.ico_cycle);
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_ALL);
			JxshApp.showToast(getActivity(), "顺序播放");
		} else {
			isRepeatPlay = true;
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PATTERN_STATE, true);
			mRepeatPlay.setImageResource(R.drawable.ico_single);
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_SINGLE);
			JxshApp.showToast(getActivity(), "单曲循环");
		}
	}
	
	/**
	 * 重复播放状态恢复
	 */
	private void recoveryRepeatPlay() {
		if (isRepeatPlay) {
			mRepeatPlay.setImageResource(R.drawable.ico_single);
		} else {
			mRepeatPlay.setImageResource(R.drawable.ico_cycle);
		}
	}

	/**
	 * 检查输出源和扬声器设置
	 */
	private boolean checkInputSourceSetting() {

		// 检测扬声器设置
		String speaker = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				"00000000");
		
		// 提示设置扬声器
		if (CommUtil.isEmpty(speaker) || isAllZeroInString(speaker)) {
			new UpdateMessageBox(getActivity(), R.string.warn,
					R.string.music_warn_speaker, UpdateMessageBox.MB_OK).show();
			return false;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void saveInputSettingToDB(String input) {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getActivity());
		String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT, "");
		List<CloudSetting> csList = (csDaoImpl.find(null,
				"customer_id=? and category=? and items=?", new String[] { _account,
						ProductConstants.FUN_TYPE_POWER_AMPLIFIER, StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD }, null,
				null, null, null));
		if(csList == null || csList.size() < 1) {
			CloudSetting cs = new CloudSetting(null, _account, ProductConstants.FUN_TYPE_POWER_AMPLIFIER, 
					StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, input, null, null);
			csDaoImpl.insert(cs, true);
		}else {
			for(CloudSetting cs : csList) {
				cs.setParams(input);
				csDaoImpl.update(cs);
			}
		}
		
		SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.INPUT, input);
	}

	/**
	 * 判断字符串中是否全部都是0
	 * 
	 * @param str
	 * @return
	 */
	private boolean isAllZeroInString(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '1') {
				return false;
			}
		}
		return true;
	}
	
	public boolean getPlayState() {
		return this.isPlaying;
	}
	
	public void  setPlayState(boolean isPlay){
		this.isPlaying = isPlay;
		recoveryPlayState();
	}
	
	public void refreshVersion(){
		handler.sendEmptyMessage(REFRESH_VERSION);
	}
	
	private void showVolumeDialog(){
		final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bootom_music_volume,null);
		final SeekBar bar = (SeekBar) view.findViewById(R.id.bar_volume);
		final ImageView mMute = (ImageView) view.findViewById(R.id.iv_mute);
		ImageView mVolumeSub = (ImageView) view.findViewById(R.id.iv_volume_sub);
		ImageView mVolumeAdd = (ImageView) view.findViewById(R.id.iv_volume_add);
		
		currVolume = SharedDB.loadIntFromDB(MusicSetting.SP_NAME,
				MusicSetting.CURRENT_VOLUME, -1);
		if (currVolume > -1) {
			bar.setProgress(currVolume);
		}
		
		final Dialog dialog = BottomDialogHelper.showDialogInBottom(getActivity(), view, null);
		dialog.setContentView(view);
		dialog.show();
		sendGetVolumeCmd(bar);
		
		if (!isMute) {
			mMute.setImageResource(R.drawable.selector_btn_music_unmute);
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, false);
			isMute = false;
		} else {
			mMute.setImageResource(R.drawable.selector_btn_music_mute);
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, true);
			isMute = true;
		}
		
		mMute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isMute) {
					productFun.setFunType(ProductConstants.POWER_AMPLIFIER_UNMUTE); // 操作指令为“取消静音”
					mMute.setImageResource(R.drawable.selector_btn_music_unmute);
					SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, false);
					isMute = false;
					JxshApp.showToast(getActivity(), "取消静音");
				} else {
					productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE); // 操作指令为“静音”
					mMute.setImageResource(R.drawable.selector_btn_music_mute);
					SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, true);
					isMute = true;
					JxshApp.showToast(getActivity(), "静音");
				}
				sendCmdList();
			}
		});
		
		mVolumeSub.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_SUB); // 操作指令为“增加减小”
				if (bar.getProgress() > 6) {
					bar.setProgress(bar.getProgress()-7);
				}else{
					bar.setProgress(0);
				}
				SharedDB.saveIntDB(MusicSetting.SP_NAME,
						MusicSetting.CURRENT_VOLUME, bar.getProgress());
				sendCmdList();
			}
		});
		mVolumeAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_ADD); // 操作指令为“增加音量”
				if (bar.getProgress() < 94) {
					bar.setProgress(bar.getProgress()+7);
				}else{
					bar.setProgress(100);
				}
				SharedDB.saveIntDB(MusicSetting.SP_NAME,
						MusicSetting.CURRENT_VOLUME, bar.getProgress());
				sendCmdList();
			}
		});
		
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SET_VOLUME);
				int currVolume = seekBar.getProgress();
				sendSetVolumeCmd(100-currVolume);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
			}
		});
	}
	
}
