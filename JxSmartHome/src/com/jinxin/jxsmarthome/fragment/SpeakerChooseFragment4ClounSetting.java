package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.Speaker;
import com.jinxin.jxsmarthome.ui.adapter.SpeakerAdapter;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 设置功放扬声器
 * @author  TangLong
 * @company 金鑫智慧
 */
public class SpeakerChooseFragment4ClounSetting extends Fragment {
	public static final String ARGUMENT_PRODUCTFUN = "ProductFun";
	public static final String ARGUMENT_FUNDETAIL = "FunDetail";
	private ListView mSpeakers;
	private List<Speaker> mSpeakersData;
	private SpeakerAdapter mAdapter;
	private CloudSetting speakerSetting;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		initData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.custom_dialog_speaker_choose_1, container, false);
		
		mSpeakers = (ListView)view.findViewById(R.id.speaker_choose_list);
		
		mAdapter = new SpeakerAdapter(getActivity(), mSpeakersData);
		mSpeakers.setAdapter(mAdapter);
		
		return view;
	}
	
	private void initData() {
		mSpeakersData = new ArrayList<Speaker>();
		
		speakerSetting = getSpeakerSettingInDb();
		
		initSpeakerNameSetting();
		
		String speakerParam = getSpeakerSettingParam();
		
		if(speakerParam.length() == mSpeakersData.size()) {
			for(int i = 0; i < mSpeakersData.size(); i++) {
				mSpeakersData.get(i).setChecked(speakerParam.charAt(i) == '0' ? false : true);
			}
		}
	}
	
	private String getSpeakerSettingParam() {
		if(speakerSetting != null) {
			String param = speakerSetting.getParams();
			return param == null ? "" : param;
		}
		return "";
	}
	
	
	private boolean isMainAccount() {
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
	/**
	 * 进入页面时获取云设置
	 */
	private CloudSetting getSpeakerSettingInDb() {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getActivity());
		String _account = CommUtil.getMainAccount();
		if(!isApplyMainSettings()) {
			_account = CommUtil.getCurrentLoginAccount();
		}
		List<CloudSetting> css = csDaoImpl.find(null, 
				"customer_id=? and category='004' and items='selectRoad'", new String[]{_account}, null, null, null, null);
		if(css.size() > 0) return css.get(0);
		else return null;
	}
	
	/**
	 * 初始化扬声器名字的设置
	 */
	private void initSpeakerNameSetting() {
		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(getActivity());
		List<FunDetailConfig> fdc4Amplifiers = daoImpl.find(null, "funType=?" , 
				new String[] {ProductConstants.FUN_TYPE_POWER_AMPLIFIER}, null, null, null, null);
		
		if(fdc4Amplifiers != null && fdc4Amplifiers.size() > 0) {
			FunDetailConfig fdc4Amplifier = fdc4Amplifiers.get(0);
			if(fdc4Amplifier != null && !CommUtil.isEmpty(fdc4Amplifier.getParams())) {
				try {
					
					JSONObject jsonObj = new JSONObject(fdc4Amplifier.getParams());
					
					JSONArray jsonArr = jsonObj.getJSONArray("param");
					
					for(int i = 0; i < jsonArr.length(); i++) {
						Speaker speaker = new Speaker();
						speaker.setName((String) jsonArr.get(i));
						mSpeakersData.add(speaker);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public CloudSetting getCloudSetting() {
		if(speakerSetting == null) {
			speakerSetting = new CloudSetting();
			speakerSetting.setCategory("004");
			speakerSetting.setCustomerId(CommUtil.getCurrentLoginAccount());
			speakerSetting.setItems("selectRoad");
		}
		speakerSetting.setParams(mAdapter.getSpeakerSelection());
		return speakerSetting;
	}
	
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
	
}
