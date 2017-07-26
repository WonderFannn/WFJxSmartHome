package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.CustomerTimerListTask;
import com.jinxin.datan.net.command.DeleteCustomerTimerTask;
import com.jinxin.datan.net.command.TimerTaskOperationListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.db.impl.TimerTaskOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.AddTimerTaskActivity;
import com.jinxin.jxsmarthome.activity.AmendTimerTaskActivity;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CornExpression;
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

public class TimerFragment extends Fragment implements AdapterView.OnItemLongClickListener {
	private static final int OPERATE_BEGIN = 1000;
	private static final int OPERATE_SUCCESS = 1001;
	private static final int OPERATE_FAIL = 1002;
	private static final int requestCode = 100;
	private ListView mList;
	private List<CustomerTimer> mListData;
	private TimerAdapter mAdapter;

	private boolean isOffMode = false; // 是否离线模式

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case OPERATE_BEGIN:
				JxshApp.showLoading(getActivity(), "正在操作请稍后...");
				break;
			case OPERATE_FAIL:
				JxshApp.closeLoading();
				break;
			case OPERATE_SUCCESS:
				JxshApp.closeLoading();
//				mAdapter = new TimerAdapter(mListData, getActivity());
//				mList.setAdapter(mAdapter);
				mAdapter.updateState();
				mAdapter.notifyDataSetChanged();
			default:
				break;
			}

			return true;
		}
	});

	/** 更新模式的广播事件处理 */
	private BroadcastReceiver mUpdateBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastManager.ACTION_UPDATE_TASK_MESSAGE)) {
				// 更新模式
				getDataFromDb();
				mHandler.sendEmptyMessage(OPERATE_SUCCESS);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParam();
		
		((BaseActionBarActivity) getActivity()).openBoradcastReceiver(
				mUpdateBroadcastReceiver,
				BroadcastManager.ACTION_UPDATE_TASK_MESSAGE);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_timer, null);
		initView(view);
		return view;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final int p = position;
		final ListDialog _listDialog = new ListDialog(getActivity(),
				CommDefines.getSkinManager().stringArray(R.array.timer_menu));
		if (CommUtil.isSubaccount()) {
			JxshApp.showToast(getActivity(), "子账号不能进行模式修改操作");
			return false;
		}
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
					.string(R.string.li_xian_cao_zuo_tips));
			return false;
		}
		
		_listDialog.setOnItemClickListener(new ListDialogOnItemClickListener() {
			@Override
			public void onItemClick(Dialog dialog, View view, int id) {
				switch (id) {
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
			JxshApp.showToast(getActivity(), CommDefines.getSkinManager()
					.string(R.string.li_xian_cao_zuo_tips));
		} else {
			_listDialog.show();
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_timer:
			if (CommUtil.isSubaccount()) {
				JxshApp.showToast(getActivity(), "子账号不能进行模式修改操作");
				break;
			}
			if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
				JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
						.string(R.string.li_xian_cao_zuo_tips));
				break;
			}
			startActivity(new Intent(getActivity(),
					AddTimerTaskActivity.class));
			break;
		}
		return true;
	}

	/**
	 * 初始化参数
	 */
	private void initParam() {
		mListData = new ArrayList<CustomerTimer>();
	}

	/**
	 * 初始化视图
	 */
	private void initView(View view) {
		String account = CommUtil.getCurrentLoginAccount();
		if (account != null && account != "") {
			isOffMode = SharedDB.loadBooleanFromDB(account,
					ControlDefine.KEY_OFF_LINE_MODE, false);
		}

		mList = (ListView) view.findViewById(R.id.timer_list);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Logger.debug(null, "onItemClick");
				if (CommUtil.isSubaccount()) {
					JxshApp.showToast(getActivity(), "子账号不能进行模式修改操作");
					return;
				}
				if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
					JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
							.string(R.string.li_xian_cao_zuo_tips));
					return;
				}
				if(mAdapter.getDisableList().contains(position)) {
					return;
				}
				if (isOffMode) {
					JxshApp.showToast(
							getActivity(),
							CommDefines.getSkinManager().string(
									R.string.li_xian_cao_zuo_tips));
				} else {
					Intent intent = new Intent(getActivity(),
							AmendTimerTaskActivity.class);
					intent.putExtra("CustomerTimer", mListData.get(position));
					startActivityForResult(intent, requestCode);
				}
			}
		});
		mList.setOnItemLongClickListener(this);
		mAdapter = new TimerAdapter(mListData, getActivity());
		mList.setAdapter(mAdapter);
	}
	
	@Override
	public void onResume() {
		initData();
		super.onResume();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(OPERATE_BEGIN);
				CustomerTimerListTask ctlTask = new CustomerTimerListTask(getActivity());
				ctlTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
						Logger.debug("Yang", "task list");
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
						Logger.debug(null, "onCanceled...");
						mHandler.sendEmptyMessage(OPERATE_FAIL);
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						Logger.debug("Yang", "task onFail...");
						mHandler.sendEmptyMessage(OPERATE_FAIL);
					}
					
					@Override
					public void onSuccess(ITask task, Object[] arg) {
						//////////////
						if (arg != null && arg.length > 0) {
							List<CustomerTimer> ctList = (List<CustomerTimer>) arg[0];
							CommonMethod.updateCustomerTimerAll(getActivity(), ctList);
						}
						getDataFromDb();
						mHandler.sendEmptyMessage(OPERATE_SUCCESS);
						
						/*****************************************/
						//更新定时任务操作列表
						StringBuffer taskIds = null;
						CustomerTimerTaskDaoImpl _cttDaoImpl = new CustomerTimerTaskDaoImpl(getActivity());
						List<CustomerTimer> _ctList = _cttDaoImpl.find();
						if (_ctList != null) {
							taskIds = new StringBuffer();
							for (int i = 0; i < _ctList.size(); i++) {
								CustomerTimer _ct = _ctList.get(i);
								if (_ct != null) {
									if (i<_ctList.size() - 1) {
										taskIds.append(_ct.getTaskId()).append(",");
									}else{
										taskIds.append(_ct.getTaskId());
									}
								}
							}
						}
						TimerTaskOperationListTask ttoplTask = new 
								TimerTaskOperationListTask(
										null, taskIds.toString());
						ttoplTask.addListener(new ITaskListener<ITask>() {

							@Override
							public void onStarted(ITask task, Object arg) {
								Logger.debug("Yang", "Operation list");
							}

							@Override
							public void onCanceled(ITask task,
									Object arg) {
							}

							@Override
							public void onFail(ITask task, Object[] arg) {
								Logger.debug("Yang", "Operation onFail...");
							}

							@Override
							public void onSuccess(ITask task,
									Object[] arg) {
								
								if (arg != null&& arg.length > 0) {
									List<TimerTaskOperation> ttoList = (List<TimerTaskOperation>) arg[0];
									CommonMethod.updateTimerTaskOperationList(
											getActivity(), ttoList);
								}
								
							}

							@Override
							public void onProcess(ITask task,
									Object[] arg) {
								
							}
						});
						ttoplTask.start();
					}
					
					@Override
					public void onProcess(ITask task, Object[] arg) {
						
					}
				});
				ctlTask.start();
				
			}
		}).start();

	}

	/**
	 * 从数据库中获取数据
	 */
	private void getDataFromDb() {
		mListData.clear();

		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(this.getActivity());
		List<CustomerTimer> originalList = dao.find(null, "status!=?", 
				new String[]{String.valueOf(0)}, null, null, "updateTime desc", null);
		
		Map<Integer, CornExpression> map = new HashMap<Integer, CornExpression>();
		for (int i=0;i<originalList.size();i++) {
			CustomerTimer customerTimer = originalList.get(i);
			CornExpression cExpression = null;
			try {
				JSONObject _jb = new JSONObject(customerTimer.getCornExpression());
				cExpression = new CornExpression(
						_jb.getInt("type"), _jb.getString("period"),formatTime(_jb.getString("time")));
				map.put(i, cExpression);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		List<Map.Entry<Integer, CornExpression>> infoIds =
		    new ArrayList<Map.Entry<Integer, CornExpression>>(map.entrySet());
		
		Collections.sort(infoIds, new Comparator<Map.Entry<Integer, CornExpression>>() {   
		    public int compare(Map.Entry<Integer, CornExpression> o1, Map.Entry<Integer, CornExpression> o2) {      
		        int timeCompareRel = (o1.getValue().getTime()).compareTo(o2.getValue().getTime());
		        if(timeCompareRel == 0) {
		        	int TypeCompareRel = (Integer.valueOf(o1.getValue().getType())).compareTo(Integer.valueOf(o2.getValue().getType()));
		        	 if(TypeCompareRel == 0) {
		        		 int PeroidCompareRel = (o1.getValue().getPeriod()).compareTo(o2.getValue().getPeriod());
			        	 if(PeroidCompareRel == 0) {
			        		 return 1;
			        	 }
			        	 return PeroidCompareRel;
		        	 }
		        	 return TypeCompareRel;
		        }
		        return timeCompareRel;
		    }
		});
		
		for (int i = 0; i < infoIds.size(); i++) {
		    int index = infoIds.get(i).getKey();
		    mListData.add(originalList.get(index));
		}
	}
	
	/**
	 * 统一时间格式mm:ss
	 * @param val
	 * @return
	 */
	private String formatTime(String val) {
		String format = "00:00:00";
		if(val.length()>format.length())
			return val;
		String pre = format.substring(0, format.length() - val.length());
		return pre.concat(val);
	}

	/**
	 * 删除定时设置
	 * 
	 * @param id
	 *            listview的id
	 */
	private void doDeleteItem(final int id) {
		final MessageBox mb = new MessageBox(getActivity(), CommDefines
				.getSkinManager().string(R.string.delete), CommDefines
				.getSkinManager().string(R.string.shi_fou_shan_chu_timer),
				MessageBox.MB_OK | MessageBox.MB_CANCEL);
		mb.setButtonText("确定", null);
		mb.show();
		mb.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				switch (mb.getResult()) {
				case MessageBox.MB_OK:
					final CustomerTimer timer = (CustomerTimer) mList
							.getAdapter().getItem(id);
					DeleteCustomerTimerTask dctTask = new DeleteCustomerTimerTask(
							getActivity(), timer.getTaskId());
					dctTask.addListener(new ITaskListener<ITask>() {

						@Override
						public void onStarted(ITask task, Object arg) {
							JxshApp.showLoading(
									getActivity(),
									CommDefines.getSkinManager().string(
											R.string.qing_qiu_chu_li_zhong));
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

	/**
	 * 删除记录并更新视图显示
	 */
	private void removeItemFromListView(CustomerTimer timer) {
		// 删除同步数据，删除本地数据库数据
		if (removeItemFromCouldAndDatabase(timer)) {
			initData();
		}
		// 发送取消定时任务的广播
		TimerManager.sendTimerCancelBroadcast(getActivity(), timer);
	}

	/**
	 * 同步并删除本地数据库中的数据
	 * 
	 * @param timer
	 *            待删除的对象
	 * @return 是否删除成功
	 */
	private boolean removeItemFromCouldAndDatabase(CustomerTimer timer) {
		// 同步数据
		if (timer == null) {
			return false;
		}

		// 删除本地数据库中的数据
		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(
				getActivity());
		timer.setStatus(0);
		dao.update(timer);
		TimerTaskOperationDaoImpl ttoDao = new TimerTaskOperationDaoImpl(
				getActivity());
		List<TimerTaskOperation> ttList = ttoDao.find(null, "taskId=?",
				new String[] { Integer.toString(timer.getTaskId()) }, null,
				null, null, null);
		if (ttList != null) {
			for (TimerTaskOperation _tto : ttList) {
				if (_tto != null) {
					ttoDao.delete(_tto.getId());
				}
			}
		}

		// 发送取消定时器的广播
		TimerManager.cancelTimeTask(getActivity(), timer.getTaskId());
		return true;
	}
}
