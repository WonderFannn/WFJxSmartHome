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
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.ModeFiveSwitchListAdapter;
import com.jinxin.jxsmarthome.ui.adapter.ModeFiveSwitchListAdapter.OnSwitchCheckBoxClickListener;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
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

public class ModeFiveSocketFragment extends DialogFragment implements OnSwitchCheckBoxClickListener {

	/**
	 * 开关列表
	 */
	private ListView lvFiveSwitch;

	/**
	 * 开关列表适配器
	 */
	private ModeFiveSwitchListAdapter switchListAdapter;

	/**
	 * 模式相关
	 */
	private ProductFunVO productFunVO;

	/**
	 * 设备相关
	 */
	private FunDetail funDetail;

	/**
	 * 开关状态列表
	 */
	private boolean[] switchStatus;
	/**
	 * 开关可使用状态
	 */
	private boolean[] switchEnableStatus;

	/**
	 * 模式参数 "key1:on;key2:off..."
	 */
	private String opreationDesc;

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

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.save_mode_btn);
		inflater.inflate(R.menu.menu_for_mode, menu);
		// 返回菜单
		((BaseActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity) getActivity()).getSupportActionBar().setTitle("配置五路交流开关");
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

	private void initData() {
		productFunVO = (ProductFunVO) getArguments().get("productFunVO");
		funDetail = (FunDetail) getArguments().get("funDetail");
		switchStatus = new boolean[5];
		switchEnableStatus = new boolean[5];
		if (productFunVO != null) {
			opreationDesc = productFunVO.getProductPatternOperation().getParaDesc();
			if (!TextUtils.isEmpty(opreationDesc)) {
				try {
					JSONObject jb = new JSONObject(opreationDesc);
					@SuppressWarnings("rawtypes")
					Iterator keyIter = jb.keys();
					String key;
					String value;
					if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)) {
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
				productFunVO.getProductPatternOperation().setParaDesc(statusToJson(switchEnableStatus, switchStatus));
			}
		}
	}

	private void initView(View view) {
		lvFiveSwitch = (ListView) view.findViewById(R.id.lv_five_switch);
		switchListAdapter = new ModeFiveSwitchListAdapter(this, funDetail, switchEnableStatus, switchStatus);
		lvFiveSwitch.setAdapter(switchListAdapter);
	}

	private String statusToJson(boolean[] enableStatus, boolean[] status) {
		JSONObject jb = new JSONObject();
		try {
			for (int i = 0; i < status.length; i++) {
				if (enableStatus[i])
					jb.put(StaticConstant.FIVE_KEY_MAP[i], status[i] ? "on" : "off");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Logger.debug(null, jb.toString());
		return jb.toString();
	}

	@Override
	public void onSwitchCheckBoxEnableClick(int pos) {
		switchEnableStatus[pos] = !switchEnableStatus[pos];
		switchListAdapter.notifyDataSetChanged(switchEnableStatus, switchStatus);
		productFunVO.getProductPatternOperation().setParaDesc(statusToJson(switchEnableStatus, switchStatus));
	}

	@Override
	public void onSwitchCheckBoxClick(int pos) {
		if (switchEnableStatus[pos]) {
			switchStatus[pos] = !switchStatus[pos];
			switchListAdapter.notifyDataSetChanged(switchEnableStatus, switchStatus);
			productFunVO.getProductPatternOperation().setParaDesc(statusToJson(switchEnableStatus, switchStatus));
		} else {
			JxshApp.showToast(getActivity(), getActivity().getString(R.string.qing_xian_da_kai_she_bei));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		productFunVO.getProductPatternOperation().setParaDesc(statusToJson(switchEnableStatus, switchStatus));
		boolean flagOpen = false;
		for (int i = 0; i < switchEnableStatus.length; i++) {
			if (switchEnableStatus[i])
				flagOpen = true;
		}
		if (flagOpen) {
			productFunVO.setOpen(true);
		} else {
			productFunVO.setOpen(false);
			JxshApp.showToast(getActivity(), "未设置具体操作");
		}
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_REFRESH_CONNECTION_UI, null);
	}

}
