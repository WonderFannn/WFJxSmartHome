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
import android.widget.ImageView;
import android.widget.TextView;

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
 * 空调控制界面
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class RemoteAirconditionControlFragment extends Fragment implements OnClickListener{
	
	private Context context;
	private CustomerBrands cBrand;
	
	private int type = 1;//1—码库方式控制， 2—学习指令， 3-学习方式控制
	private TextView tvTemp, tvTime;
	private ImageView ivLevel, ivDiriction, ivCurrMode;
	private int isOpen = 1;//0： 开，1 ：关
	private int currTemp = 25, currLevel, currMode, currDiriction;
	private Double currTime;
	private String keyName ="";
	private String keyStr ="";
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
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("空调遥控");
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
		View view = inflater.inflate(R.layout.activity_aircondition_control_layout, container, false);
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
		tvTemp = (TextView) view.findViewById(R.id.tv_curr_temp);
		tvTime = (TextView) view.findViewById(R.id.tv_time);
		ivLevel = (ImageView) view.findViewById(R.id.tv_curr_wind_level);
		ivDiriction = (ImageView) view.findViewById(R.id.tv_curr_wind_diriction);
		ivCurrMode = (ImageView) view.findViewById(R.id.iv_curr_mode);
		
		view.findViewById(R.id.tv_wind_level).setOnClickListener(this);
		view.findViewById(R.id.tv_wind_diriction).setOnClickListener(this);
		view.findViewById(R.id.tv_open).setOnClickListener(this);
		view.findViewById(R.id.tv_temp_reduce).setOnClickListener(this);
		view.findViewById(R.id.tv_temp_plus).setOnClickListener(this);
		view.findViewById(R.id.tv_time_reduce).setOnClickListener(this);
		view.findViewById(R.id.tv_time_plus).setOnClickListener(this);
		view.findViewById(R.id.tv_mode_cold).setOnClickListener(this);
		view.findViewById(R.id.tv_mode).setOnClickListener(this);
		view.findViewById(R.id.tv_mode_warm).setOnClickListener(this);
		
		currTemp = Integer.valueOf(tvTemp.getText().toString());
	}

	private void initData() {
		cBrand = (CustomerBrands) getArguments().getSerializable("customerBrand");
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
		JSONObject _jb = new JSONObject();
		try {
		switch (v.getId()) {
		case R.id.tv_wind_level:
			isOpen = 0;
			currLevel = changeWindLevelOrDiriction(currLevel);
			changLevelBackground(currLevel);
			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cWind", currLevel);
			keyStr = _jb.toString();
//			sendCmd();
			selectSendType(type, keyStr);
			break;
		case R.id.tv_open:
			isOpen = (isOpen == 1) ? 0 : 1;
			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cOnoff", isOpen);
			keyStr = _jb.toString();
			selectSendType(type, keyStr);
			break;
		case R.id.tv_wind_diriction:
			isOpen = 0;
			currDiriction = changeWindLevelOrDiriction(currDiriction);
			changeDricition(currDiriction);
			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cWinddir", currDiriction);
			keyStr = _jb.toString();
			selectSendType(type, keyStr);
			break;
		case R.id.tv_temp_reduce:
			isOpen = 0;
			currTemp =Integer.valueOf(tvTemp.getText().toString());
			if (currTemp > 16) {
				currTemp--;
				tvTemp.setText(String.valueOf(currTemp));
			}else{
				return;
			}
			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cTemp", currTemp);
			keyStr = _jb.toString();
			selectSendType(type, keyStr);
			break;
		case R.id.tv_temp_plus:
			isOpen = 0;
			currTemp =Integer.valueOf(tvTemp.getText().toString());
			if (currTemp > 0 && currTemp < 31) {
				currTemp++;
				tvTemp.setText(String.valueOf(currTemp));
			}else{
				return;
			}
			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cTemp", currTemp);
			keyStr = _jb.toString();
			selectSendType(type, keyStr);
			break;
		case R.id.tv_time_reduce:
			currTime = Double.valueOf(tvTime.getText().toString());
			if (currTime > 0) {
				currTime -= 0.5;
				tvTime.setText(String.valueOf(currTime));
			}
			break;
		case R.id.tv_time_plus:
			currTime = Double.valueOf(tvTime.getText().toString());
			if (currTime < 12.5) {
				currTime += 0.5;
				tvTime.setText(String.valueOf(currTime));
			}
			break;
		case R.id.tv_mode_cold:
			isOpen = 0;
			currMode = 1;
			changModeIcon(currMode);
			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cMode", currMode);
			keyStr = _jb.toString();
			selectSendType(type, keyStr);
			break;
		case R.id.tv_mode:
			isOpen = 0;
			currMode = changeWindLevelOrDiriction(currMode);
			changModeIcon(currMode);
			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cMode", currMode);
			keyStr = _jb.toString();
			selectSendType(type, keyStr);
			break;
		case R.id.tv_mode_warm:
			isOpen = 0;
			currMode = 4;
			changModeIcon(currMode);
			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cMode", currMode);
			keyStr = _jb.toString();
			selectSendType(type, keyStr);
			break;
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private String getKeyName(int isOpen,int mode, int windLevel, int windDir, int temp){
		JSONObject _jb = new JSONObject();
		try {
			_jb.put("cOnoff", isOpen);
			_jb.put("cMode", mode);
			_jb.put("cWind", windLevel);
			_jb.put("cWinddir", windDir);
			_jb.put("cTemp", temp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return _jb.toString();
	}
	
	private void selectSendType(int type, String code){
		if (TextUtils.isEmpty(code)) {
			return;
		}
		if (type == 1) {
			sendCmd();
		}else if (type == 2){
			sendLearnControlCMD(code);
		}
	}
	
	private void sendCmd(){
		String address485 = getAddress485ByWhId(cBrand.getWhId());
		CodeLibraryTask cLibraryTask = new CodeLibraryTask(getActivity(), cBrand.getmCode(), cBrand.getWhId(),
				keyName, cBrand.getDeviceId(), cBrand.getId() , address485, type,keyStr,cBrand.getId());
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
	
	/**
	 * 改变风速或风向
	 * @param level
	 * @return
	 */
	private int changeWindLevelOrDiriction(int level){
		int temp = level;
		if (temp < 3) {
			temp++;
		}else{
			temp = 0;
		}
		return temp;
	}
	
	private void changLevelBackground(int level){
		if (level == 0) {
			ivLevel.setImageResource(R.drawable.ic_wind_level_auto);
			shwoToast("风速：自动");
		}else if(level == 1){
			ivLevel.setImageResource(R.drawable.ic_wind_level_1);
			shwoToast("风速：慢");
		}else if(level == 2){
			ivLevel.setImageResource(R.drawable.ic_wind_level_2);
			shwoToast("风速：中");
		}else if(level == 3){
			ivLevel.setImageResource(R.drawable.ic_wind_level_3);
			shwoToast("风速：快");
		}
	}
	
	private void changeDricition(int dirct){
		if (dirct == 0) {
			ivDiriction.setImageResource(R.drawable.ic_wind_dirc_auto);
			shwoToast("风向：自动");
		}else if(dirct == 1){
			ivDiriction.setImageResource(R.drawable.ic_wind_dirc_1);
		}else if(dirct == 2){
			ivDiriction.setImageResource(R.drawable.ic_wind_dirc_2);
		}else if(dirct == 3){
			ivDiriction.setImageResource(R.drawable.ic_wind_dirc_3);
		}
	}
	
	private void changModeIcon(int mode){
		if (mode == 0) {
			ivCurrMode.setImageResource(R.drawable.icon_mode_auto);
			shwoToast("模式：自动");
			currTemp = 25;
		}else if(mode == 1){
			ivCurrMode.setImageResource(R.drawable.icon_cool);
			shwoToast("模式：制冷");
		}else if(mode == 2){
			ivCurrMode.setImageResource(R.drawable.icon_wet);
			shwoToast("模式：除湿");
		}else if(mode == 3){
			ivCurrMode.setImageResource(R.drawable.icon_wind);
			shwoToast("模式：送风");
		}else if(mode == 4){
			ivCurrMode.setImageResource(R.drawable.icon_hot);
			shwoToast("模式：制热");
		}
	}
	
	
	private void shwoToast(String str){
		if (TextUtils.isEmpty(str)) {
			return;
		}
		JxshApp.showToast(context, str);
	}
	/**
	 * 获取productFun
	 * @param code
	 * @return
	 */
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
				new String[] { code,String.valueOf(5) }, null, null, null, null);
		if (prfList.size() > 0) {
			return prfList.get(0);
		}
		return null;
	}
	
	private ProductRemoteConfigFun getProductRemoteConfigFunById(String id) {
		ProductRemoteConfigFunDaoImpl prcfDao = new ProductRemoteConfigFunDaoImpl(
				getActivity());
		List<ProductRemoteConfigFun> prcfList = prcfDao.find(null,
				"configId=? and type=?", new String[] {id, String.valueOf(5)}, null, null, null, null);
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
	
	/**
	 * 查找485
	 * @param whId
	 * @return
	 */
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
