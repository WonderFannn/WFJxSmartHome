package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.db.impl.CustomerProductAreaDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.ui.adapter.ControllerGridAdapter;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

public class RemoteTVControlFragment extends Fragment implements
		OnClickListener, OnItemClickListener {
	private List<String> buttonList = new ArrayList<String>();
	private List<Integer> funIds = new ArrayList<Integer>();
	private GridView mNumbersGrid;
	private TextView mOff;
	private TextView mOn;
	private TextView mZero;
	private ImageView mUp;
	private ImageView mDown;
	private ImageView mLeft;
	private ImageView mRight;
	private ImageView mCenter;
	private int caId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.custom_controller,
				new LinearLayout(getActivity()), false);
		initView(view);
		initData();
		return view;
	}

	private void initView(View view) {
		mOff = (TextView) view.findViewById(R.id.custom_controller_on_off);
		mOn = (TextView) view.findViewById(R.id.custom_controller_on_on);
		mZero = (TextView) view.findViewById(R.id.custom_controller_zero);
		mLeft = (ImageView) view.findViewById(R.id.custom_controller_left);
		mUp = (ImageView) view.findViewById(R.id.custom_controller_up);
		mRight = (ImageView) view.findViewById(R.id.custom_controller_right);
		mDown = (ImageView) view.findViewById(R.id.custom_controller_down);
		mCenter = (ImageView) view.findViewById(R.id.custom_controller_center);

		mNumbersGrid = (GridView) view
				.findViewById(R.id.custom_controller_numbers);

		for (int i = 0; i < 9; i++) {
			buttonList.add(String.valueOf(i + 1));
		}

		ControllerGridAdapter adapter = new ControllerGridAdapter(
				getActivity(), buttonList);
		mNumbersGrid.setAdapter(adapter);
		mNumbersGrid.setOnItemClickListener(this);

		mOff.setOnClickListener(this);
		mOn.setOnClickListener(this);
		mZero.setOnClickListener(this);
		mLeft.setOnClickListener(this);
		mUp.setOnClickListener(this);
		mRight.setOnClickListener(this);
		mDown.setOnClickListener(this);
		mCenter.setOnClickListener(this);
	}

	private void initData() {
		Bundle data = getArguments();
		if (data != null) {
			caId = (Integer) data.get("customeAreaId");
			List<CustomerProductArea> cpaList = getCustomerProductAreaByAreaId(caId);
			StringBuilder sb = new StringBuilder();
			for (CustomerProductArea cpa : cpaList) {
				sb.append(cpa.getFunId() + ",");
				funIds.add(cpa.getFunId());
			}
		}
	}

	private List<CustomerProductArea> getCustomerProductAreaByAreaId(int areaId) {
		CustomerProductAreaDaoImpl cpaDao = new CustomerProductAreaDaoImpl(
				getActivity());
		return cpaDao
				.find(null, "areaId=?",
						new String[] { String.valueOf(areaId) }, null, null,
						null, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.custom_controller_on_off:
			sendUFOCmd("001");
			break;
		case R.id.custom_controller_zero:
			sendUFOCmd("003");
			break;
		case R.id.custom_controller_on_on:
			sendUFOCmd("001");
			break;
		case R.id.custom_controller_left:
			sendUFOCmd("013");
			break;
		case R.id.custom_controller_up:
			sendUFOCmd("015");
			break;
		case R.id.custom_controller_right:
			sendUFOCmd("014");
			break;
		case R.id.custom_controller_down:
			sendUFOCmd("016");
			break;
		case R.id.custom_controller_center:
			sendUFOCmd("017");
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		operateGrid(position);
	}
	
	private void operateGrid(int position) {
		try {
			final String functionName = buttonList.get(position);
			String code = getCode(Integer.parseInt(functionName));
			Logger.debug(null, "code:" + code);
			sendUFOCmd(code);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	private String getCode(int name) {
		if(name <= 6) {
			return "00" + String.valueOf(name + 3);
		}else {
			return "0" + String.valueOf(name + 3);
		}
		
	}

	private void sendUFOCmd(String code) {
		ProductFun productFun = getProductFunByCode(code);
		FunDetail funDetail = AppUtil.getFunDetailByFunType(getActivity(),
				ProductConstants.FUN_TYPE_UFO1);
		if (productFun == null || funDetail == null) {
			return;
		}

		productFun.setFunType(ProductConstants.FUN_TYPE_UFO1);

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

	private ProductFun getProductFunByCode(String code) {
		ProductRemoteConfig prc = getProductRemoteConfigByCode(code);
		if (prc != null) {
			ProductRemoteConfigFun prcf = getProductRemoteConfigFunById(String
					.valueOf(prc.getId()));
			if (prcf != null) {
				ProductFun pf = AppUtil.getSingleProductFunByFunId(
						getActivity(), prcf.getFunId());
				return pf;
			}
		}
		return null;
	}

	private ProductRemoteConfig getProductRemoteConfigByCode(String code) {
		ProductRemoteConfigDaoImpl prfDao = new ProductRemoteConfigDaoImpl(
				getActivity());
		List<ProductRemoteConfig> prfList = prfDao.find(null, "code=?",
				new String[] { code }, null, null, null, null);
		if (prfList.size() > 0) {
			return prfList.get(0);
		}
		return null;
	}

	private ProductRemoteConfigFun getProductRemoteConfigFunById(String id) {
		ProductRemoteConfigFunDaoImpl prcfDao = new ProductRemoteConfigFunDaoImpl(
				getActivity());
		List<ProductRemoteConfigFun> prcfList = prcfDao.find(null,
				"configId=?", new String[] { id }, null, null, null, null);
		if (prcfList.size() > 0) {
			for (ProductRemoteConfigFun prcf : prcfList) {
				if (funIds.contains(Integer.parseInt(prcf.getFunId()))) {
					return prcf;
				}
			}
		}
		return null;
	}

}
