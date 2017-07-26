package com.jinxin.jxsmarthome.ui.widget.textviewex;

/***
 * 支持大中小三种SIZE的TextView
 * 用法：在layout布局文件中添加xmlns:fontType="http://schemas.android.com/apk/res/com.jinxin.jxsmarthome"
 		<com.jinxin.jxsmarthome.ui.widget.textviewex.TextViewEx
            android:id="@+id/textview_title_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="标题："
            android:textColor="@color/light_gray"
            fontType:large="@dimen/large_font_size_content_title"
            fontType:middle="@dimen/font_size_content_title"
            fontType:small="@dimen/small_font_size_content_title" />
 * @author huangnenghai 2013-7-5 下午5:15:01
 */


import com.jinxin.jxsmarthome.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class TextViewEx extends TextView implements TextSizeChangeMonitor.ITextSizeChange {

	public static class Manager {
		private static TextSizeChangeMonitor monitor = null;

		public static void setTextSizeChangeMonitor(TextSizeChangeMonitor _monitor) {
			monitor = _monitor;
		}

		public static TextSizeChangeMonitor getTextSizeChangeMonitor() {
			return monitor;
		}
	}

	public static final int TEXT_TYPE_LARGE = 1;
	public static final int TEXT_TYPE_MIDDLE = 2;
	public static final int TEXT_TYPE_SMALL = 3;

	private float textSizeLarge = 0.0f;
	private float textSizeMiddle = 0.0f;
	private float textSizeSmall = 0.0f;

	private boolean isAttached = false;

	private static TextSizeChangeMonitor monitor = null;

	public TextViewEx(Context context, float sizeLarge, float sizeMiddle, float sizeSmall) {
		super(context);
		textSizeLarge = sizeLarge;
		textSizeMiddle = sizeMiddle;
		textSizeSmall = sizeSmall;
		monitor = Manager.getTextSizeChangeMonitor();
	}

	public TextViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextType);
		float textSize = getTextSize();
		textSizeLarge = a.getDimension(R.styleable.TextType_large, textSize);
		textSizeMiddle = a.getDimension(R.styleable.TextType_middle, textSize);
		textSizeSmall = a.getDimension(R.styleable.TextType_small, textSize);
		monitor = Manager.getTextSizeChangeMonitor();	
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (isAttached)
			return;
		isAttached = true;
		if (monitor != null){
			updateTextSize(monitor.getCurrentType());
			monitor.addChange(this);
		}
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (!isAttached)
			return;
		isAttached = false;
		if (monitor != null)
			monitor.removeChange(this);
	}

	@Override
	public void onTextSizeChange(int type) {
		updateTextSize(type);
	}

	private void updateTextSize(int type) {
		if (type == TEXT_TYPE_LARGE) {
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeLarge);
		} else if (type == TEXT_TYPE_MIDDLE) {
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeMiddle);
		} else if (type == TEXT_TYPE_SMALL) {
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
		}
	}
}
