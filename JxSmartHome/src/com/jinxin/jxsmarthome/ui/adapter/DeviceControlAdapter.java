package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.datan.net.command.ChangeDeviceNameTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.DeviceActivity;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductDoorContact;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;

@SuppressLint("HandlerLeak")
public class DeviceControlAdapter extends BaseAdapter {

	private static final int REFRESH = 2;

	private List<ProductFun> productFunList = null;
	private FunDetail funDetail = null;
	private Bitmap openIcon = null;
	private Bitmap closeIcon = null;
	private Context con = null;

	private List<ProductState> psList;
	private ArrayList<String> lightsId;
	private HashMap<Integer, Boolean> isSelected;// 临时缓存选中彩灯 Map
	private HashMap<Integer, Boolean> isMarkList;// 标记当前设备是否选中 固定到首页
	private ProductFunDaoImpl proImpl = null;
	private boolean isSave = false;
	private ListView listView;
	private List<ProductDoorContact> doorContactList = null;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case REFRESH:
				notifyDataSetChanged();
				break;
			}
		}
	};

	/**
	 * @param context
	 * @param productFuns
	 * @param funDetail
	 * @param openIcon
	 */
	@SuppressLint("UseSparseArrays")
	public DeviceControlAdapter(Context context, ListView listView, List<ProductFun> productFuns, FunDetail funDetail, List<ProductState> psList,
			Bitmap openIcon, Bitmap closeIcon) {
		this.con = context;
		this.listView = listView;
		this.productFunList = productFuns;
		this.funDetail = funDetail;
		this.openIcon = openIcon;
		this.closeIcon = closeIcon;
		this.psList = psList;
		this.lightsId = new ArrayList<String>();
		this.isSelected = new HashMap<Integer, Boolean>();
		this.isMarkList = new HashMap<Integer, Boolean>();
		this.proImpl = new ProductFunDaoImpl(context);
		initDate();
	}

	// 初始化isSelected的数据
	private void initDate() {
		if (productFunList != null) {
			for (int i = 0; i < this.productFunList.size(); i++) {
				isSelected.put(i, false);
				isMarkList.put(i, false);
			}
		}
	}

	/**
	 * 设置门磁状态List
	 * 
	 * @param doorContacts
	 */
	public void setDoorContactList(List<ProductDoorContact> doorContacts) {
		if (doorContacts != null) {
			doorContactList = doorContacts;
		}
	}

//	/**
//	 * 设置状态List
//	 * 
//	 * @param psLists
//	 */
//	public void setStateList(List<ProductState> psLists) {
//		if (psLists != null) {
//			psList = psLists;
//		}
//	}

	@Override
	public int getCount() {
		return productFunList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.productFunList == null ? 0 : productFunList.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * 选中的彩灯
	 * 
	 * @return 彩灯ID
	 */
	public ArrayList<String> getCheckedItem() {
		return lightsId;
	}

	public void setCheckedItem(List<String> selected) {
		for (String val : selected) {
			for (int i= 0;i<productFunList.size();i++) {
				ProductFun productFun = productFunList.get(i);
				if(productFun.getWhId().equals(val)) {
					if(isSelected.containsKey(i)) {
						isSelected.remove(i);
					}
					isSelected.put(i, Boolean.valueOf(true));
					if(!lightsId.contains(val)) {
						lightsId.add(val);
					}
				}
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(R.layout.fragment_device_control_item);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final int pos = position;
		final ProductFun productFun = productFunList.get(position);
		ProductState productState = getProductState(productFun);
		holder.itemName.setText(productFun.getFunName());
		holder.itemText.setText(productFun.getFunName());
		// 触摸屏、燃气、无线窗帘、双路开关隐藏放在首页按钮
//		if (ProductConstants.FUN_TYPE_TFT_LIGHT.equals(funDetail.getFunType()) || 
//				ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW.equals(funDetail.getFunType())
//				|| ProductConstants.FUN_TYPE_GAS_SENSE.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_THREE_SWITCH_SIX.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_THREE_SWITCH_THREE.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_FIVE_SWITCH.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_GAS_SENSE.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_WIRELESS_GATEWAY.equals(funDetail.getFunType())||
//				ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN.equals(funDetail.getFunType())||
//				ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND.equals(funDetail.getFunType()) ||
//				ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funDetail.getFunType())||
//				ProductConstants.FUN_TYPE_PLUTO_SOUND_BOX.equals(funDetail.getFunType())) {
//			holder.markBox.setVisibility(View.GONE);
//		}
		
		if (ProductConstants.FUN_TYPE_ZG_LOCK.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_AUTO_LOCK.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_COLOR_LIGHT.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_CRYSTAL_LIGHT.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_CEILING_LIGHT.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_LIGHT_BELT.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_ONE_SWITCH.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_AIRCONDITION.equals(funDetail.getFunType())
				||ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(funDetail.getFunType())
				|| ProductConstants.FUN_TYPE_POP_LIGHT.equals(funDetail.getFunType())) {
			holder.markBox.setVisibility(View.VISIBLE);
		}else{
			holder.markBox.setVisibility(View.GONE);
		}

//		if (ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funDetail.getFunType())) {
//			holder.itemState.setVisibility(View.VISIBLE);
//			if (productState != null && !TextUtils.isEmpty(productState.getState())) {
//				holder.itemState.setText(productState.getState());
//			}
//		}
		// 门磁、烟感、ZG人体感应电量显示
		if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_DOOR_CONTACT) || 
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_INFRARED)
				|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_SMOKE_SENSE)) {
			holder.powerImg.setVisibility(View.VISIBLE);
			holder.markBox.setVisibility(View.GONE);
			holder.itemCheck.setVisibility(View.GONE);
			if (doorContactList != null && doorContactList.size() > 0) {
				String power = doorContactList.get(position).getElectric();
				if ("02".equals(power)) {
					holder.powerImg.setImageResource(R.drawable.icon_power_high);
				} else if ("03".equals(power)) {
					holder.powerImg.setImageResource(R.drawable.icon_power_mid);
				} else if ("04".equals(power)) {
					holder.powerImg.setImageResource(R.drawable.icon_power_low);
				}
			}
		}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_ZG_LOCK)){
			if (productState != null) {
				if ("00".equals(productState.getState())) {
					holder.warringImg.setVisibility(View.VISIBLE);
					holder.markBox.setVisibility(View.GONE);
					holder.itemCheck.setVisibility(View.GONE);
				}
			}
		}

		if (closeIcon != null) {
			holder.itemImg.setImageBitmap(closeIcon);
		} else {
			holder.itemImg.setImageResource(R.drawable.icon_default);
		}

		if (isSave) {
			holder.itemName.setVisibility(View.GONE);
			holder.buttonSave.setVisibility(View.VISIBLE);
			holder.itemText.setVisibility(View.VISIBLE);
			holder.itemText.setEnabled(true);
		} else {
			holder.itemName.setVisibility(View.VISIBLE);
			holder.buttonSave.setVisibility(View.GONE);
			holder.itemText.setVisibility(View.GONE);
			holder.itemText.setEnabled(false);
		}

		holder.markBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					productFun.setBeHomepage(System.currentTimeMillis());
					proImpl.update(productFun);
					isMarkList.put(pos, true);
				} else {
					productFun.setBeHomepage(0L);
					proImpl.update(productFun);
					isMarkList.put(pos, false);
				}
			}
		});

		holder.itemCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					lightsId.add(productFun.getWhId());
					isSelected.put(pos, true);
				} else {
					isSelected.put(pos, false);
					if (lightsId != null) {
						for (int i = 0; i < lightsId.size(); i++) {
							if (lightsId.get(i).equals(productFunList.get(pos).getWhId())) {
								lightsId.remove(i);
							}
						}
					}
				}
				((DeviceActivity)con).addSelected(genSelectedMap());
			}
		});

		// 根据isSelected来设置checkbox的选中状况
		if (isSelected.size() > 0) {
			holder.itemCheck.setChecked(isSelected.get(position).booleanValue());
		}
		holder.markBox.setChecked(isMarkList.get(position));

		if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) || funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)
				|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)
				|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) {
			holder.itemCheck.setVisibility(View.VISIBLE);
		} else {
			holder.itemCheck.setVisibility(View.GONE);
		}

		// if (funDetail.getFunType() != null) {//状态显示
		// int id = productFunList.get(position).getFunId();
		// if (psList != null) {
		// for (ProductState _ps : psList) {
		// if (_ps.getFunId() == id) {
		// if (_ps.getState().equals("1") || "0001".equals(_ps.getState())) {
		// if (openIcon != null) {
		// holder.itemImg.setImageBitmap(openIcon);
		// }else{
		// holder.itemImg
		// .setImageResource(R.drawable.icon_default);
		// }
		// }
		// }
		// }
		// }
		// }
		// 状态显示
		if (productState != null) {
			if (productState.getState().equals("1")
					|| productState.getState().startsWith("01") || productState.getState().endsWith("01")) {
				if (openIcon != null) {
					holder.itemImg.setImageBitmap(openIcon);
				} else {
					holder.itemImg.setImageResource(R.drawable.icon_default);
				}
			}else if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_CURTAIN)){
				if (!"0002".equals(productState.getState())) {
					if (openIcon != null) {
						holder.itemImg.setImageBitmap(openIcon);
					} else {
						holder.itemImg.setImageResource(R.drawable.icon_default);
					}
				}
			}
			
			if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN)){
				Logger.debug(null, "state : "+productState.getState());
				if ("00".equals(productState.getState())) {
					if (openIcon != null) {
						holder.itemImg.setImageBitmap(openIcon);
					} else {
						holder.itemImg.setImageResource(R.drawable.icon_default);
					}
				}else{
					if (closeIcon != null) {
						holder.itemImg.setImageBitmap(closeIcon);
					} else {
						holder.itemImg.setImageResource(R.drawable.icon_default);
					}
				}
			}
		}

		holder.buttonSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(holder.itemText.getText().toString())) {
					JxshApp.showToast(con, CommDefines.getSkinManager().string(R.string.device_name_not_null));
					return;
				}
				// 保存修改名字
				ChangeDeviceNameTask cdnTask = new ChangeDeviceNameTask(con, productFun.getWhId(), productFun.getFunUnit(), holder.itemText.getText()
						.toString());
				cdnTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
						JxshApp.showLoading(con, CommDefines.getSkinManager().string(R.string.wait_please));
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
//						JxshApp.showToast(con, CommDefines.getSkinManager().string(R.string.editor_success));
						// 更新数据库
						productFun.setTempFunName(holder.itemText.getText().toString());
						holder.itemName.setText(holder.itemText.getText().toString());
						productFun.setFunName(productFun.getTempFunName());
						ProductFunDaoImpl _pfDaoImpl = new ProductFunDaoImpl(con);
						_pfDaoImpl.update(productFun);
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
					}
				});
				cdnTask.start();
			}
		});

		return convertView;
	}

	
	private Map<String,Boolean> genSelectedMap() {
		Map<String,Boolean> map = new HashMap<String, Boolean>();
		Set<Integer> setPos = isSelected.keySet();
		for (Integer pos : setPos) {
			map.put(productFunList.get(pos).getWhId(), isSelected.get(pos));
		}
		return map;
	}
	
	/**
	 * 获取设备状态
	 * 
	 * @param pf
	 * @return
	 */
	public ProductState getProductState(ProductFun pf) {
		if (pf == null || psList == null || psList.size() < 1) {
			return null;
		}
		ProductState tempState = new ProductState();
		for (ProductState _ps : psList) {
			if (pf.getFunId() == _ps.getFunId()) {
				tempState = _ps;
			}
		}
		return tempState;
	}

	class ViewHolder {
		LinearLayout llLayout;
		ImageView itemImg;
		CheckBox markBox;
		TextView itemName;
		EditText itemText;
		CheckBox itemCheck;
		Button buttonSave;
		TextView itemState;
		ImageView powerImg, warringImg;

		public ViewHolder(View view) {
			llLayout = (LinearLayout) view.findViewById(R.id.device_item);
			itemImg = (ImageView) view.findViewById(R.id.device_control_img);
			markBox = (CheckBox) view.findViewById(R.id.device_mark);
			itemText = (EditText) view.findViewById(R.id.device_name);
			itemName = (TextView) view.findViewById(R.id.device_text);
			itemState = (TextView) view.findViewById(R.id.device_state);
			itemCheck = (CheckBox) view.findViewById(R.id.device_checked);
			buttonSave = (Button) view.findViewById(R.id.button_save_name);
			powerImg = (ImageView) view.findViewById(R.id.power_iv);
			warringImg = (ImageView) view.findViewById(R.id.lock_warring_iv);
		}
	}

	/**
	 * 是否显示保存按钮
	 * 
	 * @param isSave
	 */
	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}

	public boolean isSave() {
		return isSave;
	}
}
