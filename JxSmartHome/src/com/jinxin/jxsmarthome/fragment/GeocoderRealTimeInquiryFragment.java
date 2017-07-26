package com.jinxin.jxsmarthome.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.jinxin.datan.net.command.GetGeoLocationTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.GeoCoderActivity;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity.BasicHandler;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.DeviceControlAdapter;
import com.jinxin.jxsmarthome.util.AppUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class GeocoderRealTimeInquiryFragment extends Fragment implements OnGetGeoCoderResultListener{
	
	
	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private BaiduMap mBaiduMap = null;
	private MapView mMapView = null;
	private String longitude, latitude, address;
	private ProductFun productFun = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle bundle=getArguments();
		productFun=(ProductFun) bundle.getSerializable("productFun");
		View view = inflater.inflate(R.layout.fragment_geocoder_mapview, null);
		// 地图初始化
		mMapView = (MapView) view.findViewById(R.id.fragment_geocoder_bmapView);
		mBaiduMap = mMapView.getMap();
		if (productFun!=null) {
			System.out.println("==="+productFun.toString());
		}
		
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		getLocationPoint();
		return view;
	}
	
	private void getLocationPoint(){
		if (productFun != null) {
			GetGeoLocationTask gglTask = new GetGeoLocationTask(getActivity(), productFun.getWhId());
			gglTask.addListener(new ITaskListener<ITask>() {

				@Override
				public void onStarted(ITask task, Object arg) {
					JxshApp.showLoading(getActivity(), "正在获取位置，请稍后...");
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
					JxshApp.closeLoading();
					System.out.println("123");
					if (arg != null && arg.length > 0) {
						try {
							JSONObject _jb = new JSONObject((String)arg[0]);
							longitude = _jb.getString("longitude");
							latitude = _jb.getString("latitude");
							address = _jb.getString("address");
							handler.sendEmptyMessage(0);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void onProcess(ITask task, Object[] arg) {
					
				}
			});
			gglTask.start();
		}
	}
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
					LatLng ptCenter = new LatLng((Float.valueOf(latitude)), (Float.valueOf(longitude)));
					// 反Geo搜索
					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
					.location(ptCenter));
				}
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		Toast.makeText(getActivity(), result.getAddress(),
				Toast.LENGTH_LONG).show();

	}

}
