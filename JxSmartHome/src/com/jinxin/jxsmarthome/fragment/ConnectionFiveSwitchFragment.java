package com.jinxin.jxsmarthome.fragment;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductConnectionVO;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.ModeFiveSwitchListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.ModeFiveSwitchListAdapter.OnSwitchCheckBoxClickListener;
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
import android.view.ViewGroup;
import android.widget.ListView;

public class ConnectionFiveSwitchFragment extends DialogFragment implements OnSwitchCheckBoxClickListener {

	/**
	 * 关联参数
	 */
	private ProductConnectionVO connectionVO;
	/**
	 * 设备参数
	 */
	private FunDetail funDetail;
	/**
	 * 操作参数
	 */
	private String opreationDesc;
	/**
	 * 开关列表
	 */
	private ListView lvFiveSwitch;
	/**
	 * 开关列表适配器
	 */
	private ModeFiveSwitchListAdapter switchListAdapter;
	/**
	 * 开关状态列表
	 */
	private boolean[] switchStatus;
	/**
	 * 开关使用状态
	 */
	private boolean[] switchEnableStatus;

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = CommDefines.getSkinManager().view(R.layout.fragment_five_switch, container);
		initData();
		initView(view);
		return view;
	}

	private void initData() {
		connectionVO = (ProductConnectionVO) getArguments().get("productConnectionVO");
		funDetail = (FunDetail) getArguments().get("funDetail");
		String funtype = funDetail.getFunType();
		if (connectionVO != null) {
			opreationDesc = connectionVO.getProductConnection().getParaDesc();
			if (!TextUtils.isEmpty(opreationDesc)) {
				try {
					JSONObject jb = new JSONObject(opreationDesc);
					Iterator<String> keyIter = jb.keys();
					String key;
					String value;
					if (funtype.equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)) {
						switchStatus = new boolean[5];
						switchEnableStatus = new boolean[5];
						while (keyIter.hasNext()) {
							key = (String) keyIter.next();
							value = (String) jb.get(key);
							for (int i = 0; i < StaticConstant.FIVE_KEY_MAP.length; i++) {
								if (StaticConstant.FIVE_KEY_MAP[i].equals(key)) {
									switchEnableStatus[i] = true;
									switchStatus[i] = value.equalsIgnoreCase("on") ? true : false;
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				if (funtype.equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)) {
					switchStatus = new boolean[5];
					switchEnableStatus = new boolean[5];
				}
				connectionVO.getProductConnection()
						.setParaDesc(statusToJson(funDetail.getFunType(), switchEnableStatus, switchStatus));
				connectionVO.getProductConnection().setBindStatus(isOpen(switchEnableStatus)? "1" : "0");
			}
		}
	}

	private void initView(View view) {
		lvFiveSwitch = (ListView) view.findViewById(R.id.lv_five_switch);
		switchListAdapter = new ModeFiveSwitchListAdapter(this, funDetail, switchEnableStatus, switchStatus);
		lvFiveSwitch.setAdapter(switchListAdapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.save_mode_btn);
		inflater.inflate(R.menu.menu_for_mode, menu);
		((BaseActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity) getActivity()).getSupportActionBar().setTitle("配置五路开关");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btn_save:
			((BaseActionBarActivity) getActivity()).onBackPressed();
			break;
		default:
			break;
		}
		return true;
	}

	private String statusToJson(String funtype, boolean[] enableStatus, boolean[] status) {
		JSONObject jb = new JSONObject();
		for (int i = 0; i < status.length; i++) {
			try {
				if (enableStatus[i])
					jb.put(StaticConstant.TYPE_SOCKET_KEYS[i], status[i] ? "on" : "off");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Logger.debug(null, jb.toString());
		return jb.toString();
	}

	@Override
	public void onSwitchCheckBoxEnableClick(int pos) {
		switchEnableStatus[pos] = !switchEnableStatus[pos];
		switchListAdapter.notifyDataSetChanged(switchEnableStatus, switchStatus);
		connectionVO.getProductConnection()
				.setParaDesc(statusToJson(funDetail.getFunType(), switchEnableStatus, switchStatus));
	}

	@Override
	public void onSwitchCheckBoxClick(int pos) {
		if (switchEnableStatus[pos]) {
			switchStatus[pos] = !switchStatus[pos];
			switchListAdapter.notifyDataSetChanged(switchEnableStatus, switchStatus);
			connectionVO.getProductConnection()
					.setParaDesc(statusToJson(funDetail.getFunType(), switchEnableStatus, switchStatus));
		} else {
			JxshApp.showToast(getActivity(), getActivity().getString(R.string.qing_xian_da_kai_she_bei));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		connectionVO.getProductConnection()
				.setParaDesc(statusToJson(funDetail.getFunType(), switchEnableStatus, switchStatus));
		if (isOpen(switchEnableStatus)) {
			connectionVO.setOpen(true);
			connectionVO.getProductConnection().setBindStatus("1");
		} else {
			connectionVO.setOpen(false);
			connectionVO.getProductConnection().setBindStatus("0");
			JxshApp.showToast(getActivity(), "未设置具体操作");
		}
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_REFRESH_CONNECTION_UI, null);
	}

	private boolean isOpen(boolean[] status) {
		boolean flagOpen = false;
		for (int i = 0; i < switchEnableStatus.length; i++) {
			if (switchEnableStatus[i])
				flagOpen = true;
		}
		return flagOpen;
	}
}
