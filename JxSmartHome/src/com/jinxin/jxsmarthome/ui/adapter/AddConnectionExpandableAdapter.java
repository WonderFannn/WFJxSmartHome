package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.ProductConnectionDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.AddConnectionControlActivity;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductConnection;
import com.jinxin.jxsmarthome.entity.ProductConnectionVO;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.fragment.ConnectionDoubleSocketFragment;
import com.jinxin.jxsmarthome.fragment.ConnectionFiveSwitchFragment;
import com.jinxin.jxsmarthome.fragment.ConnectionLightsColorFragment;
import com.jinxin.jxsmarthome.fragment.ConnectionMusicSettingFragment;
import com.jinxin.jxsmarthome.fragment.ConnectionPopLightFragment;
import com.jinxin.jxsmarthome.fragment.ConnectionThreeSwitchFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 被关联设备列表适配器
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AddConnectionExpandableAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<FunDetail> groupList;
	private List<List<ProductConnectionVO>> childList;
	private ProductConnectionDaoImpl pcdImpl;

	private Handler mHandler = null;
	private ProductFun groupFun;
	private String groupStatus; 

	private String bindType = "1"; // 1 设备 ，2 模式
	private String isvalid = "1";// 1 启用， 2禁用
	private String defaultTime = "0";
	
	private static String OPERATION_SET = "set";
	private static String OPERATION_HUEANDSAT = "hueandsat";
	private static String OPERATION_SEND = "send";
	private static String OPERATION_PLAY = "play";
	private static String OPERATION_PAUSE = "pause";
	private static String OPERATION_COLOR_OFF = "000000000000";
	private static String OPERATION_DOUBLE_SOCKET = "double-on-off";
	private static String OPERATION_SIX_SOCKET = "six-on-off";
	private static String OPERATION_THREE_SOCKET = "three-on-off";
	private static String OPERATION_FIVE_SOCKET = "five-on-off";
	
	private List<ProductConnectionVO> selectVoList = new ArrayList<ProductConnectionVO>();//选中的列表
	private StringBuffer mBuffer = new StringBuffer();

	public AddConnectionExpandableAdapter(Context context,
			List<FunDetail> funDetailList,
			List<List<ProductConnectionVO>> connectionVOList,
			ProductFun groupFun, String groupStatus) {
		this.context = context;
		pcdImpl = new ProductConnectionDaoImpl(context);
		this.groupList = funDetailList;
		this.childList = connectionVOList;
		
		if (groupFun != null) {
			if (ProductConstants.FUN_TYPE_AIRCONDITION.equals(groupFun.getFunType())||
					ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(groupFun.getFunType())) {
				groupStatus = groupFun.isOpen() ? "01":"00";
			}else if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(groupFun.getFunType())){//双路开关
				this.groupStatus = groupStatus;
				Logger.debug(null, "AddConnectionExpandableAdapter = "+groupStatus);
			}else if(ProductConstants.FUN_TYPE_CURTAIN.equals(groupFun.getFunType())){//窗帘
				groupStatus = groupFun.isOpen() ? "0001":"0002";
			}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(groupFun.getFunType())){//无线窗帘
				groupStatus = groupFun.isOpen() ? "00":"01";
			}else{//其它设备
				groupStatus = groupFun.isOpen() ? "1":"0";
			}
		}
		
		this.mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				notifyDataSetChanged();
			}

		};
	}

	public void setProductFun(ProductFun pf){
		this.groupFun = pf;
		if (ProductConstants.FUN_TYPE_AIRCONDITION.equals(groupFun.getFunType())||
				ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(groupFun.getFunType())) {
			groupStatus = groupFun.isOpen() ? "01":"00";
		}else if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(groupFun.getFunType())){//双路开关
			Logger.debug(null, "groupStatus = "+groupStatus);
		}else if(ProductConstants.FUN_TYPE_CURTAIN.equals(groupFun.getFunType())){//窗帘
			groupStatus = groupFun.isOpen() ? "0001":"0002";
		}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(groupFun.getFunType())){//无线窗帘
			groupStatus = groupFun.isOpen() ? "00":"01";
		}else{//其它设备
			groupStatus = groupFun.isOpen() ? "1":"0";
		}
		mHandler.sendEmptyMessage(0);
	}
	
	public void setDoubleStatus(String status){
		this.groupStatus = status;
	}
	
	// 返回父列表个数
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    // 返回子列表个数
    @Override
    public int getChildrenCount(int groupPosition) {
    	if (childList.size() == 0) {
			return 0;
		}
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
    	if (groupList.size() == 0) {
			return null;
		}
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return true;
    }

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(
					R.layout.group_item_product);
			holder = new GroupHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}

		holder.mGroupName.setText(groupList.get(groupPosition).getFunName());
		if (isExpanded) {
			holder.mIcon.setImageResource(R.drawable.ico_arrow_open);
		} else {
			holder.mIcon.setImageResource(R.drawable.ico_arrow_close);
		}

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		SwitchHolder switchHolder = null;
		DoubleSwitchHolder doubleHolder = null;
		CurtainHolder curtainHolder = null;
		CorlorHolder colorHolder = null;
		MusicHolder musicHolder = null;
		
		final int gPos = groupPosition;
		final int cPos = childPosition;
		final FunDetail funDetail = groupList.get(gPos);
		final ProductConnectionVO connectionVO = childList.get(
				gPos).get(cPos);
		final ProductFun productFun = connectionVO.getProductFun();
		
		int viewType = 0;
		if (ProductConstants.FUN_TYPE_AUTO_LOCK.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT.
				equals(groupList.get(groupPosition).getFunType())|| 
				ProductConstants.FUN_TYPE_WIRELESS_SOCKET
					.equals(groupList.get(groupPosition).getFunType())||
					ProductConstants.FUN_TYPE_ONE_SWITCH
					.equals(groupList.get(groupPosition).getFunType())||
					ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET
					.equals(groupList.get(groupPosition).getFunType())||
					ProductConstants.FUN_TYPE_UFO1
					.equals(groupList.get(groupPosition).getFunType())||
					ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2
					.equals(groupList.get(groupPosition).getFunType())||
					ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND
					.equals(groupList.get(groupPosition).getFunType())) {
			viewType = 0;
		} else if (ProductConstants.FUN_TYPE_COLOR_LIGHT
				.equals(groupList.get(groupPosition).getFunType())||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)) {//彩灯
			viewType = 1;
		} else if (ProductConstants.FUN_TYPE_CURTAIN.equals(groupList.get(groupPosition).getFunType())
				|| ProductConstants.FUN_TYPE_WIRELESS_CURTAIN
				.equals(groupList.get(groupPosition).getFunType())) {//窗帘
			viewType = 2;
		} else if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_THREE_SWITCH_THREE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_THREE_SWITCH_SIX
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_FIVE_SWITCH
				.equals(groupList.get(groupPosition).getFunType())) {//双路开关
			viewType = 3;
		} else if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER
				.equals(groupList.get(groupPosition).getFunType())){//功放
			viewType = 4;
		} 
			
		
		//无convertView，需要new出各个控件及相应holder
		if (convertView == null) {
			switch (viewType) {
			case 0:
				convertView = CommDefines.getSkinManager().view(
						R.layout.item_bind_switch, null);
				switchHolder = new SwitchHolder(convertView);
				convertView.setTag(switchHolder);
				break;
			case 1:
				convertView = CommDefines.getSkinManager().view(
						R.layout.item_bind_color_light, null);
				colorHolder = new CorlorHolder(convertView);
				convertView.setTag(colorHolder);
				break;
			case 2:
				convertView = CommDefines.getSkinManager().view(
						R.layout.item_bind_curtain, null);
				curtainHolder = new CurtainHolder(convertView);
				convertView.setTag(curtainHolder);
				break;
			case 3:
				convertView = CommDefines.getSkinManager().view(
						R.layout.item_bind_double_switch, null);
				doubleHolder = new DoubleSwitchHolder(convertView);
				convertView.setTag(doubleHolder);
				break;
			case 4:
				convertView = CommDefines.getSkinManager().view(
						R.layout.item_bind_music, null);
				musicHolder = new MusicHolder(convertView);
				convertView.setTag(musicHolder);
				break;
			}

		} else {
			switch (viewType) {
			case 0:
				switchHolder = (SwitchHolder) convertView.getTag();
				break;
			case 1:
				colorHolder = (CorlorHolder) convertView.getTag();
				break;
			case 2:
				curtainHolder = (CurtainHolder) convertView.getTag();
				break;
			case 3:
				doubleHolder = (DoubleSwitchHolder) convertView.getTag();
				break;
			case 4:
				musicHolder = (MusicHolder) convertView.getTag();
				break;
			}
		}
		//设置资源及点击事件
		switch (viewType) {
		case 0:
			//红外转发器经过特殊处理
			switchHolder.mName.setText(productFun.getFunName());
			
			switchHolder.mCheck
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	
						@Override
						public void onCheckedChanged(
								CompoundButton buttonView, boolean isChecked) {
							connectionVO.setSelected(isChecked);
							if (isChecked) {
								addConnectionVO(connectionVO);
							}else{
								removeConnectionVO(connectionVO);
							}
							
							mHandler.sendEmptyMessage(0);
							if (groupFun != null) {
								if (isChecked && connectionVO.getProductConnection() == null) {// 选择设备，创建关联对象
									if (ProductConstants.FUN_TYPE_WIRELESS_SOCKET
											.equals(groupList.get(gPos).getFunType())||
											ProductConstants.FUN_TYPE_AIRCONDITION
											.equals(groupList.get(gPos).getFunType())||
											ProductConstants.FUN_TYPE_ONE_SWITCH
											.equals(groupList.get(gPos).getFunType())||
											ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET
											.equals(groupList.get(gPos).getFunType())) {
										connectionVO
										.setProductConnection(new ProductConnection(-1,
											groupFun.getFunId(),groupFun.getWhId(),groupStatus, 
												bindType,productFun.getFunId(), "00","off", "none",
												"none",productFun.getWhId(), defaultTime,isvalid));
									}else if(ProductConstants.FUN_TYPE_UFO1
											.equals(groupList.get(gPos).getFunType())||
											ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2
											.equals(groupList.get(gPos).getFunType())){
										connectionVO
										.setProductConnection(new ProductConnection(-1,
											groupFun.getFunId(),groupFun.getWhId(),groupStatus, 
												bindType,productFun.getFunId(), "0",OPERATION_SEND, productFun.getFunUnit(),
												"none",productFun.getWhId(), defaultTime,isvalid));
									}else if(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND
											.equals(groupList.get(gPos).getFunType())){
										connectionVO
										.setProductConnection(new ProductConnection(-1,
											groupFun.getFunId(),groupFun.getWhId(),groupStatus, 
												bindType,productFun.getFunId(), "0",OPERATION_SEND, productFun.getFunParams(),
												"none",productFun.getWhId(), defaultTime,isvalid));
									}else{
										connectionVO
										.setProductConnection(new ProductConnection(-1, groupFun.getFunId(),
												groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(),
												"0","close", "none", "none",productFun.getWhId(), defaultTime,isvalid));
									}
								}
							}else{
								JxshApp.showToast(context, "请先选择触发设备");
							}
						}
					});

			switchHolder.mCheck.setChecked(connectionVO.isSelected());
			
			//设置Item是否可选择
			if (groupFun == null || isAddedDevice(groupFun, productFun)) {
				switchHolder.mCheck.setClickable(false);
				switchHolder.mCheck.setFocusable(false);
			}else{
				switchHolder.mCheck.setFocusable(true);
				switchHolder.mCheck.setClickable(true);
			}
			
			switchHolder.mOpenBtn.setImageDrawable(CommDefines.getSkinManager().
					drawable(connectionVO.isOpen() ?
							R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
			if (ProductConstants.FUN_TYPE_UFO1.equals(groupList.get(gPos).getFunType())
					||ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND.equals(groupList.get(gPos).getFunType())) {
				switchHolder.mOpenBtn.setVisibility(View.INVISIBLE);
			}else{
				switchHolder.mOpenBtn.setVisibility(View.VISIBLE);
			}
			switchHolder.mOpenBtn
					.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if (connectionVO.isSelected()) {
								connectionVO.setOpen(!connectionVO.isOpen());
								if (ProductConstants.FUN_TYPE_WIRELESS_SOCKET
										.equals(groupList.get(gPos).getFunType())||
										ProductConstants.FUN_TYPE_AIRCONDITION
										.equals(groupList.get(gPos).getFunType())||
										ProductConstants.FUN_TYPE_ONE_SWITCH
										.equals(groupList.get(gPos).getFunType())||
										ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET
										.equals(groupList.get(gPos).getFunType())) {
									connectionVO.getProductConnection().setBindStatus(
											connectionVO.isOpen() ? "01" : "00");
									connectionVO.getProductConnection().setOperation(
											connectionVO.isOpen() ? "on" : "off");

								}else{
									connectionVO.getProductConnection().setBindStatus(
											connectionVO.isOpen() ? "1" : "0");
									connectionVO.getProductConnection().setOperation(
											connectionVO.isOpen() ? "open" : "close");
								}
								mHandler.sendEmptyMessage(0);
							}else{
								JxshApp.showToast(context, "请先选择设备");
							}
						}
					});
					
			switchHolder.mTime.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (connectionVO.getProductConnection() != null) {
						showSaveDialog(context, connectionVO);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			if (connectionVO.getProductConnection() != null) {
				switchHolder.mTime.setText(connectionVO.getProductConnection().getTimeInterval()+"s");
				connectionVO.getProductConnection().setStatus(groupStatus);
			}
			break;
		case 1:
			colorHolder.mName.setText(productFun.getFunName());
			colorHolder.mCheck
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(
						CompoundButton buttonView, boolean isChecked) {
					connectionVO.setSelected(isChecked);
					if (isChecked) {
						addConnectionVO(connectionVO);
					}else{
						removeConnectionVO(connectionVO);
					}
					
					if (groupFun != null) {
						if (isChecked
								&& connectionVO.getProductConnection() == null) {
							// 彩灯
							if (ProductConstants.FUN_TYPE_COLOR_LIGHT
									.equals(groupList.get(gPos).getFunType())) {
								connectionVO
								.setProductConnection(new ProductConnection(
									-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(), "0",
										OPERATION_SET, OPERATION_COLOR_OFF, "none",
													productFun.getWhId(),defaultTime,isvalid));
							}else{//无线彩灯
								connectionVO
								.setProductConnection(new ProductConnection(
									-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus,
										bindType,productFun.getFunId(), "0",
										OPERATION_HUEANDSAT, StringUtils.rgbToHsvJson(0, 0, 0),
										"none",productFun.getWhId(), defaultTime,isvalid));
							}
						}
					}else{
						JxshApp.showToast(context, "请先选择触发设备");
					}
					mHandler.sendEmptyMessage(0);
				}
			});
			colorHolder.mCheck.setChecked(connectionVO.isSelected());
			//设置Item是否可选择
			if (groupFun == null || isAddedDevice(groupFun, productFun)) {
				colorHolder.mCheck.setClickable(false);
				colorHolder.mCheck.setFocusable(false);
			}else{
				colorHolder.mCheck.setFocusable(true);
				colorHolder.mCheck.setClickable(true);
			}
			
			//颜色选择
			colorHolder.mColorSelect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Fragment fragment = null;
					if (connectionVO.isSelected()) {
						if (ProductConstants.FUN_TYPE_COLOR_LIGHT
								.equals(groupList.get(gPos).getFunType())) {
							fragment = new ConnectionLightsColorFragment();
						}else{
							fragment = new ConnectionPopLightFragment();
						}
						Bundle bundle = new Bundle();
						bundle.putSerializable("funDetail", funDetail);
						bundle.putSerializable("productConnectionVO", connectionVO);
						fragment.setArguments(bundle);
						addFragment(fragment, true);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			
			if (connectionVO.getProductConnection() != null) {
				//初始化颜色
				String operation = connectionVO.getProductConnection().getOperation(); 
				String strColor = connectionVO.getProductConnection().getParaDesc();
				if (ProductConstants.FUN_TYPE_COLOR_LIGHT
								.equals(groupList.get(gPos).getFunType())) {//有线彩灯
					if (!TextUtils.isEmpty(strColor)) {
						try {
							int intR = stringToInt((String) strColor.subSequence(0, 3));
							int intG = stringToInt((String) strColor.subSequence(3, 6));
							int intB = stringToInt((String) strColor.subSequence(6, 9));
							colorHolder.mColor.setBackgroundColor(Color.rgb(intR, intG, intB));// 初始背景颜色
							connectionVO.getProductConnection().setBindStatus("1");
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (StringIndexOutOfBoundsException e) {
							e.printStackTrace();
						}
					} else {
						colorHolder.mColor.setBackgroundColor(Color.rgb(0, 0, 0));// 初始背景颜色
						connectionVO.getProductConnection().setBindStatus("0");
					}
				}else{//无线彩灯
					if ("autoMode".equals(operation) ||"automode".equals(operation)) {
						colorHolder.mColor.setVisibility(View.GONE);
						colorHolder.mColorMode.setVisibility(View.VISIBLE);
						connectionVO.getProductConnection().setBindStatus("1");
						colorHolder.mColorMode.setText("魔幻灯光");
					}else{
						colorHolder.mColor.setVisibility(View.VISIBLE);
						colorHolder.mColorMode.setVisibility(View.GONE);
						if (!TextUtils.isEmpty(strColor)) {
							try {
								JSONObject jb = new JSONObject(strColor);
								String _color = jb.getString("color");
								int intR = stringToInt((String) _color.subSequence(0, 3));
								int intG = stringToInt((String) _color.subSequence(3, 6));
								int intB = stringToInt((String) _color.subSequence(6, 9));
								colorHolder.mColor.setBackgroundColor(Color.rgb(intR, intG, intB));// 初始背景颜色
								connectionVO.getProductConnection().setBindStatus("1");
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (StringIndexOutOfBoundsException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							colorHolder.mColor.setBackgroundColor(Color.rgb(0, 0, 0));// 初始背景颜色
							connectionVO.getProductConnection().setBindStatus("0");
						}
					}
				}
			}else{
				colorHolder.mColor.setBackgroundColor(Color.rgb(0, 0, 0));// 初始背景颜色
			}
			
			//延时设置
			colorHolder.mTime.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (connectionVO.getProductConnection() != null) {
						showSaveDialog(context, connectionVO);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			if (connectionVO.getProductConnection() != null) {
				colorHolder.mTime.setText(connectionVO.getProductConnection().getTimeInterval()+"s");
				connectionVO.getProductConnection().setStatus(groupStatus);
			}
			
			break;
		case 2:
			curtainHolder.mName.setText(productFun.getFunName());
			curtainHolder.mCheck
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(
								CompoundButton buttonView, boolean isChecked) {
							connectionVO.setSelected(isChecked);
							if (isChecked) {
								addConnectionVO(connectionVO);
							}else{
								removeConnectionVO(connectionVO);
							}
							
							if (groupFun != null) {
								if (isChecked
										&& connectionVO.getProductConnection() == null) {// 选择设备，创建关联对象
									if (ProductConstants.FUN_TYPE_CURTAIN.equals(productFun.getFunType())) {
										connectionVO.setProductConnection(new ProductConnection(
												-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(), "0002",
														"close", "none", "none",
																productFun.getWhId(), defaultTime,isvalid));
									}else{
										connectionVO.setProductConnection(new ProductConnection(
												-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(), "01",
														"down", productFun.getFunUnit(), "none",
																productFun.getWhId(), defaultTime,isvalid));
									}
								}
							}else{
								JxshApp.showToast(context, "请先选择触发设备");
							}
							mHandler.sendEmptyMessage(0);
						}
					});
			curtainHolder.mCheck.setChecked(connectionVO.isSelected());
			//设置Item是否可选择
			if (groupFun == null || isAddedDevice(groupFun, productFun)) {
				curtainHolder.mCheck.setClickable(false);
				curtainHolder.mCheck.setFocusable(false);
			}else{
				curtainHolder.mCheck.setFocusable(true);
				curtainHolder.mCheck.setClickable(true);
			}

			curtainHolder.mOpenBtn.setImageDrawable(CommDefines.getSkinManager().
					drawable(connectionVO.isOpen() ?
							R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
			curtainHolder.mOpenBtn
					.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if (connectionVO.isSelected()) {
								connectionVO.setOpen(!connectionVO.isOpen());
								if (ProductConstants.FUN_TYPE_CURTAIN.equals(productFun.getFunType())) {
									connectionVO.getProductConnection()
									.setOperation(connectionVO.isOpen() ? "open":"close");
									connectionVO.getProductConnection().setBindStatus(connectionVO.isOpen()?"0001":"0002");
								}else{
									connectionVO.getProductConnection()
									.setOperation(connectionVO.isOpen() ? "open":"down");
									connectionVO.getProductConnection().
									setBindStatus(connectionVO.isOpen() ? "00":"01");
								}
								mHandler.sendEmptyMessage(0);
							}else{
								JxshApp.showToast(context, "请先选择设备");
							}
						}
					});

			curtainHolder.mTime.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (connectionVO.getProductConnection() != null) {
						showSaveDialog(context, connectionVO);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			if (connectionVO.isSelected()  && connectionVO.getProductConnection() != null) {
				curtainHolder.mTime.setText(connectionVO.getProductConnection().getTimeInterval()+"s");
				connectionVO.getProductConnection().setStatus(groupStatus);
			}
			break;
		case 3:
			doubleHolder.mName.setText(productFun.getFunName());
			doubleHolder.mCheck
			.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(
						CompoundButton buttonView, boolean isChecked) {
					connectionVO.setSelected(isChecked);
					if (isChecked) {
						addConnectionVO(connectionVO);
					}else{
						removeConnectionVO(connectionVO);
					}
					
					if (groupFun != null) {
						if (isChecked
								&& connectionVO.getProductConnection() == null) {// 选择设备，创建关联对象
//							JSONObject jb = new JSONObject();
							if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)){
//								try {
//									jb.put(StaticConstant.TYPE_SOCKET_LEFT, "off");
//									jb.put(StaticConstant.TYPE_SOCKET_RIGHT, "off");
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
								connectionVO
								.setProductConnection(new ProductConnection(
										-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(), "0000",
										OPERATION_DOUBLE_SOCKET, "", "none",
										productFun.getWhId(), defaultTime,isvalid));
							}
							if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)||
									productFun.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)||
									productFun.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)||
									productFun.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)||
									productFun.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)||
									productFun.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)){
//								try {
//									jb.put(StaticConstant.TYPE_SOCKET_KEY1, "off");
//									jb.put(StaticConstant.TYPE_SOCKET_KEY2, "off");
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
								connectionVO
								.setProductConnection(new ProductConnection(
										-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(), "0000",
										OPERATION_SIX_SOCKET, "", "none",
										productFun.getWhId(), defaultTime,isvalid)); 
							}
							if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)){
//								try {
//									jb.put(StaticConstant.TYPE_SOCKET_KEY1, "off");
//									jb.put(StaticConstant.TYPE_SOCKET_KEY2, "off");
//									jb.put(StaticConstant.TYPE_SOCKET_KEY3, "off");
//									jb.put(StaticConstant.TYPE_SOCKET_KEY4, "off");
//									jb.put(StaticConstant.TYPE_SOCKET_KEY5, "off");
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
								connectionVO
								.setProductConnection(new ProductConnection(
										-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(), "0000",
										OPERATION_FIVE_SOCKET, "", "none",
										productFun.getWhId(), defaultTime,isvalid)); 
							}
							if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)||
									productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)||
									productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)||
									productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)){
//								try {
//									jb.put(StaticConstant.TYPE_SOCKET_KEY1, "off");
//									jb.put(StaticConstant.TYPE_SOCKET_KEY2, "off");
//									jb.put(StaticConstant.TYPE_SOCKET_KEY3, "off");
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
								connectionVO
								.setProductConnection(new ProductConnection(
										-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(), "000000",
										OPERATION_THREE_SOCKET, "", "none",
										productFun.getWhId(), defaultTime,isvalid)); 
							}
						}
					}else{
						JxshApp.showToast(context, "请先选择触发设备");
					}
					mHandler.sendEmptyMessage(0);
				}
			});
			doubleHolder.mCheck.setChecked(connectionVO.isSelected());
			//设置Item是否可选择
			if (groupFun == null || isAddedDevice(groupFun, productFun)) {
				doubleHolder.mCheck.setClickable(false);
				doubleHolder.mCheck.setFocusable(false);
				if (groupFun == null) {
					JxshApp.showToast(context, "请先选择触发设备");
				}
			}else{
				doubleHolder.mCheck.setFocusable(true);
				doubleHolder.mCheck.setClickable(true);
			}
			
			doubleHolder.mSocket
					.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Fragment fragment = null;
							if (connectionVO.isSelected()) {
								if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)||
										productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)||
										productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)||
										productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)){
									fragment = new ConnectionThreeSwitchFragment();
								}else if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)){
									fragment = new ConnectionFiveSwitchFragment();
								}else{
									fragment = new ConnectionDoubleSocketFragment();
								}
								Bundle bundle = new Bundle();
								bundle.putSerializable("funDetail", funDetail);
								bundle.putSerializable("productConnectionVO", connectionVO);
								fragment.setArguments(bundle);
								addFragment(fragment, true);
							}else{
								JxshApp.showToast(context, "请先选择设备");
							}
						}
					});
			
			//延时设置
			doubleHolder.mTime.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (connectionVO.getProductConnection() != null) {
						showSaveDialog(context, connectionVO);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			if (connectionVO.getProductConnection() != null) {
				doubleHolder.mTime.setText(connectionVO.getProductConnection().getTimeInterval()+"s");
				doubleHolder.mSocket.setText(connectionVO.isOpen() ? "开启":"关闭");
//				connectionVO.getProductConnection().setBindStatus(connectionVO.isOpen()?"01":"00");//TODO
				connectionVO.getProductConnection().setStatus(groupStatus);
			}
			break;
		case 4://功放
			musicHolder.mName.setText(productFun.getFunName());
			
			musicHolder.mCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(
						CompoundButton buttonView, boolean isChecked) {
					connectionVO.setSelected(isChecked);
					if (isChecked) {
						addConnectionVO(connectionVO);
					}else{
						removeConnectionVO(connectionVO);
					}
					
					if (groupFun != null) {
						if (isChecked
								&& connectionVO.getProductConnection() == null) {// 选择设备，创建关联对象
							JSONObject jb = new JSONObject();
							try {
								jb.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, "00000000");
								jb.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, StaticConstant.INPUT_TYPE_USB);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							connectionVO.setProductConnection(new ProductConnection(
									-1, groupFun.getFunId(),groupFun.getWhId(),groupStatus, bindType,productFun.getFunId(), "0",
										OPERATION_PAUSE, jb.toString(), "none",
											productFun.getWhId(), defaultTime,isvalid));
						}
					}else{
						JxshApp.showToast(context, "请先选择设触发设备");
					}
					mHandler.sendEmptyMessage(0);
				}
			});
			musicHolder.mCheck.setChecked(connectionVO.isSelected());
			//设置Item是否可选择
			if (groupFun == null || isAddedDevice(groupFun, productFun)) {
				musicHolder.mCheck.setClickable(false);
				musicHolder.mCheck.setFocusable(false);
			}else{
				musicHolder.mCheck.setFocusable(true);
				musicHolder.mCheck.setClickable(true);
			}
			
			musicHolder.mOpenBtn.setImageDrawable(CommDefines.getSkinManager().
					drawable(connectionVO.isOpen() ?
							R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
			musicHolder.mOpenBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (connectionVO.isSelected()) {
						connectionVO.setOpen(!connectionVO.isOpen());
						connectionVO.getProductConnection()
							.setBindStatus(connectionVO.isOpen() ? "1":"0");
						connectionVO.getProductConnection()
							.setOperation(connectionVO.isOpen() ? OPERATION_PLAY : OPERATION_PAUSE);
						mHandler.sendEmptyMessage(0);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			//输入源设置
			musicHolder.mInPut.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Fragment fragment = null;
					if (connectionVO.isSelected()) {
						fragment = new ConnectionMusicSettingFragment();
						Bundle bundle = new Bundle();
						bundle.putSerializable("funDetail", funDetail);
						bundle.putSerializable("productConnectionVO", connectionVO);
						fragment.setArguments(bundle);
						addFragment(fragment, true);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			
			//输出源设置
			musicHolder.mOutPut.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Fragment fragment = null;
					if (connectionVO.isSelected()) {
						fragment = new ConnectionMusicSettingFragment();
						Bundle bundle = new Bundle();
						bundle.putSerializable("funDetail", funDetail);
						bundle.putSerializable("productConnectionVO", connectionVO);
						fragment.setArguments(bundle);
						addFragment(fragment, true);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			
			//延时设置
			musicHolder.mTime.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (connectionVO.getProductConnection() != null) {
						showSaveDialog(context, connectionVO);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			
			if (connectionVO.getProductConnection() != null) {
				JSONObject _jb;
				try {
					_jb = new JSONObject(connectionVO.getProductConnection().getParaDesc());
					String input = _jb.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
					String settedSpeaker = _jb
							.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
					musicHolder.mInPut.setTag(input);
					musicHolder.mOutPut.setText(getMusicOutput(settedSpeaker));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				musicHolder.mTime.setText(connectionVO.getProductConnection().getTimeInterval()+"s");
				connectionVO.getProductConnection().setStatus(groupStatus);
			}
			break;
		}

		return convertView;
	}
	

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		int viewType = 0;
		if (ProductConstants.FUN_TYPE_AUTO_LOCK.equals(groupList.get(groupPosition).getFunType())
				|| ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT.equals(groupList.get(groupPosition).getFunType())
				|| ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(groupList.get(groupPosition).getFunType())
				|| ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND
						.equals(groupList.get(groupPosition).getFunType())
						|| ProductConstants.FUN_TYPE_ONE_SWITCH.equals(groupList.get(groupPosition).getFunType())
						|| ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(groupList.get(groupPosition).getFunType())) {
			viewType = 0;
		} else if (ProductConstants.FUN_TYPE_COLOR_LIGHT
				.equals(groupList.get(groupPosition).getFunType())||
				groupList.get(groupPosition).getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) ||
				groupList.get(groupPosition).getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT) ||
				groupList.get(groupPosition).getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT) ||
				groupList.get(groupPosition).getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)) {//彩灯
			viewType = 1;
		} else if (ProductConstants.FUN_TYPE_CURTAIN.equals(groupList.get(groupPosition).getFunType())
				|| ProductConstants.FUN_TYPE_WIRELESS_CURTAIN
				.equals(groupList.get(groupPosition).getFunType())) {//窗帘
			viewType = 2;
		} else if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_THREE_SWITCH_THREE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_THREE_SWITCH_SIX
				.equals(groupList.get(groupPosition).getFunType())||
				ProductConstants.FUN_TYPE_FIVE_SWITCH
				.equals(groupList.get(groupPosition).getFunType())) {//双路开关
			viewType = 3;
		} else if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER
				.equals(groupList.get(groupPosition).getFunType())){//功放
			viewType = 4;
		} 
		
		return viewType;
	}

	@Override
	public int getChildTypeCount() {
		return 5;
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	class GroupHolder {
		TextView mGroupName;
		ImageView mIcon;

		public GroupHolder(View view) {
			mGroupName = (TextView) view.findViewById(R.id.group_item);
			mIcon = (ImageView) view.findViewById(R.id.item_image);
		}
	}

	class SwitchHolder {
		CheckBox mCheck;
		TextView mName, mTime;
		ImageButton mOpenBtn;

		public SwitchHolder(View view) {
			mCheck = (CheckBox) view.findViewById(R.id.item_cb_btn);
			mName = (TextView) view.findViewById(R.id.item_fun_name);
			mTime = (TextView) view.findViewById(R.id.item_time);
			mOpenBtn = (ImageButton)view.findViewById(R.id.imageButton_open);
		}
	}
	
	class DoubleSwitchHolder{
		CheckBox mCheck;
		TextView mName, mTime, mSocket;
		
		public DoubleSwitchHolder(View view) {
			mCheck = (CheckBox) view.findViewById(R.id.item_cb_btn);
			mName = (TextView) view.findViewById(R.id.item_fun_name);
			mTime = (TextView) view.findViewById(R.id.item_time);
			mSocket = (TextView) view.findViewById(R.id.item_select_socket);
			
		}
	}
	
	class CurtainHolder{
		CheckBox mCheck;
		TextView mName, mTime;
		ImageButton mOpenBtn;

		public CurtainHolder(View view) {
			mCheck = (CheckBox) view.findViewById(R.id.item_cb_btn);
			mName = (TextView) view.findViewById(R.id.item_fun_name);
			mTime = (TextView) view.findViewById(R.id.item_time);
			mOpenBtn = (ImageButton)view.findViewById(R.id.imageButton_open);
		}
	}
	
	class CorlorHolder{
		CheckBox mCheck;
		TextView mName, mTime, mColor, mColorMode;
		LinearLayout mColorSelect;

		public CorlorHolder(View view) {
			mCheck = (CheckBox) view.findViewById(R.id.item_cb_btn);
			mName = (TextView) view.findViewById(R.id.item_fun_name);
			mTime = (TextView) view.findViewById(R.id.item_time);
			mColorSelect = (LinearLayout) view.findViewById(R.id.item_ll_color_select);
			mColor = (TextView) view.findViewById(R.id.item_color);
			mColorMode = (TextView) view.findViewById(R.id.item_color_mode);
		}
	}
	
	class MusicHolder{
		CheckBox mCheck;
		TextView mName,mInPut, mOutPut, mTime;
		ImageView mOpenBtn;
		public MusicHolder(View view) {
			mCheck = (CheckBox) view.findViewById(R.id.item_cb_btn);
			mName = (TextView) view.findViewById(R.id.item_fun_name);
			mTime = (TextView) view.findViewById(R.id.item_time);
			mInPut = (TextView) view.findViewById(R.id.item_input);
			mOutPut = (TextView) view.findViewById(R.id.item_output);
			mOpenBtn = (ImageView) view.findViewById(R.id.imageButton_open);
		}
	}
	
	//检验该触发设备是否已经设置过该关联设备
	private boolean isAddedDevice(ProductFun groupFun,ProductFun productFun){
		boolean isAdded = false;
		if (groupFun.getFunId() == productFun.getFunId()) {
			isAdded = true;
			return isAdded;
		}
		List<ProductConnection> pcList = null;
		if (ProductConstants.FUN_TYPE_AIRCONDITION.equals(groupFun.getFunType())||
				ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(groupFun.getFunType())||
				ProductConstants.FUN_TYPE_ONE_SWITCH.equals(groupFun.getFunType())||
				ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(groupFun.getFunType())) {
			pcList = pcdImpl.find(null,
					"funId=? and status=?", new String[]{String.valueOf(groupFun.getFunId()),
						String.valueOf(groupFun.isOpen() ? "01":"00")}, null, null, null, null);
		}else if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(groupFun.getFunType())){//双路开关
			Logger.debug(null, "groupStatus = "+groupStatus);
		}else if(ProductConstants.FUN_TYPE_CURTAIN.equals(groupFun.getFunType())){//窗帘
			pcList = pcdImpl.find(null,
					"funId=? and status=?", new String[]{String.valueOf(groupFun.getFunId()),
						String.valueOf(groupFun.isOpen() ? "0001":"0002")}, null, null, null, null);
		}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(groupFun.getFunType())){//无线窗帘
			pcList = pcdImpl.find(null,
					"funId=? and status=?", new String[]{String.valueOf(groupFun.getFunId()),
						String.valueOf(groupFun.isOpen() ? "00":"01")}, null, null, null, null);
		}else{//其它设备
			pcList = pcdImpl.find(null,
					"funId=? and status=?", new String[]{String.valueOf(groupFun.getFunId()),
						String.valueOf(groupFun.isOpen() ? "1":"0")}, null, null, null, null);
		}
		
		if (pcList != null && pcList.size() > 0) {
			for (ProductConnection productConnection : pcList) {
				if (productConnection.getBindFunId() == productFun.getFunId()) {
					isAdded = true;
					break;
				}
			}
		}else{
			isAdded = false;
		}
		return isAdded;
	}
	
	/**
	 * 添加fragment
	 * @param fragment
	 * @param addToStack
	 */
	private void addFragment(Fragment fragment, boolean addToStack) {
		if (fragment != null && addToStack) {
			((AddConnectionControlActivity)context).getSupportFragmentManager().beginTransaction()
					.add(R.id.add_connection_framelayout, fragment)
					.addToBackStack(null).commit();
		} else if (fragment != null) {
			((AddConnectionControlActivity)context).getSupportFragmentManager().beginTransaction()
					.add(R.id.add_connection_framelayout, fragment).commit();
		}
	}

	private String getMusicOutput(String settedSpeaker){
		// init speakers
		FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(context);
		List<FunDetailConfig> fdc4Amplifiers = daoImpl.find(null, "funType=?",
				new String[] { ProductConstants.FUN_TYPE_POWER_AMPLIFIER },
				null, null, null, null);
		StringBuffer _sb = new StringBuffer();
		if (fdc4Amplifiers != null && fdc4Amplifiers.size() > 0) {
			FunDetailConfig fdc4Amplifier = fdc4Amplifiers.get(0);
			if (fdc4Amplifier != null
					&& !CommUtil.isEmpty(fdc4Amplifier.getParams())) {
				try {
					JSONObject jsonObj = new JSONObject(
							fdc4Amplifier.getParams());
					JSONArray jsonArr = jsonObj.getJSONArray("param");
					for (int i = 0; i < jsonArr.length(); i++) {
						String speaker = null;
						if (settedSpeaker != null
								&& settedSpeaker.length() == jsonArr.length()) {
							if (settedSpeaker.charAt(i) == '1') {
								speaker =  jsonArr.getString(i);
								_sb.append(speaker+",");
							}
						} 
					}
				} catch (JSONException e) {
					Logger.error(null,"AddConnectionExpandableAdapter music speakers init error");
				}
			}
		}
		if (_sb.toString().length() > 0) {
			return _sb.toString().substring(0, _sb.toString().length()-1);
		}else{
			return "未设置";
		}
	}
	
	private void addConnectionVO(ProductConnectionVO connectionVO){
		if (connectionVO == null) return;
		
		if (!TextUtils.isEmpty(mBuffer.toString())) {
			if (!mBuffer.toString().contains(String.valueOf(connectionVO.getProductFun().getFunId()))) {
				mBuffer.append(connectionVO.getProductFun().getFunId()+",");
				selectVoList.add(connectionVO);
			}
		}else{
			mBuffer.append(connectionVO.getProductFun().getFunId()+",");
			selectVoList.add(connectionVO);
		}
		
	}
	
	private void removeConnectionVO(ProductConnectionVO connectionVO){
		if (connectionVO == null) return;
		
		if (selectVoList != null  && selectVoList.size() > 0) {
			for (int i = 0; i < selectVoList.size(); i++) {
				if (connectionVO.getProductFun().getFunId() == selectVoList.get(i).getProductFun().getFunId()) {
					mBuffer = new StringBuffer(mBuffer.toString().replace(connectionVO.getProductFun().getFunId()+"", ""));
					selectVoList.remove(i);
				}
			}
		}
	}
	
	public List<ProductConnection> getConnectionList(){
		List<ProductConnection> pcList = new ArrayList<ProductConnection>();
		if (selectVoList != null  && selectVoList.size() > 0) {
			for (ProductConnectionVO productConnectionVO : selectVoList) {
				pcList.add(productConnectionVO.getProductConnection());
			}
		}
		return pcList;
	}
	
	/**
	 * 设置时间
	 */
	private void showSaveDialog(Context context, final ProductConnectionVO connnectionVO) {
		if (context == null || connnectionVO == null) {
			return;
		}

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.setting_time_dialog_layout, null);
		final CustomerCenterDialog dialog = new CustomerCenterDialog(context,
				R.style.dialog, v);

		final EditText etName = (EditText) v.findViewById(R.id.et_time);
		Button btnSure = (Button) v.findViewById(R.id.button_ok);
		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String time = etName.getText().toString();
				if (!TextUtils.isEmpty(time)) {
					connnectionVO.getProductConnection().setTimeInterval(time);
					mHandler.sendEmptyMessage(0);
					dialog.dismiss();
				}
			}
		});

		btnCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private int stringToInt(String str) {
		int i = 0;
		if (str.startsWith("00")) {
			i = Integer.parseInt(str.substring(2));
			return i;
		} else if (str.startsWith("0")) {
			i = Integer.parseInt(str.substring(1));
			return i;
		} else {
			i = Integer.parseInt(str);
		}
		return i;
	}

}
