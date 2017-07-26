package com.jinxin.jxsmarthome.ui.widget.converflow;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * 图片展示
 * 
 * @author skywulei
 */
public class Converflow extends Gallery {

	/**
	 * Camera 用于对图片做旋转、缩放调整
	 */
	private Camera mCamera = new Camera();

	/**
	 * 图片旋转最大角度
	 */
	private int mMaxRotationAngle = 80;

	/**
	 * 图片最大缩放大小（负数是放大，正数为缩小）
	 */
	private int mMaxZoom = -100;
	/**
	 * 当前居中项的ID
	 */
	private int currentId = 0;
	
	private OnCurrentItemListener onCurrentItemListener = null;


	/**
	 * 用于判断图片旋转方像与缩放大小
	 */
	private int mCoveflowCenter;

	public Converflow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public Converflow(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Converflow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}
	/**
	 * 设置当前剧中项监听事件
	 * @param onCurrentItemListener
	 */
	public void setOnCurrentItemListener(OnCurrentItemListener onCurrentItemListener) {
		this.onCurrentItemListener = onCurrentItemListener;
	}
	/**
	 * This is called during layout when the size of this view has changed. If you were just added to the view hierarchy, you're called with
	 * the old values of 0.
	 * 
	 * @param w
	 *            Current width of this view.
	 * @param h
	 *            Current height of this view.
	 * @param oldw
	 *            Old width of this view.
	 * @param oldh
	 *            Old height of this view.
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}
	public int getCurrentId(){
		return this.currentId;
	}
	public void setCurrentId(int currentId) {
		this.currentId = currentId;
	}
	protected boolean getChildStaticTransformation(View child, Transformation t) {

		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;

		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (childCenter == mCoveflowCenter) {
			transformImageBitmap((ImageView) child, t, 0);
			setCurrentId(child.getId());
			if(this.onCurrentItemListener != null){
				this.onCurrentItemListener.currentItemEvent(child);
			}
		} else {
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * 80);
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
			}
			transformImageBitmap((ImageView) child, t, rotationAngle);
		}

		return true;
	}

	/**
	 * Transform the Image Bitmap by the Angle passed
	 * 
	 * @param imageView
	 *            ImageView the ImageView whose bitmap we want to rotate
	 * @param t
	 *            transformation
	 * @param rotationAngle
	 *            the Angle by which to rotate the Bitmap
	 */
	private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle) {
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
		final int rotation = Math.abs(rotationAngle);
		// As the angle of the view gets less, zoom in
		if (rotation < mMaxRotationAngle) {
			if (rotation > 0) {
				child.setAlpha(0xAF);
				mCamera.translate(0.0f, 0.0f, 100);
			} else {
				child.setAlpha(0xFF);
				mCamera.translate(0.0f, 0.0f, -50);
			}

		}

		// 在Y轴上旋转，对应图片竖向向里翻转。
		mCamera.rotateY(rotationAngle);
		// 如果在X轴上旋转，则对应图片横向向里翻转。
//		 mCamera.rotateX(Math.abs(rotationAngle));
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		mCamera.restore();
	}

	/**
	 * Get the Centre of the Coverflow
	 * 
	 * @return The centre of this Coverflow.
	 */
	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}
	/**
	 * 当前居中项监听
	 * @author JackeyZhang 2013-8-23 下午12:22:55
	 */
	public interface OnCurrentItemListener{
		public void currentItemEvent(View childView);
	}
}
