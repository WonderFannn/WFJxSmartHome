//package com.jinxin.jxsmarthome.fragment;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Stack;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import com.jinxin.jxsmarthome.util.Logger;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.ScaleAnimation;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
//import com.jinxin.datan.net.command.MusicListControlByServerTask;
//import com.jinxin.datan.toolkit.internet.InternetTask;
//import com.jinxin.datan.toolkit.task.ITask;
//import com.jinxin.datan.toolkit.task.ITaskListener;
//import com.jinxin.db.impl.FunDetailConfigDaoImpl;
//import com.jinxin.jxsmarthome.R;
//import com.jinxin.jxsmarthome.activity.BaseActivity;
//import com.jinxin.jxsmarthome.constant.CommonMethodForMusic;
//import com.jinxin.jxsmarthome.constant.GenPowerAmplifier;
//import com.jinxin.jxsmarthome.constant.ProductConstants;
//import com.jinxin.jxsmarthome.entity.FunDetail;
//import com.jinxin.jxsmarthome.entity.FunDetailConfig;
//import com.jinxin.jxsmarthome.entity.ProductFun;
//import com.jinxin.jxsmarthome.main.JxshApp;
//import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
//import com.jinxin.jxsmarthome.util.AnimateUtil;
//import com.jinxin.jxsmarthome.util.CommUtil;
//import com.jinxin.widget.MessageBox;
//
///**
// * 音乐播放控制
// * 
// * @author TangLong
// * @company 金鑫智慧
// */
//public class MusicFragmentOld extends Fragment implements View.OnClickListener {
//	public static final String ARGUMENT_PRODUCTFUN = "ProductFun";
//	public static final String ARGUMENT_FUNDETAIL = "FunDetail";
//	public static final String IS_FROM_HOME = "IS_FROM_HOME";
//	private static final String[] INPUT_ITEMS = {"USB", "SD", "AUX", "INPUT1", "INPUT2", "INPUT3", "INPUT4"};
//
//	private ImageView mAlbumCover;
//	private ImageView mPrevous;
//	private ImageView mNext;
//	private ImageView mPlay;
//	private ImageView mMute;
//	private TextView mSoundAdd;
//	private TextView mSoundSub;
//	private TextView mSpeaker;
//	private Spinner mChoseInput;
//	private ImageView mRepeatPlay;
//	private ImageButton mHideControlSwitcher;
//	@SuppressWarnings("unused")
//	private TextView mSongName;
//	private ViewGroup mHideControl;
//
//	private static SharedPreferences sp;
//	private static String selectedSpeaker;			// 扬声器选择
//	private static ProductFun productFun; 			// 产品对象
//	private static FunDetail funDetail; 			// 设备对象
//	public static boolean isPlaying;				// 是否正在播放的标志
//	public static boolean isListPlaying;			// 列表播放
//	private boolean isMute;							// 是否为静音状态的标志
//	private boolean isRepeatPlay;					// 单曲循环标志
//	private ArrayAdapter<String> inputAdapter;		// 输出源选项适配器
//	public static Map<String, Object> params; 		// 发送指令的参数
//	private static String[] speakerNames;			// 扬声器名字
//	
//	public MusicFragmentOld(){
//		
//	}
//	
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//	}
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		isPlaying = false;
//		isListPlaying = false;
//		isMute = false;
//		isRepeatPlay = false;
//		sp = getActivity().getSharedPreferences(CommUtil.getSPName4Music(), Context.MODE_PRIVATE);
//		productFun = (ProductFun) getArguments().get(ARGUMENT_PRODUCTFUN);
//		funDetail = (FunDetail) getArguments().get(ARGUMENT_FUNDETAIL);
//		funDetail.setShortcutClose("111111");
//		funDetail.setShortcutOpen("111111");
//		funDetail.setShortCutOperation("11111");
//		inputAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_input_choose, INPUT_ITEMS);
//		inputAdapter.setDropDownViewResource(R.layout.custom_spinner_input__choose_down);
//		params = new HashMap<String, Object>();
//		params.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
//		
//		initSpeakerSetting();
//	}
//	
//	private void initSpeakerSetting() {
//		// 同步扬声器名称，从同步数据中获取
//		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(getActivity());
//		List<FunDetailConfig> fdc4Amplifiers = daoImpl.find(null, "funType=?" , 
//				new String[] {ProductConstants.FUN_TYPE_POWER_AMPLIFIER}, null, null, null, null);
//		if(fdc4Amplifiers != null && fdc4Amplifiers.size() > 0) {
//			FunDetailConfig fdc4Amplifier = fdc4Amplifiers.get(0);
//			if(fdc4Amplifier != null && !CommUtil.isEmpty(fdc4Amplifier.getParams())) {
//				try {
//					JSONObject jsonObj = new JSONObject(fdc4Amplifier.getParams());
//					
//					JSONArray jsonArr = jsonObj.getJSONArray("param");
//					
//					int length = jsonArr.length();
//					speakerNames = new String[length];
//					for(int i = 0; i < jsonArr.length(); i++) {
//						speakerNames[i] = (String) jsonArr.get(i);
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		// 初始化扬声器的设置,从SharedPreference数据中获取
//		selectedSpeaker = sp.getString(ProductConstants.SP_MUSIC_SPEAKER, "");
//		if(selectedSpeaker.length() == 8) {
//			params.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, selectedSpeaker);
//		}else {
//			params.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "00000000");
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.music, null);
//		
//		((BaseActivity)getActivity()).setTitle(R.string.music);
//		Button btnRight = (Button)((BaseActivity)getActivity()).findViewById(R.id.button_save);
//		btnRight.setText(R.string.music_list);
//		((BaseActivity)getActivity()).setOnSaveListener(this);
//		
//		mAlbumCover = (ImageView) view.findViewById(R.id.music_iv_album_cover);
//		mPrevous = (ImageView) view.findViewById(R.id.music_iv_pre);
//		mNext = (ImageView) view.findViewById(R.id.music_iv_next);
//		mPlay = (ImageView) view.findViewById(R.id.music_iv_play);
//		mMute = (ImageView) view.findViewById(R.id.music_iv_mute);
//		mSoundAdd = (TextView) view.findViewById(R.id.music_tv_sound_add);
//		mSoundSub = (TextView) view.findViewById(R.id.music_tv_sound_sub);
//		mSpeaker = (TextView) view.findViewById(R.id.music_tv_chose_speaker);
//		mChoseInput = (Spinner)view.findViewById(R.id.music_sp_chose_input);
//		mRepeatPlay = (ImageView)view.findViewById(R.id.music_iv_repeat_play);
//		mHideControlSwitcher = (ImageButton)view.findViewById(R.id.music_hide_control_switcher);
//		mSongName = (TextView)view.findViewById(R.id.music_play_name);
//		mHideControl = (ViewGroup)view.findViewById(R.id.music_ll_center);
//		
//		mHideControlSwitcher.setOnClickListener(this);
//		mPlay.setOnClickListener(this);
//		mNext.setOnClickListener(this);
//		mPrevous.setOnClickListener(this);
//		mNext.setOnClickListener(this);
//		mPlay.setOnClickListener(this);
//		mMute.setOnClickListener(this);
//		mSoundAdd.setOnClickListener(this);
//		mSoundSub.setOnClickListener(this);
//		mSpeaker.setOnClickListener(this);
//		mRepeatPlay.setOnClickListener(this);
//		
//		mChoseInput.setAdapter(inputAdapter);
//		mChoseInput.setOnItemSelectedListener(new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				switch (position) {
//				case 0:
//					params.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, 
//							GenPowerAmplifier.INPUT_TYPE_USB);
//					break;
//				case 1:
//					params.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, 
//							GenPowerAmplifier.INPUT_TYPE_SD);
//					break;
//				case 2:
//					params.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, 
//							GenPowerAmplifier.INPUT_TYPE_AUX);
//					break;
//				case 3:
//					params.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, 
//							GenPowerAmplifier.INPUT_TYPE_INPUT1);
//					break;
//				case 4:
//					params.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, 
//							GenPowerAmplifier.INPUT_TYPE_INPUT2);
//					break;
//				case 5:
//					params.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, 
//							GenPowerAmplifier.INPUT_TYPE_INPUT3);
//					break;
//				case 6:
//					params.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, 
//							GenPowerAmplifier.INPUT_TYPE_INPUT4);
//					break;
//				default:
//					break;
//				}
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				
//			}
//		});
//
//		return view;
//	}
//	
//	@Override
//	public void onStart() {
//		super.onStart();
//	}
//	
//	@Override
//	public void onResume() {
//		super.onResume();
//		if(isListPlaying && !isPlaying) {
//			togglePlay();
//		}
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//	}
//	
//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.music_hide_control_switcher:
//			if(mHideControl.getVisibility() == View.VISIBLE) {
//				ScaleAnimation gongAnimation = new ScaleAnimation(1, 1, 1, 0, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, 
//						ScaleAnimation.RELATIVE_TO_SELF, 0.0f);
//				gongAnimation.setDuration(500);
//				mHideControl.startAnimation(gongAnimation);
//				mHideControl.setVisibility(View.INVISIBLE);
//			}else {
//				ScaleAnimation showAnimation = new ScaleAnimation(1, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, 
//						ScaleAnimation.RELATIVE_TO_SELF, 0.0f);
//				showAnimation.setDuration(500);
//				mHideControl.startAnimation(showAnimation);
//				mHideControl.setVisibility(View.VISIBLE);
//			}
//			break;
//		case R.id.button_save:		// BaseActivity的TitleBar的右边按钮
//			FragmentManager fm = getActivity().getSupportFragmentManager();
//			Bundle data = new Bundle();
//			if(funDetail != null) {
//				data.putSerializable(MusicListFragment.ARGUMENT_FUNDETAIL, funDetail);
//			}
//			if (productFun !=null) {
//				data.putSerializable(MusicListFragment.ARGUMENT_PRODUCTFUN, productFun);
//			}
//			Fragment fragment = new MusicListFragment();
//			fragment.setArguments(data);
//			fm.beginTransaction()
//					.replace(R.id.music_fragment_layout, fragment)
//					.addToBackStack(null).commit();
//			break;
//		case R.id.music_iv_mute :
//			if(isMute) {
//				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_UNMUTE);			// 操作指令为“取消静音”
//			}else {
//				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE);			// 操作指令为“静音”
//			}
//			toggleMute();
//			sendCmd();
//			break;
//		case R.id.music_tv_sound_add:
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_ADD); 			// 操作指令为“增加音量”
//			sendCmd();
//			break;
//		case R.id.music_tv_sound_sub:
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_SUB); 			// 操作指令为“减小音量”
//			sendCmd();
//			break;
//		case R.id.music_iv_pre:
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_PROVIOUS); 			// 操作指令为“上一曲”
//			sendCmd();
//			break;
//		case R.id.music_iv_next:
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_NEXT); 				// 操作指令为“下一曲”
//			sendCmd();
//			break;
//		case R.id.music_iv_play:
//			// 播放前检查扬声器设置
//			if(!checkInputSourceSetting() && !isPlaying) {
//				break;
//			}
//			if (isPlaying) {
//				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE);	// 操作指令为“暂停”
//				sendCmd(); 		
//			} else {
//				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);		// 操作指令为“播放”
//				sendCmd(); 		
//			}
//			togglePlay();
//			break;
//		case R.id.music_tv_chose_speaker :
//			new SpeakerChoseDialog().show(getActivity().getSupportFragmentManager(), "speaker");
//			break;
//		case R.id.music_iv_repeat_play :
//			toggleRepeatPlay();
//			sendCmd();
//			break;
//		default:
//			break;
//		}
//	}
//
//	/**
//	 * 发送操作命令
//	 */
//	private void sendCmd() {
//		Logger.debug("tangl.play", "发送操作指令...");
//		
//		List<byte[]> cmds = CommonMethodForMusic.productFunToCMD(getActivity(),
//				productFun, funDetail, 0);
//		
//		if (cmds == null || cmds.size() < 1) {
//			return;
//		}
//		
//		Stack<byte[]> ppos = new Stack<byte[]>();
//		for (int i = 0; i < cmds.size(); i++) {
//			ppos.push(cmds.get(i));
//		}
//
//		patternOperationSend(ppos);
//		JxshApp.showLoading(getActivity(), "正在操作，请稍后...");
//	}
//
//	/**
//	 * 递归发送播放指令
//	 * 
//	 * @param _ppos
//	 */
//	private void patternOperationSend(final Stack<byte[]> _ppos) {
//		Logger.warn("tangl", "" + _ppos.size());
//		if(_ppos == null || _ppos.empty()) {
//			JxshApp.closeLoading();
//			return;
//		}
//		
//		byte[] _ppo = _ppos.pop();
//
//		if (_ppo != null) {
//			byte[] cmd = _ppo;
//			
//			final MusicListControlByServerTask cdcbsTask = new MusicListControlByServerTask(
//					getActivity(), cmd, true);
//			
//			if (_ppos == null || _ppos.empty()) {
//				JxshApp.closeLoading();
//				if(cdcbsTask != null) {
//					cdcbsTask.closeSocketLong();
//					Logger.warn("tangl", "关闭连接");
//				}
//			}
//			
//			cdcbsTask.addListener(new ITaskListener<ITask>() {
//
//				public void onStarted(ITask task, Object arg) {
//					Logger.debug("tangl.play", "onstart...");
//				}
//
//				@Override
//				public void onCanceled(ITask task, Object arg) {
//					Logger.debug("tangl.play", "oncancel...");
//				}
//
//				@Override
//				public void onFail(ITask task, Object[] arg) {
//					Logger.debug("tangl.play", "onfail...");
//					JxshApp.closeLoading();
//				}
//
//				@Override
//				public void onSuccess(ITask task, Object[] arg) {
//					if(arg != null && arg.length > 0) {
//						String result = (String) arg[0];
//						Logger.debug("tangl.play.success", result);
//						patternOperationSend(_ppos);
//					}
//				}
//
//				@Override
//				public void onProcess(ITask task, Object[] arg) {
//					
//				}
//			});
//			JxshApp.getDatanAgentHandler().postDelayed(cdcbsTask, 100);
//		}
//	}
//
//	/**
//	 * 运行播放动画
//	 */
//	private void runAnimate() {
//		AnimateUtil.addRotateAnimation(false, 10000, 0);
//		AnimateUtil.setAttr(Animation.RESTART, Animation.INFINITE);
//		AnimateUtil.run(mAlbumCover);
//	}
//
//	/**
//	 * 停止播放动画
//	 */
//	private void stopAnimate() {
//		mAlbumCover.clearAnimation();
//	}
//
//	/**
//	 * 静音开关
//	 */
//	private void toggleMute() {
//		if(isMute) {
//			mMute.setImageResource(R.drawable.sound_on);
//			isMute = false;
//		}else {
//			mMute.setImageResource(R.drawable.sound_off);
//			isMute = true;
//		}
//	}
//	
//	/**
//	 * 播放开关
//	 */
//	public void togglePlay() {
//		if (isPlaying) {
//			isPlaying = false;
//			mPlay.setImageResource(R.drawable.selector_music_button_play);
//			stopAnimate();
//		} else {
//			isPlaying = true;
//			mPlay.setImageResource(R.drawable.selector_music_button_pause);
//			runAnimate();
//		}
//	}
//	
//	/**
//	 * 重复播放开关
//	 */
//	private void toggleRepeatPlay() {
//		if(isRepeatPlay) {
//			isRepeatPlay = false;
//			mRepeatPlay.setImageResource(R.drawable.player_mode_order);
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_ALL);
//		}else {
//			isRepeatPlay = true;
//			mRepeatPlay.setImageResource(R.drawable.player_mode_single);
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_SINGLE);
//		}
//	}
//	
//	/**
//	 * 检查输出源和扬声器设置
//	 */
//	private boolean checkInputSourceSetting() {
//		// 提示设置输出源
//		if(mChoseInput.getSelectedItemPosition() == Spinner.INVALID_POSITION ) {
//			
//		}
//		// 提示设置扬声器
//		if(params.get(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT) == null ||
//				"00000000".equals((String)params.get(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT))) {
//			new MessageBox(getActivity(), "提示", "使用前请设置扬声器", MessageBox.MB_OK).show();
//			return false;
//		}
//		return true;
//	}
//	
//	
//	/**
//	 * 扬声器设置对话框
//	 * @author TangLong
//	 * @company 金鑫智慧
//	 */
//	public static class SpeakerChoseDialog extends DialogFragment {
//		private StringBuilder sBuilder;
//		private boolean[] mSelectedB = new boolean[8];
//		
//		// 发送操作命令
//		private void sendCmd() {
//			if (productFun == null || funDetail == null) {
//				return;
//			}
//
//			List<byte[]> cmds = CommonMethodForMusic.productFunToCMD(getActivity(),
//					productFun, funDetail, 0);
//			if (cmds == null || cmds.size() < 1) {
//				return;
//			}
//
//			byte[] cmd = cmds.get(0);
//			InternetTask cdcbsTask = null;
//
//			if (ProductConstants.POWER_AMPLIFIER_SOUND_LIST.equals(productFun
//					.getFunType())) {
//				cdcbsTask = new MusicListControlByServerTask(getActivity(), cmd,
//						false);
//			} else {
//				cdcbsTask = new CommonDeviceControlByServerTask(getActivity(), cmd,
//						false);
//			}
//
//			cdcbsTask.addListener(new ITaskListener<ITask>() {
//				@Override
//				public void onStarted(ITask task, Object arg) {
//					Logger.debug("tangl", "onstart...");
//					JxshApp.showLoading(getActivity(), CommDefines.getSkinManager()
//							.string(R.string.qing_qiu_chu_li_zhong));
//				}
//
//				@Override
//				public void onCanceled(ITask task, Object arg) {
//					Logger.debug("tangl", "oncancel...");
//					JxshApp.closeLoading();
//				}
//
//				@Override
//				public void onFail(ITask task, Object[] arg) {
//					Logger.debug("tangl", "onfail...");
//					JxshApp.closeLoading();
//				}
//
//				@Override
//				public void onSuccess(ITask task, Object[] arg) {
//					Logger.debug("tangl", "onsuccess...");
//					JxshApp.closeLoading();
//					Logger.debug("tangl", arg.length + "");
//					if (ProductConstants.POWER_AMPLIFIER_SOUND_LIST
//							.equals(productFun.getFunType())) {
//						String result = (String) arg[0];
//						Logger.debug("tangl.success", result);
//						if (result.indexOf("filesumusb") != -1) {
//							String musicCount = result.replace("filesumusb", "")
//									.substring(0, 4);
//							Integer size = Integer.parseInt(musicCount);
//							Logger.debug("tangl", size + "");
//
//							if (size < 1) {
//								return;
//							}
//						}
//					}
//					productFun.setOpen(!productFun.isOpen());
//				}
//
//				@Override
//				public void onProcess(ITask task, Object[] arg) {
//					
//				}
//			});
//			cdcbsTask.start();
//		}
//		
//		public String reverseString(StringBuilder sb) {
//			if(sb != null && sb.length() == 8) {
//				for(int i = 0; i < 8; i++) {
//					if(sb.charAt(i) == '0') {
//						sb.setCharAt(i, '1');
//					}else {
//						sb.setCharAt(i, '0');
//					}
//				}
//			}
//			return sb.toString();
//		}
//		
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//			
//			// 从配置中获取speaker配置信息
//			selectedSpeaker = sp.getString(ProductConstants.SP_MUSIC_SPEAKER, "");
//			if(selectedSpeaker.length() == 8) {
//				sBuilder = new StringBuilder(selectedSpeaker);
//				for(int i = 0; i < selectedSpeaker.length(); i++) {
//					if('1' == selectedSpeaker.charAt(i)) {
//						mSelectedB[i] = true;
//					}else {
//						mSelectedB[i] = false;
//					}
//				}
//			}else {
//				sBuilder = new StringBuilder("00000000");
//			}
//			
//			LayoutInflater inflater = LayoutInflater.from(getActivity());
//			View view = inflater.inflate(R.layout.custom_dialog_speaker_choose, null);
//			
//			TextView mTitle = (TextView)view.findViewById(R.id.speaker_choose_title);
//			Button mPositive = (Button)view.findViewById(R.id.speaker_choose_positiveButton);
//			Button mNegative = (Button)view.findViewById(R.id.speaker_choose_negativeButton);
//			ListView mSpeakers = (ListView)view.findViewById(R.id.speaker_choose_list);
//			
//			mTitle.setText(R.string.music_chose_speaker_title);
//			mPositive.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					resetSpeakers();
//				}
//			});
//			mNegative.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					cancelSet();
//				}
//			});
//			
//			builder.setTitle(R.string.music_chose_speaker)
//				   .setMultiChoiceItems(speakerNames, mSelectedB,
//						   new DialogInterface.OnMultiChoiceClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which, boolean isChecked) {
//								if(isChecked) {
//									sBuilder.setCharAt(which, '1');
//								}else{
//									if(sBuilder.charAt(which) == '1') {
//										sBuilder.setCharAt(which, '0');
//									}
//								}
//							}
//				   })
//				   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							
//						}
//				   })
//				   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							SpeakerChoseDialog.this.getDialog().cancel();
//						}
//				   });
//			
//			return builder.create();
//		}
//		
//		/**
//		 * 取消扬声器设置
//		 */
//		private void cancelSet() {
//			SpeakerChoseDialog.this.getDialog().cancel();
//		}
//		
//		/**
//		 * 重置扬声器设置
//		 */
//		private void resetSpeakers() {
//			// 保存扬声器设置到SharedPreferences中
//			Editor editor = sp.edit();
//			editor.putString(ProductConstants.SP_MUSIC_SPEAKER, sBuilder.toString());
//			Logger.debug("tangl", sBuilder.toString());
//			editor.commit();
//			String unmuteLine = sBuilder.toString();
//			params.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, unmuteLine);
//			if(isPlaying) {
//				// 发送未选中扬声器静音的指令
//				String muteLine = reverseString(sBuilder);
//				Logger.debug("tangl.muteline", muteLine);
//				params.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, muteLine);
//				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE);
//				sendCmd();
//				
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				
//				// 发送选中扬声器取消静音的指令
//				Logger.debug("tangl.unmute", unmuteLine);
//				params.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, unmuteLine);
//				productFun.setFunType(ProductConstants.POWER_AMPLIFIER_UNMUTE);
//				sendCmd();
//			}
//		}
//		
//	}
//	
//}
