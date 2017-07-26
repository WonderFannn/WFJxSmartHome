package com.jinxin.jxsmarthome.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.cmd.OfflineCmdManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

public class VoiceLanguageFragment extends Fragment {
	
	private ListView listview;
	private LanguageAdapter adapter = null;
	private String[] itemList = null;
	private String[] idList = null;
	private String _account = "";
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_voice_language, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("选择语言");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.voice_language_fragment, container, false);
		
		initData();
		initView(view);
		return view;
	}

	/**
	 * 初始数据
	 */
	private void initData() {
		itemList = getActivity().getResources()
				.getStringArray(R.array.voice_language_item);
		idList = getActivity().getResources()
				.getStringArray(R.array.voice_language_item_id);
		_account = CommUtil.getCurrentLoginAccount();
	}

	/**
	 * 初始布局
	 * @param view
	 */
	private void initView(View view) {
		listview = (ListView) view.findViewById(R.id.language_listview);
		adapter = new LanguageAdapter(getActivity(), itemList);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String roleId = idList[position];
				sendCmdChangeVoiceLanguage(roleId,position);
			}
		});
	}
	
	/**
	 * 发送方言切换指令
	 * @param roleId 方言对应角色
	 * @param pos 方言对应位置
	 */
	private void sendCmdChangeVoiceLanguage(String roleId,int pos){
		final int position = pos;
		ProductFun productFun = AppUtil.getSingleProductFunByFunType(getActivity(),
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		FunDetail funDetail = AppUtil.getFunDetailByFunType(getActivity(),
				ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		
		if (productFun == null || funDetail == null || roleId == "") {
			Logger.warn(null, "paramter error, cancel send voice");
			return;
		}
		
		FunDetailConfigDaoImpl fdcDaoImpl = new FunDetailConfigDaoImpl(getActivity());
		List<FunDetailConfig> fdcList = fdcDaoImpl.find(null, "funType=?",
				new String[]{ProductConstants.FUN_TYPE_GATEWAY}, null, null, null, null);
		if (fdcList == null && fdcList.size() < 1) {
			JxshApp.showToast(getActivity(), "未找到网关设备");
			return;
		}
		
		FunDetailConfig fdc = fdcList.get(0);
		productFun.setWhId(fdc.getWhId());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.PARAM_TEXT, roleId);
		
		productFun.setFunType(ProductConstants.FUN_TYPE_GATEWAY_VOICE_LANGUAGE);
		funDetail.setFunType(ProductConstants.FUN_TYPE_GATEWAY_VOICE_LANGUAGE);
		if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdManager.generateCmdAndSend(getActivity(), productFun,
				funDetail, params);
			return;
		}
		
		List<byte[]> cmds = CommonMethod.productFunToCMD(getActivity(), productFun,
				funDetail, params);
		
		if (cmds == null || cmds.size() < 1) return;
		//发送指令 存储当前选中语言
//		new OnlineCmdSenderLong(getActivity(), cmds).send();
		CommonDeviceControlByServerTask cdcbsTask = new 
				CommonDeviceControlByServerTask(getActivity(), cmds.get(0), false);
		cdcbsTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(getActivity(), "语言切换中...");
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
				SharedDB.saveStrDB(_account, ControlDefine.KEY_VOICE_LANGUAGE, itemList[position]);
				handler.sendEmptyMessage(0);
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				
			}
		});
		cdcbsTask.start();
		
	}
	
	class LanguageAdapter extends BaseAdapter{
		
		private String[] itemList;
		public LanguageAdapter(Context context, String[] itemList) {
			this.itemList = itemList;
		}
		
		@Override
		public int getCount() {
			return this.itemList != null ? itemList.length : 0;
		}

		@Override
		public Object getItem(int position) {
			return itemList[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final int pos = position;
			
			if (convertView ==  null) {
				convertView = CommDefines.getSkinManager().view(
						R.layout.voice_language_item);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mLanguage.setText(itemList[pos]);
			String language = SharedDB.loadStrFromDB(_account, ControlDefine.KEY_VOICE_LANGUAGE, "");
			if (itemList[position].equals(language)) {
				holder.checkImage.setVisibility(View.VISIBLE);
			}else{
				holder.checkImage.setVisibility(View.INVISIBLE);
			}
			
			return convertView;
		}
		
		class ViewHolder{
			TextView mLanguage;
			ImageView checkImage;
			public ViewHolder(View convertView) {
				mLanguage = (TextView) convertView.findViewById(R.id.language_tv);
				checkImage = (ImageView) convertView.findViewById(R.id.language_check);
			}
		}
		
	}
	
}
