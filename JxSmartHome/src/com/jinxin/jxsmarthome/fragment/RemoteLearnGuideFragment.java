package com.jinxin.jxsmarthome.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jinxin.datan.net.command.AddCustomerRemoteBrandTask;
import com.jinxin.datan.net.command.CustomerBrandsListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.CustomerRemoteLearnActivity;
import com.jinxin.jxsmarthome.activity.RemoteBrandsTypeActivity;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.RemoteBrandsType;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 智能学习引导
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class RemoteLearnGuideFragment extends Fragment implements OnClickListener{

	private Context context;
	
	private RemoteBrandsType brand = null;
	private CustomerProduct currUFO;
	private CustomerBrands customerBrand = null;
	private int type = 2;//1 码库，2 学习
	private int brandType;//设备类型 1=空调  2=电视3=机顶盒 4= DVD/VCD 5=电风扇 6=空气净化器
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = CommDefines.getSkinManager().view(R.layout.fragment_remote_learn_guide, container);
		ininData();
		initView(view);
		return view;
	}

	private void ininData() {
		brand = (RemoteBrandsType) getArguments().getSerializable(RemoteBrandsTypeActivity.BRAND);
		currUFO = (CustomerProduct) getArguments().getSerializable("UFO");
		if (brand != null) {
			brandType = brand.getDeviceId();
		}
	}

	private void initView(View view) {
		view.findViewById(R.id.btn_learn).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_learn:
			showSaveDialog();
			break;
		}
	}
	
	/**
	 * 保存遥控器
	 */
	private void showSaveDialog(){
		if (brand == null) {
			return;
		}
		
		LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.save_remote_dialog_layout, null);
		final CustomerCenterDialog dialog = new CustomerCenterDialog(context, R.style.dialog, v);

		final EditText etName = (EditText) v.findViewById(R.id.et_remote_name);
		Button btnSure = (Button) v.findViewById(R.id.button_ok);
		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		btnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String nickName = etName.getText().toString();
				if (!TextUtils.isEmpty(nickName)) {
					addBrandTask(nickName);
					dialog.dismiss();
				}else{
					JxshApp.showToast(context, "输入遥控器昵称才能保存");
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
	
	/**
	 * 添加遥控器请求
	 * @param nickName
	 */
	private void addBrandTask(String nickName){
		if (currUFO == null ) {
			Logger.debug(null, "ufo is null");
			return;
		}
		AddCustomerRemoteBrandTask acrbTask = new AddCustomerRemoteBrandTask(getActivity(),
				"0", currUFO.getWhId(), brandType, nickName, brand.getId(), type);
		acrbTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, "正在生成遥控器，请稍后...");
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
				if (arg != null && arg.length > 0) {
					int brandId = (Integer) arg[0];
					loadData(brandId);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		acrbTask.start();
	}
	
	private void loadData(int brandId){
		if (brandId < 1)  return;
		CustomerBrandsListTask cBrandsListTask = new CustomerBrandsListTask(context,String.valueOf(brandId));
		cBrandsListTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				JxshApp.closeLoading();
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				JxshApp.closeLoading();
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "onSuccess");
				JxshApp.closeLoading();
				if(arg != null && arg.length > 0){
					List<CustomerBrands> cbList = (List<CustomerBrands>) arg[0];
					if (cbList != null && cbList.size() > 0) {
						customerBrand = cbList.get(0);
						handler.sendEmptyMessage(0);
					}
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		cBrandsListTask.start();
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Intent intent = new Intent(context, CustomerRemoteLearnActivity.class);
				intent.putExtra(RemoteBrandsTypeActivity.BRAND , customerBrand);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
		
	};
	
}
