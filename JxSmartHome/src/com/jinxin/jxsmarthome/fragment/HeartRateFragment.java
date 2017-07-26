package com.jinxin.jxsmarthome.fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.jinxin.jxsmarthome.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HeartRateFragment extends Fragment{
	
	private View view;
	private ImageView imageView;
	private GlideDrawableImageViewTarget target;
	private View viewpagerView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.fragment_geocoder_heartrate,container,false);
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		imageView=(ImageView) view.findViewById(R.id.fragment_geocoder_heartrate_imageview);
		target=new GlideDrawableImageViewTarget(imageView, 1);
		Glide.with(getActivity()).load(R.drawable.heart_rate).
			diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter().into(target);
	}
	

}
