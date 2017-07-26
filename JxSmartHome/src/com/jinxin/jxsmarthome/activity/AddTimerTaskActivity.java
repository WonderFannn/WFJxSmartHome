package com.jinxin.jxsmarthome.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jinxin.datan.net.command.AddCustomerTimerTask;
import com.jinxin.datan.net.command.CustomerTimerListTask;
import com.jinxin.datan.net.command.TimerTaskOperationListTask;
import com.jinxin.datan.net.command.UpdateCustomerTimerOperateTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.TimerTaskOperation;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.fragment.ModeDoubleSocketFragment;
import com.jinxin.jxsmarthome.fragment.ModeFiveSocketFragment;
import com.jinxin.jxsmarthome.fragment.ModeLightBeltFragment;
import com.jinxin.jxsmarthome.fragment.ModeLightsColorFragment;
import com.jinxin.jxsmarthome.fragment.ModePopLightFragment;
import com.jinxin.jxsmarthome.fragment.ModeThreeSocketFragment;
import com.jinxin.jxsmarthome.fragment.MusicSettingForPatternFragment1;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.service.TimerManager;
import com.jinxin.jxsmarthome.ui.adapter.AddTimeRepeatAdapter;
import com.jinxin.jxsmarthome.ui.adapter.AddTimerModeAdapter;
import com.jinxin.jxsmarthome.ui.adapter.AddTimerSelectDaysAdapter;
import com.jinxin.jxsmarthome.ui.adapter.AddTimerTaskListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.TimerFancyCoverFlowAdapter;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.ui.adapter.data.TimerModeVO;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlow;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlowAdapter;
import com.jinxin.jxsmarthome.ui.widget.fancyCoverFlow.FancyCoverFlowSampleAdapter;
import com.jinxin.jxsmarthome.util.DateUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.xiaos.adapter.AddNewModePinnedHeaderExpandableAdapter;
import com.xiaos.adapter.AddTimerTaskPinnedHeaderExpandableAdapter;
import com.xiaos.view.PinnedHeaderExpandableListView;

/**
 * 添加定时任务
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class AddTimerTaskActivity extends BaseActionBarActivity implements
		OnClickListener {

	private EditText taskName = null;
	private TextView taskTime = null;
	private TextView repeatText = null;
	private ListView addListview = null;
	private PinnedHeaderExpandableListView expandableListView=null;
	private boolean flag;
	private LinearLayout linearLayoutModeSize = null;
	private FrameLayout frameLayout = null;
	private LinearLayout timeLayout = null;
	private LinearLayout repeatLayout = null;

	private List<List<ProductFunVO>> productFunVOLists = null;//设备操作VO
	private List<TimerModeVO> timerModeVoLists = null;//模式操作VO
	private List<FunDetail> funDetailList = null;
	private List<List<ProductFunVO>> _pfVoList = null;//VO更新 临时存储
	private int taskId = -1;// 新增任务Id
	private int patternId = -1;// 已选择的模式Id
	private Map<Integer, Integer> idsMap = null;

	private CustomerTimer customerTimer = null;
	private FancyCoverFlow fancyCoverFlow = null;
	private FancyCoverFlowAdapter fancyCoverFlowAdapter = null;
	
	private AddTimerTaskListAdapter addTaskAdapter = null;//添加设备任务
	private AddTimerModeAdapter addTimerModeAdapter = null;//添加模式任务
	private FragmentTransaction fragmentTransaction;

	private int location = 0;
	private String cornExpression;//任务表达式 JSon
	private String cornDays = "1,2,3,4,5,6,7";//默认 每天
	private String cornTime = null;//任务重复时间
	private int repeatType = 0;
	private String period = "";//周期
	
	private int currentHour = 0;
	private int currentMinute = 0;
	
	private Context context = null; 
	
	/**
	 * 操作列表更新广播
	 */
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
		this.openBoradcastReceiver(mShowBroadcastReceiver,BroadcastManager.ACTION_MODE_COLOR_SET_MESSAGE, 
				BroadcastManager.ACTION_MODE_MUSIC_SET_MESSAGE);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
//		initConverflow();
		initFancyConverflow();
		if (addTaskAdapter == null) {
			initListView();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
//		converflowAdapter = null;
		fancyCoverFlowAdapter = null;
		this.mUIHander.postDelayed(new Runnable() {
			public void run() {
//				initConverflow();
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
		getSupportActionBar().setTitle("新增定时任务");
		
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.save_mode_btn:
			// 判断时间 
			if (repeatType == 2) {
				if (!isTimeLegel()) {
					JxshApp.showToast(context, "该任务不能设置在当前时间之前");
					return false;
				}
			}
			String _etName = taskName.getText().toString();
			idsMap = addTimerModeAdapter.getPatternIdMap();
			getModeOperation();
			JSONObject _jb = new JSONObject();
			try {
				_jb.put("type", repeatType);
				_jb.put("period", cornDays);
				_jb.put("time", cornTime);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			cornExpression = _jb.toString();
			
			if (TextUtils.isEmpty(_etName)) {
				JxshApp.showToast(
						AddTimerTaskActivity.this,
						CommDefines.getSkinManager().string(
								R.string.mo_shi_ming_zi_bu_neng_kong));
			} else if(idsMap.size() < 1 && getModeOperation().size() < 1 ){
				JxshApp.showToast(
						AddTimerTaskActivity.this,
						CommDefines.getSkinManager().string(
								R.string.timer_task_operation_not_null));
			}else {
				CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(
						AddTimerTaskActivity.this);
				List<Customer> _cList = cDaoImpl.find();
				if (_cList != null && _cList.size() > 0) {
					Customer _c = _cList.get(0);
					if (_c != null) {
						if (TextUtils.isEmpty(_etName)) {
							JxshApp.showToast(context, 
									CommDefines.getSkinManager()
											.string(R.string.task_name_not_null));
						}else if(TextUtils.isEmpty(period) || 
								TextUtils.isEmpty(cornExpression)){
							JxshApp.showToast(context, 
									CommDefines.getSkinManager()
											.string(R.string.task_time_not_null));
						}else{
							AddCustomerTimerTask actTask = new AddCustomerTimerTask(
									AddTimerTaskActivity.this, _etName, period,
									cornExpression, 1);
							actTask.addListener(new ITaskListener<ITask>() {

								@Override
								public void onStarted(ITask task, Object arg) {
									JxshApp.showLoading(
											AddTimerTaskActivity.this,
											CommDefines
													.getSkinManager()
													.string(R.string.bao_cun_zhong));
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
									// JxshApp.closeLoading();
									// 上传功能操作
									if (arg != null && arg.length > 0)
										taskId = Integer
												.valueOf((String) arg[0]);
									UpdateCustomerTimerOperateTask uctoTask = new UpdateCustomerTimerOperateTask(
											AddTimerTaskActivity.this, taskId,
											idsMap, getModeOperation());
									uctoTask.addListener(new ITaskListener<ITask>() {

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
											// 本地备份模式同步
											CustomerTimerListTask ctlTask = new CustomerTimerListTask(
													AddTimerTaskActivity.this);
											ctlTask.addListener(new ITaskListener<ITask>() {

												@Override
												public void onStarted(
														ITask task, Object arg) {
													// stub
													JxshApp.showLoading(
															AddTimerTaskActivity.this,
															CommDefines
																	.getSkinManager()
																	.string(R.string.tong_bu_shu_ju));
												}

												@Override
												public void onCanceled(
														ITask task, Object arg) {
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
												public void onSuccess(
														ITask task, Object[] arg) {
													// stub
													/****** 模式更新 *********/
													if (arg != null
															&& arg.length > 0) {
														@SuppressWarnings("unchecked")
														List<CustomerTimer> customerTimerList = (List<CustomerTimer>) arg[0];
														CommonMethod
																.updateCustomerTimer(
																		AddTimerTaskActivity.this,
																		customerTimerList);
													}
													/***************/
													// 更新定时列表操作
													StringBuffer ids = null;
													CustomerTimerTaskDaoImpl _cttDaoImpl = new CustomerTimerTaskDaoImpl(
															AddTimerTaskActivity.this);
													List<CustomerTimer> _cps = _cttDaoImpl
															.find();
													if (_cps != null) {
														ids = new StringBuffer();
														for (int i = 0; i < _cps
																.size(); i++) {
															CustomerTimer _cp = _cps
																	.get(i);
															if (_cp != null) {
																if (i < _cps
																		.size() - 1) {
																	ids.append(
																			_cp.getTaskId())
																			.append(",");
																} else {
																	ids.append(_cp
																			.getTaskId());
																}
															}
														}
													}

													TimerTaskOperationListTask ttoTask = new TimerTaskOperationListTask(
															AddTimerTaskActivity.this,
															ids.toString());
													ttoTask.addListener(new ITaskListener<ITask>() {

														@Override
														public void onStarted(
																ITask task,
																Object arg) {
															
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
															/****** 定时任务操作功能更新 *********/
															if (arg != null
																	&& arg.length > 0) {
																@SuppressWarnings("unchecked")
																List<TimerTaskOperation> timerTaskOperationList = (List<TimerTaskOperation>) arg[0];
																CommonMethod
																		.updateTimerTaskOperationList(
																				AddTimerTaskActivity.this,
																				timerTaskOperationList);
																// 广播更新
																BroadcastManager
																		.sendBroadcast(
																				BroadcastManager.ACTION_UPDATE_TASK_MESSAGE,
																				null);
																
																// 发送增加定时任务的广播 (add by tangl 2014-05-08)
																CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(getApplicationContext());
																List<CustomerTimer> ctList = dao.find(null, "taskId=?", new String[]{String.valueOf(taskId)}, null, null, null, null);
																
																if(ctList != null && ctList.size() > 0) {
																	TimerManager.sendTimerAddBroadcast(context, ctList.get(0));
																}
															}
															
															JxshApp.closeLoading();
															AddTimerTaskActivity.this
																	.finish();
														}

														@Override
														public void onProcess(
																ITask task,
																Object[] arg) {

														}
													});
													ttoTask.start();
												}

												@Override
												public void onProcess(
														ITask task, Object[] arg) {

												}
											});
											ctlTask.start();
										}

										@Override
										public void onProcess(ITask task,
												Object[] arg) {

										}
									});
									uctoTask.start();
								}

								@Override
								public void onProcess(ITask task, Object[] arg) {

								}
							});
							actTask.start();
						}
					}
				}

			}
			break;
			
		}
		return false;
	}

	private void initView() {
		this.context = this;
		this.setContentView(R.layout.add_timer_task_layout);

		this.taskName = (EditText) findViewById(R.id.et_task_name);
		this.taskTime = (TextView) findViewById(R.id.add_time);
		this.repeatText = (TextView) findViewById(R.id.add_repeat_text);
		this.addListview = (ListView) findViewById(R.id.add_task_list);
		this.expandableListView=(PinnedHeaderExpandableListView) this.findViewById(R.id.add_task_explistview);
		if (!flag) {
			expandableListView.setVisibility(View.INVISIBLE);
		}else {
			expandableListView.setVisibility(View.VISIBLE);
		}
		this.linearLayoutModeSize = (LinearLayout) findViewById(R.id.linearLayoutModeSize);
		this.frameLayout = (FrameLayout) findViewById(R.id.add_timer_fregment);
		this.timeLayout = (LinearLayout) findViewById(R.id.add_time_layout);
		this.repeatLayout = (LinearLayout) findViewById(R.id.task_repeat_layout);
		this.fancyCoverFlow = (FancyCoverFlow) this.findViewById(R.id.fancyCoverFlow);
		
		repeatLayout.setOnClickListener(this);
		timeLayout.setOnClickListener(this);
		Calendar calendar = Calendar.getInstance();
		this.currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		this.currentMinute = calendar.get(Calendar.MINUTE);
//		cornTime = getTime(currentHour, currentMinute);
//      taskTime.setText(cornTime);
		initExpandListView(productFunVOs2, expandDetail);
	}

	@SuppressLint("UseSparseArrays")
	private void initData() {
		childrenData=new ArrayList<>();
		idsMap = new HashMap<Integer, Integer>();
		this.customerTimer = new CustomerTimer();
		this.customerTimer.setStatus(1);
		// 设备类型数据
		FunDetailDaoImpl fdDaoImpl = new FunDetailDaoImpl(
				AddTimerTaskActivity.this);
		this.funDetailList = fdDaoImpl.find(null, "joinPattern=?",
				new String[]{Integer.toString(1)}, null, null, null, null);
		// 填充每种类型列表数据
		this.productFunVOLists = new ArrayList<List<ProductFunVO>>();
		for (FunDetail fd : this.funDetailList) {
			if (fd != null) {
				List<ProductFun> _pfList = CommonMethod
						.currentTypeFillingDevice(AddTimerTaskActivity.this,
								fd.getFunType());
				List<ProductFunVO> productFunVOs=creatProductFunVOList(_pfList);
				this.productFunVOLists.add(productFunVOs);
				//重新查询
				if (fd.getFunType().equals("032")) {
					productFunVOs2=productFunVOs;
					productFuns=_pfList;
					expandDetail=fd;	
					for (int j = 0; j < _pfList.size(); j++) {
						List<WHproductUnfrared> list=CommonMethod
						.catchWhProductFrarerd(AddTimerTaskActivity.this,_pfList.get(j).getFunId());
						if (list!=null&&list.size()>0) {
							childrenData.add(list);
						}
					}
					System.out.println("===="+childrenData.toString());
					System.out.println(productFunVOs2.hashCode()+"=="+productFuns.toString()+"=="+expandDetail.toString());
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
		//填充模式列表
		this.timerModeVoLists = new ArrayList<TimerModeVO>();
		CustomerPatternDaoImpl _cpDaoImpl = new CustomerPatternDaoImpl(AddTimerTaskActivity.this);
		List<CustomerPattern> cpList = _cpDaoImpl.find();
		this.timerModeVoLists = createTimerModeVOList(cpList);
		
	}

	//初始化Expandlistview
	private AddTimerTaskPinnedHeaderExpandableAdapter adapter;
	private List<ProductFun> productFuns;
	private FunDetail expandDetail;
	private List<ProductFunVO> productFunVOs2;
	private List<List<WHproductUnfrared>> childrenData;
	private void initExpandListView(List<ProductFunVO> productFunVOList,FunDetail funDetail) {
		//设置expandableListView
//		expandableListView.setHeaderView(getLayoutInflater().inflate(R.layout.group_head,
//				expandableListView, false));
		adapter = new AddTimerTaskPinnedHeaderExpandableAdapter( childrenData,
				productFunVOList, getApplicationContext(),expandableListView,expandDetail);
		expandableListView.setAdapter(adapter);
	
	}
	
	/**
	 * 构建设备显示列表
	 * 
	 * @param pfList
	 * @return
	 */
	private List<ProductFunVO> creatProductFunVOList(List<ProductFun> pfList) {
		List<ProductFunVO> _list = new ArrayList<ProductFunVO>();
		if (pfList == null)
			return _list;
		for (ProductFun pf : pfList) {
			if (pf != null) {
				_list.add(new ProductFunVO(pf));
			}
		}
		return _list;
	}
	
	private List<TimerModeVO> createTimerModeVOList(List<CustomerPattern> cpList){
		List<TimerModeVO> voList = new ArrayList<TimerModeVO>();
		if (cpList == null) {
			return voList;
		}
		for (CustomerPattern _cp : cpList) {
			if (_cp != null) {
				voList.add(new TimerModeVO(_cp));
			}
		}
		return voList;
	}
	
	private void initFancyConverflow() {
		if (linearLayoutModeSize != null
				&& linearLayoutModeSize.getMeasuredHeight() != 0) {
			// 宽高计算
			int modeHeight = (int) (linearLayoutModeSize.getMeasuredHeight() * 0.9);
			int modeWidth = (int) (modeHeight * 0.9);
			// 初始化converflow
			if (this.fancyCoverFlowAdapter == null) {
				fancyCoverFlowAdapter = new TimerFancyCoverFlowAdapter(
						AddTimerTaskActivity.this, this.funDetailList,
						modeWidth, modeHeight);
				this.fancyCoverFlow.setAdapter(fancyCoverFlowAdapter);
				fancyCoverFlow
						.setOnItemClickListener(new ConverflowOnItemClickListener());
				// refreshView();
				// this.mUIHander.sendEmptyMessage(0);
				this.fancyCoverFlow.setSelection(location);
				this.fancyCoverFlow
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								location = position;
								if (location < 1) {
									// 初始化第一个选项为 模式选项
//									CustomerPatternDaoImpl _cpdImpl = new CustomerPatternDaoImpl(context);
//									List<CustomerPattern> _cpList = _cpdImpl.find();
									if (timerModeVoLists!=null) {
										addTimerModeAdapter = new AddTimerModeAdapter(context, timerModeVoLists);
										addListview.setAdapter(addTimerModeAdapter);
										addListview
										.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(
													AdapterView<?> arg0,
													View arg1, int arg2,
													long arg3) {
												Logger.error("Yang", "onClick");
											}
										});
									}
								}else{
									final List<ProductFunVO> pfvo = productFunVOLists
											.get(position-1);
									final FunDetail _funDetail = funDetailList
											.get(position-1);
									if (pfvo != null && _funDetail != null) {
										
										if (_funDetail.getFunType().equals("032")) {
											flag=!flag;
											if (productFuns!=null&&productFuns.size()>0) {
												addListview.setVisibility(View.INVISIBLE);
												expandableListView.setVisibility(View.VISIBLE);
												expandableListView.setFocusable(true);
												expandableListView.setBackgroundResource(R.drawable.bg_list_view);
											}

										}else {
											flag=!flag;
											expandableListView.setFocusable(false);
											addListview.setVisibility(View.VISIBLE);
											expandableListView.setVisibility(View.INVISIBLE);
											addTaskAdapter = new AddTimerTaskListAdapter(
													AddTimerTaskActivity.this, pfvo,
													_funDetail, 0);
											addListview.setAdapter(addTaskAdapter);
											// mUIHander.sendEmptyMessage(1);
											// ************************* add by tanglong
											// ************************
											// 功放模式设置实现
											addListview
											.setOnItemClickListener(new OnItemClickListener() {
												@Override
												public void onItemClick(
														AdapterView<?> parent,
														View view,
														int position, long id) {
													ProductFunVO productFunVO = pfvo.get(position);
													Bundle data = new Bundle();
													Fragment fragment = null;
													if (productFunVO != null && productFunVO.isSelected()) {
														if (ProductConstants.FUN_TYPE_POWER_AMPLIFIER
																.equals(_funDetail
																		.getFunType())) {
															fragment = new MusicSettingForPatternFragment1();
															data.putSerializable("customerTimer", customerTimer);
															if (pfvo.size() > position
																	&& pfvo.get(position) != null) {
																data.putSerializable(
																		"productFunVO",
																		productFunVO);
																if (productFunVO.getProductFun() != null) {
																	data.putInt("funId",productFunVO.getProductFun().getFunId());
																}
															}
															fragment.setArguments(data);
														} else if(_funDetail
																.getFunType()
																.equals(ProductConstants.FUN_TYPE_POWER_AMPLIFIER)) {
															fragment = new ModeLightsColorFragment();
															data.putSerializable("funDetail", _funDetail);
																data.putSerializable("productFunVO",productFunVO);
															fragment.setArguments(data);
														}else if (_funDetail
																.getFunType()
																.equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) {
															fragment = new ModeLightsColorFragment();
															
															data.putSerializable("funDetail",_funDetail);
															data.putSerializable("productFunVO",productFunVO);
															fragment.setArguments(data);
														}else if(_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) ||
																_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT) ||
																_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)){
															fragment = new ModePopLightFragment();
															data.putSerializable("funDetail", _funDetail);
															data.putSerializable("productFunVO", productFunVO);
														} else if(_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)){
															fragment = new ModeLightBeltFragment();
															data.putSerializable("funDetail", _funDetail);
															data.putSerializable("productFunVO", productFunVO);
														}else if(_funDetail.getFunType().equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)
																||ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(_funDetail.getFunType())||
																ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(_funDetail.getFunType())||
																ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(_funDetail.getFunType())||
																ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(_funDetail.getFunType())||
																ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(_funDetail.getFunType())||
																ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(_funDetail.getFunType())){
															fragment = new ModeDoubleSocketFragment();
															data.putSerializable("funDetail", _funDetail);
															data.putSerializable("productFunVO", productFunVO);
														}else if(ProductConstants.FUN_TYPE_FIVE_SWITCH.equals(_funDetail.getFunType())){
															fragment = new ModeFiveSocketFragment();
															data.putSerializable("funDetail", _funDetail);
															data.putSerializable("productFunVO", productFunVO);
														}else if(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE.equals(_funDetail.getFunType())||
																ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR.equals(_funDetail.getFunType())||
																ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE.equals(_funDetail.getFunType())||
																ProductConstants.FUN_TYPE_THREE_SWITCH_SIX.equals(_funDetail.getFunType())){
															fragment = new ModeThreeSocketFragment();
															data.putSerializable("funDetail", _funDetail);
															data.putSerializable("productFunVO", productFunVO);
														}else {
															return;
														}
														
														if (productFunVO.isOpen()) {
															fragment.setArguments(data);
															fragmentTransaction = getSupportFragmentManager().beginTransaction();
															fragmentTransaction.add(R.id.add_timer_fregment, fragment);
															fragmentTransaction.addToBackStack(null);
															fragmentTransaction.commitAllowingStateLoss();
														}else{
															JxshApp.showToast(AddTimerTaskActivity.this, CommDefines.
																	getSkinManager().string(R.string.qing_xian_da_kai_she_bei));
														}
													} else {
														JxshApp.showToast(
																AddTimerTaskActivity.this,CommDefines.getSkinManager()
																.string(R.string.qing_xian_xuan_ze_she_bei));
													}
												}
											});
											// ************************* end
										
										}
										
										}
									
								}// ************************* end
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {

							}
						});
			}
		}
	}

	private void initListView() {
		if (productFunVOLists == null || productFunVOLists.size() <= 0)
			return;
		List<ProductFunVO> pfvo = productFunVOLists.get(0);
		FunDetail _funDetail = funDetailList.get(0);
		if (pfvo != null && _funDetail != null) {
			addTaskAdapter = new AddTimerTaskListAdapter(
					AddTimerTaskActivity.this, pfvo, _funDetail, 0);
			addListview.setAdapter(addTaskAdapter);
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
			if (this.addTaskAdapter != null) {
				this.addTaskAdapter.notifyDataSetChanged();
			}else if (addTimerModeAdapter != null) {
				this.addTimerModeAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	//当点击TimePickerDialog控件的设置按钮时，调用该方法    
    TimePickerDialog.OnTimeSetListener setListener = new TimePickerDialog.OnTimeSetListener()    
    {  
        @Override  
        public void onTimeSet(TimePicker view, int hour, int minute){
        	cornTime = getTime(hour, minute);
        	currentHour = hour;
        	currentMinute = minute;
            taskTime.setText(cornTime);
        }   
        
    };
    
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			this.onBackPressed();
			break;
		case R.id.task_repeat_layout:
			showTimeRepeatDialog();
			break;
		case R.id.add_time_layout:
	        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTimerTaskActivity.
	        		this, setListener, currentHour, currentMinute, true);  
	        timePickerDialog.show();
	        
			break;
		case R.id.button_save:
	         
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mUIHander.sendEmptyMessage(1);
	}

	/**
	 * 获得该模式的所有功能配置列表
	 * 
	 * @return
	 */
	private List<ProductPatternOperation> getModeOperation() {
		if (_pfVoList != null) {
			productFunVOLists = _pfVoList;
		}
		List<ProductPatternOperation> _ppoList = new ArrayList<ProductPatternOperation>();
		for (List<ProductFunVO> _list : productFunVOLists) {
			if (_list != null) {
				for (ProductFunVO _pfVO : _list) {
					if (_pfVO != null) {
						if (_pfVO.isSelected()) {
//							_pfVO.getProductPatternOperation().setPatternId(
//									this.taskId);
//							_ppoList.add(_pfVO.getProductPatternOperation());
//							 Logger.error("Timer-->",_pfVO.getProductPatternOperation().getOperation()+"|"
//							 +_pfVO.getProductPatternOperation().getParaDesc());
							
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

	/**
	 * 更新模式操作List
	 * 
	 * @param productFunVO
	 * @return
	 */
	private List<List<ProductFunVO>> updatePatternOpration(
			ProductFunVO productFunVO) {
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

	class ConverflowOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// location = arg2;
		}

	}
	
	/**
	 * 显示任务重复选中对话框
	 */
	private void showTimeRepeatDialog() {
		final String[] arr = getResources().getStringArray(R.array.repeat_items);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.customer_add_timer_list, null);
		
		ListView mList = (ListView) dialogView.findViewById(R.id.add_time_listview);
		final AddTimeRepeatAdapter _reAdapter = new AddTimeRepeatAdapter(context,repeatType,arr);
		mList.setAdapter(_reAdapter);
		final Dialog dialog = BottomDialogHelper.showDialogInBottom(this, dialogView, null);
		dialog.show();

		mList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				repeatType = arg2;
				if (arg2<arr.length-1) {
					period = arr[arg2];
					repeatText.setText(arr[arg2]);
					switch (arg2) {
					case 0:
						cornDays = "1,2,3,4,5,6,7";
						break;
					case 1:
						cornDays = "2,3,4,5,6";
						break;
					case 2:
						SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
						cornDays = dateFormat.format(new Date());
						break;
					}
					
				}else{
					final String[] array = getResources().getStringArray(R.array.repeat_days_items);
					LayoutInflater inflater = (LayoutInflater) AddTimerTaskActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View dialogView = inflater.inflate(R.layout.customer_add_days_list, null);
					ListView dayList = (ListView) dialogView.findViewById(R.id.add_time_listview);
					Button btnSure = (Button) dialogView.findViewById(R.id.btn_yes);
					Button btnNo = (Button) dialogView.findViewById(R.id.btn_no);
					final AddTimerSelectDaysAdapter _daysAdapter = new AddTimerSelectDaysAdapter(context, 0,array);
					dayList.setAdapter(_daysAdapter);
					final Dialog dayDialog = BottomDialogHelper.showDialogInBottom(AddTimerTaskActivity.this, dialogView, null);
					btnSure.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							cornDays = _daysAdapter.getIdString();
							if (cornDays != "") {
								char[] tempArr  = cornDays.toCharArray();
								Arrays.sort(tempArr);
								StringBuffer buffer = new StringBuffer();
								buffer.append(Integer.parseInt(String.valueOf(tempArr[0])));
								StringBuffer strBuffer = new StringBuffer();
								strBuffer.append(array[Integer.parseInt(
										String.valueOf(tempArr[0]))-1]);
								if (tempArr.length>1) {
									for (int i = 1; i < tempArr.length; i++) {
										buffer.append(","+String.valueOf(tempArr[i]));
										strBuffer.append(","+array[Integer.parseInt(
												String.valueOf(tempArr[i]))-1]);
									}
								}
								cornDays = buffer.toString();
								period = strBuffer.toString();
								repeatText.setText(period);
							}
							dayDialog.dismiss();
						}
					});
					btnNo.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dayDialog.dismiss();
						}
					});
					
					dayDialog.show();
				}
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
	
	private boolean isTimeLegel(){
		String strHour = "00";
		String strMin = "00";
		SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
		String currentDay = dateFormat.format(new Date());
		if (currentHour < 10) {
			strHour = "0"+Integer.toString(currentHour);
		}else{
			strHour = Integer.toString(currentHour);
		}
		if (currentMinute < 10) {
			strMin = "0"+Integer.toString(currentMinute);
		}else{
			strMin = Integer.toString(currentMinute);
		}
		String setTimeStr = currentDay + " " + strHour + ":" + strMin + ":00";
		long setTime = DateUtil.convertStrToLongNew(setTimeStr);
		long currentTime = DateUtil.getNow().getTime();
		if(setTime <= currentTime) {
			return false;
		}else{
			return true;
		}
	
	}

	public String getTime(int hour,int min){
		StringBuffer temp = new StringBuffer();
		if (hour < 10) {
			temp.append("0"+hour+":");
		}else{
			temp.append(hour+":");
		}
		if (min<10) {
			temp.append("0"+min);
		}else {
			temp.append(min+"");
		}
		temp.append(":00");
		return temp.toString();
	}
	
}
