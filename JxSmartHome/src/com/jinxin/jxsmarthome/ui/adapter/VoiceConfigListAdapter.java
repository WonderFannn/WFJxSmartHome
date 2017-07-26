package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.db.impl.CustomerVoiceConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.VoiceConfigActivity;
import com.jinxin.jxsmarthome.entity.CustomerVoiceConfig;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;
import com.jinxin.jxsmarthome.fragment.VoiceTimerModifyFragment;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.service.TimerManager;

/**
 * 语音列表显示的Adapter
 * @author TangLong
 * @company 金鑫智慧
 */
public class VoiceConfigListAdapter extends BaseAdapter{

	private Context context;
	private List<ProductVoiceConfig> typeList = null;
	private List<CustomerVoiceConfig> customerVoiceList = null;
	private CustomerVoiceConfigDaoImpl cvcDatImpl = null;
	private HashMap<Integer,Boolean> isSelected;//标识按钮选中
	private List<String> selectedList = null;
	
	private int typeId = -1;
	private int type = -1;
	boolean isShow = false;
	private int mLastPosition = -1;
	
	@SuppressLint("UseSparseArrays")
	public VoiceConfigListAdapter(Context context, List<ProductVoiceConfig> voiceList, int typeId, int type) {
		this.context = context;
		this.typeList = voiceList;
		this.typeId = typeId;
		this.type = type;
		cvcDatImpl = new CustomerVoiceConfigDaoImpl(this.context);
		customerVoiceList = cvcDatImpl.find(null,"type=?",
				new String[]{Integer.toString(type)}, null, null, null, null);
		isSelected = new HashMap<Integer, Boolean>();
		selectedList = new ArrayList<String>();
		initSelectedItem();
	}
	// 初始化isSelected的数据
    public void initSelectedItem(){
    	if (typeList != null && typeList.size() > 0) {
			for (int i = 0; i < typeList.size(); i++) {
				isSelected.put(i,false);
			}
		}
    }
	
	@Override
	public int getCount() {
		return this.typeList != null ? typeList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return typeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * 获取选中的内容
	 * @return
	 */
	public List<String> getSelectedList(){
		return selectedList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		final int pos = position;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(R.layout.voice_config_news_item);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		
		final ProductVoiceConfig  voiceConfig = typeList.get(position);
		final CustomerVoiceConfig cVoice = getCustomerVoice(voiceConfig);
		
		String title = voiceConfig.getTitle();
		if (type == 2 ||type == 3) {
			holder.mTitle.setVisibility(View.VISIBLE);
			holder.mTitle.setText(title);
			holder.mContent.setSingleLine(true);
			holder.mContent.setText(voiceConfig.getSummary());
		}else{
			holder.mTitle.setVisibility(View.GONE);
			holder.mContent.setSingleLine(false);
			holder.mContent.setText("    "+voiceConfig.getContent());
		}
		
		//显示或隐藏定时详情按钮
		if (isSetTimer(voiceConfig)) {
			holder.itemMenu.setVisibility(View.VISIBLE);
			holder.itemDelete.setVisibility(View.VISIBLE);
		}else{
			holder.itemMenu.setVisibility(View.GONE);
			holder.itemDelete.setVisibility(View.GONE);
		}
		//显示、隐藏定时详情界面
		if (mLastPosition == position) {
			holder.hideView.setVisibility(View.VISIBLE);
		}else{
			holder.hideView.setVisibility(View.GONE);
		}
		//定时任务开关
		if (cVoice != null) {
			holder.toogleBtn.setChecked(cVoice.isOPen() == 1);
			holder.timerTitle.setText(cVoice.getName());
			try {
				JSONObject _jb = new JSONObject(cVoice.getCornExpression());
				int type = _jb.getInt("type");
				String expresion = getRepeatDate(type) +" "+_jb.getString("time");
				holder.timerInfo.setText(expresion);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		//显示、隐藏多选按钮
		if (isShow) {
			holder.itemCheck.setVisibility(View.VISIBLE);
		}else{
			holder.itemCheck.setVisibility(View.INVISIBLE);
		}
		
		// 根据isSelected来设置checkbox的选中状况
		if (isSelected.size() > 0) {
			holder.itemCheck.setChecked(isSelected.get(position));
		}
		
		holder.itemMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mLastPosition != pos) {
					mLastPosition = pos;
				}else{
					mLastPosition = -1;
				}
				notifyDataSetChanged();
			}
		});
		
		//删除定时任务
		holder.itemDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (cVoice != null) {
					cvcDatImpl.delete(cVoice.getId());
					mLastPosition = -1;
					notifyDataSetChanged();
				}
			}
		});
		
		holder.itemCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isSelected.put(pos, isChecked);
				if (isChecked) {
					selectedList.add(voiceConfig.getContent());
				}else{
					if (selectedList.size() > 0) {
						for (int i = 0; i < selectedList.size(); i++) {
							if (voiceConfig.getContent().equals(selectedList.get(i))) {
								selectedList.remove(i);
							}
						}
					}
				}
			}
		});
		
		holder.toogleBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cVoice.setOPen(1);
					TimerManager.sendVoiceTimerAddBroadcast(context, cVoice);
				}else{
					cVoice.setOPen(0);
					TimerManager.sendVoiceTimerCancelBroadcast(context, cVoice);
				}
				cvcDatImpl.update(cVoice);
			}
		});
		
		holder.hideView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				VoiceTimerModifyFragment fragment = new VoiceTimerModifyFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("typeId", typeId);
				bundle.putSerializable("customerVoiceConfig", cVoice);
				fragment.setArguments(bundle);
				((VoiceConfigActivity)context).
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.voice_fragment_layout, fragment)
						.addToBackStack(null).commit();
			}
		});

		return convertView;
	}

	class Holder {
		TextView mTitle;
		TextView mContent;
		CheckBox itemCheck;
		ImageView itemMenu;
		ImageView itemDelete;
		TextView timerTitle;
		TextView timerInfo;
		CheckBox toogleBtn;
		LinearLayout hideView;
		public Holder(View view) {
			mTitle = (TextView) view.findViewById(R.id.voice_item_title);
			mContent = (TextView) view.findViewById(R.id.voice_item_content);
			itemCheck = (CheckBox) view.findViewById(R.id.voice_tiemer_multiple_choice);
			itemMenu = (ImageView) view.findViewById(R.id.voice_temer_menu);
			itemDelete = (ImageView) view.findViewById(R.id.voice_temer_delete);
			timerTitle = (TextView) view.findViewById(R.id.timer_voice_title);
			timerInfo = (TextView) view.findViewById(R.id.timer_voice_time);
			toogleBtn = (CheckBox) view.findViewById(R.id.voice_tiemer_check_iv);
			hideView = (LinearLayout) view.findViewById(R.id.voice_hide_layout);
		}
	}

	/**
	 * 显示、隐藏设置多选按钮的标识
	 * @param isShow
	 */
	public void showMultipleButton(boolean isShow){
		this.isShow = isShow;
		if (!isShow) {
			initSelectedItem();
		}
	}
	
	/**
	 * 判断是否设置过定时
	 * @param voiceConfig
	 * @return
	 */
	public boolean isSetTimer(ProductVoiceConfig  voiceConfig){
		if (voiceConfig == null || customerVoiceList == null || customerVoiceList.size() < 1) {
			return false;
		}
		List<CustomerVoiceConfig> tempList = cvcDatImpl.find(null,"voiceId=?",
				new String[]{Integer.toString(voiceConfig.getId())}, null, null, null, null);
		if (tempList != null && tempList.size() > 0) {
			return true;
		}
//		for (CustomerVoiceConfig  _cv: customerVoiceList) {
//			if (_cv.getVoiceId() == voiceConfig.getId()) {
//				return true;
//			}
//		}
		return false;
	}
	
	public CustomerVoiceConfig getCustomerVoice(ProductVoiceConfig  voiceConfig){
		if (voiceConfig == null || customerVoiceList == null || customerVoiceList.size() < 1) {
			return null;
		}
		
		for (CustomerVoiceConfig  _cv: customerVoiceList) {
			if (_cv.getVoiceId() == voiceConfig.getId()) {
				return _cv;
			}
		}
		
		return null;
	}
	
	public void showView(View view){
		if (view.isShown()) {
			view.setVisibility(View.GONE);
		}else{
			view.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 获取定时重复类型名
	 * @param type
	 * @return
	 */
	private String getRepeatDate(int type){
		String[] arr = context.getResources().getStringArray(R.array.repeat_items);
		String repeatDate ="";
		switch (type) {
		case 0:
			repeatDate = arr[0];
			break;
		case 1:
			repeatDate = arr[1];
			break;
		case 2:
			repeatDate = arr[2];
			break;
		case 3:
			repeatDate = arr[3];
			break;
		}
		return repeatDate;
	}

}
