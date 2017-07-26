package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.jinxin.datan.net.command.DeleteCustomerTimerTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.db.impl.TimerTaskOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.entity.TimerTaskOperation;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.service.TimerManager;
import com.jinxin.jxsmarthome.ui.adapter.TimerAdapter;
import com.jinxin.jxsmarthome.ui.dialog.ListDialog;
import com.jinxin.jxsmarthome.ui.dialog.ListDialogOnItemClickListener;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 定时界面
 * @author TangLong
 * @company 金鑫智慧
 */
@SuppressLint("SimpleDateFormat")
public class TimerActivity extends BaseActivity implements AdapterView.OnItemLongClickListener {
	private static final int OPERATE_BEGIN = 1000;
	private static final int OPERATE_SUCCESS = 1001;
	private static final int OPERATE_FAIL = 1002;
	private static final int requestCode = 100;
	private ListView mList;
	private List<CustomerTimer> mListData;
	private TimerAdapter mAdapter;
	private ImageView addBtn;
	
	private boolean isOffMode = false;//是否离线模式
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParam();
		initView();
		initData();
		this.openBoradcastReceiver(mUpdateBroadcastReceiver,
				BroadcastManager.ACTION_UPDATE_TASK_MESSAGE);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		super.onActivityResult(arg0, arg1, intent);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final int p = position;
		final ListDialog _listDialog = new ListDialog(TimerActivity.this,
				CommDefines.getSkinManager().stringArray(
						R.array.timer_menu));
		
		_listDialog.setOnItemClickListener(new ListDialogOnItemClickListener() {
			@Override
			public void onItemClick(Dialog dialog, View view, int id) {
				switch(id) {
				case 0:
					doDeleteItem(p);
					break;
				case 1:
					break;
				}
				_listDialog.dismiss();
			}
		});
		if (isOffMode) {
			JxshApp.showToast(TimerActivity.this,CommDefines.getSkinManager().
					string(R.string.li_xian_cao_zuo_tips));
		}else{
			_listDialog.show();
		}
		return true;
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case OPERATE_BEGIN:
			JxshApp.showLoading(TimerActivity.this, "正在操作请稍后...");
			break;
		case OPERATE_FAIL :
			JxshApp.closeLoading();
			break;
		case OPERATE_SUCCESS :
			JxshApp.closeLoading();
			mAdapter = new TimerAdapter(mListData, this);
			mList.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
	/**
	 * 更新模式的广播事件处理
	 */
	private BroadcastReceiver mUpdateBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastManager.ACTION_UPDATE_TASK_MESSAGE)) {
				// 更新模式
				getDataFromDb();
				mUIHander.sendEmptyMessage(OPERATE_SUCCESS);
			}
		}
	};
	/**
	 * 初始化参数
	 */
	private void initParam() {
		mListData = new ArrayList<CustomerTimer>();
	}
	
	/**
	 * 初始化视图
	 */
	private void initView() {
		setView(R.layout.activity_timer);
		setTitle(getResources().getString(R.string.timer_title));
		
		String account = CommUtil.getCurrentLoginAccount();
		if (account!=null && account!= "") {
			isOffMode = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_OFF_LINE_MODE, false);
		}
		setOnBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mList = (ListView) findViewById(R.id.timer_list);
		addBtn = (ImageView) findViewById(R.id.timer_add);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Logger.debug(null, "onItemClick");
				if (isOffMode) {
					JxshApp.showToast(TimerActivity.this,CommDefines.getSkinManager().
							string(R.string.li_xian_cao_zuo_tips));
				}else{
					Intent intent = new Intent(TimerActivity.this, AmendTimerTaskActivity.class);
					intent.putExtra("CustomerTimer", mListData.get(position));
					startActivityForResult(intent, requestCode);
				}
			}
		});
		addBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isOffMode) {
					JxshApp.showToast(TimerActivity.this,CommDefines.getSkinManager().
							string(R.string.li_xian_cao_zuo_tips));
				}else{
					startActivity(new Intent(TimerActivity.this,AddTimerTaskActivity.class));
				}
			}
		});
		mList.setOnItemLongClickListener(this);
		mAdapter = new TimerAdapter(mListData, this);
		mList.setAdapter(mAdapter);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mUIHander.sendEmptyMessage(OPERATE_BEGIN);
				getDataFromDb();
			}
		}).start();
		
	}
	
	/**
	 * 从数据库中获取数据
	 */
	private void getDataFromDb() {
		mListData.clear();
		
		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(this);
		mListData = dao.find(null, "status!=?", 
				new String[]{String.valueOf(0)}, null, null, "updateTime desc", null);
		mUIHander.sendEmptyMessage(OPERATE_SUCCESS);
	}
	
	/**
	 * 删除定时设置
	 * @param id	listview的id
	 */
	private void doDeleteItem(final int id) {
		final MessageBox mb = new MessageBox(this,
				CommDefines.getSkinManager().string(R.string.delete),
				CommDefines.getSkinManager().string(R.string.shi_fou_shan_chu_timer),
				MessageBox.MB_OK | MessageBox.MB_CANCEL);
		mb.setButtonText("确定", null);
		mb.show();
		mb.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				switch (mb.getResult()) {
				case MessageBox.MB_OK:
					final CustomerTimer timer = (CustomerTimer) mList.getAdapter().getItem(id);
					DeleteCustomerTimerTask dctTask = new DeleteCustomerTimerTask(
							TimerActivity.this, timer.getTaskId());
					dctTask.addListener(new ITaskListener<ITask>() {

						@Override
						public void onStarted(ITask task, Object arg) {
							JxshApp.showLoading(
									TimerActivity.this,
									CommDefines
											.getSkinManager()
											.string(R.string.qing_qiu_chu_li_zhong));
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
						public void onSuccess(ITask task, Object[] arg) {
							removeItemFromListView(timer);
						}

						@Override
						public void onProcess(ITask task, Object[] arg) {
							
						}
					});
					dctTask.start();
					break;
				}
			}
		});
	}
	
	/** 删除记录并更新视图显示  */
	private void removeItemFromListView(CustomerTimer timer) {
		// 删除同步数据，删除本地数据库数据
		if(removeItemFromCouldAndDatabase(timer)) {
			initData();
		}
		// 发送取消定时任务的广播
		TimerManager.sendTimerCancelBroadcast(this, timer);
	}
	
	/**
	 * 同步并删除本地数据库中的数据
	 * @param timer		待删除的对象
	 * @return			是否删除成功
	 */
	private boolean removeItemFromCouldAndDatabase(CustomerTimer timer) {
		// 同步数据
		if (timer == null) {
			return false;
		}
		
		// 删除本地数据库中的数据
		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(this);
		timer.setStatus(0);
		dao.update(timer);
		TimerTaskOperationDaoImpl ttoDao = new TimerTaskOperationDaoImpl(this);
		List<TimerTaskOperation> ttList = ttoDao.find(null, "taskId=?",
				new String[]{Integer.toString(timer.getTaskId())}, null, null, null, null);
		if (ttList != null) {
			for (TimerTaskOperation _tto : ttList) {
				if (_tto != null) {
					ttoDao.delete(_tto.getId());
				}
			}
		}
		
		// 发送取消定时器的广播
		TimerManager.cancelTimeTask(this, timer.getTaskId());
		return true;
	}

}
