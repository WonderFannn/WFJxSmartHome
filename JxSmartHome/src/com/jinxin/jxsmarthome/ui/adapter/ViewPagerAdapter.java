package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {
	
	private ArrayList<View> viewList;
	
	public ViewPagerAdapter(ArrayList<View> viewList) {
		this.viewList = viewList;
	}
	
	public void setPageList(ArrayList<View> viewList){
		this.viewList = viewList;
	}

	@Override
	public int getCount() {
		return this.viewList == null ? 0 : viewList.size();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position), 0);//添加页卡  
        return viewList.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager)container).removeView(viewList.get(position));
	}
	
	

}
