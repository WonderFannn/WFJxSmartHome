package com.jinxin.jxsmarthome.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * @purpose 
 * 		用于首页的浮动菜单
 * @description 
 * 		类似Path风格的弹出菜单, 可实现滑动定位到左下角和右下角
 * @version 1.0
 * @author TangLong
 * @date 2014-2-10
 * @update 2014-2-10
 */
public class ExtendAnimatePathMenu extends RelativeLayout {
	private long dragResponseMS = 1000; // item长按响应时间，默认为1s，可设置

	private enum LocateWhere {
		LEFT, RIGHT
	};

	private Context context;
	private int statusHeight;
	private int bottomMargin = 0;
	private int buttonWidth = 0;
	private int r = 340;
	private final int maxTimeSpent = 800;
	private final int minTimeSpent = 300;
	private int intervalTimeSpent;
	private DisplayMetrics dm;
	private int screenWidth;
	private int screenHeight;
	private int halfScreenWidth;
	private ImageButton[] btns;
	private ImageButton btnMain;
	private boolean isOpen;
	private boolean isDrag;
	private Vibrator vibrator;
	private float angle;

	int lastX = 0;
	int lastY = 0;
	private RelativeLayout.LayoutParams params;
	private RelativeLayout.LayoutParams originBtnMainParams;
	private RelativeLayout mWrapper;
	private LocateWhere locateWhere = LocateWhere.LEFT;

	private Handler mHandler = new Handler();

	private Runnable longClickRunnable = new Runnable() {
		@Override
		public void run() {
			if(isOpen) {
				btnMain.performClick();
			}
			isDrag = true;
			vibrator.vibrate(50);
		}
	};
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int touchX = (int) ev.getRawX();
			int touchY = (int) ev.getRawY();
			
			buttonWidth = btnMain.getWidth();
			
			int viewX = getViewLocationOnScreen(btnMain)[0] + buttonWidth / 2;
			int viewY = getViewLocationOnScreen(btnMain)[1] + buttonWidth / 2;

			// 碰撞检测
			if (!isViewBeTouched(viewX, viewY, touchX, touchY)) {
				return super.dispatchTouchEvent(ev);
			}

			// 开启判断长按/单击事件判断的线程
			mHandler.postDelayed(longClickRunnable, dragResponseMS);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			mHandler.removeCallbacks(longClickRunnable);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private OnButtonClickListener onButtonClickListener;

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int selectedItem = Integer.parseInt((String) v.getTag());

			btnMain.performClick();

			if (onButtonClickListener != null) {
				onButtonClickListener.onButtonClick(v, selectedItem);
			}
		}
	};

	public ExtendAnimatePathMenu(Context context) {
		this(context, null);
	}

	public ExtendAnimatePathMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ExtendAnimatePathMenu(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		this.isOpen = false;
		this.statusHeight = getStatusHeight((Activity)context);
		this.dm = getResources().getDisplayMetrics();
		this.screenWidth = dm.widthPixels;
		this.screenHeight = dm.heightPixels - statusHeight;
		this.halfScreenWidth = screenWidth / 2;
		this.vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 菜单位置记忆，实现的不好，其他方法不理想，暂时使用不同布局实现
		View view = null;
		String location = SharedDB.loadStrFromDB(StaticConstant.SP_NAME_HOME, StaticConstant.LOCATE_WHERE, "LEFT");
		if(!"LEFT".equals(location)) {
			view = LayoutInflater.from(context).inflate(R.layout.menu_buttons_right,
					this);
			locateWhere = LocateWhere.RIGHT;
		}else {
			view = LayoutInflater.from(context).inflate(R.layout.menu_buttons,
					this);
		}
		 
		initButtons(view);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		btnMain.setOnTouchListener(new OnTouchListener() {
			int originX = 0;
//			int originY = 0;

			boolean clickDisable = false;

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					originX = lastX;
//					originY = lastY;
					break;
				case MotionEvent.ACTION_MOVE:
					int movedX = (int) event.getRawX() - lastX;
					int movedY = (int) event.getRawY() - lastY;

					int left = v.getLeft() + movedX;
					int top = v.getTop() + movedY;
					int right = v.getRight() + movedX;
					int bottom = v.getBottom() + movedY;

					if (left < 0) {
						left = 0;
						right = left + v.getWidth();
					}

					if (right > screenWidth) {
						right = screenWidth;
						left = right - v.getWidth();
					}

					if (top < 0) {
						top = 0;
						bottom = top + v.getHeight();
					}

					if (bottom > screenHeight) {
						bottom = screenHeight;
						top = bottom - v.getHeight();
					}

					if (isDrag) {
						v.layout(left, top, right, bottom);
						for (int i = 0; i < btns.length; i++) {
							btns[i].layout(left, top, right, bottom);
						}
					}
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					if (isDrag) {
						int xOffset = Math.abs(lastX - originX);
//						int yOffset = Math.abs(lastY - originY);

						Logger.debug(null, "xOffset" + xOffset);
						Logger.debug(null, "halfScreenWidth" + halfScreenWidth);

						if ((locateWhere == LocateWhere.LEFT)
								&& xOffset < halfScreenWidth) {
							locateButtonsLeft();
							SharedDB.saveStrDB(StaticConstant.SP_NAME_HOME, StaticConstant.LOCATE_WHERE, "LEFT");
							// 如果按钮在x方向移动超过半个屏幕，则定位按钮于右下角
						} else if ((locateWhere == LocateWhere.LEFT)
								&& xOffset >= halfScreenWidth) {
							locateButtonsRight();
							SharedDB.saveStrDB(StaticConstant.SP_NAME_HOME, StaticConstant.LOCATE_WHERE, "RIGHT");
							// 如果按钮在x方向移动超过半个屏幕，则定位按钮于左下角
						} else if ((locateWhere == LocateWhere.RIGHT)
								&& xOffset < halfScreenWidth) {
							locateButtonsRight();
							SharedDB.saveStrDB(StaticConstant.SP_NAME_HOME, StaticConstant.LOCATE_WHERE, "RIGHT");
							// 如果按钮在x方向移动超过半个屏幕，则定位按钮于右下角
						} else if ((locateWhere == LocateWhere.RIGHT)
								&& xOffset >= halfScreenWidth) {
							locateButtonsLeft();
							SharedDB.saveStrDB(StaticConstant.SP_NAME_HOME, StaticConstant.LOCATE_WHERE, "LEFT");
						}
						clickDisable = true;
					} else {
						clickDisable = false;
					}
					isDrag = false;
					break;
				}

				return clickDisable;
			}
		});

		btnMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				r = screenWidth <= 500 ? 240 : 340;
				if (!isOpen) {
					isOpen = true;
					
					mWrapper.setOnTouchListener(new OnTouchListener() {
						@SuppressLint("ClickableViewAccessibility")
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_UP:
								btnMain.performClick();
								break;
							}
							return true;
						}
					});
					
					for (int i = 0; i < btns.length; i++) {
						float xLength = (float) (r * Math.sin(i * angle));
						float yLength = (float) (r * Math.cos(i * angle));

						int fromX = 0;
						int fromY = 0;
						int toX = locateWhere == LocateWhere.LEFT ? (int)xLength : (int)-xLength;
						int toY = (int)-yLength;
						
						btns[i].startAnimation(animTranslate(fromX, toX, fromY,
								toY, btns[i], minTimeSpent + i * intervalTimeSpent));
					}
					mWrapper.setBackgroundColor(0x55000000);
					btnMain.setBackgroundResource(R.drawable.clop);
				} else {
					isOpen = false;
					btnMain.setBackgroundResource(R.drawable.clop);
					mWrapper.setBackgroundColor(0x00000000);
					mWrapper.setOnTouchListener(new OnTouchListener() {
						@SuppressLint("ClickableViewAccessibility")
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							return false;
						}
					});
					for (int i = 0; i < btns.length; i++) {
						int fromX = 0;
						int fromY = 0;
						int toX = btnMain.getLeft() - btns[i].getLeft();
						int toY = btnMain.getTop() - btns[i].getTop();

						btns[i].startAnimation(animTranslate(fromX, toX, fromY,
								toY, btns[i], maxTimeSpent - i
										* intervalTimeSpent));
					}
				}
			}
		});
	}
	
	/**
	 * 判断按钮是否被触摸到，触摸点在按钮中心60像素内，认为触摸到了按钮
	 */
	private boolean isViewBeTouched(int viewX, int viewY, int touchX, int touchY) {
		boolean flag = false;

		if (Math.abs(viewX - touchX) < 60 && Math.abs(viewY - touchY) < 60) {
			flag = true;
		}

		return flag;
	}

	/**
	 * 初始化按钮
	 */
	private void initButtons(View view) {
		btns = new ImageButton[5];
		btns[0] = (ImageButton) view.findViewById(R.id.menu_btn_message);
		btns[1] = (ImageButton) view.findViewById(R.id.menu_btn_music);
		btns[2] = (ImageButton) view.findViewById(R.id.menu_btn_telecontrol_board);
		btns[3] = (ImageButton) view.findViewById(R.id.menu_btn_temperature);
		btns[4] = (ImageButton) view.findViewById(R.id.menu_btn_voice);
		btnMain = (ImageButton) view.findViewById(R.id.btn_menu);
		mWrapper = (RelativeLayout) view.findViewById(R.id.menu_btn_wapper);
		
		originBtnMainParams = (LayoutParams) btnMain.getLayoutParams();
		bottomMargin = ((RelativeLayout.LayoutParams) btnMain.getLayoutParams()).bottomMargin;

		btnMain.setBackgroundColor(0x00000000);
		for (int i = 0; i < btns.length; i++) {
			btns[i].setBackgroundColor(0x00000000);
			btns[i].setLayoutParams(btnMain.getLayoutParams());
			btns[i].setTag(String.valueOf(i));
			btns[i].setOnClickListener(clickListener);
		}

		intervalTimeSpent = (maxTimeSpent - minTimeSpent) / btns.length;
		angle = (float) Math.PI / (2 * (btns.length - 1));
		
		mWrapper.setBackgroundColor(0x00000000);
	}

	/**
	 * 定位按钮的位置为左下角
	 */
	public void locateButtonsLeft() {
		Logger.debug(null, "left");
		buttonWidth = btnMain.getWidth();
		RelativeLayout.LayoutParams params = originBtnMainParams;
		params.setMargins(0, 0, screenWidth - buttonWidth, bottomMargin);
		for (int i = 0; i < btns.length; i++) {
			btns[i].setLayoutParams(originBtnMainParams);
		}
		btnMain.setLayoutParams(originBtnMainParams);
		locateWhere = LocateWhere.LEFT;
	}

	/**
	 * 定位按钮的位置为右下角
	 */
	public void locateButtonsRight() {
		Logger.debug(null, "right");
		buttonWidth = btnMain.getWidth();
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		RelativeLayout.LayoutParams params = originBtnMainParams;
		params.setMargins(screenWidth - buttonWidth, 0, 0, bottomMargin);
		for (int i = 0; i < btns.length; i++) {
			btns[i].setLayoutParams(params);
		}
		btnMain.setLayoutParams(params);
		locateWhere = LocateWhere.RIGHT;
	}

	@SuppressWarnings("unused")
	private Animation animScale(float toX, float toY) {
		Animation animation = new ScaleAnimation(1.0f, toX, 1.0f, toY,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setInterpolator(context,
				android.R.anim.accelerate_decelerate_interpolator);
		animation.setDuration(200);
		animation.setFillAfter(false);
		return animation;

	}

	@SuppressWarnings("unused")
	private Animation animRotate(float toDegrees, float pivotXValue,
			float pivotYValue) {
		final Animation animation = new RotateAnimation(0, toDegrees,
				Animation.RELATIVE_TO_SELF, pivotXValue,
				Animation.RELATIVE_TO_SELF, pivotYValue);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				animation.setFillAfter(true);
			}
		});
		return animation;
	}

	/**
	 * 移动动画，用于实现弹出效果
	 * 
	 * @param fromX
	 *            开始位置
	 * @param toX
	 *            结束位置
	 * @param fromY
	 *            开始位置
	 * @param toY
	 *            结束位置
	 * @param button
	 *            执行动画的按钮
	 * @param durationMillis
	 *            动画执行时间
	 * @return
	 */
	private Animation animTranslate(final float fromX, final float toX, final float fromY,
			final float toY, final View button, long durationMillis) {
		Animation animation = new TranslateAnimation(fromX, toX, fromY, toY);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				buttonWidth = btnMain.getWidth();
				params = new RelativeLayout.LayoutParams(0, 0);
				params.height = buttonWidth;
				params.width = buttonWidth;
				int marginLeft1 = (int)(button.getLeft() + toX);
				int marginTop1 = (int)(button.getTop() + toY);
				params.setMargins(marginLeft1, marginTop1, 0, 0);
				button.setLayoutParams(params);
				button.clearAnimation();
			}
		});
		animation.setDuration(durationMillis);
		animation.setInterpolator(new AccelerateInterpolator(3.0f));
		return animation;
	}

	/**
	 * 关闭按钮
	 */
	public void closePathMenu() {
		btnMain.performClick();
	}
	
	/**
	 * 获取view在屏幕中的位置
	 */
	private int[] getViewLocationOnScreen(View view) {
		int[] location = new int[2];  
        view.getLocationOnScreen(location);  
        return location;
	}

	/**
	 * 得到状态栏的高度
	 */
	private int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;

		if (statusHeight == 0) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = activity.getResources()
						.getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return statusHeight;
	}

	/**
	 * 子菜单的点击事件的监听
	 */
	public interface OnButtonClickListener {
		public void onButtonClick(View view, int selectedItem);
	}

	public void setOnButtonClickListener(
			OnButtonClickListener onButtonClickListener) {
		this.onButtonClickListener = onButtonClickListener;
	}

}
