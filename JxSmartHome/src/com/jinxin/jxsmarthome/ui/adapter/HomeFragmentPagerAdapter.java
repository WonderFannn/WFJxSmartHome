package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments;

	public HomeFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public HomeFragmentPagerAdapter(FragmentManager fm,
			List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return this.fragments == null ? null : this.fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return this.fragments == null ? 0 : this.fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {

		// return super.getPageTitle(position);
		switch (position) {
		case 0:
			return "test 1";
		case 1:
			return "test 2";
		default:
			return "test default";
		}

	}

}
