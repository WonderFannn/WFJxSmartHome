package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.SpeakerChooseDialogAdapter;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;
import com.jinxin.jxsmarthome.activity.MusicActivity;

public class SpeakerChooseFragment extends Fragment {
	private ListView mSpeakers;
	private List<String> speakerNames;
	private Button mPositive;
	
	public ProductFun productFun; 				// 产品对象
	public FunDetail funDetail; 				// 设备对象
	
	private static SharedPreferences sp;
	private StringBuilder sBuilder;					// 保存扬声器路数设置
	private Map<String, Object> params; 			// 发送指令的参数
	private SpeakerChooseDialogAdapter adapter;		// 扬声器名称显示的适配器
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sp = getActivity().getSharedPreferences(CommUtil.getSPName4Music(), Context.MODE_PRIVATE);
		params = new HashMap<String, Object>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.custom_dialog_speaker_choose, null);
		initView(view);
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			getActivity().onBackPressed();
			break;
		}
		return true;
	}
	
	private void initView(View view) {
		((MusicActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mSpeakers = (ListView)view.findViewById(R.id.speaker_choose_list);
		mPositive = (Button)view.findViewById(R.id.speaker_choose_positiveButton);
		
		speakerNames = getSpeakerNames();
		adapter = new SpeakerChooseDialogAdapter(getActivity(), speakerNames);
		mSpeakers.setAdapter(adapter);
		
		mPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SparseBooleanArray checkBoxState = adapter.getCheckboxItems();
				for(int i = 0; i < checkBoxState.size(); i++) {
					if(checkBoxState.get(i)) {
						sBuilder.setCharAt(i, '1');
					}else {
						sBuilder.setCharAt(i, '0');
					}
				}
				setSpeakers();
			}
		});
	}
	
	/**
	 * 设置扬声器
	 */
	private void setSpeakers() {
		funDetail = AppUtil.getFunDetailByFunType(getActivity(), ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		List<ProductFun> productFuns = AppUtil.getProductFunByFunType(getActivity(), ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		
		if(productFuns != null && productFuns.size() > 0) {
			productFun = productFuns.get(0);
		}
		if(funDetail == null || productFun == null) {
			Logger.error(null, "funDetail and productFun cannot be null");
			return;
		}
		
		saveSpeakerSettingToDB(sBuilder.toString());
		
		// 发送未选中扬声器静音的指令
		String muteLine = getOneString(8);
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, muteLine);
		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE);
		sendCmd();
	}
	
	private void saveSpeakerSettingToDB(String speakerSetting) {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getActivity());
		String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT, "");
		List<CloudSetting> csList = (csDaoImpl.find(null,
				"customer_id=? and funType=? and items=?", new String[] { _account,
						ProductConstants.FUN_TYPE_POWER_AMPLIFIER, StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT }, null,
				null, null, null));
		if(csList == null || csList.size() < 1) {
			CloudSetting cs = new CloudSetting(null, _account, ProductConstants.FUN_TYPE_POWER_AMPLIFIER, 
					StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speakerSetting, null, null);
			csDaoImpl.insert(cs, true);
		}else {
			for(CloudSetting cs : csList) {
				cs.setParams(speakerSetting);
				csDaoImpl.update(cs);
			}
		}
		
		// save to sp
		Editor editor = sp.edit();
		editor.putString(ProductConstants.SP_MUSIC_SPEAKER, speakerSetting);
		editor.commit();
	}
	
	/**
	 *  获取扬声器名字
	 */
	private List<String> getSpeakerNames() {
		List<String> speakerList = new ArrayList<String>();
		String speakerNamesStr = getSpeakerNamesFromDb();
		if(!CommUtil.isEmpty(speakerNamesStr)) {
				try {
					JSONObject jsonObj = new JSONObject(speakerNamesStr);
					JSONArray jsonArr = jsonObj.getJSONArray("param");
					
					for(int i = 0; i < jsonArr.length(); i++) {
						speakerList.add((String) jsonArr.get(i));
					}
					
					return speakerList;
				} catch (JSONException e) {
					return Collections.emptyList();
				}
		}else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * 从数据库中获取扬声器的配置 
	 */
	private String getSpeakerNamesFromDb() {
		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(getActivity());
		List<FunDetailConfig> fdc4Amplifiers = daoImpl.find(null, "funType=?" , 
				new String[] {ProductConstants.FUN_TYPE_POWER_AMPLIFIER}, null, null, null, null);
		if(fdc4Amplifiers != null && fdc4Amplifiers.size() > 0) {
			FunDetailConfig fdc4Amplifier = fdc4Amplifiers.get(0);
			if(fdc4Amplifier != null && !CommUtil.isEmpty(fdc4Amplifier.getParams())) {
				return fdc4Amplifier.getParams();
			}
		}
		return "";
	}
	
	private void sendCmd() {
		String input = sp.getString(ProductConstants.SP_MUSIC_INPUT, StaticConstant.INPUT_TYPE_USB);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, input);
		
		// offline
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			JxshApp.showLoading(getActivity(), CommDefines.getSkinManager()
					.string(R.string.qing_qiu_chu_li_zhong));
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun, funDetail, params);
		
		// online
		}else {
			byte[] cmd = CommonMethod.productPatternOperationToCMD(getActivity(), productFun, funDetail, params);
			OnlineCmdSenderLong onlineCmdSender = new OnlineCmdSenderLong(getActivity(), cmd);
			onlineCmdSender.send();
		}
	}
	
	//////////////////////////////////////////////////////
	//common methods
	//////////////////////////////////////////////////////
	public String reverseString(StringBuilder sb) {
		if(sb != null ) {
			for(int i = 0; i < sb.length(); i++) {
				if(sb.charAt(i) == '0') {
					sb.setCharAt(i, '1');
				}else {
					sb.setCharAt(i, '0');
				}
			}
		}
		return sb.toString();
	}
	
	private String getOneString(int count) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < count; i++) {
			sb.append("1");
		}
		
		return sb.toString();
	}
	
}
