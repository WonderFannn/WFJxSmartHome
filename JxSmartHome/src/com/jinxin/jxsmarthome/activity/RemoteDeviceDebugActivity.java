package com.jinxin.jxsmarthome.activity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.jinxin.datan.net.command.AddCustomerRemoteBrandTask;
import com.jinxin.datan.net.command.CustomerBrandsListTask;
import com.jinxin.datan.net.command.ProductFunListTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigFunTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.entity.RemoteBrandsType;
import com.jinxin.jxsmarthome.fragment.CustomerAirConditionDebugFragment;
import com.jinxin.jxsmarthome.fragment.CustomerTVDebugFragment;
import com.jinxin.jxsmarthome.fragment.RemoteLearnGuideFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;
import com.jinxin.jxsmarthome.ui.popupwindow.ListPopupWindowForUFO;
import com.jinxin.jxsmarthome.ui.widget.tab.ViewPagerAdapter;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 遥控按键调试
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@SuppressLint("NewApi")
public class RemoteDeviceDebugActivity extends BaseActionBarActivity implements 
							ViewPager.OnPageChangeListener, OnClickListener{
	
	private TextView tvItem;
	private ImageView btnPre, btnSave, btnNext;
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPageAdapter;
	private LinearLayout linearLayout;
	
	private Context context = null;
	private ArrayList<Fragment> mPagerItemList;
	private int currPos = 0;
	private RemoteBrandsType brand = null;
	private List<String> modeList;
	private int brandType;//设备类型 1=空调  2=电视3=机顶盒 4= DVD/VCD 5=电风扇 6=空气净化器

	private List<CustomerProduct> cpList = null;
	private CustomerProduct  currUFO = null;
	private CustomerProductDaoImpl cpdImpl = null;
	
	private int selectPos = 0;
	private String mCode;
	private int type = 1;//1 码库，2 学习
	
	private int successCount = 0;
	private CustomerBrands customerBrand = null;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_my_ufo, menu);
		
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("遥控按键调试");
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.btn_select_ufo:
			showUfoSelcetWindow();
			break;
		case R.id.action_learn://智能学习，先生成遥控器
			type = 2;
			showSaveDialog();
			break;
		}
		return false;
	}
	private void initData() {
		context = this;
		mPagerItemList = new ArrayList<Fragment>();
		cpdImpl = new CustomerProductDaoImpl(context);
		brand = (RemoteBrandsType) getIntent().getSerializableExtra(RemoteBrandsTypeActivity.BRAND);
		
		cpList = cpdImpl.find(null, "code=? or code=?", new String[]{"005","009"}, null, null, null, null);
		Logger.debug(null, "cpList:"+cpList.size());
		if (cpList != null && cpList.size() > 0) {
			currUFO = cpList.get(0);
		}
		
		if (brand != null) {
			brandType = brand.getDeviceId();
			String mCodes = brand.getModelList();
			modeList = Arrays.asList(mCodes.split(","));
			Logger.debug(null, "modeList:"+modeList.size());
		}
		
		initPagerData(currUFO);
	}

	private void initPagerData(CustomerProduct ufo) {
		if (modeList == null || ufo == null) {
			Logger.debug(null, "ufo is null");
			return;
		}
		mCode = modeList.get(0);
		
		for (String code : modeList) {
			Fragment fragment = null;
			Bundle datas = new Bundle();
			datas.putString("mCode", code);
			datas.putSerializable("UFO" , ufo);
			datas.putSerializable(RemoteBrandsTypeActivity.BRAND , brand);
			if (brandType == 1) {
				fragment = new CustomerAirConditionDebugFragment();
			}else if(brandType == 2){
				fragment = new CustomerTVDebugFragment();
			}
			fragment.setArguments(datas);
			mPagerItemList.add(fragment);
		}
	}
	
	private void initView() {
		setContentView(R.layout.activity_remote_device_debug);
		this.tvItem = (TextView) findViewById(R.id.tv_current_item);
		this.mViewPager = (ViewPager) findViewById(R.id.vp_remote_debug);
		this.btnPre = (ImageView) findViewById(R.id.iv_pre_device);
		this.btnSave = (ImageView) findViewById(R.id.btn_save);
		this.btnNext = (ImageView) findViewById(R.id.iv_next_device);
		this.linearLayout = (LinearLayout) findViewById(R.id.ll_group);
		initViewPager();
		
		btnPre.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		
	}
	
	 /**
	 * 初始化ViewPager
	 */
	private void initViewPager(){
		 mViewPageAdapter = new ViewPagerAdapter(mPagerItemList, getSupportFragmentManager());
		 mViewPager.setAdapter(mViewPageAdapter);
		 mViewPager.setOnPageChangeListener(this);
		 if (mPagerItemList.size() > 0) {
			 tvItem.setText("1/"+mPagerItemList.size());
		}
	 }
	
	private void showUfoSelcetWindow(){
		int dialogWidth = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.5);
		final ListPopupWindowForUFO window = new ListPopupWindowForUFO(context, linearLayout, cpList,dialogWidth);
		window.setSelectIndex(selectPos);
		window.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				selectPos = window.getSelectIndex();
				currUFO = cpList.get(selectPos);
			}
		});
//		Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_right_in);
		window.setAnimationStyle(R.anim.right_in);
		window.show(0, getActionBar().getHeight()+35);
	}

	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			mViewPager.removeAllViews();
			initPagerData(currUFO);
			initViewPager();
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		currPos = arg0;
		mCode = modeList.get(arg0);
		tvItem.setText(currPos+1 + "/"+ mPagerItemList.size());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_pre_device:
			if (currPos > 0) {
				currPos--;
				mViewPager.setCurrentItem(currPos);
			}
			break;
		case R.id.btn_save:
			type = 1;
			showSaveDialog();
			break;
		case R.id.iv_next_device:
			if (currPos < mPagerItemList.size()-1) {
				currPos++;
				mViewPager.setCurrentItem(currPos);
			}else{
				Fragment fragment = new RemoteLearnGuideFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(RemoteBrandsTypeActivity.BRAND , brand);
				bundle.putSerializable("UFO" , currUFO);
				fragment.setArguments(bundle);
				addFragment(fragment, true);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 添加fragment
	 * @param fragment
	 * @param addToStack
	 */
	private void addFragment(Fragment fragment, boolean addToStack) {
		if (fragment != null && addToStack) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.frameLayout, fragment)
					.addToBackStack(null).commit();
		} else if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.frameLayout, fragment).commit();
		}
	}
	
	/**
	 * 保存遥控器
	 */
	private void showSaveDialog(){
		if (modeList == null || modeList.size() < 1) {
			JxshApp.showToast(context, "无遥控器模板，无法保存");
			return;
		}
		LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.save_remote_dialog_layout, null);
		final CustomerCenterDialog dialog = new CustomerCenterDialog(RemoteDeviceDebugActivity.this, R.style.dialog, v);

		final EditText etName = (EditText) v.findViewById(R.id.et_remote_name);
		Button btnSure = (Button) v.findViewById(R.id.button_ok);
		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		etName.setHint(brand.getBrandName());
		btnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String nickName = etName.getText().toString();
				if (!TextUtils.isEmpty(nickName)) {
					addBrandTask(nickName);
					dialog.dismiss();
				}else{
					JxshApp.showToast(context, "输入遥控器昵称才能保存");
				}
			}
		});
		
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	/**
	 * 添加遥控器请求
	 * @param nickName
	 */
	private void addBrandTask(String nickName){
		if (currUFO == null || TextUtils.isEmpty(mCode)) {
			Logger.debug(null, "ufo or mcode is null");
			return;
		}
		AddCustomerRemoteBrandTask acrbTask = new AddCustomerRemoteBrandTask(RemoteDeviceDebugActivity.this,
				mCode, currUFO.getWhId(), brandType, nickName, brand.getId(), type);
		acrbTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, "正在生成遥控器，请稍后...");
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
				if (arg != null && arg.length > 0) {
					int brandId = (Integer) arg[0];
					if (type == 1) {
						loadData(brandId);
					}else if(type == 2){
						loadCustomerBrand(brandId);
					}
				}
				
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		acrbTask.start();
	}
	
	/**
	 * 获取通过码库方式生成的遥控数据
	 * @param brandId
	 */
	private void loadData(int brandId){
		if (brandId < 1)  return;
		successCount = 0;
		CustomerBrandsListTask cBrandsListTask = new CustomerBrandsListTask(context,String.valueOf(brandId));
		cBrandsListTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "onSuccess");
				if(arg != null && arg.length > 0){
					List<CustomerBrands> cbList = (List<CustomerBrands>) arg[0];
					if (cbList != null && cbList.size() > 0) {
						customerBrand = cbList.get(0);						
					}
				}
				successCount++;
				gotoRemotePage();
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		cBrandsListTask.start();
		// 更新产品功能列表
		ProductFunListTask pflTask = new ProductFunListTask(
				null);
		pflTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context,"同步数据...");
			}

			@Override
			public void onCanceled(ITask task,
					Object arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.closeLoading();
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
				gotoRemotePage();
			}

			@Override
			public void onProcess(ITask task,
					Object[] arg) {
				
			}
		});
		pflTask.start();
		
		Logger.warn(null, "更新遥控配置信息2");
		UpdateProductRemoteConfigTask uprcTask = new UpdateProductRemoteConfigTask(null);
		final ProductRemoteConfigDaoImpl uprcDao = new ProductRemoteConfigDaoImpl(getApplicationContext());
		uprcTask.addListener(new TaskListener<ITask>() {

			@Override
			public void onCanceled(ITask task, Object arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				Logger.warn(null, "onFail");
				JxshApp.closeLoading();
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
				gotoRemotePage();
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		uprcTask.start();
		
		Logger.warn(null, "更新遥控配置信息");
		UpdateProductRemoteConfigFunTask uprcfTask = new UpdateProductRemoteConfigFunTask(null);
		final ProductRemoteConfigFunDaoImpl uprcfDao = new ProductRemoteConfigFunDaoImpl(getApplicationContext());
		uprcfTask.addListener(new TaskListener<ITask>() {

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
				gotoRemotePage();
			}
		});
		uprcfTask.start();
	}
	
	/**
	 * 获取通过学习方式生成的遥控数据
	 * @param brandId
	 */
	private void loadCustomerBrand(int brandId){
		CustomerBrandsListTask cBrandsListTask = new CustomerBrandsListTask(context,String.valueOf(brandId));
		cBrandsListTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "onSuccess");
				if(arg != null && arg.length > 0){
					List<CustomerBrands> cbList = (List<CustomerBrands>) arg[0];
					if (cbList != null && cbList.size() > 0) {
						customerBrand = cbList.get(0);						
					}
				}
				gotoLearnPage();
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		cBrandsListTask.start();
	}
	
	private void gotoRemotePage(){
		if (successCount < 4 || customerBrand == null) {
			return;
		}else{
			JxshApp.closeLoading();
			Intent intent = new Intent(context,CustomerRemoteControlActivity.class);
			intent.putExtra(RemoteBrandsTypeActivity.BRAND, customerBrand);
			startActivity(intent);
			RemoteDeviceDebugActivity.this.finish();
		}
	}
	
	private void gotoLearnPage(){
		if (customerBrand == null) {
			return;
		}else{
			JxshApp.closeLoading();
			Intent intent = new Intent(context,CustomerRemoteLearnActivity.class);
			intent.putExtra(RemoteBrandsTypeActivity.BRAND, customerBrand);
			startActivity(intent);
			RemoteDeviceDebugActivity.this.finish();
		}
	}
	
}
