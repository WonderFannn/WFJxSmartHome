package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.net.command.GatewayStateTask;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.entity.ZigbeeResponse;
import com.jinxin.jxsmarthome.cmd.response.RegexConstants;
import com.jinxin.jxsmarthome.cmd.response.ResponseParserFactory;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.CommonMethodForZGDetect;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.data.Device;
import com.jinxin.jxsmarthome.ui.adapter.tile.TileList;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 我的设备
 * 
 * @author HuangBinHua
 * @company 金鑫智慧
 */
@SuppressLint("SimpleDateFormat")
public class MyDeviceActivity extends BaseActionBarActivity {
	private static final int OPERATE_BEGIN = 1000;
	private static final int OPERATE_SUCCESS = 1001;
	private static final int OPERATE_FAIL = 1002;
	private static final int OPERATE_UPDATE_STATE = 1003;
	private static final int OPERATE_UPDATE_WIRELESS_GATEWAY = 1005;
	private static final int OPERATE_END = 1004;

	private static final String RESULT = "RESULT";
	private static final String DID = "DEVICEID";
	private static final String STATE = "STATE";

	private ListView mList;
	private Cursor mData;
	private DeviceAdatper mDeviceAdapter;
	private TileList<Device> myDevice = new TileList<Device>();
	private List<String> zgDeviceList = new ArrayList<String>();
	private String currentWhId = "";
	private ConcurrentLinkedQueue<Device> priorDevice = new ConcurrentLinkedQueue<Device>();
	private volatile boolean oneKeyDetecting = false;
	private MenuItem oneKeyItem;

	private boolean isOffMode = false;// 是否离线模式
	private final String ZG_DEVICE_S_NORMAL = "CF B1 OO OO OO A3 22 00";
	private int WG_NULL = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParam();
		initView();
		initData();
	}

	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case OPERATE_BEGIN:
			JxshApp.showLoading(MyDeviceActivity.this, "正在操作请稍后...");
			break;
		case OPERATE_FAIL:
			JxshApp.closeLoading();
			Bundle bundle = msg.getData();
			Device device = findByWhId(bundle.getString(DID));
			if (device != null) {
				device.setState("连接超时");

				mDeviceAdapter.listData = myDevice;
				mDeviceAdapter.notifyDataSetChanged();
				return;
			}
			break;
		case OPERATE_SUCCESS:
			JxshApp.closeLoading();

			mDeviceAdapter = new DeviceAdatper(MyDeviceActivity.this, myDevice, myDevice.getRange());
			mList.setAdapter(mDeviceAdapter);
			break;
		case OPERATE_UPDATE_WIRELESS_GATEWAY:
			JxshApp.closeLoading();

			int index = findWG();
			if (WG_NULL == index)
				return;
			Device wgDevice = myDevice.get(index);
			Bundle wgBundle = msg.getData();
			String wgState = wgBundle.getString(STATE);
			wgDevice.setState(wgState);
			if ("网关离线".equals(wgState)) {
				JxshApp.showToast(this, wgState);
				break;
			}
			mDeviceAdapter.listData = myDevice;
			mDeviceAdapter.notifyDataSetChanged();
			break;
		case OPERATE_UPDATE_STATE:
			bundle = msg.getData();
			String response = bundle.getString(RESULT);
			System.out.println("send " + response);
			device = findByWhId(bundle.getString(DID));
			if(device.getCode().equals("G102")) break;

			if (zgDeviceList.contains(bundle.getString(DID))) {
				if (parseResponseData(response)) {
					device = findByWhId(bundle.getString(DID));
					if (device != null) {
						device.setState("设备正常");

						mDeviceAdapter.listData = myDevice;
						mDeviceAdapter.notifyDataSetChanged();
						break;
					}
				}
			} else if (response != null && response.length() >= 24) {
				String whId = response.substring(3, 19);
				String state = response.substring(19, 23);
				if (response.length() < 29) {
					state = response.substring(19, 21);
				}
				device = findByWhId(whId);
				if (device != null) {
					device.setState(convertState(state));
					mDeviceAdapter.listData = myDevice;
					mDeviceAdapter.notifyDataSetChanged();
					break;
				} else {
					device = findByWhId(bundle.getString(DID));
					if (device != null) {
						device.setState(convertState(state));

						mDeviceAdapter.listData = myDevice;
						mDeviceAdapter.notifyDataSetChanged();
						break;
					}
				}
			}
			if (response != null) {
				device = findByWhId(bundle.getString(DID));
				if (device != null) {
					device.setState("连接超时");

					mDeviceAdapter.listData = myDevice;
					mDeviceAdapter.notifyDataSetChanged();
				}
			}
			break;
		case OPERATE_END:
			oneKeyDetecting = false;
			oneKeyItem.setEnabled(true);
			break;
		default:
			break;
		}
	}

	/**
	 * 解析结果 返回数据模板：len=0x04 sender=0xCFB4 profile=0x0104 cluster=0x0102
	 * dest=0x01 sour=0x02 payload[08 02 04 00 08 02 04 00 08 02 04 00 ]
	 */
	private boolean parseResponseData(String result) {
		Logger.debug(null, "result:" + result);
		ZigbeeResponse data = ResponseParserFactory.parseContent(result, RegexConstants.ZG_BASIC_CONTENT_REP);

		String payload = data.getPayload();
		Logger.debug(null, "payload:" + payload);
		if (!TextUtils.isEmpty(payload)) {
			String[] items = payload.split(" ");
			Logger.debug(null, "length:" + items.length);
			if (items.length > 6) {
				return true;
			}
		}
		return false;
	}

	private Device findByWhId(String whId) {
		if(TextUtils.isEmpty(whId)) 
			return null;
		for (Object object : myDevice) {
			Device device = (Device) object;
			if (whId.equalsIgnoreCase(device.getWhId())) {
				return device;
			}
		}
		return null;
	}

	/**
	 * 初始化参数
	 */
	private void initParam() {
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		setContentView(R.layout.activity_my_device);
		getSupportActionBar().setTitle(getResources().getString(R.string.title_my_device));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String account = CommUtil.getCurrentLoginAccount();
		if (account != null && account != "") {
			isOffMode = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_OFF_LINE_MODE, false);
		}
		mList = (ListView) findViewById(R.id.my_device_list);
		isRunning = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_my_device, menu);
		oneKeyItem = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.action_onekey_detect:
			oneKeyItem.setEnabled(false);
			oneKeyDetecting = true;
			deviceDetect();
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mUIHander.sendEmptyMessage(OPERATE_BEGIN);
				getDataFromDb();
			}
		}).start();
	}

	/**
	 * 从数据库中获取数据
	 */
	private void getDataFromDb() {
		DBHelper helper = new DBHelper(this);
		SQLiteDatabase db = helper.getReadableDatabase();
		mData = db
				.rawQuery(
						"select b.id as _id,b.address485, b.whId, c.alias, c.funType, b.code,a.gatewayWhId from product_register as a inner join customer_product as b on a.whId = b.whId inner join fun_detail_config as c on a.whId = c.whId order by c.funType DESC",
						new String[] {});

		Cursor zgDeviceCursor = db.rawQuery(
				"select whId, code from product_register where gatewayWhId in (select whId from product_register where code = 'G102')", new String[] {});
		if (zgDeviceCursor != null && zgDeviceCursor.moveToNext()) {
			do {
				int col = 0;
				String whId = zgDeviceCursor.getString(col);

				col = 1;
				if (zgDeviceCursor.getString(col).equals("G102")) { // 过滤网关
					// continue;
				}
				zgDeviceList.add(whId);
			} while (zgDeviceCursor.moveToNext());
		}

		String lastFunType = null;// 上一次fun type
		myDevice.clear();
		if (mData.moveToLast()) {
			do {
				int i = 1;
				Device device = new Device();
				device.setAddress485(mData.getString(i++));
				device.setWhId(mData.getString(i++));
				device.setAlias(mData.getString(i++));
				device.setFunType(mData.getString(i++));

				if (zgDeviceList.contains(device.getWhId())) {
					device.setCode(mData.getString(i++));
					device.setZg(true);
				} else {
					device.setCode("0".concat(mData.getString(i++)));
					device.setZg(false);
				}
				device.setGatewayId(mData.getString(i++));

				if (device.getCode().equals("0007") || device.getCode().equals("0G102")) { // 过滤网关
					// continue;
				}
				// 过滤禁用状态设备
				if (!device.getCode().equals("001")) {// 多路设备
					ProductFunDaoImpl proDaoImpl = new ProductFunDaoImpl(this);
					List<ProductFun> list = proDaoImpl.find(null, "whId = ?", new String[] { device.getWhId() }, null, null, null, null);
					if (list != null && list.size() > 0) {
						ProductFun fun = list.get(0);
						if (fun.getEnable() == 0) {
							continue;
						}
					}
				}
				if (lastFunType != null && !lastFunType.equals(device.getFunType())) {
					myDevice.split();
				}
				myDevice.add(device);
				lastFunType = device.getFunType();
			} while (mData.moveToPrevious());
			myDevice.split();
		}
		mData.close();
		db.close();

		Message message = mUIHander.obtainMessage();
		message.what = OPERATE_SUCCESS;
		message.sendToTarget();
		// mUIHander.sendMessage(message);
	}

	// 查找无线网关
	private int findWG() {
		for (int i = 0; i < myDevice.size(); i++) {
			if (myDevice.get(i).getCode().equals("G102")) {
				return i;
			}
		}
		return WG_NULL;
	}

	// 一键巡检
	private void deviceDetect() {
		// 无线网关单独巡检
		int index = findWG();
		if (WG_NULL != index) {
			getWirelessGatewayState(myDevice.get(index).getWhId());
		}
		sendDeviceDetectCmd(myDevice);
	}

	// 优先巡检
	private void deviceDetectPrior() {
		sendDeviceDetectCmd(new ArrayList<Device>());
	}

	// 发送设备巡检指令
	private void sendDeviceDetectCmd(ArrayList<Device> deviceBudle) {

		List<byte[]> cmds = new ArrayList<byte[]>();

		for (Device device : deviceBudle) {
			if (device.isZg()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(StaticConstant.OPERATE_SET_TYPE, device.getCode());
				device.setFunType(ProductConstants.FUN_TYPE_ZG_DEVICE_DETECT);
				cmds.addAll(CommonMethodForZGDetect.productDetectToZGCMD(this, device, params, null));
			} else {
				cmds.addAll(CommonMethod.productDetectToCMD(this, device.getWhId(), device.getCode()));
			}
		}
		
		if (cmds == null || cmds.size() < 1) {
			// return;
		}

		Stack<byte[]> ppos = new Stack<byte[]>();
		for (int i = cmds.size() - 1; i >= 0; i--) {
			ppos.push(cmds.get(i));
		}
		if (isRunning)
			patternOperationSend(ppos);
	}

	// 无线网关单个巡检
	private void getWirelessGatewayState(String whId) {
		GatewayStateTask gsTask = new GatewayStateTask(null, whId);
		gsTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				Message msg = mUIHander.obtainMessage();
				msg.what = OPERATE_UPDATE_WIRELESS_GATEWAY;
				Bundle data = new Bundle();
				data.putString(STATE, "网关离线");
				msg.setData(data);
				msg.sendToTarget();

			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length > 0) {

				}
				Message msg = mUIHander.obtainMessage();
				msg.what = OPERATE_UPDATE_WIRELESS_GATEWAY;
				Bundle data = new Bundle();
				data.putString(STATE, "设备正常");
				msg.setData(data);
				msg.sendToTarget();

			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		gsTask.start();
	}

	/* 递归发送指令 */
	private void patternOperationSend(final Stack<byte[]> _ppos) {
		Logger.warn("tangl", "" + _ppos.size());

		if (priorDevice.size() > 0 && (cdcbsTask == null || (cdcbsTask != null && cdcbsTask.isEnd()))) {// 处理单个巡检
			Device prior = priorDevice.poll();
			List<byte[]> cmds = new ArrayList<byte[]>();
			if (prior.isZg()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(StaticConstant.OPERATE_SET_TYPE, prior.getCode());
				prior.setFunType(ProductConstants.FUN_TYPE_ZG_DEVICE_DETECT);
				cmds.addAll(CommonMethodForZGDetect.productDetectToZGCMD(this, prior, params, null));
			} else {
				cmds.addAll(CommonMethod.productDetectToCMD(this, prior.getWhId(), prior.getCode()));
			}
			for (int i = 0; i < cmds.size(); i++) {
				_ppos.push(cmds.get(i));
			}
			currentWhId = prior.getWhId();
			System.out.println("巡检:" + currentWhId);
		} else {// 一键巡检
			if (_ppos == null || _ppos.empty()) {
				finishOneKeyDetect();

				JxshApp.closeLoading();
				return;
			}
			Device dev = (Device) myDevice.get(myDevice.size() - _ppos.size());
			currentWhId = dev.getWhId();
			System.out.println("巡检:" + currentWhId);
		}

		byte[] _ppo = _ppos.pop();

		if (_ppo != null) {
			byte[] cmd = _ppo;

			cdcbsTask = new CommonDeviceControlByServerTask(null, cmd, false, 1);

			if (zgDeviceList.contains(currentWhId)) {// zg
				System.out.println("zigbee设备巡检:" + currentWhId);
				((CommonDeviceControlByServerTask) cdcbsTask).setRequestHost(DatanAgentConnectResource.HOST_ZEGBING, 2);
			}

			if (_ppos == null || _ppos.empty()) {
				JxshApp.closeLoading();
				if (cdcbsTask != null) {
					cdcbsTask.setcloseSocketLongAfterTaskFinish(true);
					Logger.warn("tangl", "关闭连接");
				}
			}

			cdcbsTask.addListener(new ITaskListener<ITask>() {

				public void onStarted(ITask task, Object arg) {
					Logger.debug("tangl.home", "onstart...");
				}

				@Override
				public void onCanceled(ITask task, Object arg) {
					Logger.debug("tangl.home", "oncancel...");
				}

				@Override
				public void onFail(ITask task, Object[] arg) {
					Logger.debug("tangl.home", "onfail...");
					JxshApp.closeLoading();
					mUIHander.sendEmptyMessage(OPERATE_FAIL);
				}

				@Override
				public void onSuccess(ITask task, Object[] arg) {
					Logger.debug("tangl.home", "onSuccess...");
					if (arg != null && arg.length > 0) {
						RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
						Logger.debug(null, resultObj.toString());

						if (resultObj.validResultCode.equals("0000")) {
							String result = resultObj.validResultInfo;
							System.out.println("send cmd：" + DID + " " + result + " " + currentWhId);

							if ("01".equals(result)) {
								JxshApp.showToast(MyDeviceActivity.this, "网关离线", Toast.LENGTH_SHORT);
								finishOneKeyDetect();
							} else {
								Message msg = mUIHander.obtainMessage();
								Bundle data = new Bundle();
								data.putString(DID, currentWhId);
								if (result != null) {
									data.putString(RESULT, result);
									msg.what = OPERATE_UPDATE_STATE;
								} else {
									msg.what = OPERATE_FAIL;
								}
								msg.setData(data);
								msg.sendToTarget();
							}
						}
					}
					if (isRunning == true) {
						try {
							Thread.currentThread().sleep(2000);
						} catch (InterruptedException e) {
						}
						patternOperationSend(_ppos);
					}
				}

				@Override
				public void onProcess(ITask task, Object[] arg) {

				}
			});
			synchronized (MyDeviceActivity.this) {
				if (mDownLoadHandler != null && cdcbsTask != null) {
					mDownLoadHandler.postDelayed(cdcbsTask, 10);
				}
			}
		}
	}

	private void finishOneKeyDetect() {
		Message msg = mUIHander.obtainMessage();
		msg.what = OPERATE_END;
		// mUIHander.sendMessage(msg);
		msg.sendToTarget();
	}

	boolean isRunning = false;
	CommonDeviceControlByServerTask cdcbsTask = null;

	private class DeviceAdatper extends BaseAdapter {

		private final int ITEM_TYPE_NULL = 0;
		private final int ITEM_TYPE_UP = 1;
		private final int ITEM_TYPE_MIDDLE = 2;
		private final int ITEM_TYPE_DOWN = 3;
		private final int ITEM_TYPE_SINGLE = 4;

		private Context mContext;
		private TileList<Device> listData;
		private List<Tile> mTile = new ArrayList<Tile>();

		public DeviceAdatper(Context context, TileList<Device> value, ArrayList<Integer> tiles) {
			mContext = context;
			listData = value;

			computeTile(tiles);
		}

		private void computeTile(ArrayList<Integer> segs) {
			int index = 0;
			for (int segSize : segs) {
				if (segSize > 0) {
					mTile.add(new Tile(index, index + segSize));
					index += segSize;
				}
			}
		}

		public int getCount() {
			return listData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int itemType = getItemType(position);

			switch (itemType) {
			case ITEM_TYPE_UP:
				convertView = inflater.inflate(R.layout.setting_item_up_detail, null);
				break;
			case ITEM_TYPE_MIDDLE:
				convertView = inflater.inflate(R.layout.setting_item_middle_detail, null);
				break;
			case ITEM_TYPE_DOWN:
				convertView = inflater.inflate(R.layout.setting_item_down_detail, null);
				break;
			case ITEM_TYPE_SINGLE:
				convertView = inflater.inflate(R.layout.setting_item_single_detail, null);
				break;
			default:
				convertView = inflater.inflate(R.layout.setting_item_single_detail, null);
				break;
			}

			holder = new ViewHolder();
			holder.mTvAlias = (TextView) convertView.findViewById(R.id.device_alias);
			holder.mTvWhId = (TextView) convertView.findViewById(R.id.device_whId);
			holder.mTvState = (TextView) convertView.findViewById(R.id.device_state);
			holder.mBtnDetect = (Button) convertView.findViewById(R.id.single_detect);
			convertView.setTag(holder);
			holder.mBtnDetect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Device dev = (Device) listData.get(position);
					if (dev.getCode().equals("G102")) {
						getWirelessGatewayState(dev.getWhId());
					} else {
						priorDevice.add(dev);
					}
					if (!oneKeyDetecting) {
						deviceDetectPrior();
					}
				}
			});
			
			Device device = (Device) listData.get(position);
			holder.mTvAlias.setText(device.getAlias());
			holder.mTvWhId.setText(device.getWhId());
			holder.mTvState.setText(device.getState());
			
			return convertView;
		}

		private int getItemType(int position) {
			for (Tile tile : mTile) {
				if (tile.start == position) {
					if (tile.start == tile.end - 1) {
						return ITEM_TYPE_SINGLE;
					}
					return ITEM_TYPE_UP;
				} else if (tile.end - 1 == position) {
					return ITEM_TYPE_DOWN;
				} else if (tile.start < position && tile.end - 1 > position) {
					return ITEM_TYPE_MIDDLE;
				}
			}
			return ITEM_TYPE_NULL;
		}

		private class Tile {
			int start;
			int end;

			public Tile(int startValue, int endValue) {
				start = startValue;
				end = endValue;
			}
		}

		private class ViewHolder {
			private TextView mTvAlias = null;
			private TextView mTvWhId = null;
			private TextView mTvState = null;
			private Button mBtnDetect = null;
		}

	}

	private String convertState(String val) {
		if (val == null || val.equals(""))
			return "";
		if ("0000".equals(val)) {
			return "设备正常";
		} else if ("0001".equals(val)) {
			return "连接超时";
		} else if ("0002".equals(val)) {
			return "设备损坏";
		} else if ("0009".equals(val)) {
			return "未知异常";
		} else if ("00".equals(val)) {
			return "设备正常";
		}
		return "其他";
	}

	@Override
	protected void onDestroy() {
		isRunning = false;
		synchronized (MyDeviceActivity.this) {
			if (cdcbsTask != null) {
				cdcbsTask.clearSocketConnector();
				cdcbsTask.cancel();
				cdcbsTask = null;
			}
			super.onDestroy();
		}
	}
}
