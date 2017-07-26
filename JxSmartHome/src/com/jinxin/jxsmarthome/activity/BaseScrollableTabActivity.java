package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.ui.widget.tab.ScrollableTabView;
import com.jinxin.jxsmarthome.ui.widget.tab.ScrollingTabsAdapter;
import com.jinxin.jxsmarthome.ui.widget.tab.ViewPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

/**
 * 可自定义tab的 actionbar activity
 * 		使用方式请查看{@link TestActivity}
 * @author TangLong
 * @company 金鑫智慧
 */
public abstract class BaseScrollableTabActivity extends BaseActionBarActivity implements
		ViewPager.OnPageChangeListener {
	private ScrollableTabView mTabs;
	private ViewPager mViewPager;
	private ScrollingTabsAdapter mTabAdapter;
	private ViewPagerAdapter mViewPageAdapter;
	private ArrayList<Fragment> mPagerItemList;
	private List<String> mTabNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParam();
		initView();
	}
	
	private void initParam() {
		mTabNames = new ArrayList<String>();
		mPagerItemList = new ArrayList<Fragment>();
		initTabsAndFragment();
	}
	
	private void initView() {
		setContentView(R.layout.activity_base_scrollable_tab);
		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		mTabs = (ScrollableTabView)findViewById(R.id.tab_view);
		initViewPager();
		initTabs();
	}
	
	private void initTabs() {
		mTabAdapter = new ScrollingTabsAdapter(this, mTabNames);
		mTabs.setAdapter(mTabAdapter);
		mTabs.setViewPage(mViewPager);
	}
	
	private void initViewPager() {
		mViewPageAdapter = new ViewPagerAdapter(mPagerItemList, getSupportFragmentManager());
		mViewPager.setAdapter(mViewPageAdapter);
		mViewPager.setOnPageChangeListener(this);
	}
	
	/**
	 * 增加tab和fragment
	 * @param tabName		tab名称
	 * @param fragment		tab的fragment
	 * @return				当前activity对象
	 */
	public BaseScrollableTabActivity addTab(String tabName,  Fragment fragment) {
		mTabNames.add(tabName);
		mPagerItemList.add(fragment);
		return this;
	}
	
	/**
	 * 初始化tabs和fragment相关的数据，子类必须在这个方法里对tab和fragment做初始化的设置
	 */
	public abstract void initTabsAndFragment();
	
	@Override
	public void onPageScrollStateChanged(int state) {
		// state ==1的时候表示正在滑动，state==2的时候表示滑动完毕了，state==0的时候表示什么都没做，就是停在那。
		// do nothing...
	}

	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
		// position:当前页面，及你点击滑动的页面, arg1:当前页面偏移的百分比, arg2:当前页面偏移的像素位置
	}

	@Override
	public void onPageSelected(int position) {// position:当前选中也没位置
		if(mTabs != null){
			mTabs.selectTab(position);
		}
		refreshFragmente(position);
	}
	
	protected abstract void refreshFragmente(int Position);
	
	public Fragment getFragment(int position) {
		return mPagerItemList.get(position);
	}
}
