package com.jinxin.jxsmarthome.cmd.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.VoiceIdentifyTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 文本识别发送器
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class OpenVoiceIndentifyManager {
	private Context con;

	public OpenVoiceIndentifyManager(Context con) {
		this.con = con;
	}

	/**
	 * 发送语音
	 */
	public OpenVoiceIndentifyManager switchAndSend() {
		String amplifierWhId = getAmplifierWhId();
		ProductRegister productRegister = AppUtil.getProductRegisterByWhId(con,
				amplifierWhId);
		if (productRegister != null) {
			VoiceIdentifyTask task = new VoiceIdentifyTask(con, "01",
					productRegister.getGatewayWhId());
			task.addListener(new TaskListener<ITask>() {
				@Override
				public void onSuccess(ITask task, Object[] arg) {
					super.onSuccess(task, arg);
					sendVoiceOpenCmd();
				}

				@Override
				public void onFail(ITask task, Object[] arg) {
					super.onFail(task, arg);
					sendVoiceOpenCmd();
				}
			});
			task.start();
		}
		return this;
	}

	/**
	 * 打开智能语音
	 */
	private void sendVoiceOpenCmd() {
		ProductFun productFun = AppUtil.getSingleProductFunByFunType(con,
				ProductConstants.FUN_TYPE_UFO1);
		FunDetail funDetail = AppUtil.getFunDetailByFunType(con,
				ProductConstants.FUN_TYPE_UFO1);

		if (productFun == null || funDetail == null) {
			return;
		}
		
		String[] inputAndAddr = getInput4Voice();
		String input = inputAndAddr[0] == null ? "input3" : inputAndAddr[0];
		String addr = TextUtils.isEmpty(inputAndAddr[1]) ? "" : inputAndAddr[1];

		Map<String, Object> params = new HashMap<String, Object>();
		productFun.setFunType(ProductConstants.FUN_TYPE_GATEWAY_WATCH);
		params.put(StaticConstant.OPERATE_INPUT_SET, input);
		params.put(StaticConstant.PARAM_ADDR, addr);

		if (NetworkModeSwitcher.useOfflineMode(con)) {
			OfflineCmdManager.generateCmdAndSend(con, productFun, funDetail,
					params);
			return;
		}

		List<byte[]> cmds = CommonMethod.productFunToCMD(con,
				productFun, funDetail, params);

		if (cmds == null || cmds.size() < 1) return;
		OnlineCmdSenderLong onlineCmdSender = new OnlineCmdSenderLong(con, cmds, true);
		onlineCmdSender.send();
	}

	/**
	 * 获取功放设备的whid（优先重音乐设置中获取，如果音乐中未设置，取任意一个功放设备的whid）
	 * 
	 * @return
	 */
	private String getAmplifierWhId() {
		String whid = SharedDB.loadStrFromDB(MusicSetting.SP_NAME,
				MusicSetting.AMPLIFIER_WHID, null);
		if (whid == null) {
			ProductFun amplifierDevice = AppUtil.getSingleProductFunByFunType(
					con, ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
			if (amplifierDevice != null) {
				whid = amplifierDevice.getWhId();
			}
		}
		return whid;
	}
	
	/**
	 * 获取合成语音的输入源设置
	 * @return
	 */
	private String[] getInput4Voice() {
		String[] ret = new String[2];
		ProductRegister pr = AppUtil.getProductRegisterByWhId(con, getAmplifierWhId());
		FunDetailConfigDaoImpl dao = new FunDetailConfigDaoImpl(con);
		Logger.debug(null, "whId=? and funType=?" + pr.getGatewayWhId());
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
		return ret;
	}
}
