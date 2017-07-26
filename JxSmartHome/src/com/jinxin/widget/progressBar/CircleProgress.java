package com.jinxin.widget.progressBar;

import java.util.Timer;
import java.util.TimerTask;

import com.jinxin.jxsmarthome.R;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.View;

/**
 * @author YangJiJun
 * @company 金鑫智慧
 * 弧形进度条控件
 */
public class CircleProgress extends View {

	private static final int DEFAULT_MAX_VALUE = 100; // 默认进度条最大值
	private static final int DEFAULT_PAINT_WIDTH = 10; // 默认画笔宽度
	private static final int DEFAULT_PAINT_COLOR = 0xffffcc00; // 默认画笔颜色
	private static final boolean DEFAULT_FILL_MODE = true; // 默认填充模式
	private static final int DEFAULT_INSIDE_VALUE = 10; // 默认缩进距离

	private CircleAttribute mCircleAttribute; // 圆形进度条基本属性

	private double mMaxProgress; // 进度条最大值
	private double mMainCurProgress; // 主进度条当前值
	private double mSubCurProgress; // 主进度条当前值
	private double mStartDrawPos = 135; // 绘制圆形的起点（默认为0度即3点钟方向）

	private CartoomEngine mCartoomEngine; // 动画引擎

	private Drawable mBackgroundPicture; // 背景图
	
	public CircleProgress(Context context) {
		super(context);
		defaultParam();
	}

	public CircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultParam();

		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.CircleProgressBar);

		mMaxProgress = array.getInteger(R.styleable.CircleProgressBar_max,
				DEFAULT_MAX_VALUE); // 获取进度条最大值

		boolean bFill = array.getBoolean(R.styleable.CircleProgressBar_fill,
				DEFAULT_FILL_MODE); // 获取填充模式
		int paintWidth = array.getInt(
				R.styleable.CircleProgressBar_Paint_Width, DEFAULT_PAINT_WIDTH); // 获取画笔宽度
		mCircleAttribute.setFill(bFill);
		if (bFill == false) {
			mCircleAttribute.setPaintWidth(paintWidth);
		}

		int paintColor = array.getColor(
				R.styleable.CircleProgressBar_Paint_Color, DEFAULT_PAINT_COLOR); // 获取画笔颜色

//		Logger.info("", "paintColor = " + Integer.toHexString(paintColor));
		mCircleAttribute.setPaintColor(paintColor);

		mCircleAttribute.mSidePaintInterval = array.getInt(
				R.styleable.CircleProgressBar_Inside_Interval,
				DEFAULT_INSIDE_VALUE);// 圆环缩进距离

		array.recycle(); // 一定要调用，否则会有问题

	}

	/*
	 * 默认参数.02
	 */
	private void defaultParam() {
		mCircleAttribute = new CircleAttribute();

		mCartoomEngine = new CartoomEngine();

		mMaxProgress = DEFAULT_MAX_VALUE;
		mMainCurProgress = 0;
		mSubCurProgress = 0;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { // 设置视图大小
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		// int height = MeasureSpec.getSize(heightMeasureSpec);

		mBackgroundPicture = getBackground();
		if (mBackgroundPicture != null) {
			width = mBackgroundPicture.getMinimumWidth();
			// height = mBackgroundPicture.getMinimumHeight();
		}

		setMeasuredDimension(resolveSize(width, widthMeasureSpec),
				resolveSize(width, heightMeasureSpec));

	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mCircleAttribute.autoFix(w, h);

	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mBackgroundPicture == null) // 没背景图的话就绘制底色
		{
			canvas.drawArc(mCircleAttribute.mRoundOval, 0, 360,
					mCircleAttribute.mBRoundPaintsFill,
					mCircleAttribute.mBottomPaint);
		}
		double subRate = mSubCurProgress / mMaxProgress;
		float subSweep = (float)(360D * subRate);
		canvas.drawArc(mCircleAttribute.mRoundOval, (float)mCircleAttribute.mDrawPos,
				subSweep, mCircleAttribute.mBRoundPaintsFill,
				mCircleAttribute.mSubPaint);

		double rate = mMainCurProgress / mMaxProgress;
		float sweep = (float)(360D * rate);
		canvas.drawArc(mCircleAttribute.mRoundOval, (float)mCircleAttribute.mDrawPos,
				sweep, mCircleAttribute.mBRoundPaintsFill,
				mCircleAttribute.mMainPaints);
//		canvas.drawText(text, _x, _y, mCircleAttribute.mTextPaint);

	}
	
//	private String text="0%";
	private float _x=0;
	private float _y=0;
	
	/*
	 *  文字位置
	 */
	public void setMoveText(double radius){
		double r = radius-mCircleAttribute.mSidePaintInterval;
		double rate = (double) mMainCurProgress / mMaxProgress;
		double sweep = 360D * rate;
		if(sweep==0 || sweep>=315D){
			sweep = 45D;
			this._x = (float) (r*(1D-Math.cos(sweep*(Math.PI/180))));
			this._y = (float) (r+r*Math.sin(sweep*(Math.PI/180)));
		}else if(sweep<45&&sweep>0) {
			this._x = (float) (r*(1-Math.cos((45-sweep)*(Math.PI/180))));
			this._y = (float) (r*(1+Math.sin((45-sweep)*(Math.PI/180))));
		}else if (sweep==45D) {
			this._x = 0;
			this._y = (float) r;
		}else if (sweep<135D&&sweep>45D) {
			sweep-=45D;
			this._x = (float) (r*(1D-Math.cos(sweep*(Math.PI/180))));
			this._y = (float) (r*(1D-Math.sin(sweep*(Math.PI/180))));
		}else if(sweep ==135D){
			this._x = (float) r;
			this._y = 0;
		}else if (sweep<225D&&sweep>135D) {
			sweep-=135D;
			this._x =  (float) (r*(1D+Math.sin(sweep*(Math.PI/180))));
			this._y = (float) (r*(1D-Math.cos(sweep*(Math.PI/180))));
		}else if (sweep<=270D&&sweep>=225D) {
			sweep-=225D;
			this._x = (float) (r*(1D+Math.cos(sweep*(Math.PI/180))));
			this._y = (float) (r+r*Math.sin(sweep*(Math.PI/180)));
		}else if (sweep>270D&&sweep<315D) {
			sweep = 270D;
			this._x = (float) (r*(1D+Math.cos(sweep*(Math.PI/180))));
			this._y = (float) (r+r*Math.sin(sweep*(Math.PI/180)));
		}
	}
	
	/*
	 * 设置主进度值
	 */
	public synchronized void setMainProgress(double progress) {
		mMainCurProgress = progress;
//		int i = (int) (mMainCurProgress/0.75);
//		this.text = i+"%";
		if (mMainCurProgress < 0) {
			mMainCurProgress = 0;
		}

		if (mMainCurProgress > mMaxProgress) {
			mMainCurProgress = mMaxProgress;
		}

		invalidate();
	}
	
	/**
	 * 设置画笔颜色
	 * @param paintColor
	 */
	public synchronized void setPaintColor(int paintColor){
		mCircleAttribute.setPaintColor(paintColor);
		invalidate();
	}

	public synchronized double getMainProgress() {
		return mMainCurProgress;
	}
	
	public double getPaintInterval(){
		return mCircleAttribute.mSidePaintInterval;
	}

	/*
	 * 设置子进度值
	 */
	public synchronized void setSubProgress(double progress) {
		mSubCurProgress = progress;
		if (mSubCurProgress < 0) {
			mSubCurProgress = 0;
		}

		if (mSubCurProgress > mMaxProgress) {
			mSubCurProgress = mMaxProgress;
		}

		invalidate();
	}

	public synchronized double getSubProgress() {
		return mSubCurProgress;
	}

	/*
	 * 开启动画
	 */
	public void startCartoom(int time) {
		mCartoomEngine.startCartoom(time);

	}

	/*
	 * 结束动画
	 */
	public void stopCartoom() {
		mCartoomEngine.stopCartoom();
	}

	class CircleAttribute {
		public RectF mRoundOval; // 圆形所在矩形区域
		public boolean mBRoundPaintsFill; // 是否填充以填充模式绘制圆形
		public double mSidePaintInterval; // 圆形向里缩进的距离
		public double mPaintWidth; // 圆形画笔宽度（填充模式下无视）
		public int mPaintColor; // 画笔颜色 （即主进度条画笔颜色，子进度条画笔颜色为其半透明值）
		public double mDrawPos; // 绘制圆形的起点（默认为-90度即12点钟方向）

		public Paint mMainPaints; // 主进度条画笔
		public Paint mSubPaint; // 子进度条画笔
		public Paint mTextPaint; // 文字画笔

		public Paint mBottomPaint; // 无背景图时绘制所用画笔
		
		public CircleAttribute() {
			mRoundOval = new RectF();
			mBRoundPaintsFill = DEFAULT_FILL_MODE;
			mSidePaintInterval = DEFAULT_INSIDE_VALUE;
			mPaintWidth = 0;
			mPaintColor = DEFAULT_PAINT_COLOR;
			mDrawPos = mStartDrawPos;

			mMainPaints = new Paint();
			mMainPaints.setAntiAlias(true);
			mMainPaints.setStyle(Paint.Style.FILL);
			mMainPaints.setStrokeWidth((int)mPaintWidth);
			mMainPaints.setColor(mPaintColor);

			mSubPaint = new Paint();
			mSubPaint.setAntiAlias(true);
			mSubPaint.setStyle(Paint.Style.FILL);
			mSubPaint.setStrokeWidth((int)mPaintWidth);
			mSubPaint.setColor(mPaintColor);

			mBottomPaint = new Paint();
			mBottomPaint.setAntiAlias(true);
			mBottomPaint.setStyle(Paint.Style.FILL);
			mBottomPaint.setStrokeWidth((int)mPaintWidth);
			mBottomPaint.setColor(Color.TRANSPARENT);
			
			// 初始化，画笔
			mTextPaint = new Paint();
			mTextPaint.setAntiAlias(true);
			mTextPaint.setStrokeWidth((int)mPaintWidth);
			mTextPaint.setStyle(Paint.Style.FILL);
//			mTextPaint.setTextAlign(Align.LEFT);
			mTextPaint.setColor(Color.WHITE);
			
			
		}

		/*
		 * 设置画笔宽度
		 */
		public void setPaintWidth(int width) {
			mMainPaints.setStrokeWidth(width);
			mSubPaint.setStrokeWidth(width);
			mBottomPaint.setStrokeWidth(width);
		}

		/*
		 * 设置画笔颜色
		 */
		public void setPaintColor(int color) {
			mMainPaints.setColor(color);
			int color1 = color & 0x00ffffff | 0x66000000;
			mSubPaint.setColor(color1);
		}

		/*
		 * 设置填充模式
		 */
		public void setFill(boolean fill) {
			mBRoundPaintsFill = fill;
			if (fill) {
				mMainPaints.setStyle(Paint.Style.FILL);
				mSubPaint.setStyle(Paint.Style.FILL);
				mBottomPaint.setStyle(Paint.Style.FILL);
			} else {
				mMainPaints.setStyle(Paint.Style.STROKE);
				mSubPaint.setStyle(Paint.Style.STROKE);
				mBottomPaint.setStyle(Paint.Style.STROKE);
			}
		}

		/*
		 * 自动修正
		 */
		public void autoFix(int w, int h) {
			if (mSidePaintInterval != 0) {
				mRoundOval.set((float)mPaintWidth / 2 + (float)mSidePaintInterval,
						(float)mPaintWidth / 2 + (float)mSidePaintInterval, w - (float)mPaintWidth
								/ 2 - (float)mSidePaintInterval, h - (float)mPaintWidth / 2
								- (float)mSidePaintInterval);
			} else {

				int sl = getPaddingLeft();
				int sr = getPaddingRight();
				int st = getPaddingTop();
				int sb = getPaddingBottom();

				mRoundOval.set(sl + (float)mPaintWidth / 2, st + (float)mPaintWidth / 2, w
						- sr - (float)mPaintWidth / 2, h - sb - (float)mPaintWidth / 2);
			}
		}

	}

	class CartoomEngine {
		public Handler mHandler;
		public boolean mBCartoom; // 是否正在作动画
		public Timer mTimer; // 用于作动画的TIMER
		public MyTimerTask mTimerTask; // 动画任务
		public double mSaveMax; // 在作动画时会临时改变MAX值，该变量用于保存值以便恢复
		public int mTimerInterval; // 定时器触发间隔时间(ms)
		public float mCurFloatProcess; // 作动画时当前进度值

		// private long timeMil;

		public CartoomEngine() {
			mHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case TIMER_ID: {
						if (mBCartoom == false) {
							return;
						}

						mCurFloatProcess += 1;
						setMainProgress((int) mCurFloatProcess);

						// long curtimeMil = System.currentTimeMillis();

						// timeMil = curtimeMil;

						if (mCurFloatProcess >= mMaxProgress) {
							stopCartoom();
						}
					}
						break;
					}
				}

			};

			mBCartoom = false;
			mTimer = new Timer();
			mSaveMax = 0;
			mTimerInterval = 50;
			mCurFloatProcess = 0;

		}

		public synchronized void startCartoom(int time) {
			if (time <= 0 || mBCartoom == true) {
				return;
			}
			// timeMil = 0;
			mBCartoom = true;

			setMainProgress(0);
			setSubProgress(0);

			mSaveMax = mMaxProgress;
			mMaxProgress = (1000 / mTimerInterval) * time;
			mCurFloatProcess = 0;

			mTimerTask = new MyTimerTask();
			mTimer.schedule(mTimerTask, mTimerInterval, mTimerInterval);

		}

		public synchronized void stopCartoom() {

			if (mBCartoom == false) {
				return;
			}

			mBCartoom = false;
			mMaxProgress = mSaveMax;

			setMainProgress(0);
			setSubProgress(0);

			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null;
			}
		}

		private final static int TIMER_ID = 0x0010;

		class MyTimerTask extends TimerTask {

			@Override
			public void run() {
				Message msg = mHandler.obtainMessage(TIMER_ID);
				msg.sendToTarget();
			}
		}
	}

	public double getStartPos() {
		return mStartDrawPos;
	}
	
	/**
	 * @return 获取X坐标
	 */
	public double getXPoint(){
		return this._x;
	}
	/**
	 * @return 获取Y坐标
	 */
	public double getYPoint(){
		return this._y;
	}
}
