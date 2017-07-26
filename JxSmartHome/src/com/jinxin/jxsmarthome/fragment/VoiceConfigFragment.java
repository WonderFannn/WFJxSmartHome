package com.jinxin.jxsmarthome.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.datan.net.command.VoiceConfigerNewListTask;
import com.jinxin.datan.net.command.VoiceConfigerOldListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerVoiceConfigDaoImpl;
import com.jinxin.db.impl.ProductVoiceConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.VoiceConfigActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.device.Text2VoiceManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.CustomerVoiceConfig;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.service.TimerManager;
import com.jinxin.jxsmarthome.ui.adapter.VoiceConfigListAdapter;
import com.jinxin.widget.PullToRefresh.PullToRefreshBase;
import com.jinxin.widget.PullToRefresh.PullToRefreshBase.OnRefreshListener;
import com.jinxin.widget.PullToRefresh.PullToRefreshListView;

/**
 * 语音列表展示
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class VoiceConfigFragment extends Fragment implements OnClickListener{

	private TextView voiceTitle;
	private TextView voiceTimer;
	private TextView voicePriod;
	private CheckBox checkBtn;
	private ListView mListView;
	private LinearLayout bottomLayout;
	private Button palyBtn;
	private PullToRefreshListView mPullListView;
	private VoiceConfigListAdapter adapter = null;
	
	private ProductFun productFun;
	private FunDetail funDetail;
	private int typeId = -1;
	private int type = -1;
	private String title = "";
	private List<ProductVoiceConfig> voiceList = null;
	private List<ProductVoiceConfig> playList = null;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private ProductVoiceConfigDaoImpl pvcfDaoImpl =  null;
	private CustomerVoiceConfig customerVoiceTimer = null;
	private CustomerVoiceConfigDaoImpl cvcDaoImpl = null;
	
	private boolean mIsStart = true;
	boolean hasMoreLocalData = false;//是否还有本地缓存数据
	boolean hasMoreData = true;
	private int startPos = 0;//开始查找本地数据库中的位置
	private int currentPos = 10;//当前查找位置
	
	private boolean isShow = false;
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				adapter.initSelectedItem();
				adapter.notifyDataSetChanged();
	            mPullListView.onPullDownRefreshComplete();
	            mPullListView.onPullUpRefreshComplete();
	            mPullListView.setHasMoreData(hasMoreData);
	            setLastUpdateTime();
				break;
			case 1:
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_voice_config, container, false);
		initData();
		initView(view);
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle(title);
	}

	private void initData(){
		
		this.typeId = getArguments().getInt("typeId");
		this.type = getArguments().getInt("type");
		this.title = getArguments().getString("title");
		this.productFun = (ProductFun) getArguments().getSerializable("productFun");
		this.funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		
		this.pvcfDaoImpl = new ProductVoiceConfigDaoImpl(getActivity());
		this.voiceList = new LinkedList<ProductVoiceConfig>();
		this.playList = new ArrayList<ProductVoiceConfig>();
		this.cvcDaoImpl = new CustomerVoiceConfigDaoImpl(getActivity());
		if (type == 3) {//查询数据库是否设置过新闻定时任务
			List<CustomerVoiceConfig> timerList = cvcDaoImpl.find(
					null, "type=?", new String[]{Integer.toString(type)}, null, null, null, null);
			if (timerList != null && timerList.size() > 0) {
				customerVoiceTimer = timerList.get(0);
			}
		}
		getHistoryDataFromDB();
	}
	
	/**
	 * 获取本地缓存的语音
	 */
	private void getHistoryDataFromDB(){
		List<ProductVoiceConfig> tempList = pvcfDaoImpl.find(null,"typeId=?",
				new String[]{Integer.toString(typeId)}, null, null, "updateTime DESC",
					Integer.toString(startPos)+","+Integer.toString(currentPos));
		if (tempList.size() > 0) {
			hasMoreLocalData = true;
			this.startPos = currentPos;
			this.currentPos += currentPos;
		}else{
			hasMoreLocalData = false;
		}
		voiceList.addAll(tempList);
	}
	
	/**
	 * 初始化布局
	 */
	private void initView(View view) {
		this.bottomLayout = (LinearLayout) view.findViewById(R.id.voice_bottom_layout);
		this.voiceTitle = (TextView) view.findViewById(R.id.timer_voice_title);
		this.voiceTimer = (TextView) view.findViewById(R.id.timer_voice_time);
		this.checkBtn = (CheckBox) view.findViewById(R.id.voice_check_btn);
		this.palyBtn = (Button) view.findViewById(R.id.voice_play_btn);
		palyBtn.setOnClickListener(this);
		bottomLayout.setOnClickListener(this);

		mPullListView = (PullToRefreshListView) view.findViewById(R.id.voice_config_list_view);
		mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(true);
		mListView = mPullListView.getRefreshableView();
		adapter = new VoiceConfigListAdapter(getActivity(), voiceList, typeId, type);
		mListView.setAdapter(adapter);
		if (type == 3) {//如果新闻类型
			bottomLayout.setVisibility(View.VISIBLE);
			if (customerVoiceTimer != null) {
				voiceTitle.setText(customerVoiceTimer.getName());
				checkBtn.setChecked(customerVoiceTimer.isOPen() == 1);
				JSONObject _jb;
				try {
					_jb = new JSONObject(customerVoiceTimer.getCornExpression());
					int type = _jb.getInt("type");
					String expresion = getRepeatDate(type) +" "+_jb.getString("time");
					voiceTimer.setText(expresion);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}else{
			bottomLayout.setVisibility(View.GONE);
		}
		
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                mIsStart = true;
                getNewsVoiceListTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                mIsStart = false;
            	if (hasMoreLocalData) {
            		getHistoryDataFromDB();
            		mPullListView.onPullDownRefreshComplete();
                    mPullListView.onPullUpRefreshComplete();
				}else{
					getOldVoiceListTask();
				}
            }
        });
		
		setLastUpdateTime();
        if (voiceList!= null &&voiceList.size() > 0) {
        	mPullListView.doPullRefreshing(true, 500,true);
		}else{
			getOldVoiceListTask();
		}
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ProductVoiceConfig productVoiceConfig = voiceList.get(position);
				if (type == 2 || type == 3) {//新闻：3，唐诗：2
					ProductVoiceConfig pvConfig = voiceList.get(position);
					Bundle bundle = new Bundle();
					bundle.putString("title", title);
					bundle.putSerializable("productVoiceConfig", pvConfig);
					VoiceNewsFragment newsFragment = new VoiceNewsFragment();
					newsFragment.setArguments(bundle);
					addFragment(newsFragment, true);
				}else{
					Text2VoiceManager manager = new Text2VoiceManager(getActivity());
					manager.switchAndSend(productVoiceConfig.getContent());
				}
			}
		});
        
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (type != 3) {//不是新闻类型 设置定时
					ProductVoiceConfig productVoiceConfig = voiceList.get(position);
					Bundle bundle = new Bundle();
					Fragment fragment = null;
					List<CustomerVoiceConfig> list = cvcDaoImpl.find(null, "voiceId=?",
							new String[]{Integer.toString(productVoiceConfig.getId())}, null, null, null, null);
					if (list != null && list.size() > 0) {
						fragment = new VoiceTimerModifyFragment();
						bundle.putInt("type", type);
						bundle.putSerializable("customerVoiceConfig", list.get(0));
					}else{
						fragment = new VoiceTimerFragment();
						bundle.putInt("type", type);
						bundle.putSerializable("productVoiceConfig", productVoiceConfig);
					}
					fragment.setArguments(bundle);
					addFragment(fragment, true);
				}
				return true;
			}
		});
        
        checkBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (customerVoiceTimer != null) {
					if (isChecked) {
						customerVoiceTimer.setOPen(1);
						TimerManager.sendVoiceTimerAddBroadcast(getActivity(), customerVoiceTimer);
					}else{
						customerVoiceTimer.setOPen(0);
						TimerManager.sendVoiceTimerCancelBroadcast(getActivity(), customerVoiceTimer);
					}
					cvcDaoImpl.update(customerVoiceTimer);
				}
			}
		});
        
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_voice_by_order://顺序播放
			if (voiceList != null && voiceList.size() > 0) {
				List<String> stringList = new ArrayList<String>();
				if (type == 2 || type == 3) {//新闻、诗词类型只播放标题和摘要
					for (ProductVoiceConfig _pvc : voiceList) {
						stringList.add(_pvc.getTitle() + " " +_pvc.getSummary());
					}
				}else{
					for (ProductVoiceConfig _pvc : voiceList) {
						stringList.add(_pvc.getContent());
					}
				}
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("voiceList", (ArrayList<String>) stringList);
				BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_MULTIPLE_PLAY, bundle);
			}
			
			break;
		case R.id.action_voice_multiple://多选播放
			if (adapter != null) {
				if (isShow) {
					isShow = false;
					palyBtn.setVisibility(View.GONE);
				}else{
					isShow = true;
					palyBtn.setVisibility(View.VISIBLE);
				}
				adapter.showMultipleButton(isShow);
				handler.sendEmptyMessage(1);
			}
			break;
		case R.id.action_voice_random://随机播放
			if (voiceList != null && voiceList.size() > 0) {
				Text2VoiceManager manager = new Text2VoiceManager(getActivity());
				manager.switchAndSend(voiceList.get(getRandomNum(voiceList.size())).getContent());
			}else{
				JxshApp.showToast(getActivity(), "无可播放语音");
			}
			break;
		}
		return false;
	}

	private int getRandomNum(int num){
		if (num < 1) return 0;
		
		Random random = new Random();
		num = random.nextInt(num);
		return num;
	}
	
	@Override
	public void onResume() {
		if (adapter != null) {
			handler.sendEmptyMessage(1);
		}
		((VoiceConfigActivity) getActivity()).changeMenu(0);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle(title);
		super.onResume();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//重置查询位置
		startPos = 0;
		currentPos = 10;
	}

	/**
	 * 添加fragment
	 */
	private void addFragment(Fragment fragment, boolean addToStack) {
		if (fragment != null && addToStack) {
			((VoiceConfigActivity)getActivity()).
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.voice_fragment_layout, fragment)
					.addToBackStack(null).commit();
		} else if (fragment != null) {
			((VoiceConfigActivity)getActivity()).
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.voice_fragment_layout, fragment).commit();
		}
	}
	
	/**
	 * 获取新增语音列表
	 */
	private void getNewsVoiceListTask(){
		VoiceConfigerNewListTask vcnTask = new VoiceConfigerNewListTask(getActivity(), typeId);
		vcnTask.addListener(listenerNew);
		vcnTask.start();
	}
	
	/**
	 * 获取历史语音列表
	 */
	private void getOldVoiceListTask(){
		VoiceConfigerOldListTask vcoTask = new VoiceConfigerOldListTask(getActivity(), typeId, 10);
		vcoTask.addListener(listenerOld);
		vcoTask.start();
	}
	
	ITaskListener<ITask> listenerNew = new ITaskListener<ITask>() {

		@Override
		public void onStarted(ITask task, Object arg) {
		}
		@Override
		public void onCanceled(ITask task, Object arg) {
			handler.sendEmptyMessage(0);
		}
		@Override
		public void onFail(ITask task, Object[] arg) {
			handler.sendEmptyMessage(0);
		}
		@Override
		public void onSuccess(ITask task, Object[] arg) {
			if (arg != null && arg.length > 0) {
				@SuppressWarnings("unchecked")
				List<ProductVoiceConfig> list = (List<ProductVoiceConfig>) arg[0];
				CommonMethod.updateProductVoiceConfigList(getActivity(), list);
				voiceList.addAll(0,list);
				handler.sendEmptyMessage(0);
			}
		}
		@Override
		public void onProcess(ITask task, Object[] arg) {
		}
	};
	ITaskListener<ITask> listenerOld = new ITaskListener<ITask>() {
		
		@Override
		public void onStarted(ITask task, Object arg) {
		}
		@Override
		public void onCanceled(ITask task, Object arg) {
			handler.sendEmptyMessage(0);
		}
		@Override
		public void onFail(ITask task, Object[] arg) {
			handler.sendEmptyMessage(0);
		}
		
		@Override
		public void onSuccess(ITask task, Object[] arg) {
			if (arg != null && arg.length > 0) {
				@SuppressWarnings("unchecked")
				List<ProductVoiceConfig> list = (List<ProductVoiceConfig>) arg[0];
				if (list != null && list.size() > 0) {
					hasMoreData = true;
					CommonMethod.updateProductVoiceConfigList(getActivity(), list);
					voiceList.addAll(voiceList.size(),list);
				}else{
					hasMoreData = false;
				}
				handler.sendEmptyMessage(0);
			}
		}
		@Override
		public void onProcess(ITask task, Object[] arg) {
		}
	};
	
	private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullListView.setLastUpdatedLabel(text);
    }
	
	private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }
        
        return mDateFormat.format(new Date(time));
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.voice_bottom_layout:
			if (voiceList != null && voiceList.size() > 0) {
				Bundle bundle = new Bundle();
				Fragment fragment = null;
				if (customerVoiceTimer == null) {
					ProductVoiceConfig productVoiceConfig = new ProductVoiceConfig();
					fragment = new VoiceTimerFragment();
					bundle.putInt("type", type);
					bundle.putSerializable("productVoiceConfig", productVoiceConfig);
				}else{
					fragment = new VoiceTimerModifyFragment();
					bundle.putSerializable("customerVoiceConfig", customerVoiceTimer);
				}
				fragment.setArguments(bundle);
				addFragment(fragment, true);
			}else{
				JxshApp.showToast(getActivity(), "列表为空，不能设置定时任务");
			}
			break;
		case R.id.voice_play_btn:
			if (adapter != null) {
				Bundle bundle1 = new Bundle();
				List<String> voiceList = adapter.getSelectedList();
				bundle1.putStringArrayList("voiceList", (ArrayList<String>) voiceList);
				BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_MULTIPLE_PLAY, bundle1);
			}
			break;
		}
	}
	
	/**
	 * 获取定时类型名
	 * @param type
	 * @return
	 */
	private String getRepeatDate(int type){
		String[] arr = getActivity().getResources().getStringArray(R.array.repeat_items);
		String repeatDate ="";
		switch (type) {
		case 0:
			repeatDate = arr[0];
			break;
		case 1:
			repeatDate = arr[1];
			break;
		case 2:
			repeatDate = arr[2];
			break;
		case 3:
			repeatDate = arr[3];
			break;
		}
		return repeatDate;
	}

}
