package com.jinxin.jxsmarthome.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.jinxin.datan.net.command.DeleteProductConnectionTask;
import com.jinxin.datan.net.command.ProductConntectionListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.ProductConnectionDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.ProductConnection;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.ProductConnectionAdapter;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;

/**
 * 我的关联列表
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ConnectionControlActivity extends BaseActionBarActivity {

	private Context context;
	private ListView mListView;
	
	private ProductConnectionDaoImpl pcDaoOMpl;
	private List<ProductConnection> mConnectionList;
	private ProductConnectionAdapter adapter;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_scene, menu);
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("我的关联");
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.add_mode_btn://设置关联
			startActivity(new Intent(ConnectionControlActivity.this,AddConnectionControlActivity.class));
			break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mUIHander.sendEmptyMessage(1);
	}
	
	private void initView() {
		setContentView(R.layout.activity_connection_control);
		this.mListView = (ListView) findViewById(R.id.listview);
		
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showDeleteConfirmDialog(position);
				return false;
			}
		});
	}

	private void initData() {
		context = this;
		pcDaoOMpl = new ProductConnectionDaoImpl(this);
		syncData();
//		mConnectionList = pcDaoOMpl.find();
	}
	
	//同步关联操作数据
	private void syncData() {
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
//					mConnectionList = connectionList;
					mUIHander.sendEmptyMessage(0);
				}
				
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		pclTask.start();
	}
	
	private void showDeleteConfirmDialog(final int position){
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.delete_connection_confirm_dialog, null);
		final CustomerCenterDialog dialog = new CustomerCenterDialog(context, R.style.dialog, v);

		Button btnSure = (Button) v.findViewById(R.id.button_ok);
		Button deleteAll = (Button) v.findViewById(R.id.button_cancel);
		ImageView cancleBtn = (ImageView) v.findViewById(R.id.iv_dismiss);
		btnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deletcTask(position,false);
				dialog.dismiss();
			}
		});
		deleteAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deletcTask(position,true);
				dialog.dismiss();
			}
		});
		
		cancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	private void deletcTask(final int pos,final boolean isAll) {
		DeleteProductConnectionTask dpcTask = null;
		if (!isAll) {
			dpcTask = new DeleteProductConnectionTask(
					context, String.valueOf(mConnectionList.get(pos).getId()), null);
		}else{
			dpcTask =  new DeleteProductConnectionTask(
					context,null, String.valueOf(mConnectionList.get(pos).getFunId()));
		}
		dpcTask.addListener(new ITaskListener<ITask>() {

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
				JxshApp.showToast(context, "删除成功");
				if (!isAll) {//删除一条关联
					pcDaoOMpl.delete(mConnectionList.get(pos).getId());
					mConnectionList.remove(pos);
					mUIHander.sendEmptyMessage(0);
				}else{//删除当前funId对应所有关联设备
					List<ProductConnection> deleteList = pcDaoOMpl.
							find(null, "funId=?", new String[]{String.
									valueOf(mConnectionList.get(pos).getFunId())},
									null, null, null, null);
					if (deleteList != null && deleteList.size() > 0) {
						for (ProductConnection _pc : deleteList) {
							pcDaoOMpl.delete(_pc.getId());
						}
					}
					mUIHander.sendEmptyMessage(1);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		dpcTask.start();
	}

	@Override
	public void uiHandlerData(Message msg) {
		super.uiHandlerData(msg);
		switch (msg.what) {
		case 0:
			mConnectionList = pcDaoOMpl.find();
			if (mConnectionList != null) {
//				if (adapter != null) {
					adapter = new ProductConnectionAdapter(mConnectionList, ConnectionControlActivity.this);
					mListView.setAdapter(adapter);
//					adapter.setListData(mConnectionList);
//					adapter.notifyDataSetChanged();
//				}
			}
			break;
		case 1:
			mConnectionList = pcDaoOMpl.find();
			if (adapter != null) {
				adapter.setListData(mConnectionList);
				adapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}

}
