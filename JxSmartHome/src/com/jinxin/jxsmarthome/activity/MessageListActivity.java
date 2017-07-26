package com.jinxin.jxsmarthome.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jinxin.db.impl.CustomerMessageDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.entity.CustomerMeassage;
import com.jinxin.jxsmarthome.ui.adapter.MessageListAdapter;
import com.jinxin.widget.PullToRefresh.PullToRefreshBase;
import com.jinxin.widget.PullToRefresh.PullToRefreshListView;
import com.jinxin.widget.PullToRefresh.PullToRefreshBase.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

/**
 * 消息列表界面
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class MessageListActivity extends BaseActionBarActivity {

	private Context context;
	private ListView listView;
	private PullToRefreshListView mPullListView;
	private MessageListAdapter adapter = null;
	private CustomerMessageDaoImpl cmDaoImpl = null;
	private List<CustomerMeassage> messageList = null;
	
	boolean hasMoreLocalData = false;//是否还有本地缓存数据
	private int startPos = 0;//开始查找本地数据库中的位置
	private int currentPos = 20;//当前查找位置
	
	private BroadcastReceiver WarnMessageBroadcast = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastManager.MESSAGE_WARN_RECEIVED_ACTION)) {
				refreshData();
                mUIHander.sendEmptyMessage(0);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_layout);
		initData();
		initView();
	}

	private void initData() {
		context = this;
		messageList = new ArrayList<CustomerMeassage>();
		cmDaoImpl = new CustomerMessageDaoImpl(context);
		getMessageFromDB();
		this.registerReceiver(WarnMessageBroadcast, 
				new IntentFilter(BroadcastManager.MESSAGE_WARN_RECEIVED_ACTION));
//		messageList = cmDaoImpl.find(null, null, null, null, null, "time DESC", null);
	}

	private void initView() {
		this.mPullListView = (PullToRefreshListView) findViewById(R.id.lv_message_list);
		mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(true);
        mPullListView.setPullRefreshEnabled(true);
		listView = mPullListView.getRefreshableView();
		adapter = new MessageListAdapter(context, messageList);
		listView.setAdapter(adapter);
		
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            	refreshData();
                mUIHander.sendEmptyMessage(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	if (hasMoreLocalData) {
            		getMessageFromDB();
                    mUIHander.sendEmptyMessage(0);
				}else{
                    mUIHander.sendEmptyMessage(0);
				}
            }
        });
	}
	
	/**
	 * 获取本地缓存数据
	 */
	private void getMessageFromDB(){
		List<CustomerMeassage> tempList = cmDaoImpl.find(null, null, null, null, null, 
				"time DESC", Integer.toString(startPos)+","+Integer.toString(currentPos));
		if (tempList.size() > 0) {
			hasMoreLocalData = true;
			this.startPos = currentPos;
			this.currentPos += currentPos;
		}else{
			hasMoreLocalData = false;
		}
		messageList.addAll(tempList);
	}
	
	//下拉刷新 获取数据
	private void refreshData(){
		startPos = 0;
		currentPos = 20;
		messageList.clear();
		List<CustomerMeassage> tempList = cmDaoImpl.find(null, null, null, null, null, 
				"time DESC", Integer.toString(startPos)+","+Integer.toString(currentPos));
		if (tempList.size() > 0) {
			hasMoreLocalData = true;
			this.startPos = currentPos;
			this.currentPos += currentPos;
		}else{
			hasMoreLocalData = false;
		}
		messageList.addAll(tempList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("消息");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			adapter.notifyDataSetChanged();
            mPullListView.onPullDownRefreshComplete();
            mPullListView.onPullUpRefreshComplete();
            mPullListView.setHasMoreData(hasMoreLocalData);
            setLastUpdateTime();
			break;
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(WarnMessageBroadcast);
	}

	private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullListView.setLastUpdatedLabel(text);
    }
	
	@SuppressLint("SimpleDateFormat")
	private String formatDateTime(long time) {
		SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        if (0 == time) {
            return "";
        }
        return mDateFormat.format(new Date(time));
    }
	
}
