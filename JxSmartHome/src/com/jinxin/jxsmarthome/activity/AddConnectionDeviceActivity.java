package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductConnectionVO;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.AddConnectionDeviceAdapter;
import com.jinxin.jxsmarthome.ui.widget.pinnerExpandListView.PinnedHeaderExpandableListView;

/**
 * 选择关联触发设备
 * @author YangJJ
 * @company 金鑫智慧
 */
public class AddConnectionDeviceActivity extends BaseActionBarActivity implements  OnClickListener{
	
	private Context context;
	private PinnedHeaderExpandableListView expandableListView;
	private AddConnectionDeviceAdapter adapter;
	
	private ProductFunDaoImpl pfDaoImpl;
    private FunDetailDaoImpl fdDaoImpl;
    private List<FunDetail> funDetailList;
    private List<List<ProductConnectionVO>> connectionVOList;
    private ProductFun groupFun = null;
    private String groupStatus = null;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_add, menu);
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("选择当前设置点");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.save_mode_btn:
			this.groupFun = adapter.getGroupFun();
			this.groupStatus = adapter.getDoubleStatus();
			
			if (groupFun != null) {
				Intent in = new Intent();
				in.putExtra("productFun", groupFun);
				in.putExtra("groupStatus", groupStatus);
				setResult(RESULT_OK, in);
				this.finish();
			}else{
				JxshApp.showToast(context, "请选择触发设备");
			}
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		initData();
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_add_conntection_device);
		this.expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expandable_listview);
		
		adapter = new AddConnectionDeviceAdapter(context, funDetailList, connectionVOList);
		expandableListView.setAdapter(adapter);
	}

	private void initData() {
		pfDaoImpl = new ProductFunDaoImpl(context);
		fdDaoImpl = new FunDetailDaoImpl(context);
		funDetailList = new ArrayList<FunDetail>();
		connectionVOList = new ArrayList<List<ProductConnectionVO>>();
		List<FunDetail> allDetailList = new ArrayList<FunDetail>();
		if (getFunTypeArr().length > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < getFunTypeArr().length; i++) {
				sb.append("?"+",");
			}
			sb.deleteCharAt(sb.length()-1);
			allDetailList = fdDaoImpl.find(null, "funType in ("+sb.toString()+")", getFunTypeArr(), null, null, null, null);
		}
		
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
				for (FunDetail funDetail : funDetailList) {
					tempList = pfDaoImpl.find(null, "funType=? and enable=?",
							new String[]{funDetail.getFunType(),Integer.toString(1)}, null, null, null, null);
					if (tempList != null && tempList.size() > 0) {
						connectionVOList.add(createConnectionVOList(tempList));
					}
				}
			}
		}
	}
	
	
	private List<ProductConnectionVO> createConnectionVOList(List<ProductFun> tempList){
		List<ProductConnectionVO> connectionVOList = new ArrayList<ProductConnectionVO>();
		if (tempList == null ) {
			return connectionVOList;
		}
		for (ProductFun _pf : tempList) {
			connectionVOList.add(new ProductConnectionVO(_pf));
		}
		
		return connectionVOList;
	}
	
	/**
	 * 要显示的设备类型
	 * @return
	 */
	private String[] getFunTypeArr(){
		StringBuffer sb = new StringBuffer();
		sb.append(ProductConstants.FUN_TYPE_AUTO_LOCK+",");//锁
		sb.append(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT+",");//电灯主控
		sb.append(ProductConstants.FUN_TYPE_COLOR_LIGHT+",");//彩灯
		sb.append(ProductConstants.FUN_TYPE_CURTAIN+",");//窗帘
		sb.append(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN+",");//无线窗帘
		sb.append(ProductConstants.FUN_TYPE_WIRELESS_SOCKET+",");//无线插座
		sb.append(ProductConstants.FUN_TYPE_DOUBLE_SWITCH+",");//双路开关
		sb.append(ProductConstants.FUN_TYPE_AIRCONDITION);//无线空调
		return sb.toString().split(",");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}

}
