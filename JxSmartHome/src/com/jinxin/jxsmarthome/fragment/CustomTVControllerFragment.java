package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.ui.adapter.CustomTVControllerListAdapter;

public class CustomTVControllerFragment extends DialogFragment {
	private OnItemClickListener listener;
	private String tag;
	private List<ProductFun> items;
	private int selectedPosition;
	
	public static CustomTVControllerFragment newInstance(int areaId, OnItemClickListener listener, String tag) {
		CustomTVControllerFragment frag = new CustomTVControllerFragment();
		Bundle args = new Bundle();
		args.putInt("areaId", areaId);
		frag.setArguments(args);
		frag.addOnItemClickListener(listener);
		frag.addTag(tag);
		return frag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(R.style.dialog_fragment_right, android.R.style.Theme_Light);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dialog_list, new LinearLayout(getActivity()), false);
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		ListView listView = (ListView) view.findViewById(R.id.dialog_list);
		String areaId = String.valueOf(getArguments().getInt("areaId"));
		items = getProductFunListByType(areaId, "1");
		
		final CustomTVControllerListAdapter adapter = new CustomTVControllerListAdapter(getActivity(), items);
		adapter.setSelectedPosition(selectedPosition);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(listener != null) {
					listener.onItemClick(position, items.get(position), tag);
				}
			}
		});
	}
	
	private void addOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}
	
	private void addTag(String tag) {
		this.tag = tag;
	}
	
	public interface OnItemClickListener {
		public void onItemClick(int position, Object item, String tag);
	}
	
	public void setSelectedPosition(int position) {
		this.selectedPosition = position;
	}
	
	private List<ProductFun> getProductFunListByType(String areaId, String type) {
		ProductRemoteConfigFunDaoImpl prcfDao = new ProductRemoteConfigFunDaoImpl(getActivity());
		ProductFunDaoImpl pfDao = new ProductFunDaoImpl(getActivity());
		List<ProductRemoteConfigFun> prcfList = prcfDao.find(null, "type=? and areaId=? and configId=''", new String[]{type, areaId}, null, null, null, null);
		if(prcfList.size() > 0) {
			List<ProductFun> pfList = new ArrayList<ProductFun>();
			for(ProductRemoteConfigFun prcf : prcfList) {
				List<ProductFun> temp = pfDao.find(null, "funId=?", new String[]{prcf.getFunId()}, null, null, null, null);
				pfList.addAll(temp);
			}
			return pfList;
		}else {
			return Collections.emptyList();
		}
	}
}
