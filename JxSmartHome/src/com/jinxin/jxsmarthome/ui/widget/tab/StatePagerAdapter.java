package com.jinxin.jxsmarthome.ui.widget.tab;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class StatePagerAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> mPagerItemList;
	
	public StatePagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public StatePagerAdapter(ArrayList<Fragment> pagerItemList, FragmentManager fm) {
		this(fm);
		this.mPagerItemList = pagerItemList;
	}

	@Override
	public Fragment getItem(int position) {
		return mPagerItemList.get(position);
	}

	@Override
	public int getCount() {
		return mPagerItemList.size();
	}
}