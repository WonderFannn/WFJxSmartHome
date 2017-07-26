package com.jinxin.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 任务栏图标信息
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class NoticeInfo {

	private String title = null;
	private boolean isSound = false;// 是否有声音
	private boolean isShake = false;// 是否有震动
	private int id = 0000;// 消息类型
	private int icon = -1;// 图标索引
	private int flags = Notification.FLAG_NO_CLEAR;// 设置notification的位置类型（默认为FLAG_NO_CLEAR）
	private PendingIntent pendingIntent = null;//点击事件
	private String content = null;//消息内容
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}
/**
 * 设置点击事件
 * @param context
 * @param intent
 * PendingIntent.getActivity(Context context, int requestCode, Intent intent, int flags)
 */
	public void setPendingIntent(PendingIntent pendingIntent) {
		this.pendingIntent = pendingIntent;
	}

	/**
	 * 获得notification的位置类型
	 * 
	 * @return
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * 设置notification的位置类型
	 * 
	 * @param flags
	 *            设置flag位 // FLAG_AUTO_CANCEL 该通知能被状态栏的清除按钮给清除掉 // FLAG_NO_CLEAR 该通知能被状态栏的清除按钮给清除掉 // FLAG_ONGOING_EVENT 通知放置在正在运行 //
	 *            FLAG_INSISTENT 是否一直进行，比如音乐一直播放，知道用户响应 // 以上的效果常量可以叠加,即通过 //notification.defaults =DEFAULT_SOUND|DEFAULT_VIBRATE;
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isSound() {
		return isSound;
	}

	public void setSound(boolean isSound) {
		this.isSound = isSound;
	}

	public boolean isShake() {
		return isShake;
	}

	public void setShake(boolean isShake) {
		this.isShake = isShake;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
