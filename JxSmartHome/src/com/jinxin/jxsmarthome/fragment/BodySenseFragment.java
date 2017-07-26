package com.jinxin.jxsmarthome.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 人体感应详细控制
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class BodySenseFragment extends Fragment {
	
	private Context context;
	private View view = null;
	private TextView delayTime, lightSense;
	private FunDetailConfig fConfig;
	private String _times,_lights;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=CommDefines.getSkinManager().view(R.layout.body_sensor, container);
		initData();
		initView(view);
		return view;
	}

	private void initData() {
		try {
			fConfig = (FunDetailConfig) getArguments().get("FunDetailConfig");
			JSONObject _jo = new JSONObject(fConfig.getParams());
			_times = _jo.getString("delay");
			_lights = _jo.getString("lightSense");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initView(View view) {
		delayTime = (TextView) view.findViewById(R.id.delay_time);
		lightSense = (TextView) view.findViewById(R.id.light_sense_level);
		if (!CommUtil.isNull(_times)) {
			delayTime.setText(_times+"s");
		}
		if (!CommUtil.isNull(_lights)) {
			lightSense.setText(_lights);
		}
	}

}
