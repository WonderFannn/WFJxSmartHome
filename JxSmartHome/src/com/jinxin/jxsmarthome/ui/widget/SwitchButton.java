package com.jinxin.jxsmarthome.ui.widget;

import com.jinxin.jxsmarthome.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义发动开关空间
 * @author  TangLong
 * @company 金鑫智慧
 */
public class SwitchButton extends View {
	private Bitmap mOnBitmap;				// 状态开时背景
	private Bitmap mOffBitmap;				// 状态关时背景
	private Bitmap mThumbBitmap;			// 滑块图标
	private float currentX;					// 当前滑块位置
	private int thumbWidth;					// 滑块宽度
	private int switchBgWidth;				// 背景宽度
	private int switchBgHeight;				// 背景高度
	private boolean isOff;					// 开关状态
	private boolean isSlipping;				// 滑块是否在滑动
	private SwitchChangeListener listener;	// 状态改变监听
	
	public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	
	public SwitchButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SwitchButton(Context context) {
		this(context, null);
	}
	
	/** 初始化视图   */
	private void initView() {
		mOnBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ico_swithch_on);
		mOffBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ico_swithch_off);
		mThumbBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.switch_thumb);
		switchBgWidth = mOnBitmap.getWidth();
		switchBgHeight = mOnBitmap.getHeight();
		thumbWidth = mThumbBitmap.getWidth();
		this.setOnTouchListener(new MyTouchListener());
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		
		// 根据状态不同，绘制不同的背景
		if(isOff) {
			canvas.drawBitmap(mOffBitmap, matrix, paint);
		}else {
			canvas.drawBitmap(mOnBitmap, matrix, paint);
		}
		
		// 是否滑动
		if(isSlipping) {
			canvas.drawBitmap(mThumbBitmap, currentX, 17, paint);
		}else {
			if(isOff) {
				canvas.drawBitmap(mThumbBitmap, currentX - thumbWidth, 17, paint);
			}else {
				canvas.drawBitmap(mThumbBitmap, currentX, 17, paint);
			}
		}
	}
	
	/**
	 * 状态改变监听
	 * @param listener	监听器
	 */
	public void setOnSwitchChangeListener(SwitchChangeListener listener) {
		this.listener = listener;
	}
	
	/** switch change listener */
	public interface SwitchChangeListener {
		public void onChangeListener(View v, boolean isOff);
	}
	
	/**
	 * 滑动监听器
	 * @author  TangLong
	 * @company 金鑫智慧
	 */
	private class MyTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				if(event.getX() > switchBgWidth || event.getY() > switchBgHeight) {
					return false;
				}else {
					isSlipping = true;
					currentX = event.getX();
				}
				break;
			case MotionEvent.ACTION_MOVE :
				currentX = event.getX();
				break;
			case MotionEvent.ACTION_UP :
				isSlipping = false;
				if(currentX > switchBgWidth / 2) {
					isOff = false;
				}else {
					isOff = true;
				}
				listener.onChangeListener(v, isOff);
				break;
			}
			return true;
		}
		
	}
}
