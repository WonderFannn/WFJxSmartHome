package com.jinxin.jxsmarthome.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.jinxin.datan.net.command.GetGeoLocationTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.fragment.GeocoderHealthManagerFragment;
import com.jinxin.jxsmarthome.fragment.GeocoderRealTimeInquiryFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.Logger;

/**
 *展示用地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class GeoCoderActivity extends BaseActionBarActivity implements OnClickListener {
	private TextView realTime;
	private TextView healthManager;
	private ProductFun productFun = null;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("定位手表");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return false;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geocoder);
		initView();
		
	}
	
	private void initView() {
		productFun = (ProductFun) getIntent().getSerializableExtra("productFun");
		realTime=(TextView) findViewById(R.id.tv_geocoder_reale_time);
		healthManager=(TextView) findViewById(R.id.tv_geocoder_health_manager);
		realTime.setOnClickListener(this);
		healthManager.setOnClickListener(this);
		onClick(realTime);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private GeocoderRealTimeInquiryFragment realTimeFragment;
	private GeocoderHealthManagerFragment healthManagerFragment;
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
		Bundle bundle=new Bundle();
		bundle.putSerializable("productFun", productFun);
		
		switch (arg0.getId()) {
		case R.id.tv_geocoder_reale_time:
			realTimeFragment=new GeocoderRealTimeInquiryFragment();
			
			realTime.setSelected(true);
			healthManager.setSelected(false);
			realTimeFragment.setArguments(bundle);
			if (realTimeFragment.isAdded()) {
				transaction.show(realTimeFragment);
			}else{
				transaction.add(R.id.fragment_geocoder_replace, realTimeFragment);
				transaction.show(realTimeFragment);
			}
			if (null!=healthManagerFragment) {
				transaction.hide(healthManagerFragment);
			}
			break;
		case R.id.tv_geocoder_health_manager:
			healthManagerFragment=new GeocoderHealthManagerFragment(); 
			realTime.setSelected(false);
			healthManager.setSelected(true);
			healthManagerFragment.setArguments(bundle);
			if (healthManagerFragment.isAdded()) {
				transaction.show(healthManagerFragment);
			}else{
				transaction.add(R.id.fragment_geocoder_replace, healthManagerFragment);
				transaction.show(healthManagerFragment);
			}
			if (null!=realTimeFragment) {
				transaction.hide(realTimeFragment);
			}
			break;
		default:
			break;
		}
		
		
		/*if (fragment.isAdded()) {
			transaction.show(fragment);
		}else{
			transaction.add(R.id.fragment_geocoder_replace, fragment);
			transaction.show(fragment);
		}
//			transaction.replace(R.id.fragment_geocoder_replace, fragment );
*/		transaction.commit();
		
	}


}
