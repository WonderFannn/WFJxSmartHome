package com.jinxin.offline.util;

import java.util.ArrayList;
import java.util.List;

import xgzx.VeinUnlock.VeinLogin;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.datan.net.command.CustomerDetailTask;
import com.jinxin.datan.net.command.CustomerPatternListTask;
import com.jinxin.datan.net.command.CustomerProductListTask;
import com.jinxin.datan.net.command.CustomerTimerListTask;
import com.jinxin.datan.net.command.DoorbellListTask;
import com.jinxin.datan.net.command.FunDetailConfigTask;
import com.jinxin.datan.net.command.FunDetailListTask;
import com.jinxin.datan.net.command.LoginTask;
import com.jinxin.datan.net.command.ProductFunListTask;
import com.jinxin.datan.net.command.ProductPatternOperationListTask;
import com.jinxin.datan.net.command.SyncCloudSettingTask;
import com.jinxin.datan.net.command.SysUserDetailTask;
import com.jinxin.datan.net.command.TimerTaskOperationListTask;
import com.jinxin.datan.net.command.UpdateCustomerAreaTask;
import com.jinxin.datan.net.command.UpdateCustomerDataSyncTask;
import com.jinxin.datan.net.command.UpdateCustomerProductAreaTask;
import com.jinxin.datan.net.command.UpdateProductRegisterTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigFunTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.CustomerProductAreaDaoImpl;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.db.impl.ProductRegisterDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.db.impl.SysUserDaoImpl;
import com.jinxin.db.util.CustomerDataSyncHelper;
import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.LoginActivity;
import com.jinxin.jxsmarthome.activity.VoiceLoginActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.entity.CustomerDataSync;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.CustomerProductArea;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.entity.TimerTaskOperation;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.widget.vfad.ADManager;
import com.jinxin.jxsmarthome.ui.widget.vfad.Advertising;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.EncryptUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.MD5Util;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 离线转在线登录
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class OfflineToOnline{
	
	private static int taskCount = 0;
	private static int cancelTaskCount = 0;
	private static int failTaskCount = 0;
	private static int succesTaskCount = 0;
	private static LoginTask loginTask = null;
	
	private static Context context = null;
	
	private static Dialog loadingDialog = null;
	
	protected static Handler mUIHander = new Handler();
	
	public static void changeToOnline(String account,final String password,final Context con){
		context = con;
		
		loadingDialog = new Dialog(context, R.style.custom_dialog_loading);
		
		LayoutInflater inflater = (LayoutInflater) con.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.custom_dialog_loading, null);
		
		final TextView loadingText = (TextView)dialogView.findViewById(R.id.custom_dialog_loading_text);
		ImageView loadingImg = (ImageView)dialogView.findViewById(R.id.custom_dialog_loading_img);
		
		loadingText.setText("正在同步数据，请勿取消操作...");
		
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_loading);
		loadingImg.startAnimation(animation);
		
		loadingDialog.setContentView(dialogView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		loadingDialog.setCancelable(true);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
		
		//登陆
		loginTask = new LoginTask(con, account,
				password);
		loginTask.addListener(new ITaskListener<ITask>() {
			@Override
			public void onStarted(ITask task, Object arg) {
//				JxshApp.showLoading(
//						context,
//						CommDefines.getSkinManager().string(
//								R.string.zhang_hu_deng_lu));
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				System.out.println("onFail");
				mUIHander.post(new Runnable() {
					@Override
					public void run() {
						JxshApp.showToast(con, JxshApp.getContext().getResources().getString(R.string.yong_hu_mi_ma_error));
						if (context != null && loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
						BroadcastManager.sendBroadcast(VeinLogin.ACTION_LOGIN_FAIL, null);
					}
				});
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
//				taskCount+=1;
				if (arg != null && arg.length > 0){
					SysUser sysUser = (SysUser) arg[0];
					String account = sysUser.getAccount();
					String secretkey = sysUser.getPassword();
					String nickyName = sysUser.getNickyName();
					String subAccount = sysUser.getSubAccunt();
//					String pwd = MD5Util.getURLEncode(password);
					if (TextUtils.isEmpty(subAccount)) {//空为账户登录
						SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
								ControlDefine.KEY_ACCOUNT, account);
						SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
								ControlDefine.KEY_SUNACCOUNT, "");
					}else{//子账户登录
						SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
								ControlDefine.KEY_ACCOUNT, account);
						SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
								ControlDefine.KEY_SUNACCOUNT, subAccount);
					}
					SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
							ControlDefine.KEY_SECRETKEY,secretkey);
					SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
							ControlDefine.KEY_NICKNAME,nickyName);
//					SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
//							ControlDefine.KEY_SUNACCOUNT,subAccount);
					
//					SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
//							ControlDefine.KEY_PASSWORD,pwd);
					EncryptUtil.setPasswordForOrdinary(password);
					CrashReport.setUserId(CommUtil.getCurrentLoginAccount());
					DoorbellListTask doorbellTask = new DoorbellListTask(null, "01b");
					doorbellTask.start();
					DoorbellListTask momitorTask = new DoorbellListTask(null, "01d");
					momitorTask.start();
				}
				//数据库版本升级检测
				checkDBUpdate();
				/*****************************************/
				
				//用户详情更新SysUser Info
				SysUserDetailTask sddTask = new SysUserDetailTask(context);
				sddTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
						
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
						cancelTaskCount += 1;
						goToHome();
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						failTaskCount += 1;
						goToHome();
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						if(arg != null && arg.length > 0){
							SysUserDaoImpl sudImpl = new SysUserDaoImpl(context);
							SysUser sysUser = (SysUser)arg[0];
							if (sysUser != null) {
								int enable = sysUser.getEnable();
								if (enable == 0) {
									JxshApp.showToast(context, context.getString(R.string.account_is_not_enable));
									return;
								}else{
									sudImpl.clear();
									sudImpl.insert((SysUser)arg[0],true);
								}
							}
						}
						// 跳转首页    10代表所有接口数据都已获取成功 
						succesTaskCount+=1;
						goToHome();

						/*************** customer data sync *************************/
						UpdateCustomerDataSyncTask ucdsTask = new UpdateCustomerDataSyncTask(context);
						final CustomerDataSyncHelper ucdsHelper = new CustomerDataSyncHelper(context);
						ucdsTask.addListener(new ITaskListener<ITask>() {

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
								if(arg != null && arg.length > 0){
									List<CustomerDataSync> dataList = (List<CustomerDataSync>)arg[0];
									for(CustomerDataSync cds : dataList) {
										String model = cds.getModel();
										String whereClause = parseWhereClause(cds.getActionFieldName());
										String[] whereArgs = parseWhereArgs(cds.getActionId());
										if(isWhereClauseAndArgsMatch(cds.getActionFieldName(), cds.getActionId()) &&
												!StringUtils.isEmpty(whereClause) && !CommUtil.isEmpty(whereArgs) ) {
											ucdsHelper.doCustomerDataSyncTask(model, whereClause, whereArgs);
										}else {
//											Logger.error(null, "delete recored error:" + cds.toString());
										}
									}
									ucdsHelper.close();
								}
								
								
								// 选择静脉纹文件
								JxshApp.setVeinUser(CommUtil.getCurrentLoginAccount());
								//用户资料更新Customer Info
								CustomerDetailTask cdTask = new CustomerDetailTask(context);
								cdTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(ITask task, Object arg) {
//										JxshApp.showLoading(
//												context,
//												CommDefines.getSkinManager().string(
//														R.string.yong_hu_xin_xi_geng_xin));
									}

									@Override
									public void onCanceled(ITask task, Object arg) {
//										JxshApp.closeLoading();
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
//										JxshApp.closeLoading();
										failTaskCount += 1;
										goToHome();
									}

									@Override
									public void onSuccess(ITask task, Object[] arg) {
//										JxshApp.closeLoading();
										/****** 用户详情更新 *********/
										if(arg != null && arg.length > 0){
											CustomerDaoImpl cdImpl = new CustomerDaoImpl(context);
											cdImpl.clear();
											cdImpl.insert((Customer)arg[0],true);
										}
										// 跳转首页    10代表所有接口数据都已获取成功 
										succesTaskCount +=1;

											goToHome();
						
									}

									@Override
									public void onProcess(ITask task, Object[] arg) {
										
									}
								});
								cdTask.start();
								
								/*****************************************/
								// 更新设备列表
								CustomerProductListTask cplTask = new CustomerProductListTask(
										context);
								cplTask.addListener(new ITaskListener<ITask>() {
									@Override
									public void onStarted(ITask task, Object arg) {
//										JxshApp.showLoading(
//												context,
//												CommDefines
//														.getSkinManager()
//														.string(R.string.she_bei_geng_xin_jian_ce));
									}

									@Override
									public void onCanceled(ITask task, Object arg) {
//										JxshApp.closeLoading();
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
//										JxshApp.closeLoading();
										failTaskCount += 1;
										goToHome();
									}

									@Override
									public void onSuccess(ITask task, Object[] arg) {
										/****** 设备信息更新 *********/
										if (arg != null && arg.length > 0) {
											List<CustomerProduct> customerProductList = (List<CustomerProduct>) arg[0];
											CommonMethod.updateCustomerProduct(
													context,
													customerProductList);
										}
										// 跳转首页    10代表所有接口数据都已获取成功 
										succesTaskCount+=1;

											goToHome();

										/***************/
										JxshApp.closeLoading();
										
									}

									@Override
									public void onProcess(ITask task,
											Object[] arg) {
										
									}
								});
								cplTask.start();
								
								/*****************************************/
								// 更新产品功能列表
								ProductFunListTask pflTask = new ProductFunListTask(
										context);
								pflTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(ITask task, Object arg) {
//										JxshApp.showLoading(
//												context,
//												CommDefines
//														.getSkinManager()
//														.string(R.string.she_bei_gong_neng_geng_xin_jian_ce));
									}

									@Override
									public void onCanceled(ITask task,
											Object arg) {
//										JxshApp.closeLoading();
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
//										JxshApp.closeLoading();
										failTaskCount += 1;
										goToHome();
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
										// 跳转首页    10代表所有接口数据都已获取成功 
										succesTaskCount+=1;
					
											goToHome();
						
										
									}

									@Override
									public void onProcess(ITask task,
											Object[] arg) {
										
									}
								});
								pflTask.start();
								
								/*****************************************/
								// 更新模式列表
								CustomerPatternListTask _cplTask = new CustomerPatternListTask(
										context);
								_cplTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(ITask task,
											Object arg) {
										// stub
//										JxshApp.showLoading(
//												context,
//												CommDefines
//														.getSkinManager()
//														.string(R.string.yong_hu_mo_shi_geng_xin_jian_ce));
									}

									@Override
									public void onCanceled(ITask task,
											Object arg) {
										// stub
//										JxshApp.closeLoading();
										cancelTaskCount += 2;
										goToHome();
									}

									@Override
									public void onFail(ITask task,
											Object[] arg) {
										// stub
//										JxshApp.closeLoading();
										failTaskCount += 2;
										goToHome();
									}

									@Override
									public void onSuccess(ITask task,
											Object[] arg) {
										// stub
										/****** 模式更新 *********/
										if (arg != null
												&& arg.length > 0) {
											List<CustomerPattern> customerPatternList = (List<CustomerPattern>) arg[0];
											CommonMethod
													.updateCustomerPattern(
															context,
															customerPatternList);
										}
										// 跳转首页    10代表所有接口数据都已获取成功 
										succesTaskCount+=1;
									
//											goToHome();
									
										
										/*****************************************/
										// 更新产品模式操作
										StringBuffer ids = null;
										CustomerPatternDaoImpl _cpDaoImpl = new CustomerPatternDaoImpl(
												context);
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
												context, ids
														.toString());
										ppolTask.addListener(new ITaskListener<ITask>() {

											@Override
											public void onStarted(
													ITask task,
													Object arg) {
												// method stub
//												JxshApp.showLoading(
//														context,
//														CommDefines
//																.getSkinManager()
//																.string(R.string.mo_shi_gong_neng_geng_xin_jian_ce));
											}

											@Override
											public void onCanceled(
													ITask task,
													Object arg) {
												// method stub
//												JxshApp.closeLoading();
												cancelTaskCount += 1;
												goToHome();
											}

											@Override
											public void onFail(
													ITask task,
													Object[] arg) {
												// method stub
//												JxshApp.closeLoading();
												failTaskCount += 1;
												goToHome();
											}

											@Override
											public void onSuccess(
													ITask task,
													Object[] arg) {
												// method stub
												/****** 模式操作功能更新 *********/
												if (arg != null
														&& arg.length > 0) {
													List<ProductPatternOperation> productPatternOperationList = (List<ProductPatternOperation>) arg[0];
													CommonMethod
															.updateProductPatternOperationList(
																	context,
																	productPatternOperationList);
												}
												// 跳转首页    10代表所有接口数据都已获取成功 
												succesTaskCount+=1;
												
													goToHome();
												
//												JxshApp.closeLoading();
												
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
											ITask task,
											Object[] arg) {
										
									}
								});
								_cplTask.start();
								
								/*****************************************/
								//更新产品功能明细
								FunDetailListTask fdlTask = new FunDetailListTask(context);
								fdlTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(
											ITask task,
											Object arg) {
									}

									@Override
									public void onCanceled(
											ITask task,
											Object arg) {
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(
											ITask task,
											Object[] arg) {
										failTaskCount += 1;
										goToHome();
									}

									@Override
									public void onSuccess(
											ITask task,
											Object[] arg) {
//										JxshApp.closeLoading();
										if (arg != null && arg.length > 0) {
											List<FunDetail> funDetailList = (List<FunDetail>)arg[0];
											CommonMethod.updateFunDetailList(context, funDetailList);
										}
										// 跳转首页    10代表所有接口数据都已获取成功 
										succesTaskCount+=1;
											goToHome();
									}

									@Override
									public void onProcess(
											ITask task,
											Object[] arg) {
										
									}
								});
								fdlTask.start();
								
								//产品功能明细配置参数列表
								FunDetailConfigTask fdcTask = new FunDetailConfigTask(context);
								fdcTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(
											ITask task,
											Object arg) {
									}

									@Override
									public void onCanceled(ITask task,Object arg) {
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(
											ITask task,
											Object[] arg) {
										failTaskCount += 1;
										goToHome();
									}

									@Override
									public void onSuccess(
											ITask task,
											Object[] arg) {
										if (arg != null && arg.length > 0) {
											List<FunDetailConfig> funDetailConfigList = (List<FunDetailConfig>)arg[0];
											for(FunDetailConfig fc : funDetailConfigList) {
												Logger.error("tangl", fc.toString());
											}
											
											CommonMethod.updateFunDetailConfigList(context, funDetailConfigList);
										}
										// 跳转首页    10代表所有接口数据都已获取成功 
										succesTaskCount+=1;
										
											goToHome();
										
									}

									@Override
									public void onProcess(
											ITask task,
											Object[] arg) {
										
									}
								});
								fdcTask.start();
								
//								/*****************************************/
								// 更新云设置
								SyncCloudSettingTask csTask = new SyncCloudSettingTask(context, "get", null, null, null);
								csTask.addListener(new ITaskListener<ITask>() {
									@Override
									public void onStarted(ITask task, Object arg) {
										Logger.debug(null, "onstart...");
									}

									@Override
									public void onCanceled(ITask task, Object arg) {
										Logger.debug(null, "onCanceled...");
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
										Logger.debug(null, "onFail...");
										failTaskCount += 1;
										goToHome();
									}

									@Override
									public void onSuccess(ITask task, Object[] arg) {
										Logger.debug(null, "onSuccess...");
										if (arg != null && arg.length > 0) {
											try {
												List<CloudSetting> csList = (List<CloudSetting>) arg[0];
												CommonMethod.updateCloudSettings(context, csList);
											} catch (Exception e) {
												Logger.error("loginactivity.tangl", "class cast error");
												e.printStackTrace();
											}
										}
										
										// 跳转首页    10代表所有接口数据都已获取成功 
										succesTaskCount+=1;
										
											goToHome();
										
									}

									@Override
									public void onProcess(ITask task, Object[] arg) {
										Logger.debug(null, "onProcess...");
									}
								});
								csTask.start();
								/*****************************************/
								//更新定时任务列表
								CustomerTimerListTask ctlTask = new CustomerTimerListTask(context);
								ctlTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(ITask task, Object arg) {
										Logger.debug("Yang", "task list");
									}

									@Override
									public void onCanceled(ITask task, Object arg) {
										Logger.debug(null, "onCanceled...");
										cancelTaskCount += 2;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
										failTaskCount += 2;
										goToHome();
										Logger.debug("Yang", "task onFail...");
									}
									@Override
									public void onSuccess(ITask task, Object[] arg) {
										//////////////
										if (arg != null && arg.length > 0) {
											List<CustomerTimer> ctList = (List<CustomerTimer>) arg[0];
											CommonMethod.updateCustomerTimer(context, ctList);
										}
										succesTaskCount += 1;
										/*****************************************/
										//更新定时任务操作列表
										StringBuffer taskIds = null;
										CustomerTimerTaskDaoImpl _cttDaoImpl = new CustomerTimerTaskDaoImpl(context);
										List<CustomerTimer> _ctList = _cttDaoImpl.find();
										if (_ctList != null) {
											taskIds = new StringBuffer();
											for (int i = 0; i < _ctList.size(); i++) {
												CustomerTimer _ct = _ctList.get(i);
												if (_ct != null) {
													if (i<_ctList.size() - 1) {
														taskIds.append(_ct.getTaskId()).append(",");
													}else{
														taskIds.append(_ct.getTaskId());
													}
												}
											}
										}
										TimerTaskOperationListTask ttoplTask = new 
												TimerTaskOperationListTask(
														context, taskIds.toString());
										ttoplTask.addListener(new ITaskListener<ITask>() {

											@Override
											public void onStarted(ITask task, Object arg) {
												Logger.debug("Yang", "Operation list");
											}

											@Override
											public void onCanceled(ITask task,
													Object arg) {
												cancelTaskCount += 1;
												goToHome();
											}

											@Override
											public void onFail(ITask task, Object[] arg) {
												failTaskCount += 1;
												goToHome();
												Logger.debug("Yang", "Operation onFail...");
											}

											@Override
											public void onSuccess(ITask task,
													Object[] arg) {
												
												if (arg != null&& arg.length > 0) {
													List<TimerTaskOperation> ttoList = (List<TimerTaskOperation>) arg[0];
													CommonMethod.updateTimerTaskOperationList(
															context, ttoList);
												}
												// 跳转首页    10代表所有接口数据都已获取成功 
												succesTaskCount += 1;
											
													goToHome();
												
											}

											@Override
											public void onProcess(ITask task,
													Object[] arg) {
												
											}
										});
										ttoplTask.start();
									}
									
									@Override
									public void onProcess(ITask task, Object[] arg) {
										
									}
								});
								ctlTask.start();
								
								/*************** update customer area *************************/
								Logger.warn(null, "更新用户区域设置");
								UpdateCustomerAreaTask ucaTask = new UpdateCustomerAreaTask(context, -1);
								ucaTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(ITask task, Object arg) {
									}

									@Override
									public void onCanceled(ITask task, Object arg) {
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
										failTaskCount += 1;
										goToHome();
									}

									@Override
									public void onSuccess(ITask task, Object[] arg) {
										if(arg != null && arg.length > 0){
											List<CustomerArea> cas = (List<CustomerArea>)arg[0];
											for(CustomerArea ca : cas) {
												try {
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
										succesTaskCount += 1;
										
											goToHome();
										
									}

									@Override
									public void onProcess(ITask task, Object[] arg) {
									}
								});
								ucaTask.start();
								/*************** update customer area end *************************/
								
								/*************** update customer product area *************************/
								Logger.warn(null, "更新设备用户区域设置");
								UpdateCustomerProductAreaTask upcaTask = new UpdateCustomerProductAreaTask(context);
								final CustomerProductAreaDaoImpl cpaDao = new CustomerProductAreaDaoImpl(context);
								upcaTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(ITask task, Object arg) {
									}

									@Override
									public void onCanceled(ITask task, Object arg) {
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
										failTaskCount += 1;
										goToHome();
									}

									@Override
									public void onSuccess(ITask task, Object[] arg) {
										if(arg != null && arg.length > 0){
											List<CustomerProductArea> cpas = (List<CustomerProductArea>)arg[0];
											for(CustomerProductArea cpa : cpas) {
												try {
													cpaDao.saveOrUpdate(cpa);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
										succesTaskCount += 1;
										
											goToHome();
										
									}

									@Override
									public void onProcess(ITask task, Object[] arg) {
									}
								});
								upcaTask.start();
								/*************** update customer product area end *************************/
								
								Logger.warn(null, "更新产品注册信息");
								UpdateProductRegisterTask uprTask = new UpdateProductRegisterTask(context);
								final ProductRegisterDaoImpl uprDao = new ProductRegisterDaoImpl(context);
								uprTask.addListener(new ITaskListener<ITask>() {

									@Override
									public void onStarted(ITask task, Object arg) {
									}

									@Override
									public void onCanceled(ITask task, Object arg) {
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
										failTaskCount += 1;
										goToHome();
									}

									@Override
									public void onSuccess(ITask task, Object[] arg) {
										if(arg != null && arg.length > 0){
											List<ProductRegister> prs = (List<ProductRegister>)arg[0];
											for(ProductRegister pr : prs) {
												try {
													uprDao.saveOrUpdate(pr);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
										succesTaskCount += 1;
										
										goToHome();
										
									}

									@Override
									public void onProcess(ITask task, Object[] arg) {
									}
								});
								uprTask.start();
								/*************** update product regiest info end *************************/
								Logger.warn(null, "更新遥控配置信息2");
								UpdateProductRemoteConfigTask uprcTask = new UpdateProductRemoteConfigTask(con);
								final ProductRemoteConfigDaoImpl uprcDao = new ProductRemoteConfigDaoImpl(con);
								uprcTask.addListener(new TaskListener<ITask>() {

									@Override
									public void onCanceled(ITask task, Object arg) {
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
										Logger.warn(null, "onFail");
										goToHome();
									}

									@Override
									public void onSuccess(ITask task, Object[] arg) {
										Logger.warn(null, "onSuccess");
										if(arg != null && arg.length > 0){
											List<ProductRemoteConfig> prs = (List<ProductRemoteConfig>)arg[0];
											for(ProductRemoteConfig pr : prs) {
												try {
													Logger.debug(null, pr.toString());
													uprcDao.saveOrUpdate(pr);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
										succesTaskCount += 1;
										
										goToHome();
										
									}

									@Override
									public void onProcess(ITask task, Object[] arg) {
									}
								});
								uprcTask.start();
								
								Logger.warn(null, "更新遥控配置信息");
								UpdateProductRemoteConfigFunTask uprcfTask = new UpdateProductRemoteConfigFunTask(con);
								final ProductRemoteConfigFunDaoImpl uprcfDao = new ProductRemoteConfigFunDaoImpl(con);
								uprcfTask.addListener(new TaskListener<ITask>() {

									@Override
									public void onCanceled(ITask task, Object arg) {
										cancelTaskCount += 1;
										goToHome();
									}

									@Override
									public void onFail(ITask task, Object[] arg) {
										goToHome();
									}

									@Override
									public void onSuccess(ITask task, Object[] arg) {
										if(arg != null && arg.length > 0){
											List<ProductRemoteConfigFun> prs = (List<ProductRemoteConfigFun>)arg[0];
											for(ProductRemoteConfigFun pr : prs) {
												try {
													Logger.debug(null, pr.toString());
													uprcfDao.saveOrUpdate(pr);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
										succesTaskCount += 1;
										
										goToHome();
									}
								});
								uprcfTask.start();
								
							}

							@Override
							public void onProcess(ITask task, Object[] arg) {
							}
						});
						ucdsTask.start();
						/*************** customer data sync end *************************/
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
					}
				});
				sddTask.start();
				/*****************************************/
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		loginTask.start();//开始登录
	
	}
	
	public static void stopLogin() {
		if (loginTask == null) {
			JxshApp.instance.onDestroy();
		} else {
			loginTask.cancel();
			loginTask = null;
		}
	}
	
	/**
	 * 跳转首页
	 */
	private static void goToHome() {
		/***跳转策略**********/
		Logger.debug(null, "task count:"+cancelTaskCount+failTaskCount+succesTaskCount);
		if(cancelTaskCount+failTaskCount+succesTaskCount != 16){//未达到16项
			return;
		}else if(cancelTaskCount > 0 || failTaskCount > 0){
			failTaskCount= 0;
			cancelTaskCount = 0;
			succesTaskCount = 0;
			if (loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
			//载入失败 跳转到登录界面 重新登录
			context.startActivity(new Intent(context,LoginActivity.class));
			return;
		}
		failTaskCount= 0;
		cancelTaskCount = 0;
		succesTaskCount = 0;
		taskCount = 0;
		String account = CommUtil.getCurrentLoginAccount();
		if (!TextUtils.isEmpty(account)) {
			SharedDB.saveBooleanDB(account, ControlDefine.KEY_ISLOADING, true);
			SharedDB.saveBooleanDB(account, ControlDefine.KEY_OFF_LINE_MODE, false);
		}
		/***zj:广告假数据测试********/
		List<Advertising> adList = new ArrayList<Advertising>();
		Advertising _ad = new Advertising();
		_ad.setPath("ad/ad/pic1.png");
		adList.add(_ad);
		_ad = new Advertising();
		_ad.setPath("ad/ad/pic2.png");
		adList.add(_ad);
		_ad = new Advertising();
		_ad.setPath("ad/ad/pic3.png");
		ADManager.instance().setAdvertisingList(adList);
		/***************************/
		if (loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
		
		if(!(context instanceof VeinLogin) && !(context instanceof VoiceLoginActivity)) {
			JxshApp.showToast(context, "已切换在线模式");
		}
		JxshApp.getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				JxshApp.instance.initBellController();// 启动门铃
			}
		}, 1500);
		//广播更新 刷新页面数据
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_UPDATE_MODE_MESSAGE, null);
		BroadcastManager.sendBroadcast(VeinLogin.ACTION_LOGINED, null);
		// 跳转首页
//		Intent intent = new Intent(context,HomeActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		context.startActivity(intent);
	}
	
	private static String parseWhereClause(String whereStr) {
		if(!StringUtils.isEmpty(whereStr)) {
			String[] wheres = whereStr.split(",");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < wheres.length; i++) {
				if(i == wheres.length - 1 ) {
					sb.append(" " + wheres[i] + "=? ");
				}else {
					sb.append(" " + wheres[i] + "=? and ");
				}
			}
			return sb.toString();
		}
		return "";
	}
	
	private static String[] parseWhereArgs(String whereArgs) {
		if(!StringUtils.isEmpty(whereArgs)) {
			return whereArgs.split(",");
		}
		return null;
	}
	
	private static boolean isWhereClauseAndArgsMatch(String WhereClause, String whereArgs) {
		if(!StringUtils.isEmpty(WhereClause) && !StringUtils.isEmpty(whereArgs)) {
			String[] cluses =  WhereClause.split(",");
			String[] args = whereArgs.split(",");
			if(cluses != null && args != null && cluses.length == args.length) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 数据库版本升级检测（跨版本升级策略）
	 */
		private static void checkDBUpdate() {
//			final String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,"");
//			final String secretkey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,ControlDefine.KEY_SECRETKEY,"");
//			final String nickyName = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,ControlDefine.KEY_NICKNAME,"");
//							DBHelper.setDbName(_account);
			
			DBHelper dBHelper = new DBHelper(context);
//			dBHelper.addDBHelperListener(new DBHelperListener() {
//				@Override
//				public void onUpgrade(int oldVersion, int newVersion) {
//					JxshApp.showToast(LoginActivity.this, CommDefines.getSkinManager().string(R.string.update_db));
//					SharedDB.removeAct(JxshApp.getContext(), _account);
//					SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
//							ControlDefine.KEY_ACCOUNT, _account);
//					SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
//							ControlDefine.KEY_SECRETKEY,secretkey);
//					// save key to personal property
//					SharedDB.saveStrDB(_account,
//							ControlDefine.KEY_SECRETKEY,secretkey);
//					//存入别名：
//					SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
//							ControlDefine.KEY_NICKNAME,nickyName);
//				}
	//
//			});
//			dBHelper.addDBHelperListener(new DBHelperListener() {
//				
//				@Override
//				public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//					// TODO Auto-generated method stub
//					JxshApp.showToast(context, CommDefines.getSkinManager().string(R.string.update_db));
//					int upgradeVersion  = oldVersion; 
//					
////					if(11 == upgradeVersion){//11升12策略
////						new From11To12DBupdateStrategy().updateDBStrategy(db);
////						upgradeVersion = 12;
////					}
////					if(12 == upgradeVersion){//12升13策略
////						upgradeVersion = 13;
////					}
//					if (upgradeVersion != newVersion) { //不符合升级策略，清空数据库表 
//						new ClearDBupdateStrategy().updateDBStrategy(db);
//				    }  
//				}
//			});
			dBHelper.getWritableDatabase().close();
		}
}
