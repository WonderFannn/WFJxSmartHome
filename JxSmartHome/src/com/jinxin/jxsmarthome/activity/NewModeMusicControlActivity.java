package com.jinxin.jxsmarthome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;
import com.jinxin.datan.net.command.ForgetPasswordTask;
import com.jinxin.datan.net.command.GetSafeQuestionTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.NewModeMusicControlAdapter;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.record.SharedDB;

/** 新模式下音乐控制界面
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class NewModeMusicControlActivity extends BaseActionBarActivity implements OnClickListener{
	
	private Context context = null;
	private ListView mMusicControlView;
	private String controlType = "";
	private int selectedPosition;
	private ProductFunVO productFunVO;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("控制操作");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return true;
	}

	/**
	 * 初始化
	 */
	private void initView() {
		this.context = this;
		this.setContentView(R.layout.new_mode_music_control_layout);
		this.mMusicControlView = (ListView) findViewById(R.id.new_mode_music_control);
	}
	
	/**
	 * 初始化显示数据(在initView之后)
	 */
	private void initData() {
		String[] music_names = getResources().getStringArray(R.array.music_names);
		
		productFunVO = (ProductFunVO) getIntent().getSerializableExtra("productFunVO");
		if (productFunVO != null) {
			String operation = productFunVO.getProductPatternOperation().getOperation();
			String control = getControlTypeFromProductPatternOperation(operation);
			if (!TextUtils.isEmpty(control)) {
				for (int i = 0; i < music_names.length; i++) {
					if (music_names[i].equals(control)) {
						selectedPosition = i;
						break;
					}
				}
			}
		}
		
		final List<String> strs = Arrays.asList(music_names);
		final NewModeMusicControlAdapter newModeMusicControlAdapter = new NewModeMusicControlAdapter(context, strs);
		mMusicControlView.setAdapter(newModeMusicControlAdapter);
		newModeMusicControlAdapter.setSelectedPosition(selectedPosition);
		mMusicControlView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                controlType = getSelectControlType(strs.get(position));
//            	JxshApp.showToast(JxshApp.getContext(),strs.get(position)+"--->"+controlType, Toast.LENGTH_SHORT);	
            	selectedPosition= position;
            	newModeMusicControlAdapter.setSelectedPosition(selectedPosition);
            	newModeMusicControlAdapter.notifyDataSetChanged();
            	
            }
        });
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(!TextUtils.isEmpty(controlType)){
			SharedDB.saveStrDB(MusicSetting.SP_NAME,"SELECT_MUSIC_CONTROL_TYPE", controlType);
		}
		 
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		switch(msg.what){
		case 0:
			 
		break;
		case 1:
			this.finish();
		break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			this.onBackPressed();
			break;
			
		}
	}
	
	/**
	 * 控制播放类型
	 * @param speakerType
	 * @return
	 */
	private String getSelectControlType(String controlType) {
		 String control = "";
			switch (controlType) {
			case "播放":
				control = "play";
				break;
			case "停止": 
				control = "pause";
				break;
			case "上一首": 
				control = "preSong";
				break;
			case "下一首":  
				control = "nextSong";
				break;
			case "音量+":  
				control = "soundAdd";
				break;
			case "音量-":  
				control = "soundSub";
				break;
			case "静音":  
				control = "muteSingle";
				break;
			case "取消静音":
				control = "unmuteSingle";
				break;
			}
			return control;
		}
	
	/**
	 * 获取播放类型
	 * @param speakerType
	 * @return
	 */
	private String getControlTypeFromProductPatternOperation(String controlType) {
		 String control = "";
			switch (controlType) {
			case "play":
				control = "播放";
				break;
			case "pause": 
				control = "停止";
				break;
			case "preSong": 
				control = "上一首";
				break;
			case "nextSong":  
				control = "下一首";
				break;
			case "soundAdd":  
				control = "音量+";
				break;
			case "soundSub":  
				control = "音量-";
				break;
			case "muteSingle":  
				control = "静音";
				break;
			case "unmuteSingle":
				control = "取消静音";
				break;
			}
			return control;
		}
	
}
