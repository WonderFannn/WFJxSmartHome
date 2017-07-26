
package com.jinxin.jxsmarthome.ui.widget;


import java.io.File;
import java.text.DecimalFormat;

import xgzx.VeinUnlock.VeinLogin;

import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.datan.net.command.cb.DownLoadFileTask;
import com.jinxin.datan.net.command.cb.LoadAPKTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.notification.NoticeInfo;
import com.jinxin.record.FileUtil;

/**
 * 下载APK框
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class DownloadAPKDialog extends Dialog {

	private String url = null;//下载url地址
	private ProgressBar progressBar = null;

	private TextView textView_sum = null;

	private TextView textView_current = null;

	private boolean isRunning = true;
	private int size = 0;//包大小
	private String title = null;//title
	private File file = null;
	private Context context = null;
	private LoadAPKTask task = null;
	private NoticeInfo noticeInfo = null;
	private static final int UPDATE_UI = 0;
	private static final int RETRY = 1;
	
//	String path = FileManager.instance().getFilePath(FileManager.TYPE_APK)+FileManager.APK_NAME;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case UPDATE_UI:
			if (file != null) {
				progressBar.setProgress((int) file.length());
				setTextViewKM(textView_current, (int) file.length());
				}
			break;
			case RETRY:
				final UpdateMessageBox box = new UpdateMessageBox(
						context, "更新失败",
						"更新失败或已取消。",
						UpdateMessageBox.MB_OK|UpdateMessageBox.MB_CANCEL);
				box.setButtonText("重试", null);
				box.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						int ret = box.getResult();
						if (ret == UpdateMessageBox.MB_OK) {
//							EddhcApp.instance.downloadInstallPackage(
//									context, url,size,2);
							new DownloadAPKDialog(context, title, url, size).show();
						}else{
							if(context instanceof VeinLogin) {
								VeinLogin vein = (VeinLogin)context;
								vein.downloadCancelCallback();
							}
						}
					}
				});
				box.show();
				break;
			}
		}

	};
	/**
	 * 
	 * @param context
	 * @param title 名字
	 * @param url 下载地址
	 * @param size 包大小
	 */
	public DownloadAPKDialog(Context context, String title, String url,int size) {
		super(context, R.style.dialog);
		setContentView(CommDefines.getSkinManager().view(R.layout.download_apk_dialog_layout));
		this.context = context;
		this.title = title;
		this.url = url;
		this.size = size;
		this.init();
		updateInstallAPK();
		
	}
	private void init(){
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setMax(size);
		progressBar.setProgress(0);
		textView_sum = (TextView) findViewById(R.id.textView_sum);
		setTextViewKM(textView_sum, size);
		textView_current = (TextView) findViewById(R.id.textView_current);
		TextView textview_title = (TextView)findViewById(R.id.textview_title);
		textview_title.setText(title);
		this.url = url;
//		Button button_ok = (Button)findViewById(R.id.button_ok);
//		button_ok.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
		Button button_cancel = (Button)findViewById(R.id.button_cancel);
		button_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(task != null){
					task.cancel();

				}
			}
		});
	}
	public NoticeInfo getNoticeInfo() {
		return noticeInfo;
	}
	/**
	 * 更新状态
	 * @return
	 */
    public boolean isUpdate() {
		return this.isRunning;
	}

	public void setTextViewKM(TextView textView, int content) {
		float k = content / 1024.00f;
		float m = k / 1024.00f;
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("0.00");
		if (m >= 1) {// K
			textView.setText(df.format(m) + "M");
		} else {
			textView.setText(df.format(k) + "K");
		}
	}
	private void checkDownLoad() {

		new Thread() {
			public void run() {
				try {
					while (isRunning && progressBar.getMax() > progressBar.getProgress()) {
						handler.sendEmptyMessage(UPDATE_UI);
						Thread.sleep(200);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();
	}
	/**
	 * 创建任务栏显示内容
	 */
	private void creatNoticeInfo(){
		this.noticeInfo = new NoticeInfo();
		this.noticeInfo.setContent("正在努力为您更新...");
		this.noticeInfo.setFlags(Notification.FLAG_INSISTENT | Notification.FLAG_ONGOING_EVENT);
		this.noticeInfo.setIcon(R.drawable.ic_launcher);
		this.noticeInfo.setId(80219);
		this.noticeInfo.setTitle("Beone智慧家居更新程序");
		Intent _intent;
		try {
			_intent = new Intent(JxshApp.getContext(),Class.forName(this.context.getClass().getName()));// 加载类，如果直接通过类名，会在点击时重新加载页面，无法恢复最后页面状态。
			_intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent _endingIntent = PendingIntent.getActivity(this.context, 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
			this.noticeInfo.setPendingIntent(_endingIntent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.error("更新程序","更新程序创建跳转异常");
		} 
	}
	/**
	 * 版本更新
	 */
	private void updateInstallAPK() {
		this.creatNoticeInfo();
		final int _saveType = 0;
		final boolean _isHide = true;
		task = new LoadAPKTask(null,this.url);
		task.addListener(new ITaskListener<ITask>() {
			@Override
			public void onStarted(ITask task, Object arg) {
				// TODO Auto-generated method stub
				file = FileUtil.reBuildFile(CommDefines.getFileManager().getAPKPath());
				checkDownLoad();
				//发送消息到任务栏
				CommDefines.getNotificationManager().sendNotification(noticeInfo);
				// handler.post(new Runnable() {
				//
				// @Override
				// public void run() {
				// // TODO Auto-generated method stub
				// ((AnimationDrawable) imageViewAcl.getDrawable()).start();
				// }
				//
				// });
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				// TODO Auto-generated method stub
				CommDefines.getNotificationManager().closeNotice(noticeInfo);
				isRunning = false;
				dismiss();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				// TODO Auto-generated method stub
				CommDefines.getNotificationManager().closeNotice(noticeInfo);
				isRunning = false;
				if (arg != null && arg.length > 0)
					JxshApp.instance.showToast(context, arg[0].toString());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				progressBar.setMax(0);
				progressBar.setProgress(0);
				dismiss();

				handler.sendEmptyMessage(RETRY);

				//////////////////////////////////////

				
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				// TODO Auto-generated method stub
				CommDefines.getNotificationManager().closeNotice(noticeInfo);
				isRunning = false;
				// 安装
				if (arg != null && arg.length > 0) {
					String _apkPath = arg[0].toString();
					CommUtil.installAPK(context, _apkPath);
					CommUtil.kill(context);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				// TODO Auto-generated method stub
				
			}
		});
		task.start();
//		new Thread(){
//			
//		}
//			_task
//		}.start();
//		LockScreenApp.getDatanAgentHandler().post(_task);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		task.cancel();
		isRunning = false;
	}
}
	
