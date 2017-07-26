package com.jinxin.widget.progressBar;

import com.jinxin.widget.progressBar.UniversalColorView.OnActionUpListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.MotionEvent;
import android.view.View;

public class RectColorView extends View {
	
	private static final int HSV_X = 250;//矩形右下水平坐标
	private static final int HSV_Y_TOP = 0;
	private static final int HSV_Y_BOTOM = 50;
	private Paint mHSVPaint;//矩形画笔
	private int[] mHSVColors;
	private int mColor; //画笔颜色
	
	private int tempColor;
	
	private OnColorChangedListener mListener;//圆形选择器监听
	private OnColorGetListener rectListener;//矩形选择器监听
	private OnActionUpListener upListener;//发送指令监听
	
	public RectColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultParam();
	}

	public RectColorView(Context context,OnColorGetListener getColorListener, int color) {
		super(context);
		mColor = color;
		rectListener = getColorListener;
		mHSVColors = new int[] { 0xFF000000, color, 0xFFFFFFFF };
		mHSVPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHSVPaint.setStrokeWidth(10);
	}

	/**
	 * 初始化属性
	 */
	public void defaultParam(){
		mHSVColors = new int[] { 0xFF000000, Color.WHITE, 0xFFFFFFFF };
		mHSVPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		mHSVPaint.setColor(Color.WHITE);
        mHSVPaint.setStrokeWidth(10);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(HSV_X, HSV_Y_BOTOM - HSV_Y_TOP);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		mHSVColors[1] = mHSVPaint.getColor();
        mHSVPaint.setShader(new LinearGradient(0 - HSV_X, 0, HSV_X, 0,
                mHSVColors, null, Shader.TileMode.CLAMP));
        canvas.drawRect(new RectF(0 - HSV_X, HSV_Y_TOP, HSV_X, HSV_Y_BOTOM),
                mHSVPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX() - HSV_X/2+50;
        float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				if ((x >= 0 - HSV_X && x <= HSV_X)
	                    && (y <= HSV_Y_BOTOM && y >= HSV_Y_TOP)) {
	                // see if we are in the hsv slider    
	                int a, r, g, b, c0, c1;    
	                float p;    
	                // set the center paint to this color    
	                if (x < 0) {    
	                    c0 = mHSVColors[0];    
	                    c1 = mHSVColors[1];    
	                    p = (x + HSV_X) / HSV_X;
	                } else {    
	                    c0 = mHSVColors[1];    
	                    c1 = mHSVColors[2];    
	                    p = x / HSV_X;
	                }    
	                a = ave(Color.alpha(c0), Color.alpha(c1), p);    
	                r = ave(Color.red(c0), Color.red(c1), p);    
	                g = ave(Color.green(c0), Color.green(c1), p);    
	                b = ave(Color.blue(c0), Color.blue(c1), p);
	                mListener.colorChanged(this,    
                            ColorChooserType.UNIVERSAL_COLOR_TYPE, Color.argb(a,r,g,b));//改变色盘中心颜色
//	                mHSVPaint.setColor(Color.argb(a,r,g,b));//改变色条画笔颜色
	                tempColor = Color.argb(a,r,g,b);
	                invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				upListener.sendColor(tempColor);
				break;
		
		}
		return true;
	}
	
	
	public interface OnColorGetListener {
		public void onClorGet(Object source,int color);
	}
	
	/**
     * 获取圆形颜色选择区的监听
     * @return
     */
	public OnColorGetListener getListener(){
		if (rectListener == null) {
			rectListener = new OnColorGetListener() {
				
				@Override
				public void onClorGet(Object source,int color) {
					mHSVPaint.setColor(color);
					invalidate();
				}
			};
		}
		return rectListener;
	}
	
	/**
	 * 添加Circle监听
	 * @param listener
	 */
	public void addListener(OnColorChangedListener listener){
		mListener = listener;
    }
	
	/**
     * 添加发送指令监听
     * @param listener
     */
    public void addActionListener(OnActionUpListener listener){
    	upListener = listener;
    }
	
    private int ave(int s, int d, float p) {    
        return s + Math.round(p * (d - s));    
    }    
    public int getColor() {    
        return mHSVPaint.getColor();    
    }    
    public void setColor(int color) {    
    	mHSVPaint.setColor(color);
    	invalidate();
    }

	

}
