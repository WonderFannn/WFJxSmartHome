package com.jinxin.jxsmarthome.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jinxin.datan.net.command.ChangeCustomerTimerTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CornExpression;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.service.TimerManager;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 定时器的适配器
 * @author TangLong
 * @company 金鑫智慧
 */
public class TimerAdapter extends BaseAdapter {
	private List<Boolean> checkStatusList;
	private List<CustomerTimer> listData;
	private Context context;
	CustomerTimerTaskDaoImpl dao;
	private Set<Integer> disableList;
	
	public TimerAdapter(List<CustomerTimer> listData, Context context) {
		checkStatusList = new ArrayList<Boolean>();
		disableList = new HashSet<Integer>();
		dao = new CustomerTimerTaskDaoImpl(context);
		for(int i = 0; i < listData.size(); i++) {
			int status = listData.get(i).getStatus();
			if(status == 1) {
				checkStatusList.add(true);
			}else {
				checkStatusList.add(false);
			}
		}
		this.listData = listData;
		this.context = context;
	}
	
	public void updateState() {
		checkStatusList.clear();
		for(int i = 0; i < listData.size(); i++) {
			int status = listData.get(i).getStatus();
			if(status == 1) {
				checkStatusList.add(true);
			}else {
				checkStatusList.add(false);
			}
		}
	}
	
	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		final CustomerTimer ct = (CustomerTimer) getItem(position);
		final int p = position;
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_timer, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		
		holder.mSwitcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					ct.setStatus(1);
				}else {
					ct.setStatus(2);
				}
				
				if(checkStatusList.get(p) != isChecked) {
					checkStatusList.set(p, isChecked);
					
					// 更新状态改变到数据库
					updateStateToDb(p, isChecked);
					
					ChangeCustomerTimerTask cctTask = new ChangeCustomerTimerTask(
							context, listData.get(position).getTaskId(),
							listData.get(position).getPeriod(), listData.get(position).getTaskName(), listData.get(position).getCornExpression(), ct.getStatus());
					cctTask.addListener(new ITaskListener<ITask>() {
						@Override
						public void onStarted(ITask task, Object arg) {
							JxshApp.showLoading(
									context,
									CommDefines
											.getSkinManager()
											.string(R.string.bao_cun_zhong));
							
						}
						@Override
						public void onSuccess(ITask task, Object[] arg) {
							JxshApp.closeLoading();
							
						}
						@Override
						public void onCanceled(ITask task, Object arg) {
							JxshApp.closeLoading();
							
						}
						@Override
						public void onFail(ITask task, Object[] arg) {
							JxshApp.closeLoading();
							
						}
						@Override
						public void onProcess(ITask task, Object[] arg) {
							
						}
					});
					cctTask.start();
					
					if(isChecked) {
						sendTimerAddBroadcast(ct);
					}else {
						sendTimerCancelBroadcast(ct);
					}
				}
			}
		});
		
		holder.mName.setText(listData.get(position).getTaskName());
		holder.mPeriod.setText(listData.get(position).getPeriod());
		holder.mDesc.setText(listData.get(position).getRemark());
		
		CornExpression cExpression = null;
		try {
			JSONObject _jb = new JSONObject(listData.get(position).getCornExpression());
			cExpression = new CornExpression(
					_jb.getInt("type"), _jb.getString("period"),_jb.getString("time"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (cExpression!=null) {
			String str;
			try {
				str = (String) cExpression.getTime().subSequence(0, cExpression.getTime().length()-3);
				holder.mTime.setText(formatTime(str));
				
				int type = cExpression.getType();
				String peroid = cExpression.getPeriod();
				if(type == 2) {//仅今天
					SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd hh:mm");
					Date date = dateFormat.parse(peroid + " " + str);
					Date today = new Date(System.currentTimeMillis());
					if(date.before(today)) {
						checkStatusList.set(position, Boolean.valueOf(false));
						disableList.add(position);
						holder.mPeriod.setText(peroid);
						holder.mSwitcher.setClickable(false);
					} else {
						holder.mPeriod.setText(peroid);
						holder.mSwitcher.setClickable(true);
					}
				} else {
					holder.mSwitcher.setClickable(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (checkStatusList != null &&checkStatusList.size() > 0) {
			holder.mSwitcher.setChecked(checkStatusList.get(position));
		}
		
		return convertView;
	}
	
	public Set<Integer> getDisableList() {
		return disableList;
	}
	
	private String formatTime(String val) {
		String format = "00:00";
		if(val.length()>format.length())
			return val;
		String pre = format.substring(0, format.length() - val.length());
		return pre.concat(val);
	}
	
	/**
	 * 更新状态到数据库
	 * @param position		位置
	 * @param isChecked		状态
	 */
	private void updateStateToDb(int position, boolean isChecked) {
		Logger.debug(null, "updateStateToDb");
		CustomerTimer timer = listData.get(position);
		if(isChecked) {
			timer.setStatus(1);
		}else {
			timer.setStatus(2);
		}
		dao.update(timer);
	}
	
	/**
	 * 发送定时器增加的广播
	 */
	private void sendTimerAddBroadcast(CustomerTimer ct) {
		TimerManager.sendTimerAddBroadcast(context, ct);
	}
	
	/**
	 * 发送定时器关闭的广播
	 */
	private void sendTimerCancelBroadcast(CustomerTimer ct) {
		TimerManager.sendTimerCancelBroadcast(context, ct);
	}
	
	class Holder{
		TextView mName;
		TextView mTime;
		TextView mPeriod;
		TextView mDesc;
		ToggleButton mSwitcher;
		
		public Holder(View view){
			mName = (TextView)view.findViewById(R.id.timer_item_name);
			mTime = (TextView)view.findViewById(R.id.timer_item);
			mPeriod = (TextView)view.findViewById(R.id.timer_item_time);
			mDesc = (TextView)view.findViewById(R.id.timer_item_desc);
			mSwitcher = (ToggleButton)view.findViewById(R.id.timer_item_toggle);
		}
	}
}
