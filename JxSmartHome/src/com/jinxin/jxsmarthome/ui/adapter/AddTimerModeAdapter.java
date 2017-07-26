package com.jinxin.jxsmarthome.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.data.TimerModeVO;

/**
 * 添加操作模式的定时任务适配器
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class AddTimerModeAdapter extends BaseAdapter {
	
	private List<TimerModeVO> timerModeVoLists = null;
	private Context context;
	private Map<Integer, Integer> map = null;
	
	
	public AddTimerModeAdapter(Context context,List<TimerModeVO> timerModeVoLists) {
		this.context = context;
		this.timerModeVoLists = timerModeVoLists;
	}

	@Override
	public int getCount() {
		return this.timerModeVoLists == null ? 0 : timerModeVoLists.size();
	}

	@Override
	public Object getItem(int position) {
		return this.timerModeVoLists == null ? null : timerModeVoLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * @return 选中的模式ID
	 */
	public Map<Integer, Integer> getPatternIdMap(){
		if (map!=null) {
			return map;
		}
		return null;
	}

	@SuppressLint("UseSparseArrays")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		final int pos = position;
		if(convertView==null){
			map = new HashMap<Integer, Integer>();
			convertView=CommDefines.getSkinManager().view(R.layout.add_mode_list_item);
			holder=new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		final TimerModeVO _timerModeVo = timerModeVoLists.get(position);
		if (_timerModeVo != null) {
			holder.checkBox_selected.setChecked(_timerModeVo.isSelected());
			holder.imageButton_open.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ico_swithch_on));
			holder.textView_name.setText(_timerModeVo.getcPattern().getPaternName());
		}
		
		holder.checkBox_selected.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_timerModeVo.setSelected(holder.checkBox_selected.isChecked());
			}
		});
		
		holder.checkBox_selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					map.put(pos, _timerModeVo.getcPattern().getPatternId());
				}else{
					map.remove(pos);
				}
			}
		});
		holder.imageButton_open.setVisibility(View.INVISIBLE);
		
		return convertView;
	}
	
    class Holder{
		CheckBox checkBox_selected;
		TextView textView_name;
		ImageButton imageButton_open;
		LinearLayout linearItem;
		public Holder(View view){
			checkBox_selected=(CheckBox) view.findViewById(R.id.checkBox_selected);
			textView_name=(TextView) view.findViewById(R.id.textView_name);
			imageButton_open=(ImageButton) view.findViewById(R.id.imageButton_open);
			linearItem=(LinearLayout) view.findViewById(R.id.linearItem);
		}
	}
    
}
