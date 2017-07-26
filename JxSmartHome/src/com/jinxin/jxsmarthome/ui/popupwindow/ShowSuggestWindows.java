package com.jinxin.jxsmarthome.ui.popupwindow;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.record.SharedDB;

/**
 * 展示引导
 */
public class ShowSuggestWindows extends View {
	private static LinearLayout lineOverLay;
	private static WindowManager windowManager;
	private Context context;
	private String filed;
	private boolean isFrist;
	private int res;
	private ImageView imgOverLay;
	
	public ShowSuggestWindows(Context paramContext, int paramInt1,
			int paramInt2, int paramInt3, int paramInt4) {
		super(paramContext);
		showPopupWindow(paramContext, paramInt1, paramInt2, paramInt3,
				paramInt4);
	}

	public ShowSuggestWindows(Context paramContext, int paramInt,
			String paramString) {
		super(paramContext);
		this.isFrist = true;// this.mSharedPreferences.getBoolean(paramString,
							// true);
		this.context = paramContext;
		this.res = paramInt;
		this.filed = paramString;
	}

	@SuppressLint("InflateParams")
	private void showPopupWindow(Context paramContext, int resouce,
			int paramInt2, int paramInt3, int paramInt4) {
		View localView = ((LayoutInflater) paramContext
				.getSystemService("layout_inflater")).inflate(
				R.layout.guide_popup_layout, null);
		localView.setBackgroundResource(resouce);
		final PopupWindow localPopupWindow = new PopupWindow(localView, -1, -2,
				true);
		localPopupWindow.showAtLocation(localView, paramInt2, paramInt3,
				paramInt4);
		localPopupWindow.setOutsideTouchable(true);
		localPopupWindow.update();
		localPopupWindow.setTouchable(true);
		localPopupWindow.setFocusable(true);
		localView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				localPopupWindow.dismiss();
			}
		});
	}

	/**
	 * 从Assets中读取图片
	 * 
	 * @param fileName
	 * @return
	 */
	private Bitmap getAssetsBitmap(String fileName) {
		Bitmap image = null;
		AssetManager am = this.context.getResources().getAssets();
		try {
			InputStream is = am.open("help/" + fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}

	/**
	 * 当前应用程序是否是第一次使用
	 * 
	 * @param key
	 * @return
	 */
	private boolean isFirst(String key) {
		return SharedDB.loadBooleanFromDB(SharedDB.ORDINARY_CONSTANTS, key, true);
	}

	public void showFullWindows(final String key, int resouce) {
		if (isFirst(key)) {
			windowManager = (WindowManager) JxshApp.instance
					.getSystemService("window");
			if (imgOverLay == null) {
				imgOverLay = new ImageView(context);
			}
			imgOverLay.setClickable(true);
			imgOverLay.setFocusable(true);
			imgOverLay.setFocusableInTouchMode(true);
			imgOverLay.setScaleType(ScaleType.FIT_XY);
			WindowManager.LayoutParams localLayoutParams1 = new WindowManager.LayoutParams(-1, -1);
			// localLayoutParams1.width =
			// WindowManager.LayoutParams.MATCH_PARENT;
			// localLayoutParams1.height =
			// WindowManager.LayoutParams.MATCH_PARENT;
			imgOverLay.setLayoutParams(localLayoutParams1);
			// lineOverLay.setBackgroundResource(this.res);
			// imgOverLay.setImageBitmap(getAssetsBitmap(fileName));
			imgOverLay.setImageResource(resouce);
			WindowManager.LayoutParams localLayoutParams2 = new WindowManager.LayoutParams(-1, -1, 2002, 8, -3);
			windowManager.addView(imgOverLay, localLayoutParams2);
			imgOverLay.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {
					try {
						ShowSuggestWindows.windowManager.removeView(imgOverLay);
						imgOverLay = null;
						//模式界面操作提示二
						if (key.equalsIgnoreCase("SenceFragment")) {
							showFullWindows("SenceFragment2", R.drawable.bg_guide_mode2);
						}
//						//设备界面操作提示二
//						if (key.equalsIgnoreCase("DeviceFragment")) {
//							showFullWindows("DeviceFragment2", R.drawable.bg_guide_device2);
//						}
						//模式添加界面操作提示二
						if (key.equalsIgnoreCase("AddNewModeActivity")) {
							showFullWindows("AddNewModeActivity2", R.drawable.bg_guide_add_mode2);
						}
						
						return;
					} catch (Exception localException) {
						localException.printStackTrace();
					}
				}
			});
			SharedDB.saveBooleanDB(SharedDB.ORDINARY_CONSTANTS, key,
					false);
		}
	}

	public void showFullWindows() {
		if (this.isFrist) {
			windowManager = (WindowManager) JxshApp.instance
					.getSystemService("window");
			lineOverLay = new LinearLayout(this.context);
			WindowManager.LayoutParams localLayoutParams1 = new WindowManager.LayoutParams(
					-1, -1);
			lineOverLay.setLayoutParams(localLayoutParams1);
			lineOverLay.setBackgroundResource(this.res);
			WindowManager.LayoutParams localLayoutParams2 = new WindowManager.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_FULLSCREEN, -3);
			windowManager.addView(lineOverLay, localLayoutParams2);
			lineOverLay.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {
					try {
						ShowSuggestWindows.windowManager
								.removeView(ShowSuggestWindows.lineOverLay);
						return;
					} catch (Exception localException) {
						localException.printStackTrace();
					}
				}
			});
		}
	}
	
	public void removeView(){
		if (imgOverLay != null) {
			ShowSuggestWindows.windowManager.removeView(imgOverLay);
			imgOverLay = null;
		}
	}
	

}