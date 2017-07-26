package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.LoginTask;
import com.jinxin.datan.net.command.SyncCloudSettingTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 亲情账号列表
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class RelativeAccountActivity extends BaseActionBarActivity {

	private Context context;
	private ListView listView;
	private MessageTimerAdapter adapter;
	private TextView tvTips;
	private List<CloudSetting> csList;
	private String relativeAccounts = "";
	private String[] accountArr;
	private Dialog dialog = null;
	private CloudSettingDaoImpl csDaoImpl = null;
	private CloudSetting cloudSetting = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = RelativeAccountActivity.this;
		setContentView(R.layout.activity_relative_account_layout);
		
		tvTips = (TextView) findViewById(R.id.tv_no_timer_tips);
		listView = (ListView) findViewById(R.id.lv_relative_account);
		
		initData();
		
		if (!TextUtils.isEmpty(relativeAccounts)) {
			accountArr = relativeAccounts.replace(" ", "").split(",");
//			Logger.debug(null, "accountArr-->"+accountArr.length);
			tvTips.setVisibility(View.GONE);
		}else{
			tvTips.setVisibility(View.VISIBLE);
		}
		adapter = new MessageTimerAdapter();
		listView.setAdapter(adapter);
	}
	
	private void initData(){
		csDaoImpl = new CloudSettingDaoImpl(context);
		csList = new ArrayList<CloudSetting>();
		initCloudSettings();
	}
	
	/**
	 * 进入页面时获取云设置
	 */
	private void initCloudSettings() {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(RelativeAccountActivity.this);
		csList = csDaoImpl.find(null, "category=?", 
				new String[]{StaticConstant.PARAM_RELATIVE_ACCOUNT}, null, null, null, null);
		Logger.debug(null, "csList:"+csList.size() + "");
		if (csList != null && csList.size() > 0) {
			for (CloudSetting cs : csList) {
				if (StaticConstant.PARAM_RELATIVE_ACCOUNT.equals(cs.getCategory()) && 
						StaticConstant.PARAM_RELATIVE_ACCOUNT.equals(cs.getItems())) {
					if (" ".equals(cs.getParams())) {
						relativeAccounts = "";
					}else{
						relativeAccounts = cs.getParams();
					}
//					Logger.debug(null, "relativeAccounts-->"+relativeAccounts.length());
					cloudSetting = cs;
				}
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_timer, menu);
		
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("亲情账号");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.action_timer:
			if (CommUtil.isSubaccount()) {
				JxshApp.showToast(context, "子账号不能进行此操作");
				return false;
			}
			if(NetworkModeSwitcher.useOfflineMode(context)) {
				JxshApp.showToast(context,  CommDefines.getSkinManager()
						.string(R.string.li_xian_cao_zuo_tips));
				return false;
			}
			showAddDialog();
			break;
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void showAddDialog() {
		
		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.add_account_dialog_layout, null);
		dialog.setContentView(v);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.width = (int) (d.getWidth() * 0.85); // 宽度设置为屏幕的0.85
		dialogWindow.setAttributes(lp);
		dialog.show();

		final EditText etAccount = (EditText) v.findViewById(R.id.et_account);
		final EditText etPwd = (EditText) v.findViewById(R.id.et_pwd);
		Button btnSure = (Button) v.findViewById(R.id.button_ok);
		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(etAccount.getText().toString()) || 
						TextUtils.isEmpty(etPwd.getText().toString())) {
					JxshApp.showToast(context, "用户名或密码不能为空");
					return;
				}
				if (CommUtil.getMainAccount().equals(etAccount.getText().toString())) {
					JxshApp.showToast(context, "无法添加当前登录账号");
					return;
				}
				if (relativeAccounts.contains(etAccount.getText().toString())) {
					JxshApp.showToast(context, "请勿重复添加已关联账号");
					return;
				}
				confirmAccount(etAccount.getText().toString(), etPwd.getText().toString());
			}
		});

		btnCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
	}

	/**
	 * 验证账号
	 * @param account
	 * @param password
	 */
	private void confirmAccount(final String account, String password){
		//登陆
		LoginTask loginTask = new LoginTask(RelativeAccountActivity.this, account,password);
		loginTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, "正在验证，请稍后...");
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
					SysUser user = (SysUser) arg[0];
					if (user != null) {
						String subAccount = user.getSubAccunt();
						if (!TextUtils.isEmpty(subAccount)) {
							JxshApp.showToast(context, "无法添加子账号作为亲情账号");
						}else{
							syncCloudSettings(account);
						}
					}
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		loginTask.start();
	}
	
	/**
	 * 上传亲情账号
	 */
	private void syncCloudSettings(String account){
		
		StringBuffer sb = new StringBuffer(relativeAccounts);
		if (!TextUtils.isEmpty(account)) {
			if (!TextUtils.isEmpty(relativeAccounts)) {
				relativeAccounts = sb.append(","+account).toString();
			}else{
				relativeAccounts = sb.append(account).toString();
			}
		}
		
		if (cloudSetting == null) {
			cloudSetting = new CloudSetting();
			cloudSetting.setCategory(StaticConstant.PARAM_RELATIVE_ACCOUNT);
			cloudSetting.setItems(StaticConstant.PARAM_RELATIVE_ACCOUNT);
			cloudSetting.setCustomerId(CommUtil.getMainAccount());
			cloudSetting.setParams(relativeAccounts);
			csList.add(cloudSetting);
		}else{
			cloudSetting.setParams(relativeAccounts);
		}

		JSONObject jb = new JSONObject();
		try {
			jb.put(StaticConstant.PARAM_RELATIVE_ACCOUNT, relativeAccounts);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		SyncCloudSettingTask task = new SyncCloudSettingTask(RelativeAccountActivity.this,
							"sync", StaticConstant.PARAM_RELATIVE_ACCOUNT, jb.toString(), csList);
		task.addListener(new ITaskListener<ITask>() {
			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(RelativeAccountActivity.this, "正在同步数据...");
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
				CommonMethod.updateCloudSettings(context, csList);
				mUIHander.sendEmptyMessage(0);
				if (dialog != null &&dialog.isShowing()) {
					dialog.dismiss();
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		task.start();
	}
	

	class MessageTimerAdapter extends BaseAdapter{
		public MessageTimerAdapter() {
		}

		@Override
		public int getCount() {
			return accountArr != null ? accountArr.length : 0;
		}

		@Override
		public Object getItem(int position) {
			return accountArr[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			final int pos = position;
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_relative_account, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder=(ViewHolder) convertView.getTag();
			}
			
			holder.tvItem.setText(accountArr[position]);
			holder.btnDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (CommUtil.isSubaccount()) {
						JxshApp.showToast(context, "子账号不能进行此操作");
						return;
					}
					if(NetworkModeSwitcher.useOfflineMode(context)) {
						JxshApp.showToast(context,  CommDefines.getSkinManager()
								.string(R.string.li_xian_cao_zuo_tips));
						return;
					}
					
					relativeAccounts = relativeAccounts.replaceAll(accountArr[pos]+","+"|"+accountArr[pos],"");
					if (relativeAccounts.startsWith(",")) {
						relativeAccounts = relativeAccounts.substring(1);
					}
					if (relativeAccounts.endsWith(",")) {
						relativeAccounts = relativeAccounts.substring(0, relativeAccounts.length()-1);
					}
					//删除最后一个时，传“ ”（空格）
					if (TextUtils.isEmpty(relativeAccounts)) {
						relativeAccounts = " ";
					}
					syncCloudSettings("");
				}
			});
			return convertView;
		}
		
		class ViewHolder{
			TextView tvItem;
			Button btnDelete;
			public ViewHolder(View view) {
				tvItem = (TextView) view.findViewById(R.id.tv_account);
				btnDelete = (Button) view.findViewById(R.id.iv_delete_btn);
			}
		}
		
	}

	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			if (" ".equals(relativeAccounts)) {
				relativeAccounts = "";
			}
			if (!TextUtils.isEmpty(relativeAccounts)) {
				accountArr = relativeAccounts.replace(" ", "").split(",");
				tvTips.setVisibility(View.GONE);
			}else{
				accountArr = null;
				tvTips.setVisibility(View.VISIBLE);
			}
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}

}
