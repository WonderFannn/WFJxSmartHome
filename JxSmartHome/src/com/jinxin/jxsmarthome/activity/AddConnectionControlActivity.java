package com.jinxin.jxsmarthome.activity;


import java.util.ArrayList;
import java.util.List;

import com.jinxin.datan.net.command.AddConnectionOperationTask;
import com.jinxin.datan.net.command.ProductConntectionListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.WhProductUnfraredDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductConnection;
import com.jinxin.jxsmarthome.entity.ProductConnectionVO;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.AddConnectionExpandableAdapter;
import com.jinxin.jxsmarthome.ui.widget.pinnerExpandListView.PinnedHeaderExpandableListView;
import com.jinxin.jxsmarthome.ui.widget.pinnerExpandListView.PinnedHeaderExpandableListView.OnHeaderUpdateListener;
import com.jinxin.jxsmarthome.ui.widget.pinnerExpandListView.StickyLayout;
import com.jinxin.jxsmarthome.ui.widget.pinnerExpandListView.StickyLayout.OnGiveUpTouchEventListener;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 添加联动设备
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AddConnectionControlActivity extends BaseActionBarActivity 
	implements OnHeaderUpdateListener,OnGiveUpTouchEventListener, OnClickListener{

	private Context context;
    private PinnedHeaderExpandableListView expandableListView;
    private StickyLayout stickyLayout;
    private ImageView addDevice;
    private LinearLayout addLayout;
    private RelativeLayout funLayut;
    private ImageView funIcon;
    private TextView funName, openBtn;
    
    private ProductFunDaoImpl pfDaoImpl;
    private FunDetailDaoImpl fdDaoImpl;
    private List<FunDetail> funDetailList;
    private AddConnectionExpandableAdapter adapter;
    private List<List<ProductConnectionVO>> connectionVOList;
    public static final int REQUSET = 101;
    private ProductFun groupFun;
    private String groupStatus = "";//除设置双路开关为设置点时，其他都设置为 空
	
    private BroadcastReceiver mShowBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (BroadcastManager.ACTION_REFRESH_CONNECTION_UI.equals(intent.getAction())) {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			}
		}
	};
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_add, menu);
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("新增关联");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.save_mode_btn:
			Log.e("lop", adapter.getConnectionList().toString());
			if (groupFun != null && adapter.getConnectionList().size() > 0) {
				//上传关联操作
				AddConnectionOperationTask addTask = new AddConnectionOperationTask(context, adapter.getConnectionList());
				addTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
						JxshApp.showLoading(context, "正在上传数据...");
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
						//同步关联操作数据
						ProductConntectionListTask pclTask = new ProductConntectionListTask(context);
						pclTask.addListener(new ITaskListener<ITask>() {

							@Override
							public void onStarted(ITask task, Object arg) {
								JxshApp.showLoading(context, "同步数据...");
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
								JxshApp.closeLoading();
								if (arg != null && arg.length > 0) {
									List<ProductConnection> connectionList = (List<ProductConnection>) arg[0];
									CommonMethod.updateProductConnectionList(context, connectionList);
									mUIHander.sendEmptyMessage(1);
								}
								
							}

							@Override
							public void onProcess(ITask task, Object[] arg) {
							}
						});
						pclTask.start();
					}
					@Override
					public void onProcess(ITask task, Object[] arg) {
						
					}
				});
				addTask.start();
			}else{
				JxshApp.showToast(context, "请至少选择一个关联设备");
			}
			break;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		this.openBoradcastReceiver(mShowBroadcastReceiver,BroadcastManager.ACTION_REFRESH_CONNECTION_UI);
		initData();
		initView();
	}
	
	private void initView() {
		setContentView(R.layout.activity_add_connection_control);
		this.addDevice = (ImageView) findViewById(R.id.iv_add_device);
		this.expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expandable_listview);
		this.stickyLayout = (StickyLayout) findViewById(R.id.sticky_layout);
		
		this.addLayout = (LinearLayout) findViewById(R.id.add_layout);
		this.funLayut = (RelativeLayout) findViewById(R.id.fun_layout);
		this.funIcon = (ImageView) findViewById(R.id.item_icon);
		this.funName = (TextView) findViewById(R.id.tv_fun_name);
		this.openBtn = (TextView) findViewById(R.id.tv_switch);
		
		if(funDetailList.size()>0&&connectionVOList.size()>0){
			adapter = new AddConnectionExpandableAdapter(context, funDetailList, connectionVOList, groupFun, groupStatus);
			expandableListView.setAdapter(adapter);
		}else{
			expandableListView.setVisibility(View.GONE);
		}

		addDevice.setOnClickListener(this);
		funLayut.setOnClickListener(this);
		expandableListView.setOnHeaderUpdateListener(this);
        stickyLayout.setOnGiveUpTouchEventListener(this);
        
        if (groupFun == null) {
        	expandableListView.setFocusable(false);
		}
	}

	private void initData() {
		pfDaoImpl = new ProductFunDaoImpl(context);
		fdDaoImpl = new FunDetailDaoImpl(context);
		funDetailList = new ArrayList<FunDetail>();
		connectionVOList = new ArrayList<List<ProductConnectionVO>>();
		updateListData();
		
	}

	//更新显示数据
	private void updateListData() {
//		if (getFunTypeArr().length > 0) {
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < getFunTypeArr().length; i++) {
//				sb.append("?"+",");
//			}
//			sb.deleteCharAt(sb.length()-1);
//			funDetailList = fdDaoImpl.find(null, "funType in ("+sb.toString()+")", getFunTypeArr(), null, null, null, null);
//		}
		List<FunDetail> allDetailList = fdDaoImpl.find(null, "joinPattern=?",
				new String[]{Integer.toString(1)}, null, null, null, null);
		// and funtype not in ('037','047','046','045','044')
		if (allDetailList != null && allDetailList.size() > 0) {
			List<ProductFun> tempList = null;
			//不显示没有设备的设备类型
			for (FunDetail funDetail : allDetailList) {
				tempList = pfDaoImpl.find(null, "funType=? and enable=?",
						new String[]{funDetail.getFunType(),Integer.toString(1)}, null, null, null, null);
				if (tempList != null && tempList.size() > 0) {
					funDetailList.add(funDetail);
				}
			}
			
			if (funDetailList!= null && funDetailList.size() > 0) {
				for (int i = 0; i < funDetailList.size(); i++) {
					tempList = pfDaoImpl.find(null, "funType=? and enable=?", 
							new String[]{funDetailList.get(i).getFunType(),Integer.toString(1)}, null, null, null, null);
					connectionVOList.add(createConnectionVOList(tempList));
				}
			}
		}
	}

	@Override
	public void uiHandlerData(Message msg) {
		super.uiHandlerData(msg);
		switch (msg.what) {
		case 0:
			if (groupFun != null) {
				addLayout.setVisibility(View.GONE);
				funLayut.setVisibility(View.VISIBLE);
				expandableListView.setFocusable(true);
				FunDetail funDetail = AppUtil.getFunDetailByFunType(context, groupFun.getFunType());
				if (funDetail !=null && !TextUtils.isEmpty(funDetail.getIcon())) {
					Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
					JxshApp.instance.getFinalBitmap().display(funIcon,
							FileManager.instance().createImageUrl(funDetail.getIcon()), _defaultBitmap,_defaultBitmap);
				}
				funName.setText(groupFun.getFunName());
				openBtn.setText(groupFun.isOpen() ? "开启" : "关闭");
				
				//根据选择的设备productFun重新判断要显示的数据
				if(funDetailList.size()>0&&connectionVOList.size()>0){
					adapter = new AddConnectionExpandableAdapter(context, funDetailList, connectionVOList, groupFun, groupStatus);
					adapter.setProductFun(groupFun);
					if (!TextUtils.isEmpty(groupStatus)) {
						adapter.setDoubleStatus(groupStatus);
					}
					expandableListView.setAdapter(adapter);
				}else{
					expandableListView.setVisibility(View.GONE);
				}
			}
			break;
		case 1:
			this.finish();
			break;
		}
	}
	
	/**
	 * 获取关联操作List
	 * @return
	 */
//	private List<ProductConnection> getConnectionList(){
//		List<ProductConnection> pcList = new ArrayList<ProductConnection>();
//		if (connectionVOList != null) {
//			for (List<ProductConnectionVO> voList : connectionVOList) {
//				if (voList != null) {
//					for (ProductConnectionVO productConnectionVO : voList) {
//						if (productConnectionVO != null && productConnectionVO.isSelected()) {
//							pcList.add(productConnectionVO.getProductConnection());
//						}
//					}
//				}
//			}
//		}
//		return pcList;
//	}

	@Override
	public View getPinnedHeader() {
		View headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.group_item_product, null);
        headerView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return headerView;
	}

	@Override
	public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
		if(adapter!=null){
			FunDetail firstVisibleGroup = (FunDetail) adapter.getGroup(firstVisibleGroupPos);
			TextView textView = (TextView) headerView.findViewById(R.id.group_item);
			textView.setText(firstVisibleGroup.getFunName());
		}
	}

	@Override
	public boolean giveUpTouchEvent(MotionEvent event) {
		if (expandableListView.getFirstVisiblePosition() == 0) {
            View view = expandableListView.getChildAt(0);
            if (view != null && view.getTop() >= 0) {
                return true;
            }
        }
        return false;
	}
	
	private List<ProductConnectionVO> createConnectionVOList(List<ProductFun> tempList){
		List<ProductConnectionVO> connectionVOList = new ArrayList<ProductConnectionVO>();
		if (tempList == null ) {
			return connectionVOList;
		}
		for (ProductFun _pf : tempList) {
			if (_pf.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND)) {
				WhProductUnfraredDaoImpl unfraredDaoImpl = new WhProductUnfraredDaoImpl(context);
				List<WHproductUnfrared> unfrareds = unfraredDaoImpl.find(null, "fundId=?",
						new String[] { String.valueOf(_pf.getFunId()) }, null, null, null, null);
				if (unfrareds != null && unfrareds.size() > 0) {
					int size = unfrareds.size();
					for (int j = 0; j < size; j++) {
						ProductFun pf;
						try {
							pf = (ProductFun)_pf.clone();
							pf.setFunName(_pf.getFunName()+"_"+unfrareds.get(j).getRecoorName());
							pf.setFunParam(unfrareds.get(j).getInfraRedCode());
							connectionVOList.add(new ProductConnectionVO(pf));
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				connectionVOList.add(new ProductConnectionVO(_pf));
			}
		}
		
		return connectionVOList;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_add_device:
		case R.id.fun_layout:
			startActivityForResult(new Intent(AddConnectionControlActivity.this,
					AddConnectionDeviceActivity.class), REQUSET);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUSET && resultCode == RESULT_OK) {
			groupFun = (ProductFun) data.getSerializableExtra("productFun");
			groupStatus = data.getStringExtra("groupStatus");
			mUIHander.sendEmptyMessage(0);
		}
	}
	
	

}
