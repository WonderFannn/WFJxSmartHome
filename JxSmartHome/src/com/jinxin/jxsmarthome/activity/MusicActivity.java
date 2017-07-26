package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.SaveInput4VoiceTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.fragment.CheckboxListDialogFragment;
import com.jinxin.jxsmarthome.fragment.MusicFragment;
import com.jinxin.jxsmarthome.fragment.MusicListFragment;
import com.jinxin.jxsmarthome.fragment.MusicStateFragment;
import com.jinxin.jxsmarthome.fragment.RadioListDialogFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 音乐播放
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class MusicActivity extends BaseActionBarActivity implements
		RadioListDialogFragment.OnItemClickListener, CheckboxListDialogFragment.OnClickListener {
	private ProductFun productFun;
	private FunDetail funDetail;
	private List<ProductFun> amplifiers;
	private MusicFragment musicFragment;

	private List<CloudSetting> csList;
	private String newestVersion = "V1.0";//功放最新版本
	private String currVersion = "";//功放当前版本
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParam();
		initView();
		
		if (compareVersion(currVersion, newestVersion)) {
			sendGetVersionCmd();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_music, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.action_music_input:
			RadioListDialogFragment inputFragment = RadioListDialogFragment.newInstance(
					R.string.music_choose_input, R.array.input_items, this, "input");
			int i = getSelectedInputPosition();
			inputFragment.setSelectedPosition(i);
			inputFragment.setShowsDialog(false);
			getSupportFragmentManager().beginTransaction()
				.add(inputFragment, "inputFragment").addToBackStack(null).commitAllowingStateLoss();
			break;
		case R.id.action_music_speaker:
			DialogFragment speakerFragment = CheckboxListDialogFragment
					.newInstance(R.string.music_chose_speaker,
							getSpeakerNames(), this);
			getSupportFragmentManager().beginTransaction()
				.add(speakerFragment, "speakerFragment").addToBackStack(null).commitAllowingStateLoss();
			break;
		case R.id.action_music_amplifier:
			RadioListDialogFragment amplifierFragment = RadioListDialogFragment.newInstance(
					R.string.music_choose_amplifier, getAmplifierNames(), this, "amplifier");
			amplifierFragment.setSelectedPosition(getSelectedAmplifierPosition());
			getSupportFragmentManager().beginTransaction()
				.add(amplifierFragment, "amplifierFragment").addToBackStack(null).commitAllowingStateLoss();
			break;
		case R.id.action_music_list://音乐列表
			addMusicListFragment();
			break;
		case R.id.action_music_state://状态查询
			addMusicStateFragment();
			break;
		case R.id.action_cloud_music_list://打开云音乐界面
//			startActivity(new Intent(MusicActivity.this,CloudMusicActivity.class));
			finish();
			break;
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onItemClick(int position, String itemName, String tag) {
		if("input".equals(tag)) {
			CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(this);	
			List<CloudSetting> csList = csDaoImpl.find(null, "customer_id=? and items=?",
						new String[]{CommUtil.getCurrentLoginAccount(),
					StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD }, null, null, null, null);
			if (csList != null && csList.size() > 0) {
				CloudSetting cs = csList.get(0);
				cs.setParams(itemName.toLowerCase(Locale.CHINA));
				csDaoImpl.update(cs);
			}else{
				CloudSetting cs = new CloudSetting("", CommUtil.getCurrentLoginAccount(),
						"004", StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, itemName.toLowerCase(Locale.CHINA), null, null);
				csDaoImpl.insert(cs,true);
			}
			SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.INPUT, itemName.toLowerCase(Locale.CHINA));
			
			// 发送保存音乐输入源设置到平台（为语音切换做基础）
			setInput4Voice(itemName.toLowerCase(Locale.US));
			updateMusicListFragment();
		}else if("amplifier".equals(tag)) {
			ProductFun amplifier = amplifiers.get(position);
			int amplifierId = SharedDB.loadIntFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER, -1);
			if(amplifier.getFunId() != amplifierId) {
				productFun = amplifier;
				if(musicFragment.getPlayState()) sendPalyCmd();
				// 发送保存音乐设置到平台（为语音切换做基础）
				setAmplifier4Voice(amplifier.getWhId());
			}
		}
		getSupportFragmentManager().popBackStack();
	}

	@Override
	public void onClick(View view, SparseBooleanArray checkedItems) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < checkedItems.size(); i++) {
			if (checkedItems.get(i)) {
				sb.append(1);
			} else {
				sb.append(0);
			}
		}
		saveSpeakerSettingsAndSendCmd(sb.toString());
//		if(musicFragment.getPlayState()) sendPalyCmd();
		getSupportFragmentManager().popBackStack();
	}
	
	

	@Override
	public void uiHandlerData(Message msg) {
		super.uiHandlerData(msg);
		switch (msg.what) {
		case 0:
			updateMusicFragment();
			break;

		default:
			break;
		}
		updateMusicFragment();
	}

	private void initView() {
		setContentView(R.layout.activity_music);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("音乐播放");
		
		musicFragment = new MusicFragment();
		Bundle args = new Bundle();
		args.putSerializable("productFun", productFun);
		args.putSerializable("funDetail", funDetail);
		musicFragment.setArguments(args);
		if(productFun != null && funDetail != null) {
			addFragment(musicFragment, false,"music");
		}else {
			Toast.makeText(this, "音乐设备初始化失败,请尝试重新登录后重试!", Toast.LENGTH_LONG).show();
		}
	}

	private void initParam() {
		productFun = AppUtil.getSingleProductFunByFunType(this,
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		funDetail = AppUtil.getFunDetailByFunType(this,
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		csList = new ArrayList<CloudSetting>();
		
		newestVersion = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.NEWEST_VERSION, "V1.0");
		
		initCloudSettings();
	}

	/**
	 * 添加fragment
	 * @param fragment
	 * @param addToStack
	 */
	private void addFragment(Fragment fragment, boolean addToStack,String tag) {
		if(getSupportFragmentManager().getBackStackEntryCount() >= 1) {
			getSupportFragmentManager().popBackStackImmediate();
		}
		if (fragment != null && addToStack) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.music_fragment_layout, fragment,tag)
					.addToBackStack(tag).commitAllowingStateLoss();
		} else if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.music_fragment_layout, fragment,tag).commitAllowingStateLoss();
		}
	}
	
	
	/**
	 * 播放列表展示
	 */
	private void addMusicListFragment() {
		if(productFun != null && funDetail != null) {
			Fragment listFragment = new MusicListFragment();
			Bundle data = new Bundle();
			data.putSerializable("productFun", productFun);
			data.putSerializable("funDetail", funDetail);
			listFragment.setArguments(data);
			addFragment(listFragment, true,"musicList");
		}
	}
	/**
	 * 播放状态列表展示
	 */
	private void addMusicStateFragment() {
		if(productFun != null && funDetail != null) {
			Fragment stateFragment = new MusicStateFragment();
			Bundle data = new Bundle();
			data.putSerializable("productFun", productFun);
			data.putSerializable("funDetail", funDetail);
			stateFragment.setArguments(data);
			addFragment(stateFragment, true,"musicstate");
		}
	}

	/**
	 * 获取扬声器名字
	 */
	private ArrayList<String> getSpeakerNames() {
		ArrayList<String> speakerList = new ArrayList<String>();
		String speakerNamesStr = getSpeakerNamesFromDb();
		if (!CommUtil.isEmpty(speakerNamesStr)) {
			try {
				JSONObject jsonObj = new JSONObject(speakerNamesStr);
				JSONArray jsonArr = jsonObj.getJSONArray("param");

				for (int i = 0; i < jsonArr.length(); i++) {
					speakerList.add((String) jsonArr.get(i));
				}

				return speakerList;
			} catch (JSONException e) {
				return getDefaultSpeakerNames();
			}
		} else {
			return getDefaultSpeakerNames();
		}
	}

	/**
	 * 获取默认扬声器设置
	 */
	private ArrayList<String> getDefaultSpeakerNames() {
		String[] speakers = getResources()
				.getStringArray(R.array.speaker_items);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < speakers.length; i++) {
			list.add(speakers[i]);
		}
		return list;
	}

	/**
	 * 从数据库中获取扬声器的配置
	 */
	private String getSpeakerNamesFromDb() {
		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(this);
		List<FunDetailConfig> fdc4Amplifiers = daoImpl.find(null, "funType=?",
				new String[] { ProductConstants.FUN_TYPE_POWER_AMPLIFIER },
				null, null, null, null);
		if (fdc4Amplifiers != null && fdc4Amplifiers.size() > 0) {
			FunDetailConfig fdc4Amplifier = fdc4Amplifiers.get(0);
			if (fdc4Amplifier != null
					&& !CommUtil.isEmpty(fdc4Amplifier.getParams())) {
				return fdc4Amplifier.getParams();
			}
		}
		return "";
	}
	
	/**
	 * 获取功放设备
	 * @return
	 */
	private List<ProductFun> getAmplifiers() {
		return AppUtil.getProductFunByFunType(this, ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
	}
	
	/**
	 * 获取功放名称
	 */
	private String[] getAmplifierNames() {
		amplifiers = getAmplifiers();
		String[] amplifierNames = new String[amplifiers.size()];
		
		for(int i = 0; i < amplifiers.size(); i++) {
			amplifierNames[i] = AppUtil.
					getFunDetailConfigByWhId(this,amplifiers.get(i).getWhId()).getAlias();
		}
			
		return amplifierNames;
	}

	/**
	 * 保存扬声器设置，并发送切换命令
	 * 
	 * @param speakerSettingStr
	 */
	private void saveSpeakerSettingsAndSendCmd(String speakerSettingStr) {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(this);	
		List<CloudSetting> csList = csDaoImpl.find(null, "customer_id=? and items=?",
					new String[]{CommUtil.getCurrentLoginAccount(),
				StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT }, null, null, null, null);
		if (csList != null && csList.size() > 0) {
			CloudSetting cs = csList.get(0);
			cs.setParams(speakerSettingStr);
			csDaoImpl.update(cs);
		}else{
			CloudSetting cs = new CloudSetting("", CommUtil.getCurrentLoginAccount(),
					"004", StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speakerSettingStr, null, null);
			csDaoImpl.insert(cs,true);
		}
		
		SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				speakerSettingStr);
//		sendMuteCmd();
//		sendUnmuteCmd(speakerSettingStr);
//		sendSpeakerChangeCmd(speakerSettingStr);
		sendPalyCmd();
	}
	
	private void updateMusicListFragment() {
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		for(Fragment f : fragments) {
			if(f instanceof MusicListFragment) {
				((MusicListFragment)f).reloadMusicList();
				return;
			}
		}
	}
	
	private void updateMusicFragment() {
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		for(Fragment f : fragments) {
			if(f instanceof MusicFragment) {
				((MusicFragment)f).setPlayState(true);
				return;
			}
		}
	}
	
	/**
	 * “发送静音命令”和“取消静音命令”
	 * 合并为一次请求依次发送
	 * @param speakerSettingStr
	 */
	@SuppressWarnings("unchecked")
	private void sendSpeakerChangeCmd(String speakerSettingStr){
		if (productFun == null)  return;
		
		TaskListener listener = new TaskListener<ITask>(){

			@Override
			public void onAllSuccess(ITask task, Object[] arg) {
				if(musicFragment.getPlayState()) sendPalyCmd();
			}
			
			@Override
			public void onAnyFail(ITask task, Object[] arg) {
				if(musicFragment.getPlayState()) sendPalyCmd();
			}
			
		};
		
		String setting_input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME,
				MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		List<byte[]> cmd = new ArrayList<byte[]>();
		
		//生成切换扬声器指令
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, setting_input);
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speakerSettingStr);
		byte[] unMuteCmd = CommonMethod.productPatternOperationToCMD(this,
				productFun, funDetail, params);
		Logger.debug(null, "MusicActivity unMuteCmd:"+unMuteCmd);
		cmd.add(unMuteCmd);
		
//		//生成静音指令
//		Map<String, Object> params2 = new HashMap<String, Object>();
//		params2.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, setting_input);
//		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE);
//		byte[] muteCmd = CommonMethod.productPatternOperationToCMD(
//				this, productFun, funDetail, params2);
//		Logger.debug(null, "MusicActivity muteCmd:"+muteCmd);
//		cmd.add(muteCmd);
		
		OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(MusicActivity.this, cmd);
		cmdSender.addListener(listener);
		cmdSender.send();
	}

	/**
	 * 发送静音命令
	 */
	private void sendMuteCmd() {
		String setting_input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME,
				MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, setting_input);
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE);

		if (NetworkModeSwitcher.useOfflineMode(this)) {
			OfflineCmdManager.generateCmdAndSend(this, productFun, funDetail, params);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(
					this, productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(this, cmd);
			cmdSender.send();
		}
	}
	
	/**
	 * 发送取消静音命令
	 */
	private void sendUnmuteCmd(String speakerSettingStr) {
		String setting_input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME,
				MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, setting_input);
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speakerSettingStr);
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE);

		if (NetworkModeSwitcher.useOfflineMode(this)) {
			OfflineCmdManager.generateCmdAndSend(this, productFun, funDetail,
					params);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(this,
					productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(this, cmd);
			cmdSender.send();
		}
	}
	
	/**
	 * 获取功放版本号
	 */
	private void sendGetVersionCmd(){
		if (productFun == null || funDetail == null) {
			Logger.debug(null, "productFun or funDetail is null");
			return;
		}
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_GET_VERSION);
		
		TaskListener<ITask> listener = new TaskListener<ITask>(){

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				super.onSuccess(task, arg);
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (result.startsWith("T")) {
						currVersion = result.substring(1,6).replaceFirst("^0+", "");
						SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_CURR_MUSIC_VERSION, currVersion);
						MusicFragment mFragment = (MusicFragment) getSupportFragmentManager().findFragmentByTag("music");
						if (mFragment != null) {
							mFragment.refreshVersion();
						}
						
						if (compareVersion(currVersion, newestVersion)) {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									JxshApp.showToast(MusicActivity.this, "检测到功放固件版本需要更新，请及时更新以获得更好的体验");
								}
							});
						}
					}else{
						Logger.debug(null, "查询功放版本失败");
					}
				}
			}
			
		};
		if (NetworkModeSwitcher.useOfflineMode(this)) {
			OfflineCmdManager.generateCmdAndSend(this, productFun, funDetail,null);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(this,
					productFun, funDetail, null);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(this, cmd);
			cmdSender.addListener(listener);
			cmdSender.send();
		}
	}
	
	/**
	 * 为语音合成保存输入源设置
	 */
	private void setInput4Voice(String input) {
		if (productFun == null)  return;
		
		ProductRegister pr = AppUtil.getProductRegisterByWhId(this, productFun.getWhId());
		CustomerProduct cp = AppUtil.getCustomerProductByWhId(this, productFun.getWhId());
		String gatewayWhid = "";
		String address485 = "";
		if(pr != null && cp != null) {
			gatewayWhid = pr.getGatewayWhId();
			address485 = cp.getAddress485();
		}
		SaveInput4VoiceTask task = new SaveInput4VoiceTask(this, input, gatewayWhid, address485);
		task.addListener(new TaskListener<ITask>() {
			public void onSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "SaveInput4VoiceTask success");
			};
			
			public void onFail(ITask task, Object[] arg) {
				Logger.debug(null, "SaveInput4VoiceTask onFail");
			};
		});
		task.start();
	}
	
	/**
	 * 保存扬声器设备的whid
	 * @param whId
	 */
	private void setAmplifier4Voice(String whId) {
		SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_WHID, whId);
	}
	
	/**
	 * 发送播放命令
	 */
	private void sendPalyCmd() {
		String setting_input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME,
				MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		String setting_speaker = SharedDB.loadStrFromDB(MusicSetting.SP_NAME,
				MusicSetting.SPEAKER, "00000000");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, setting_input);
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, setting_speaker);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_INPUT, setting_input);
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);
		
		TaskListener<ITask> listener = new TaskListener<ITask>(){

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				super.onSuccess(task, arg);
				mUIHander.sendEmptyMessage(0);
			}
			
		};

		if (NetworkModeSwitcher.useOfflineMode(this)) {
			OfflineCmdManager.generateCmdAndSend(this, productFun, funDetail,
					params);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(this,
					productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(null, cmd);
			cmdSender.addListener(listener);
			cmdSender.send();
			//释放按钮权限
			JxshApp.instance.isClinkable = true;
		}
	}
	
	private int getSelectedInputPosition() {
		String input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, "");
		//初次使用，同步云设置中input设置
		if (TextUtils.isEmpty(input)) {
			if (csList != null && csList.size() > 0) {
				for(CloudSetting cs : csList) {
					if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER.equals(cs.getCategory()) && 
							StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD.equals(cs.getItems())) {
						if (!TextUtils.isEmpty(cs.getParams())) {
							input = cs.getParams();
						}
					} 
				}
			}else{
				if (TextUtils.isEmpty(input)) {
					input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, "");
				}else{
					input = "USB";
				}
			}
		}
		String[] inputArr = getResources().getStringArray(R.array.input_items);
		if(inputArr != null) {
			for(int i = 0; i < inputArr.length; i++) {
				if(inputArr[i].equalsIgnoreCase(input)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private int getSelectedAmplifierPosition() {
		return 0;
	}
	
	/**
	 * 进入页面时获取云设置
	 */
	private void initCloudSettings() {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(this);
		if (!CommUtil.isSubaccount()) {
			csList.addAll(csDaoImpl.find(null, "customer_id=?", new String[]{CommUtil.getMainAccount()}, null, null, null, null));
		}else{
			if (isApplyMainSettings()) {
				csList.addAll(csDaoImpl.find(null, "customer_id=?", new String[]{CommUtil.getMainAccount()}, null, null, null, null));
			}else{
				csList.addAll(csDaoImpl.find(null, "customer_id=?", new String[]{CommUtil.getCurrentLoginAccount()}, null, null, null, null));
			}
		}
		
		if (csList != null && csList.size() > 0) {
			for (CloudSetting cs : csList) {
				if (cs != null) {
					if (StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD.equals(cs.getItems())) {
						SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.INPUT, cs.getParams());
					}else if(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT.equals(cs.getItems())){
						SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER, cs.getParams());
					}
				}
			}
		}
		
		currVersion = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_CURR_MUSIC_VERSION, "");
		
	}
	
	private boolean compareVersion(String currVersion, String newestVersion){
		if (TextUtils.isEmpty(currVersion) || TextUtils.isEmpty(newestVersion)) {
			return true;
		}
		
		if (Integer.valueOf(newestVersion.substring(1).replace(".", "")) > 
				Integer.valueOf(currVersion.substring(1).replace(".", ""))) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 是否应用主帐号的云设置
	 * @return
	 */
	private boolean isApplyMainSettings() {
		if(CommUtil.isSubaccount()) {
			CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(this);
			String _account = CommUtil.getMainAccount();
			
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
		}
		return true;
	}
	
}
