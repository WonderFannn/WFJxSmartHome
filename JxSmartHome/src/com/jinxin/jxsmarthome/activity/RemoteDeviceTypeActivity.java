package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;

import com.jinxin.datan.net.command.CodeLibraryTask;
import com.jinxin.datan.net.command.CustomerMatchCodeTask;
import com.jinxin.datan.net.command.RemoteDeviceTypeListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.RemoteDeviceType;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.RemoteDeviceTypeAdapter;
import com.jinxin.jxsmarthome.ui.popupwindow.ListPopupWindow2;

/**
 * 遥控设备类型选择
 * @author YangJijun
 * @company 金鑫智慧
 */
@SuppressLint("NewApi")
public class RemoteDeviceTypeActivity extends BaseActionBarActivity {

	private GridView gridView;
	private LinearLayout linerarlayout = null;
	private RemoteDeviceTypeAdapter adapter = null;
	private Context context = null;
	
	private RemoteDeviceTypeListTask deviceTypeListTask = null;
	private List<RemoteDeviceType> deviceTypeList = null;
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_my_remote, menu);
		
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("选择家电类型");
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		initData();
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_remote_device_select);
		this.gridView = (GridView) findViewById(R.id.gv_brand);
		linerarlayout = (LinearLayout) findViewById(R.id.linerarlayout);
		
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(RemoteDeviceTypeActivity.this, RemoteBrandsTypeActivity.class);
				intent.putExtra("deviceId", deviceTypeList.get(position).getId());
				startActivity(intent);
			}
		});
	}

	private void initData() {
		deviceTypeList = new ArrayList<RemoteDeviceType>();
		getDataFromInternet();
	}

	/**
	 * 网络获取数据
	 */
	private void getDataFromInternet() {
		deviceTypeListTask = new RemoteDeviceTypeListTask(RemoteDeviceTypeActivity.this);
		deviceTypeListTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if(arg != null && arg.length > 0){
					deviceTypeList = (List<RemoteDeviceType>) arg[0];
					mUIHander.sendEmptyMessage(0);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		deviceTypeListTask.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.select_remote_board:
			showSlideWindow();
			break;
		}
		return false;
	}

	
	private void showSlideWindow(){
		int height = getWindowManager().getDefaultDisplay().getHeight();
		int dialogWidth = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.7);
		int dialogHeight = linerarlayout.getHeight() + getActionBar().getHeight();
		ListPopupWindow2 mPopupWindow = new ListPopupWindow2(context, linerarlayout, dialogWidth,dialogHeight);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
//				startActivity(new Intent(RemoteDeviceTypeActivity.this, RemoteDeviceDebugActivity.class));
			}
		});
		mPopupWindow.show(0,height - dialogHeight);
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		super.uiHandlerData(msg);
		switch (msg.what) {
		case 0:
			if (adapter == null) {
				adapter = new RemoteDeviceTypeAdapter(RemoteDeviceTypeActivity.this, deviceTypeList);
				gridView.setAdapter(adapter);
			}else{
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}
	
}
