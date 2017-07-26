package com.jinxin.jxsmarthome.fragment;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 单路开关
 * 
 * @author Huangl
 * @company 金鑫智慧
 */
public class ModeOneSocketFragment extends DialogFragment {

	/**
	 * 开关按钮
	 */
	private ImageView ivSocket;
	/**
	 * 状态显示
	 */
	private TextView tvSocketState;
	/**
	 * 启用按钮
	 */
	private ImageButton ibEnable;

	/**
	 * 模式参数实体
	 */
	private ProductFunVO productFunVO;
	// /**
	// * 设备类
	// */
	// private FunDetail funDetail;
	/**
	 * 模式操作参数字符 "on/off"
	 */
	private String operation;

	/**
	 * 开关是否可用
	 */
	private boolean isSocketEnable;
	/**
	 * 开关是否打开
	 */
	private boolean isSocketOpen;

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = CommDefines.getSkinManager().view(R.layout.mode_one_socket_layout, container);
		initData();
		initView(view);
		return view;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.save_mode_btn);
		inflater.inflate(R.menu.menu_for_mode, menu);
		((BaseActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity) getActivity()).getSupportActionBar().setTitle("配置单路开关");
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
		// funDetail = (FunDetail) getArguments().get("funDetail");
		if (productFunVO != null) {
			operation = productFunVO.getProductPatternOperation().getOperation();
			if (!TextUtils.isEmpty(operation)) {
				isSocketEnable = true;
				isSocketOpen = operation.equals("on") ? true : false;
			} else {
				// 设置默认模式配置参数
				isSocketEnable = false;
				isSocketOpen = false;
				productFunVO.getProductPatternOperation().setOperation("off");
			}
		}
	}

	private void initView(View view) {
		ivSocket = (ImageView) view.findViewById(R.id.iv_socket);
		ibEnable = (ImageButton) view.findViewById(R.id.ib_enbale);
		updateUI();

		ibEnable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSocketEnable = !isSocketEnable;
				updateUI();
			}
		});
		ivSocket.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSocketOpen = !isSocketOpen;
				updateUI();
			}
		});
	}

	private void updateUI() {
		ibEnable.setBackgroundResource(isSocketEnable ? R.drawable.mode_switch_on : R.drawable.mode_switch_on);
		ivSocket.setBackgroundResource(isSocketOpen ? R.drawable.icon_socket_open : R.drawable.icon_socket_close);
		tvSocketState.setText(isSocketOpen ? "状态：开" : "状态：关");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (isSocketEnable) {
			productFunVO.setOpen(true);
			productFunVO.getProductPatternOperation().setOperation(isSocketOpen ? "on" : "off");
		} else {
			productFunVO.setOpen(false);
			JxshApp.showToast(getActivity(), "未设置具体操作");
		}
	}
}
