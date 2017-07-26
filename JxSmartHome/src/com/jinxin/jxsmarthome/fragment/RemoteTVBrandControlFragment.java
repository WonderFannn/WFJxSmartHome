package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jinxin.datan.net.command.CodeLibraryTask;
import com.jinxin.datan.net.command.CustomerLearnCmdSendTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.RemoteBrandsTypeActivity;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 电视控制界面
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class RemoteTVBrandControlFragment extends Fragment implements OnClickListener{
	
	private Context context;
	private CustomerBrands cBrand;
	private RelativeLayout okLayout;
	private LinearLayout numLayout;
	private int type = 1;//1—码库方式控制， 2—学习指令
	
	private List<ProductFun> productFunList = null;
	private List<Integer> funIds = new ArrayList<Integer>();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("电视遥控");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
			break;

		}
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_tv_control_layout, container, false);
		initData();
		initView(view);
		return view;
	}
	
	@Override
	public void onResume() {
		funIds.clear();
		productFunList = getFunListByBrandId(cBrand.getId());
		if (productFunList != null && productFunList.size() > 0) {
			for (ProductFun _pf : productFunList) {
				funIds.add(_pf.getFunId());
			}
		}
		super.onResume();
	}
	
	private void initView(View view) {
		okLayout = (RelativeLayout) view.findViewById(R.id.ok_layout);
		numLayout = (LinearLayout) view.findViewById(R.id.num_layout);
		
		view.findViewById(R.id.tv_open).setOnClickListener(this);
		view.findViewById(R.id.tv_mode).setOnClickListener(this);
		view.findViewById(R.id.tv_mute).setOnClickListener(this);
		view.findViewById(R.id.btn_up).setOnClickListener(this);
		view.findViewById(R.id.btn_right).setOnClickListener(this);
		view.findViewById(R.id.iv_ok).setOnClickListener(this);
		view.findViewById(R.id.btn_left).setOnClickListener(this);
		view.findViewById(R.id.btn_down).setOnClickListener(this);
		view.findViewById(R.id.tv_back).setOnClickListener(this);
		view.findViewById(R.id.tv_home).setOnClickListener(this);
		view.findViewById(R.id.tv_more).setOnClickListener(this);
		//数字键
		view.findViewById(R.id.btn_num1).setOnClickListener(this);
		view.findViewById(R.id.btn_num2).setOnClickListener(this);
		view.findViewById(R.id.btn_num3).setOnClickListener(this);
		view.findViewById(R.id.btn_num4).setOnClickListener(this);
		view.findViewById(R.id.btn_num5).setOnClickListener(this);
		view.findViewById(R.id.btn_num6).setOnClickListener(this);
		view.findViewById(R.id.btn_num7).setOnClickListener(this);
		view.findViewById(R.id.btn_num8).setOnClickListener(this);
		view.findViewById(R.id.btn_num9).setOnClickListener(this);
		view.findViewById(R.id.btn_select_switch).setOnClickListener(this);
		view.findViewById(R.id.btn_num0).setOnClickListener(this);
		view.findViewById(R.id.btn_sleep).setOnClickListener(this);
	}

	private void initData() {
		cBrand = (CustomerBrands) getArguments().getSerializable(RemoteBrandsTypeActivity.BRAND);
		
		if (cBrand != null) {
			productFunList = getFunListByBrandId(cBrand.getId());
			if (productFunList != null && productFunList.size() > 0) {
				for (ProductFun _pf : productFunList) {
					funIds.add(_pf.getFunId());
				}
			}
			
			type = cBrand.getType();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_open:
			selectSendType(type, "0");
			break;
		case R.id.tv_mode://切换中介控制区域
			if (okLayout.isShown()) {
				okLayout.setVisibility(View.INVISIBLE);
				numLayout.setVisibility(View.VISIBLE);
			}else{
				okLayout.setVisibility(View.VISIBLE);
				numLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.tv_mute:
			selectSendType(type, "37");
			break;
		case R.id.btn_up://音量+
			selectSendType(type, "33");
			break;
		case R.id.btn_right://频道+
			selectSendType(type, "35");
			break;
		case R.id.iv_ok://画中画
			selectSendType(type, "18");
			break;
		case R.id.btn_left:
			selectSendType(type, "36");
			break;
		case R.id.btn_down:
			selectSendType(type, "34");
			break;
		case R.id.tv_back:
			selectSendType(type, "17");
			break;
		case R.id.tv_home:
			selectSendType(type, "29");
			break;
		case R.id.tv_more://TODO
			
			break;
		case R.id.btn_num1:
			selectSendType(type, "5");
			break;
		case R.id.btn_num2:
			selectSendType(type, "6");
			break;
		case R.id.btn_num3:
			selectSendType(type, "7");
			break;
		case R.id.btn_num4:
			selectSendType(type, "8");
			break;
		case R.id.btn_num5:
			selectSendType(type, "9");
			break;
		case R.id.btn_num6:
			selectSendType(type, "10");
			break;
		case R.id.btn_num7:
			selectSendType(type, "11");
			break;
		case R.id.btn_num8:
			selectSendType(type, "12");
			break;
		case R.id.btn_num9:
			selectSendType(type, "13");
			break;
		case R.id.btn_select_switch:
			selectSendType(type, "14");
			break;
		case R.id.btn_num0:
			selectSendType(type, "15");
			break;
		case R.id.btn_sleep:
			selectSendType(type, "4");
			break;
		}
	}
	
	private void selectSendType(int type, String code){
		if (TextUtils.isEmpty(code)) {
			return;
		}
		if (type == 1) {
			sendUFOCmd(code);
		}else if (type == 2){
			sendLearnControlCMD(code);
		}
	}
	
	/**
	 * 码库方式发送控制指令
	 * @param code
	 */
	private void sendUFOCmd(String code) {
//		ProductFun productFun = getProductFunByCode(code);
//		FunDetail funDetail = AppUtil.getFunDetailByFunType(getActivity(),
//				ProductConstants.FUN_TYPE_UFO1);
		if (cBrand == null || TextUtils.isEmpty(code)) {
			return;
		}
		String address485 = getAddress485ByWhId(cBrand.getWhId());

		JSONObject _jb = new JSONObject();
		try {
			_jb.put("code", code);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CodeLibraryTask cLibraryTask = new CodeLibraryTask(getActivity(),
				cBrand.getmCode(), cBrand.getWhId(), _jb.toString(),
				cBrand.getDeviceId(), cBrand.getId(), address485, type, code,
				cBrand.getId());
		cLibraryTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showToast(context, "指令已发送，请稍后...");
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
				JxshApp.showToast(context, "操作成功");
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {

			}
		});
		cLibraryTask.start();

	}
	
	/**
	 * 学习方式发送控制指令
	 * @param code
	 */
	private void sendLearnControlCMD(String code){
		String address485 = getAddress485ByWhId(cBrand.getWhId());
		ProductFun productFun = getProductFunByCode(code);
		if (cBrand == null || TextUtils.isEmpty(code) || productFun == null) {
			Logger.debug(null, "productFun is null");
			JxshApp.showToast(context, "该按键未学习，点击右上方可进入学习界面");
			return;
		}
		CustomerLearnCmdSendTask clcTask = new CustomerLearnCmdSendTask(context, cBrand.getWhId(),address485, productFun.getFunId());
		clcTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				JxshApp.showToast(context, "操作成功");
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		clcTask.start();
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
		List<ProductRemoteConfig> prfList = prfDao.find(null, "code=? and type=?",
				new String[] { code,String.valueOf(4) }, null, null, null, null);
		if (prfList.size() > 0) {
			return prfList.get(0);
		}
		return null;
	}
	
	private ProductRemoteConfigFun getProductRemoteConfigFunById(String id) {
		ProductRemoteConfigFunDaoImpl prcfDao = new ProductRemoteConfigFunDaoImpl(
				getActivity());
		List<ProductRemoteConfigFun> prcfList = prcfDao.find(null,
				"configId=? and type=?", new String[] {id, String.valueOf(4)}, null, null, null, null);
		if (prcfList.size() > 0) {
			for (ProductRemoteConfigFun prcf : prcfList) {
				if (funIds.contains(Integer.parseInt(prcf.getFunId()))) {
					return prcf;
				}
			}
		}
		return null;
	}

	/**
	 * 通过遥控板ID查找该遥控下所有的ProductFun
	 * @param brandId
	 * @return
	 */
	private List<ProductFun> getFunListByBrandId(int brandId){
		ProductFunDaoImpl pfDaoImpl = new ProductFunDaoImpl(context);
		List<ProductFun> tempList = new ArrayList<ProductFun>();
		List<ProductFun> lists = pfDaoImpl.find(null, "funType=?",
				new String[]{ProductConstants.FUN_TYPE_UFO7}, null, null, null, null);
		if (lists != null && lists.size() > 0) {
			for (ProductFun productFun : lists) {
				String funParams = productFun.getFunParams();
				if (!TextUtils.isEmpty(funParams)) {
					try {
						JSONObject _jb = new JSONObject(funParams);
						if (brandId == _jb.getInt("customerBrands")) {
							tempList.add(productFun);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		return tempList;
	}
	
	private String getAddress485ByWhId(String whId){
		String address485 = "";
		CustomerProductDaoImpl cpdImpl = new CustomerProductDaoImpl(context);
		List<CustomerProduct> lists = cpdImpl.find(null, "whId", new String[]{}, null, null, null, null);
		if (lists != null && lists.size() > 0) {
			address485 = lists.get(0).getAddress485();
		}
		return address485;
	}
	
}
