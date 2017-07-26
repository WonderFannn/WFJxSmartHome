package com.jinxin.jxsmarthome.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jinxin.datan.net.command.CodeLibraryTask;
import com.jinxin.datan.net.command.ProductFunListTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigFunTask;
import com.jinxin.datan.net.command.UpdateProductRemoteConfigTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigDaoImpl;
import com.jinxin.db.impl.ProductRemoteConfigFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.RemoteBrandsTypeActivity;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfig;
import com.jinxin.jxsmarthome.entity.ProductRemoteConfigFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 自定义电视控制学习界面
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class RemoteTVLearnFragment extends Fragment implements OnClickListener{
	
	private Context context;
	private int type = 2;//1—码库方式控制， 2—学习指令， 3-学习方式控制
	private CustomerBrands cBrand;
	private RelativeLayout okLayout;
	private LinearLayout numLayout;
	private int successCount = 0;
	private CustomerCenterDialog dialog = null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_my, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("电视遥控学习");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
		}
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_tv_control_layout, container, false);
		initData();
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		okLayout = (RelativeLayout) view.findViewById(R.id.ok_layout);
		numLayout = (LinearLayout) view.findViewById(R.id.num_layout);
		
		view.findViewById(R.id.tv_open).setOnClickListener(this);
		view.findViewById(R.id.tv_mode).setOnClickListener(this);
		view.findViewById(R.id.tv_mute).setOnClickListener(this);
		view.findViewById(R.id.btn_up).setOnClickListener(this);
		view.findViewById(R.id.btn_right).setOnClickListener(this);
		view.findViewById(R.id.iv_ok).setOnClickListener(this);
		view.findViewById(R.id.btn_left).setOnClickListener(this);
		view.findViewById(R.id.btn_down).setOnClickListener(this);
		view.findViewById(R.id.tv_back).setOnClickListener(this);
		view.findViewById(R.id.tv_home).setOnClickListener(this);
		view.findViewById(R.id.tv_more).setOnClickListener(this);
		//数字键
		view.findViewById(R.id.btn_num1).setOnClickListener(this);
		view.findViewById(R.id.btn_num2).setOnClickListener(this);
		view.findViewById(R.id.btn_num3).setOnClickListener(this);
		view.findViewById(R.id.btn_num4).setOnClickListener(this);
		view.findViewById(R.id.btn_num5).setOnClickListener(this);
		view.findViewById(R.id.btn_num6).setOnClickListener(this);
		view.findViewById(R.id.btn_num7).setOnClickListener(this);
		view.findViewById(R.id.btn_num8).setOnClickListener(this);
		view.findViewById(R.id.btn_num9).setOnClickListener(this);
		view.findViewById(R.id.btn_select_switch).setOnClickListener(this);
		view.findViewById(R.id.btn_num0).setOnClickListener(this);
		view.findViewById(R.id.btn_sleep).setOnClickListener(this);
	}

	private void initData() {
		cBrand = (CustomerBrands) getArguments().getSerializable(RemoteBrandsTypeActivity.BRAND);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_open:
			sendUFOCmd("0");
			break;
		case R.id.tv_mode://切换中介控制区域
			if (okLayout.isShown()) {
				okLayout.setVisibility(View.INVISIBLE);
				numLayout.setVisibility(View.VISIBLE);
			}else{
				okLayout.setVisibility(View.VISIBLE);
				numLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.tv_mute:
			sendUFOCmd("37");
			break;
		case R.id.btn_up://音量+
			sendUFOCmd("33");
			break;
		case R.id.btn_right://频道+
			sendUFOCmd("35");
			break;
		case R.id.iv_ok://画中画
			sendUFOCmd("18");
			break;
		case R.id.btn_left:
			sendUFOCmd("36");
			break;
		case R.id.btn_down:
			sendUFOCmd("34");
			break;
		case R.id.tv_back:
			sendUFOCmd("17");
			break;
		case R.id.tv_home:
			sendUFOCmd("29");
			break;
		case R.id.tv_more://TODO
			JxshApp.showToast(context, "暂不支持遥控板扩展");
			break;
		case R.id.btn_num1:
			sendUFOCmd("5");
			break;
		case R.id.btn_num2:
			sendUFOCmd("6");
			break;
		case R.id.btn_num3:
			sendUFOCmd("7");
			break;
		case R.id.btn_num4:
			sendUFOCmd("8");
			break;
		case R.id.btn_num5:
			sendUFOCmd("9");
			break;
		case R.id.btn_num6:
			sendUFOCmd("10");
			break;
		case R.id.btn_num7:
			sendUFOCmd("11");
			break;
		case R.id.btn_num8:
			sendUFOCmd("12");
			break;
		case R.id.btn_num9:
			sendUFOCmd("13");
			break;
		case R.id.btn_select_switch:
			sendUFOCmd("14");
			break;
		case R.id.btn_num0:
			sendUFOCmd("15");
			break;
		case R.id.btn_sleep:
			sendUFOCmd("4");
			break;
		}
	}
	
	private void sendUFOCmd(String code) {
		if (cBrand == null || TextUtils.isEmpty(code)) {
			return;
		}
		String address485 = getAddress485ByWhId(cBrand.getWhId());


		CodeLibraryTask cLibraryTask = new CodeLibraryTask(getActivity(),
				cBrand.getmCode(), cBrand.getWhId(), "",
				cBrand.getDeviceId(), cBrand.getId(), address485, type, code,
				cBrand.getId());
		cLibraryTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
//				JxshApp.showToast(context, "指令已发送，请稍后...");
				handler.sendEmptyMessage(0);
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				loadData();
//				handler.sendEmptyMessageDelayed(1, 2000);
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {

			}
		});
		cLibraryTask.start();

	}
	
	/**
	 * 保存遥控器
	 */
	private void showSaveDialog(){
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.remote_info_receive_dialog, null);
		dialog = new CustomerCenterDialog(context, R.style.dialog, v);

		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	/**
	 * 获取学习按键对应的数据
	 */
	private void loadData(){
		successCount = 0;
		
		// 更新产品功能列表
		ProductFunListTask pflTask = new ProductFunListTask(
				null);
		pflTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
//				JxshApp.showLoading(context,"同步数据...");
			}

			@Override
			public void onCanceled(ITask task,
					Object arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task,
					Object[] arg) {
				/****** 产品功能更新 *********/

				if (arg != null && arg.length > 0) {
					List<ProductFun> productFunList = (List<ProductFun>) arg[0];
					CommonMethod.updateProductFunList(
							context,
							productFunList);
				}
				successCount++;
				if (successCount == 3 ) {
					handler.sendEmptyMessage(1);
				}
			}

			@Override
			public void onProcess(ITask task,
					Object[] arg) {
				
			}
		});
		pflTask.start();
		
		Logger.warn(null, "更新遥控配置信息2");
		UpdateProductRemoteConfigTask uprcTask = new UpdateProductRemoteConfigTask(null);
		final ProductRemoteConfigDaoImpl uprcDao = new ProductRemoteConfigDaoImpl(context);
		uprcTask.addListener(new TaskListener<ITask>() {

			@Override
			public void onCanceled(ITask task, Object arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				Logger.warn(null, "onFail");
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				Logger.warn(null, "onSuccess");
				if(arg != null && arg.length > 0){
					List<ProductRemoteConfig> prs = (List<ProductRemoteConfig>)arg[0];
					for(ProductRemoteConfig pr : prs) {
						try {
							uprcDao.saveOrUpdate(pr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				successCount++;
				if (successCount == 3 ) {
					handler.sendEmptyMessage(1);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		uprcTask.start();
		
		Logger.warn(null, "更新遥控配置信息");
		UpdateProductRemoteConfigFunTask uprcfTask = new UpdateProductRemoteConfigFunTask(null);
		final ProductRemoteConfigFunDaoImpl uprcfDao = new ProductRemoteConfigFunDaoImpl(context);
		uprcfTask.addListener(new TaskListener<ITask>() {

			@Override
			public void onCanceled(ITask task, Object arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if(arg != null && arg.length > 0){
					List<ProductRemoteConfigFun> prs = (List<ProductRemoteConfigFun>)arg[0];
					for(ProductRemoteConfigFun pr : prs) {
						try {
							uprcfDao.saveOrUpdate(pr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				successCount++;
				if (successCount == 3 ) {
					handler.sendEmptyMessage(1);
				}
			}
		});
		uprcfTask.start();
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				showSaveDialog();
				break;
			case 1:
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				if (successCount == 3) {
					JxshApp.showToast(context, "学习成功");
				}
				break;
			}
		}
	};
	
	private String getAddress485ByWhId(String whId){
		String address485 = "";
		CustomerProductDaoImpl cpdImpl = new CustomerProductDaoImpl(context);
		List<CustomerProduct> lists = cpdImpl.find(null, "whId", new String[]{}, null, null, null, null);
		if (lists != null && lists.size() > 0) {
			address485 = lists.get(0).getAddress485();
		}
		return address485;
	}
	
}
