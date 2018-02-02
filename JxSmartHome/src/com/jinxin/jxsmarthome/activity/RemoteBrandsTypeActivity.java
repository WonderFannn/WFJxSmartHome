package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jinxin.datan.net.command.AddCustomerRemoteBrandTask;
import com.jinxin.datan.net.command.CustomerBrandsListTask;
import com.jinxin.datan.net.command.CustomerMatchCodeTask;
import com.jinxin.datan.net.command.RemoteBrandsTypeListTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.RemoteBrandsType;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;
import com.jinxin.jxsmarthome.ui.widget.pinnerHeaderListView.IndexBarView;
import com.jinxin.jxsmarthome.ui.widget.pinnerHeaderListView.PinnedHeaderAdapter;
import com.jinxin.jxsmarthome.ui.widget.pinnerHeaderListView.PinnedHeaderListView;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 遥控设备品牌选择
 * @author YangJijun
 * @company 金鑫智慧
 */
@SuppressLint("NewApi")
public class RemoteBrandsTypeActivity extends BaseActionBarActivity {

	private Context context = null;
	
	private List<RemoteBrandsType> brandsList;
	
	// unsorted list items
	ArrayList<RemoteBrandsType> tempItems;
	// array list to store section positions
	ArrayList<Integer> mListSectionPos;
	
	// 排序后的搜索列表显示的List
	ArrayList<String> mListItems;
	// 排序后的搜索列表对应的拼音List
	ArrayList<String> mEngListItems;
	
	// custom list view with pinned header
	private PinnedHeaderListView mListView;

	// custom adapter
	private PinnedHeaderAdapter mAdaptor;

	// search box
	private EditText mSearchView;

	// loading view
	private ProgressBar mLoadingView;

	// empty view
	private TextView mEmptyView;
	
	private RemoteBrandsTypeListTask rbtlTask = null;
	private int deviceId = 0;
	private static final int LODING = 1;
	private static final int SHOW_DIALOG = 2;
	private static final int LEARNING = 3;
	
	public static final String BRAND= "customerBrand";
	
	private List<CustomerProduct> cpList = null;
	private CustomerProduct  currUFO = null;
	private CustomerProductDaoImpl cpdImpl = null;
	
	private CustomerCenterDialog searchDialog;
	private int type = 2;//1—码库  2—学习
	private CustomerBrands customerBrand = null;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_remote_learn, menu);
		
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("选择品牌");
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		initData();
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_remote_brands_layout);
		mSearchView = (EditText) findViewById(R.id.search_view);
		mLoadingView = (ProgressBar) findViewById(R.id.loading_view);
		mListView = (PinnedHeaderListView) findViewById(R.id.list_view);
		mEmptyView = (TextView) findViewById(R.id.empty_view);
	}

	private void initData() {
		deviceId = getIntent().getIntExtra("deviceId",0);
		tempItems = new ArrayList<RemoteBrandsType>();
		mListSectionPos = new ArrayList<Integer>();
		mListItems = new ArrayList<String>();
		mEngListItems = new ArrayList<String>();
		
		cpdImpl = new CustomerProductDaoImpl(context);
		
		cpList = cpdImpl.find(null, "code=? or code=?", new String[]{"005","009"}, null, null, null, null);
		Logger.debug(null, "cpList:"+cpList.size());
		if (cpList != null && cpList.size() > 0) {
			currUFO = cpList.get(0);
		}
		
		rbtlTask = new RemoteBrandsTypeListTask(context, deviceId);
		rbtlTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				mUIHander.sendEmptyMessage(LODING);
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					brandsList = (List<RemoteBrandsType>) arg[0];
					if (brandsList != null && brandsList.size() > 0) {
						Collections.sort(brandsList, comparator);
					}
					if (tempItems != null) {
						tempItems.clear();
						tempItems.addAll(brandsList);
					}
					mUIHander.sendEmptyMessage(0);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		rbtlTask.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.action_match://智能对码
			sendMatchAction();
			break;
		case R.id.action_learn://智能学习
			showLearnDialog();
			break;
		}
		return false;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		mSearchView.addTextChangedListener(filterTextWatcher);
		super.onPostCreate(savedInstanceState);
	}
	
	/**
	 * 智能对码  
	 */
	private void sendMatchAction(){
		if (currUFO == null) {
			JxshApp.showToast(context, "未找到飞碟设备无法进行此操作");
			return;
		}
		CustomerMatchCodeTask cmcTask = new CustomerMatchCodeTask(context, currUFO.getWhId(), deviceId);
		cmcTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
//				JxshApp.showLoading(context, "请发送遥控指令，设备将进行智能对码");
				mUIHander.sendEmptyMessage(SHOW_DIALOG);
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				if (searchDialog.isShowing()) {
					searchDialog.dismiss();
				}
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				if (searchDialog.isShowing()) {
					searchDialog.dismiss();
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				if (searchDialog.isShowing()) {
					searchDialog.dismiss();
				}
				JxshApp.showToast(context, "对码成功");
				if (arg != null && arg.length > 0) {
					brandsList = (List<RemoteBrandsType>) arg[0];
					if (brandsList != null && brandsList.size() > 0) {
						Collections.sort(brandsList, comparator);
					}
					if (tempItems != null) {
						tempItems.clear();
						tempItems.addAll(brandsList);
					}
					mUIHander.sendEmptyMessage(0);
				}
				
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		cmcTask.start();
	}

	/**
	 * 先生成保存遥控器
	 */
	private void showLearnDialog(){
		
		LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.save_remote_dialog_layout, null);
		final CustomerCenterDialog dialog = new CustomerCenterDialog(context, R.style.dialog, v);

		final EditText etName = (EditText) v.findViewById(R.id.et_remote_name);
		Button btnSure = (Button) v.findViewById(R.id.button_ok);
		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		btnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String nickName = etName.getText().toString();
				if (!TextUtils.isEmpty(nickName)) {
					addBrandTask(nickName);
					dialog.dismiss();
				}else{
					JxshApp.showToast(context, "输入遥控器昵称才能保存");
				}
			}
		});
		
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	/**
	 * 添加遥控器请求
	 * @param nickName
	 */
	private void addBrandTask(String nickName){
		if (currUFO == null ) {
			Logger.debug(null, "ufo is null");
			return;
		}
		AddCustomerRemoteBrandTask acrbTask = new AddCustomerRemoteBrandTask(context,
				"0", currUFO.getWhId(), deviceId, nickName, 0, type);
		acrbTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				JxshApp.showLoading(context, "正在生成遥控器，请稍后...");
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
				if (arg != null && arg.length > 0) {
					int brandId = (Integer) arg[0];
					loadData(brandId);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		acrbTask.start(); 
	}
	
	private void loadData(int brandId){
		if (brandId < 1)  return;
		CustomerBrandsListTask cBrandsListTask = new CustomerBrandsListTask(context,String.valueOf(brandId));
		cBrandsListTask.addListener(new ITaskListener<ITask>() {

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

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "onSuccess");
				JxshApp.closeLoading();
				if(arg != null && arg.length > 0){
					List<CustomerBrands> cbList = (List<CustomerBrands>) arg[0];
					if (cbList != null && cbList.size() > 0) {
						customerBrand = cbList.get(0);
						mUIHander.sendEmptyMessage(LEARNING);
					}
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		cBrandsListTask.start();
	}
	
	public class ListFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// NOTE: this function is *always* called from a background thread,
			// and
			// not the UI thread.

			tempItems.clear();
			String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<RemoteBrandsType> filterItems = new ArrayList<RemoteBrandsType>();
				synchronized (this) {
					for (RemoteBrandsType item : brandsList) {
						if (item.getEbrandName().toLowerCase(Locale.getDefault()).startsWith(constraintStr)
								|| item.getBrandName().startsWith(constraintStr)) {
							filterItems.add(item);
							tempItems.add(item);
						}
					}
					result.count = filterItems.size();
					result.values = filterItems;
				}
			} else {
				synchronized (this) {
					result.count = brandsList.size();
					result.values = brandsList;
					tempItems.addAll(brandsList);
				}
			}
			return result;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
//			if (tempItems != null) {
//				tempItems.clear();
//				tempItems = (ArrayList<RemoteBrandsType>) results.values;
//			}
			setIndexBarViewVisibility(constraint.toString());
			mUIHander.sendEmptyMessage(0);
		}

	}
	
	private void setIndexBarViewVisibility(String constraint) {
		// hide index bar for search results
		if (constraint != null && constraint.length() > 0) {
			mListView.setIndexBarVisibility(false);
		} else {
			mListView.setIndexBarVisibility(true);
		}
	}
	
	private void showLoading(View contentView, View loadingView, View emptyView) {
		contentView.setVisibility(View.GONE);
		loadingView.setVisibility(View.VISIBLE);
		emptyView.setVisibility(View.GONE);
	}

	private void showContent(View contentView, View loadingView, View emptyView) {
		contentView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.GONE);
		emptyView.setVisibility(View.GONE);
	}

	private void showEmptyText(View contentView, View loadingView, View emptyView) {
		contentView.setVisibility(View.GONE);
		loadingView.setVisibility(View.GONE);
		emptyView.setVisibility(View.VISIBLE);
	}
	
	Comparator<RemoteBrandsType> comparator = new Comparator<RemoteBrandsType>() {

        @Override
        public int compare(RemoteBrandsType lhs, RemoteBrandsType rhs) {
            return lhs.getEbrandName().compareToIgnoreCase(rhs.getEbrandName());
        }
    };
	
    private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
			String str = s.toString();
			if (mAdaptor != null && str != null)
				mAdaptor.getFilter().filter(str);
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}
	};
    
	private void setListAdaptor() {
		// 创建 PinnedHeaderAdapter
		mAdaptor = new PinnedHeaderAdapter(this, mListItems, mEngListItems, mListSectionPos);
		mListView.setAdapter(mAdaptor);

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		// set header view
		View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, mListView, false);
		mListView.setPinnedHeaderView(pinnedHeaderView);

		// set index bar view----------------------------------------------------------------------------------------------------------------------------------------------------
		IndexBarView indexBarView = (IndexBarView) inflater.inflate(R.layout.index_bar_view, mListView, false);
		indexBarView.setData(mListView, mListItems, mListSectionPos);
		mListView.setIndexBarView(indexBarView);

		// set preview text view
		View previewTextView = inflater.inflate(R.layout.preview_view, mListView, false);
		mListView.setPreviewView(previewTextView);

		// for configure pinned header view on scroll change
		mListView.setOnScrollListener(mAdaptor);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(RemoteBrandsTypeActivity.this,RemoteDeviceDebugActivity.class);
				if (tempItems != null && tempItems.size() > 0) {
					for (RemoteBrandsType mBrand : tempItems) {
						if (mBrand.getBrandName().equals(mListItems.get(position))) {
							intent.putExtra(BRAND, mBrand);
							startActivity(intent);
							break;
						}
					}
				}
			}
		});
	}
	
	@Override
	public void uiHandlerData(Message msg) {
		super.uiHandlerData(msg);
		switch (msg.what) {
		case 0:
			if (tempItems != null && tempItems.size() > 0) {
				mListItems.clear();
				mEngListItems.clear();
				mListSectionPos.clear();
				Collections.sort(tempItems, comparator);
				String prvSection = "";
				for (RemoteBrandsType _brand : tempItems) {
					String currSection = _brand.getEbrandName().
                            substring(0, 1).toUpperCase(Locale.getDefault());
					if (!prvSection.equals(currSection)){
						//添加中文列表
						mListItems.add(currSection);
						mListItems.add(_brand.getBrandName());
						//添加对应英文列表
						mEngListItems.add(currSection);
						mEngListItems.add(_brand.getEbrandName());
						// array list of section positions
						mListSectionPos.add(mListItems.indexOf(currSection));
						prvSection = currSection;
					} else {
						mListItems.add(_brand.getBrandName());
						mEngListItems.add(_brand.getEbrandName());
					}
				}
				if (mAdaptor == null){
                    setListAdaptor();
                    showContent(mListView, mLoadingView, mEmptyView);
                }else{
                	showContent(mListView, mLoadingView, mEmptyView);
                    mAdaptor.notifyDataSetChanged();
                }
			}else{
				showEmptyText(mListView, mLoadingView, mEmptyView);
			}
			break;
		case 1:
			showLoading(mListView, mLoadingView, mEmptyView);
			break;
		case SHOW_DIALOG:
			showSearchDialog();
			break;
		case LEARNING:
			Intent  intent = new Intent(context,CustomerRemoteLearnActivity.class);
			intent.putExtra(BRAND, customerBrand);
			startActivity(intent);
			break;
		}
	}
	
	/**
	 * 显示是否可用对话框
	 */
	private void showSearchDialog(){
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.remote_match_dialog_layout, null);
		 searchDialog = new CustomerCenterDialog(context, R.style.dialog, v);
		ImageView ivSearch = (ImageView) v.findViewById(R.id.iv_search);
		//摇摆    
		TranslateAnimation alphaAnimation2 = new TranslateAnimation(0, 25f, 0, 10);    
		alphaAnimation2.setDuration(1000);    
		alphaAnimation2.setRepeatCount(Animation.INFINITE);    
		alphaAnimation2.setRepeatMode(Animation.REVERSE);    
		ivSearch.setAnimation(alphaAnimation2);    
		alphaAnimation2.start();
		
		searchDialog.show();
	}
	
}
