package com.jinxin.jxsmarthome.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.infrared.model.InfraredCodeLibraryConstant;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;

public class SelectUFOActivity extends BaseActionBarActivity implements
		OnItemClickListener {
	private ListView lv;
	private String[] strs;

//	private List<CustomerProduct> cpList = null;
//	private CustomerProduct currUFO = null;
//	private CustomerProductDaoImpl cpdImpl = null;

	private List<FunDetail> fdList;
	private FunDetailDaoImpl funImpl = null;
	private List<ProductFun> productFunList;
	private FunDetail funDetail;
	private ProductFun productFun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initView() {
		setContentView(R.layout.activity_select_brand);
		lv = (ListView) findViewById(R.id.lv_select_brand);
	}

	private void initData() {
		funImpl = new FunDetailDaoImpl(this);
		fdList = funImpl.find(null, null, null, null, null, "id ASC", null);
		if (fdList != null && fdList.size() > 0) {
			for (int i = 0; i < fdList.size(); i++) {
				FunDetail _fd = fdList.get(i);
				if (_fd.getFunType().equals(
						ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND)) {
					funDetail = fdList.get(i);
					
					break;
				}
			}
		}
		if (funDetail != null) {
			ProductFunDaoImpl pfImpl = new ProductFunDaoImpl(this);
			productFunList = pfImpl.find(null, "funType=? and enable=?", new String[] {
							funDetail.getFunType(), Integer.toString(1) },
							null, null, null, null);
		}
		if (productFunList != null && productFunList.size() > 0) {
			strs = new String[productFunList.size()];
			for (int i = 0; i < productFunList.size(); i++) {
				strs[i] = productFunList.get(i).getFunName();
			}
		}
		lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, strs));

		lv.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		productFun = productFunList.get(position);
		Intent intent = new Intent(this, AddRemoteDeviceActivity.class);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.PRODUCTFUN, productFun);
		intent.putExtra(InfraredCodeLibraryConstant.IntentTag.FUNDETIAL, funDetail);

		startActivity(intent);
	}

}
