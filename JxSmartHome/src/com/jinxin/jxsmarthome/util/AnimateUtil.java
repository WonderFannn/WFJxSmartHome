package com.jinxin.jxsmarthome.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.jinxin.jxsmarthome.R;

/**
 * 用于做一般的动画处理
 * @author Tanglong
 * @company 金鑫智慧
 * 
 * 使用示例：
 *  // AnimateUtil.addScaleAnimation(true, 3000, 200);
 *	AnimateUtil.addRotateAnimation(false, 5000, 100);
 *	AnimateUtil.setAttr(Animation.RESTART, Animation.INFINITE);
 *	AnimateUtil.run(view);
 */
public class AnimateUtil {
	private static AnimationSet animSet = new AnimationSet(true);
	
	/**
	 * 增加大小变化的动画
	 * @param isFill	第一次动画完成后是否停止
	 * @param duration	动画的持续时间
	 * @param offset	动画相对于动画集（AnimationSet）的开始时间
	 */
	public static void addScaleAnimation(boolean isFill, int duration, int offset) {
		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, 
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		scale.setFillAfter(isFill);
		scale.setDuration(duration);
		scale.setStartOffset(offset);
		animSet.addAnimation(scale);
	}
	
	/**
	 * 增加渐变动画
	 * @param isFill	第一次动画完成后是否停止
	 * @param duration	动画的持续时间
	 * @param offset	动画相对于动画集（AnimationSet）的开始时间
	 */
	public static void addAlphaAnimation(boolean isFill, int duration, int offset) {
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setFillAfter(isFill);
		alpha.setDuration(duration);
		alpha.setStartOffset(offset);
		animSet.addAnimation(alpha);
	}
	
	/**
	 * 增加旋转动画
	 * @param isFill	第一次动画完成后是否停止
	 * @param duration	动画的持续时间
	 * @param offset	动画相对于动画集（AnimationSet）的开始时间
	 */
	public static void addRotateAnimation(Context context,boolean isFill, int duration, int offset) {
//		RotateAnimation rotate = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		rotate.setInterpolator(new LinearInterpolator());//不停顿 
		Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_anim);
		LinearInterpolator lin = new LinearInterpolator();
		rotate.setInterpolator(lin);
		rotate.setFillAfter(isFill);
		animSet.addAnimation(rotate);
	}
	
	/**
	 * 设置动画集的属性
	 * @param repeatMode	是否重复,如：Animation.RESTART
	 * @param repeatCount	重复次数,如：Animation.INFINITE
	 */
	public static void setAttr(int repeatMode, int repeatCount) {
		animSet.setRepeatMode(repeatMode);
		animSet.setRepeatCount(repeatCount);
	}
	
	/**
	 * 运行动画
	 * @param view 需要运行动画的对象
	 */
	public static void run(View view) {
		view.startAnimation(animSet);
	}
	
	/**
	 * 对给定的viewGroup设置旋转动画
	 * @param viewGroup		运行动画的对象
	 * @param duration		动画运行时间
	 */
	public static void startRoateAnimationIn(ViewGroup viewGroup, int duration) {
		for(int i = 0; i < viewGroup.getChildCount(); i++) {
			viewGroup.getChildAt(i).setVisibility(View.VISIBLE);
			viewGroup.getChildAt(i).setFocusable(true);
			viewGroup.getChildAt(i).setClickable(true);
		}
		
		Animation animation =  new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 
				0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		animation.setFillAfter(true);
		animation.setDuration(duration);
		viewGroup.startAnimation(animation);
	}
	
	/**
	 * 对给定的viewGroup设置旋转动画
	 * @param viewGroup		运行动画的对象
	 * @param duration		动画运行时间
	 * @param startOffset	延迟启动动画时间
	 */
	public static void startRoateAnimationOut(final ViewGroup viewGroup, int duration , int startOffset) {
		Animation animation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 1.0f);
		animation.setFillAfter(true);
		animation.setDuration(duration);
		animation.setStartOffset(startOffset);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				for(int i = 0; i < viewGroup.getChildCount(); i++ ){
					viewGroup.getChildAt(i).setVisibility(View.GONE);//设置显示
					viewGroup.getChildAt(i).setFocusable(false);//获得焦点
					viewGroup.getChildAt(i).setClickable(false);//可以点击
				}
			}
		});
		viewGroup.startAnimation(animation);
	}
}
