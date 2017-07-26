package com.jinxin.jxsmarthome.password.pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

public class NinePointLineView extends View {

	private final static int ON_UP = 1;
	private final static int LOCK_SCREEN_STATUS = 3;
	private String firstLockKey;
	private int mInputErrorCnt = 0;
	private int status = 0;
	private String mCurrentPassword;

	public StringBuffer getLockString() {
		return lockString;
	}

	public void setLockString(StringBuffer lockString) {
		this.lockString = lockString;
	}

	private Handler mHander = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ON_UP://设置密码

				if (lockString.length() < 4) {
					mNineLockListener.showMessage(context.getString(R.string.nine_string_one));
					finishDraw();
					return;
				}

				if (isFirstTime) {
					NinePointLineView.this.firstLockKey = lockString.toString();
					mNineLockListener.showMessage(context.getString(R.string.nine_string_two));
					isFirstTime = false;
				} else {
					if (NinePointLineView.this.firstLockKey.equals(lockString.toString())) {
						mNineLockListener.showMessage(context.getString(R.string.nine_string_three));
						String _account = CommUtil.getCurrentLoginAccount();
						SharedDB.saveStrDB(_account, ControlDefine.KEY_PASSWORD_PATTERN, NinePointLineView.this.firstLockKey);
				    	SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_PATTERN, true);
						mNineLockListener.finish();
					} else {
						mNineLockListener.showMessage(context.getString(R.string.nine_string_four));
						mInputErrorCnt++;
					}
				}

				if (mInputErrorCnt >= 5) {
					mNineLockListener.showMessage(context.getString(R.string.nine_string_eight));
					// 注销....
					mNineLockListener.finish();
				}

				finishDraw();

				break;
			case 2://解锁
				if (lockString.length() < 4) {
					mNineLockListener.showMessage(context.getString(R.string.nine_string_one));
					finishDraw();
					return;
				}

				if (mCurrentPassword.equals(lockString.toString())) {
					mNineLockListener.showMessage(context.getString(R.string.finger_closed));
					mNineLockListener.finish();

				} else {
					mInputErrorCnt++;

					if (mInputErrorCnt >= 5) {
						mNineLockListener.showMessage(context.getString(R.string.nine_string_five));

						BroadcastManager.sendBroadcast(BroadcastManager.ACTION_EXIT_MESSAGE_RELOGIN, null);
						mNineLockListener.finish();
						return;
					}

					mNineLockListener.showMessage(context.getString(R.string.nine_string_six) + (5 - mInputErrorCnt)
							+ context.getString(R.string.nine_string_seven));
				}
				finishDraw();
				break;

			case LOCK_SCREEN_STATUS:

				if (mCurrentPassword.equals(lockString.toString())) {
					mNineLockListener.finish();

					Intent intent = new Intent(context, PatternActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				} else {
					mInputErrorCnt++;

					if (mInputErrorCnt >= 5) {
						mNineLockListener.showMessage(context.getString(R.string.nine_string_five));
						mNineLockListener.finish();
						return;
					}

					mNineLockListener.showMessage(context.getString(R.string.nine_string_six) + (5 - mInputErrorCnt)
							+ context.getString(R.string.nine_string_seven));
				}
				break;
			default:
				break;
			}
		};

	};

	private Context context;

	NineLockListener mNineLockListener;

	Paint linePaint = new Paint();

	Paint whiteLinePaint = new Paint();

	Paint textPaint = new Paint();

	// 由于两个图片都是正方形，所以获取一个长度就行了
	Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lock);
	int defaultBitmapRadius = defaultBitmap.getWidth() / 2;

	// 初始化被选中图片的直径、半径
	Bitmap selectedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lock_area);
	int selectedBitmapDiameter = selectedBitmap.getWidth();
	int selectedBitmapRadius = selectedBitmapDiameter / 2;

	// 定义好9个点的数组
	PointInfo[] points = new PointInfo[9];

	// 相应ACTION_DOWN的那个点
	PointInfo startPoint = null;

	// 屏幕的宽高
	int screen_width, screen_height;

	// 当ACTION_MOVE时获取的X，Y坐标
	int moveX, moveY;

	// 是否发生ACTION_UP
	boolean isUp = false;

	// 最终生成的用户锁序列
	StringBuffer lockString = new StringBuffer();

	private boolean isFirstTime = true;

	public NinePointLineView(Context context) {
		super(context);
		// this.setBackgroundColor(Color.WHITE);
		initPaint(context);
	}

	public NinePointLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// this.setBackgroundColor(Color.WHITE);
		initPaint(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// JLogger.info("onMeasure");
		// 初始化屏幕大小
		// screen_width = getWidth();
		// screen_height = getHeight();
		// if (screen_width != 0 && screen_height != 0) {
		// initPoints(points);
		// }

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setWindows(int width, int height, NineLockListener mNineLockListener, int status) {
		this.mNineLockListener = mNineLockListener;
		this.status = status;

		// 初始化屏幕大小
		screen_width = width;
		screen_height = height;
		if (screen_width != 0 && screen_height != 0) {
			initPoints(points);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	private int startX = 0, startY = 0;

	@Override
	protected void onDraw(Canvas canvas) {

		// canvas.drawText("用户的滑动顺序：" + lockString, 0, 40, textPaint); remark by
		// hujunyu

		if (moveX != 0 && moveY != 0 && startX != 0 && startY != 0) {
			// 绘制当前活动的线段
			drawLine(canvas, startX, startY, moveX, moveY);
		}

		drawNinePoint(canvas);

		super.onDraw(canvas);
	}

	// 记住，这个DOWN和MOVE、UP是成对的，如果没从UP释放，就不会再获得DOWN；
	// 而获得DOWN时，一定要确认消费该事件，否则MOVE和UP不会被这个View的onTouchEvent接收
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean flag = true;

		if (isUp) {// 如果已滑完，重置每个点的属性和lockString

			finishDraw();

			// 当UP后，要返回false，把事件释放给系统，否则无法获得Down事件
			flag = false;

		} else {// 没滑完，则继续绘制

			handlingEvent(event);

			// 这里要返回true，代表该View消耗此事件，否则不会收到MOVE和UP事件
			flag = true;

		}
		return flag;
	}

	private void handlingEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			moveX = (int) event.getX();
			moveY = (int) event.getY();
			for (PointInfo temp : points) {
				if (temp.isInMyPlace(moveX, moveY) && temp.isNotSelected()) {
					temp.setSelected(true);
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					int len = lockString.length();
					if (len != 0) {
						int preId = lockString.charAt(len - 1) - 48;
						points[preId].setNextId(temp.getId());
					}
					lockString.append(temp.getId());
					break;
				}
			}

			// invalidate(0, screen_height - screen_width, screen_width,
			// screen_height);
			// invalidate(0, 0, screen_width, screen_height);
			invalidate();
			break;

		case MotionEvent.ACTION_DOWN:
			int downX = (int) event.getX();
			int downY = (int) event.getY();
			for (PointInfo temp : points) {
				if (temp.isInMyPlace(downX, downY)) {
					temp.setSelected(true);
					startPoint = temp;
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					lockString.append(temp.getId());
					break;
				}
			}
			// invalidate(0, 0, screen_width, screen_height);
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			startX = startY = moveX = moveY = 0;
			isUp = true;
			invalidate();

			if (status == 0) {
				mHander.sendEmptyMessage(ON_UP);//设置
			} else if (status == 1) {
				mHander.sendEmptyMessage(2);//解锁
			} else if (status == 3) {
				mHander.sendEmptyMessage(LOCK_SCREEN_STATUS);
			}
			break;
		default:
			break;
		}
	}

	private void finishDraw() {
		for (PointInfo temp : points) {
			temp.setSelected(false);
			temp.setNextId(temp.getId());
		}
		lockString.delete(0, lockString.length());
		isUp = false;
		invalidate();
	}

	private void initPoints(PointInfo[] points) {

		int len = points.length;

		int seletedSpacing = (screen_width - selectedBitmapDiameter * 3) / 4;

		// 被选择时显示图片的左上角坐标
		int seletedX = seletedSpacing;
		// int seletedY = screen_height - screen_width + seletedSpacing;
		int seletedY = seletedX;

		// 没被选时图片的左上角坐标
		int defaultX = seletedX + selectedBitmapRadius - defaultBitmapRadius;
		int defaultY = seletedY + selectedBitmapRadius - defaultBitmapRadius;

		// 绘制好每个点
		for (int i = 0; i < len; i++) {
			if (i == 3 || i == 6) {
				seletedX = seletedSpacing;
				seletedY += selectedBitmapDiameter + seletedSpacing;

				defaultX = seletedX + selectedBitmapRadius - defaultBitmapRadius;
				defaultY += selectedBitmapDiameter + seletedSpacing;

			}
			points[i] = new PointInfo(i, defaultX, defaultY, seletedX, seletedY);

			seletedX += selectedBitmapDiameter + seletedSpacing;
			defaultX += selectedBitmapDiameter + seletedSpacing;
		}
	}

	private void initPaint(Context context) {
		init(context);
		initLinePaint(linePaint);
		initTextPaint(textPaint);
		initWhiteLinePaint(whiteLinePaint);
	}

	private void init(Context context) {
		this.context = context;
		String _account = CommUtil.getCurrentLoginAccount();
		this.mCurrentPassword = SharedDB.loadStrFromDB(_account, ControlDefine.KEY_PASSWORD_PATTERN, "");
		System.out.println("@current password:" + this.mCurrentPassword);
	}

	/**
	 * 初始化文本画笔
	 * 
	 * @param paint
	 */
	private void initTextPaint(Paint paint) {
		textPaint.setTextSize(30);
		textPaint.setAntiAlias(true);
		textPaint.setTypeface(Typeface.MONOSPACE);
	}

	/**
	 * 初始化黑线画笔
	 * 
	 * @param paint
	 */
	private void initLinePaint(Paint paint) {
		paint.setColor(-7829368);
		paint.setStrokeWidth(this.defaultBitmap.getWidth() / 4);
		paint.setAntiAlias(true);
		paint.setStrokeCap(Cap.ROUND);
	}

	/**
	 * 初始化白线画笔
	 * 
	 * @param paint
	 */
	private void initWhiteLinePaint(Paint paint) {
		paint.setColor(-256);
		// paint.setStrokeWidth(defaultBitmap.getWidth() - 5);
		paint.setStrokeWidth(defaultBitmap.getWidth() / 4);
		paint.setAntiAlias(true);
		paint.setStrokeCap(Cap.ROUND);

	}

	/**
	 * 绘制已完成的部分
	 * 
	 * @param canvas
	 */
	private void drawNinePoint(Canvas canvas) {

		if (startPoint != null) {
			drawEachLine(canvas, startPoint);
		}

		// 绘制每个点的图片
		for (PointInfo pointInfo : points) {
			if (pointInfo.isSelected()) {// 绘制大圈
				canvas.drawBitmap(selectedBitmap, pointInfo.getSeletedX(), pointInfo.getSeletedY(), null);
			}
			// 绘制点
			canvas.drawBitmap(defaultBitmap, pointInfo.getDefaultX(), pointInfo.getDefaultY(), null);
		}

	}

	/**
	 * 递归绘制每两个点之间的线段
	 * 
	 * @param canvas
	 * @param point
	 */
	private void drawEachLine(Canvas canvas, PointInfo point) {
		if (point.hasNextId()) {
			int n = point.getNextId();
			drawLine(canvas, point.getCenterX(), point.getCenterY(), points[n].getCenterX(), points[n].getCenterY());
			// 递归
			drawEachLine(canvas, points[n]);
		}
	}

	/**
	 * 先绘制黑线，再在上面绘制白线，达到黑边白线的效果
	 * 
	 * @param canvas
	 * @param startX
	 * @param startY
	 * @param stopX
	 * @param stopY
	 */
	private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {
		canvas.drawLine(startX, startY, stopX, stopY, linePaint);
		canvas.drawLine(startX, startY, stopX, stopY, whiteLinePaint);
	}

	/**
	 * 用来表示一个点
	 * 
	 * @author zkwlx
	 * 
	 */
	private class PointInfo {

		// 一个点的ID
		private int id;

		// 当前点所指向的下一个点的ID，当没有时为自己ID
		private int nextId;

		// 是否被选中
		private boolean selected;

		// 默认时图片的左上角X坐标
		private int defaultX;

		// 默认时图片的左上角Y坐标
		private int defaultY;

		// 被选中时图片的左上角X坐标
		private int seletedX;

		// 被选中时图片的左上角Y坐标
		private int seletedY;

		public PointInfo(int id, int defaultX, int defaultY, int seletedX, int seletedY) {
			this.id = id;
			this.nextId = id;
			this.defaultX = defaultX;
			this.defaultY = defaultY;
			this.seletedX = seletedX;
			this.seletedY = seletedY;
		}

		public boolean isSelected() {
			return selected;
		}

		public boolean isNotSelected() {
			return !isSelected();
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public int getId() {
			return id;
		}

		public int getDefaultX() {
			return defaultX;
		}

		public int getDefaultY() {
			return defaultY;
		}

		public int getSeletedX() {
			return seletedX;
		}

		public int getSeletedY() {
			return seletedY;
		}

		public int getCenterX() {
			return seletedX + selectedBitmapRadius;
		}

		public int getCenterY() {
			return seletedY + selectedBitmapRadius;
		}

		public boolean hasNextId() {
			return nextId != id;
		}

		public int getNextId() {
			return nextId;
		}

		public void setNextId(int nextId) {
			this.nextId = nextId;
		}

		/**
		 * 坐标(x,y)是否在当前点的范围内
		 * 
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean isInMyPlace(int x, int y) {
			boolean inX = x > seletedX && x < (seletedX + selectedBitmapDiameter);
			boolean inY = y > seletedY && y < (seletedY + selectedBitmapDiameter);

			return (inX && inY);
		}

	}

}
