package com.jinxin.jxsmarthome.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.MainMenuItem;

/**
 * 自定义横滚效果
 * 
 * @author zj 
 * 调用方法：this.gallery.setPadding(CommUtil.dip2px(this, 27), 0, CommUtil.dip2px(this, 27), 0);
 *         this.gallery.setTextPadding(CommUtil.dip2px(this, 27), 0, CommUtil.dip2px(this, 27), 0);  
 *         this.gallery.setAllowSelected(true);
 *         this.gallery.setView(this.businesslists); 
 *         this.gallery.checkScrollArrowView( this.businesslists.size(), 4);
 */
public class MyGalleryLayout extends LinearLayout {
	private View view = null;
	private Context mContext = null;
	private LayoutInflater inflator = null;
	private LinearLayout linearLayoutList = null;
	private HorizontalScrollView horizontalScrollView = null;
	private List<MainMenuItem> menuList = null;
	private boolean isAllowSelected = false;// 是否可以选中

	public MyGalleryLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflator.inflate(R.layout.my_gallery_layout, null);
		this.addView(view);
		this.init(context);
	}

	public MyGalleryLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            Context
	 */
	private void init(Context context) {
		this.mContext = context;
		this.horizontalScrollView = (HorizontalScrollView) this.findViewById(R.id.horizontalScrollView1);
		this.linearLayoutList = (LinearLayout) this.view.findViewById(R.id.linearLayoutList);
	}

	/**
	 * 设置界面
	 * 
	 * @param list
	 *            ArrayList<MainMenuItem>
	 */
	public void setView(List<MainMenuItem> list) {
		if (list == null || list.size() <= 0)
			return;
		this.menuList = list;
		linearLayoutList.removeAllViews();
		MainMenuItem _item = null;
		for (int i = 0; i < list.size(); i++) {
			_item = list.get(i);
			View _view = this.inflator.inflate(R.layout.main_menu_item_layout,null);
			RelativeLayout _relativeLayout = (RelativeLayout) _view.findViewById(R.id.relativeLayout1);
			_relativeLayout.setGravity(Gravity.CENTER_VERTICAL);
			_relativeLayout.setPadding(this.left, this.top, this.right, this.bottom);
			Button _button = (Button) _view.findViewById(R.id.button1);
			_button.setPadding(textLeft, textTop, textRight, textBottom);
			if (_item != null) {
				if(_item.getIconId() != -1){
					_button.setBackgroundResource(_item.getIconId());
				}
				_button.setText(_item.getText());
				if (isAllowSelected())
					_button.setSelected(_item.isSelected());
				_button.setOnClickListener(_item.getOnClickListener());
				linearLayoutList.addView(_view);
			}

		}
		this.horizontalScrollView.setHorizontalFadingEdgeEnabled(false);
	}

	/**
	 * 监测并显示滚动箭头状态
	 * 
	 * @param total
	 *            滚动总数量
	 * @param maxNum
	 *            滚动箭头不介入最大值
	 */
	public void checkScrollArrowView(int total, int maxNum) {
		final Button buttonLeft = (Button) this.findViewById(R.id.buttonLeft);
		final Button buttonRight = (Button) this.findViewById(R.id.buttonRight);
		// horizontalScrollView.measure(0, 0);
		// linearLayoutList.measure(0, 0);
		if (total <= maxNum) {
			buttonLeft.setVisibility(View.INVISIBLE);
			buttonRight.setVisibility(View.INVISIBLE);
			return;
		}
		if (horizontalScrollView.getWidth() < linearLayoutList.getMeasuredWidth()) {
			// Logger.error("--->", horizontalScrollView.getMeasuredWidth()+"|"+linearLayoutList.getMeasuredWidth());
			buttonLeft.setVisibility(View.VISIBLE);
			buttonRight.setVisibility(View.VISIBLE);
		}

		// 水平滑动栏箭头的出现与消失
		horizontalScrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
				// 如果触动屏幕就执行
				case MotionEvent.ACTION_MOVE:
					View view = (horizontalScrollView).getChildAt(0);
					// 判断是否滑动栏到底了，如果是，就让iv这个图片隐藏，否则显示
					if (view.getMeasuredWidth() <= v.getScrollX() + v.getWidth() + 2) {
						buttonRight.setVisibility(View.INVISIBLE);
					} else {
						buttonRight.setVisibility(View.VISIBLE);
					}
					if (v.getScrollX() <= 0) {
						buttonLeft.setVisibility(View.INVISIBLE);
					} else {
						buttonLeft.setVisibility(View.VISIBLE);
					}
					break;
				default:
					break;

				}
				return false;

			}
		});
	}

	/**
	 * 是否允许选中
	 * 
	 * @return
	 */
	public boolean isAllowSelected() {
		return isAllowSelected;
	}

	/**
	 * 设定是否选中(必须在setView（）之前调用)
	 * 
	 * @param isAllowSelected
	 */
	public void setAllowSelected(boolean isAllowSelected) {
		this.isAllowSelected = isAllowSelected;
	}
	/****** 文字间距设置 ***************************/
	private int textLeft = 0;
	private int textTop = 0;
	private int textRight = 0;
	private int textBottom = 0;

	/**
	 * 设置Button文字边距(必须在setView（）之前执行才起作用)
	 */
	public void setTextPadding(int left, int top, int right, int bottom) {
		this.textLeft = left;
		this.textTop = top;
		this.textRight = right;
		this.textBottom = bottom;
	}

	/*************************************/
	/****** 间距设置 ***************************/
	private int left = 0;
	private int top = 0;
	private int right = 0;
	private int bottom = 0;

	/**
	 * 设置gallery间距(必须在setView（）之前执行才起作用)
	 */
	public void setPadding(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	/*************************************/
	/**
	 * 清除选中状态（需在主线程中执行）
	 */
	public void clearAllSelected() {
		if (menuList == null)
			return;
		for (int i = 0; i < menuList.size(); i++) {
			MainMenuItem _item = menuList.get(i);
			if (_item != null) {
				_item.setSelected(false);
			}
			if (linearLayoutList != null) {
				View _view = linearLayoutList.getChildAt(i);
				if (_view != null) {
					Button _button = (Button) _view.findViewById(R.id.button1);
					if (_button != null) {
						_button.setSelected(false);
					}
				}
			}
		}
	}

	/**
	 * 获取当前选中项的位置索引(未找到为-1)
	 * 
	 * @return
	 */
	private int getCurrentSelectedItemIndex() {
		if (menuList == null)
			return -1;
		for (int i = 0; i < menuList.size(); i++) {
			MainMenuItem _item = menuList.get(i);
			if (_item != null) {
				if (_item.isSelected()) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * MyGallery选项事件
	 * 
	 * @author JackeyZhang 2013-6-24 下午2:12:57
	 * @copyright 大道信通
	 */
	public abstract class MyGalleryItemOnClickListener implements OnClickListener {
		private MainMenuItem item = null;

		public MyGalleryItemOnClickListener(MainMenuItem item) {
			this.item = item;
		}

		/**
		 * 点击更新选中（允许选中时）
		 */
		private void updateSelected() {
			if (isAllowSelected()) {
				if (menuList != null) {
					// 清除选中
					clearAllSelected();
					// 设置选中
					if (item != null) {
						item.setSelected(true);
						View _view = linearLayoutList.getChildAt(getCurrentSelectedItemIndex());
						if (_view != null) {
							Button _button = (Button) _view.findViewById(R.id.button1);
							if (_button != null) {
								_button.setSelected(true);
							}
						}
					}
				}
			}
		}

		/**
		 * 执行内容
		 */
		public abstract void doEvent();

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			updateSelected();
			doEvent();
		}
	}

}
