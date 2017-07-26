package com.jinxin.jxsmarthome.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.OnlineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.TimerCmdGenerator;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.entity.CornExpression;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 定时器广播管理
 * @author TangLong
 * @company 金鑫智慧
 */
public class TimerBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.debug(null, "TimerBroadcastReceiver:" + intent.getAction());
		
		CustomerTimer ct = (CustomerTimer) intent.getSerializableExtra("timer");
		
		// 开机时注册全部启用的定时事件
		if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Logger.warn(null, "set timer after reboot");
//			String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,"");
//			DBHelper.setDbName(_account);
			List<CustomerTimer> listTimer = getTimerFromDb(context);
			Logger.warn(null, "timer number:" + listTimer.size());
			if(listTimer != null && listTimer.size() > 0) {
				for(CustomerTimer timer : listTimer) {
					if(timer != null) {
						try {
							JSONObject jb = new JSONObject(timer.getCornExpression());
							CornExpression exp = new CornExpression(jb.getInt("type"), 
									jb.getString("period"), jb.getString("time"));
							long triggerTime = TimerManager.getNextExecuteTime(exp.getType(), exp.getPeriod(), 
									exp.getTime());
							Logger.debug(null, "TimerManager:"+TimerManager.parseLongToStr(triggerTime));
							TimerManager.setTimeTask(context, timer.getTaskId(), triggerTime, timer);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		
		// 执行定时任务
		}else if("com.jinxin.jxsmarthome.ACTION_TIMER".equals(intent.getAction())) {
			/*Logger.warn(null, "action timer broadcast");
			Logger.warn(null, "do timer task:" + ct.toString());
			// 发送命令
			int taskId = ct.getTaskId();
//			generateAndSendTimerCmd(context, taskId);
			
			// 设置下次执行任务（周期性任务）
			try {
				JSONObject jb = new JSONObject(ct.getCornExpression());
				CornExpression exp = new CornExpression(jb.getInt("type"), 
						jb.getString("period"), jb.getString("time"));
				if(exp != null && exp.getType() != 2) {
					Logger.warn(null, "set next timer:" + ct.toString());
					setNextTimeTask(context, ct);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
			
		// 移出定时任务
		}else if("com.jinxin.jxsmarthome.ACTION_TIMER_CANCEL".equals(intent.getAction())) {
			Logger.warn(null, "cancel timer:" + ct.toString());
			if(ct != null) {
				TimerManager.cancelTimeTask(context, ct.getTaskId());
			}
		
		// 增加定时任务
		}else if("com.jinxin.jxsmarthome.ACTION_TIMER_ADD".equals(intent.getAction())) {
			Logger.warn(null, "add timer:" + ct.toString());
			if(ct != null) {
				try {
					String coreExpression = ct.getCornExpression();
					Logger.warn(null, "add timer:" + coreExpression);
					if(coreExpression != null) {
						JSONObject jb = new JSONObject(coreExpression);
						CornExpression exp = new CornExpression(jb.getInt("type"), 
								jb.getString("period"), jb.getString("time"));
						
						long triggerTime = TimerManager.getNextExecuteTime(exp.getType(), exp.getPeriod(), 
								exp.getTime());
						
						if(triggerTime != -1) {
							TimerManager.setTimeTask(context, ct.getTaskId(), triggerTime, ct);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		// 修改定时任务	
		} else if("com.jinxin.jxsmarthome.ACTION_TIMER_MODIFY".equals(intent.getAction())) {
			Logger.warn(null, "modify timer:" + ct.toString());
			if(ct != null) {
				try {
					JSONObject jb = new JSONObject(ct.getCornExpression());
					CornExpression exp = new CornExpression(jb.getInt("type"), 
							jb.getString("period"), jb.getString("time"));
					long triggerTime = TimerManager.getNextExecuteTime(exp.getType(), exp.getPeriod(), 
							exp.getTime());
					if(triggerTime != -1) {
						TimerManager.cancelTimeTask(context, ct.getTaskId());
						TimerManager.setTimeTask(context, ct.getTaskId(), triggerTime, ct);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		// 广播更新
		BroadcastManager
				.sendBroadcast(
						BroadcastManager.ACTION_UPDATE_TASK_MESSAGE,
						null);
	}
	
	/**
	 * 从数据库中获取定时任务
	 * @param context	Context对象
	 * @return			获取的对象
	 */
	private List<CustomerTimer> getTimerFromDb(Context context) {
		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(context);
		List<CustomerTimer> timerList = dao.find(null, null, null, 
				null, null, null, null);
		return timerList;
	}
	
	/**
	 * 设置定时任务下一次执行时间
	 * @param context		Context对象
	 * @param ct			定时任务对象
	 */
	private void setNextTimeTask(Context context, CustomerTimer ct) {
		if(ct != null) {
			try {
				JSONObject jb = new JSONObject(ct.getCornExpression());
				CornExpression exp = new CornExpression(jb.getInt("type"), 
						jb.getString("period"), jb.getString("time"));
				long triggerTime = TimerManager.getNextExecuteTime(exp.getType(), exp.getPeriod(), 
						exp.getTime());
				
				Logger.debug(null, "TimerManager:"+TimerManager.parseLongToStr(triggerTime));
				
				TimerManager.setTimeTask(context, ct.getTaskId(), triggerTime, ct);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 生成并发送定时任务的命令
	 * @param context
	 * @param id
	 */
	private void generateAndSendTimerCmd(Context context, int id) {
		Logger.warn(null, "do timer task action");
		
		TimerCmdGenerator cmdGenerator = new TimerCmdGenerator(context);
		if(NetworkModeSwitcher.useOfflineMode(context)) {
			List<Command> cmdList = cmdGenerator.generateOfflineCmd(id);
			if(cmdList != null && cmdList.size() > 0) {
				OfflineMulitGatewayCmdSender cmdSender = new OfflineMulitGatewayCmdSender(context, cmdList);
//				cmdSender.send();
			}
		}else {
			List<Command> cmdList = cmdGenerator.generateOnlineCmd(id);
			if(cmdList != null && cmdList.size() > 0) {
				OnlineMulitGatewayCmdSender cmdSender = new OnlineMulitGatewayCmdSender(context, cmdList);
//				cmdSender.send();
			}
		}
	}

}
