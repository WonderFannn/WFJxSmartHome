package com.jinxin.jxsmarthome.fragment;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductConnectionVO;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.Logger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 联动双向插座配置
 * @author YangJijun
 * @company 金鑫智慧
 */
public class ConnectionDoubleSocketFragment extends DialogFragment implements OnClickListener{
	
	private Button leftSwitch, rightSwitch;
	private ImageView switchBtn = null;
	private TextView tvState = null;
	private ImageButton openCheck = null;
	
	private ProductConnectionVO connectionVO = null;
	private FunDetail funDetail = null;
	private String opreationDesc = "";// 格式：l-on|r-off
	
	private boolean isLeftSide = true;
	private String funtype;
	private boolean isLeftEnable = false;//左开关是否启用
	private boolean isRightEnable = false;//右开关是否启用
	private boolean isLeftOpen = false;
	private boolean isRightOpen = false;
	private String leftStatus = "";
	private String rightStatus = "";
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=CommDefines.getSkinManager().view(R.layout.mode_double_socket_layout, container);
		initData();
		initView(view);
		return view;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.save_mode_btn);
		inflater.inflate(R.menu.menu_for_mode, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if (funDetail != null) {
			if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH))
				((BaseActionBarActivity) getActivity()).getSupportActionBar().setTitle("配置双路开关");
			if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)
					|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)
					|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)
					|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)
					|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)
					|| funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)) {
				((BaseActionBarActivity) getActivity()).getSupportActionBar().setTitle("配置多路开关");
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btn_save:
			((BaseActionBarActivity)getActivity()).onBackPressed();
			break;
		default:
			break;
		}
		return true;
	}

	private void initData() {
		connectionVO = (ProductConnectionVO) getArguments().get("productConnectionVO");
		funDetail = (FunDetail) getArguments().get("funDetail");
		funtype = funDetail.getFunType();
		if (connectionVO != null) {
			opreationDesc = connectionVO.getProductConnection().getParaDesc();
			if (!TextUtils.isEmpty(opreationDesc)) {
				try {
					JSONObject jb = new JSONObject(opreationDesc);
					Iterator<String> keyIter = jb.keys();
					String key;
					String value;
					if (funtype.equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)) {
						while (keyIter.hasNext()) {
							key = (String) keyIter.next();
							value = (String) jb.get(key);
							if ("left".equals(key)) {
								isLeftEnable = true;
								leftStatus = value;
							} else if ("right".equals(key)) {
								isRightEnable = true;
								rightStatus = value;
							}
						}
					}
					if (ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funtype)
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funtype)
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funtype)
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funtype)
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funtype)
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funtype)) {
						while (keyIter.hasNext()) {
							key = (String) keyIter.next();
							value = (String) jb.get(key);
							if ("key1".equals(key)) {
								isLeftEnable = true;
								leftStatus = value;
							} else if ("key2".equals(key)) {
								isRightEnable = true;
								rightStatus = value;
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				isLeftOpen = leftStatus.equals("on") ? true : false;
				isRightOpen = rightStatus.equals("on") ? true : false;
			}else{
				leftStatus = "off";
				rightStatus = "off";
				isLeftEnable = false;
				isRightEnable = false;
				isLeftOpen = false;
				isRightOpen = false;
				//设置默认模式配置参数
				connectionVO.getProductConnection().setParaDesc(statusToJson(funtype,leftStatus, rightStatus));
				connectionVO.getProductConnection().setBindStatus(isLeftEnable | isRightEnable ? "1" : "0");
			}
		}
	}

	private void initView(View view) {
		this.leftSwitch = (Button) view.findViewById(R.id.btn_socket1);
		this.rightSwitch = (Button) view.findViewById(R.id.btn_socket2);
		this.switchBtn = (ImageView) view.findViewById(R.id.iv_socket_switch);
		this.tvState = (TextView) view.findViewById(R.id.tv_socket_state);
		this.openCheck = (ImageButton) view.findViewById(R.id.cb_open_switch);
		updateUI();
		
		this.leftSwitch.setOnClickListener(this);
		this.rightSwitch.setOnClickListener(this);
		this.switchBtn.setOnClickListener(this);
		this.openCheck.setOnClickListener(this);
		
		if (ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funDetail.getFunType())
				|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funDetail.getFunType())
				|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funDetail.getFunType())
				|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funDetail.getFunType())
				|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funDetail.getFunType())
				|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funDetail.getFunType())) {
			this.leftSwitch.setText("键一");
			this.rightSwitch.setText("键二");
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_socket1://选择设置左边开关
			isLeftSide = true;
			updateUI();
			break;
		case R.id.btn_socket2://选择设置右边边开关
			isLeftSide = false;
			updateUI();
			break;
		case R.id.cb_open_switch:
			if (isLeftSide) {
				isLeftEnable = !isLeftEnable;
			} else {
				isRightEnable = !isRightEnable;
			}
			connectionVO.getProductConnection().setParaDesc(statusToJson(funtype,leftStatus, rightStatus));
			connectionVO.getProductConnection().setBindStatus(isLeftEnable | isRightEnable ? "1" : "0");
			updateUI();
			break;
		case R.id.iv_socket_switch:
			if (isLeftSide) {//设置左路开关状态
				if (isLeftEnable) {
					isLeftOpen = !isLeftOpen;
					leftStatus = isLeftOpen ? "on" : "off";
					changSwitchBackground(isLeftOpen);
				}else{
					if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funDetail.getFunType())) {
						JxshApp.showToast(getActivity(), "请先启用左路开关再进行设置");
					} else if (ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funDetail.getFunType())) {// 六路开关
						JxshApp.showToast(getActivity(), "请先启用键一开关再进行设置");
					}
				}
			}else{//设置右路开关状态
				if (isRightEnable) {
					isRightOpen = !isRightOpen;
					rightStatus = isRightOpen ? "on" : "off";
					changSwitchBackground(isRightOpen);
				}else{
					if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funDetail.getFunType())) {
						JxshApp.showToast(getActivity(), "请先启用右路开关再进行设置");
					} else if (ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funDetail.getFunType())
							|| ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funDetail.getFunType())) {// 六路开关
						JxshApp.showToast(getActivity(), "请先启用键二开关再进行设置");
					}
				}
			}
			connectionVO.getProductConnection().setParaDesc(statusToJson(funDetail.getFunType(),leftStatus, rightStatus));
			connectionVO.getProductConnection().setBindStatus(isLeftEnable | isRightEnable ? "1" : "0");
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(isLeftEnable ||isRightEnable){
			connectionVO.setOpen(true);
		}else{
			connectionVO.setOpen(false);
			JxshApp.showToast(getActivity(), "未设置具体操作");
		}
		connectionVO.getProductConnection().setBindStatus(isLeftEnable | isRightEnable ? "1" : "0");
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_REFRESH_CONNECTION_UI,null);
	}

	
	/**
	 * 设置当前设置的开关状态
	 * @param isOpen
	 */
	private void changSwitchBackground(boolean isOpen){
		switchBtn.setImageResource(isOpen ? 
				R.drawable.icon_socket_open :R.drawable.icon_socket_close);
		tvState.setText(isOpen ? "状态：开" : "状态：关");
	}
	
	private void updateUI() {
		if (isLeftSide) {
			leftSwitch.setBackgroundResource(R.drawable.button_right_selected_bg);
			rightSwitch.setBackgroundResource(R.drawable.button_left_unselect_bg);
			if (isLeftEnable) {
				openCheck.setBackgroundResource(R.drawable.mode_switch_on);
			} else {
				openCheck.setBackgroundResource(R.drawable.mode_switch_off);
			}
			changSwitchBackground(isLeftOpen);
		} else {
			leftSwitch.setBackgroundResource(R.drawable.button_rigtht_unselect_bg);
			rightSwitch.setBackgroundResource(R.drawable.button_left_selected_bg);
			if (isRightEnable) {
				openCheck.setBackgroundResource(R.drawable.mode_switch_on);
			} else {
				openCheck.setBackgroundResource(R.drawable.mode_switch_off);
			}
			changSwitchBackground(isRightOpen);
		}
	}
	
	private String statusToJson(String funtype,String leftStatus,String rightStatus){
		if(funtype.equalsIgnoreCase(ProductConstants.FUN_TYPE_DOUBLE_SWITCH))
			return statusToJson(leftStatus, rightStatus);
		if(funtype.equalsIgnoreCase(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)||
				funtype.equalsIgnoreCase(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)||
				funtype.equalsIgnoreCase(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)||
				funtype.equalsIgnoreCase(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)||
				funtype.equalsIgnoreCase(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)||
				funtype.equalsIgnoreCase(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)){
			return statusToJsonSix(leftStatus, rightStatus);
		}
		return statusToJson(leftStatus, rightStatus);
	}
	
	private String statusToJson(String leftStatus, String rightStatus){
		JSONObject  jb = new JSONObject();
		try {
			if (isLeftEnable && !TextUtils.isEmpty(leftStatus)) {
				jb.put("left", leftStatus);
			}
			if (isRightEnable && !TextUtils.isEmpty(rightStatus)) {
				jb.put("right", rightStatus);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Logger.debug(null, jb.toString());
		return jb.toString();
	}
	
	/**
	 * 六路开关参数转json
	 * @param key1    键一状态
	 * @param key2   键二状态
	 * @return str
	 */
	private String statusToJsonSix(String key1, String key2) {
		JSONObject jb = new JSONObject();
		try {
			if (isLeftEnable && !TextUtils.isEmpty(key1)) {
				jb.put("key1", key1);
			}
			if (isRightEnable && !TextUtils.isEmpty(key2)) {
				jb.put("key2", key2);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Logger.debug(null, jb.toString());
		return jb.toString();
	}
	
}
