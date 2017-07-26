package com.jinxin.jxsmarthome.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.data.TimerModeVO;

/**
 * 修改操作模式的定时任务适配器
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class AmendTimerModeAdapter extends BaseAdapter {
	
	private Context context;
	private Map<Integer, Integer> map = null;
	private List<TimerModeVO> tmVOLists = null;
	private Handler mHandler = null;
	
	@SuppressLint("UseSparseArrays")
	public AmendTimerModeAdapter(Context context,List<TimerModeVO> tmVOLists) {
		this.context = context;
		this.tmVOLists = tmVOLists;
		map = new HashMap<Integer, Integer>();
		for (TimerModeVO timerVO : tmVOLists) {
			if (timerVO.isSelected()) {
				map.put(timerVO.getcPattern().getPatternId(),
							timerVO.getcPattern().getPatternId());
			}
		}
		
		this.mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				notifyDataSetChanged();
			}
			
		};
	}

	@Override
	public int getCount() {
		return this.tmVOLists == null ? 0 : tmVOLists.size();
	}

	@Override
	public Object getItem(int position) {
		return this.tmVOLists == null ? null : tmVOLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * @return 选中的模式ID
	 */
	public Map<Integer, Integer> getPatternIdMap(){
		if (map!=null) 
		{
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
			convertView=CommDefines.getSkinManager().view(R.layout.add_mode_list_item);
			holder=new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		
		final TimerModeVO timerVO = tmVOLists.get(pos);
		
		holder.checkBox_selected.setChecked(timerVO.isSelected());
		holder.imageButton_open.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ico_swithch_on));
		holder.textView_name.setText(timerVO.getcPattern().getPaternName());
		
		holder.checkBox_selected.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				timerVO.setSelected(holder.checkBox_selected.isChecked());
				mHandler.sendEmptyMessage(0);
			}
		});
		if (timerVO != null) {
			if (timerVO.isSelected()) {
				map.put(timerVO.getcPattern().getPatternId(), timerVO.getcPattern().getPatternId());
			}else{
				map.remove(timerVO.getcPattern().getPatternId());
			}
		}
		
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
