package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.jinxin.cd.smarthome.product.service.newcmd.GenPowerAmplifier;
import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.UpdateMusicListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.impl.MusicDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.MusicActivity;
import com.jinxin.jxsmarthome.cmd.ICmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.Music;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.MusicListAdapter;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 音乐列表展示
 * @author  TangLong
 * @company 金鑫智慧
 */
public class MusicListFragment extends Fragment implements OnClickListener {
	public static final int LOAD_MUSIC_FROM_DB_SUCCESS = 10;	// 加载本地数据成功
	public static final int GET_MUSIC_LIST_SUCCESS = 100;		// 获取歌曲列表成功
	public static final int GET_MUSIC_LIST_FAIL = 200;			// 获取歌曲列表失败
	public static final int REFRESH_MUSIC = 300;			// 刷新当前播放音乐
	
	private ProductFun productFun; 				// 产品对象
	private FunDetail funDetail; 				// 设备对象
	private ListView mSongList;					// 歌曲列表视图
	private Button mBtnSync;					// 同步歌曲列表
	private List<Music> songs; 					// 歌曲列表
	private MusicListAdapter adapter;			// 歌曲列表显示适配器
	private Map<String, Object> params; 		// 发送指令的参数
	private MusicDaoImpl musicDaoImpl;
	private int playIndex = -1;
	private static MusicListFragment musicListFragment = null;
	
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case LOAD_MUSIC_FROM_DB_SUCCESS:
				updateListView();
				break;
			case GET_MUSIC_LIST_SUCCESS:
				updateListView();
				break;
			case GET_MUSIC_LIST_FAIL:
				updateListView();
				break;
			case REFRESH_MUSIC:
				playIndex = SharedDB.loadIntFromDB(MusicSetting.SP_NAME, MusicSetting.CURRENT_PLAYING_SONG, -1);
				adapter.setSelectIndex(playIndex);
				adapter.notifyDataSetChanged();
				break;
			}
			return true;
		}
	});
	
	public static MusicListFragment getIntance() {
		if (musicListFragment == null) {
			musicListFragment = new MusicListFragment();
		}
		return musicListFragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		params = new HashMap<String, Object>();
		songs = new ArrayList<Music>();
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		handler.sendEmptyMessage(REFRESH_MUSIC);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.music_list, container, false);
		
		((MusicActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mSongList = (ListView)view.findViewById(R.id.musiclist_lv_songs_list);
		
		adapter = new MusicListAdapter(songs, this.getActivity());
		mSongList.setFocusable(true);
		mSongList.setFocusableInTouchMode(true);
		
		mBtnSync = (Button)view.findViewById(R.id.musiclist_btn_sync);
		
		mSongList.setAdapter(adapter);
		mSongList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// 检查播放设置
				if(!checkInputSourceSetting()) {
					return;
				}
				
				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY_SONG);
				Music m = songs.get(position);
				if(m != null) sendCmd(m.getNo());	
			}
		});
		
		mBtnSync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
//					updateDataFromLocal();
				}else {
					syncDataFromCloud();
				}
			}
		});
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		loadMusicList();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_save:
			updateDataFromCloud();
			break;
		}
	}
	
	/**
	 * 界面初始化时，加载歌曲列表
	 */
	public void loadMusicList() {
		Logger.debug(null, NetworkModeSwitcher.useOfflineMode(getActivity()) + "");
		
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
					.string(R.string.li_xian_cao_zuo_tips));
			return;
		}
		String input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		String listInput = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.LIST_INPUT, StaticConstant.INPUT_TYPE_USB);
		
		if(input.equalsIgnoreCase(listInput)) {
			songs = loadDataFromDb();
			if(songs.size() < 1) {
				updateDataFromCloud();
			}else {
				handler.sendEmptyMessage(LOAD_MUSIC_FROM_DB_SUCCESS);
			}
		}else {
			updateDataFromCloud();
		}
	}
	
	public void reloadMusicList() {
		String input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		String listInput = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.LIST_INPUT, StaticConstant.INPUT_TYPE_USB);
		
		Logger.debug(null, "input:" + input + ";listInput:" + listInput);
		if(!input.equalsIgnoreCase(listInput)) {
			updateDataFromCloud();
		}
	}
	
	/**
	 * 从数据库加载歌曲列表
	 */
	private List<Music> loadDataFromDb() {
		musicDaoImpl = new MusicDaoImpl(getActivity());
		return (List<Music>) musicDaoImpl.find(null, null, null, null, null, "no", null);
	}
	
	/**
	 * 刷新item
	 */
	public void refreshItem(){
		handler.sendEmptyMessage(REFRESH_MUSIC);
	}
	
	/**
	 * 更新视图
	 */
	private void updateListView() {
		adapter.setList(songs);
		adapter.setSelectIndex(-1);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 加载数据，从平台加载
	 */
	private void updateDataFromCloud() {
		// 设置输入源，默认为USB
		String input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		
		JxshApp.showLoading(getActivity(), "正在从云端加载歌曲列表...");
		
		doUpdateDataFromCloud(input.toLowerCase(Locale.CHINA));
	}
	
	/**
	 * 同步歌曲列表
	 */
	private void syncDataFromCloud() {
		String source = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, "");
		if (TextUtils.isEmpty(source)) {
			CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getActivity());
			List<CloudSetting> csList = csDaoImpl.find(null, "customer_id=? and items=?",
					new String[]{CommUtil.getCurrentLoginAccount(),
				StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD }, null, null, null, null);
			if (csList != null && csList.size() > 0) {
				source = csList.get(0).getParams();
			}
		}
		if (TextUtils.isEmpty(source)) {
			source = StaticConstant.INPUT_TYPE_USB;
		}
		
		if(source.equalsIgnoreCase(StaticConstant.INPUT_TYPE_SD) || source.equalsIgnoreCase(StaticConstant.INPUT_TYPE_USB)) {
			JxshApp.showLoading(getActivity(), "正在从云端同步歌曲列表...");
			doUpdateDataFromCloud(source);
		}else {
			JxshApp.showToast(getActivity(), "只有在输入源为USB或SD卡时，才能获取歌曲列表");
			return;
		}
	}
	
	private void doUpdateDataFromCloud(final String source) {
		UpdateMusicListTask updateTask = new UpdateMusicListTask(getActivity(), source,
				productFun.getWhId());
		
		updateTask.addListener(new ITaskListener<ITask>() {
			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.closeLoading();
				songs.clear();
				handler.sendEmptyMessage(GET_MUSIC_LIST_FAIL);
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if(arg != null && arg.length > 0) {
					
					List<Music> tempList = (List<Music>)arg[0];
					try {
//						songs.addAll((List<Music>)arg[0]);
						
						// 保存到数据库
						MusicDaoImpl musicDaoImpl = new MusicDaoImpl(getActivity());
						musicDaoImpl.clear();
						
						SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.LIST_INPUT, source);
						
						for(Music m : tempList) {
							musicDaoImpl.insert(m, true);
						}
						songs.clear();
						songs = loadDataFromDb();
						
					} catch (ClassCastException e) {
						Logger.error(null, "the format of music name list get from server is wrong");
					}
					
					// 更新界面
					handler.sendEmptyMessage(GET_MUSIC_LIST_SUCCESS);
				}
				JxshApp.closeLoading();
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		
		updateTask.start();
	}
	
	
	private void sendCmd(int index) {
		String source = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		String line = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "00000000");
		params.put(StaticConstant.PARAM_MUSIC_INDEX, index);
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, line);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, source);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_INPUT, source);
		
		byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(), productFun, funDetail, params);
		if(cmd == null) {
			return;
		}
		patternOperationSendList(index,cmd);
	}
	
	/**
	 * 一次发送多条指令的集合
	 */
	private void patternOperationSendList(final int index, byte[] cmd){
		
		TaskListener<ITask> listener = new TaskListener<ITask>(){

			@Override
			public void onStarted(ITask task, Object arg) {
				Logger.debug(null, "music onstart...");
				JxshApp.showLoading(getActivity(), getResources().getString(R.string.comm_loading_send_cmd_text));
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				Logger.debug(null, "music onfail...");
				JxshApp.showToast(getActivity(), "播放失败");
				JxshApp.closeLoading();
			}

			@Override
			public void onAllSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "music onSuccess...");
				if(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY_SONG.equals(productFun.getFunType())) {
					SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, true);
					SharedDB.saveIntDB(MusicSetting.SP_NAME, MusicSetting.CURRENT_PLAYING_SONG, index);
					handler.sendEmptyMessage(REFRESH_MUSIC);
				}
				JxshApp.closeLoading();
				if (getActivity() == null || getActivity().getSupportFragmentManager().findFragmentByTag("music") == null) {
					Logger.debug(null, "music tag is null");
					return;
				}
				MusicFragment mFragment = (MusicFragment) getActivity().getSupportFragmentManager().findFragmentByTag("music");
				if (mFragment != null) {
					mFragment.refreshPlayStatus();
				}
			}
			
		};
		
		OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(null, cmd);
		cmdSender.addListener(listener);
		cmdSender.send();
		
	}
	
	/**
	 * 检查扬声器设置
	 */
	private boolean checkInputSourceSetting() {
		String selectedSpeaker = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, 
				StaticConstant.INPUT_TYPE_USB);
		if(CommUtil.isEmpty(selectedSpeaker)) {
			MessageBox msgBox = new MessageBox(getActivity(), "提示", "使用播放功能前请设置扬声器", MessageBox.MB_OK);
			msgBox.setButtonText("确定", null);
			msgBox.show();
			return false;
		}
		return true;	
	}
	
	/**
	 * 用于Fragment之间数据交互的接口，Fragment依附的Acitivity必须实现这个接口
	 * @author 	TangLong
	 * @company 金鑫智慧
	 */
	public interface OnPlayStateChangeListener {
		public void onPlayStateChange(int state);
	}
	
	///////////////////////////////////////////////////////
	// 本地加载歌曲列表
	///////////////////////////////////////////////////////
	int loadMusicNameIndex = 0;
	int songSum = 0;
	TaskListener<ITask> listener = new TaskListener<ITask>() {
		public void onStarted(ITask task, Object arg) {
			Logger.debug(null, "onstart...");
		}

		@Override
		public void onCanceled(ITask task, Object arg) {
			JxshApp.closeLoading();
			Logger.debug(null, "oncancel...");
		}

		@Override
		public void onFail(ITask task, Object[] arg) {
			JxshApp.closeLoading();
			Logger.debug(null, "onFail...");
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			Logger.debug(null, "onSuccess...");
			// 对结果做判断处理
			if(arg != null && arg.length > 0) {
				String result = (String) arg[0];
				Logger.debug("tangl.playlist.success", result);
				
				// 如果结果中包含“filesum”，说明获取的是歌曲数量
				if (isGetSongSum(result)) {
					songSum = getSongSum(result);
					Logger.debug("tangl.song.size", "歌曲总数量: " + songSum);

					if (songSum < 1) {
						return;
					}
					
					/////////////////////////////////////////
					// 递归获取歌曲列表
					////////////////////////////////////////
					Logger.debug("tangl.song.list", "开始递归获取歌曲...");
					productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_SONG);
					OfflineCmdGenerator offlineGenerator = new OfflineCmdGenerator();
					String source = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
					String line = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "00000000");
					params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, line);
					params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, source);
					
					List<byte[]> cmdList = new ArrayList<byte[]>();
					for(int i = 0; i< songSum; i++) {
						params.put(StaticConstant.PARAM_MUSIC_INDEX, i);
						List<byte[]> tempList = offlineGenerator.generateCmd(getActivity(), productFun, funDetail, params);
						cmdList.addAll(tempList);
					}
					
					getSongNameFromLocal(cmdList);
				} else if (isGetSongName(result)) {
					String temp = getSongName(result);
					temp = temp.substring(0, temp.length() - 6);
					Logger.warn("tangl.song.name", temp);
					String str = GenPowerAmplifier.getLongMusicName(temp);
					Logger.debug("tangl.song.name", str + "");
					Music m = new Music();
					m.setTitle(str);
					m.setId(loadMusicNameIndex);
					songs.add(m);
				}
			}
		}

		@Override
		public void onProcess(ITask task, Object[] arg) {
			
		}
		
		private boolean isGetSongSum(String str) {
			if(!CommUtil.isEmpty(str)) {
				return str.contains("filesum");
			}
			return false;
		}
		
		private boolean isGetSongName(String str) {
			if(!CommUtil.isEmpty(str)) {
				if(str.contains("filesumusb")) {
					return true;
				}else if(str.contains("filesumsd")) {
					return true;
				}else if(str.contains("filesumaux")) {
					return true;
				}else if(str.contains("filesuminput1")) {
					return true;
				}else if(str.contains("filesuminput2")) {
					return true;
				}else if(str.contains("filesuminput3")) {
					return true;
				}else if(str.contains("filesuminput4")) {
					return true;
				}
			}
			return false;
		}
		
		private int getSongSum(String str) {
			if(!CommUtil.isEmpty(str)) {
				String musicCount = null;
				if(str.contains("filesumusb")) {
					musicCount = str.replace("filesumusb", "").substring(0, 4);
				}else if(str.contains("filesumsd")) {
					musicCount = str.replace("filesumsd", "").substring(0, 4);
				}else if(str.contains("filesumaux")) {
					musicCount = str.replace("filesumaux", "").substring(0, 4);
				}else if(str.contains("filesuminput1")) {
					musicCount = str.replace("filesuminput1", "").substring(0, 4);
				}else if(str.contains("filesuminput2")) {
					musicCount = str.replace("filesuminput1", "").substring(0, 4);
				}else if(str.contains("filesuminput3")) {
					musicCount = str.replace("filesuminput3", "").substring(0, 4);
				}else if(str.contains("filesuminput4")) {
					musicCount = str.replace("filesuminput4", "").substring(0, 4);
				}
				if(musicCount != null) {
					return Integer.parseInt(musicCount);
				}
			}
			return 0;
		}
		
		private String getSongName(String str) {
			String musicName = "";
			if(!CommUtil.isEmpty(str)) {
				if(str.contains("filesumusb")) {
					musicName = str.replace("filesumusb", "").substring(12);
				}else if(str.contains("filesumsd")) {
					musicName = str.replace("filesumsd", "").substring(12);
				}else if(str.contains("filesumaux")) {
					musicName = str.replace("filesumaux", "").substring(12);
				}else if(str.contains("filesuminput1")) {
					musicName = str.replace("filesuminput1", "").substring(12);
				}else if(str.contains("filesuminput2")) {
					musicName = str.replace("filesuminput1", "").substring(12);
				}else if(str.contains("filesuminput3")) {
					musicName = str.replace("filesuminput3", "").substring(12);
				}else if(str.contains("filesuminput4")) {
					musicName = str.replace("filesuminput4", "").substring(12);
				}
			}
			return musicName;
		}
	};
	
	/**
	 * 加载数据，从本地网关加载
	 */
	@SuppressWarnings("unused")
	private void updateDataFromLocal() {
		/*	获取歌曲列表长度	 */
		String listInput = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.LIST_INPUT, StaticConstant.INPUT_TYPE_USB);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, listInput.toLowerCase(Locale.CHINA));
		
		JxshApp.showLoading(getActivity(), "正在从网关加载歌曲列表...");
		
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_LIST);
		
		ICmdGenerator offlineCmdGenerator = new OfflineCmdGenerator();
		List<byte[]> cmdList = offlineCmdGenerator.generateProductPatternOperationCmd(getActivity(), productFun, funDetail, params);
		
		if(cmdList != null && cmdList.size() > 0) {
			OfflineCmdSenderLong offlineCmdSender = new OfflineCmdSenderLong(getActivity(), cmdList);
			offlineCmdSender.addListener(listener);
			offlineCmdSender.send();
		}
	}
	
	/**
	 * 获取歌曲名称
	 * @param cmdList	获取歌曲名称的命令列表
	 */
	private void getSongNameFromLocal(List<byte[]> cmdList) {
		OfflineCmdSenderLong offlineCmdSender = new OfflineCmdSenderLong(getActivity(), cmdList);
		offlineCmdSender.addListener(listener);
	}
	
}	