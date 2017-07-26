package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductConnectionVO;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.widget.progressBar.CircleProgress;
import com.jinxin.widget.progressBar.ProgressUtil;
import com.jinxin.widget.progressBar.RectColorView;
import com.jinxin.widget.progressBar.UniversalColorView;
import com.jinxin.widget.progressBar.UniversalColorView.OnActionUpListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 模式添加或修改 彩灯颜色设置界面
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
@SuppressLint({ "HandlerLeak", "NewApi" })
public class ConnectionLightsColorFragment extends Fragment implements
		OnClickListener {
	FrameLayout panel;
	private double startAngle;
	private CircleProgress mCircleProgressBar1;
	private UniversalColorView mCircleColorView;
	private RectColorView mRectColorView;

	private double circleHeight, circleWidth;// 控件所在矩形宽高
	private double xWidth, yHeight;// 进度条控件宽高
	private double radius;
	private double _layoutWidth, _layoutHeight;// 屏幕宽高
	private int popWidth = 0, popHeight = 0;
	private int xOff, yOff;
	private String mProgress = "";// 已保存进度值
	private int mColor = 0;// 初始布局背景Int颜色值
	private String strColor = null;// 初始布局背景String颜色值
	private int intA = 0, intR = 0, intG = 0, intB = 0;// 单个RGB值
	private String lColor = "";// 灯光颜色
	private int curColor;
	private double curProgress = 0;

	private ViewGroup popView;
	private TextView tvProgress;
	private PopupWindow pw;
	private Context context;
	private View view;
	private RelativeLayout rLayout;
	private LinearLayout footLayout;

	private Boolean isLoading = true;
	private Boolean isChanged = false;

	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private ArrayList<ProductFun> _funList = null;
	private ProductConnectionVO connectionVO = null;

	private OnActionUpListener upListener;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				tvProgress.setText(msg.arg1 + "%");
			}
		}

	};

	@SuppressLint("NewApi")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = CommDefines.getSkinManager().view(R.layout.brightness_layout,
				container);
		initView(view);
		initData();
		initCircle();
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.save_mode_btn);
		inflater.inflate(R.menu.menu_for_mode, menu);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle("配置灯光颜色");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btn_save:
			((BaseActionBarActivity)getActivity()).onBackPressed();
			break;
		default:
			break;
		}
		return true;
	}

	public void initView(View view) {
		panel = (FrameLayout) view.findViewById(R.id.main_panel);
		mCircleProgressBar1 = (CircleProgress) view
				.findViewById(R.id.roundBar1);
		this.mCircleColorView = (UniversalColorView) view
				.findViewById(R.id.circle_color_view);
		this.mRectColorView = (RectColorView) view
				.findViewById(R.id.rect_color_view);

		this.rLayout = (RelativeLayout) view.findViewById(R.id.ib_light_power);
		this.footLayout = (LinearLayout) view
				.findViewById(R.id.light_foot_layout);
		footLayout.setVisibility(View.GONE);
		rLayout.setOnClickListener(this);

		/************* 添加色盘 事件监听 ***************/
		this.mCircleColorView.addListener(mRectColorView.getListener());
		this.mRectColorView.addListener(mCircleColorView.getListener());

		this.mCircleColorView.addActionListener(setListener());
		this.mRectColorView.addActionListener(setListener());
		/******************* end ***********************/

		LayoutInflater mLayoutInflater = LayoutInflater.from(context);
		popView = (ViewGroup) mLayoutInflater.inflate(R.layout.pup_layout,
				null, true);
		tvProgress = (TextView) popView.findViewById(R.id.pup_text);
		pw = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, false);
		pw.setContentView(popView);
		pw.setOutsideTouchable(false); // 设置是否允许在外点击使其消失
		pw.setFocusable(false);
		pw.setTouchable(false);
		pw.setIgnoreCheekPress();
		panel.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isLoading) {
					initData();
					isLoading = false;
				}
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startAngle = 360D - getAngle(event.getX(), event.getY());
					mCircleProgressBar1.setMainProgress((ProgressUtil
							.getProgress(startAngle) * 100D / 360D));
					tvProgress.setText((int) ((ProgressUtil
							.getProgress(startAngle) * 100D / 360D) / 0.75)
							+ "%");
					showPop();// 显示进度值窗口
					break;
				case MotionEvent.ACTION_MOVE:
					double currentAngle = 360D - getAngle(event.getX(),
							event.getY());
					mCircleProgressBar1.setMainProgress((ProgressUtil
							.getProgress(currentAngle) * 100D / 360D));
					// 发送消息更新进度
					Message message = handler.obtainMessage();
					message.what = 0;
					message.arg1 = (int) ((ProgressUtil
							.getProgress(currentAngle) * 100D / 360D) / 0.75);
//					handler.sendMessage(message);
					message.sendToTarget();
					showPop();
					break;
				case MotionEvent.ACTION_UP:
					curProgress = mCircleProgressBar1.getMainProgress();
					intA = (int) (curProgress * 255D / 75);
					lColor = ProgressUtil
							.setRgbToString(intA, intR, intG, intB);
					curColor = Color.argb(intA, intR, intG, intB);
					// rLayout.setBackgroundColor(curColor);
					mCircleProgressBar1.setPaintColor(curColor);
					connectionVO.getProductConnection().setParaDesc(lColor);// 设置模式单个彩灯颜色
					System.out.println("lColor:" + lColor);
//					Bundle bundle = new Bundle();
					// bundle.putSerializable("productFunVO", _productFunVO);
					// BroadcastManager.sendBroadcast(BroadcastManager.ACTION_MODE_COLOR_SET_MESSAGE,
					// bundle);
					break;
				}

				return true;
			}
		});
	}

	/**
	 * 显示进度值窗口
	 */
	public void showPop() {
		mCircleProgressBar1.setMoveText(radius);// 初始化进度值位置
		if (popView != null && popView.getWidth() != 0) {
			popWidth = popView.getMeasuredWidth();
			popHeight = popView.getMeasuredHeight() * 4 / 5;
		}
		int x = (int) (mCircleProgressBar1.getXPoint()
				+ (_layoutWidth - xWidth) + mCircleProgressBar1
					.getPaintInterval()) + xOff;
		int y = (int) (mCircleProgressBar1.getYPoint()
				+ (_layoutHeight - yHeight)
				+ mCircleProgressBar1.getPaintInterval() + yOff);
		if (pw.isShowing())
			pw.update(x - popWidth / 2, y - popHeight,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		else
			pw.showAtLocation(panel, Gravity.NO_GRAVITY, x - popWidth / 2, y
					- popHeight);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (pw.isShowing()) {
			pw.dismiss();
		}
		isLoading = true;
		getActivity().getActionBar().show();
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_REFRESH_CONNECTION_UI, null);
	}

	/**
	 * 获取控件宽高
	 */
	private void initData() {
		_funList = new ArrayList<ProductFun>();
		connectionVO = (ProductConnectionVO) getArguments().getSerializable(
				"productConnectionVO");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		if (connectionVO != null) {
			productFun = connectionVO.getProductFun();
		}
		if (productFun != null) {
			_funList.add(productFun);
		}
		if (mCircleProgressBar1 != null) {
			xWidth = mCircleProgressBar1.getMeasuredWidth() / 2D;
			yHeight = mCircleProgressBar1.getMeasuredHeight() / 2D;
			radius = mCircleProgressBar1.getMeasuredWidth() / 2D;
		}

		if (panel != null) {
			circleWidth = panel.getMeasuredWidth();
			circleHeight = panel.getMeasuredHeight();
		}

		int[] location = new int[2];
		panel.getLocationInWindow(location);
		xOff = location[0];
		yOff = location[1];
		_layoutWidth = circleWidth / 2D;
		_layoutHeight = circleHeight / 2D;

		// 修改模式时才执行以下操作:初始彩灯颜色
		if (isChanged) {
			mColor = Color.rgb(intR, intG, intB);// 初始弹出对话框颜色
			// rLayout.setBackgroundColor(Color.rgb(intR, intG, intB));
			mCircleProgressBar1.setPaintColor(Color.rgb(intR, intG, intB));
		} else {
			if (connectionVO != null
					&& connectionVO.getProductConnection() != null) {
				strColor = connectionVO.getProductConnection().getParaDesc();
				if (!TextUtils.isEmpty(strColor)) {
					try {
						intR = stringToInt((String) strColor.subSequence(0, 3));
						intG = stringToInt((String) strColor.subSequence(3, 6));
						intB = stringToInt((String) strColor.subSequence(6, 9));
						intA = stringToInt((String) strColor.subSequence(9, 12));
						mColor = Color.rgb(intR, intG, intB);// 初始弹出对话框颜色
						// rLayout.setBackgroundColor(Color.rgb(intR, intG,
						// intB));
						mCircleProgressBar1.setPaintColor(Color.rgb(intR, intG,
								intB));
						mRectColorView.setColor(Color.rgb(intR, intG, intB));
						mCircleColorView.setColor(Color.rgb(intR, intG, intB));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				} else {
					mColor = Color.rgb(intR, intG, intB);// 初始弹出对话框颜色
					// rLayout.setBackgroundColor(Color.rgb(intR, intG, intB));
					mCircleProgressBar1.setPaintColor(Color.rgb(intR, intG,
							intB));
				}
				if (intA >= 0) {// 初始进度
					mProgress = (intA / 255D * 75D) + "";
					mCircleProgressBar1.setMainProgress(Double
							.parseDouble(mProgress));
				}

			}
		}

	}

	/**
	 * 初始颜色设置背景色
	 */
	private void initCircle() {
		if (mColor != 0) {
			intR = Color.red(mColor);
			intG = Color.green(mColor);
			intB = Color.blue(mColor);
		}
	}

	private int stringToInt(String str) {
		int i = 0;
		if (str.startsWith("00")) {
			i = Integer.parseInt(str.substring(2));
			return i;
		} else if (str.startsWith("0")) {
			i = Integer.parseInt(str.substring(1));
			return i;
		} else {
			i = Integer.parseInt(str);
		}
		return i;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		}
	}

	/**
	 * 设置颜色变化的监听 返回当前选择颜色值
	 * 
	 * @return
	 */
	public OnActionUpListener setListener() {
		if (upListener == null) {
			upListener = new OnActionUpListener() {

				@Override
				public void sendColor(int color) {
					mCircleProgressBar1.setPaintColor(color);
					mCircleProgressBar1.setMainProgress(0D);
					showPop();
					Message message = handler.obtainMessage();
					message.what = 0;
					message.arg1 = 0;
//					handler.sendMessage(message);
					message.sendToTarget();
					intA = 0;
					intR = Color.red(color);
					intG = Color.green(color);
					intB = Color.blue(color);
					lColor = ProgressUtil
							.setRgbToString(intA, intR, intG, intB);
					// 模式操作 发送广播进行彩灯颜色设置
					lColor = ProgressUtil
							.setRgbToString(intA, intR, intG, intB);
					connectionVO.getProductConnection().setParaDesc(lColor);
				}
			};
		}
		return upListener;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isLoading = true;
	}

	/**
	 * @return 以progress为中心的当前位置的角度
	 */
	private double getAngle(double xTouch, double yTouch) {
		double x = xTouch - (circleWidth / 2d);
		double y = circleHeight - yTouch - (circleHeight / 2d);

		switch (getQuadrant(x, y)) {
		case 1:
			return Math.asin(y / Math.hypot(x, y)) * 180D / Math.PI;

		case 2:
		case 3:
			return 180D - (Math.asin(y / Math.hypot(x, y)) * 180D / Math.PI);

		case 4:
			return 360D + Math.asin(y / Math.hypot(x, y)) * 180D / Math.PI;

		default:
			return 0;
		}
	}

	/**
	 * @return 返回象限
	 */
	private static int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}

}
