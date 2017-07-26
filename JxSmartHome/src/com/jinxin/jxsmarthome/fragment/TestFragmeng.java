package com.jinxin.jxsmarthome.fragment;

import android.support.v4.app.Fragment;

public class TestFragmeng extends BaseScrollableTabFragment {

	@Override
	public void initTabsAndFragment() {
		addTab("啦啦啦", new Fragment());
		addTab("呵呵呵", new Fragment());
		addTab("哦哦哦", new Fragment());
	}
}
