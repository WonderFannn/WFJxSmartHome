package com.jinxin.jxsmarthome.ui.adapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.DeleteCustomerPatternTask;
import com.jinxin.datan.net.command.ProductPatternOperationListTask;
import com.jinxin.datan.net.command.VoiceIdentifyTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.AmendModeNewActivity;
import com.jinxin.jxsmarthome.activity.ModeDetailActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OfflineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.dialog.ListDialog;
import com.jinxin.jxsmarthome.ui.dialog.ListDialogOnItemClickListener;
import com.jinxin.jxsmarthome.ui.widget.MessageBox;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.DateUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 模式组合gridview适配器
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ModeGridAdapter extends BaseAdapter {

	private List<CustomerPattern> customerPatternList;
	private Context con;
	private String textToSpeakBefore;
	private String textToSpeakEnd;

	public ModeGridAdapter(Context con,
			List<CustomerPattern> customerPatternList) {
		this.con = con;
		if (customerPatternList!=null) {
			Collections.sort(customerPatternList, new Comparator<CustomerPattern>() {
				
				@Override
				public int compare(CustomerPattern lhs, CustomerPattern rhs) {
					int i = lhs.getStayTop().compareTo(rhs.getStayTop());
					if (i == 0) {
						return lhs.getClickCount().compareTo(rhs.getClickCount());
					}
					return i;
				}
			});
		}
//		Collections.reverse(customerPatternList);
		List<CustomerPattern> tempList = new ArrayList<CustomerPattern>();
		if (customerPatternList!=null) {
			for (int i = customerPatternList.size()-1; i > -1; i--) {
				tempList.add(customerPatternList.get(i));
			}
			this.customerPatternList = tempList;
		}
	}


	@Override
	public int getCount() {
		return this.customerPatternList == null ? 0 : customerPatternList
				.size();
	}

	@Override
	public Object getItem(int position) {
		return this.customerPatternList == null ? null : customerPatternList
				.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridHolder gridHolder;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(
					R.layout.mode_fragment_item);
			gridHolder = new GridHolder(convertView);
			convertView.setTag(gridHolder);
		} else {
			gridHolder = (GridHolder) convertView.getTag();
		}
		final int pos = position;
		final CustomerPattern _customerPattern = this.customerPatternList
				.get(position);
		
		if (!TextUtils.isEmpty(_customerPattern.getMemo())) {
			gridHolder.scene_comments.setText(_customerPattern.getMemo());
		}else{
			gridHolder.scene_comments.setText("");
		}
		
		if (TextUtils.isEmpty(_customerPattern.getIcon())) {
			gridHolder.itemImg.setImageBitmap(CommDefines.getSkinManager()
					.getAssetsBitmap("images/img/upload/default.png"));
		} else {
			String iconPath = _customerPattern.getIcon();
			if (iconPath.startsWith("/")) {
				iconPath = iconPath.substring(1);
			}
			Bitmap _bitmap = CommDefines.getSkinManager().getAssetsBitmap(
					iconPath);
			if (_bitmap != null) {
				gridHolder.itemImg.setImageBitmap(_bitmap);
			} else {
				gridHolder.itemImg.setImageBitmap(CommDefines.getSkinManager()
						.getAssetsBitmap("images/img/upload/default.png"));
			}
		}
		gridHolder.imageView_stop.setVisibility(_customerPattern.getStatus()
				.equals("0") ? View.VISIBLE : View.INVISIBLE);
		gridHolder.itemName.setText(_customerPattern.getPaternName());
		
		if ("0".equals(_customerPattern.getStatus())) {
			gridHolder.ll_button.setFocusable(false);
		}else{
			gridHolder.ll_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (JxshApp.instance.isClinkable) {
						JxshApp.showToast(con, CommDefines.getSkinManager().string(R.string.take_a_break));
						Logger.debug(null, "上次操作未完成，请稍后");
						return;
					}
					
					int _patternId = _customerPattern.getPatternId();
					int _count = _customerPattern.getClickCount()+1;
					ProductPatternOperationDaoImpl _ppoDaoImpl = new ProductPatternOperationDaoImpl(
							con);
					List<ProductPatternOperation> _ppoList = _ppoDaoImpl.find(null,
							"patternId=?",
							new String[] { Integer.toString(_patternId) }, null,
							null, null, null);
					System.out.println("====sss"+_ppoList.toString());
					if (_ppoList != null) {
//						textToSpeakBefore = _customerPattern.getTtsStart();
//						textToSpeakEnd = _customerPattern.getTtsEnd();
						textToSpeakBefore = ttsTextToString(_customerPattern.getTtsStart().replaceAll("；；", ";;"));
						textToSpeakEnd = ttsTextToString(_customerPattern.getTtsEnd().replaceAll("；；", ";;"));
						
						if (TextUtils.isEmpty(textToSpeakBefore) || NetworkModeSwitcher.useOfflineMode(con) ) {
							if (_ppoList.size() > 0) {
								operatePattern(_ppoList);
							}else{
								if (!TextUtils.isEmpty(textToSpeakEnd)) {
									setVoiceIdentifySwitch(textToSpeakEnd, null);//发送结束开始语音
								}
							}
							System.out.println(textToSpeakBefore+"====sss结束"+textToSpeakEnd);
						}else{
							System.out.println("===开始=sss"+_ppoList.toString());
							setVoiceIdentifySwitch(textToSpeakBefore, _ppoList);//发送模式开始语音
						}
						
						customerPatternList.get(pos).setClickCount(_count);
						CustomerPatternDaoImpl _cpdImpl = new CustomerPatternDaoImpl(con);
						_cpdImpl.update(customerPatternList.get(pos));
					}
					
				}
			});
		}
		gridHolder.ll_button
		.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				final ListDialog _listDialog = new ListDialog(con,
						CommDefines.getSkinManager().stringArray(
								R.array.mode_menu));
				_listDialog
				.setOnItemClickListener(new ListDialogOnItemClickListener() {
					
					@Override
					public void onItemClick(Dialog dialog,
							View view, int id) {
						switch (id) {
						/***********通过模式 ID 控制模式******/
						case 0:
							String _cmd = getModeCMDContent(String.valueOf(_customerPattern.getPatternId()));
							List<byte[]> cmds = CommonMethod.createCmdListWithLength(CommUtil.getMainAccount(), _cmd);
							OnlineCmdSenderLong sender = new OnlineCmdSenderLong(con, cmds);
							TaskListener<ITask> listener = new TaskListener<ITask>(){

								@Override
								public void onSuccess(ITask task, Object[] arg) {
									super.onSuccess(task, arg);
									Logger.debug(null, "mode onSuccess");
								}

								@Override
								public void onAllSuccess(ITask task,
										Object[] arg) {
									super.onAllSuccess(task, arg);
									Logger.debug(null, "mode onAllSuccess");
								}
								
							};
							sender.addListener(listener);
							sender.send();
							break;
							/****************END*************/
						case 1:
							//置顶
							CustomerPatternDaoImpl _cpdImpl = new CustomerPatternDaoImpl(con);
							List<CustomerPattern> cpList= _cpdImpl.find(null, null,
									null, null, null, "stayTop", null);
							if (cpList == null) {
								return;
							}
							int topId = cpList.get(cpList.size()-1).getStayTop() + 1;
							_customerPattern.setStayTop(topId);
							_cpdImpl.update(_customerPattern);
							BroadcastManager.sendBroadcast(
									BroadcastManager.ACTION_UPDATE_MODE_MESSAGE,
									null);
							break;
						case 2: 
							// 修改
							if (CommUtil.isSubaccount()) {
								JxshApp.showToast(con, "子账号不能进行模式修改操作");
								return;
							}
							if(NetworkModeSwitcher.useOfflineMode(con)) {
								JxshApp.showToast(con,  CommDefines.getSkinManager()
										.string(R.string.li_xian_cao_zuo_tips));
								return;
							}
							Intent _intent = new Intent(con,
									AmendModeNewActivity.class);
							_intent.putExtra("patternId",
									_customerPattern
									.getPatternId());
							_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							con.startActivity(_intent);
							
							break;
						case 3:
							//查看
							Intent intent = new Intent(con,
									ModeDetailActivity.class);
							intent.putExtra("patternId",
									_customerPattern
									.getPatternId());
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							con.startActivity(intent);
							break;
						case 4:
							// 删除
							if (CommUtil.isSubaccount()) {
								JxshApp.showToast(con, "子账号不能进行模式修改操作");
								return;
							}
							if(NetworkModeSwitcher.useOfflineMode(con)) {
								JxshApp.showToast(con,  CommDefines.getSkinManager()
										.string(R.string.li_xian_cao_zuo_tips));
								return;
							}
							if (_customerPattern
									.getPaternType()
									.equals("1")) {
								JxshApp.showToast(
										con,
										CommDefines
										.getSkinManager()
										.string(R.string.xi_tong_mo_ren_mo_shi_bu_neng_shan_chu));
							} else {
								final MessageBox mb = new MessageBox(
										con,
										CommDefines
										.getSkinManager()
										.string(R.string.delete),
										CommDefines
										.getSkinManager()
										.string(R.string.shi_fou_shan_chu),
										MessageBox.MB_OK
										| MessageBox.MB_CANCEL);
								mb.setButtonText("确定", null);
								mb.show();
								mb.setOnDismissListener(new OnDismissListener() {
									@Override
									public void onDismiss(
											DialogInterface dialog) {
										// method stub
										switch (mb.getResult()) {
										case MessageBox.MB_OK:
											DeleteCustomerPatternTask dcpTask = new DeleteCustomerPatternTask(
													con,
													_customerPattern
													.getPatternId());
											dcpTask.addListener(new ITaskListener<ITask>() {
												
												@Override
												public void onStarted(
														ITask task,
														Object arg) {
													JxshApp.showLoading(
															con,
															CommDefines
															.getSkinManager()
															.string(R.string.qing_qiu_chu_li_zhong));
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
													JxshApp.closeLoading();
													// 本地数据删除
													deleteModeFromDB(_customerPattern
															.getPatternId());
													deleteOldModeFunFromDB(_customerPattern
															.getPatternId());
													BroadcastManager
													.sendBroadcast(
															BroadcastManager.ACTION_UPDATE_MODE_MESSAGE,
															null);
												}
												
												@Override
												public void onProcess(
														ITask task,
														Object[] arg) {
													
												}
											});
											dcpTask.start();
											break;
										}
									}
								});
							}
							break;
						case 5:
							// 取消
							break;
						}
						_listDialog.dismiss();
					}
					
				});
				_listDialog.show();
				return true;
			}
		});
		return convertView;
	}

	class GridHolder {
		ImageView itemImg;
		ImageView imageView_stop;
		TextView itemName;
		TextView scene_comments;
		LinearLayout ll_button;

		public GridHolder(View view) {
			itemImg = (ImageView) view.findViewById(R.id.mode_item_bg);
			imageView_stop = (ImageView) view.findViewById(R.id.imageView_stop);
			itemName = (TextView) view.findViewById(R.id.mode_item_name);
			scene_comments = (TextView) view.findViewById(R.id.scene_comments);
			ll_button = (LinearLayout) view.findViewById(R.id.ll_button);
		}
	}

	/**
	 * 删除原模式旧的操作
	 */
	private void deleteOldModeFunFromDB(int patternId) {
		if (patternId == -1)
			return;
		ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
				con);
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

	/**
	 * 删除该模式
	 * 
	 * @param patternId
	 */
	private void deleteModeFromDB(int patternId) {
		CustomerPatternDaoImpl cpDaoImpl = new CustomerPatternDaoImpl(con);
		cpDaoImpl.delete(patternId);
	}
	
	/**
	 * 发送指令
	 * @param ppsList
	 */
	TaskListener<ITask> listener;
	private void operatePattern(final List<ProductPatternOperation> ppsList) {
		
		listener = new TaskListener<ITask>(){
			
			@Override
			public void onStarted(ITask task, Object arg) {
				super.onStarted(task, arg);
				Logger.debug(null, "mode onAllonStarted"+"----"+DateUtil.convertLongToStr1(System.currentTimeMillis()));
			}

			@Override
			public void onAllSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "mode onAllSuccess"+"----"+DateUtil.convertLongToStr1(System.currentTimeMillis()));
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				if (!"-1".equals(resultObj.validResultInfo))
					JxshApp.showToast(con, "操作成功");
				else
					JxshApp.showToast(con, CommDefines
							.getSkinManager()
							.string(R.string.mode_contorl_fail_any));
				//释放按钮权限
				JxshApp.instance.isClinkable = false;
				if (TextUtils.isEmpty(textToSpeakEnd) || NetworkModeSwitcher.useOfflineMode(con)) {
					return;
				}
				setVoiceIdentifySwitch(textToSpeakEnd, null);//发送模式结束语音
			}
			
			@Override
			public void onAnyFail(ITask task, Object[] arg) {
				Logger.debug(null, "mode onAnyFail"+"----"+DateUtil.convertLongToStr1(System.currentTimeMillis()));
				//释放按钮权限
				JxshApp.instance.isClinkable = false;
				JxshApp.showToast(con, CommDefines
						.getSkinManager()
						.string(R.string.mode_contorl_fail_any));
				if (TextUtils.isEmpty(textToSpeakEnd) || NetworkModeSwitcher.useOfflineMode(con)) {
					return;
				}
				setVoiceIdentifySwitch(textToSpeakEnd, null);//发送模式结束语音
			}
			
			
		};
		// offline
		if(NetworkModeSwitcher.useOfflineMode(con)) {
			Logger.debug(null, "offline operation pattern");
			AsyncExecutor asyncExecutor = new AsyncExecutor();
			asyncExecutor.execute(new AsyncExecutor.AsyncTask<List<Command>>() {
				@Override
				public List<Command> doInBackground() {
					OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
					List<Command> listCmd = cmdGenerator.generateProductPatternOperationCommand(con, ppsList, null);
					return listCmd;
				}
				public void onPostExecute(List<Command> data) {
					OfflineMulitGatewayCmdSender cmdSender = new OfflineMulitGatewayCmdSender(con, data);
					cmdSender.send();
				};
			});
		// online
		}else {
			AsyncExecutor asyncExecutor = new AsyncExecutor();
			asyncExecutor.execute(new AsyncExecutor.AsyncTask<List<Command>>() {
				@Override
				public List<Command> doInBackground() {
					OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
					List<Command> listCmd = cmdGenerator.generateProductPatternOperationCommand(con, ppsList, null);
					return listCmd;
				}
				public void onPostExecute(List<Command> data) {
					OnlineMulitGatewayCmdSender cmdSender = new OnlineMulitGatewayCmdSender(con, data);
					cmdSender.addListener(listener);
					cmdSender.send();
				};
			});
		}
		//防止多次发送相同指令
		JxshApp.instance.isClinkable = true;
	}
	
	/**
	 * 发送语音前的准备
	 */
	private void setVoiceIdentifySwitch(final String txtToSpeech, final List<ProductPatternOperation> _ppoList) {
		sendVoiceCmd(txtToSpeech, _ppoList);
		/**** 2016.3.16  取消接口请求功放切换操作步骤 ***/
//		String amplifierWhId = getAmplifierWhId();
//		if(amplifierWhId != null) {
//			ProductRegister productRegister = AppUtil.getProductRegisterByWhId(con, amplifierWhId);
//			if(productRegister != null) {
//				VoiceIdentifyTask task = new VoiceIdentifyTask(con, "02", productRegister.getGatewayWhId());
//				task.addListener(new TaskListener<ITask>() {
//					@Override
//					public void onSuccess(ITask task, Object[] arg) {
//						super.onSuccess(task, arg);
//						sendVoiceCmd(txtToSpeech, _ppoList);
//					}
//					@Override
//					public void onFail(ITask task, Object[] arg) {
//						super.onFail(task, arg);
//						sendVoiceCmd(txtToSpeech, _ppoList);
//					}
//				});
//				task.start();
//			}
//		}
	}
	
	private String getModeCMDContent(String id){
		if (TextUtils.isEmpty(id)) {
			JxshApp.showToast(con, "mode id is null");
			return null;
		}
		String cmd = "";
		String content = "mode"+id;
		cmd = "|gate"+getCmdLength(id)+content;
		return cmd;
	}
	
	private String getCmdLength(String content){
		String str = content;
		String result = "";
		if (str.length() < 10) {
			result = "0"+str.length();
		}else{
			result = String.valueOf(str.length());
		}
		return result;
	}
	
	/**
	 * 发送文本，通过语音播放
	 * @param sendText 发送文本
	 */
	private void sendVoiceCmd(final String sendText, final List<ProductPatternOperation> _ppoList) {
		TaskListener<ITask> _listener = new TaskListener<ITask>(){
			
			@Override
			public void onFinish() {
				if (_ppoList != null && _ppoList.size() > 0) {
					operatePattern(_ppoList);
				}
				
				//如果无任何操作，且设置过结束语音，播放结束语音
				if (_ppoList != null && _ppoList.size() < 1 && !sendText.equals(textToSpeakEnd)) {
					if (!TextUtils.isEmpty(textToSpeakEnd)) {
						setVoiceIdentifySwitch(textToSpeakEnd, null);
					}
				}
			}

		};
		ProductFun productFun = AppUtil.getSingleProductFunByFunType(con,
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		FunDetail funDetail = AppUtil.getFunDetailByFunType(con,
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		
		if (productFun == null || funDetail == null || sendText == null) {
			Logger.warn(null, "paramter error, cancel send voice");
			return;
		}
		
		String[] inputAndAddr = getInput4Voice();
		String input = inputAndAddr[0] == null ? "input3" : inputAndAddr[0];
		String addr = TextUtils.isEmpty(inputAndAddr[1]) ? "" : inputAndAddr[1];
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_TEXT, sendText);
		params.put(StaticConstant.OPERATE_INPUT_SET, input);
		params.put(StaticConstant.PARAM_ADDR, addr);
		
		//productFun表中午对应网关设备，此处将FunType设置为网关设备
		productFun.setFunType(ProductConstants.FUN_TYPE_GATEWAY);
		
		if(NetworkModeSwitcher.useOfflineMode(con)) {
			OfflineCmdManager.generateCmdAndSend(con, productFun,
				funDetail, params);
			return;
		}
		
		List<byte[]> cmds = CommonMethod.productFunToCMD(con, productFun,
				funDetail, params);
		
		if (cmds == null || cmds.size() < 1) return;
		
		OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(null, cmds);
		cmdSender.addListener(_listener);
		cmdSender.send();
	}
		
	/**
	 * 获取合成语音的输入源设置
	 * @return
	 */
	private String[] getInput4Voice() {
		String[] ret = new String[2];
		ProductRegister pr = AppUtil.getProductRegisterByWhId(con, getAmplifierWhId());
		FunDetailConfigDaoImpl dao = new FunDetailConfigDaoImpl(con);
		if (pr != null) {
			List<FunDetailConfig> fdcs = dao.find(null, "whId=? and funType=?", 
					new String[]{pr.getGatewayWhId(), ProductConstants.FUN_TYPE_GATEWAY}, 
					null, null, null, null);
			if(fdcs.size() > 0) {
				String jsonStr = fdcs.get(0).getParams();
				if(jsonStr != null) {
					try {
						JSONObject jb = new JSONObject(jsonStr);
						ret[0] = jb.getString("input");
						ret[1] = jb.getString("addr");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		return ret;
	}
	
	/**
	 * 获取功放设备的whid（优先重音乐设置中获取，如果音乐中未设置，取任意一个功放设备的whid）
	 * @return
	 */
	private String getAmplifierWhId() {
		String whid = SharedDB.loadStrFromDB(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_WHID, null);
		if(whid == null) {
			ProductFun amplifierDevice = AppUtil.getSingleProductFunByFunType(con, 
					ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
			if(amplifierDevice != null) {
				whid = amplifierDevice.getWhId();
			}
		}
		return whid;
	}
	
	
	/**
	 * 多条语音文字合成一条
	 * @param speakText
	 * @return
	 */
	private String ttsTextToString(String speakText){
		String text = "";
		if (TextUtils.isEmpty(speakText)) {
			return text;
		}
		String[] arr = speakText.split(";;");
		if (arr.length > 1) {
			StringBuffer sb = new StringBuffer("#" + arr.length + "#");
			for (int i = 0; i < arr.length; i++) {
				try {
					sb.append(intToString(arr[i].getBytes("utf-8").length));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < arr.length; i++) {
				sb.append(arr[i]);
			}
			text = sb.toString();
		}else {
			text = speakText;
		}
		return text;
	}
	
	private String intToString(int length){
		String _r = "";
		if (length < 10) {
			_r = "00" + length;
		} else if (length >= 10 && length < 100) {
			_r = "0" + length;
		} else if (length >= 100 && length < 1000) {
			_r = length + "";
		}
		return _r;
	}
	
}
