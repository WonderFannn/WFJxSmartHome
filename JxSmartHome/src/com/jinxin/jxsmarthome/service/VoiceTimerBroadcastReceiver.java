package com.jinxin.jxsmarthome.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jinxin.db.impl.CustomerVoiceConfigDaoImpl;
import com.jinxin.db.impl.ProductVoiceConfigDaoImpl;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.device.Text2VoiceManager;
import com.jinxin.jxsmarthome.entity.CornExpression;
import com.jinxin.jxsmarthome.entity.CustomerVoiceConfig;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;
import com.jinxin.jxsmarthome.util.Logger;

public class VoiceTimerBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		CustomerVoiceConfig cvc = (CustomerVoiceConfig) intent.getSerializableExtra("voiceTimer");
		
		// 开机时注册全部启用的语音定时事件
		if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			List<CustomerVoiceConfig> cVoiceList = getVoiceTimerFromDB(context);
			Logger.warn(null, "voice timer number:" + cVoiceList.size());
			if (cVoiceList != null && cVoiceList.size() > 0) {
				for (CustomerVoiceConfig customerVoiceConfig : cVoiceList) {
					JSONObject jb;
					try {
						jb = new JSONObject(customerVoiceConfig.getCornExpression());
						CornExpression exp = new CornExpression(jb.getInt("type"), 
								jb.getString("period"), jb.getString("time"));
						long triggerTime = TimerManager.getNextExecuteTime(exp.getType(), exp.getPeriod(), 
								exp.getTime());
						Logger.debug(null, TimerManager.parseLongToStr(triggerTime));
						TimerManager.setVoiceTimeTask(context, customerVoiceConfig.getId(), triggerTime, customerVoiceConfig);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		// 执行语音定时任务
		}else if(BroadcastManager.ACTION_DO_VOICE_TIMER.equals(action)){
			if (cvc == null) return;
			Logger.warn(null, "do voice timer task:" + cvc.toString());
			if (cvc.getType() == 3) {//新闻类语音
				List<String> voiceList = getNewsStringList(context);
				if (voiceList != null && voiceList.size() > 0) {
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("voiceList", (ArrayList<String>) voiceList);
					BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_MULTIPLE_PLAY, bundle);
				}
			}else{
				Text2VoiceManager manager = new Text2VoiceManager(context);
				manager.switchAndSend(cvc.getContent());
			}
			// 设置下次执行任务
			try {
				JSONObject jb = new JSONObject(cvc.getCornExpression());
				CornExpression exp = new CornExpression(jb.getInt("type"), 
						jb.getString("period"), jb.getString("time"));
				if(exp != null && exp.getType() != 2) {
					Logger.warn(null, "set next timer:" + cvc.toString());
					setNextTimeTask(context, cvc);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		//取消语音定时任务
		}else if(BroadcastManager.ACTION_VOICE_TIMER_CANCLE.equals(action)){
			Logger.warn(null, "voice cancel timer");
			if (cvc != null) {
				TimerManager.cancleVoiceTimerTask(context, cvc.getVoiceId());
			}
		//添加定时语音
		}else if(BroadcastManager.ACTION_VOICE_TIMER_ADD.equals(action)){
			Logger.warn(null, "add voice timer task");
			if(cvc != null) {
				try {
					String coreExpression = cvc.getCornExpression();
					Logger.warn(null, "add timer coreExpression:" + coreExpression);
					if(coreExpression != null) {
						JSONObject jb = new JSONObject(coreExpression);
						CornExpression exp = new CornExpression(jb.getInt("type"), 
								jb.getString("period"), jb.getString("time"));
						
						long triggerTime = TimerManager.getNextExecuteTime(exp.getType(), exp.getPeriod(), 
								exp.getTime());
						
						if(triggerTime != -1) {
							TimerManager.setVoiceTimeTask(context, cvc.getVoiceId(), triggerTime, cvc);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		// 修改语音定时任务
		} else if(BroadcastManager.ACTION_VOICE_TIMER_AMEND.equals(intent.getAction())) {
			Logger.warn(null, "modify timer:" + cvc.toString());
			if(cvc != null) {
				try {
					JSONObject jb = new JSONObject(cvc.getCornExpression());
					CornExpression exp = new CornExpression(jb.getInt("type"), 
							jb.getString("period"), jb.getString("time"));
					long triggerTime = TimerManager.getNextExecuteTime(exp.getType(), exp.getPeriod(), 
							exp.getTime());
					if(triggerTime != -1) {
						TimerManager.cancelTimeTask(context, cvc.getId());
						TimerManager.setVoiceTimeTask(context, cvc.getId(), triggerTime, cvc);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 设置定时任务下一次执行时间
	 * @param context		Context对象
	 * @param cvc			定时任务对象
	 */
	private void setNextTimeTask(Context context, CustomerVoiceConfig cvc) {
		if(cvc != null) {
			try {
				JSONObject jb = new JSONObject(cvc.getCornExpression());
				CornExpression exp = new CornExpression(jb.getInt("type"), 
						jb.getString("period"), jb.getString("time"));
				long triggerTime = TimerManager.getNextExecuteTime(exp.getType(), exp.getPeriod(), 
						exp.getTime());
				
				Logger.debug(null, TimerManager.parseLongToStr(triggerTime));
				
				TimerManager.setVoiceTimeTask(context, cvc.getId(), triggerTime, cvc);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取语音定时任务列表
	 * @param context
	 * @return
	 */
	private List<CustomerVoiceConfig> getVoiceTimerFromDB(Context context){
		CustomerVoiceConfigDaoImpl cvcDaoImpl = new CustomerVoiceConfigDaoImpl(context);
		List<CustomerVoiceConfig> cVoiceList = cvcDaoImpl.find(
				null, "isOpen=?", new String[]{}, null, null, null, null);
		return cVoiceList;
	}
	
	/**
	 * 获取所有新闻内容列表
	 * @param context
	 * @return
	 */
	private List<String> getNewsStringList(Context context){
		List<ProductVoiceConfig> pVoiceList = getVoiceListFromDB(context);
		List<String> voiceList = new ArrayList<String>();
		if (pVoiceList != null && pVoiceList.size() > 0) {
			for (ProductVoiceConfig pvc : pVoiceList) {
				voiceList.add(pvc.getTitle() + " " +pvc.getSummary());
			}
		}
		return voiceList;
	}
	
	/**
	 * 获取新闻列表
	 * @param context
	 * @return
	 */
	private List<ProductVoiceConfig> getVoiceListFromDB(Context context){
		ProductVoiceConfigDaoImpl pvcDaoImpl = new ProductVoiceConfigDaoImpl(context);
		List<ProductVoiceConfig> pVoiceList = pvcDaoImpl.find(
				null, "type=?", new String[]{Integer.toString(3)}, null, null, null, null);
		return pVoiceList;
	}
	
}
