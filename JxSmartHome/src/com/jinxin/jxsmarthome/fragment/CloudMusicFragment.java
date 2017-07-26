package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.AddCustomerMusicTask;
import com.jinxin.datan.net.command.DeleteCustomerMusicTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.impl.CustomerMusicLibDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.MusicLibDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.aidl.MusicServiceAidl;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.CustomerMusicLib;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.service.MusicService;
import com.jinxin.jxsmarthome.ui.adapter.DlnaDeviceListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.HotMusicListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.ViewPagerAdapter;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.ui.widget.lrcview.Lyric;
import com.jinxin.jxsmarthome.ui.widget.lrcview.LyricView;
import com.jinxin.jxsmarthome.ui.widget.lrcview.PlayListItems;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.DateUtil;
import com.jinxin.record.FileManager;
import com.jinxin.record.FileUtil;
import com.jinxin.record.SharedDB;

/**
 * 音乐播放界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
@SuppressLint("HandlerLeak")
public class CloudMusicFragment extends Fragment implements OnClickListener, OnPageChangeListener{
	
	private LinearLayout viewGroup;
	private ImageView btnFav;
	private ViewPager viewPager;
	private SeekBar durationBar;//进度条
	private ImageView playBtn,preBtn,nextBtn;
	private LyricView lyricView;//歌词显示View
	private TextView tv_currentTime,tv_maxTimeText;
	private ListView lv_musicList;//音乐列表ListView
	private DlnaDeviceListAdapter adapter;
	private HotMusicListAdapter listAdapter;
	
	private String NOW_STATUS = "";//当前播放状态
	private String currentTime;//当前播放时间
	private String maxTime;//歌曲总时长
	private int currTime = 0;//当前播放毫秒数
	private int totalTime = 0;//总播放毫秒数
	
	private ImageView mMute;
	private ImageView mSoundAdd;
	private ImageView mSoundSub;
	private ImageView mRepeatPlay;
	private ImageView mShowMenu;
	private LinearLayout mHideControl;
	
    private ImageView[] pointArr;//装点点的ImageView数组
    private ArrayList<View> viewList;
    private ViewPagerAdapter pagerAdapter;
    private int musicId = 0;//当前播放音乐ID
    private MusicLib musicLib;//本地存储音乐
    private String musicUrl;//音乐播放地址
    private String lrcUrl = "";//歌词地址
    private List<MusicLib> musicList;//音乐播放列表
    private int enjoy = 0;//是否收藏的音乐
    private CustomerMusicLibDaoImpl cmDaoImpl;
    
    private String songName;
	private String singerName;
    
    private boolean isPlaying = true;//是否正在播放
    private boolean isRunning = true;
    private boolean isNewSong = false;//是否播放新歌
    private MusicUpdateThread musicThread;
    
    private ProductFun productFun; // 产品对象
	private FunDetail funDetail; // 设备对象
	private boolean isMute; // 是否为静音状态的标志
	private boolean isRepeatPlay; // 单曲循环标志
	private Map<String, Object> params = new HashMap<String, Object>(); // 发送指令的参数
	private String[] speakerNames; // 扬声器名字
	private String inputOrigionalSet;
	private String speakerOrigionalSet;
    
    private List<String> deviceList;//DLNA设备名List
    
    private static final int LIKE_BACKGROUND = 1;
    private static final int UNLIKE_BACKGROUND = 2;
    
    private MusicServiceAidl serviceBinder = null;
    
    
    private ServiceConnection mConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        serviceBinder = MusicServiceAidl.Stub.asInterface(service);
	    }
	    public void onServiceDisconnected(ComponentName className) {
	    	serviceBinder = null;  
	    }  
    };
    
	Handler handler = new Handler(){

		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case 0:
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
				break;
			case LIKE_BACKGROUND:
				btnFav.setImageResource(R.drawable.icon_music_fav_hover);
				break;
			case UNLIKE_BACKGROUND:
				btnFav.setImageResource(R.drawable.icon_fav_no);
				break;
			}
		}
    	
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(getActivity(),MusicService.class);
		getActivity().bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(BroadcastManager.ACTION_DLNA_DEVICE_LIST_ADD);
		mFilter.addAction(BroadcastManager.ACTION_DLNA_DEVICE_LIST_REMOVE);
		mFilter.addAction(BroadcastManager.ACTION_MUSIC_UPDATE_TIME);
		mFilter.addAction(BroadcastManager.ACTION_MUSIC_CHANGE_NEXT_SONG);
		mFilter.addAction(BroadcastManager.ACTION_MUSIC_PAUSE);
		getActivity().registerReceiver(dlnaListReceiver, mFilter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cloud_music_layout, container, false);
		initData();
		// 初始化扬声器设置
		initSettings();
		initView(view);
		
		setHasOptionsMenu(true);
		
		return view;
	}
	
	/**
	 * 初始化数据
	 */
	@SuppressLint("NewApi")
	private void initData(){
		this.deviceList = new ArrayList<String>();
		this.cmDaoImpl = new CustomerMusicLibDaoImpl(getActivity());
		MusicLibDaoImpl mlDaoImpl = new MusicLibDaoImpl(getActivity());
		this.musicList = mlDaoImpl.find();
		
		musicThread = new MusicUpdateThread();
		musicThread.start();
		
		this.productFun = (ProductFun) getArguments().get("productFun");
		this.funDetail = (FunDetail) getArguments().get("funDetail");
		this.isMute = false;
		this.isRepeatPlay = false;
//		this.musicLib = (MusicLib) getArguments().getSerializable("musicLib");
		this.musicLib = JxshApp.instance.getMusicLib();//得到Application里的音乐对象进行播放
		
		if (musicLib == null) {//为空，则播放历史列表第一首歌
			if (musicList != null && musicList.size() > 0) {
				musicLib = musicList.get(0);
				//当前没播放歌曲则自动开始播放
				if (musicId != musicLib.getId()) {
					getIsFavMusic(musicLib);
//					playMusic(musicLib);
				}
			}
		}else{
			getIsFavMusic(musicLib);
			setIsNewSong(true);
			//当前没播放歌曲则自动开始播放
			if (musicId != musicLib.getId()) {
				playMusic(musicLib);
			}
		}
	}
	/**
	 * 检测是否可执行后台切换歌曲
	 * @param isNewSong
	 */
	private void setIsNewSong(boolean isNewSong){
		if ((!this.isNewSong == isNewSong)) {//变量发生变化时往下执行
			// 如果是新歌的话，设置变量并发送广播
			this.isNewSong = isNewSong;
			Bundle bundle = new Bundle();
			bundle.putBoolean("isNewSong", isNewSong);
			BroadcastManager.sendBroadcast(BroadcastManager.ACTION_CHANG_MUSIC_IN_BACKGROUND, bundle);
		}
	}
	
	/**
	 * 判断是否是收藏音乐
	 * @param musicLib
	 */
	private void getIsFavMusic(MusicLib musicLib){
		List<CustomerMusicLib> _list = cmDaoImpl.find(null, "musicName=? and singer=?",
				new String[]{musicLib.getName(),musicLib.getSinger()}, null, null, null, null);
		if (_list != null && _list.size() > 0) {
			enjoy = 1;
		}else{
			enjoy = 0;
		}
	}
	
	/**
	 * 播放状态监听
	 */
	class MusicUpdateThread extends Thread{

		@Override
		public void run() {
			while(isRunning){
				if (serviceBinder != null) {
					try {
						NOW_STATUS = serviceBinder.getPlayStatus();
						if (MusicService.PLAYSTATUS.PLAYING.toString().equals(NOW_STATUS)) {
							JxshApp.closeLoading();
							setIsNewSong(false);
							maxTime = DateUtil.formatRegularTime(serviceBinder.getDurationTime());
							currentTime = DateUtil.formatRegularTime(serviceBinder.getCurrentTime());
							totalTime = DateUtil.convertStrToLongShort(maxTime);
							currTime = DateUtil.convertStrToLongShort(currentTime);
							if (isPlaying) {//发生广播更新进度
								Intent threadIntent = new Intent(BroadcastManager.ACTION_MUSIC_UPDATE_TIME);
								threadIntent.putExtra("currentTime", currentTime);
								threadIntent.putExtra("maxTime", maxTime);
								if (totalTime > 0) {
									int progress = (currTime*100/totalTime);
									threadIntent.putExtra("progress", progress);
								}
								getActivity().sendBroadcast(threadIntent);
							}
							musicLib = JxshApp.instance.getMusicLib();
							if (musicId != musicLib.getId()) {//通过ID 判断是否播放新的歌曲，如果是则切换歌词
								musicId = musicLib.getId();
								Bundle _bundle = new Bundle();
								_bundle.putSerializable("musicLib", musicLib);
								BroadcastManager.sendBroadcast(BroadcastManager.ACTION_MUSIC_CHANGE_NEXT_SONG, _bundle);
							}
						}else if(MusicService.PLAYSTATUS.TRANSITIONING.toString().equals(NOW_STATUS)){
							JxshApp.showLoading(getActivity(), "缓冲中，请稍后...");
						}
//						else if(MusicService.PLAYSTATUS.STOPPED.toString().equals(NOW_STATUS)){
//							JxshApp.closeLoading();
//							if (!isNewSong) {
//								if (!isRepeatPlay) {
//									if (musicList != null && musicList.size() > 0) {
//										if (musicId < musicList.size()) {
//											musicId++;
//										}else{
//											musicId = 1;
//										}
//										musicLib = musicList.get(musicId-1);
//									}
//									
//								}
//								Intent _intent = new Intent(BroadcastManager.ACTION_MUSIC_CHANGE_NEXT_SONG);
//								_intent.putExtra("musicLib", musicLib);
//								getActivity().sendBroadcast(_intent);
//							}
//						}
						else {
							BroadcastManager.sendBroadcast(BroadcastManager.ACTION_MUSIC_PAUSE, null);
						}
						Thread.sleep(1000);
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	/**
	 * 初始化布局
	 * @param view
	 */
	private void initView(View view){
		this.viewGroup = (LinearLayout) view.findViewById(R.id.view_group);
		this.viewPager = (ViewPager) view.findViewById(R.id.view_pager);
		this.durationBar = (SeekBar) view.findViewById(R.id.duration_seekbar);
		this.playBtn = (ImageView) view.findViewById(R.id.music_play_control);
		this.preBtn = (ImageView) view.findViewById(R.id.pre_music_btn);
		this.nextBtn = (ImageView) view.findViewById(R.id.next_music_btn);
		this.tv_currentTime = (TextView) view.findViewById(R.id.current_time_text);
		this.tv_maxTimeText = (TextView) view.findViewById(R.id.max_time_text);
		this.mShowMenu = (ImageView) view.findViewById(R.id.show_music_menu_iv);
		this.mHideControl = (LinearLayout) view.findViewById(R.id.music_menu_layout);
		this.mRepeatPlay = (ImageView) view.findViewById(R.id.music_play_mode_iv);
		this.mSoundAdd = (ImageView) view.findViewById(R.id.voice_up_iv);
		this.mSoundSub = (ImageView) view.findViewById(R.id.voice_down_iv);
		this.mMute = (ImageView) view.findViewById(R.id.voice_mute_open_iv);
		
		playBtn.setOnClickListener(this);
		preBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		mShowMenu.setOnClickListener(this);
		mRepeatPlay.setOnClickListener(this);
		mSoundAdd.setOnClickListener(this);
		mSoundSub.setOnClickListener(this);
		mMute.setOnClickListener(this);
		
		initViewPage();
		showLrc(musicLib);
		
		durationBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int seekTime = durationBar.getProgress()*totalTime/100;
				Bundle bundle = new Bundle();
				bundle.putString("seekTo", DateUtil.parseTime(seekTime));
				BroadcastManager.sendBroadcast(BroadcastManager.ACTION_MUSIC_PLAY_SEEK, bundle);
				lyricView.updateIndex(seekTime, handler);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
//				getActivity().sendBroadcast(new Intent(BroadcastManager.ACTION_MUSIC_PLAY_PAUSE));//先暂停播放音乐
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
			}
		});
		
	}
	
	/**
	 * 初始化ViewPager
	 */
	@SuppressLint("InflateParams")
	private void initViewPage() {
		// 填充歌词的ViewPager
		viewList = new ArrayList<View>();
		@SuppressWarnings("static-access")
		LayoutInflater lf = getLayoutInflater(getArguments()).from(
				getActivity());
		View lrcView = lf.inflate(R.layout.music_lrc_layout, null);
		View infoView = lf.inflate(R.layout.music_info_layout, null);
		viewList.add(lrcView);
		viewList.add(infoView);
		pagerAdapter = new ViewPagerAdapter(viewList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);

		listAdapter = new HotMusicListAdapter(getActivity(), musicList);
		this.lyricView = (LyricView) lrcView.findViewById(R.id.lrcview);
		this.btnFav = (ImageView) lrcView.findViewById(R.id.fav_btn);
		this.lv_musicList = (ListView) infoView.findViewById(R.id.current_music_lv);
		lyricView.setBrackgroundcolor(Color.TRANSPARENT);
		setFavBtnBackground();
		btnFav.setOnClickListener(this);
		lv_musicList.setAdapter(listAdapter);
		lv_musicList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MusicLib music = musicList.get(position);
				if (music != null) {
					updataMusic(music);
				}
			}
		});
		
		// 添加ViewPager选项标识
		this.pointArr = new ImageView[viewList.size()];
		for (int i = 0; i < pointArr.length; i++) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new LayoutParams(20, 20));
			pointArr[i] = imageView;
			if (i == 0) {
				pointArr[i].setBackgroundResource(R.drawable.icon_switch_white);
			} else {
				pointArr[i].setBackgroundResource(R.drawable.icon_switch_black);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			viewGroup.addView(imageView, layoutParams);
			
		}
	}
	
	/**
	 * 设置收藏按钮背景
	 */
	private void setFavBtnBackground(){
		if (enjoy == 1) {
			btnFav.setImageResource(R.drawable.icon_music_fav_hover);
		}else{
			btnFav.setImageResource(R.drawable.icon_fav_no);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		isMute = SharedDB.loadBooleanFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, false);
		isRepeatPlay =  SharedDB.loadBooleanFromDB(MusicSetting.SP_NAME, MusicSetting.CLOUD_MUSIC_PLAY_MODE, false);
		recoveryPlayState();
		recoveryRepeatPlay();
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.dlna_device_button:
			
			final MessageBox mb = new MessageBox(getActivity(),"搜索设备", "是否重新搜索DLNA设备",
					MessageBox.MB_OK | MessageBox.MB_CANCEL);
			mb.setButtonText("确定", "取消");
			if (!mb.isShowing()) {
				mb.show();
			}
			mb.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					switch (mb.getResult()) {
					case MessageBox.MB_OK://点击消失
						mb.dismiss();
						getActivity().getApplicationContext().sendBroadcast(new
								Intent(BroadcastManager.ACTION_DLNA_DEVICE_SEARCH));
						showDlnaDialog();
						break;
					case MessageBox.MB_CANCEL:
						mb.dismiss();
						showDlnaDialog();
						break;
					}
				}
			});
			break;
		}
		return true;
	}
	
	/**
	 * 初始化歌词显示
	 */
	@SuppressLint("NewApi")
	private void showLrc(MusicLib musicLib){
		if (musicLib == null) return;
		songName = musicLib.getName();
		singerName = musicLib.getSinger();
		lrcUrl = musicLib.getLrcUrl();
		if (!TextUtils.isEmpty(songName)) {
			((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle(songName);
		}
		FileManager _fm = new FileManager();
		String localPath = _fm.getSDPath()+ FileManager.PROJECT_NAME
				+ FileManager.CACHE +FileManager.LRC+"/";
		FileUtil.checkDirectory(localPath);
		
		PlayListItems items = new PlayListItems(localPath,
				songName, singerName, lrcUrl);
		Lyric lrc = new Lyric(items);
		lyricView.setmLyric(lrc);
		lyricView.setSentencelist(lrc.list);
		lyricView.setCurrentPaintColor(Color.WHITE);
		lyricView.setNotCurrentPaintColor(Color.GRAY);
		lyricView.setCurrentTextSize(40);
		lyricView.setTextSize(20);
		lyricView.setTexttypeface(Typeface.SERIF);
		lyricView.setTextHeight(40);
	}
	
	/**
	 * 隐藏、显示音量调节界面
	 */
	private void showHideControllView() {
		if (mHideControl.getVisibility() == View.VISIBLE) {
			ScaleAnimation gongAnimation = new ScaleAnimation(1, 1, 1, 0,
					ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
					ScaleAnimation.RELATIVE_TO_SELF, 0.0f);
			gongAnimation.setDuration(250);
			mHideControl.startAnimation(gongAnimation);
			mHideControl.setVisibility(View.INVISIBLE);
		} else {
			ScaleAnimation showAnimation = new ScaleAnimation(1, 1, 0, 1,
					ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
					ScaleAnimation.RELATIVE_TO_SELF, 0.0f);
			showAnimation.setDuration(250);
			mHideControl.startAnimation(showAnimation);
			mHideControl.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 显示DLNA设备列表弹出框
	 */
	@SuppressLint("InflateParams")
	private void showDlnaDialog(){
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(
				R.layout.custom_dialog_open_dlna, null);
		ListView listView = (ListView) dialogView.findViewById(R.id.dlna_listview);
		Button cancleBtn = (Button) dialogView.findViewById(R.id.dlna_cance_btn);
		adapter = new DlnaDeviceListAdapter(getActivity(), deviceList);
		listView.setAdapter(adapter);
		final Dialog mDialog = BottomDialogHelper.showDialogInBottom(getActivity(),
				dialogView, null);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(BroadcastManager.ACTION_DLNA_DEVICE_CHOOSE);
				intent.putExtra("name", deviceList.get(position));
				String _account = CommUtil.getCurrentLoginAccount();
				SharedDB.saveStrDB(_account,ControlDefine.
						KEY_DLNA_NAME,deviceList.get(position));//保存选中的播放设备名称
				getActivity().sendBroadcast(intent);
				handler.sendEmptyMessage(0);
				mDialog.dismiss();
			}
		});
		cancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.fav_btn://收藏
			if (enjoy == 0) {
				sendFavTask(enjoy);
			}else{
				if (musicLib != null) {
					List<CustomerMusicLib> _list = cmDaoImpl.find(null, "musicName=? and singer=?",
							new String[]{musicLib.getName(),musicLib.getSinger()}, null, null, null, null);
					if (_list != null && _list.size() > 0) {
						CustomerMusicLib _cml = _list.get(0);
						deleteFavTask(_cml.getId());
					}
				}
			}
			break;
		case R.id.music_play_control://播放、暂停
			if (NOW_STATUS.equals(MusicService.PLAYSTATUS.PLAYING.toString())) {
				intent.setAction(BroadcastManager.ACTION_MUSIC_PLAY_PAUSE);
				playBtn.setImageResource(R.drawable.selecter_music_play);
			}else{
				playMusic(musicLib);
			}
			break;
		case R.id.pre_music_btn://上一曲
			if (musicList == null || musicList.size() < 1) {
				return;
			}
			if (musicId == 0 || musicId == 1) {
				musicId = musicList.size();
			}else if (musicId > 1){
				musicId--;
			}
			musicLib = musicList.get(musicId-1);
			updataMusic(musicLib);
			setIsNewSong(true);
			break;
		case R.id.next_music_btn://下一曲
			if (musicList == null || musicList.size() < 1) {
				return;
			}
			if (musicId < musicList.size()) {
				musicId++;
			}else{
				musicId = 1;
			}
			musicLib = musicList.get(musicId-1);
			updataMusic(musicLib);
			setIsNewSong(true);
			break;
		case R.id.show_music_menu_iv://显示隐藏音量调节
			showHideControllView();
			break;
		case R.id.music_play_mode_iv://重复播放
			isRepeatPlay();
			break;
		case R.id.voice_down_iv://音量减
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_SUB); // 操作指令为“减小音量”
			sendCmdList();
			break;
		case R.id.voice_up_iv://音量加
			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_ADD); // 操作指令为“增加音量”
			sendCmdList();
			break;
		case R.id.voice_mute_open_iv://静音
			if (isMute) {
				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_UNMUTE); // 操作指令为“取消静音”
				SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, false);
			} else {
				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE); // 操作指令为“静音”
				SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, true);
			}
			getIsMute();
			sendCmdList();
			break;
		}
		getActivity().sendBroadcast(intent);
	}
	
	/**
	 * 改变静音按钮图片
	 */
	private void getIsMute(){
		if (isMute) {
			mMute.setImageResource(R.drawable.selector_btn_music_mute);
			isMute = false;
		}else{
			mMute.setImageResource(R.drawable.selector_btn_music_unmute);
			isMute = true;
		}
	}
	
	/**
	 * 播放音乐
	 */
	private void playMusic(MusicLib musicLib){
		if (musicLib == null) {
			JxshApp.showToast(getActivity(), "无可歌曲播放");
			return;
		}
		musicId = musicLib.getId();
		musicUrl = musicLib.getNetUrl();
		if (TextUtils.isEmpty(musicUrl)) {
			JxshApp.showToast(getActivity(), "歌曲地址为空");
		}else{
			Intent intent = new Intent();
//			intent.putExtra("musicId", musicId);
//			intent.putExtra("musicUrl", musicUrl);
			intent.putExtra("musicLib", musicLib);
			intent.setAction(BroadcastManager.ACTION_MUSIC_PLAY_CONTROL);
			getActivity().sendBroadcast(intent);
			JxshApp.instance.setMusic(musicLib);
		}
	}
	
	/**
	 * 发送添加收藏的请求
	 * @param enjoy
	 */
	private void sendFavTask(final int _enjoy){
		if (musicLib != null) {
			final String singer = musicLib.getSinger();
			final String musicName = musicLib.getName();
			AddCustomerMusicTask cmTask = new AddCustomerMusicTask(getActivity(), musicId, singer, musicName, enjoy);
				cmTask.addListener(new ITaskListener<ITask>() {

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
							String userId = SharedDB.loadStrFromDB(SharedDB.
									ORDINARY_CONSTANTS,ControlDefine.KEY_ACCOUNT, "");
							int id = (Integer) arg[0];
							enjoy = 1;
							String storeTime = DateUtil.convertLongToStr1(System.currentTimeMillis());
							CustomerMusicLib cMusic = new CustomerMusicLib(
									id, userId, musicId, singer, musicName, storeTime, enjoy);
							cmDaoImpl.insert(cMusic);
							handler.sendEmptyMessage(LIKE_BACKGROUND);
						}
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
					}
				});
				JxshApp.getDatanAgentHandler().postDelayed(cmTask,10);
		}
	}

	private void deleteFavTask(int id){
		final int _id = id;
		DeleteCustomerMusicTask dcmTask = new DeleteCustomerMusicTask(getActivity(), id);
		dcmTask.addListener(new ITaskListener<ITask>() {

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
				cmDaoImpl.delete(_id);
				enjoy = 0;
				handler.sendEmptyMessage(UNLIKE_BACKGROUND);
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		JxshApp.getDatanAgentHandler().postDelayed(dcmTask,10);
	}
	
	/**
	 * 初始化功放扬声器设置
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
//					SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.INPUT,
//							inputOrigionalSet);
				}
			}
		} else {
			initDefaultSettings();
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
	 * 重复播放状态
	 */
	private void isRepeatPlay(){
		if (isRepeatPlay) {
			isRepeatPlay = false;
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.CLOUD_MUSIC_PLAY_MODE, false);
			mRepeatPlay.setImageResource(R.drawable.selector_btn_music_cycle);
		}else{
			isRepeatPlay = true;
			SharedDB.saveBooleanDB(MusicSetting.SP_NAME, MusicSetting.CLOUD_MUSIC_PLAY_MODE, true);
			mRepeatPlay.setImageResource(R.drawable.selector_btn_music_single);
		}
	}
	
	/**
	 * 重复播放状态恢复
	 */
	private void recoveryRepeatPlay() {
		if (isRepeatPlay) {
			mRepeatPlay.setImageResource(R.drawable.selector_btn_music_single);
		} else {
			mRepeatPlay.setImageResource(R.drawable.selector_btn_music_cycle);
		}
	}
	
	/**
	 * 播放按钮状态恢复
	 */
	private void recoveryPlayState(){
		if (NOW_STATUS.equals(MusicService.PLAYSTATUS.PLAYING.toString())) {
			playBtn.setImageResource(R.drawable.selecter_music_pause);
		}else{
			playBtn.setImageResource(R.drawable.selecter_music_play);
		}
	}
	
	/**
	 * 更新播放音乐
	 * @param baiduMusic
	 */
	private void updataMusic(MusicLib musicLib){
		if (musicLib == null) {
			return;
		}
		showLrc(musicLib);
		getIsFavMusic(musicLib);
		setFavBtnBackground();
		playMusic(musicLib);
	}

	/**
	 * 发送命令
	 */
	private void sendCmdList() {
		String input = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.INPUT, 
				StaticConstant.INPUT_TYPE_USB).toLowerCase(Locale.CHINA);
		String speaker = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				"00010000");
		
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speaker);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, input);
		
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(),
				productFun, funDetail, params);
		}else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(),
					productFun, funDetail, params);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(getActivity(), cmd);
			cmdSender.send();
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		setImageBackground(arg0);
	}
	
	/**
	 * DLNA设备列表添加、删除广播接收器
	 */
	private BroadcastReceiver dlnaListReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BroadcastManager.ACTION_DLNA_DEVICE_LIST_ADD.equals(action)) {
				String name = intent.getStringExtra("name");
				deviceList.add(name);
				handler.sendEmptyMessage(0);
			}else if(BroadcastManager.ACTION_DLNA_DEVICE_LIST_REMOVE.equals(action)){
				if (deviceList.size() < 1) return;
				String name = intent.getStringExtra("name");
				for (int i = 0; i < deviceList.size(); i++) {
					if (deviceList.get(i).equals(name)) {
						deviceList.remove(i);
						handler.sendEmptyMessage(0);
					}
				}
			}else if(BroadcastManager.ACTION_MUSIC_UPDATE_TIME.equals(action)){//播放中
           	 	try {
					tv_currentTime.setText( intent.getStringExtra("currentTime").substring(3));
					tv_maxTimeText.setText(intent.getStringExtra("maxTime").substring(3));
					durationBar.setProgress(intent.getIntExtra("progress", 0));
					playBtn.setImageResource(R.drawable.selecter_music_pause);
				} catch (Exception e) {
					e.printStackTrace();
				}
           	 	if (lyricView != null) {
           	 		lyricView.updateIndex(currTime, handler);
           	 		lyricView.setinvalidate();
				}
			}else if (BroadcastManager.ACTION_MUSIC_PAUSE.equals(action)) {//播放暂停或停止
				playBtn.setImageResource(R.drawable.selecter_music_play);
			}else if(BroadcastManager.ACTION_MUSIC_CHANGE_NEXT_SONG.equals(action)){
				MusicLib musicLib = (MusicLib) intent.getSerializableExtra("musicLib");
				if (musicLib != null) {
//					updataMusic(musicLib);
					showLrc(musicLib);
				}
			}
		}
	};
	
	/** 
     * 设置选中的tip的背景
     * @param selectItems
     */  
    private void setImageBackground(int selectItems){
        for(int i=0; i<pointArr.length; i++){
            if(i == selectItems){
                pointArr[i].setBackgroundResource(R.drawable.icon_switch_white);  
            }else{
                pointArr[i].setBackgroundResource(R.drawable.icon_switch_black);  
            }  
        }  
    }
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		isPlaying = false;
		isRunning = false;
		setIsNewSong(false);
		if (musicThread.isAlive()) {
			musicThread.interrupt();
		}
		getActivity().unbindService(mConnection);
		if (dlnaListReceiver != null) {
			getActivity().unregisterReceiver(dlnaListReceiver);
		}
	}

	@Override
	public void onDestroyView() {
		isPlaying = false;
		super.onDestroyView();
	}

}
