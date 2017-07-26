package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jinxin.datan.net.command.InfraredTranspondTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.CustomContorllerGridAdapter;
import com.jinxin.jxsmarthome.ui.adapter.InfraredControlGridAdapter;
import com.jinxin.jxsmarthome.ui.adapter.InfraredTransportListViewAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.R.menu;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class InfraredTranspondActivity extends BaseActionBarActivity implements OnItemClickListener  {

	
	private GridView mGrideView;
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private Context mContext;
	private ListView listView;
	private InfraredTransportListViewAdapter listViewAdapter;
	private InfraredControlGridAdapter gridAdapter;
	private View leftView;
	private SlidingMenu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
		
	}
	
	private void initData() {
		mContext=this;		
		wHproductUnfrareds=new ArrayList<>();
		_vo=new ArrayList<>();
		Intent intent=getIntent();
		productFun=(ProductFun) intent.getSerializableExtra("productFun");
		funDetail=(FunDetail) intent.getSerializableExtra("funDetail");
		if (productFun!=null) {
			doUpgradeTask();
		}else {
			JxshApp.showToast(this, "productFun为空");
		}
		
	}
	

	private void initView(){
		setActionbar();
		setContentView(R.layout.activity_infrared_transpond);
		mGrideView = (GridView)findViewById(R.id.controller_custom_grid);
//		leftView=LayoutInflater.from(this).inflate(R.layout.infrared_transport_left_item, null);
//		setSlidingmenu(leftView);
		gridAdapter = new InfraredControlGridAdapter(this,wHproductUnfrareds,productFun,funDetail);
		mGrideView.setAdapter(gridAdapter);
	}
	
	private void setSlidingmenu(View leftView) {
		listView=(ListView) leftView.findViewById(R.id.activity_infrared_transpond_lv);
		menu = new SlidingMenu(this);  
        menu.setMode(SlidingMenu.LEFT);  
        // 设置触摸屏幕的模式  
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBackgroundColor(getResources().getColor(R.color.bg));
        // 设置滑动菜单视图的宽度  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
        // 设置渐入渐出效果的值  
        menu.setFadeDegree(0.35f);  
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);  
        //为侧滑菜单设置布局  
        menu.setMenu(leftView); 
        
	}
	
	private void setActionbar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(this.getString(R.string.infrared_control));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.infrared_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return false;
	}
	
	private List<WHproductUnfrared> _vo;
	private List<WHproductUnfrared> wHproductUnfrareds;
	private InfraredTranspondTask upgradeTask;
	private void doUpgradeTask() {
		this.upgradeTask = new InfraredTranspondTask(this,productFun.getFunId());
		this.upgradeTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showToast(mContext, "数据获取中...");
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				JxshApp.showToast(mContext, "取消");
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.showToast(mContext, "暂无数据");
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				wHproductUnfrareds.addAll((List<WHproductUnfrared> )arg[0]);
				JxshApp.showToast(mContext, "获取成功");
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				JxshApp.showToast(mContext, "进度");
			}
			
		});
		this.upgradeTask.start();
	}
	
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (wHproductUnfrareds.size()>0) {
					gridAdapter.notifyDataSetChanged();
					/*listViewAdapter=new InfraredTransportListViewAdapter(mContext,_vo,productFun,funDetail);
//					setSlidingmenu(leftView);
					listView.setAdapter(listViewAdapter);
					listView.setOnItemClickListener(InfraredTranspondActivity.this);
					menu.showMenu();*/
				}
				break;
			default:
				break;
			}
			
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
//		menu.clo
		System.out.println("==aaaaaaaaaa=="+_vo.get(arg2).toString());
		wHproductUnfrareds.add(_vo.get(arg2));
		gridAdapter.notifyDataSetChanged();
//		menu.toggle();
	}
	
}
