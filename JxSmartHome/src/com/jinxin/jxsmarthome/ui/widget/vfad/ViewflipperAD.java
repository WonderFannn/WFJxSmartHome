package com.jinxin.jxsmarthome.ui.widget.vfad;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.FileUtil;

public class ViewflipperAD extends LinearLayout {
	private View view = null;
	private Context context = null;
	private LayoutInflater inflator = null;

	private ViewFlipper viewflipper_ad = null;
	private LinearLayout linearlayout_index = null;

	private GestureDetector detector = null;
	private OnGestureListener gesturelistener = null;

	private static final int AUTO_FLIPPER = 4;
	private static final int UPDATA_FLIPPER = 2;
	private static final int INIT_FLIPPER = 1;
	private static final long DELAY_TIME = 5000;
	private boolean is_auto = false;// 是否允许自动轮播

	public ViewflipperAD(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflator.inflate(R.layout.viewflipper_ad_layout, null);
		this.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		this.initData(context);
		this.initView();
	}

	public ViewflipperAD(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private void initData(Context context) {
		this.context = context;
	}

	private void initView() {
		this.viewflipper_ad = (ViewFlipper) findViewById(R.id.viewflipper_ad);
		this.linearlayout_index = (LinearLayout) findViewById(R.id.linearlayout_index);
		handler.sendEmptyMessage(INIT_FLIPPER);
		handler.sendEmptyMessage(UPDATA_FLIPPER);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case 0:// 执行动画
			// slideAnimation.start();
			// break;
			case INIT_FLIPPER: // 初始化flipper
				initViewFlipper();
				break;
			case UPDATA_FLIPPER: // 更新flipper
				if (viewflipper_ad != null) {
					for (int i = 0; i < viewflipper_ad.getChildCount(); i++) {
						ImageView _imageView = (ImageView) viewflipper_ad
								.getChildAt(i);
						if (_imageView != null) {
							Advertising _advertising = ADManager.instance()
									.getAdvertisingList().get(i);
							if (_advertising != null) {
								String _path = _advertising.getPath();
								if (!TextUtils.isEmpty(_path)) {
									Bitmap _bitmap = FileUtil.getImage(_path);
									/****** zj:目前没有广告,使用assets中的测试图片 ******/
									_bitmap = CommDefines.getSkinManager()
											.getAssetsBitmap(_path);
									/****************************/
									if (_bitmap != null) {
										if (linearlayout_index.getVisibility() == View.INVISIBLE) {
											linearlayout_index
													.setVisibility(View.VISIBLE);
										}
										// _imageView.setImageBitmap(_bitmap);
										_imageView
												.setBackgroundDrawable(new BitmapDrawable(
														_bitmap));
									}
								}
							}
						}
					}
					viewflipper_ad.invalidate();
				}
				break;
			case AUTO_FLIPPER: // 自动flipper
//				Logger.debug(null, "ViewflipperAD:"+viewflipper_ad.getChildCount());
				if(viewflipper_ad.getChildCount() < 1){
					/***zj:广告假数据测试********/
					List<Advertising> adList = new ArrayList<Advertising>();
					Advertising _ad = new Advertising();
					_ad.setPath("ad/ad/pic1.png");
					adList.add(_ad);
					_ad = new Advertising();
					_ad.setPath("ad/ad/pic2.png");
					adList.add(_ad);
					_ad = new Advertising();
					_ad.setPath("ad/ad/pic3.png");
					adList.add(_ad);
					ADManager.instance().setAdvertisingList(adList);
					/***************************/
					initViewFlipper();
					is_auto = false;
					startAutoLoop();
					Logger.debug(null, "end");
					return;
				}
				viewflipper_ad.setInAnimation(AnimationUtils.loadAnimation(
						context, R.anim.left_in));
				viewflipper_ad.setOutAnimation(AnimationUtils.loadAnimation(
						context, R.anim.left_out));
				viewflipper_ad.showNext();
				checkDot();
				if (is_auto) {// 可以循环调用自己，注意避免死循环
					Message message = handler.obtainMessage(AUTO_FLIPPER);
					handler.sendMessageDelayed(message, DELAY_TIME);
				}
				break;
			default:
				break;
			}
		}
	};
	/**
	 * 启动自动轮播
	 * @see 警告：调用startAutoLoop()之后，必须一对一调用用stopAutoLoop()适时停止，否则有可能造成内存溢出
	 */
public void startAutoLoop(){
	if(!is_auto){
		Logger.debug(null, "ViewflipperAD startAutoLoop");
		// 轮播启动
		this.is_auto = true;
		Message msg = handler.obtainMessage(AUTO_FLIPPER);
		handler.sendMessageDelayed(msg, DELAY_TIME);
	}
}
/**
 * 关闭自动轮播
 */
public void stopAutoLoop(){
	this.is_auto = false;
}
	/**
	 * 初始化viewFlipper
	 */
	private void initViewFlipper() {
		this.viewflipper_ad.removeAllViews();
		this.linearlayout_index.removeAllViews();
		if (ADManager.instance().getAdvertisingList() == null
				|| ADManager.instance().getAdvertisingList().size() <= 0) {
			return;
		}
		
		for (int i = 0; i < ADManager.instance().getAdvertisingList().size(); i++) {
			Advertising _advertising = ADManager.instance()
					.getAdvertisingList().get(i);
			ImageView _imageView = new ImageView(this.context);
			this.viewflipper_ad.addView(_imageView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			// 载入图片
//			 this.loadADPicture(_advertising, _imageView);
			View _view = this.inflator.inflate(R.layout.ad_dot, null);
			linearlayout_index.addView(_view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		handler.sendEmptyMessage(UPDATA_FLIPPER);
		// 允许显示
		this.viewflipper_ad.setVisibility(View.VISIBLE);
		this.linearlayout_index.setVisibility(View.VISIBLE);
		// 更新dot位置
		this.checkDot();
		// 载入图片
		// 添加拖拽事件
		if (ADManager.instance().getAdvertisingList().size() > 1) {
			gesturelistener = new OnViewFlipperGestureListener();
			detector = new GestureDetector(gesturelistener);
			viewflipper_ad.setClickable(true);
			viewflipper_ad.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return detector.onTouchEvent(event);
				}
			});
//			// 轮播启动
//			this.is_auto = true;
//			Message msg = handler.obtainMessage(AUTO_FLIPPER);
//			handler.sendMessageDelayed(msg, DELAY_TIME);
		}
	}

	/**
	 * 监测dot当前位置
	 */
	private void checkDot() {
		int index = viewflipper_ad.getDisplayedChild();
		int count = linearlayout_index.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = linearlayout_index.getChildAt(i);
			view.setSelected(i == index);
		}

	}

	// /**
	// * 加载图片
	// * @param advertising 广告图片对象
	// * @param imageView 需要更新的图片对象
	// */
	// private void loadADPicture(Advertising advertising,ImageView imageView){
	// if(advertising == null)return;
	// if(!CommUtil.isNull(advertising.getPath())){
	// //加载本地图片
	// Bitmap _bitmap = FileUtil.getImage(advertising.getPath());
	// if(_bitmap != null && imageView != null){
	// imageView.setImageBitmap(_bitmap);
	// handler.sendEmptyMessage(UPDATA_FLIPPER);
	// }
	// }else{
	// //下载图片
	// if(!advertising.isLoading()){
	// advertising.setLoading(true);
	// InternetTask _task = new LoadADTask(null, advertising, null);
	// _task.addListener(new ITaskListener() {
	// @Override
	// public void onStarted(ITask task, Object arg) {
	// // TODO Auto-generated method stub
	// Logger.e("-->", "开始下载广告图。");
	// }
	//
	// @Override
	// public void onCanceled(ITask task, Object arg) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onFail(ITask task, Object[] arg) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onSuccess(ITask task, Object[] arg) {
	// // TODO Auto-generated method stub
	// Logger.e("-->", "下载一张广告图完成");
	// if(arg != null && arg.length > 0){
	// handler.sendEmptyMessage(UPDATA_FLIPPER);
	// }
	// }
	// });
	// LockScreenApp.getDatanAgentHandler().post(_task);
	// }
	// }
	// }

	class OnViewFlipperGestureListener implements OnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > 120) {
				viewflipper_ad.setInAnimation(AnimationUtils.loadAnimation(
						context, R.anim.left_in));
				viewflipper_ad.setOutAnimation(AnimationUtils.loadAnimation(
						context, R.anim.left_out));
				viewflipper_ad.showNext();
				checkDot();
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				viewflipper_ad.setInAnimation(AnimationUtils.loadAnimation(
						context, R.anim.right_in));
				viewflipper_ad.setOutAnimation(AnimationUtils.loadAnimation(
						context, R.anim.right_out));
				viewflipper_ad.showPrevious();
				checkDot();
				return true;
			}
			return false;
		}
	}
}
