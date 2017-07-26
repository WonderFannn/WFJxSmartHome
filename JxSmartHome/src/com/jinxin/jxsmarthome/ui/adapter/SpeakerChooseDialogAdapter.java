package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 扬声器适配器
 * @author TangLong
 * @company 金鑫智慧
 */
public class SpeakerChooseDialogAdapter extends BaseAdapter {
	private Context context;
	private List<String> speakers; // 扬声器的名字
	private StringBuilder selectedSpeaker; // 保存的扬声器路数设置
	private SparseBooleanArray checkboxItems; // 改变时的扬声器选择路数设置

	public SpeakerChooseDialogAdapter(Context context, List<String> speakers) {
		super();
		this.context = context;
		this.speakers = speakers;
		checkboxItems = new SparseBooleanArray();
		
		// 从配置中获取speaker配置信息
		selectedSpeaker = getSpeakerSelectedItem();
	}

	public SparseBooleanArray getCheckboxItems() {
		return checkboxItems;
	}

	@Override
	public int getCount() {
		return speakers.size();
	}

	@Override
	public Object getItem(int position) {
		return speakers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		final ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.speaker_choose_dialog_list_item, null);
			holder.mName = (TextView) convertView
					.findViewById(R.id.speaker_choose_item_name);
			holder.mSelector = (CheckBox) convertView
					.findViewById(R.id.speaker_choose_item_ck);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (parent.getChildCount() == position) {
			if (selectedSpeaker.charAt(position) == '1') {
				holder.mSelector.setChecked(true);
			}
		}

		holder.mSelector
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						checkboxItems.put(pos, isChecked);
					}
				});

		holder.mName.setText(speakers.get(position));

		return convertView;
	}

	private static final class ViewHolder {
		TextView mName;
		CheckBox mSelector;
	}

	private String getZeroString(int count) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < count; i++) {
			sb.append("0");
		}

		return sb.toString();
	}

	/**
	 * 得到保存的扬声器设置
	 */
	public StringBuilder getSpeakerSelectedItem() {
		String selectedSpeaker = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "");
		if (TextUtils.isEmpty(selectedSpeaker)) {
			CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(context);
			List<CloudSetting> csList = csDaoImpl.find(null, "customer_id=? and items=?",
					new String[]{CommUtil.getCurrentLoginAccount(),
				StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT }, null, null, null, null);
			if (csList != null && csList.size() > 0) {
				selectedSpeaker = csList.get(0).getParams();
			}
		}
		
		StringBuilder sBuilder = new StringBuilder(getZeroString(getCount()));

		// 如果扬声器设置过并且和服务器端同步的路数相同，则显示已设置情况,否则所有路数设置为未选择
		if (!CommUtil.isEmpty(selectedSpeaker)
				&& selectedSpeaker.length() == getCount()) {
			for (int i = 0; i < selectedSpeaker.length(); i++) {
				if ('1' == selectedSpeaker.charAt(i)) {
					checkboxItems.put(i, true);
					sBuilder.setCharAt(i, '1');
				} else {
					checkboxItems.put(i, false);
				}
			}
		} else {
			for (int i = 0; i < getCount(); i++) {
				checkboxItems.put(i, false);
			}
		}

		return sBuilder;
	}
}