package com.jinxin.jxsmarthome.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;

/**
 * 窗帘详细控制
 * @author YangJijun
 * @company 金鑫智慧
 */
public class ModeCurtainFragment extends DialogFragment implements OnClickListener{
	
	private View view = null;
	private ImageView curtainUp, curtainDown, curtainStop;
	private ImageView curtainMode1,curtainMode2,curtainMode3,curtainMode4;
	
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private ProductFunVO productFunVO = null;
	private Context context;
	private static String type = "";
	private Map<String, Object> params = null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
	
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
		view=CommDefines.getSkinManager().view(R.layout.curtain_up_down_layout, container);
		initData();
		initView(view);
		return view;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.save_mode_btn);
		inflater.inflate(R.menu.menu_my, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("配置窗帘");
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
		productFunVO = (ProductFunVO) getArguments().get("productFunVO");
		funDetail = (FunDetail) getArguments().get("funDetail");
		if (productFunVO != null) {
			productFun = productFunVO.getProductFun();
		}
	}

	private void initView(View view) {
		this.curtainUp = (ImageView) view.findViewById(R.id.curtain_up);
		this.curtainDown = (ImageView) view.findViewById(R.id.curtain_down);
		this.curtainStop = (ImageView) view.findViewById(R.id.curtain_stop);
		this.curtainMode1 = (ImageView) view.findViewById(R.id.curtain_mode_1);
		this.curtainMode2 = (ImageView) view.findViewById(R.id.curtain_mode_2);
		this.curtainMode3 = (ImageView) view.findViewById(R.id.curtain_mode_3);
		this.curtainMode4 = (ImageView) view.findViewById(R.id.curtain_mode_4);
		
		curtainUp.setOnClickListener(this);
		curtainDown.setOnClickListener(this);
		curtainStop.setOnClickListener(this);
		curtainMode1.setOnClickListener(this);
		curtainMode2.setOnClickListener(this);
		curtainMode3.setOnClickListener(this);
		curtainMode4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (productFunVO == null) return;
		params = new HashMap<String, Object>();
		
		switch (v.getId()) {
		case R.id.curtain_up:
			productFunVO.getProductPatternOperation().setOperation("up");
			productFunVO.getProductPatternOperation().setParaDesc(null);
			break;
		case R.id.curtain_down:
			productFunVO.getProductPatternOperation().setOperation("down");
			productFunVO.getProductPatternOperation().setParaDesc(null);
			break;
		case R.id.curtain_stop:
			productFunVO.getProductPatternOperation().setOperation("stop");
			productFunVO.getProductPatternOperation().setParaDesc(null);
			break;
		case R.id.curtain_mode_1://窗帘控制 1/3
			productFunVO.getProductPatternOperation().setOperation("offset");
			productFunVO.getProductPatternOperation().setParaDesc(Integer.toString(33));
			break;
		case R.id.curtain_mode_2://窗帘控制 1/2
			productFunVO.getProductPatternOperation().setOperation("offset");
			productFunVO.getProductPatternOperation().setParaDesc(Integer.toString(50));
			break;
		case R.id.curtain_mode_3://窗帘控制 2/3
			productFunVO.getProductPatternOperation().setOperation("offset");
			productFunVO.getProductPatternOperation().setParaDesc(Integer.toString(65));
			break;
		case R.id.curtain_mode_4://窗帘控制 3/4
			productFunVO.getProductPatternOperation().setOperation("offset");
			productFunVO.getProductPatternOperation().setParaDesc(Integer.toString(75));
			break;
		}
		JxshApp.showToast(context, "设置已保存");
	}
	
	public static String setType(String tem){
		type = tem;
		return type;
	}
	
	public static String getType(){
		return type;
	}
	
	/**
	 * 窗帘控制 发送指令
	 * @param context
	 * @param productFun
	 * @param funDetail
	 */
	public void curtainControl(final Context context,
			final ProductFun productFun,FunDetail funDetail,Map<String, Object> params){
		List<byte[]> cmdList = CommonMethod.
				productFunToCMD(context, productFun, funDetail, params);
		if(cmdList == null || cmdList.size() < 1) {
			return;
		}
			byte[] cmd = cmdList.get(0);
			CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(context, cmd,false);
			cdcbsTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, CommDefines.getSkinManager().string(R.string.qing_qiu_chu_li_zhong));
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
//				productFun.setOpen(!productFun.isOpen());
				//刷新
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		cdcbsTask.start();
	}
}
