package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.ui.adapter.InfraredActivityAdapter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class InfraredControlActivity extends BaseActionBarActivity implements OnItemClickListener {

	private GridView gridView;
	private InfraredActivityAdapter adapter;
	/**
	 * 产品类
	 */
	private ProductFun productFun = null;
	/**
	 * 产品详情类
	 */
	private FunDetail funDetail = null;
	private String[] strings;
	private List<Integer> ids;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
		
	}

	private void initData() {
		ids=new ArrayList<>();
		ids.add(R.drawable.infrared_control_gridview_tv);
		ids.add(R.drawable.infrared_control_gridview_aircondition);
		ids.add(R.drawable.infrared_control_gridview_mutil_function);
		strings=getResources().getStringArray(R.array.infrared_control_gridview);
		adapter=new InfraredActivityAdapter(this, strings,ids);
	}
	
	private void initView(){
		
		setActionbar();
		setSlidingMenu();
		setContentView(R.layout.activity_infrared_control);
		gridView=(GridView) findViewById(R.id.activity_infrared_control_gridview);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		//
	}
	
	private void setActionbar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(this.getString(R.string.infrared_control));
	}
	
	private void setSlidingMenu() {
		//设置SlidingMenu
		SlidingMenu menu = new SlidingMenu(this);  
        menu.setMode(SlidingMenu.RIGHT);  
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
        menu.setShadowWidthRes(R.dimen.slidingmenu_offset);  
        menu.setShadowDrawable(R.color.light_white);  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
        menu.setFadeDegree(0.67f);  
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);  
		TextView view2=new TextView(this);
		view2.setText("右侧视图");
		menu.setSecondaryMenu(view2);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
