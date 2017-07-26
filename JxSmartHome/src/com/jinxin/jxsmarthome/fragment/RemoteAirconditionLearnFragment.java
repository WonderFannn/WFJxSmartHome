package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.datan.net.command.CodeLibraryTask;
import com.jinxin.datan.net.command.ProductFunListTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigFunTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 自定义空调控制学习界面
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class RemoteAirconditionLearnFragment extends Fragment implements OnClickListener{
	
	private Context context;
	private CustomerBrands cBrand;
	
	private int type = 2;//1—码库  2—学习
	private TextView tvTemp, tvTime;
	private ImageView ivLevel, ivDiriction, ivCurrMode;
	private int isOpen = 1;//0： 开，1 ：关
	private int currTemp = 25, currLevel, currMode, currDiriction;
	private Double currTime;
	private String keyName = "";
	private String keyStr = "";
	private List<ProductFun> productFunList = null;
	private List<Integer> funIds = new ArrayList<Integer>();
	private int successCount = 0;
	private CustomerCenterDialog dialog = null;
	
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				showSaveDialog();
				break;
			case 1:
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				if (successCount == 3  ) {
					JxshApp.showToast(context, "学习成功");
				}
				break;
			}
		}
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_my, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("空调遥控学习");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
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
		
//		if (cBrand != null) {
//			productFunList = getFunListByBrandId(cBrand.getId());
//			if (productFunList != null && productFunList.size() > 0) {
//				for (ProductFun _pf : productFunList) {
//					funIds.add(_pf.getFunId());
//				}
//			}
//		}
	}

	@Override
	public void onClick(View v) {
		try {
		JSONObject _jb = new JSONObject();
		switch (v.getId()) {
		case R.id.tv_wind_level:
			currLevel = changeWindLevelOrDiriction(currLevel);
//			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			changLevelBackground(currLevel);
			_jb.put("cWind", currLevel);
			keyStr = _jb.toString();
			sendCmd();
			break;
		case R.id.tv_open:
			isOpen = (isOpen == 1) ? 0 : 1;
//			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cOnoff", isOpen);
			keyStr = _jb.toString();
			sendCmd();
			break;
		case R.id.tv_wind_diriction:
			currDiriction = changeWindLevelOrDiriction(currDiriction);
			changeDricition(currDiriction);
//			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cWinddir", currDiriction);
			keyStr = _jb.toString();
			sendCmd();
			break;
		case R.id.tv_temp_reduce:
			currTemp =Integer.valueOf(tvTemp.getText().toString());
			if (currTemp > 16) {
				currTemp--;
				tvTemp.setText(String.valueOf(currTemp));
			}else{
				return;
			}
//			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cTemp", currTemp);
			keyStr = _jb.toString();
			sendCmd();
			break;
		case R.id.tv_temp_plus:
			currTemp =Integer.valueOf(tvTemp.getText().toString());
			if (currTemp > 0 && currTemp < 31) {
				currTemp++;
				tvTemp.setText(String.valueOf(currTemp));
			}else{
				return;
			}
//			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cTemp", currTemp);
			keyStr = _jb.toString();
			sendCmd();
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
			currMode = 1;
			changModeIcon(currMode);
//			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cMode", currMode);
			keyStr = _jb.toString();
			sendCmd();
			break;
		case R.id.tv_mode:
			currMode = changeWindLevelOrDiriction(currMode);
//			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cMode", currMode);
			keyStr = _jb.toString();
			changModeIcon(currMode);
			sendCmd();
			break;
		case R.id.tv_mode_warm:
			currMode = 4;
//			keyName = getKeyName(isOpen, currMode, currLevel, currDiriction, currTemp);
			_jb.put("cMode", currMode);
			keyStr = _jb.toString();
			changModeIcon(currMode);
			sendCmd();
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
	
	private void sendCmd(){
		String address485 = getAddress485ByWhId(cBrand.getWhId());
		CodeLibraryTask cLibraryTask = new CodeLibraryTask(getActivity(), cBrand.getmCode(), cBrand.getWhId(),
				"", cBrand.getDeviceId(), cBrand.getId() , address485, type,keyStr,cBrand.getId());
		cLibraryTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
//				JxshApp.showToast(context, "指令已发送，请稍后...");
				handler.sendEmptyMessage(0);
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				loadData();
//				handler.sendEmptyMessageDelayed(1, 2000);
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		cLibraryTask.start();
	}
	
	private void loadData(){
		successCount = 0;
		
		// 更新产品功能列表
		ProductFunListTask pflTask = new ProductFunListTask(
				null);
		pflTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
//				JxshApp.showLoading(context,"同步数据...");
			}

			@Override
			public void onCanceled(ITask task,
					Object arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task,
					Object[] arg) {
				/****** 产品功能更新 *********/

				if (arg != null && arg.length > 0) {
					List<ProductFun> productFunList = (List<ProductFun>) arg[0];
					CommonMethod.updateProductFunList(
							context,
							productFunList);
				}
				successCount++;
				if (successCount == 3 ) {
					handler.sendEmptyMessage(1);
				}
			}

			@Override
			public void onProcess(ITask task,
					Object[] arg) {
				
			}
		});
		pflTask.start();
		
		Logger.warn(null, "更新遥控配置信息2");
		UpdateProductRemoteConfigTask uprcTask = new UpdateProductRemoteConfigTask(null);
		final ProductRemoteConfigDaoImpl uprcDao = new ProductRemoteConfigDaoImpl(context);
		uprcTask.addListener(new TaskListener<ITask>() {

			@Override
			public void onCanceled(ITask task, Object arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				Logger.warn(null, "onFail");
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				Logger.warn(null, "onSuccess");
				if(arg != null && arg.length > 0){
					List<ProductRemoteConfig> prs = (List<ProductRemoteConfig>)arg[0];
					for(ProductRemoteConfig pr : prs) {
						try {
							uprcDao.saveOrUpdate(pr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				successCount++;
				if (successCount == 3 ) {
					handler.sendEmptyMessage(1);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		uprcTask.start();
		
		Logger.warn(null, "更新遥控配置信息");
		UpdateProductRemoteConfigFunTask uprcfTask = new UpdateProductRemoteConfigFunTask(null);
		final ProductRemoteConfigFunDaoImpl uprcfDao = new ProductRemoteConfigFunDaoImpl(context);
		uprcfTask.addListener(new TaskListener<ITask>() {

			@Override
			public void onCanceled(ITask task, Object arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if(arg != null && arg.length > 0){
					List<ProductRemoteConfigFun> prs = (List<ProductRemoteConfigFun>)arg[0];
					for(ProductRemoteConfigFun pr : prs) {
						try {
							uprcfDao.saveOrUpdate(pr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				successCount++;
				if (successCount == 3 ) {
					handler.sendEmptyMessage(1);
				}
			}
		});
		uprcfTask.start();
	}
	
	/**
	 * 保存遥控器
	 */
	private void showSaveDialog(){
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.remote_info_receive_dialog, null);
		dialog = new CustomerCenterDialog(context, R.style.dialog, v);

		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
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
			shwoToast("风速：高");
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
		List<ProductFun> lists = pfDaoImpl.find(null, "funtype=?",
				new String[]{ProductConstants.FUN_TYPE_UFO6}, null, null, null, null);
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
