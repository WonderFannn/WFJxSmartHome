package com.jinxin.jxsmarthome.ui.widget.tab;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> mPagerItemList;
	
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public ViewPagerAdapter(ArrayList<Fragment> pagerItemList, FragmentManager fm) {
		this(fm);
		this.mPagerItemList = pagerItemList;
	}

	@Override
	public Fragment getItem(int position) {
		return mPagerItemList.get(position);
	}
	
	public int getItemPosition(Object object) {
		Fragment fragment = (Fragment)object;
		return mPagerItemList.indexOf(fragment);
	}

	@Override
	public int getCount() {
		return mPagerItemList.size();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
//		Logger.error(null, "destroyItem");
		super.destroyItem(container, position, object);
	}
}