package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.ui.adapter.CustomContorllerGridAdapter;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 多功能遥控板
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomControllerFragment extends Fragment implements OnItemClickListener {
	private GridView mGrideView;
	private List<ProductFun> mListData;
	private CustomContorllerGridAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_custom_control, new LinearLayout(getActivity()), false);
		initView(view);
		return view;
	}
	
	private void initData() {
		mListData = getProductFunListByType("3");
		adapter = new CustomContorllerGridAdapter(getActivity(), mListData);
	}
	
	private void initView(View view) {
		mGrideView = (GridView)view.findViewById(R.id.controller_custom_grid);
		mGrideView.setAdapter(adapter);
		mGrideView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		sendUFOCmd(mListData.get(position));
	}
	
	private List<ProductFun> getProductFunListByType(String type) {
		ProductRemoteConfigFunDaoImpl prcfDao = new ProductRemoteConfigFunDaoImpl(getActivity());
		ProductFunDaoImpl pfDao = new ProductFunDaoImpl(getActivity());
		List<ProductRemoteConfigFun> prcfList = prcfDao.find(null, "type=?", new String[]{type}, null, null, null, null);
		if(prcfList.size() > 0) {
			List<ProductFun> pfList = new ArrayList<ProductFun>();
			for(ProductRemoteConfigFun prcf : prcfList) {
				List<ProductFun> temp = pfDao.find(null, "funId=? and enable=1", new String[]{prcf.getFunId()}, null, null, null, null);
				pfList.addAll(temp);
			}
			Logger.debug(null, "pfList:"+pfList.size() + "");
			return pfList;
		}else {
			return Collections.emptyList();
		}
	}
	
	private void sendUFOCmd(ProductFun productFun) {
		FunDetail funDetail = AppUtil.getFunDetailByFunType(getActivity(),productFun.getFunType());
		if (productFun == null || funDetail == null) {
			return;
		}

//		productFun.setFunType(ProductConstants.FUN_TYPE_UFO1);
		
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(StaticConstant.PARAM_INDEX, productFun.getFunUnit());
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun,
					funDetail, params);
		} else {

			List<byte[]> cmds = CommonMethod.productFunToCMD(getActivity(),
					productFun, funDetail, null);

			if (cmds == null || cmds.size() < 1) {
				return;
			}

			new OnlineCmdSenderLong(getActivity(), cmds).send();
		}
	}
}
