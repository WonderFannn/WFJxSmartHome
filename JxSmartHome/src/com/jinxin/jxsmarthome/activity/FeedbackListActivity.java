package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jinxin.datan.net.command.FeedbackListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.FeedbackDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor.AsyncTask;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.Feedback;
import com.jinxin.jxsmarthome.ui.adapter.FeedbackHisAdapter;
import com.zhy.imageloader.BetterSpinner;

public class FeedbackListActivity extends BaseActionBarActivity {
	
	private BetterSpinner spinner1 = null;
	private ListView feedbackLv = null;
	private FeedbackHisAdapter fhAdapter = null;
	private int msgType = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_feedback_add, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void initView() {
		setContentView(R.layout.activity_feedback_list);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.title_feedback));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
        spinner1 = (BetterSpinner)findViewById(R.id.spinner1);
		feedbackLv = (ListView)findViewById(R.id.feedbackLv);
		
		String[] list = getResources().getStringArray(R.array.feedback_type);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, list);
		spinner1.setAdapter(adapter);
		spinner1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				msgType = position + 1;
				loadDataByType(msgType);
				System.out.println("onItemClick");
			}
		});
		spinner1.setSelection(0);
		
		loadDataByType(msgType);
		
		feedbackLv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Feedback fb = (Feedback)fhAdapter.getItem(position);
				Intent intent = new Intent(FeedbackListActivity.this,FeedbackInfoActivity.class);
				intent.putExtra("feedbackId", fb.getMessageId());
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		FeedbackListTask feedbackTask = new FeedbackListTask(null);
		feedbackTask.addListener(new ITaskListener<ITask>() {

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
				if(arg != null && arg.length > 0) {
					List<Feedback> list = new ArrayList<Feedback>();
					list.addAll((List<Feedback>)arg[0]);
					CommonMethod.updateFeedback(getApplicationContext(),list);
					
					if (list.size() > 0) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								loadDataByType(msgType);
							}
						});
					}
				}
			}
			
			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}

		});
		feedbackTask.start();
		super.onResume();
	}
	
	private void loadDataByType(final int type) {
		AsyncExecutor executor = new AsyncExecutor();
		executor.execute(new AsyncTask<List<Feedback>>() {

			@Override
			public List<Feedback> doInBackground() {
				FeedbackDaoImpl daoImpl = new FeedbackDaoImpl(getApplicationContext());
				List<Feedback> list = daoImpl.find(null,"messageType = ?",new String[]{String.valueOf(type)},null,null,"creTime DESC",null);
				return list;
			}
			
			@Override
			public void onPostExecute(List<Feedback> data) {
				if (fhAdapter == null) {
					fhAdapter = new FeedbackHisAdapter(FeedbackListActivity.this, data);
					feedbackLv.setAdapter(fhAdapter);
				} else {
					fhAdapter.setList(data);
					fhAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_add:
			Intent intent = new Intent(FeedbackListActivity.this,AddFeedbackActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return true;
	}
	
}
