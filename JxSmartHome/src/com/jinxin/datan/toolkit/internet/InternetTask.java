package com.jinxin.datan.toolkit.internet;

/**
 * 通信事件网络请求父类（联网方式）
 * @author zj
 *
 */
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.DatanAgentConnector;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

public class InternetTask extends Task {

	private String errorMessage;

	private NetRequest mRequest;
	private byte socketOrHttp = 3;// 0socket，1http连接 ,2 httpforxml，3 httpJson,4本地文件读写,5socketJson,6socketJson长连接
	private byte socketType = 0;// socket连接种类
	private byte httpType = 0;// http连接种类（目前区分为0:post和1:get）
	private byte fileReadOrWrite = 0;//文件读写操作，0读文件1写文件
	private static DatanAgentConnector connector = null;
	protected Context _context = null;
	private boolean isLogin = false;//是否登录过应用
	private boolean isOffMode = false;//当前是否离线模式
	private boolean isNotFirst = false;//是否第一次登录
	private int code = 0;//是否为发送指令操作   0：调接口；1：发指令

	/**
	 * 设置联网请求（必须设置）
	 * @param request
	 */
	public void setNetRequest(Context context, NetRequest request, IUIUpdate iUIUpdate) {
		this.mRequest = request;
		this.connectingShow(context, iUIUpdate);
	}
	public void setNetRequest(Context context, NetRequest request, IUIUpdate iUIUpdate, int code) {
		this.mRequest = request;
		this.code = code;
		this.connectingShow(context, iUIUpdate);
	}

	/**
	 * 联网状态loading显示窗口监听
	 */
	private void connectingShow(Context context, IUIUpdate iUIUpdate) {
//		final Context _context = context;
		_context = context;
		final IUIUpdate _iUIUpdate = iUIUpdate;
		this.addListener(new ITaskListener() {

			@Override
			public void onStarted(ITask task, Object arg) {
				// TODO Auto-generated method stub
				if (_context != null) {
//					EddhcApp.showLoading(_context);//zj20131011停用loading
				}
//				 HmsApp.setPd(ProgressDialog.show(_context, "", "loading...）", true, true));
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				// TODO Auto-generated method stub
				// HmsApp.getHandler().sendEmptyMessage(HmsApp.PROGRESS_DIALOG_STOP);
//				EddhcApp.closeLoading();//zj20131011停用loading
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				// TODO Auto-generated method stub
				
				if (mRequest != null && mRequest.getResponseJson() != null){
					if(mRequest.getResponseJson().getResultInfo() != null){
						//服务器返回错误信息
						if (_context != null) {
							if (DatanAgentConnectResource.SERVER_ERROR_MSG_0101.equalsIgnoreCase(mRequest.getResponseJson().getResultInfo().validResultCode.trim()) || 
									DatanAgentConnectResource.SERVER_ERROR_MSG_0115.equalsIgnoreCase(mRequest.getResponseJson().getResultInfo().validResultCode.trim())) {// 界面过期刷新
								//发送消息到主线程弹出提示对话框
								Message msg = JxshApp.getHandler().obtainMessage();
								msg.obj = _context;
								Bundle bundle = new Bundle();
								bundle.putString("error", mRequest.getResponseJson().getResultInfo().validResultInfo);
								msg.setData(bundle);
								msg.what = JxshApp.EXIT_MESSAGE_RELOGIN;
//								JxshApp.getHandler().sendMessage(msg);
								msg.sendToTarget();
								JxshApp.showToast(_context, mRequest.getResponseJson().getResultInfo().validResultInfo);
							}else if(
									DatanAgentConnectResource.SERVER_ERROR_MSG_00.equalsIgnoreCase(mRequest.getResponseJson().getResultInfo().validResultCode.trim())){
								JxshApp.showToast(_context, mRequest.getResponseJson().getResultInfo().validResultInfo);
							}else if(DatanAgentConnectResource.SERVER_ERROR_MSG_02.equalsIgnoreCase(
									mRequest.getResponseJson().getResultInfo().validResultCode.trim())){
								JxshApp.showToast(_context, mRequest.getResponseJson().getResultInfo().validResultInfo);
							}else if(DatanAgentConnectResource.SERVER_ERROR_MSG_01.equalsIgnoreCase(
									mRequest.getResponseJson().getResultInfo().validResultCode.trim())){
								JxshApp.showToast(_context, mRequest.getResponseJson().getResultInfo().validResultInfo);
							}else if(mRequest.getResponseJson().getResultInfo().validResultCode.trim().
									equalsIgnoreCase("0404")){
								changeToOffllineMode(_context);
							}else{//错误信息返回提示
								JxshApp.showToast(_context, mRequest.getResponseJson().getResultInfo().validResultInfo);
							}
						}
					}else{
			        	if (_context != null) {
			        		Logger.debug(null, "request info is null");
			        		if (!TextUtils.isEmpty(JxshApp.lastInputID)) {
			        			changeToOffllineMode(_context);
							}
			        	}
					}
//				EddhcApp.closeLoading();//zj20131011停用loading
					// 联网成功，但服务器返回的错误信息处理
//				if (mRequest != null && mRequest.getResponseXml() != null) {
//					if (mRequest.getResponseXml().getErrorInfo() != null) {
//						if (_context != null) {
//							// Message msg=mUIHandler.obtainMessage();
//							// msg.obj = _context;
//							// Bundle bundle = new Bundle();
//							// bundle.putString("error", mRequest.getResponseXml().getErrorInfo().ValidErrorInfo);
//							// msg.setData(bundle);
//							// msg.what = HmsApp.SHOW_MY_TOAST;
//							// HmsApp.getHandler().sendMessage(msg);
//							JxshApp.showToast(_context, mRequest.getResponseXml().getErrorInfo().validErrorInfo);
//						}
//							if (_iUIUpdate != null) {
//								JxshApp.getHandler().post(new Runnable() {
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//										_iUIUpdate.UIUpdate(mRequest.getResponseXml().getErrorInfo());
//									}
//								});
//							}
//							
//						}
//				}
		        }else{
		        	if (_context != null) 
		        		JxshApp.showToast(_context, CommDefines.getSkinManager().string(R.string.qing_qiu_fu_wu_chao_shi));
				}
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				// TODO Auto-generated method stub
				
				if (mRequest != null && mRequest.getResponseJson() != null && code == 1) {
					if (mRequest.getResponseJson().getResultInfo() != null) {

//						if (_context != null) {
//							JxshApp.showToast(_context, "操作成功");
//						}
					}
				} else {
//					if (_context != null && code == 1)
//						JxshApp.showToast(_context, CommDefines.getSkinManager().string(R.string.qing_qiu_fu_wu_chao_shi));
				}
//				EddhcApp.closeLoading();//zj20131011停用loading
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}

		});
	}
	
	/**
	 * 切换到离线模式提示框
	 */
	private void changeToOffllineMode(Context _context) {
		JxshApp.showToast(_context, CommDefines.getSkinManager().string(R.string.qing_qiu_fu_wu_chao_shi));
		/**********切换到当前输入的帐号对应的数据库***********/
		SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_ACCOUNT, JxshApp.lastInputID);
		SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_SUNACCOUNT, "");
		String account = CommUtil.getCurrentLoginAccount();
		DBHelper dBHelper = new DBHelper(_context);
		/********************END*******************/
		
		if (account!=null && account!= "") {
			isLogin = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_ISLOADING,false);
			isOffMode = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_OFF_LINE_MODE, false);
			isNotFirst = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_NOT_FIRST_LOGING, false);
		}
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(_context);
		boolean isEnableOfflineSwitch;
		List<CloudSetting> csList = csDaoImpl.find(null, "items = ?",
				new String[]{StaticConstant.PARAM_OFFLINE_SWITCH}, null, null, null, null);
		if (csList!= null && csList.size() > 0) {
			CloudSetting cs = csList.get(0);
			isEnableOfflineSwitch =  Boolean.valueOf(cs.getParams());
		}else{
			isEnableOfflineSwitch = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_ENABLE_OFFLINE_MODE, true);
		}
		if(isEnableOfflineSwitch) {
			if (isLogin && !isOffMode) {//登录之后 弹出提示
				//发送消息到主线程弹出提示对话框
				Bundle bundle = new Bundle();
				bundle.putString("modeOff", "启用离线模式");
				BroadcastManager.sendBroadcast(BroadcastManager.ACTION_CHANGE_OFF_LINE_MODE, bundle);
			}else if (!isLogin && isNotFirst) {
				List<String> list = JxshApp.getHistoryUserList();
				if (list != null && list.size() > 0) {
					for (String string : list) {
						if (string.equals(JxshApp.lastInputID)) {
							//登录界面 弹出提示
							Bundle bundle = new Bundle();
							bundle.putString("modeOff", "启用离线模式");
							BroadcastManager.sendBroadcast(BroadcastManager.ACTION_CHANGE_OFF_LINE_MODE, bundle);
							break;
						}
					}
				}
			}
		}else{
			JxshApp.showToast(_context, "离线模式未开启");
		}
	}

	/**
	 * 设置连接方式（默认为socket方式）
	 * 
	 * @param type
	 */
	public void setSocketOrHttp(byte type) {
		this.socketOrHttp = type;
	}

	/**
	 * 设置Socket连接方式（默认为connect方式）
	 * 
	 * @param type
	 */
	public void setSocketType(byte type) {
		this.socketType = type;
	}

	/**
	 * 设置Http连接方式（默认为get方式）
	 * 
	 * @param type
	 */
	public void setHttpType(byte type) {
		this.httpType = type;
	}
	public byte getFileReadOrWrite() {
		return fileReadOrWrite;
	}

	public void setFileReadOrWrite(byte fileReadOrWrite) {
		this.fileReadOrWrite = fileReadOrWrite;
	}
	public int excute() {
		if (this.mRequest == null)
			return Task.STATE_FAIL;
		mRequest.setExcuteTime(System.currentTimeMillis());
		if(socketOrHttp == 6){
			//要保持长连接,connector不能重new
			if(this.connector == null){
				this.connector = new DatanAgentConnector();
			}
		}else{
			this.connector = new DatanAgentConnector();
		}
		// CmdGetRoot cmdGetRoot = new
		// CmdGetRoot(MobileMusicApplication.getInstance());
		int state = 0;
		switch (this.socketOrHttp) {
		case 0:// socket
			state = connector.connectSocket(mRequest.getRequestId(), mRequest.getCommandBase(), mRequest.getIntdesFlag(), this.socketType);
			break;
		case 1:// http
				// 目前用来读取流文件
			state = connector.connectHttp(mRequest.getUrl(), mRequest.getCommandBase(), this.httpType);
			break;
		case 2:// http for xml
				// 给url添加延迟时间差
				// mRequest.setUrl(mRequest.getUrl()+"&bd="+(mRequest.getExcuteTime()-mRequest.getCreateTime()));
			state = connector.connectHttpForXML(mRequest.getUrl(), mRequest.getResponseXml(), this.httpType);
			break;
		case 3:// http for json
				// mRequest.setUrl(mRequest.getUrl()+"&bd="+(mRequest.getExcuteTime()-mRequest.getCreateTime()));
			state = connector.connectHttpForJson(mRequest.getUrl(), mRequest.getResponseJson(), this.httpType);
			break;
		case 4://本地文件读写
			if(this.getFileReadOrWrite() == 0){
				//读文件
				state = (mRequest.getmCommandFile().read()) ? Task.STATE_SUCCESS : Task.STATE_FAIL;
			}else if(this.getFileReadOrWrite() == 1){
				//写文件
				state = (mRequest.getmCommandFile().write()) ? Task.STATE_SUCCESS : Task.STATE_FAIL;
			}
			break;
		case 5://socketJson
			state = connector.connectSocketForJson(mRequest.getUrl(), mRequest.getResponseJson());
			break;
		case 6://socketJsonLong
			state = connector.connectSocketLongForJson(mRequest.getUrl(), mRequest.getResponseJson());
			break;
		}
		if(connector != null)
			this.errorMessage = connector.msg;
		if(state == Task.STATE_FAIL){//联网失败自动关闭socket长连接的地方
			clearSocketConnector();
		}
		if(this.isCloseSocketLong()){//手动设置关闭socket长连接的地方
			clearSocketConnector();
		}
		if(this.ismTryCancel()){//任务是被取消的
			state = Task.STATE_CANCELED;
		}
		return state;
	}
	/**
	 * 关闭socket连接并清空DatanAgentConnector 
	 */
	public void clearSocketConnector(){
		if(connector != null){
			connector.clearSocketConnector();
			connector = null;
		}
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	public NetRequest getRequest() {
		return mRequest;
	}

	/**
	 * 封装 get requestBytes
	 * @param header
	 * @param serviceContent
	 * @return
	 * @throws JSONException
	 */
	protected byte[] getRequestByte(Header header, JSONObject serviceContent) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("serviceContent", serviceContent);

		jsonObj.put("origDomain", header.getOrigDomain());
		jsonObj.put("homeDomain", header.getHomeDomain());
		jsonObj.put("bipCode", header.getBipCode());
		jsonObj.put("bipVer", header.getBipVer());
		jsonObj.put("activityCode", header.getActivityCode());
		jsonObj.put("processTime", header.getProcessTime());
		jsonObj.put("response", null);
		jsonObj.put("testFlag", header.getTestFlag());
		jsonObj.put("actionCode", header.getActionCode());
				
		StringBuffer _data = new StringBuffer();
		_data.append("$data=");
		_data.append(jsonObj.toString());
		Log.d("wangfan", _data.toString());
		return _data.toString().getBytes();
	}
	
}
