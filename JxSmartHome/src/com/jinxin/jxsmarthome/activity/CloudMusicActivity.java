package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.SaveInput4VoiceTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.fragment.CheckboxListDialogFragment;
import com.jinxin.jxsmarthome.fragment.CloudMusicFragment;
import com.jinxin.jxsmarthome.fragment.MusicHistoryListFragment;
import com.jinxin.jxsmarthome.fragment.MusicListFragment;
import com.jinxin.jxsmarthome.fragment.RadioListDialogFragment;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 云音乐播放界面
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class CloudMusicActivity extends BaseActionBarActivity implements
		RadioListDialogFragment.OnItemClickListener, CheckboxListDialogFragment.OnClickListener {
	private ProductFun productFun;
	private FunDetail funDetail;
	private List<ProductFun> amplifiers;
	private CloudMusicFragment musicFragment;
	private MusicLib musicLib;//本地存储音乐
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParam();
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_cloud_music, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
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
		case R.id.action_music_list://打开本地音乐播放界面
//			addMusicListFragment();
			startActivity(new Intent(CloudMusicActivity.this,MusicActivity.class));
			break;
		}

		return false;
	}

	@Override
	public void onItemClick(int position, String itemName, String tag) {
		if("input".equals(tag)) {
			SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.INPUT, itemName.toLowerCase(Locale.CHINA));
			// 发送保存音乐输入源设置到平台（为语音切换做基础）
			setInput4Voice(itemName.toLowerCase(Locale.US));
			updateMusicListFragment();
			sendPalyCmd();
		}else if("amplifier".equals(tag)) {
			ProductFun amplifier = amplifiers.get(position);
			int amplifierId = SharedDB.loadIntFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER, -1);
			if(amplifier.getFunId() != amplifierId) {
				productFun = amplifier;
//				if(musicFragment.getPlayState()) sendPalyCmd();
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

	private void initView() {
		setContentView(R.layout.activity_music);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("云音乐");
		
		musicFragment = new CloudMusicFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("productFun", productFun);
		bundle.putSerializable("funDetail", funDetail);
		bundle.putSerializable("musicLib", musicLib);
		musicFragment.setArguments(bundle);
		if(productFun != null && funDetail != null) {
			addFragment(musicFragment, false);
		}else {
			Toast.makeText(this, "音乐设备初始化失败,请尝试重新登录后重试!", Toast.LENGTH_LONG).show();
		}
	}

	private void initParam() {
		productFun = AppUtil.getSingleProductFunByFunType(this,
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		funDetail = AppUtil.getFunDetailByFunType(this,
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		musicLib = (MusicLib) getIntent().getSerializableExtra("musicLib");
	}

	/**
	 * 添加fragment
	 * @param fragment
	 * @param addToStack
	 */
	private void addFragment(Fragment fragment, boolean addToStack) {
		if(getSupportFragmentManager().getBackStackEntryCount() >= 1) {
			getSupportFragmentManager().popBackStackImmediate();
		}
		if (fragment != null && addToStack) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.music_fragment_layout, fragment)
					.addToBackStack(null).commitAllowingStateLoss();
		} else if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.music_fragment_layout, fragment).commitAllowingStateLoss();
		}
	}
	
	
	/**
	 * 播放列表展示
	 */
	private void addMusicListFragment() {
		if(productFun != null && funDetail != null) {
			Fragment listFragment = new MusicHistoryListFragment();
			Bundle data = new Bundle();
			data.putSerializable("productFun", productFun);
			data.putSerializable("funDetail", funDetail);
			listFragment.setArguments(data);
			addFragment(listFragment, true);
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
		
		int len = amplifiers.size();
		for(int i = 0; i < len; i++) {
			amplifierNames[i] = amplifiers.get(i).getFunName();
		}
			
		return amplifierNames;
	}

	/**
	 * 保存扬声器设置，并发送切换命令
	 * 
	 * @param speakerSettingStr
	 */
	private void saveSpeakerSettingsAndSendCmd(String speakerSettingStr) {
		SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				speakerSettingStr);
		sendMuteCmd();
		sendUnmuteCmd(speakerSettingStr);
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
	 * 为语音合成保存输入源设置
	 */
	private void setInput4Voice(String input) {
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
//		params.put(StaticConstant.PARAM_MUSIC_SELECT_INPUT, setting_input);
		
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);

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
	
	private int getSelectedInputPosition() {
		String input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, "USB");
		String[] inputArr = getResources().getStringArray(R.array.input_items);
		if(inputArr != null) {
			int len = inputArr.length;
			for(int i = 0; i < len; i++) {
				if(inputArr[i].equalsIgnoreCase(input)) {
					return i;
				}
			}
		}
		return 0;
	}
	
	private int getSelectedAmplifierPosition() {
		return 0;
	}

}
