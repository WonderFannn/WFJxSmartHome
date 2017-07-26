package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.net.command.VoiceIdentifyTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.cmd.OfflineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.CommonMethodWithParams;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 模式Fragment gridview适配器
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ModeFragmentGridAdapter extends BaseAdapter {

	private List<CustomerPattern> customerPatternList;
	private Context con;
	protected Handler mDownLoadHandler;
	private String textToSpeakBefore;
	private String textToSpeakEnd;
	private CustomerPatternDaoImpl _cpdImpl = null;

	// private CommonDeviceControlByServerTask cdcbsTask; // 发送指令的连接
	public ModeFragmentGridAdapter(Context con,
			List<CustomerPattern> customerPatternList) {
		_cpdImpl = new CustomerPatternDaoImpl(con);
		this.con = con;
		List<CustomerPattern> cpLists = new ArrayList<CustomerPattern>();
		if (customerPatternList != null) {
			for (CustomerPattern customerPattern : customerPatternList) {
				if (customerPattern.getPaternType().equals("2")) {
					cpLists.add(customerPattern);
				}
			}
			if (cpLists != null && cpLists.size() > 0) {
				customerPatternList = cpLists;
			}
			Collections.sort(customerPatternList,
					new Comparator<CustomerPattern>() {

						@Override
						public int compare(CustomerPattern lhs,
								CustomerPattern rhs) {
							int i = lhs.getStayTop().compareTo(rhs.getStayTop());
							if (i == 0) {
								return lhs.getClickCount().compareTo(rhs.getClickCount());
							}
							return i;
						}
					});
		}

		List<CustomerPattern> tempList = new ArrayList<CustomerPattern>();
		if (customerPatternList != null) {
			for (int i = customerPatternList.size() - 1; i > -1; i--) {
				tempList.add(customerPatternList.get(i));
			}
			this.customerPatternList = tempList;
		}

	}

	/**
	 * 设置DownLoadHandler(取activity的下载handler)
	 * 
	 * @param objects
	 */
	public void setDownLoadHandler(Handler downLoadHandler) {
		this.mDownLoadHandler = downLoadHandler;
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
		final GridHolder gridHolder;
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
		if (TextUtils.isEmpty(_customerPattern.getIcon())) {
			gridHolder.itemImg.setImageBitmap(CommDefines.getSkinManager()
					.getAssetsBitmap("images/default.png"));
			// gridHolder.itemImg.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.umeng_example_xp_action_refresh));
		} else {
			Bitmap _bitmap = CommDefines.getSkinManager().getAssetsBitmap(
					_customerPattern.getIcon());
			if (_bitmap != null) {
				gridHolder.itemImg.setImageBitmap(_bitmap);
			} else {
				gridHolder.itemImg.setImageBitmap(CommDefines.getSkinManager()
						.getAssetsBitmap("images/default.png"));
			}
		}
		gridHolder.itemName.setText(_customerPattern.getPaternName());

		gridHolder.ll_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int _patternId = _customerPattern.getPatternId();
				int _count = _customerPattern.getClickCount() + 1;
				textToSpeakBefore = _customerPattern.getTtsStart();
				textToSpeakEnd = _customerPattern.getTtsEnd();
				ProductPatternOperationDaoImpl _ppoDaoImpl = new ProductPatternOperationDaoImpl(
						con);
				final List<ProductPatternOperation> ppsList = _ppoDaoImpl.find(null,
						"patternId=?",
						new String[] { Integer.toString(_patternId) }, null,
						null, null, null);

				setVoiceIdentifySwitch(textToSpeakBefore);
				
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
							cmdSender.send();
						};
					});
				}
				
				customerPatternList.get(pos).setClickCount(_count);
				_cpdImpl.update(customerPatternList.get(pos));
			}
		});
		return convertView;
	}

	class GridHolder {
		ImageView itemImg;
		TextView itemName;
		LinearLayout ll_button;

		public GridHolder(View view) {
			itemImg = (ImageView) view.findViewById(R.id.mode_item_bg);
			itemName = (TextView) view.findViewById(R.id.mode_item_name);
			ll_button = (LinearLayout) view.findViewById(R.id.ll_button);
		}
	}

	/**
	 * 发送语音前的准备
	 */
	private void setVoiceIdentifySwitch(final String txtToSpeech) {
		String amplifierWhId = getAmplifierWhId();
		
		if(amplifierWhId != null) {
			ProductRegister productRegister = AppUtil.getProductRegisterByWhId(con, amplifierWhId);
			if(productRegister != null) {
				VoiceIdentifyTask task = new VoiceIdentifyTask(con, "02", productRegister.getGatewayWhId());
				task.addListener(new TaskListener<ITask>() {
					@Override
					public void onSuccess(ITask task, Object[] arg) {
						super.onSuccess(task, arg);
						sendVoiceCmd(txtToSpeech);
					}
					@Override
					public void onFail(ITask task, Object[] arg) {
						super.onFail(task, arg);
						sendVoiceCmd(txtToSpeech);
					}
				});
				task.start();
			}
		}
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
	
	
	public int getDpi(){
		DisplayMetrics metric = new DisplayMetrics();
		return metric.densityDpi;
	}
	
	/**
	 * 获取合成语音的输入源设置
	 * @return
	 */
	private String getInput4Voice() {
		String input = null;
		ProductRegister pr = AppUtil.getProductRegisterByWhId(con, getAmplifierWhId());
		FunDetailConfigDaoImpl dao = new FunDetailConfigDaoImpl(con);
		List<FunDetailConfig> fdcs = dao.find(null, "whId=? and funType=?", 
				new String[]{pr.getGatewayWhId(), ProductConstants.FUN_TYPE_GATEWAY}, 
				null, null, null, null);
		Logger.debug(null, fdcs.size() + "");
		if(fdcs.size() > 0) {
			String jsonStr = fdcs.get(0).getParams();
			if(jsonStr != null) {
				try {
					Logger.debug(null, jsonStr);
					JSONObject jb = new JSONObject(jsonStr);
					input = jb.getString("input");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}
		return input;
	}
	
	/**
	 * 发送文本，通过语音播放
	 * @param sendText 发送文本
	 */
	private void sendVoiceCmd(String sendText) {
		ProductFun productFun = null;
		List<ProductFun> productFuns = AppUtil.getProductFunByFunType(con, ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		if(productFuns.size() < 1) {
			return;
		}else {
			productFun = productFuns.get(0);
		}
		
		FunDetail funDetail = AppUtil.getFunDetailByFunType(con, ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		
		if (productFun == null || funDetail == null || sendText == null) {
			return;
		}
		
		String input = getInput4Voice();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_TEXT, sendText);
		params.put(StaticConstant.OPERATE_INPUT_SET, input);
		
		productFun.setFunType(ProductConstants.FUN_TYPE_GATEWAY);
		
		// -------------- offline -------------------
		if(NetworkModeSwitcher.useOfflineMode(con)) {
			OfflineCmdManager.generateCmdAndSend(con, productFun,
				funDetail, params);
			return;
		}
		// ---------------- end -------------------
		
		List<byte[]> cmds = CommonMethodWithParams.productFunToCMD(con, productFun,
				funDetail, params);

		if (cmds == null || cmds.size() < 1) {
			return;
		}
		
		OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(con, cmds);
		cmdSender.send();
	}

}
