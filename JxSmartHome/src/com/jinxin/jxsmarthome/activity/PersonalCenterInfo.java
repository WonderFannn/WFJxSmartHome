package com.jinxin.jxsmarthome.activity;

import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.widget.TextView;

import com.jinxin.datan.net.command.CustomerDetailTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.main.JxshApp;

/**个人详情
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class PersonalCenterInfo extends BaseActionBarActivity {
	
	private CustomerDetailTask cdTask = null;
	private Customer customer = null;
	private List<Customer> _customers = null;
	
	private TextView customerName;
	private TextView gender;
	private TextView age;
	private TextView phoneNo;
	private TextView location;
	private TextView address;
	private TextView state;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	/**
	 * 初始化
	 */
	private void initView() {
		this.setContentView(R.layout.customer_info_layout);
		getSupportActionBar().setTitle(getResources().getString(R.string.personal_info));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		this.customerName = (TextView) findViewById(R.id.tv_customer_name);
		this.gender = (TextView) findViewById(R.id.tv_gender);
		this.age = (TextView) findViewById(R.id.tv_age);
		this.phoneNo = (TextView) findViewById(R.id.tv_phone_no);
		this.location = (TextView) findViewById(R.id.tv_location);
		this.address = (TextView) findViewById(R.id.tv_address);
		this.state = (TextView) findViewById(R.id.tv_state);
	}

	private void initData() {

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		cdTask = new CustomerDetailTask(null);
		cdTask.addListener(new ITaskListener<ITask>() {

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

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if(arg != null && arg.length > 0){
					customer = (Customer) arg[0];
					checkDataBase();
				}
				mUIHander.sendEmptyMessage(0);
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
				// TODO Auto-generated method stub
				
			}
		});
		cdTask.start();
	}
	
	/**
	 * 检测并更新数据库（避免修改不允许更新的数据）
	 */
	private void checkDataBase(){
		if(customer == null)return;
		CustomerDaoImpl _cdImpl = new CustomerDaoImpl(PersonalCenterInfo.this);
//		if(_cdImpl.isExist("select customerId from customer where customerId='"+customer.getCustomerId()+ "'", null)){
//			_cdImpl.update(customer);
//		}else {
//		}
		_cdImpl.clear();
		_cdImpl.insert(customer,true);
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		switch (msg.what) {
		case 0:
			if (customer != null) {
				this.customerName.setText(customer.getCustomerName());
				if (customer.getSex()==1) {
					this.gender.setText("男");
				}else{
					this.gender.setText("女");
				}
				this.age.setText(customer.getAge()+"");
				this.phoneNo.setText(customer.getMobile());
				this.location.setText(customer.getProvence()+" "+customer.getCity()+" "+customer.getContry());
				this.address.setText(customer.getAddress());
				this.state.setText(customer.getComments());
			}
			break;

		default:
			break;
		}
	}

}
