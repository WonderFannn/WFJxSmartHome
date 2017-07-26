package com.jinxin.jxsmarthome.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jinxin.db.impl.MessageTimerDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.MessageTimer;
import com.jinxin.jxsmarthome.ui.widget.ragebar.RangeBar;
import com.jinxin.jxsmarthome.ui.widget.ragebar.RangeBar.OnRangeBarChangeListener;

/**
 * 修改推送消息分时断接收
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AMendMessageReceiveActivity extends BaseActionBarActivity {

	private RangeBar rangeBar;
	private TextView tvTimeRange;
	private int leftTime = 0;
	private int rightTime = 24;
	private MessageTimerDaoImpl mtdImpl;
	private MessageTimer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_add, menu);
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("修改消息提醒时间");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.save_mode_btn:
			MessageTimer mt = new MessageTimer();
			mt.setTimeRange(tvTimeRange.getText().toString());
			mtdImpl.insert(mt,true);
			finish();
			break;
		}
		return false;
	}
	
	private void initView() {
		setContentView(R.layout.activity_message_receive_layout);
		rangeBar = (RangeBar) findViewById(R.id.range_bar);
		tvTimeRange = (TextView) findViewById(R.id.tv_time_range);

		leftTime = rangeBar.getLeftIndex();
		rightTime = rangeBar.getRightIndex();
		if (mTimer != null) {
			String timeRange = mTimer.getTimeRange();
			if (!TextUtils.isEmpty(timeRange)) {
				tvTimeRange.setText(timeRange);
				String[] arr = timeRange.split("-");
				if (arr[0].startsWith("0")) {
					leftTime = Integer.parseInt(arr[0].subSequence(1, 2).toString());
				}else{
					leftTime = Integer.parseInt(arr[0].subSequence(0, 2).toString());
				}
				
				if (arr[1].startsWith("0")) {
					rightTime = Integer.parseInt(arr[1].subSequence(1, 2).toString());
				}else{
					rightTime = Integer.parseInt(arr[1].subSequence(0, 2).toString());
				}
				rangeBar.setRangePinsByIndices(leftTime, rightTime);
			}
		}
		
		rangeBar.setOnRangeBarChangeListener(new OnRangeBarChangeListener() {
			
			@Override
			public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
					int rightPinIndex, String leftPinValue, String rightPinValue) {
				tvTimeRange.setText(formatIntToString(leftPinIndex)+ "-" + formatIntToString(rightPinIndex));
			}
		});
	}

	private void initData() {
		mtdImpl = new MessageTimerDaoImpl(AMendMessageReceiveActivity.this);
		mTimer = (MessageTimer) getIntent().getSerializableExtra("messageTimer");
	}
	
	private String formatIntToString(int time){
		String str = "";
		if (time < 10) {
			str = "0"+time + ":00";
		}else{
			str = time + ":00";
		}
		
		return str;
	}

	@Override
	public void uiHandlerData(Message msg) {
		super.uiHandlerData(msg);
	}
	
}
