package com.jinxin.jxsmarthome.ui.widget.converflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;

/**
 * 
 * @author skywu
 * 
 */
public class ImageUtil {

	/**
	 * 改变图片大小与是否添加倒影
	 * 
	 * @param mContext
	 *            Comtext
	 * @param id
	 *            图片R.id
	 * @param imgWidth
	 *            将图片修改为此Width
	 * @param imgHeight
	 *            将图片修改为此Height
	 * @param isShadow
	 *            图片是否添加倒影
	 * @return Bitmap
	 */
	public static Bitmap changeImage(Context mContext, int id, int imgWidth, int imgHeight, boolean isShadow) {
		// 加载需要操作的图片
		Bitmap bitmapOrg = BitmapFactory.decodeResource(mContext.getResources(), id);
		return changeImage(mContext, bitmapOrg, imgWidth, imgHeight, isShadow);
	}

	/**
	 * 改变图片大小与是否添加倒影
	 * 
	 * @param mContext
	 *            Comtext
	 * @param bitmapOrg
	 *            图片Bitmap
	 * @param imgWidth
	 *            将图片修改为此Width
	 * @param imgHeight
	 *            将图片修改为此Height
	 * @param isShadow
	 *            图片是否添加倒影
	 * @return Bitmap
	 */
	public static Bitmap changeImage(Context mContext, Bitmap bitmapOrg, int imgWidth, int imgHeight, boolean isShadow) {
		// 获取这个图片的宽和高
		if(bitmapOrg == null)return null;
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) imgWidth) / width;
		float scaleHeight = ((float) imgHeight) / height;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);

		// 是否有倒影
		if (isShadow) {
			return addImageShadow(Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true));
		} else {
			return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
		}
	}

	/**
	 * 给图片添加倒影
	 * 
	 * @param bitmap
	 *            需要添加倒影的图片
	 * @return 添加好倒影的 Bitmap
	 */
	public static Bitmap addImageShadow(Bitmap bitmap) {
		Bitmap originalImage = bitmap;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		// Create a Bitmap with the flip matrix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, false);

		// Create a new bitmap with same width but taller to fit
		// reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// Draw in the gap
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height, deafaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height, null);

		// Create a shader that is a linear gradient that covers the
		// reflection
		Paint paint = new Paint();

		// 设置渐变
		LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight(), 0xFFffffff, 0x00ffff,
				TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight(), paint);
		return bitmapWithReflection;
	}

}
