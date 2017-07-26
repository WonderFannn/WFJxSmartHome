package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.command.AddCustomerPatternTask;
import com.jinxin.datan.net.command.CustomerPatternListTask;
import com.jinxin.datan.net.command.ProductPatternOperationListTask;
import com.jinxin.datan.net.command.UpdateCustomerPatternTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerAreaDaoImpl;
import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
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
import com.jinxin.jxsmarthome.ui.adapter.AddModeListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.ui.popupwindow.ListPopupWindow;
import com.jinxin.jxsmarthome.ui.popupwindow.ShowSuggestWindows;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlow;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlowAdapter;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlowSampleAdapter;
import com.jinxin.jxsmarthome.util.Logger;
import com.xiaos.adapter.AddNewModePinnedHeaderExpandableAdapter;
import com.xiaos.view.PinnedHeaderExpandableListView;

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

/**
 * 模式新增
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class AddNewModeActivity extends BaseActionBarActivity implements OnClickListener{
	private AddModeListAdapter addModeListAdapter = null;
	private ListView listView = null;
	private CustomerPattern customerPattern = null;
	private String iconLocal = null;//图标本地路径
	private String iconPath = null;//图标上传路径
	private int patternId = -1;//新增模式Id
	
	private FancyCoverFlow fancyCoverFlow = null;
	private FancyCoverFlowAdapter fancyCoverFlowAdapter = null;
	
	private List<List<ProductFunVO>> productFunVOLists; //设备操作VO
	private List<FunDetail> funDetailList = null;
	private List<List<ProductFunVO>> _pfVoList = null; //VO更新 临时存储
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
	private int groupTypeId;
	private LinearLayout linearLayoutModeSize = null;
	private static final int REQUEST_CODE_SELECT_ICON = 1;
	private int location = 0;
	
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
				String operation = _funVO.getProductPatternOperation().getOperation();
				_pfVoList = updatePatternOpration(_funVO);
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		ininView();
		this.openBoradcastReceiver(mShowBroadcastReceiver,BroadcastManager.ACTION_MODE_COLOR_SET_MESSAGE, 
				BroadcastManager.ACTION_MODE_MUSIC_SET_MESSAGE);
		
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		initFancyConverflow();
		if (addModeListAdapter == null) {
			initListView();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		fancyCoverFlowAdapter = null;
		this.mUIHander.postDelayed(new Runnable() {
			public void run() {
				initFancyConverflow();
			}
		}, 500);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_add, menu);
		
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("新增模式");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.save_mode_btn:
			String _etName = editText_name.getText().toString();
			if(TextUtils.isEmpty(_etName)){
				JxshApp.showToast(AddNewModeActivity.this, CommDefines.getSkinManager().string(R.string.mo_shi_ming_zi_bu_neng_kong));
			}else{
				CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(AddNewModeActivity.this);
				 List<Customer> _cList = cDaoImpl.find();
				 if(_cList != null && _cList.size() > 0){
					 Customer _c =_cList.get(0);
					 if(_c != null){
						 	// 如果分组为做选择，默认选择第一个分组
						 	if(cAreaList.size() > 0 && groupTypeId == 0) {
						 		groupTypeId = cAreaList.get(0).getId();
						 	}
							AddCustomerPatternTask acpTask = new AddCustomerPatternTask(AddNewModeActivity.this,
									_etName, _c.getCustomerId(), customerPattern.getStatus(), iconPath,
									String.valueOf(groupTypeId),modeComments.getText().toString()); 
							acpTask.addListener(new ITaskListener<ITask>() {

								@Override
								public void onStarted(ITask task, Object arg) {
									JxshApp.showLoading(AddNewModeActivity.this, CommDefines.getSkinManager().string(R.string.bao_cun_zhong));
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
//									JxshApp.closeLoading();
									//上传功能操作
									if(arg != null && arg.length > 0)
										patternId = Integer.valueOf((String)arg[0]);
									UpdateCustomerPatternTask ucpTask = new UpdateCustomerPatternTask(AddNewModeActivity.this,patternId, getModeOperation());
									ucpTask.addListener(new ITaskListener<ITask>() {

										@Override
										public void onStarted(ITask task,
												Object arg) {
											
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
											JxshApp.closeLoading();
											//本地备份模式同步
											CustomerPatternListTask cplTask = new CustomerPatternListTask(
													AddNewModeActivity.this);
											cplTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(ITask task,
														Object arg) {
													// stub
													JxshApp.showLoading(
															AddNewModeActivity.this,
															CommDefines
																	.getSkinManager()
																	.string(R.string.tong_bu_shu_ju));
												}

												@Override
												public void onCanceled(ITask task,
														Object arg) {
													// stub
													JxshApp.closeLoading();
												}

												@Override
												public void onFail(ITask task,
														Object[] arg) {
													// stub
													JxshApp.closeLoading();
												}

												@Override
												public void onSuccess(ITask task,
														Object[] arg) {
													// stub
													/****** 模式更新 *********/
													if (arg != null
															&& arg.length > 0) {
														@SuppressWarnings("unchecked")
														List<CustomerPattern> customerPatternList = (List<CustomerPattern>) arg[0];
														CommonMethod
																.updateCustomerPattern(
																		AddNewModeActivity.this,
																		customerPatternList);
													}
													/***************/
													// 更新产品模式操作
													StringBuffer ids = null;
													CustomerPatternDaoImpl _cpDaoImpl = new CustomerPatternDaoImpl(
															AddNewModeActivity.this);
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
															AddNewModeActivity.this, ids
																	.toString());
													ppolTask.addListener(new ITaskListener<ITask>() {

														@Override
														public void onStarted(
																ITask task,
																Object arg) {
															// method stub
															}

														@Override
														public void onCanceled(
																ITask task,
																Object arg) {
															// method stub
															JxshApp.closeLoading();
														}

														@Override
														public void onFail(
																ITask task,
																Object[] arg) {
															// method stub
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
																				AddNewModeActivity.this,
																				productPatternOperationList);
																//广播更新
																BroadcastManager.sendBroadcast(BroadcastManager.ACTION_UPDATE_MODE_MESSAGE, null);
															}
															AddNewModeActivity.this.finish();
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
												public void onProcess(
														ITask task, Object[] arg) {
													
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
							acpTask.start();
					 }
				 }
			}
			break;
		}
		return false;
	}
	
	public void ininView(){
		setContentView(R.layout.add_mode_new);
		
		this.editText_name = (EditText) findViewById(R.id.mode_name_text);
		this.iconButton = (ImageView) findViewById(R.id.add_mode_icon);
		this.iconLayout = (RelativeLayout) findViewById(R.id.add_icon_layout);
		this.listView = (ListView)this.findViewById(R.id.mode_device_list);
		this.expandableListView=(PinnedHeaderExpandableListView) this.findViewById(R.id.mode_device_explistview);
		if (!flag) {
			expandableListView.setVisibility(View.INVISIBLE);
		}else {
			expandableListView.setVisibility(View.VISIBLE);
		}
		this.linearLayoutModeSize = (LinearLayout)findViewById(R.id.linearLayoutModeSize);
		this.groupButton = (Button)findViewById(R.id.button_grouptype);
		this.modeComments = (EditText) findViewById(R.id.add_comments);
		this.fancyCoverFlow = (FancyCoverFlow) this.findViewById(R.id.fancyCoverFlow);
		
		this.checkBoxStrart = (CheckBox)findViewById(R.id.mode_checked);
		if(this.customerPattern != null){
			this.checkBoxStrart.setChecked(this.customerPattern.getStatus().equals("1"));
		}
		this.checkBoxStrart.setOnClickListener(this);
		this.iconButton.setOnClickListener(this);
		this.groupButton.setOnClickListener(this);
		this.iconLayout.setOnClickListener(this);
		
		// 初始化分组数据视图
		cAreaList = getPatternGroups();
		if(cAreaList != null && cAreaList.size() > 0) {
			String initText = cAreaList.get(0).getAreaName() == null ? 
					"未命名" : cAreaList.get(0).getAreaName();
			this.groupButton.setText(initText);
			this.customerPattern.setPatternGroupId(cAreaList.get(0).getId());
		}
		
		//新手引导
		suggestWindow = new ShowSuggestWindows(AddNewModeActivity.this, R.drawable.bg_guide_add_mode1, "");
		suggestWindow.showFullWindows("AddNewModeActivity",R.drawable.bg_guide_add_mode1);
		
		initExpandListView(productFunVOs2,expandDetail);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		childrenData=new ArrayList<>();
		this.customerPattern = new CustomerPattern();
		this.customerPattern.setStatus("1");
		//设备类型数据
		FunDetailDaoImpl fdDaoImpl = new FunDetailDaoImpl(AddNewModeActivity.this);
		this.funDetailList = fdDaoImpl.find(null, "joinPattern=?",
				new String[]{Integer.toString(1)}, null, null, null, null);
		//填充每种类型列表数据
		this.productFunVOLists = new ArrayList<List<ProductFunVO>>(); 
		for(FunDetail fd : this.funDetailList){
			if(fd != null){
				 List<ProductFun> _pfList = CommonMethod.currentTypeFillingDevice(AddNewModeActivity.this, fd.getFunType());
				 List<ProductFunVO> productFunVOs =creatProductFunVOList(_pfList);
				 this.productFunVOLists.add(productFunVOs);
				 
				//重新查询
				if (fd.getFunType().equals("032")) {
					productFunVOs2=productFunVOs;
					productFuns=_pfList;
					expandDetail=fd;	
					for (int j = 0; j < _pfList.size(); j++) {
						List<WHproductUnfrared> list=CommonMethod
						.catchWhProductFrarerd(AddNewModeActivity.this,_pfList.get(j).getFunId());
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
	private List<ProductFunVO> productFunVOs2;
	private AddNewModePinnedHeaderExpandableAdapter adapter;
	private List<List<WHproductUnfrared>> childrenData;
	private void initExpandListView(List<ProductFunVO> productFunVOList,FunDetail funDetail) {
//		System.out.println(productFunVOList.size()+"====="+funDetail.toString());
		//设置expandableListView
//		expandableListView.setHeaderView(getLayoutInflater().inflate(R.layout.group_head,
//				expandableListView, false));
		adapter = new AddNewModePinnedHeaderExpandableAdapter( childrenData,
				productFunVOList, getApplicationContext(),expandableListView,expandDetail);
		expandableListView.setAdapter(adapter);
	
	}
	
	
	/**
	 * 构建设备显示列表
	 * @param pfList
	 * @return
	 */
	private List<ProductFunVO> creatProductFunVOList(List<ProductFun> pfList){
		List<ProductFunVO> _list = new ArrayList<ProductFunVO>();
		if(pfList == null)return _list;
			for(ProductFun pf : pfList){
				if(pf != null){
					_list.add(new ProductFunVO(pf));
				}
			}
		return _list;
	}
	
	private void initListView(){
		if(productFunVOLists == null || productFunVOLists.size() <= 0)return;
		 List<ProductFunVO> pfvo = productFunVOLists.get(0);
		 FunDetail _funDetail = funDetailList.get(0);
		 if(pfvo != null && _funDetail != null){
			 addModeListAdapter = new AddModeListAdapter(
					 AddNewModeActivity.this,pfvo,_funDetail,0);
			 listView.setAdapter(addModeListAdapter);
		 }
	}

	/**
	 * 获得该模式的所有功能配置列表
	 * @return
	 */
	private List<ProductPatternOperation> getModeOperation(){
		if(_pfVoList != null){
			productFunVOLists = _pfVoList;
		}
		List<ProductPatternOperation> _ppoList = new ArrayList<ProductPatternOperation>();
		for(List<ProductFunVO> _list : productFunVOLists){
			if(_list != null){
				for(ProductFunVO _pfVO : _list){
					if(_pfVO != null){
						if(_pfVO.isSelected() ){
							/*_pfVO.getProductPatternOperation().setPatternId(this.patternId);
							_ppoList.add(_pfVO.getProductPatternOperation());*/
//							Logger.error("Yang",_pfVO.getProductPatternOperation().getOperation()+"|"
//									+_pfVO.getProductPatternOperation().getParaDesc());
							if (_pfVO.getProductPatternOperations()!=null) {
								for (ProductPatternOperation productPatternOperation : _pfVO.getProductPatternOperations()) {
									if (productPatternOperation!=null&&
											productPatternOperation.getParaDesc()!=null) {
										productPatternOperation.setPatternId(this.patternId);
										_ppoList.add(productPatternOperation);
									}
									
								}
							}else if(_pfVO.getProductPatternOperations()==null){
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
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mUIHander.sendEmptyMessage(1);
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		switch(msg.what){
		case 0:
			//刷新converflow
			if (fancyCoverFlowAdapter != null) {
				this.fancyCoverFlowAdapter.notifyDataSetChanged();
			}
			break;
		case 1:
			//刷新listView
			if(this.addModeListAdapter != null){
				this.addModeListAdapter.notifyDataSetChanged();
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
			Intent intent = new Intent(this,ModeIconActivity.class);
			startActivityForResult(intent,REQUEST_CODE_SELECT_ICON);
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
							int id = cAreaList.get(groupListPopWindow.getSelectIndex())
									.getId();
							customerPattern.setPatternGroupId(id);
							groupTypeId = id;
						}
					}
				});
				
				groupListPopWindow.show();
			}
			break;
		}
	}
	
	
	
	private void initFancyConverflow() {
		if (linearLayoutModeSize != null && linearLayoutModeSize.getMeasuredHeight() != 0) {
			// 宽高计算
			int modeHeight = (int) (linearLayoutModeSize.getMeasuredHeight() * 0.9);
			int modeWidth = (int) (modeHeight * 0.9);
			// 初始化converflow
			if (this.fancyCoverFlowAdapter == null) {
				fancyCoverFlowAdapter = new FancyCoverFlowSampleAdapter(AddNewModeActivity.this,this.funDetailList,modeWidth, modeHeight);
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
//									fancyCoverFlowAdapter.notifyDataSetChanged();
//									expandableListView.setBackgroundResource(R.drawable.bg_list_view);
								}

							}else {
								flag=!flag;
								expandableListView.setFocusable(false);
								listView.setVisibility(View.VISIBLE);
								expandableListView.setVisibility(View.INVISIBLE);
								addModeListAdapter = new AddModeListAdapter(AddNewModeActivity.this,pfvo,_funDetail,0);
								 listView.setAdapter(addModeListAdapter);
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
												if(pfvo.size() > position && pfvo.get(position) != null) {
													data.putSerializable("productFunVO",productFunVO);
													data.putSerializable("funDetail", _funDetail);
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
													_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)
													){
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
												JxshApp.showToast(AddNewModeActivity.this, CommDefines.
														getSkinManager().string(R.string.qing_xian_da_kai_she_bei));
											}
										}else {
											JxshApp.showToast(AddNewModeActivity.this, CommDefines.getSkinManager().string(R.string.qing_xian_xuan_ze_she_bei));
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
	
	class ConverflowOnItemClickListener implements OnItemClickListener{
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		}
		
	}
	
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			Logger.debug(null, bundle.getString("input"));
			Logger.debug(null, bundle.getString("speaker"));

			for (List<ProductFunVO> pfs : productFunVOLists) {
				for (int i = 0; i < pfs.size(); i++) {
					if (pfs.get(i) != null
							&& pfs.get(i).getProductFun() != null
							&& ProductConstants.FUN_TYPE_POWER_AMPLIFIER
									.equals(pfs.get(i).getProductFun()
											.getFunType())) {
						JSONObject jb = new JSONObject();
						try {
							jb.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, bundle.getString("input"));
							jb.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, bundle.getString("speaker"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						pfs.get(i).getProductPatternOperation().setParaDesc(jb.toString());
					}
				}
			}

		}
		if(REQUEST_CODE_SELECT_ICON == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				iconPath = data.getStringExtra("ICON");
				iconLocal = data.getStringExtra("ICON").substring(1);
				mUIHander.sendEmptyMessage(2);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
