package com.jinxin.jxsmarthome.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.db.impl.MessageTimerDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.MessageTimer;

/**
 * 消息通知分时断提醒
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class MessageNotifyActivity extends BaseActionBarActivity {

	private Context context;
	private ListView listView;
	private MessageTimerDaoImpl mtdImpl;
	private List<MessageTimer> timerList = null;
	private MessageTimerAdapter adapter;
	private TextView tvTips;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = MessageNotifyActivity.this;
		setContentView(R.layout.activity_message_timer_layout);
		initData();
		
		tvTips = (TextView) findViewById(R.id.tv_no_timer_tips);
		listView = (ListView) findViewById(R.id.lv_message_timer);
		adapter = new MessageTimerAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MessageNotifyActivity.this, AMendMessageReceiveActivity.class);
				Bundle data = new Bundle();
				data.putSerializable("messageTimer", timerList.get(position));
				intent.putExtras(data);
				startActivity(intent);
			}
		});
	}
	
	private void initData(){
		mtdImpl = new MessageTimerDaoImpl(MessageNotifyActivity.this);
		timerList = mtdImpl.find();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_timer, menu);
		
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("消息提醒");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.action_timer:
			startActivity(new Intent(MessageNotifyActivity.this, AddMessageReceiveActivity.class));
			break;
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		timerList = mtdImpl.find();
		if (timerList.size() > 0) {
			mUIHander.sendEmptyMessage(0);
			tvTips.setVisibility(View.GONE);
		}else{
			tvTips.setVisibility(View.VISIBLE);
		}
	}


	class MessageTimerAdapter extends BaseAdapter{
		public MessageTimerAdapter() {
		}

		@Override
		public int getCount() {
			return timerList != null ? timerList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return timerList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final int pos = position;
			final MessageTimer timer = timerList.get(position);
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_message_timer, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder=(ViewHolder) convertView.getTag();
			}
			
			holder.tvItem.setText(timer.getTimeRange());
			holder.btnDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					timerList.remove(pos);
					mtdImpl.delete(timer.getId());
					mUIHander.sendEmptyMessage(0);
				}
			});
			return convertView;
		}
		
		class ViewHolder{
			TextView tvItem;
			Button btnDelete;
			public ViewHolder(View view) {
				tvItem = (TextView) view.findViewById(R.id.tv_timer_item);
				btnDelete = (Button) view.findViewById(R.id.iv_delete_btn);
			}
		}
		
	}

	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}

}
