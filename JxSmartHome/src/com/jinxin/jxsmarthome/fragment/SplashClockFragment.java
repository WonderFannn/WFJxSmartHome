package com.jinxin.jxsmarthome.fragment;

import java.util.Timer;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jinxin.jxsmarthome.R;
import com.umeng.analytics.MobclickAgent;

public class SplashClockFragment extends Fragment {

	private ImageView animationIV;
	private AnimationDrawable animationDrawable;
	Handler handler = new Handler();
	Timer timer = new Timer();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_splash_clock, container, false);
		setHasOptionsMenu(true);
		
		animationIV = (ImageView) view.findViewById(R.id.animationIV);
		animationIV.setImageResource(R.drawable.splash_clock_anim);
		animationDrawable = (AnimationDrawable) animationIV.getDrawable();
		animationDrawable.start();
		
		int duration = 0;
		for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
			duration += animationDrawable.getDuration(i);
		}
		
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (getActivity() != null) {
					SplashModeFragment fragment = new SplashModeFragment();
					getActivity().getSupportFragmentManager().beginTransaction().
					replace(R.id.splash_fragment,fragment).addToBackStack(null).commitAllowingStateLoss();
				}
			}
		}, duration);
		
		return view;
	}
}
