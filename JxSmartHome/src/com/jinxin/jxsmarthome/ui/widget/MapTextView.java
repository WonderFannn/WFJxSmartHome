package com.jinxin.jxsmarthome.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;

public class MapTextView extends RelativeLayout {
	private TextView textKey;
	private TextView textValue;
	
	public MapTextView(Context context) {
		this(context, null);
	}
	
	public MapTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        inflater.inflate(R.layout.custom_maptextview, this);
        
        textKey = (TextView)findViewById(R.id.maptextview_key);
        textValue = (TextView)findViewById(R.id.maptextview_value);
        
        init(attrs);
	}
	
	private void init(AttributeSet attrs) {
		TypedArray tArr = getContext().obtainStyledAttributes(attrs, R.styleable.MapTextView);
		String key = tArr.getString(R.styleable.MapTextView_textKey);
		String value = tArr.getString(R.styleable.MapTextView_textValue);
		
		if(key != null) {
			textKey.setText(key);
		}
		if(value != null) {
			textValue.setText(value);
		}
		
		tArr.recycle();
	}
	
	public void hideValue(boolean b) {
		if(b) {
			textValue.setVisibility(View.INVISIBLE);
		}else {
			textValue.setVisibility(View.VISIBLE);
		}
	}

	////////////////////////////////////////////////////
	// getters and setters
	////////////////////////////////////////////////////
	public String getTextKey() {
		return textKey.getText().toString();
	}

	public void setTextKey(String textKey) {
		this.textKey.setText(textKey);
	}

	public String getTextValue() {
		return textValue.getText().toString();
	}

	public void setTextValue(String value) {
		textValue.setText(value);
	}
}
