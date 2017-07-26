package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.ui.adapter.ModeDetailAdapter;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;

public class ModeDetailActivity extends BaseActionBarActivity implements OnScrollListener{
	
	private ExpandableListView exListview = null;
	private ModeDetailAdapter adapter = null;
	private List<List<ProductFunVO>> productFunVOLists;
	private CustomerPattern customerPattern = null;
	private List<FunDetail> funDetailList = null;
	private int patternId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("模式查看");
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return true;
	}
	
	/**
	 * 初始化View
	 */
	private void initView() {
		setContentView(R.layout.mode_detail_layout);
		this.exListview = (ExpandableListView) findViewById(R.id.mode_device_list);
		adapter = new ModeDetailAdapter(ModeDetailActivity.this,
				productFunVOLists, funDetailList);
		exListview.setAdapter(adapter);
		exListview.setOnScrollListener(this);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		List<ProductPatternOperation> ppoList = null;
		// 取当前模式数据
		this.patternId = this.getIntent().getIntExtra("patternId", -1);
		if (this.patternId != -1) {
			CustomerPatternDaoImpl cpDaoImpl = new CustomerPatternDaoImpl(
					ModeDetailActivity.this);
			this.customerPattern = cpDaoImpl.get(this.patternId);
			ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
					ModeDetailActivity.this);
			ppoList = ppoDaoImpl.find(null, "patternId=?",
					new String[] { Integer.toString(this.patternId) }, null,
					null, null, null);
		}
		// 设备类型数据
		FunDetailDaoImpl fdDaoImpl = new FunDetailDaoImpl(
				ModeDetailActivity.this);
		this.funDetailList = fdDaoImpl.find(null, "joinPattern=?",
				new String[]{Integer.toString(1)}, null, null, null, null);
		// 填充每种类型列表数据
		this.productFunVOLists = new ArrayList<List<ProductFunVO>>();
		List<ProductFun> _pfList = new ArrayList<ProductFun>();
		for (FunDetail fd : this.funDetailList) {
			_pfList.clear();
			if (fd != null) {
				List<ProductFun> tempList = CommonMethod
						.currentTypeFillingDevice(ModeDetailActivity.this,
								fd.getFunType());
				for (ProductFun productFun : tempList) {
					for (ProductPatternOperation _ppo : ppoList) {
						if (productFun.getFunId() == _ppo.getFunId()) {
							_pfList.add(productFun);
						}
					}
				}
				this.productFunVOLists.add(creatProductFunVOList(_pfList,
						ppoList, fd));
			}
		}
	}
	
	/**
	 * 构建设备显示列表
	 * 
	 * @param pfList
	 *            设备功能列表
	 * @param ppoList
	 *            模式控制列表
	 * @return
	 */
	private List<ProductFunVO> creatProductFunVOList(List<ProductFun> pfList,
			List<ProductPatternOperation> ppoList, FunDetail fd) {
		List<ProductFunVO> _list = new ArrayList<ProductFunVO>();
		if (pfList == null)
			return _list;
		for (ProductFun pf : pfList) {
			if (pf != null) {
				ProductFunVO _pfVO = new ProductFunVO(pf);
				_list.add(_pfVO);
				if (ppoList != null) {
					ProductPatternOperation _ppo = this
							.isExistProductPatternOperation(ppoList,
									pf.getFunId());
					if (_ppo != null) {
						_pfVO.setProductPatternOperation(_ppo);
						_pfVO.setSelected(true);
						if (_ppo.getOperation().equals("open") || _ppo.getOperation().
								equals("set") || _ppo.getOperation().equals("send")
								|| _ppo.getOperation().equals("play")
								||_ppo.getOperation().equals("hueandsat")
								||_ppo.getOperation().equals("on")
								||_ppo.getOperation().equals("up")
								||_ppo.getOperation().equals("double-on-off")
								||_ppo.getOperation().equals("five-on-off")
								||_ppo.getOperation().equals("six-on-off")
								||_ppo.getOperation().equals("three-on-off")
								||_ppo.getOperation().equals("autoMode")
								||_ppo.getOperation().equals("automode")) {
							_pfVO.setOpen(true);
						}else{
							_pfVO.setOpen(false);
						}
						
					}
				}
			}
		}
		return _list;
	}
	
	/**
	 * 判断当前funId的设备功能在该模式下是否已存在
	 * 
	 * @param ppoList
	 * @param funId
	 * @return
	 */
	private ProductPatternOperation isExistProductPatternOperation(
			List<ProductPatternOperation> ppoList, int funId) {
		if (ppoList == null || funId == -1)
			return null;
		for (ProductPatternOperation ppo : ppoList) {
			if (ppo != null) {
				if (ppo.getFunId() == funId) {
					return ppo;
				}
			}
		}
		return null;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	
}
