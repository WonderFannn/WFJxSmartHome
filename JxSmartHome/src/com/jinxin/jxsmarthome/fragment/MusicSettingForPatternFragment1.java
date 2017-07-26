package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.TimerTaskOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.BaseActivity;
import com.jinxin.jxsmarthome.activity.BaseFragmentActivity;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.Speaker;
import com.jinxin.jxsmarthome.entity.TimerTaskOperation;
import com.jinxin.jxsmarthome.ui.adapter.SpeakerAdapter;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;

@SuppressLint("NewApi")
public class MusicSettingForPatternFragment1 extends Fragment {
	private ListView mSpeakersView;
	private List<String> mInputsData;
	private List<Speaker> mSpeakersData;
	private SpeakerAdapter mSpeakerAdapter;
	private TextView mInputsChoose;
	private CustomerTimer customerTimer;
	private ProductFunVO productFunVO;
	private int funId;
	
	private String settedInput = null;
	private String settedSpeaker = null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.save_mode_btn);
		inflater.inflate(R.menu.menu_for_mode, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("功放设置");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_music_setting_1, null);
		initParams();
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btn_save:
			((BaseActionBarActivity)getActivity()).onBackPressed();
			break;
		default:
			break;
		}
		return true;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		String inputSet = mInputsChoose.getText() == null ? "usb"
				: mInputsChoose.getText().toString().toLowerCase(Locale.CHINA);
		String sepeakerSet = mSpeakerAdapter.getSpeakerSelection();
		
		// send broadcast for music setting
		Bundle result = new Bundle();
		result.putString("input", inputSet);
		result.putString("speaker", sepeakerSet);
		JSONObject jb = new JSONObject();
		try {
			jb.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, inputSet);
			jb.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, sepeakerSet);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		productFunVO.getProductPatternOperation().setParaDesc(jb.toString());
		result.putSerializable("productFunVO", productFunVO);
//		Intent intent = new Intent();
//		intent.putExtras(result);
//		intent.setAction(BroadcastManager.ACTION_MODE_MUSIC_SET_MESSAGE);
//		getActivity().sendBroadcast(intent);
//		
//		Activity activity = getActivity();
//		
//		if(activity instanceof BaseActivity) {
//			((BaseActivity)activity).hideSaveButton(false);
//		}else if(activity instanceof BaseFragmentActivity) {
//			((BaseFragmentActivity)activity).hideSaveButton(false);
//		}
//		getActivity().getActionBar().show();
		
	}
	
	/* init paramters */
	private void initParams() {
		mInputsData = new ArrayList<String>();
		mSpeakersData = new ArrayList<Speaker>();
		Bundle data = getArguments();
		if (data != null) {
			customerTimer = (CustomerTimer) data.getSerializable("customerTimer");
			
			funId = data.getInt("funId");
			productFunVO = (ProductFunVO) data.getSerializable("productFunVO");
			if (customerTimer == null || funId <= 0) {
				Logger.error(null, "customerPattern passed is null");
				Logger.error(null, "funId is error");
			}
		}
	}

	/* init views */
	private void initView(View view) {
		view.setClickable(true);// 防止点击穿透，底层的fragment响应上层点击触摸事件  
		mSpeakersView = (ListView) view.findViewById(R.id.music_setting_speakers);
		mInputsChoose = (TextView) view.findViewById(R.id.music_setting_inputs);
 
		mInputsChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog dialog = showInputsDialog();
				dialog.show();
			}
		});
		
		Activity activity = getActivity();
		
		if(activity instanceof BaseActivity) {
			((BaseActivity)activity).hideSaveButton(true);
		}else if(activity instanceof BaseFragmentActivity) {
			((BaseFragmentActivity)activity).hideSaveButton(true);
		}

		if (customerTimer != null) {
			int taskId = customerTimer.getTaskId();
			
			TimerTaskOperationDaoImpl dao = new TimerTaskOperationDaoImpl(
					getActivity());
			List<TimerTaskOperation> operations = dao.find(null,
					"taskId=? and funId=?", new String[] { String.valueOf(taskId), String.valueOf(funId) },
					null, null, null, null);
			
			if(operations != null && operations.size() > 0) {
				TimerTaskOperation ppo = operations.get(0);
				Logger.debug(null, ppo.toString());
				String desc = ppo.getParaDesc();
				if(!CommUtil.isEmpty(desc)) {
					try {
						JSONObject jo = new JSONObject(desc);
						settedSpeaker = jo.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
						settedInput = jo.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
						Logger.debug(null, settedSpeaker);
						Logger.debug(null, settedInput);
					} catch (JSONException e) {
						e.printStackTrace();
						Logger.error("tangl", "music parameters is error or cannot parse");
					}
				}
			}
			
			if(!CommUtil.isEmpty(settedInput)) {
				mInputsChoose.setText(settedInput.toUpperCase(Locale.CHINA));
			}
		}
	}

	/* init data */
	private void initData() {
		// init inputs, fixed
		mInputsData.add("USB");
		mInputsData.add("SD");
		mInputsData.add("AUX");
		mInputsData.add("INPUT1");
		mInputsData.add("INPUT2");
		mInputsData.add("INPUT3");
		mInputsData.add("INPUT4");

		// init speakers
		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(getActivity());
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
					for (int i = 0; i < jsonArr.length(); i++) {
						Speaker speaker = null;
						if(settedSpeaker != null && settedSpeaker.length() == jsonArr.length()) {
							boolean check = settedSpeaker.charAt(i) == '1' ? true : false;
							speaker = new Speaker((String) jsonArr.get(i), check);
						} else {
							speaker = new Speaker((String) jsonArr.get(i));
						}
						mSpeakersData.add(speaker);
					}
				} catch (JSONException e) {
					Logger.error("MusicSettingActivity",
							"music speakers init error");
				}
			}
		}
		mSpeakerAdapter = new SpeakerAdapter(getActivity(), mSpeakersData);
		mSpeakersView.setAdapter(mSpeakerAdapter);
	}

	/**
	 * Input source choose dialog 
	 */
	private Dialog showInputsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.music_setting_inputs);
		builder.setItems(R.array.input_items,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String[] items = getResources().getStringArray(
								R.array.input_items);
						mInputsChoose.setText(items[which]);
						dialog.dismiss();
					}
				});
		return builder.create();
	}

}
