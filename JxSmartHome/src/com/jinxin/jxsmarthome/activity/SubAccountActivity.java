package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jinxin.datan.net.command.SubAccountListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.Customer;

public class SubAccountActivity extends BaseActionBarActivity {

	private ListView listView = null;
	private List<Customer> customerList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	private Handler uiHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
	        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
	        for(int i=0;i<customerList.size();i++)
	        {
	        	HashMap<String, Object> map = new HashMap<String, Object>();
	        	map.put("accountStatus", customerList.get(i).getStatus() == 1?R.drawable.ic_sub_account_enable:R.drawable.ic_sub_account_disable);
				map.put("customerId", customerList.get(i).getCustomerId());
				map.put("customerName", customerList.get(i).getCustomerName());
	        	lstImageItem.add(map);
	        }
	        
	        SimpleAdapter saImageItems = new SimpleAdapter(SubAccountActivity.this, 
	        		                                    lstImageItem,
	        		                                    R.layout.item_sub_account,
	        		                                    new String[] {"accountStatus","customerId","customerName"}, 
	        		                                    new int[] {R.id.ItemImageStatus,R.id.textViewCustomerId,R.id.textViewCustomerName});
	        listView.setAdapter(saImageItems);
		};
	};

	private void initView() {
		setContentView(R.layout.activity_sub_account);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.title_sub_account));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	private void initData() {
		SubAccountListTask sTask = new SubAccountListTask(null);
		sTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null
						&& arg.length > 0) {
					customerList = (List<Customer>) arg[0];
					if (customerList.size() > 0) {
						uiHandler.sendEmptyMessage(0);
					}
				}
				
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
			
		});
		sTask.start();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return true;
	}
}
