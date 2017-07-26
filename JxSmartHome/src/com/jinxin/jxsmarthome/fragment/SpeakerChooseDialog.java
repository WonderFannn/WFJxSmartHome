package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.ui.adapter.SpeakerChooseDialogAdapter;
import com.jinxin.jxsmarthome.util.CommUtil;

public class SpeakerChooseDialog extends Dialog {
	
	public SpeakerChooseDialog(Context context) {
		super(context);
	}
	
	public SpeakerChooseDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder {
		private Context context;
		private String title;
		private ListView mSpeakers;
		private String positiveText;
		private String negativeText;
		private List<String> speakerNames;
		
		private View.OnClickListener positiveButtonClickListener;
		private View.OnClickListener negativeButtonClickListener;
		
		public Builder(Context context) {
			this.context = context;
		}
		
		public Builder setTitle(int title) {
			this.title = (String)context.getText(title);
			return this;
		}
		
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		
		public Builder setPositiveButton(int positiveText, 
				View.OnClickListener positiveButtonClickListener) {
			this.positiveText = (String)context.getText(positiveText);
			this.positiveButtonClickListener = positiveButtonClickListener;
			return this;
		}
		
		public Builder setPositiveButton(String positiveText, 
				View.OnClickListener positiveButtonClickListener) {
			this.positiveText = positiveText;
			this.positiveButtonClickListener = positiveButtonClickListener;
			return this;
		}
		
		public Builder setNegativeButton(int negativeText, 
				View.OnClickListener negativeButtonClickListener) {
			this.negativeText = (String)context.getText(negativeText);
			this.negativeButtonClickListener = negativeButtonClickListener;
			return this;
		}
		
		public Builder setNegativeButton(String negativeText, 
				View.OnClickListener negativeButtonClickListener) {
			this.negativeText = negativeText;
			this.negativeButtonClickListener = negativeButtonClickListener;
			return this;
		}
		
		public SpeakerChooseDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final SpeakerChooseDialog dialog = new SpeakerChooseDialog(context, R.style.dialog);
			
			View view = inflater.inflate(R.layout.custom_dialog_speaker_choose, null);
			
			TextView mTitle = (TextView)view.findViewById(R.id.speaker_choose_title);
			ListView mSpeakers = (ListView)view.findViewById(R.id.speaker_choose_list);
			Button mPositive = (Button)view.findViewById(R.id.speaker_choose_positiveButton);
			Button mNegative = (Button)view.findViewById(R.id.speaker_choose_negativeButton);
			
			mTitle.setText(title);
			mPositive.setText(positiveText);
			mNegative.setText(negativeText);
			initSpeakerSetting();
			SpeakerChooseDialogAdapter adapter = new SpeakerChooseDialogAdapter(context, speakerNames);
			mSpeakers.setAdapter(adapter);
			
			mPositive.setOnClickListener(positiveButtonClickListener);
			
			mNegative.setOnClickListener(negativeButtonClickListener);
			
			dialog.setContentView(view);
			
			return dialog;
		}
		
		private void initSpeakerSetting() {
			FunDetailConfigDaoImpl daoImpl = new FunDetailConfigDaoImpl(context);
			List<FunDetailConfig> fdc4Amplifiers = daoImpl.find(null, "funType=?" , 
					new String[] {ProductConstants.FUN_TYPE_POWER_AMPLIFIER}, null, null, null, null);
			if(fdc4Amplifiers != null && fdc4Amplifiers.size() > 0) {
				FunDetailConfig fdc4Amplifier = fdc4Amplifiers.get(0);
				if(fdc4Amplifier != null && !CommUtil.isEmpty(fdc4Amplifier.getParams())) {
					try {
						JSONObject jsonObj = new JSONObject(fdc4Amplifier.getParams());
						
						JSONArray jsonArr = jsonObj.getJSONArray("param");
						
						int length = jsonArr.length();
						speakerNames = new ArrayList<String>();
						for(int i = 0; i < jsonArr.length(); i++) {
							speakerNames.add((String) jsonArr.get(i));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
}

