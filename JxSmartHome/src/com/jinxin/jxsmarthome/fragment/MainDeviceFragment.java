package com.jinxin.jxsmarthome.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.CloudMusicActivity;
import com.jinxin.jxsmarthome.activity.DeviceActivity;
import com.jinxin.jxsmarthome.activity.EnvironmentActivity;
import com.jinxin.jxsmarthome.activity.MainMusicActivity;
import com.jinxin.jxsmarthome.activity.MusicActivity;
import com.jinxin.jxsmarthome.activity.RemoteControlActivity;
import com.jinxin.jxsmarthome.activity.RemoteDeviceTypeActivity;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.DeviceFragmentAdapter;
import com.jinxin.jxsmarthome.util.Logger;

public class MainDeviceFragment extends Fragment {

	private List<FunDetail> fdList;
	private FunDetailDaoImpl funImpl = null;

	private GridView gridView = null;
	private DeviceFragmentAdapter adapter = null;

	public static String FUNDETAIL = "funDetail";// key

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_device, null);
		initData();
		initView(view);
		return view;
	}

	/**
	 * 初始化View
	 * 
	 * @param view
	 */
	public void initView(View view) {
		this.gridView = (GridView) view.findViewById(R.id.device_fragment_grid);
		if (fdList == null) return;
		
		adapter = new DeviceFragmentAdapter(getActivity(), fdList);
		gridView.setAdapter(adapter);
		System.out.println("==to=="+fdList.toString());
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				FunDetail funDetail = fdList.get(position);
				
				if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER.equals(funDetail.getFunType())) {
					Intent intent = new Intent(getActivity(), MainMusicActivity.class);
					startActivity(intent);
				}else if(ProductConstants.FUN_TYPE_UFO1.equals(funDetail.getFunType()) ||
						ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2.equals(funDetail.getFunType())) {
					startActivity(new Intent(getActivity(), RemoteControlActivity.class));
//					startActivity(new Intent(getActivity(), RemoteDeviceTypeActivity.class));
				}else if(ProductConstants.FUN_TYPE_ENV.equals(funDetail.getFunType())) {
					if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
						JxshApp.showToast(getActivity(),  CommDefines.getSkinManager()
								.string(R.string.li_xian_cao_zuo_tips));
						return;
					}
					startActivity(new Intent(getActivity(), EnvironmentActivity.class));
				}else {
					System.out.println("===funDetail=="+funDetail.toString());
					Intent intent = new Intent(getActivity(), DeviceActivity.class);
					intent.putExtra(FUNDETAIL, funDetail);
					startActivity(intent);
				}
			}
		});

	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		// 从缓存区数据
		funImpl = new FunDetailDaoImpl(getActivity());
		fdList = funImpl.find(null, null, null, null, null, "id ASC", null);
		if (fdList != null && fdList.size() > 0) {
			for (int i = 0; i < fdList.size(); i++) {
				FunDetail _fd = fdList.get(i);
				if (_fd.getFunType().equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)||
					_fd.getFunType().equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW) ||
					_fd.getFunType().equals(ProductConstants.FUN_TYPE_TFT_LIGHT) || 
					_fd.getFunType().equals(ProductConstants.FUN_TYPE_VIOCE_BOX)) {
					fdList.remove(i);
				}
			}
		}
	}

}
