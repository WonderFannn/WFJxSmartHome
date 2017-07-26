package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.cd.smarthome.product.service.newcmd.GenPowerAmplifier;
import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.UpdateMusicListTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.MusicActivity;
import com.jinxin.jxsmarthome.cmd.ICmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.Music;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.QuickAdapter;
import com.jinxin.jxsmarthome.ui.widget.FlowLayout;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 音乐列表展示
 * @author  TangLong
 * @company 金鑫智慧
 */
public class MusicStateFragment extends Fragment implements OnClickListener {
	public static final int REFRESH_USB_STATE = 100;	// 刷新 USB 状态
	public static final int REFRESH_SD_STATE = 200;		// 刷新 SD 状态
	public static final int REFRESH_USB_ERROR = 300;		// 获取USB状态失败
	public static final int REFRESH_SD_ERROR = 400;		// 获取SD状态失败
	public static final int REFRESH_ADAPTER = 500;
	public static final int REFRESH = 501;
	
	public static final int GET_USB_MUSIC_LIST_SUCCESS = 1;		// 获取USB歌曲列表成功
	public static final int GET_SD_MUSIC_LIST_SUCCESS = 2;		// 获取SD歌曲列表成功
	public static final int GET_USB_MUSIC_LIST_FAIL = 3;		// 获取USB歌曲列表失败
	public static final int GET_SD_MUSIC_LIST_FAIL = 4;			// 获取SD歌曲列表失败
	
	private ProductFun productFun; 				// 产品对象
	private FunDetail funDetail; 				// 设备对象
	private Map<String, Object> params; 		// 发送指令的参数
	private List<Music> songs = new ArrayList<Music>();
	
	private RelativeLayout mUsbItem,mSdItem;
	private LinearLayout mUsbState, mSdState;
	private TextView tvUsbState, tvSdState;
	private ImageView ivUsb, ivSd;
	private ListView mListView;
	private FlowLayout usbFlowlayout;
	private FlowLayout sdFlowlayout;
	
	private String playNum = "-1";//当前播放歌曲id
	
	private boolean isUsbPlaying = false, isSdPlaying = false;
	private boolean isUsbExpand = false, isSdExpand = false;//是否展开
	
	private UpdateMusicListTask updateTask = null;
	private Map<Integer,String> usbSelectSpeakers = new HashMap<Integer, String>();
	private Map<Integer,String> sdSelectSpeakers = new HashMap<Integer, String>();
	
	private MyAdapter adapter = null;
	private String bindStr = "";
	private String muteStr = "";
	
	private Handler handler = new Handler(new Handler.Callback() {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case REFRESH_USB_STATE:
				if (usbSelectSpeakers.size() > 0) {
					usbFlowlayout.removeAllViews();
					Iterator iter = usbSelectSpeakers.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						TextView speakerView = (TextView) getActivity().
								getLayoutInflater().inflate(R.
										layout.item_input_textview, usbFlowlayout, false);
						speakerView.setText((String)val);
						if (checkIsMute((Integer)key)) {
							speakerView.setTextColor(Color.parseColor("#cccccc"));
						}
						usbFlowlayout.addView(speakerView);
					}
				}else{
					TextView speakerView = (TextView) getActivity().
							getLayoutInflater().inflate(R.
									layout.item_input_textview, sdFlowlayout, false);
					speakerView.setText("未绑定扬声器");
					usbFlowlayout.addView(speakerView);
				}
				if (isUsbPlaying) {
					tvUsbState.setText("正在播放");
					updateDataFromCloud("usb");
				}else{
					tvUsbState.setText("未播放歌曲");
				}
				break;
			case REFRESH_SD_STATE:
				if (sdSelectSpeakers.size() > 0) {
					sdFlowlayout.removeAllViews();
					Iterator iter2 = sdSelectSpeakers.entrySet().iterator();
					while (iter2.hasNext()) {
						Map.Entry entry = (Map.Entry) iter2.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						TextView speakerView = (TextView) getActivity().
								getLayoutInflater().inflate(R.
										layout.item_input_textview, usbFlowlayout, false);
						speakerView.setText((String)val);
						if (checkIsMute((Integer)key)) {
							speakerView.setTextColor(Color.parseColor("#cccccc"));
						}
						sdFlowlayout.addView(speakerView);
					}

				}else{
					TextView speakerView = (TextView) getActivity().
							getLayoutInflater().inflate(R.
									layout.item_input_textview, sdFlowlayout, false);
					speakerView.setText("未绑定扬声器");
					sdFlowlayout.addView(speakerView);
				}
				if (isSdPlaying) {
					tvSdState.setText("正在播放");
					updateDataFromCloud("sd");
				}else{
					tvSdState.setText("未播放歌曲");
				}
				break;
			case REFRESH_USB_ERROR:
				tvUsbState.setText("状态获取失败");
				break;
			case REFRESH_SD_ERROR:
				tvSdState.setText("状态获取失败");
				break;
			case GET_SD_MUSIC_LIST_SUCCESS:
				if (songs != null && songs.size() > 0) {
					for (Music song : songs) {
						if (song.getNo() == Integer.valueOf(playNum)) {
							tvSdState.setText("正在播放--"+songs.get(Integer.valueOf(playNum)).getTitle());
						}
					}
				}
				break;
			case GET_USB_MUSIC_LIST_SUCCESS:
				if (songs != null && songs.size() > 0) {
					for (Music song : songs) {
						if (song.getNo() == Integer.valueOf(playNum)) {
							tvUsbState.setText("正在播放--"+songs.get(Integer.valueOf(playNum)).getTitle());
						}
					}
				}
				break;
			case GET_USB_MUSIC_LIST_FAIL:
				tvUsbState.setText("正在播放--"+"未知歌曲");
				break;
			case GET_SD_MUSIC_LIST_FAIL:
				tvSdState.setText("正在播放--"+"未知歌曲");
				break;
			case REFRESH_ADAPTER:
				if (!TextUtils.isEmpty(bindStr)) {
					Map<Integer, String> bindSpeakerList = getBindSpeakerList(bindStr);
					
					if (adapter != null && bindSpeakerList.size() > 0) {
						adapter.setSpeakerList(bindSpeakerList);
						adapter.notifyDataSetChanged();
					}
					
					if (bindSpeakerList.size() > 0) {//显示usb、sd绑定扬声器
						usbFlowlayout.removeAllViews();
						sdFlowlayout.removeAllViews();
						
						for (int i = 0; i < bindSpeakerList.size(); i++) {
							if ("USB".equalsIgnoreCase(bindSpeakerList.get(i))) {
								TextView usbView = (TextView) getActivity().
										getLayoutInflater().inflate(R.
												layout.item_input_textview, usbFlowlayout, false);
								usbView.setText(getSpeakerNames().get(i));
								if (checkIsMute(i)) {
									usbView.setTextColor(Color.parseColor("#cccccc"));
								}
								usbFlowlayout.addView(usbView);
							}else if("SD".equalsIgnoreCase(bindSpeakerList.get(i))){
								TextView sdView1 = (TextView) getActivity().
										getLayoutInflater().inflate(R.
												layout.item_input_textview, sdFlowlayout, false);
								sdView1.setText(getSpeakerNames().get(i));
								if (checkIsMute(i)) {
									sdView1.setTextColor(Color.parseColor("#cccccc"));
								}
								sdFlowlayout.addView(sdView1);
							}
						}
						if (usbFlowlayout.getChildCount() < 1) {
							TextView speakerView = (TextView) getActivity().
									getLayoutInflater().inflate(R.
											layout.item_input_textview, sdFlowlayout, false);
							speakerView.setText("未获取到绑定状态");
							usbFlowlayout.addView(speakerView);
						}
						if (sdFlowlayout.getChildCount() < 1) {
							TextView sdView = (TextView) getActivity().
									getLayoutInflater().inflate(R.
											layout.item_input_textview, sdFlowlayout, false);
							sdView.setText("未获取到绑定状态");
							sdFlowlayout.addView(sdView);
						}
					}
				}
				break;
				
			case REFRESH:
				adapter.notifyDataSetChanged();
				break;
			}
			return true;
		}

	});
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		params = new HashMap<String, Object>();
		productFun = (ProductFun) getArguments().get("productFun");
		funDetail = (FunDetail) getArguments().get("funDetail");
		
		sendGetBindCmd();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music_state, container, false);
		
		((MusicActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mUsbItem = (RelativeLayout) view.findViewById(R.id.rl_usb_item);
		mSdItem = (RelativeLayout) view.findViewById(R.id.rl_sd_item);
		mUsbState = (LinearLayout) view.findViewById(R.id.ll_usb_state_layout);
		mSdState = (LinearLayout) view.findViewById(R.id.ll_sd_state_layout);
		tvUsbState = (TextView) view.findViewById(R.id.tv_usb_state);
		tvSdState = (TextView) view.findViewById(R.id.tv_sd_state);
		ivUsb = (ImageView) view.findViewById(R.id.iv_usb_next);
		ivSd = (ImageView) view.findViewById(R.id.iv_sd_next);
		usbFlowlayout = (FlowLayout) view.findViewById(R.id.tv_usb_speakers);
		sdFlowlayout = (FlowLayout) view.findViewById(R.id.tv_sd_speakers);
		mListView = (ListView) view.findViewById(R.id.lv_input);
		
		mUsbItem.setOnClickListener(this);
		mSdItem.setOnClickListener(this);
		
		adapter = new MyAdapter(R.layout.item_input_state, getInputNames());
		mListView.setAdapter(adapter);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.rl_usb_item://查询 USB 状态
			if (!isUsbExpand) {
				sendPlayStatusCmd("usb");
				isUsbExpand = true;
				mUsbState.setVisibility(View.VISIBLE);
				tvUsbState.setText("正在获取...");
				ivUsb.setImageResource(R.drawable.ico_arrow_open);
			}else{
				isUsbExpand = false;
				mUsbState.setVisibility(View.GONE);
				ivUsb.setImageResource(R.drawable.ico_arrow_close);
			}
			break;
		case R.id.rl_sd_item://查询 SD 状态
			if (!isSdExpand) {
				sendPlayStatusCmd("sd");
				isSdExpand = true;
				mSdState.setVisibility(View.VISIBLE);
				tvSdState.setText("正在获取...");
				ivSd.setImageResource(R.drawable.ico_arrow_open);
			}else{
				isSdExpand = false;
				mSdState.setVisibility(View.GONE);
				ivSd.setImageResource(R.drawable.ico_arrow_close);
			}
			break;
		}
	}
	
	class MyAdapter extends QuickAdapter<String>{
		private Map<Integer, String> bindSpeakers;
		private ArrayList<String> speakers = getSpeakerNames();
//		pirvate String[] intputArr = getActivity().getResources().getStringArray(R.array.input_items);
		private Map<Integer, Boolean> selectMap =  new HashMap<Integer, Boolean>();
		
		public MyAdapter(int res, List<String> data){
			this(getActivity(), res, data);
			for (int i = 0; i < data.size(); i++) {
				selectMap.put(i, false);
			}
		}
		
		public MyAdapter(Context context, int res, List<String> data) {
			super(context, res, data);
		}
		
		public void setSpeakerList(Map<Integer, String> bindSpeakerList){
			this.bindSpeakers = bindSpeakerList;
		}

		@Override
		public View getItemView(int position, View convertView, ViewHolder holder) {
			final int pos = position;
			LinearLayout mLayout = holder.getView(R.id.rl_item);
			LinearLayout itemLayout = holder.getView(R.id.ll_speaker_item);
			TextView tvInput = holder.getView(R.id.tv_input_name);
			FlowLayout speakersGroup = holder.getView(R.id.ll_group_view);
			ImageView arrow = holder.getView(R.id.iv_usb_next);
			
			speakersGroup.removeAllViews();
			@SuppressWarnings("static-access")
			LayoutInflater mInflater = getActivity().getLayoutInflater().from(getActivity());
			tvInput.setText(data.get(position));
			if (bindSpeakers != null && bindSpeakers.size() > 0) {
				Iterator iter = bindSpeakers.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					if (data.get(position).equalsIgnoreCase((String)val)) {
						TextView childView = (TextView) mInflater.
								inflate(R.layout.item_input_textview, speakersGroup,false);
						childView.setText(speakers.get((Integer)key));
						if (checkIsMute((Integer)key)) {
							childView.setTextColor(Color.parseColor("#cccccc"));
						}
						speakersGroup.addView(childView);
					}
				}
//				for (int i = 0; i < bindSpeakers.size(); i++) {
//					if (data.get(position).equalsIgnoreCase(bindSpeakers.get(i))) {
//						TextView childView = (TextView) mInflater.
//								inflate(R.layout.item_input_textview, speakersGroup,false);
//						childView.setText(speakers.get(i));
//						if (checkIsMute(i)) {
//							childView.setTextColor(Color.parseColor("#cccccc"));
//						}
//						speakersGroup.addView(childView);
//					}
//				}
			}
			
			if (speakersGroup.getChildCount() < 1) {
				TextView childView = (TextView) mInflater.
						inflate(R.layout.item_input_textview, speakersGroup,false);
				childView.setText("未绑定扬声器");
				speakersGroup.addView(childView);
			}
			
			if (selectMap.get(position)) {
				itemLayout.setVisibility(View.VISIBLE);
				arrow.setImageResource(R.drawable.ico_arrow_open);
			}else{
				itemLayout.setVisibility(View.GONE);
				arrow.setImageResource(R.drawable.ico_arrow_close);
			}
			
			mLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Logger.debug(null, "onclick");
					if (selectMap.get(pos)) {
						selectMap.remove(pos);
						selectMap.put(pos, false);
					}else{
						selectMap.remove(pos);
						selectMap.put(pos, true);
					}
					handler.sendEmptyMessage(REFRESH);
				}
			});
			
			return convertView;
		}
		
	}
	
	private void sendGetBindCmd(){
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_GET_BIND);
		
		TaskListener<ITask> listener = new TaskListener<ITask>(){

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (result.startsWith("T")) {
						bindStr = result.substring(1, 9);
						
						sendGetetMuteStatus();
					}else{
						Logger.debug(null, "get bind fail");
					}
				}
			}
			
		};
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun, funDetail, null);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(),
					productFun, funDetail, null);
			if (cmd == null) {
				return;
			}
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(getActivity(), cmd);
			cmdSender.addListener(listener);
			cmdSender.send();
		}
	}
	
	/**
	 * 获取扬声器是否静音
	 */
	private void sendGetetMuteStatus(){
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_GET_MUTE_STATUS);
		
		TaskListener<ITask> listener = new TaskListener<ITask>(){

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (result.startsWith("T")) {
						muteStr = result.substring(1, 9);
						handler.sendEmptyMessage(REFRESH_ADAPTER);
					}else{
						Logger.debug(null, "get status fail");
					}
				}
			}
			
		};
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun, funDetail, null);
		} else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(),
					productFun, funDetail, null);
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
	private void sendPlayStatusCmd(String input){
		if (TextUtils.isEmpty(input)) {
			return;
		}
		final String index = input.equalsIgnoreCase("USB") ? "1" : "2";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_INDEX,index);
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
					if (result.startsWith("T")) {
						if (result.length() > 2) {
							decodePlayState(index,String.valueOf(result.charAt(2)));
						}
						
						if (result.length() > 14) {
							decodePlaySpeakers(index, result.substring(result.length()-14, result.length()-6));
						}
						
						playNum = result.substring(3, 10);
						playNum = playNum.replaceFirst("^0+", "");
						if (TextUtils.isEmpty(playNum)) {
							playNum = "0";
						}
//						SharedDB.saveIntDB(MusicSetting.SP_NAME,
//								MusicSetting.CURRENT_PLAYING_SONG, Integer.valueOf(playIndex));
						
						if (index.equals("1")) {
							handler.sendEmptyMessage(REFRESH_USB_STATE);
						}else{
							handler.sendEmptyMessage(REFRESH_SD_STATE);
						}
						
					}else{
//						SharedDB.saveBooleanDB(MusicSetting.SP_NAME, 
//								MusicSetting.AMPLIFIER_PALY_STATE, false);
						if (index.equals("1")) {
							handler.sendEmptyMessage(REFRESH_USB_ERROR);
						}else{
							handler.sendEmptyMessage(REFRESH_SD_ERROR);
						}
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
	private void decodePlayState(String usbOrSd, String state){
		if (usbOrSd.equals("1")) {//USB状态
			isUsbPlaying = state.equals("2") ? true : false;
		}else{//SD状态
			isSdPlaying = state.equals("2") ? true : false;
		}
	}
	
	/**
	 * 解析当前输入源占用扬声器
	 * @param str
	 */
	private void decodePlaySpeakers(String usbOrSd, String selectedSpeaker) {
		usbSelectSpeakers.clear();
		sdSelectSpeakers.clear();
		if (TextUtils.isEmpty(selectedSpeaker))
			return;

		List<String> tempList = getSpeakerNames();
		if (tempList.size() == selectedSpeaker.length()) {
			for (int i = 0; i < selectedSpeaker.length(); i++) {
				if ('1' == selectedSpeaker.charAt(i)) {
					if (usbOrSd.equals("1")) {// USB扬声器状态
						usbSelectSpeakers.put(i,tempList.get(i));
					} else {// SD扬声器状态
						sdSelectSpeakers.put(i,tempList.get(i));
					}
				}
			}

		} else {
			Logger.debug(null, "decode sepaker error");
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
	
	private List<String> getInputNames(){
		String[] arr = {"AUX","INPUT1","INPUT2","INPUT3","INPUT4"};
		return Arrays.asList(arr);
	}
	
	/**
	 * 转换绑定结果字符串到对应的 INPUT
	 * 0: 与喇叭1相绑定的输入(其中：1: usb, 2: sd, 4: line_in,
	 *        5: input1, 6: input2, 7: input3, 8: input4, 0: 没有绑定输入)
	 * @param bindStr
	 * @return
	 */
	private Map<Integer, String> getBindSpeakerList(String bindStr){
		Map<Integer, String> tempList = new HashMap<Integer, String>();
		if (!TextUtils.isEmpty(bindStr)) {
			for (int i = 0; i < bindStr.length(); i++) {
				switch (bindStr.charAt(i)) {
				case '0':
					break;
				case '1':
					tempList.put(i, "USB");
					break;
				case '2':
					tempList.put(i, "SD");
					break;
				case '4':
					tempList.put(i,"AUX");
					break;
				case '5':
					tempList.put(i,"INPUT1");
					break;
				case '6':
					tempList.put(i,"INPUT2");
					break;
				case '7':
					tempList.put(i,"INPUT3");
					break;
				case '8':
					tempList.put(i,"INPUT4");
					break;
				}
			}
		}
		
		return tempList;
	}
	
	private List<String> getMuteSpeakerList(String muteStr){
		List<String> tempList = new ArrayList<String>();
		if (!TextUtils.isEmpty(muteStr)) {
			
		}
		
		return tempList;
	}
	
	private boolean checkIsMute(int key) {

		if (TextUtils.isEmpty(muteStr)) {
			return false;
		}
		
		if (muteStr.charAt(key) == '0') {
			return true;
		}
		
		return false;
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
		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(getActivity());
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
	 * 加载数据，从平台加载
	 */
	private void updateDataFromCloud(String input) {
		JxshApp.showLoading(getActivity(), "正在获取当前播放歌曲");
		doUpdateDataFromCloud(input);
		
	}
	
	private void doUpdateDataFromCloud(final String source) {
		updateTask = new UpdateMusicListTask(getActivity(), source,
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
				if (source.equalsIgnoreCase("usb")) {
					handler.sendEmptyMessage(GET_USB_MUSIC_LIST_FAIL);
				}else{
					handler.sendEmptyMessage(GET_SD_MUSIC_LIST_FAIL);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if(arg != null && arg.length > 0) {
					
					List<Music> tempList = (List<Music>)arg[0];
					Collections.sort(tempList, new Comparator<Music>(){

						@Override
						public int compare(Music lhs, Music rhs) {
							int i = lhs.getNo().compareTo(rhs.getNo());
							return i;
						}
						
					});
					try {
						songs.addAll(tempList);
					} catch (ClassCastException e) {
						Logger.error(null, "the format of music name list get from server is wrong");
					}
					
					// 更新界面
					if (source.equalsIgnoreCase("usb")) {
						handler.sendEmptyMessage(GET_USB_MUSIC_LIST_SUCCESS);
					}else{
						handler.sendEmptyMessage(GET_USB_MUSIC_LIST_SUCCESS);
					}
				}
				JxshApp.closeLoading();
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		
		updateTask.start();
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
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (updateTask != null) {
			updateTask.cancel();
			updateTask = null;
		}
	}
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