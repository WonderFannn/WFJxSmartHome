package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jinxin.db.impl.CustomerAreaDaoImpl;
import com.jinxin.db.impl.CustomerProductAreaDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.activity.RemoteControlActivity;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 遥控控制
 * @author TangLong
 * @company 金鑫智慧
 */
public class RemoteControlFragment extends BaseScrollableTabFragment {
	private List<Integer> areaIdList = new ArrayList<Integer>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initTabsAndFragment() {
		String type = getArguments().getString("type");
		initConrollers(type);
	}
	
	@Override
	public void onPageSelected(int position) {
		super.onPageSelected(position);
		RemoteControlActivity.areaId = areaIdList.get(position);
	}
	
	private void initConrollers(String type) {
		List<String> funIdList = getFunIdListByType(type);
		Logger.debug(null, "funIdList" + funIdList.size());
		if (funIdList.size() > 0) {
			List<CustomerProductArea> cpaList = getCustomerProductAreaByFunIds(funIdList);
			Logger.debug(null, "cpaList" + cpaList.size());
			List<CustomerArea> caList = getCustomerAreaByCPAs(cpaList);
			Logger.debug(null, "caList" + caList.size());
			if(caList.size() > 0) {
				for (CustomerArea ca : caList) {
					if("1".equals(type)) {
						areaIdList.add(ca.getId());
						RemoteTVControlFragment fragment = new RemoteTVControlFragment();
						Bundle args = new Bundle();
						args.putInt("customeAreaId", ca.getId());
						fragment.setArguments(args);
						addTab(ca.getAreaName(), fragment);
					}else if("2".equals(type)) {
						areaIdList.add(ca.getId());
						RemoteACControlFragment fragment = new RemoteACControlFragment();
						Bundle args = new Bundle();
						args.putInt("customeAreaId", ca.getId());
						fragment.setArguments(args);
						addTab(ca.getAreaName(), fragment);
					}
					else addTab(ca.getAreaName(), new Fragment());
				}
			} else {
				addTab("没有遥控设备", new Fragment());
			}
		} else {
			addTab("没有遥控设备", new Fragment());
		}
	}
	
	/**
	 * 根据类型获取分组的funid
	 */
	private List<String> getFunIdListByType(String type) {
		ProductRemoteConfigFunDaoImpl prcfDao = new ProductRemoteConfigFunDaoImpl(getActivity());
		List<ProductRemoteConfigFun> prcfList = prcfDao.find(null, "type=?", new String[]{type}, null, null, null, null);
		if(prcfList.size() > 0) {
			List<String> funIdList = new ArrayList<String>();
			for(ProductRemoteConfigFun prcf : prcfList) {
				funIdList.add(prcf.getFunId());
			}
			return funIdList;
		}else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * 根据多个ProductFun查询CustomerProductArea
	 */
	private List<CustomerProductArea> getCustomerProductAreaByFunIds(List<String> funIdList) {
		if(funIdList.size() > 0) {
			String[] ins = new String[funIdList.size()];
			StringBuilder sb1 = new StringBuilder();
			for(int i = 0; i < funIdList.size(); i++) {
				ins[i] = funIdList.get(i);
				sb1.append("?" + ",");
			}
			sb1.deleteCharAt(sb1.length() - 1);
			CustomerProductAreaDaoImpl cpaDao = new CustomerProductAreaDaoImpl(getActivity());
			return cpaDao.find(null, "funId in(" + sb1.toString() + ")", 
					ins, null, null, null, null);
		}else {
			return Collections.emptyList();
		}
	}
	
	
	/**
	 * 根据多个CustomerProductArea查询CustomerArea
	 */
	private List<CustomerArea> getCustomerAreaByCPAs(List<CustomerProductArea> cpaList) {
		if(cpaList.size() > 0) {
			String[] ins = new String[cpaList.size()];
			StringBuilder sb1 = new StringBuilder();
			for(int i = 0; i < cpaList.size(); i++) {
				ins[i] = String.valueOf(cpaList.get(i).getAreaId());
				sb1.append("?" + ",");
			}
			sb1.deleteCharAt(sb1.length() - 1);
			CustomerAreaDaoImpl caDao = new CustomerAreaDaoImpl(getActivity());
			return caDao.find(null, "id in(" + sb1.toString() + ")", 
					ins, null, null, null, null);
		}else {
			return Collections.emptyList();
		}
	}

}
