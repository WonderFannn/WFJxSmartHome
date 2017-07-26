package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.ProductFun;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class GeocoderHealthManagerFragment extends Fragment implements OnClickListener, OnPageChangeListener{
	
	private ProductFun productFun = null;
	private View view ;
	private TextView heartRate;
	private TextView exercise;
	private TextView sleep;
	private List<TextView> textViews;
	private ViewPager viewPager;
	private ViewPagerAdapter pagerAdapter;
	private HeartRateFragment heartRateFragment;
	private ExerciseFragment exerciseFragment;
	private SleepFragment sleepFragment;
	private List<Fragment> fragments;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle bundle=getArguments();
		productFun=(ProductFun) bundle.getSerializable("productFun");
		view= inflater.inflate(R.layout.fragment_geocoder_health_manager, null);
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		textViews=new ArrayList<TextView>();
		heartRate=(TextView) view.findViewById(R.id.fragment_geocoder_heart_rate_tv);
		exercise=(TextView) view.findViewById(R.id.fragment_geocoder_execise_tv);
		sleep=(TextView) view.findViewById(R.id.fragment_geocoder_sleep_tv);
		viewPager=(ViewPager) view.findViewById(R.id.fragment_geocoder_viewpager);
		heartRate.setOnClickListener(this);
		exercise.setOnClickListener(this);
		sleep.setOnClickListener(this);
		textViews.add(heartRate);
		textViews.add(exercise);
		textViews.add(sleep);
		onClick(heartRate);
		fragments=new ArrayList<Fragment>();
		heartRateFragment=new HeartRateFragment();
		exerciseFragment=new ExerciseFragment();
		sleepFragment=new SleepFragment();
		fragments.add(heartRateFragment);
		fragments.add(exerciseFragment);
		fragments.add(sleepFragment);
		pagerAdapter=new ViewPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		for (int i = 0; i < textViews.size(); i++) {
			textViews.get(i).setSelected(false);
//			textViews.get(i).setBackgroundResource(R.color.white);
			textViews.get(i).setBackgroundColor(Color.argb(0, 255, 255, 255)); 
		}
		switch (arg0.getId()) {
		case R.id.fragment_geocoder_heart_rate_tv:
			textViews.get(0).setSelected(true);
			textViews.get(0).setBackgroundResource(R.drawable.tv_geocoder_select);
			viewPager.setCurrentItem(0);
			break;
		case R.id.fragment_geocoder_execise_tv:
			textViews.get(1).setSelected(true);	
			textViews.get(1).setBackgroundResource(R.drawable.tv_geocoder_select);
			viewPager.setCurrentItem(1);
			break;
		case R.id.fragment_geocoder_sleep_tv:
			textViews.get(2).setSelected(true);
			textViews.get(2).setBackgroundResource(R.drawable.tv_geocoder_select);
			viewPager.setCurrentItem(2);
			break;

		default:
			break;
		}
	}
	
	public class ViewPagerAdapter extends FragmentPagerAdapter {
		
		private List<Fragment> fragments;

		public ViewPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
			super(fm);
			this.fragments=fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
		}
		
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		onClick(textViews.get(arg0));
	}
}
