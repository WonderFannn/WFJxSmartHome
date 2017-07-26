package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.command.ChangeCustomerPatternTask;
import com.jinxin.datan.net.command.CustomerPatternListTask;
import com.jinxin.datan.net.command.ProductPatternOperationListTask;
import com.jinxin.datan.net.command.UpdateCustomerPatternTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerAreaDaoImpl;
import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.fragment.ModeDoubleSocketFragment;
import com.jinxin.jxsmarthome.fragment.ModeFiveSocketFragment;
import com.jinxin.jxsmarthome.fragment.ModeLightBeltFragment;
import com.jinxin.jxsmarthome.fragment.ModeLightsColorFragment;
import com.jinxin.jxsmarthome.fragment.ModePopLightFragment;
import com.jinxin.jxsmarthome.fragment.ModeThreeSocketFragment;
import com.jinxin.jxsmarthome.fragment.MusicSettingForPatternFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.AmendModeListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.ui.popupwindow.ListPopupWindow;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlow;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlowAdapter;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlowSampleAdapter;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.xiaos.adapter.AmendModePinnedHeaderExpandableAdapter;
import com.xiaos.view.PinnedHeaderExpandableListView;

public class AmendModeNewActivity extends BaseActionBarActivity implements OnClickListener{

	private List<List<ProductFunVO>> productFunVOLists;
	private AmendModeListAdapter amendModeListAdapter = null;
	private ListView listView = null;
	private CustomerPattern customerPattern = null;
	private int patternId = -1;
	private String iconLocal = null;// 图标本地路径
	private String iconPath = null;// 图标本地路径
	private FancyCoverFlow fancyCoverFlow = null;
	private FancyCoverFlowAdapter fancyCoverFlowAdapter = null;
	private List<List<WHproductUnfrared>> childrenData;
	private List<FunDetail> funDetailList = null;
	private List<List<ProductFunVO>> _pfVoList = null;
	private FragmentTransaction fragmentTransaction;
	private List<CustomerArea> cAreaList;
	private boolean flag;
	private EditText editText_name = null;
	private CheckBox checkBoxStrart = null;
	private ImageView iconButton = null;
	private Button groupButton;
	private EditText modeComments = null;
	private RelativeLayout iconLayout = null;
	private PinnedHeaderExpandableListView expandableListView;
	private LinearLayout linearLayoutModeSize = null;
	private static final int REQUEST_CODE_SELECT_ICON = 1;
	private int location = 0;
	private List<ProductFunVO> productFunVOs2;
	private AmendModePinnedHeaderExpandableAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
		this.openBoradcastReceiver(mShowBroadcastReceiver,BroadcastManager.ACTION_MODE_COLOR_SET_MESSAGE, 
				BroadcastManager.ACTION_MODE_MUSIC_SET_MESSAGE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_add, menu);
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("模式修改");
		return true;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
//		initConverflow();
		initFancyConverflow();
		if(amendModeListAdapter == null) {
			initListView();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		fancyCoverFlowAdapter = null;
		this.mUIHander.postDelayed(new Runnable() {
			public void run() {
//				initConverflow();
				initFancyConverflow();
			}
		}, 500);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mUIHander.sendEmptyMessage(1);
	}
	
	private void initListView() {
		if (productFunVOLists == null || productFunVOLists.size() <= 0)
			return;
		List<ProductFunVO> pfvo = productFunVOLists.get(0);
		FunDetail _funDetail = funDetailList.get(0);
		if (pfvo != null && _funDetail != null) {
			amendModeListAdapter = new AmendModeListAdapter(AmendModeNewActivity.this,
					pfvo, _funDetail,1);
			listView.setAdapter(amendModeListAdapter);
		}
	}
	
	/**
	 * 初始化
	 */
	private void initView() {
		this.setContentView(R.layout.add_mode_new);

		this.editText_name = (EditText) findViewById(R.id.mode_name_text);
		this.iconButton = (ImageView) findViewById(R.id.add_mode_icon);
		this.iconLayout = (RelativeLayout) findViewById(R.id.add_icon_layout);
		this.listView = (ListView) this.findViewById(R.id.mode_device_list);
		this.expandableListView=(PinnedHeaderExpandableListView) this.findViewById(R.id.mode_device_explistview);
		if (!flag) {
			expandableListView.setVisibility(View.INVISIBLE);
		}else {
			expandableListView.setVisibility(View.VISIBLE);
		}
		this.linearLayoutModeSize = (LinearLayout) findViewById(R.id.linearLayoutModeSize);
		this.groupButton = (Button)findViewById(R.id.button_grouptype);
		this.modeComments = (EditText)findViewById(R.id.add_comments);
		this.fancyCoverFlow = (FancyCoverFlow) this.findViewById(R.id.fancyCoverFlow);

		this.checkBoxStrart = (CheckBox) findViewById(R.id.mode_checked);
		if (this.customerPattern != null) {
			this.checkBoxStrart.setChecked(this.customerPattern.getStatus()
					.equals("1"));
			this.editText_name.setText(this.customerPattern.getPaternName());
			iconLocal = this.customerPattern.getIcon();
			if (iconLocal.startsWith("/")) {
				iconLocal = iconLocal.substring(1);
				Bitmap _bitmap = CommDefines.getSkinManager().getAssetsBitmap(
						iconLocal);
				if (_bitmap != null) {
					iconButton.setImageBitmap(_bitmap);
				}
			}
			modeComments.setText(customerPattern.getMemo());
		}
		
		
		this.checkBoxStrart.setOnClickListener(this);
		this.iconButton.setOnClickListener(this);
		this.groupButton.setOnClickListener(this);
		this.iconLayout.setOnClickListener(this);
		initGroupData();
		initExpandListView(productFunVOs2,expandDetail);
	}


	
	private void initExpandListView(List<ProductFunVO> productFunVOList,FunDetail funDetail) {
		
		//设置expandableListView
//		expandableListView.setHeaderView(getLayoutInflater().inflate(R.layout.group_head,
//				expandableListView, false));
		adapter = new AmendModePinnedHeaderExpandableAdapter( childrenData,
				productFuns, getApplicationContext(),expandableListView,productFunVOList,funDetail,patternId);
		expandableListView.setAdapter(adapter);
	
	}
	
	private void initData() {
		List<ProductPatternOperation> ppoList = null;
		childrenData=new ArrayList<>();
		// 取当前模式数据
		this.patternId = this.getIntent().getIntExtra("patternId", -1);
		if (this.patternId != -1) {
			CustomerPatternDaoImpl cpDaoImpl = new CustomerPatternDaoImpl(
					AmendModeNewActivity.this);
			this.customerPattern = cpDaoImpl.get(this.patternId);
			ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
					AmendModeNewActivity.this);
			ppoList = ppoDaoImpl.find(null, "patternId=?",
					new String[] { Integer.toString(this.patternId) }, null,
					null, null, null);
		}
		// 设备类型数据
		FunDetailDaoImpl fdDaoImpl = new FunDetailDaoImpl(
				AmendModeNewActivity.this);
		this.funDetailList = fdDaoImpl.find(null, "joinPattern=?",
				new String[]{Integer.toString(1)}, null, null, null, null);
		// 填充每种类型列表数据
		this.productFunVOLists = new ArrayList<List<ProductFunVO>>();
		for (int i = 0; i < funDetailList.size(); i++) {
			FunDetail fd=funDetailList.get(i);
			if (fd != null) {
				List<ProductFun> _pfList = CommonMethod
						.currentTypeFillingDevice(AmendModeNewActivity.this,
								fd.getFunType());
				List<ProductFunVO> productFunVOs=creatProductFunVOList(_pfList,
						ppoList, fd);
				this.productFunVOLists.add(productFunVOs);		
								
				//重新查询
				if (fd.getFunType().equals("032")) {
					productFunVOs2=productFunVOs;
					productFuns=_pfList;
					expandDetail=fd;	
					for (int j = 0; j < _pfList.size(); j++) {
						List<WHproductUnfrared> list=CommonMethod
						.catchWhProductFrarerd(AmendModeNewActivity.this,_pfList.get(j).getFunId());
						if (list!=null&&list.size()>0) {
							childrenData.add(list);
							
						}
					}
				}
				//删除无记录数据的product
				if (childrenData.size()>0) {
					for (int j = 0; j < productFunVOs2.size(); j++) {
						boolean flag=false;
						for (int m = 0; m < childrenData.size(); m++) {
							if (childrenData.get(m).get(0).getFundId()==productFunVOs2.get(j).getProductFun().getFunId()) {
								flag=true;
							}
						}
						if (!flag) {
							productFunVOs2.remove(j);
							productFuns.remove(j);
						}
					}
				}
				
				
			}
		}
	}
	private List<ProductFun> productFuns;
	private FunDetail expandDetail;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.save_mode_btn:
			String _etName = editText_name.getText().toString();
			if (TextUtils.isEmpty(_etName)) {
				JxshApp.showToast(
						AmendModeNewActivity.this,
						CommDefines.getSkinManager().string(
								R.string.mo_shi_ming_zi_bu_neng_kong));
			} else {
				CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(
						AmendModeNewActivity.this);
				List<Customer> _cList = cDaoImpl.find();
				if (_cList != null && _cList.size() > 0) {
					Customer _c = _cList.get(0);
					if (_c != null) {
						ChangeCustomerPatternTask ccpTask = new ChangeCustomerPatternTask(
								AmendModeNewActivity.this, patternId, _etName,
								_c.getCustomerId(),
								customerPattern.getStatus(), iconPath,
								String.valueOf(customerPattern.getPatternGroupId()),
								modeComments.getText().toString());
						ccpTask.addListener(new ITaskListener<ITask>() {

							@Override
							public void onStarted(ITask task, Object arg) {
								JxshApp.showLoading(
										AmendModeNewActivity.this,
										CommDefines.getSkinManager().string(
												R.string.bao_cun_zhong));
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
								UpdateCustomerPatternTask ucpTask = new UpdateCustomerPatternTask(
										AmendModeNewActivity.this, patternId,
										getModeOperation());
								
								ucpTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(ITask task, Object arg) {

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
										JxshApp.closeLoading();
										// 本地备份模式同步
										CustomerPatternListTask cplTask = new CustomerPatternListTask(
												AmendModeNewActivity.this);
										cplTask.addListener(new ITaskListener<ITask>() {

											@Override
											public void onStarted(ITask task,
													Object arg) {
												JxshApp.showLoading(
														AmendModeNewActivity.this,
														CommDefines
																.getSkinManager()
																.string(R.string.tong_bu_shu_ju));
											}

											@Override
											public void onCanceled(ITask task,
													Object arg) {
												JxshApp.closeLoading();
											}

											@Override
											public void onFail(ITask task,
													Object[] arg) {
												JxshApp.closeLoading();
												
											}

											@Override
											public void onSuccess(ITask task,
													Object[] arg) {
												/****** 模式名字和图标更新 *********/
//												if (arg != null
//														&& arg.length > 0) {
//													@SuppressWarnings("unchecked")
//													List<CustomerPattern> customerPatternList = (List<CustomerPattern>) arg[0];
//													CommonMethod
//															.updateCustomerPattern(
//																	AmendModeNewActivity.this,
//																	customerPatternList);
//												}
												List<CustomerPattern> customerPatternList = new ArrayList<CustomerPattern>();
												customerPattern.setPaternName(editText_name.getText().toString());
												customerPattern.setIcon(iconPath);
												customerPattern.setMemo(modeComments.getText().toString());
												customerPatternList.add(customerPattern);
												CommonMethod
												.updateCustomerPattern(
														AmendModeNewActivity.this,
														customerPatternList);
												
												/**********************************/
												// 更新产品模式操作
												// 首先删除修改的原模式所有操作
												deleteOldModeFunFromDB(patternId);
												StringBuffer ids = null;
												CustomerPatternDaoImpl _cpDaoImpl = new CustomerPatternDaoImpl(
														AmendModeNewActivity.this);
												List<CustomerPattern> _cps = _cpDaoImpl
														.find();
												if (_cps != null) {
													ids = new StringBuffer();
													for (int i = 0; i < _cps
															.size(); i++) {
														CustomerPattern _cp = _cps
																.get(i);
														if (_cp != null) {
															if (i < _cps.size() - 1) {
																ids.append(
																		_cp.getPatternId())
																		.append(",");
															} else {
																ids.append(_cp
																		.getPatternId());
															}
														}
													}
												}

												ProductPatternOperationListTask ppolTask = new ProductPatternOperationListTask(
														AmendModeNewActivity.this,
														ids.toString());
												ppolTask.addListener(new ITaskListener<ITask>() {

													@Override
													public void onStarted(
															ITask task,
															Object arg) {
													}

													@Override
													public void onCanceled(
															ITask task,
															Object arg) {
														JxshApp.closeLoading();
													}

													@Override
													public void onFail(
															ITask task,
															Object[] arg) {
														JxshApp.closeLoading();
													}

													@Override
													public void onSuccess(
															ITask task,
															Object[] arg) {
														// method stub
														JxshApp.closeLoading();
														/****** 模式操作功能更新 *********/
														if (arg != null
																&& arg.length > 0) {
															@SuppressWarnings("unchecked")
															List<ProductPatternOperation> productPatternOperationList = (List<ProductPatternOperation>) arg[0];
															CommonMethod
																	.updateProductPatternOperationList(
																			AmendModeNewActivity.this,
																			productPatternOperationList);
															// 广播更新
															BroadcastManager
																	.sendBroadcast(
																			BroadcastManager.ACTION_UPDATE_MODE_MESSAGE,
																			null);
														}
														
														AmendModeNewActivity.this
																.finish();// 操作成功后关闭界面
													}

													@Override
													public void onProcess(
															ITask task,
															Object[] arg) {

													}
												});
												ppolTask.start();
											}

											@Override
											public void onProcess(ITask task,
													Object[] arg) {

											}
										});
										cplTask.start();
									}

									@Override
									public void onProcess(ITask task,
											Object[] arg) {

									}
								});
								ucpTask.start();
							}

							@Override
							public void onProcess(ITask task, Object[] arg) {

							}
						});
						ccpTask.start();
					}
				}
			}
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void initFancyConverflow() {

		if (linearLayoutModeSize != null && linearLayoutModeSize.getMeasuredHeight() != 0) {
			// 宽高计算
			int modeHeight = (int) (linearLayoutModeSize.getMeasuredHeight() * 0.9);
			int modeWidth = (int) (modeHeight * 0.9);
			// 初始化converflow
			if (this.fancyCoverFlowAdapter == null) {
				fancyCoverFlowAdapter = new FancyCoverFlowSampleAdapter(AmendModeNewActivity.this,this.funDetailList,modeWidth, modeHeight);
				this.fancyCoverFlow.setAdapter(fancyCoverFlowAdapter);
				this.fancyCoverFlow.setSelection(location);
				fancyCoverFlow.setOnItemClickListener(new ConverflowOnItemClickListener());
				fancyCoverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						
						location = position;
						final List<ProductFunVO> pfvo = productFunVOLists.get(position);
						final FunDetail _funDetail = funDetailList.get(position);
						if(pfvo != null && _funDetail != null){
							if (_funDetail.getFunType().equals("032")) {
								flag=!flag;
								if (productFuns!=null&&productFuns.size()>0) {
									listView.setVisibility(View.INVISIBLE);
									expandableListView.setVisibility(View.VISIBLE);
									expandableListView.setFocusable(true);
								}

							}else {
								flag=!flag;
								expandableListView.setFocusable(false);
								listView.setVisibility(View.VISIBLE);
								expandableListView.setVisibility(View.INVISIBLE);
								amendModeListAdapter = new AmendModeListAdapter(AmendModeNewActivity.this, pfvo,_funDetail,1);
								listView.setAdapter(amendModeListAdapter);
//								 mUIHander.sendEmptyMessage(1);
								// ************************* add by tanglong ************************
								// 功放模式设置实现
								listView.setOnItemClickListener(new OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> parent,
											View view, int position, long id) {
										ProductFunVO productFunVO = pfvo.get(position);
										Fragment fragment = null;
										Bundle data = new Bundle();
										if (productFunVO!=null && productFunVO.isSelected()) {
											if (ProductConstants.FUN_TYPE_POWER_AMPLIFIER
													.equals(_funDetail.getFunType())) {
												fragment = new MusicSettingForPatternFragment();
												data = new Bundle();
												data.putSerializable("customerPattern",customerPattern);
												if(pfvo.size() > position && pfvo.get(position) != null) {
													data.putSerializable("productFunVO",productFunVO);
													if(productFunVO.getProductFun() != null) {
														data.putInt("funId", productFunVO.getProductFun().getFunId());
													}
												}
											}else if (_funDetail.getFunType().
													equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) {
												if (productFunVO.getProductPatternOperation()!=null) {
													fragment = new ModeLightsColorFragment();
													data.putSerializable("funDetail", _funDetail);
													if(productFunVO.getProductFun() != null) {
														data.putSerializable("productFunVO", productFunVO);
													}
												}
											}else if(_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) ||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT) ||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)){
												if (productFunVO.getProductPatternOperation()!=null) {
													fragment = new ModePopLightFragment();
													data.putSerializable("funDetail", _funDetail);
													if(productFunVO.getProductFun() != null) {
														data.putSerializable("productFunVO", productFunVO);
													}
												}
											}else if(_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)){
												if (productFunVO.getProductPatternOperation()!=null) {
													fragment = new ModeLightBeltFragment();
													data.putSerializable("funDetail", _funDetail);
													if(productFunVO.getProductFun() != null) {
														data.putSerializable("productFunVO", productFunVO);
													}
												}
											}else if(_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)){
												if (productFunVO.getProductPatternOperation()!=null) {
													fragment = new ModeDoubleSocketFragment();
													data.putSerializable("funDetail", _funDetail);
													if(productFunVO.getProductFun() != null) {
														data.putSerializable("productFunVO", productFunVO);
													}
												}
											}else if(_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)||
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)
													){
												if (productFunVO.getProductPatternOperation()!=null) {
													fragment = new ModeThreeSocketFragment();
													data.putSerializable("funDetail", _funDetail);
													if(productFunVO.getProductFun() != null) {
														data.putSerializable("productFunVO", productFunVO);
													}
												}
											}else if(_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)){
												if (productFunVO.getProductPatternOperation()!=null) {
													fragment = new ModeFiveSocketFragment();
													data.putSerializable("funDetail", _funDetail);
													if(productFunVO.getProductFun() != null) {
														data.putSerializable("productFunVO", productFunVO);
													}
												}
											}
//											else if(_funDetail.getFunType().
//													equals(ProductConstants.FUN_TYPE_CURTAIN)){
//												fragment = new ModeCurtainFragment();
//												data.putSerializable("funDetail", _funDetail);
//												if(productFunVO.getProductFun() != null) {
//													data.putSerializable("productFunVO", productFunVO);
//												}
//											}
											else {
												return;
											}
											if (productFunVO.isOpen()) {
												fragment.setArguments(data);
												fragmentTransaction = getSupportFragmentManager().beginTransaction();
												fragmentTransaction.add(R.id.light_fragment_layout, fragment);
												fragmentTransaction.addToBackStack(null);
												fragmentTransaction.commitAllowingStateLoss();
											}else{
												JxshApp.showToast(AmendModeNewActivity.this, CommDefines.
														getSkinManager().string(R.string.qing_xian_da_kai_she_bei));
											}
										}else {
											JxshApp.showToast(AmendModeNewActivity.this, CommDefines.getSkinManager().string(R.string.qing_xian_xuan_ze_she_bei));
										}
									}
								});
								// ************************* end ************************
							 
							}
							
							}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						
					}
				});
			}
		}
	}
	
	@Override
	public void onActivityReenter(int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityReenter(resultCode, data);
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		   case RESULT_OK:
		    Bundle b=data.getExtras(); //data为B中回传的Intent
		    String str=b.getString("str1");//str即为回传的值
		    break;
		default:
		    break;
		    }
	}
	
	/**
	 * 构建设备显示列表
	 * 
	 * @param pfList
	 *            设备功能列表
	 * @param ppoList
	 *            模式控制列表
	 * @return
	 */
	private List<ProductFunVO> creatProductFunVOList(List<ProductFun> pfList,
			List<ProductPatternOperation> ppoList, FunDetail fd) {
		List<ProductFunVO> _list = new ArrayList<ProductFunVO>();
		if (pfList == null)
			return _list;
		for (ProductFun pf : pfList) {
			if (pf != null) {
				ProductFunVO _pfVO = new ProductFunVO(pf);
				_list.add(_pfVO);
				if (ppoList != null) {
					
					ProductPatternOperation _ppo = this
							.isExistProductPatternOperation(ppoList,
									pf.getFunId());
					List<ProductPatternOperation> patternOperations=
							this.isExistProductPatternOperationLists(ppoList, pf.getFunId());
					if (_ppo != null) {
						_pfVO.setProductPatternOperation(_ppo);
						_pfVO.setProductPatternOperations(patternOperations);
						_pfVO.setSelected(true);
//						_pfVO.setOpen(_ppo.getOperation().equals(//暂时处理
//								"open") || _ppo.getOperation().equals("set")
//								|| _ppo.getOperation().equals("send"));
						if (_ppo.getOperation().equals("open") || _ppo.getOperation().
								equals("set") || _ppo.getOperation().equals("send")
								|| _ppo.getOperation().equals("play")           // 播放
								|| _ppo.getOperation().equals("pause")          // 停止
								|| _ppo.getOperation().equals("preSong")        // 上一首
								|| _ppo.getOperation().equals("nextSong")       // 下一首 
								|| _ppo.getOperation().equals("soundAdd")       // 音量+
								|| _ppo.getOperation().equals("soundSub")       // 音量-
								|| _ppo.getOperation().equals("muteSingle")     // 静音
								|| _ppo.getOperation().equals("unmuteSingle")   // 取消静音
								||_ppo.getOperation().equals("hueandsat")
								||_ppo.getOperation().equals("on")
								||_ppo.getOperation().equals("up")
								||_ppo.getOperation().equals("double-on-off")
								||_ppo.getOperation().equals("five-on-off")
								||_ppo.getOperation().equals("six-on-off")
								||_ppo.getOperation().equals("three-on-off")
								||_ppo.getOperation().equals("autoMode")
								||_ppo.getOperation().equals("automode")) {
							_pfVO.setOpen(true);
						}else{
							_pfVO.setOpen(false);
						}
						
					}
				}
			}
		}
		return _list;
	}
	
	
	/**
	 * 初始化分组数据
	 */
	private void initGroupData() {
		// 初始化分组数据视图
		cAreaList = getPatternGroups();
		if(cAreaList != null && cAreaList.size() > 0) {
			int groupId = customerPattern.getPatternGroupId();
			String initText = null;
			for(CustomerArea ca : cAreaList) {
				if(ca.getId() == groupId) {
					initText = ca.getAreaName();
					break;
				}
			}
			if(initText != null) {
				this.groupButton.setText(initText);
			}
		}
	}
	
	/**
	 * 获取所有分组
	 */
	private List<CustomerArea> getPatternGroups() {
		List<CustomerArea> patternGroups = null;
		CustomerAreaDaoImpl cadImpl = new CustomerAreaDaoImpl(this);
		patternGroups = cadImpl.find(null, "status=?",
				new String[]{Integer.toString(1)}, null, null, "areaOrder", null);
		
		if(patternGroups == null) {
			patternGroups = Collections.emptyList();
		}
		
		return patternGroups;
	}
	
	/**
	 * 判断当前funId的设备功能在该模式下是否已存在
	 * 
	 * @param ppoList
	 * @param funId
	 * @return
	 */
	private ProductPatternOperation isExistProductPatternOperation(
			List<ProductPatternOperation> ppoList, int funId) {
		if (ppoList == null || funId == -1)
			return null;
		for (ProductPatternOperation ppo : ppoList) {
			if (ppo != null) {
				if (ppo.getFunId() == funId) {
					return ppo;
				}
			}
		}
		return null;
	}
	
	private List<ProductPatternOperation> isExistProductPatternOperationLists(
			List<ProductPatternOperation> ppoList, int funId) {
		List<ProductPatternOperation> patternOperations=new ArrayList<>();
		if (ppoList == null || funId == -1)
			return null;
		for (ProductPatternOperation ppo : ppoList) {
			if (ppo != null) {
				if (ppo.getFunId() == funId) {
//					ProductPatternOperation pro=new ProductPatternOperation(
//							ppo.getId(), ppo.getFunId(), ppo.getWhId(),
//							patternId, "send", 
//						ppo.getParaDesc(), String.valueOf(ppo.getUpdateTime()));
					patternOperations.add(ppo);
					
				}
			}
		}
		return patternOperations;
	}
	
	/**
	 * 获得该模式的所有功能配置列表
	 * 
	 * @return
	 */
	private List<ProductPatternOperation> getModeOperation() {
		List<ProductPatternOperation> _ppoList = new ArrayList<ProductPatternOperation>();
		for (List<ProductFunVO> _list : productFunVOLists) {
			if (_list != null) {
				for (ProductFunVO _pfVO : _list) {
					if (_pfVO != null) {
						if (_pfVO.isSelected()) {
							
							if (_pfVO.getProductPatternOperations()!=null) {
								for (ProductPatternOperation productPatternOperation : _pfVO.getProductPatternOperations()) {
									if (productPatternOperation!=null&&
											productPatternOperation.getParaDesc()!=null) {
										productPatternOperation.setPatternId(patternId);
										_ppoList.add(productPatternOperation);
									}
									
								}
							}else {
								_pfVO.getProductPatternOperation().setPatternId(
										patternId);
								_ppoList.add(_pfVO.getProductPatternOperation());
							}
							
						}
					}
				}
			}
		}
		return _ppoList;
	}
	
	/**
	 * 删除原模式旧的操作
	 */
	private void deleteOldModeFunFromDB(int patternId) {
		if (patternId == -1)
			return;
		ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
				AmendModeNewActivity.this);
		List<ProductPatternOperation> _ppoList = ppoDaoImpl.find(null,
				"patternId=?", new String[] { Integer.toString(patternId) },
				null, null, null, null);
		if (_ppoList != null) {
			for (ProductPatternOperation _ppo : _ppoList) {
				if (_ppo != null) {
					ppoDaoImpl.delete(_ppo.getId());
				}
			}
		}
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			// 刷新converflow
			if (this.fancyCoverFlowAdapter != null) {
				this.fancyCoverFlowAdapter.notifyDataSetChanged();
			}
			break;
		case 1:
			// 刷新listView
			if (this.amendModeListAdapter != null) {
				this.amendModeListAdapter.notifyDataSetChanged();
			}
			break;
		case 2://设置图标
			Bitmap _bitmap = CommDefines.getSkinManager().getAssetsBitmap(
					iconLocal);
			if (_bitmap != null) {
				iconButton.setImageBitmap(_bitmap);
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.mode_checked:
				customerPattern.setStatus(checkBoxStrart.isChecked() ? "1" : "0");
				break;
			case R.id.add_mode_icon:
			case R.id.add_icon_layout:
				Intent intent = new Intent(this, ModeIconActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SELECT_ICON);
				break;
			case R.id.button_grouptype :
				groupButton.setSelected(true);
				if(cAreaList != null && cAreaList.size() > 0) {
					List<String> groups = new ArrayList<String>();
					int len = cAreaList.size();
					for(int i = 0; i < len; i++) {
						groups.add(cAreaList.get(i).getAreaName());
					}
					
					final ListPopupWindow groupListPopWindow = new ListPopupWindow(this, groups, 
							groupButton, groupButton.getWidth(), groups.get(0));
					
					groupListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){
						@Override
						public void onDismiss() {
							groupButton.setSelected(false);
							if(groupListPopWindow.getSelectIndex() != -1) {
								groupButton.setText(groupListPopWindow.getSelectStr());
								customerPattern.setPatternGroupId(
										cAreaList.get(groupListPopWindow.getSelectIndex())
										.getId());
							}
						}
					});
					
					groupListPopWindow.show();
				}
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Activity.RESULT_OK == resultCode) {
			Logger.debug(null, "path:"+data.getStringExtra("ICON"));
			iconPath = data.getStringExtra("ICON");
			iconLocal = data.getStringExtra("ICON").substring(1);
			mUIHander.sendEmptyMessage(2);
		}
	}
	
	class ConverflowOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
		}

	}
	
	private BroadcastReceiver mShowBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (BroadcastManager.ACTION_MODE_COLOR_SET_MESSAGE.equals(intent.getAction())) {
				Bundle bundle = intent.getExtras();
				ProductFunVO _funVO = (ProductFunVO) bundle.get("productFunVO");
				_pfVoList = updatePatternOpration(_funVO);
			}else if(BroadcastManager.ACTION_MODE_MUSIC_SET_MESSAGE.equals(intent.getAction())) {
				Bundle bundle = intent.getExtras();
				ProductFunVO _funVO = (ProductFunVO) bundle.get("productFunVO");
				_pfVoList = updatePatternOpration(_funVO);
			}
		}
		
	};
	
	/**
	 * 更新模式操作List
	 * @param productFunVO
	 * @return
	 */
	private List<List<ProductFunVO>> updatePatternOpration(ProductFunVO productFunVO){
		if (productFunVO != null) {
			for (List<ProductFunVO> _list : productFunVOLists) {
				if (_list != null) {
					for (ProductFunVO _pfVO : _list) {
						if (_pfVO != null
								&& productFunVO.getProductFun().getFunId() == _pfVO
										.getProductFun().getFunId()) {
							_pfVO = productFunVO;
						}
					}
				}
			}
		}
		return productFunVOLists;
	}
	
	private Map<String, Object> params = null;
	/**
	 * 发送控制命令
	 * 
	 * @param productFun 产品
	 * @param funDetail 产品详情
	 * @param cmdStr 命令
	 */
	public void plutoSoundBoxControl(ProductFun productFun, FunDetail funDetail, String cmdStr,String infraredCode) {
		JxshApp.instance.isClinkable = false;
		params = new HashMap<String, Object>();
		params.put("src", "0x01");
		params.put("dst", "0x01");
		params.put("type", "00 32");
		params.put("op", cmdStr);
		StringBuffer buffer=new StringBuffer(infraredCode);
		int count=0;
		for (int i = 2; i < infraredCode.length();i+=2) {
			buffer.insert(i+count, " ");
			count+=1;
		}
		params.put(StaticConstant.PARAM_TEXT, buffer.toString()+" ");
		String type = StaticConstant.OPERATE_COMMON_CMD;
		if (NetworkModeSwitcher.useOfflineMode(this)) {
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(this, productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(this, zegbingWhId);
			if (localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(this, "未找到对应网关", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(this, productFun, funDetail, params, type);

			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(this, localHost + ":3333", cmdList, true,
					false);
			offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(this, productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(this, DatanAgentConnectResource.HOST_ZEGBING,
					true, cmdList, true, 0, false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	/**
	 * 应答监听
	 */
	TaskListener<ITask> listener = new TaskListener<ITask>() {

		@Override
		public void onFail(ITask task, Object[] arg) {
			JxshApp.showToast(AmendModeNewActivity.this, AmendModeNewActivity.this.getString(R.string.mode_contorl_fail));
			if (arg == null) {
				JxshApp.showToast(AmendModeNewActivity.this, AmendModeNewActivity.this.getString(R.string.mode_contorl_fail));
			} else {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				JxshApp.showToast(AmendModeNewActivity.this, resultObj.validResultInfo);
			}
			JxshApp.instance.isClinkable = true;
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			
			RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
			JxshApp.showToast(AmendModeNewActivity.this, "=="+resultObj.validResultInfo+"===="+resultObj.validResultCode);
			if ("-1".equals(resultObj.validResultInfo))
				return;
			String resultStr=resultObj.validResultInfo;
			String payload=resultStr.substring(resultStr.indexOf("[")+1, resultStr.indexOf("]"));
			JxshApp.showToast(AmendModeNewActivity.this,getResponseStr(payload));
		}

	};
	
	/**
	 * 获取状态字符串
	 * 
	 * @param result 应答字符串
	 * @return string 状态字符
	 */
	protected String getResponseStr(String result) {
		if (result == null)
			return null;
		StringBuffer stb = new StringBuffer();
		int length = result.length();
		String statusCode = result.substring(length - 9, length - 1);
		String[] code = statusCode.split(" ");
		for (int i = 0; i < code.length; i++) {
			System.out.println("code="+code[i].toString()+"==");
		}
		if (code[code.length-1].equals("00")) {//读取功率数值
			stb.append("发送成功");
		}else {
			stb.append("发送成功");
		}

		return stb.toString();
	}
	
}
