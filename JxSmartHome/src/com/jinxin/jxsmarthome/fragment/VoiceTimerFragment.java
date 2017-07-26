package com.jinxin.jxsmarthome.fragment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jinxin.db.impl.CustomerVoiceConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.VoiceConfigActivity;
import com.jinxin.jxsmarthome.entity.CustomerVoiceConfig;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.service.TimerManager;
import com.jinxin.jxsmarthome.ui.adapter.AddTimeRepeatAdapter;
import com.jinxin.jxsmarthome.ui.adapter.AddTimerSelectDaysAdapter;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.util.DateUtil;

/**
 * 语音定时时间设置界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class VoiceTimerFragment extends Fragment implements OnClickListener{

	private EditText taskName;
	private LinearLayout repeatLayout, timeLayout;
	private TextView repeatPreiod;
	private TextView repeatTime;
	
	private ProductVoiceConfig voiceConfig;//语音消息
	private CustomerVoiceConfigDaoImpl cvcDaoImpl;
	private CustomerVoiceConfig customerVoiceConfig;//自定义定时语音消息
	
	private String cornExpression;//任务表达式 JSon
	private String cornDays = "1,2,3,4,5,6,7";//默认 每天
	private String cornTime = null;//任务重复时间
	private int repeatType = 0;//任务重复类型
	private String period = "每天";//周期
	
	private int currentHour = 0;
	private int currentMinute = 0;
	private int type = -1;//定时情景类别
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		getActivity().getMenuInflater().inflate(R.menu.menu_add, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("设置时间");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.voice_time_setting_layout, container, false);
		initData();
		initView(view);
		return view;
	}

	/**
	 * 初始数据
	 */
	private void initData() {
		this.type = getArguments().getInt("type");
		voiceConfig = (ProductVoiceConfig) getArguments().getSerializable("productVoiceConfig");
		cvcDaoImpl = new CustomerVoiceConfigDaoImpl(getActivity());
	}
	
	/**
	 * 初始View
	 * @param view
	 */
	private void initView(View view){
		this.taskName = (EditText) view.findViewById(R.id.voice_repeat_name);
		this.repeatLayout = (LinearLayout) view.findViewById(R.id.voice_repeat_layout);
		this.timeLayout = (LinearLayout) view.findViewById(R.id.voice_time_set_layout);
		this.repeatPreiod = (TextView) view.findViewById(R.id.voice_repeat_preiod);
		this.repeatTime = (TextView) view.findViewById(R.id.voice_repeat_time);
		
		repeatLayout.setOnClickListener(this);
		timeLayout.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_mode_btn:
			String name = taskName.getText().toString();
			if (TextUtils.isEmpty(name)) {
				JxshApp.showToast(getActivity(), "任务名不能为空");
				return false;
			}
			// 判断时间 
			if (repeatType == 2) {
				if (!isTimeLegel()) {
					JxshApp.showToast(getActivity(), "该任务不能设置在当前时间之前");
					return false;
				}
			}
			JSONObject _jb = new JSONObject();
			try {
				_jb.put("type", repeatType);
				_jb.put("period", cornDays);
				_jb.put("time", cornTime);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			cornExpression = _jb.toString();
			setCustomerVoiceToDB(voiceConfig, name, cornExpression);
//			((VoiceConfigActivity)getActivity()).getSupportFragmentManager().popBackStack();
			getActivity().onBackPressed();
			break;
		}
		return false;
	}
	
	/**
	 * 添加任务到数据库
	 * @param voiceConfig
	 * @param name 任务名
	 * @param cornExpression 任务表达式
	 */
	private void setCustomerVoiceToDB(ProductVoiceConfig voiceConfig,String name, String cornExpression){
		if (type == 3) {//添加新闻定时任务
			customerVoiceConfig = new CustomerVoiceConfig(voiceConfig.getId(), name,
					type, voiceConfig.getContent(),
						voiceConfig.getTitle(), voiceConfig.getSummary(), 1,period, cornExpression);
			cvcDaoImpl.insert(customerVoiceConfig, true);
			List<CustomerVoiceConfig> list = cvcDaoImpl.find(
					null, "type=?", new String[]{Integer.toString(type)}, null, null, null, null);
			TimerManager.sendVoiceTimerAddBroadcast(getActivity(), list.get(0));
		}else{
			customerVoiceConfig = new CustomerVoiceConfig(voiceConfig.getId(), name,
					voiceConfig.getTypeId(), voiceConfig.getContent(),
						voiceConfig.getTitle(), voiceConfig.getSummary(), 1, period, cornExpression);
			cvcDaoImpl.insert(customerVoiceConfig, true);
			List<CustomerVoiceConfig> list = cvcDaoImpl.find(
					null, "voiceId=?", new String[]{Integer.
							toString(customerVoiceConfig.getVoiceId())}, null, null, null, null);
			TimerManager.sendVoiceTimerAddBroadcast(getActivity(), list.get(0));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		((VoiceConfigActivity) getActivity()).changeMenu(1);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		((VoiceConfigActivity) getActivity()).changeMenu(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.voice_repeat_layout://重复菜单对话框
			showTimeRepeatDialog();
			break;
		case R.id.voice_time_set_layout://时间选择
			 TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
					 	setListener, currentHour, currentMinute, true);  
		        timePickerDialog.show();
			break;

		}
	}
	
	/**
	 * 显示任务重复选中对话框
	 */
	private void showTimeRepeatDialog() {
		final String[] arr = getResources().getStringArray(R.array.repeat_items);
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.customer_add_timer_list, null);
		
		ListView mList = (ListView) dialogView.findViewById(R.id.add_time_listview);
		final AddTimeRepeatAdapter _reAdapter = new AddTimeRepeatAdapter(getActivity(),repeatType,arr);
		mList.setAdapter(_reAdapter);
		final Dialog dialog = BottomDialogHelper.showDialogInBottom(getActivity(), dialogView, null);
		dialog.show();

		mList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				repeatType = arg2;
				if (arg2<arr.length-1) {
					period = arr[arg2];
					repeatPreiod.setText(arr[arg2]);
					switch (arg2) {
					case 0:
						cornDays = "1,2,3,4,5,6,7";
						break;
					case 1:
						cornDays = "2,3,4,5,6";
						break;
					case 2:
						SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
						cornDays = dateFormat.format(new Date());
						break;
					}
					
				}else{
					final String[] array = getResources().getStringArray(R.array.repeat_days_items);
					LayoutInflater inflater = (LayoutInflater) getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View dialogView = inflater.inflate(R.layout.customer_add_days_list, null);
					ListView dayList = (ListView) dialogView.findViewById(R.id.add_time_listview);
					Button btnSure = (Button) dialogView.findViewById(R.id.btn_yes);
					Button btnNo = (Button) dialogView.findViewById(R.id.btn_no);
					final AddTimerSelectDaysAdapter _daysAdapter = new AddTimerSelectDaysAdapter(getActivity(), 0,array);
					dayList.setAdapter(_daysAdapter);
					final Dialog dayDialog = BottomDialogHelper.showDialogInBottom(getActivity(), dialogView, null);
					btnSure.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							cornDays = _daysAdapter.getIdString();
							if (cornDays != "") {
								char[] tempArr  = cornDays.toCharArray();
								Arrays.sort(tempArr);
								StringBuffer buffer = new StringBuffer();
								buffer.append(Integer.parseInt(String.valueOf(tempArr[0])));
								StringBuffer strBuffer = new StringBuffer();
								strBuffer.append(array[Integer.parseInt(
										String.valueOf(tempArr[0]))-1]);
								if (tempArr.length>1) {
									for (int i = 1; i < tempArr.length; i++) {
										buffer.append(","+String.valueOf(tempArr[i]));
										strBuffer.append(","+array[Integer.parseInt(
												String.valueOf(tempArr[i]))-1]);
									}
								}
								cornDays = buffer.toString();
								period = strBuffer.toString();
								repeatPreiod.setText(period);
							}
							dayDialog.dismiss();
						}
					});
					btnNo.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dayDialog.dismiss();
						}
					});
					
					dayDialog.show();
				}
			}
		});
		
	}
	
	//当点击TimePickerDialog控件的设置按钮时，调用该方法    
    TimePickerDialog.OnTimeSetListener setListener = new TimePickerDialog.OnTimeSetListener()    
    {  
        @Override  
        public void onTimeSet(TimePicker view, int hour, int minute){
        	cornTime = getTime(hour, minute);
        	currentHour = hour;
        	currentMinute = minute;
        	repeatTime.setText(cornTime);
        }   
        
    };
    
    /**
     * 时间格式化
     * @param hour
     * @param min
     * @return
     */
    public String getTime(int hour,int min){
		StringBuffer temp = new StringBuffer();
		if (hour < 10) {
			temp.append("0"+hour+":");
		}else{
			temp.append(hour+":");
		}
		if (min<10) {
			temp.append("0"+min);
		}else {
			temp.append(min+"");
		}
		temp.append(":00");
		return temp.toString();
	}
    
    private boolean isTimeLegel(){
		String strHour = "00";
		String strMin = "00";
		SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
		String currentDay = dateFormat.format(new Date());
		if (currentHour < 10) {
			strHour = "0"+Integer.toString(currentHour);
		}else{
			strHour = Integer.toString(currentHour);
		}
		if (currentMinute < 10) {
			strMin = "0"+Integer.toString(currentMinute);
		}else{
			strMin = Integer.toString(currentMinute);
		}
		String setTimeStr = currentDay + " " + strHour + ":" + strMin + ":00";
		long setTime = DateUtil.convertStrToLongNew(setTimeStr);
		long currentTime = DateUtil.getNow().getTime();
		if(setTime <= currentTime) {
			return false;
		}else{
			return true;
		}
	
	}
}
