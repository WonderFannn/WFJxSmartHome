package com.jinxin.jpush;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.jinxin.datan.net.command.CustomerProductListTask;
import com.jinxin.datan.net.command.UpdateProductRegisterTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerMessageDaoImpl;
import com.jinxin.db.impl.MessageTimerDaoImpl;
import com.jinxin.db.impl.ProductDoorContactDaoImpl;
import com.jinxin.db.impl.ProductRegisterDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CustomerMeassage;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.MessageTimer;
import com.jinxin.jxsmarthome.entity.ProductDoorContact;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.DateUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JPushMessageReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private static final String MESSAGE_ZIGBEE_NETWORK_RECREATED = "Zigbee Device Register successed";//Zigbee 网关重组消息
	private static final String MESSAGE_VOICE_PLAY_COMPLITE = "Voice play complete";//语音播放完毕消息
	
	private static final String MESSAGE_TYPE_100 =  "(100)";//设备状态
	private static final String MESSAGE_TYPE_200 =  "(200)";//无线设备注册成功
	private static final String MESSAGE_TYPE_300 =  "(300)";//语音播报完成
	private static final String MESSAGE_TYPE_400 =  "(400)";//门磁开关状态
	private static final String MESSAGE_TYPE_401 =  "(401)";//门磁电量
	private static final String MESSAGE_TYPE_501 =  "(501)";//烟感电量
	private static final String MESSAGE_TYPE_601 =  "(601)";//红外电量
	private static final String MESSAGE_TYPE_701 =  "(701)";//气感故障
	private static final String MESSAGE_TYPE_500 =  "(500)";//红外状态
	private static final String MESSAGE_TYPE_600 =  "(600)";//烟感状态
	private static final String MESSAGE_TYPE_700 =  "(700)";//气感状态 00:正常，01：过高
	private static final String MESSAGE_TYPE_800 =  "(800)";//无线只能锁功率过载
	private static final String MESSAGE_TYPE_900 =  "(900)";//亲情账号消息
	private static final String MESSAGE_TYPE_1001 =  "(1001)";//无线智能锁电量  64:正常, ff:低电
	private static final String MESSAGE_TYPE_1002 =  "(1002)";//无线智能锁报警
	private static final String MESSAGE_TYPE_2500 =  "(2500)";//只能手表报警
	
	private CustomerMessageDaoImpl cmDaoImpl = null;
	private static final String EXTRA_EXTRA = "extra";//extra消息对应key 值

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Logger.debug(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Logger.debug(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Logger.error(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Logger.debug(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Logger.debug(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Logger.debug(TAG, "[MyReceiver] 用户点击打开了通知");

			JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));

			// 打开自定义的Activity
			Intent i = new Intent(context, TestActivity.class);
			i.putExtras(bundle);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Logger.debug(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else {
			Logger.debug(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// 出来接收到的消息
	private void processCustomMessage(Context context, Bundle bundle) {
//		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String messageJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
		JSONObject _jb;
		String message = "";
		try {
			_jb = new JSONObject(messageJson);
			message = _jb.getString(EXTRA_EXTRA);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (!TextUtils.isEmpty(message) && !"{}".equals(message)) {
			String content="";
			String type="";
			if(message.lastIndexOf("(")>=0){
				content = message.substring(0,message.lastIndexOf("(")).replaceAll("：", ":");
				type = message.substring(message.lastIndexOf("("),message.length());
			}
			ProductFun productFun = null;
			if(MESSAGE_TYPE_100.equals(type)){
				Logger.debug("JPush", "接收到设备状态通知");
				try{
					content = content.substring(5);
				}catch(Exception e){
					content="";
				}
				List<ProductState> psList = new ArrayList<ProductState>();
				if (!TextUtils.isEmpty(content)) {
					JSONObject jb = null;
					try {
						jb = new JSONObject(content);
						@SuppressWarnings("unchecked")
						Iterator<String> it = jb.keys();
						while (it.hasNext()) {
							String key = it.next().toString();
							ProductState _ps = new ProductState();
							_ps.setFunId(Integer.parseInt(key));
							_ps.setState(jb.getString(key));
							psList.add(_ps);
						}
						if (psList.size() > 0) {
							CommonMethod.updateProductStateList(context, psList);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					BroadcastManager.sendBroadcast(
							BroadcastManager.MESSAGE_RECEIVED_ACTION, bundle);
				}
			}else if (MESSAGE_TYPE_200.equals(type)) {
				Logger.debug("JPush", "接收到更新设备通知");
				requestServerToUpdateProductRegister(context);
				return;
			} else if (MESSAGE_TYPE_300.equals(type)) {
				Logger.debug("JPush", "接收到语音播放完成通知");
				BroadcastManager.sendBroadcast(
						BroadcastManager.ACTION_VOICE_MULTIPLE_PLAY_NEXT, null);
				return;
			} else if(MESSAGE_TYPE_400.equals(type) || MESSAGE_TYPE_500.equals(type) ||
					MESSAGE_TYPE_600.equals(type) || MESSAGE_TYPE_700.equals(type)){
				Logger.debug("JPush", "接收到门磁开关通知");
				if (!TextUtils.isEmpty(content)) {
					String[] arr = content.split(",");//拆分消息内容
					if (arr.length > 1) {
						String actionCode="";
						try{
							actionCode = arr[0].substring(arr[0].length()-2, arr[0].length());
						}catch(Exception e){
							actionCode="";
						}
						String whId = arr[1];
						productFun = AppUtil.getSingleProductFunByWhId(context, whId);
						String msg = "";
						if (productFun != null) {
							if (MESSAGE_TYPE_400.equals(type)) {
								if ("01".equals(actionCode)) {
									msg = productFun.getFunName()+":开";
								}else if("00".equals(actionCode)){
									msg = productFun.getFunName()+":关";
								}
							}else if(MESSAGE_TYPE_500.equals(type)){
								if ("01".equals(actionCode)) {
									msg = productFun.getFunName()+":烟雾浓度过高";
								}else if("00".equals(actionCode)){
									msg = productFun.getFunName()+":烟雾浓度恢复正常";
								}
							}else if(MESSAGE_TYPE_600.equals(type)){
								if ("01".equals(actionCode)) {
									msg = productFun.getFunName()+":检测到有人靠近";
								}else if("00".equals(actionCode)){
									msg = productFun.getFunName()+":人已远离";
								}
							}else if(MESSAGE_TYPE_700.equals(type)){
								if ("01".equals(actionCode)) {
									msg = productFun.getFunName()+":气体浓度过高";
								}else if("00".equals(actionCode)){
									msg = productFun.getFunName()+":气体浓度恢复正常";
								}
							}
							
							boolean isOutside = isOpenWarn(whId,context);
							showNotification(context, msg,isOutside);
							
							//存储消息到本地数据库
							saveMessageToLoacl(context, whId, msg);
						}
					}
				}
			} else if(MESSAGE_TYPE_401.equals(type) || MESSAGE_TYPE_501.equals(type) ||
					MESSAGE_TYPE_601.equals(type) || MESSAGE_TYPE_701.equals(type)){
				Logger.debug("JPush", "接收到设备电量上报消息");
				if (!TextUtils.isEmpty(content)) {
					String[] arr = content.split(",");//拆分消息内容
					String msg = "";
					if (arr.length > 1) {
						//消息类型
						String actionCode = arr[0].substring(arr[0].length()-2, arr[0].length());
						//设备whId
						String whId = arr[1];
						productFun = AppUtil.getSingleProductFunByWhId(context, whId);
						//保存、更新设备状态
						saveDeviceAction(context, whId, actionCode);
						//低电量、异常提醒
						if (MESSAGE_TYPE_701.equals(type)) {
							if (productFun != null) {
								msg = productFun.getFunName() + ":设备故障";
								showNotification(context, msg, true);
								//存储消息到本地数据库
								saveMessageToLoacl(context, whId, msg);
							}
						}else{
							if ("04".equals(actionCode)) {
								if (productFun != null) {
									msg = productFun.getFunName() + ":电量低";
									showNotification(context, msg, true);
								}
							}
						}
					}
				}
			}else if(MESSAGE_TYPE_900.equals(type)){
				if (!TextUtils.isEmpty(content)) {
					String[] arr = content.split(",");//拆分消息内容
					String msg = "";
					if (arr.length > 1) {
						//消息类型
//						String actionCode = arr[0].substring(arr[0].length()-1, arr[0].length());
						String actionCode = "";
						String msgContent = arr[0];
						String[] msgArr = msgContent.split(":");
						if (msgArr.length > 0) {
							actionCode = msgArr[1];
						}
						String deviceName = arr[1];
						msg = deviceName + ":" + actionCode;
						//存储消息到本地数据库
						saveMessageToLoacl(context, "00ff123456789", msg);
						showNotification(context, msg, true);
					}
				}
			}else if(MESSAGE_TYPE_1001.equals(type)){
				if (!TextUtils.isEmpty(content)) {
					String[] arr = content.split(",");//拆分消息内容
					if (arr.length > 1) {
						String power = "";
						String msgContent = arr[0];
						String whId = arr[1];
						ProductFun pf = AppUtil.getSingleProductFunByWhId(context, whId);
						ProductState ps = new ProductState();
						String[] msgArr = msgContent.split(":");
						if (msgArr.length > 0) {
							power = msgArr[1];
						}
						if (pf != null) {
							ps.setFunId(pf.getFunId());
							ps.setState(power.trim());
							List<ProductState> psList = new ArrayList<ProductState>();
							psList.add(ps);
							CommonMethod.updateProductStateList(context, psList);
						}
						Bundle data = new Bundle();
						data.putString("power", power.trim());
						Logger.debug(null, "power = "+power.trim());
						data.putString("whId", whId);
						if ("FF".equals(power)) {
							showNotification(context, pf.getFunName()+":电量低", true);
							//存储消息到本地数据库
							saveMessageToLoacl(context, whId, pf.getFunName()+":电量低");
						}
						BroadcastManager.sendBroadcast(BroadcastManager.MESSAGE_WARN_LOW_POWER, data);
					}
				}
			}else if(MESSAGE_TYPE_800.equals(type)){ 
				if (!TextUtils.isEmpty(content)) {
					String[] arr = content.split(":");//拆分消息内容
					if (arr.length > 0) {
						String msgContent = arr[0];
//						String whId = arr[1];
						if (!TextUtils.isEmpty(msgContent)) {
							Bundle data = new Bundle();
							data.putString("msg", msgContent);
							data.putString("whId", "00ff123456789");
							
							showNotification(context, msgContent, true);
							//存储消息到本地数据库
							saveMessageToLoacl(context, "00ff123456789", msgContent);
						}
					}
				}
			}else if(MESSAGE_TYPE_1002.equals(type)){
//				Logger.debug(null, "content = "+content);
				if (!TextUtils.isEmpty(content)) {
					String[] arr = content.split(",");//拆分消息内容
					if (arr.length > 0) {
						String msgContent = arr[0];
						String whId = arr[1];
						if (!TextUtils.isEmpty(msgContent)) {
							Bundle data = new Bundle();
							data.putString("msg", msgContent);
							data.putString("whId", whId);
							
							showNotification(context, msgContent, true);
							//存储消息到本地数据库
							saveMessageToLoacl(context, whId, msgContent);
							BroadcastManager.sendBroadcast(BroadcastManager.MESSAGE_WARN_ERROR, data);
						}
					}
				}
			}else if(MESSAGE_TYPE_2500.equals(type)){
				if (!TextUtils.isEmpty(content)) {
					
					showNotification(context, content, true);
					//存储消息到本地数据库
					saveMessageToLoacl(context, "00ff123456789", content);
				}
			}
		}
	}
	
	/**
	 * 保存消息到本地
	 * @param context
	 * @param whId
	 * @param msg
	 */
	private void saveMessageToLoacl(Context context,String whId, String msg){
		if (context == null || TextUtils.isEmpty(whId)) {
			Logger.debug(TAG, "param is null");
			return;
		}
		//存储消息到本地数据库
		CustomerMeassage meassage = new CustomerMeassage();
		cmDaoImpl = new CustomerMessageDaoImpl(context);
		meassage.setWhId(whId);
		meassage.setIsReaded(0);
		meassage.setTime(DateUtil.convertLongToStr1(System.currentTimeMillis()));
		meassage.setMessage(msg);
		cmDaoImpl.insert(meassage, true);
		BroadcastManager.sendBroadcast(
				BroadcastManager.MESSAGE_WARN_RECEIVED_ACTION, null);
	}

	/**
	 * 保存、更新设备状态（开、关等操作）
	 * @param context
	 * @param whId
	 * @param actionCode
	 */
	private void saveDeviceAction(Context context, String whId, String actionCode){
		if (context == null || TextUtils.isEmpty(whId)) {
			Logger.debug(TAG, "param is null");
			return;
		}
		
		ProductDoorContact doorContact = getProductDoorContactByWhId(whId, context);
		ProductDoorContactDaoImpl pdcDaoImpl = new ProductDoorContactDaoImpl(context);
		if (doorContact != null) {
			doorContact.setElectric(actionCode);
			pdcDaoImpl.update(doorContact);
		}else{
			doorContact = new ProductDoorContact();
			doorContact.setWhId(whId);
			doorContact.setElectric(actionCode);
			doorContact.setIsWarn(1);
			doorContact.setStatus("00");
			pdcDaoImpl.insert(doorContact, false);
		}
	}
	
	private void requestServerToUpdateProductRegister(Context context) {
		// 更新设备注册表
		UpdateProductRegisterTask cplTask = new UpdateProductRegisterTask(context);
		final ProductRegisterDaoImpl uprDao = new ProductRegisterDaoImpl(context);
		cplTask.addListener(new ITaskListener<ITask>() {
			@Override
			public void onStarted(ITask task, Object arg) {
				Logger.debug("JPush", "onStarted");
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				Logger.debug("JPush", "onCanceled");
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				Logger.debug("JPush", "onFail");
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				Logger.debug("JPush", "onSuccess");
				if (arg != null && arg.length > 0) {
					List<ProductRegister> prs = (List<ProductRegister>) arg[0];
					for (ProductRegister pr : prs) {
						try {
							Logger.debug("JPush", "onSuccess " + pr.getAddress485());
							uprDao.saveOrUpdate(pr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// 更新用户设备表
					requestServerToUpdateCustomerProduct(JxshApp.instance);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				Logger.debug("JPush", "onProcess");

			}
		});
		cplTask.start();

	}

	private void requestServerToUpdateCustomerProduct(Context context) {
		String _account = CommUtil.getCurrentLoginAccount();
		SharedDB.saveStrDB(_account, ControlDefine.KEY_CUSTOMER_PRODUCT_LIST, ControlDefine.DEFAULT_UPDATE_TIME);
		// 更新设备列表
		CustomerProductListTask cplTask = new CustomerProductListTask(context);
		cplTask.addListener(new ITaskListener<ITask>() {
			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				// JxshApp.closeLoading();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				// JxshApp.closeLoading();
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				/****** 设备信息更新 *********/
				if (arg != null && arg.length > 0) {
					List<CustomerProduct> customerProductList = (List<CustomerProduct>) arg[0];
					CommonMethod.updateCustomerProduct(JxshApp.instance, customerProductList);
				}

			}

			@Override
			public void onProcess(ITask task, Object[] arg) {

			}
		});
		cplTask.start();

	}

	/**
	 * 检测设备是否开启报警功能
	 * @param whId
	 * @param context
	 * @return
	 */
	private boolean isOpenWarn(String whId, Context context){
		boolean isOpen = true;
		ProductDoorContactDaoImpl pdcDaoImpl = new ProductDoorContactDaoImpl(context);
		List<ProductDoorContact> doorContacts = pdcDaoImpl.
				find(null, "whId=?", new String[]{whId}, null, null, null, null);
		if (doorContacts != null && doorContacts.size() > 0) {
			ProductDoorContact doorContact = doorContacts.get(0);
			if (doorContact.getIsWarn() == 1) {
				isOpen = true;
			}else{
				isOpen = false;
			}
		}
		return isOpen;
	}
	
	//获取报警设备消息存储对象
	public ProductDoorContact getProductDoorContactByWhId(String whId, Context context){
		ProductDoorContact doorContact = null;
		ProductDoorContactDaoImpl pdcDaoImpl = new ProductDoorContactDaoImpl(context);
		List<ProductDoorContact> doorContacts = pdcDaoImpl.
				find(null, "whId=?", new String[]{whId}, null, null, null, null);
		if (doorContacts != null && doorContacts.size() > 0) {
			doorContact = doorContacts.get(0);
		}
		return doorContact;
	}
	
	/**
	 * 报警通知栏
	 * @param context
	 * @param content
	 * @param isOutside 户外模式  or 居家模式
	 */
	@SuppressLint({ "NewApi", "SimpleDateFormat" })
	private void showNotification(Context context, String content , boolean isOutside){
		String _account = CommUtil.getCurrentLoginAccount();
		boolean isOpenNotice = false; //是否开启了分时断提醒
		boolean noShake = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_WARN_SHAKE, false);
		isOpenNotice = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_NOTICE_ON_OFF, true);
		boolean isNotice = true;//是否发送通知
		
		if (isOpenNotice) {
			MessageTimerDaoImpl mtdImpl = new MessageTimerDaoImpl(context);
			List<MessageTimer> list = mtdImpl.find();
			if (list != null && list.size() > 0) {
				Date date =new Date();
				SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");//只有时分
				String currentTime = sdf.format(date); 
				for (MessageTimer messageTimer : list) {
					String[] arr = messageTimer.getTimeRange().split("-");
					int l = largerTime(arr[0], currentTime);
					int r = largerTime(arr[1], currentTime);
					if ((l+r) == 0) {
						isNotice = true;
						break;
					}else{
						isNotice = false;
					}
				}
			}else{
				isNotice = true;
			}
		}else{
			Logger.debug(null, "消息提醒已关闭");
			return;
		}
		
		if (!isNotice) return;
		
		// 创建一个NotificationManager的引用   
		NotificationManager mNotifyMgr = 
		        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int mNotificationId = 001;
		// 创建一个NotificationCompat.Builder
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(context)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle("设备报警提示")
			    .setContentText(content)
			    .setWhen(System.currentTimeMillis())
			    .setAutoCancel(true);
		if (!isOutside) {//居家模式
			if (noShake) {//居家免打扰
				mBuilder.setVibrate(null);
				mBuilder.setSound(null);
			}else{
				mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
				mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.ring_tips));
			}
		}else{//户外模式
			mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.ring_warnning));
		}
		// 把Builder传递给NotificationManager 
		if (mNotifyMgr != null) {
			mNotifyMgr.notify(mNotificationId, mBuilder.build());
		}
	}
	
	private int largerTime(String t1, String t2) {
		Date date1, date2;
		DateFormat formart = new SimpleDateFormat("hh:mm");
		try {
			date1 = formart.parse(t1);
			date2 = formart.parse(t2);
			if (date1.compareTo(date2) < 0) {
				return -1;
			} else if(date1.compareTo(date2) == 0){
				return 0;
			}else{
				return 1;
				
			}
		} catch (ParseException e) {
			System.out.println("date init fail!");
			e.printStackTrace();
			return 0;
		}

	}
}
