package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.Speaker;
import com.jinxin.jxsmarthome.ui.adapter.SpeakerAdapter;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 模式编辑中的音乐设置
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class MusicSettingActivity extends BaseActivity {
	private ListView mSpeakersView;
	private List<String> mInputsData;
	private List<Speaker> mSpeakersData;
	private SpeakerAdapter mSpeakerAdapter;
	private View mInputsPopView;
	private TextView mInputsChoose;
	private PopupWindow inputsPopWindow;
	private CustomerPattern customerPattern;
	private int funId;
	
	private String settedInput = null;
	private String settedSpeaker = null;

	@Override
	public void uiHandlerData(Message msg) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParams();
		initView();
		initData();
	}

	/* init paramters */
	private void initParams() {
		mInputsData = new ArrayList<String>();
		mSpeakersData = new ArrayList<Speaker>();
		Bundle data = getIntent().getExtras();
		if (data != null) {
			customerPattern = (CustomerPattern) data
					.getSerializable("customerPattern");
			funId = data.getInt("funId");
			if (customerPattern == null || funId <= 0) {
				Logger.error(null, "customerPattern passed is null");
				Logger.error(null, "funId is error");
			}
		}
	}

	/* init views */
	private void initView() {
		setView(R.layout.activity_music_setting_1);
		mSpeakersView = (ListView) findViewById(R.id.music_setting_speakers);
		mInputsChoose = (TextView) findViewById(R.id.music_setting_inputs);

		mInputsChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Rect frame = new Rect();
				// getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
				// int y = mInputsChoose.getBottom();
				// int x = getWindowManager().getDefaultDisplay().getWidth() /
				// 4;
				// showInputsWindow(x, y);
				Dialog dialog = showInputsDialog();
				dialog.show();
			}
		});

		setTitle("功放设置");
		setOnBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String inputSet = mInputsChoose.getText() == null ? "usb"
						: mInputsChoose.getText().toString()
								.toLowerCase(Locale.CHINA);
				String sepeakerSet = mSpeakerAdapter.getSpeakerSelection();
				
				Intent intent = new Intent();
				Bundle result = new Bundle();
				result.putString("input", inputSet);
				result.putString("speaker", sepeakerSet);
				intent.putExtras(result);
				setResult(Activity.RESULT_OK, intent);

				onBackPressed();
			}
		});

		if (customerPattern != null) {
			int patternId = customerPattern.getPatternId();
			
			ProductPatternOperationDaoImpl dao = new ProductPatternOperationDaoImpl(
					this);
			List<ProductPatternOperation> operations = dao.find(null,
					"patternId=? and funId=?", new String[] { String.valueOf(patternId), String.valueOf(funId) },
					null, null, null, null);
			
			if(operations != null && operations.size() > 0) {
				ProductPatternOperation ppo = operations.get(0);
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
		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(this);
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
		mSpeakerAdapter = new SpeakerAdapter(this, mSpeakersData);
		mSpeakersView.setAdapter(mSpeakerAdapter);
	}

	/**
	 * Input source choose dialog 
	 */
	private Dialog showInputsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

	/**
	 * Input source choose window, deprecated 
	 */
	@SuppressWarnings("unused")
	private void showInputsWindow(int width, int height) {
		mInputsPopView = LayoutInflater.from(this).inflate(
				R.layout.popwindow_inputs, null);
		ListView mListView = (ListView) mInputsPopView
				.findViewById(R.id.inputs_list);
		mListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.popwindow_inputs_item, R.id.inputs_item_text,
				mInputsData));
		inputsPopWindow = new PopupWindow(this);
		inputsPopWindow.setBackgroundDrawable(new BitmapDrawable());
		inputsPopWindow.setWidth(getWindowManager().getDefaultDisplay()
				.getWidth());
		inputsPopWindow.setHeight(300);
		inputsPopWindow.setOutsideTouchable(true);
		inputsPopWindow.setFocusable(true);
		inputsPopWindow.setContentView(mInputsPopView);
		// inputsPopWindow.showAtLocation(mInputsChoose, Gravity.LEFT
		// | Gravity.TOP, width, height);// 需要指定Gravity，默认情况是center.
		inputsPopWindow.showAtLocation(findViewById(R.id.music_setting_ll),
				Gravity.CENTER, width, height);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mInputsChoose.setText(mInputsData.get(position));
				inputsPopWindow.dismiss();
				inputsPopWindow = null;
			}
		});
	}

}
