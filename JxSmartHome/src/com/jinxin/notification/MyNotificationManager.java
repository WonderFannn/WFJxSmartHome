package com.jinxin.notification;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

/**
 * 任务栏图标管理
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class MyNotificationManager {
	private volatile static MyNotificationManager myNotificationManager;
	/**
	 * 用于存储Notification组合
	 */
	private ArrayList<NoticeInfo> array_notification;
	private Context context;

	private MyNotificationManager(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * @param context
	 *            (会转成APP context)
	 * @return
	 */
	public static final MyNotificationManager instance(Context context) {
		if (myNotificationManager == null)
			synchronized (MyNotificationManager.class) {
				if (myNotificationManager == null) {
					myNotificationManager = new MyNotificationManager(context.getApplicationContext());
				}
			}
		return myNotificationManager;
	}

	public static void clean() {
		if (myNotificationManager != null)
			myNotificationManager.closeNotice();
		myNotificationManager = null;
	}

	/**
	 * 送出一个任务栏消息（存在只更新）
	 * 
	 * @param noticeInfo
	 */
	public void sendNotification(NoticeInfo noticeInfo) {
		if (noticeInfo == null)
			return;

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// closeNotice();
		if (array_notification == null)
			array_notification = new ArrayList<NoticeInfo>();

		Notification nf = new Notification();
		nf.icon = noticeInfo.getIcon();// 消息图标
		nf.when = System.currentTimeMillis();
		// n.defaults = Notification.DEFAULT_SOUND
		// | Notification.DEFAULT_VIBRATE;
		nf.sound = null;
		nf.vibrate = null;
		nf.tickerText = "";
		if (noticeInfo.getTitle() != null) {
			nf.tickerText = noticeInfo.getTitle();// 消息标题
		}
		if (noticeInfo.isSound()) {// 声音提醒
			nf.defaults |= Notification.DEFAULT_SOUND;
			nf.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}
		if (noticeInfo.isShake()) {// 震动
			nf.defaults |= Notification.DEFAULT_VIBRATE;
			nf.vibrate = new long[] { 300, 500, 700, 900 };
		}

		// 设置flag位
		// FLAG_AUTO_CANCEL 该通知能被状态栏的清除按钮给清除掉
		// FLAG_NO_CLEAR 该通知能被状态栏的清除按钮给清除掉
		// FLAG_ONGOING_EVENT 通知放置在正在运行
		// FLAG_INSISTENT 是否一直进行，比如音乐一直播放，知道用户响应
		nf.flags = noticeInfo.getFlags();

		// Intent it = new Intent(context, MessageAlertDialog.class);
		// it.putExtra("ID", info.getId());
		// it.putExtra("title", info.getTitle());
		// PendingIntent pi = PendingIntent.getActivity(context, i, it,
		// PendingIntent.FLAG_CANCEL_CURRENT);
		PendingIntent pi = noticeInfo.getPendingIntent();
		// 设置事件信息，显示在抽屉拉开的里面
		nf.setLatestEventInfo(context, noticeInfo.getTitle(), noticeInfo.getContent(), pi);

		// 发出通知
		nm.notify(noticeInfo.getId(), nf);
		//存在则不新增
		if(!this.isExist(noticeInfo)){
			array_notification.add(noticeInfo);
		}

	}
	/**
	 * 该消息是否已存在
	 * @param noticeInfo
	 * @return
	 */
	private boolean isExist(NoticeInfo noticeInfo){
		if(this.array_notification == null)return false;
		for(NoticeInfo info : this.array_notification){
			if(info != null){
				if(info.getId() == noticeInfo.getId()){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 关闭指定提醒
	 */
	public void closeNotice(NoticeInfo info) {
		if (info == null || (array_notification == null || array_notification.size() <= 0))
			return;
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(info.getId());
		array_notification.remove(info);
	}

	/**
	 * 关闭所有提醒
	 */
	public void closeNotice() {
		if (array_notification == null || array_notification.size() <= 0)
			return;
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		for (int i = 0; i < array_notification.size(); i++) {
			NoticeInfo _info = array_notification.get(i);
			if (_info != null) {
				nm.cancel(_info.getId());
				array_notification.remove(_info);
			}
		}
	}
}
