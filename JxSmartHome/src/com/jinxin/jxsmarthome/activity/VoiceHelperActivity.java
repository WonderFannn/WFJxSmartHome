package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.VoiceTypeListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.impl.ProductVoiceTypeDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.device.Text2VoiceManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.ProductVoiceType;
import com.jinxin.jxsmarthome.fragment.Text2VoiceFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.VoiceHelperGridAdapter;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 语音助手主页面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class VoiceHelperActivity extends BaseActionBarActivity implements OnClickListener{

	private GridView gridView = null;
	
	private VoiceHelperGridAdapter adapter = null;
	private List<ProductVoiceType> typeList = null;
	private ProductVoiceTypeDaoImpl pvtDaoImpl = null;
	
	private int indicator = 0;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (indicator == 0) {//语音助手主界面ActionBar
			getMenuInflater().inflate(R.menu.menu_voice, menu);
		}else if(indicator == 1){//自定义语音识别ActionBar
			getMenuInflater().inflate(R.menu.menu_text2voice, menu);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("语音助手");
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_voice_layout);
		initData();
		initView();
	}

	/**
	 * 初始布局
	 */
	private void initView(){
		gridView = (GridView) findViewById(R.id.main_voice_gridview);
		adapter = new VoiceHelperGridAdapter(VoiceHelperActivity.this, typeList);
		gridView.setAdapter(adapter);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					if(AppUtil.getProductFunByFunType(VoiceHelperActivity.this, 
							ProductConstants.FUN_TYPE_POWER_AMPLIFIER).size() > 0) {
//						showSendTextToVoiceDialog();
						Fragment fragment = new Text2VoiceFragment();
						addFragment(fragment, true);
					}else {
						JxshApp.showToast(VoiceHelperActivity.this, "无可用的音乐设备!");
					}
				}else if(position == 1){
					String account = CommUtil.getCurrentLoginAccount();
					boolean autoSend = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_VOICE_SEND_SWITCH, false);
					Intent intent = null;
					if (autoSend) {
						intent = new Intent(getApplicationContext(),Voice2TextActivity2.class);
					}else{
						intent = new Intent(getApplicationContext(),Voice2TextActivity.class);
					}
					intent.putExtra("type", position);
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}else if(position == 2){
					String account = CommUtil.getCurrentLoginAccount();
					boolean autoSend = SharedDB.loadBooleanFromDB(account, ControlDefine.KEY_VOICE_SEND_SWITCH, false);
					Intent intent = null;
					if (autoSend) {
						intent = new Intent(getApplicationContext(),Voice2TextActivity2.class);
					}else{
						intent = new Intent(getApplicationContext(),Voice2TextActivity.class);
					}
					intent.putExtra("type", 3);
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}else{
					ProductVoiceType  _voiceType = typeList.get(position);
					int typeId = _voiceType.getId();
					int type = _voiceType.getCategory();
					Intent _intent = new Intent(VoiceHelperActivity.this,VoiceConfigActivity.class);
					_intent.putExtra("typeId", typeId);
					_intent.putExtra("type", type);
					_intent.putExtra("title", _voiceType.getName());
					startActivity(_intent);
				}
			}
		});
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		
		typeList = new ArrayList<ProductVoiceType>();
		pvtDaoImpl = new ProductVoiceTypeDaoImpl(VoiceHelperActivity.this);
//		typeList = pvtDaoImpl.find();
		getVoiceTypeTask();
		initAutoSend();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
//		case R.id.main_voice2text:
//			startActivity(new Intent(VoiceHelperActivity.this,Voice2TextActivity.class));
//			break;
		}
		return false;
	}
	
	/**
	 * 进入页面时获取云设置
	 */
	private void initAutoSend() {
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getApplicationContext());
		
		String _account = CommUtil.getCurrentLoginAccount();
		if(isApplyMainSettings()) {
			_account = CommUtil.getMainAccount();
		}
		List<CloudSetting> csList = new ArrayList<CloudSetting>();
		csList.addAll(csDaoImpl.find(null, "customer_id=?", new String[]{_account}, null, null, null, null));
		if (csList != null && csList.size() > 0) {
			for (CloudSetting cs : csList) {
				if (StaticConstant.PARAM_SEND_SWITCH.equals(cs.getCategory()) && 
						StaticConstant.PARAM_SEND_SWITCH.equals(cs.getItems())) {
					SharedDB.saveBooleanDB(CommUtil.getCurrentLoginAccount(), ControlDefine.KEY_VOICE_SEND_SWITCH, Boolean.valueOf(cs.getParams()));
				}
			}
		}
	}
	
	private boolean isMainAccount() {
		if(CommUtil.getMainAccount().equals(CommUtil.getCurrentLoginAccount())) {
			return true;
		}
		return false;
	}
	
	private boolean isApplyMainSettings() {
		if(!isMainAccount()) {
			CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getApplicationContext());
			String _account = CommUtil.getMainAccount();
			Logger.debug(null, _account);
			List<CloudSetting> csList = csDaoImpl.find(null, "customer_id=?", new String[]{_account}, null, null, null, null);
			for (CloudSetting cs : csList) {
				if(StaticConstant.PARAM_APPLY_SUB_SWITCH.equals(cs.getCategory()) && 
						StaticConstant.PARAM_APPLY_SUB_SWITCH.equals(cs.getItems())) {
					String applySubSwitch = cs.getParams();
					if (applySubSwitch != null) {
						return applySubSwitch.equals("1")?true:false;
					}
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 获取语音类型
	 */
	private void getVoiceTypeTask(){
		VoiceTypeListTask vtTask = new VoiceTypeListTask(VoiceHelperActivity.this);
		vtTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					typeList.clear();
					List<ProductVoiceType> list = (List<ProductVoiceType>) arg[0];
					ProductVoiceType voiceType1 = new ProductVoiceType(0, "语音合成", 1, "", "", "", "", 1, "", 1, 1);
					ProductVoiceType voiceType2 = new ProductVoiceType(1, "语音控制", 1, ""	, "", "", "", 1, "", 1, 1);
					ProductVoiceType voiceType3 = new ProductVoiceType(2, "音乐播放", 1, ""	, "", "", "", 1, "", 1, 1);
					typeList.add(voiceType1);
					typeList.add(voiceType2);
					typeList.add(voiceType3);
					CommonMethod.updateProductVoiceTypeList(VoiceHelperActivity.this, list);
					typeList.addAll(list);
					mUIHander.sendEmptyMessage(0);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		vtTask.start();
	}
	
	/**
	 * 添加fragment
	 * @param fragment
	 * @param addToStack
	 */
	private void addFragment(Fragment fragment, boolean addToStack) {
		if (fragment != null && addToStack) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.voice_framelayout, fragment)
					.addToBackStack(null).commitAllowingStateLoss();
		} else if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.voice_framelayout, fragment).commitAllowingStateLoss();
		}
	}
	
	/**
	 * 显示发送文本转化为语音的窗口
	 */
	private void showSendTextToVoiceDialog() {
		if(NetworkModeSwitcher.useOfflineMode(VoiceHelperActivity.this)) {
			Toast.makeText(VoiceHelperActivity.this, "离线时此功能不可用", Toast.LENGTH_SHORT).show();;
			return;
		}
		LayoutInflater inflater = (LayoutInflater) VoiceHelperActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.custom_dialog_send_voice, new LinearLayout(VoiceHelperActivity.this), false);
		final EditText mSendText = (EditText)dialogView.findViewById(R.id.send_voice_text);
		Button mCancel = (Button)dialogView.findViewById(R.id.send_voice_cancel);
		Button mSend = (Button)dialogView.findViewById(R.id.send_voice_send);
		final Dialog dialog = BottomDialogHelper.showDialogInBottom(VoiceHelperActivity.this, dialogView, null);
		mCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		mSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sendText = mSendText.getText().toString();
				if(sendText != null && sendText.length() > 0) {
					sendText = sendText.length() > 140 ? sendText.substring(0, 140) : sendText;
					Text2VoiceManager manager = new Text2VoiceManager(VoiceHelperActivity.this);
					manager.switchAndSend(mSendText.getText().toString());
					dialog.cancel();
				}
			}
		});
	}
	
	/**
	 * 改变当前显示的ActionBar
	 * @param i
	 */
	public void changeMenu(int i){
		indicator = i;
		ActivityCompat.invalidateOptionsMenu(this);
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		super.uiHandlerData(msg);
		switch (msg.what) {
		case 0:
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		
	}
	
}
