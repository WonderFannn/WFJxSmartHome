package com.jinxin.jxsmarthome.fragment;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;

public class SplashModeFragment extends Fragment {

	private ImageView leftLoadIV;
	private ImageView leftModeIV;
	private ImageView centerLoadIV;
	private ImageView centerModeIV;
	private ImageView rightLoadIV;
	private ImageView rightModeIV;
	
	private TextView tvInit;
	private Handler handler;
	private int dotNum = 0;

	Animation leftScaleAnim;
	Animation leftTransAnim;
	Animation centerScaleAnim;
	Animation centerTransAnim;
	Animation rightScaleAnim;
	Animation rightTransAnim;
	ScheduledThreadPoolExecutor threadPool;
	private boolean isForground = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_splash_mode, container, false);
		setHasOptionsMenu(true);

		leftScaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_mode_scale);
		leftTransAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_mode_trans);
		leftScaleAnim.setFillAfter(true);
		leftTransAnim.setFillAfter(true);
		
		centerScaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_mode_scale);
		centerTransAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_mode_trans);
		centerScaleAnim.setFillAfter(true);
		centerTransAnim.setFillAfter(true);
		
		
		rightScaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_mode_scale);
		rightTransAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_anim_mode_trans);
		rightScaleAnim.setFillAfter(true);
		rightTransAnim.setFillAfter(true);
		
		tvInit = (TextView)view.findViewById(R.id.tv_init);
		
		leftTransAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				centerLoadIV.setVisibility(View.VISIBLE);
				centerLoadIV.startAnimation(centerScaleAnim);
				centerModeIV.startAnimation(centerTransAnim);

				centerTransAnim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						rightLoadIV.setVisibility(View.VISIBLE);
						rightLoadIV.startAnimation(rightScaleAnim);
						rightModeIV.startAnimation(rightTransAnim);
						
						rightTransAnim.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// 等待或进入主程序
							}
						});
					}
				});
			}
		});

		leftLoadIV = (ImageView) view.findViewById(R.id.leftLoadIV);
		leftModeIV = (ImageView) view.findViewById(R.id.leftModeIV);

		centerLoadIV = (ImageView) view.findViewById(R.id.centerLoadIV);
		centerModeIV = (ImageView) view.findViewById(R.id.centerModeIV);

		rightLoadIV = (ImageView) view.findViewById(R.id.rightLoadIV);
		rightModeIV = (ImageView) view.findViewById(R.id.rightModeIV);
		
		leftLoadIV.setVisibility(View.VISIBLE);
		leftLoadIV.startAnimation(leftScaleAnim);
		leftModeIV.startAnimation(leftTransAnim);
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(++dotNum <= 6) {
					StringBuffer bf = new StringBuffer();
					for(int i=0;i<dotNum;i++) {
						bf.append(".");
					}
					if (isForground) {
						tvInit.setText(getString(R.string.splash_initing) + bf.toString());
					}
				} else {
					dotNum = 0;
				}
				super.handleMessage(msg);
			}
		};
		threadPool = new ScheduledThreadPoolExecutor(1);
		threadPool.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		}, 1, 1, TimeUnit.SECONDS);

		return view;
	}
	
	@Override
	public void onResume() {
		isForground = true;
		super.onResume();
	}
	
	@Override
	public void onPause() {
		isForground = false;
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		threadPool.shutdownNow();
		super.onDestroy();
	}

}
