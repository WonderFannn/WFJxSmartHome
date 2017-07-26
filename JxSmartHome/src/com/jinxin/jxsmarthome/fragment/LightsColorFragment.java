package com.jinxin.jxsmarthome.fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.wheelview.ArrayWheelAdapter;
import com.jinxin.jxsmarthome.ui.widget.wheelview.OnWheelChangedListener;
import com.jinxin.jxsmarthome.ui.widget.wheelview.WheelView;
import com.jinxin.widget.progressBar.CircleProgress;
import com.jinxin.widget.progressBar.ProgressUtil;
import com.jinxin.widget.progressBar.RectColorView;
import com.jinxin.widget.progressBar.UniversalColorView;
import com.jinxin.widget.progressBar.UniversalColorView.OnActionUpListener;

/**
 * 灯光控制
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
@SuppressLint("HandlerLeak")
public class LightsColorFragment extends DialogFragment  implements OnClickListener {
	FrameLayout panel;
	private double startAngle;
	private CircleProgress mCircleProgressBar1;
	private UniversalColorView mCircleColorView;//条形颜色选择器
	private RectColorView mRectColorView;//圆形颜色选择器

	private double circleHeight, circleWidth;// 控件所在矩形宽高
	private double xWidth, yHeight;// 进度条控件宽高
	private double radius;
	private double _layoutWidth, _layoutHeight;// 屏幕宽高
	private int popWidth = 0, popHeight = 0;
	private int xOff, yOff;
	private String mProgress = "";// 已保存进度值
	private int mColor = 0;// 颜色值
	private int intA = 0;// 单个RGB值
	private int intR = 0;
	private int intG = 0;
	private int intB = 0;
	private String lColor = "";// 灯光颜色
	private int curColor;
	private double curProgress = 0;

	private ViewGroup popView;
	private TextView tvProgress;
	private PopupWindow pw;
	private Context context;
	private View view;
	private RelativeLayout rLayout;
	private ImageView lightOff;
	private ImageView lightOn;
	private RelativeLayout selectLayout = null;
	private RelativeLayout changeLayout = null;
	private Button cModeBtn = null;// 改变模式按钮
	private TextView cSelectBtn = null;
	private TextView cChangeBtn = null;
	private ImageView colorPanle = null;
	private WheelView wheelMode = null;
	private WheelView wheelDelay = null;

	private Boolean isLoading = true;

	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private ArrayList<ProductFun> _funList;
	private ArrayList<String> idList;
	
	private OnActionUpListener upListener;

	private static String[] MODES = new String[]{"00","01","02",
		"03","04","05","06","07",};
	private static String[] MODE_TIME = new String[]{"01","02",
		"03","04","05","06","07","08","09","10","11","12","13",
		"14","15","16","17","18","19","20","21","22","23","24",
		"25","26","27","28","29","30","31"};
	private String modeStr = "0001";

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				tvProgress.setText(msg.arg1 + "%");
			}
		}

	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
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

	public void initView(View view) {
		panel = (FrameLayout) view.findViewById(R.id.main_panel);
		mCircleProgressBar1 = (CircleProgress) view
				.findViewById(R.id.roundBar1);
		this.mCircleColorView = (UniversalColorView) view.findViewById(R.id.circle_color_view);
		this.mRectColorView = (RectColorView) view.findViewById(R.id.rect_color_view);
		this.rLayout = (RelativeLayout) view.findViewById(R.id.ib_light_power);
		this.lightOff = (ImageView) view.findViewById(R.id.color_light_off);
		this.lightOn = (ImageView) view.findViewById(R.id.color_light_on);
		this.selectLayout = (RelativeLayout) view
				.findViewById(R.id.color_select_layout);
		this.changeLayout = (RelativeLayout) view
				.findViewById(R.id.color_change_layout);
		this.cSelectBtn = (TextView) view.findViewById(R.id.color_select_btn);
		this.cChangeBtn = (TextView) view.findViewById(R.id.color_change_btn);
		this.cModeBtn = (Button) view
				.findViewById(R.id.color_mode_change_btn);
		
		/*************************彩灯模式变换设置***************************/
		this.wheelMode = (WheelView) view.findViewById(R.id.wheel_mode);
		this.wheelDelay = (WheelView) view.findViewById(R.id.wheel_delay_time);
		this.wheelMode.setCurrentItem(5);
		this.wheelDelay.setCurrentItem(5);
		wheelMode.setCurrentItem(3);
		wheelDelay.setCurrentItem(16);
		wheelMode.setCyclic(true);
		wheelDelay.setCyclic(true);
		wheelMode.setLabel("mode");
		wheelDelay.setLabel("s");
		wheelMode.setAdapter(new ArrayWheelAdapter<String>(MODES));
		wheelDelay.setAdapter(new ArrayWheelAdapter<String>(MODE_TIME));
		wheelMode.addChangingListener(changedListener);
		wheelDelay.addChangingListener(changedListener);
		/********************end*****************************************/
		
		rLayout.setOnClickListener(this);
		lightOff.setOnClickListener(this);
		lightOn.setOnClickListener(this);
		cSelectBtn.setOnClickListener(this);
		cChangeBtn.setOnClickListener(this);
		cModeBtn.setOnClickListener(this);
		
		/*************添加色盘 事件监听***************/
		this.mCircleColorView.addListener(mRectColorView.getListener());
		this.mRectColorView.addListener(mCircleColorView.getListener());
		
		this.mCircleColorView.addActionListener(setListener());
		this.mRectColorView.addActionListener(setListener());
		/*******************end***********************/
		
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
//					showPop();// 显示进度值窗口
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
//					showPop();
					break;
				case MotionEvent.ACTION_UP:
					curProgress = mCircleProgressBar1.getMainProgress();
					intA = (int) (curProgress * 255D / 75);
					lColor = ProgressUtil
							.setRgbToString(intA, intR, intG, intB);
					curColor = Color.argb(intA, intR, intG, intB);
					// rLayout.setBackgroundColor(curColor);
					mCircleProgressBar1.setPaintColor(curColor);
					if (_funList != null) {
						Stack<ProductFun> _ppoStack = new Stack<ProductFun>();
						for (int i = _funList.size() - 1; i >= 0; i--) {
							_ppoStack.push(_funList.get(i));
						}
						JxshApp.showLoading(
								context,
								CommDefines.getSkinManager().string(
										R.string.qing_qiu_chu_li_zhong));
						patternOperationSendToChangColor(_ppoStack);

					}
					break;
				}

				return true;
			}
		});
	}

	/**
	 * 初始化进度值弹出窗口位置
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
		if (pw.isShowing()){
			pw.update(x - popWidth / 2, y - popHeight,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}else{
			pw.showAtLocation(panel, Gravity.NO_GRAVITY, x - popWidth / 2, y
					- popHeight);
		}
	}
	
	

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (pw.isShowing()) {
			pw.dismiss();
		}
		isLoading = true;
	}

	/**
	 * 获取控件宽高
	 */
	private void initData() {
		_funList = new ArrayList<ProductFun>();
		idList = new ArrayList<String>();
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		idList = getArguments().getStringArrayList(
				ControlDefine.KEY_COLOR_LIGHT_LIST);
		if (idList.size() > 0 && idList != null) {
			ProductFunDaoImpl _pfdImpl = new ProductFunDaoImpl(context);
			ArrayList<ProductFun> funAllList = (ArrayList<ProductFun>) _pfdImpl
					.find();
			for (int i = 0; i < idList.size(); i++) {
				for (ProductFun productFun : funAllList) {
					if (idList.get(i).equals(productFun.getWhId())) {
						_funList.add(productFun);
					}
				}
			}
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
		if (_funList != null) {
			productFun = _funList.get(0);
			if (productFun.getBrightness() != 0) {
				mProgress = (productFun.getBrightness() / 255D * 75D) + "";
				// 初始进度
				if (mProgress.equals("")) {
					mCircleProgressBar1.setMainProgress(0D);
				} else {
					mCircleProgressBar1.setMainProgress(Double
							.parseDouble(mProgress));
				}
			}
			mColor = productFun.getIntColor();
			// 初始布局背景色
			if (mColor > 0) {
				mCircleProgressBar1.setPaintColor(mColor);
				rLayout.setBackgroundColor(mColor);
				mCircleColorView.setColor(mColor);
				mRectColorView.setColor(mColor);
				lColor = ProgressUtil.setRgbToString(Color.alpha(mColor),
						Color.red(mColor), Color.green(mColor),
						Color.blue(mColor));
			}
		}
	}

	private void initCircle() {
		if (mColor != 0) {
			intR = Color.red(mColor);
			intG = Color.green(mColor);
			intB = Color.blue(mColor);
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.color_select_btn:
			if (!selectLayout.isShown()) {
				cSelectBtn.setBackgroundDrawable(CommDefines.getSkinManager()
						.drawable(R.drawable.bottom_hover));
				cChangeBtn.setBackgroundDrawable(CommDefines.getSkinManager()
						.drawable(R.drawable.bottom_bg));
				selectLayout.setVisibility(View.VISIBLE);
				changeLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.color_change_btn:
			cSelectBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_bg));
			cChangeBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_hover));
			if (!changeLayout.isShown()) {
				changeLayout.setVisibility(View.VISIBLE);
				selectLayout.setVisibility(View.GONE);
			}
			break;

		case R.id.color_mode_change_btn:
			if (_funList != null && _funList.size() > 0) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(StaticConstant.PARAM_MODE, modeStr);
				List<byte[]> cmdList = CommonMethod.createColorModeCmd(context,
						_funList, funDetail,params);
				if (cmdList == null || cmdList.size() < 1) {
					return;
				}
				byte[] cmd = cmdList.get(0);
				CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(
						context, cmd, false);
				cdcbsTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
					}

					@Override
					public void onFail(ITask task, Object[] arg) {

					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						JxshApp.showToast(context, CommDefines.getSkinManager()
								.string(R.string.light_color_setting));
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
					}
				});
				// cdcbsTask.start();
				JxshApp.getDatanAgentHandler().post(cdcbsTask);
			}
			break;
		case R.id.color_light_off:
			break;
		case R.id.color_light_on:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isLoading = true;
	}

	// 改变灯光颜色请求
	/**
	 * 模式指令发送（递归发送）
	 * 
	 * @param ppos
	 */
	private void patternOperationSendToChangColor(Stack<ProductFun> pfs) {
//		if (pfs == null || pfs.empty()) {
//			JxshApp.closeLoading();
//			return;
//		}
		final Stack<ProductFun> _pfs = pfs;
		List<byte[]> cmdList = new ArrayList<byte[]>();
		ProductFun _pf = _pfs.pop();
		List<byte[]> tempList = CommonMethod.lightFunToCMD(context, _pf,funDetail, lColor);
		if (tempList != null && tempList.size() > 0) {
			cmdList.addAll(tempList);
		}
		if (!_pfs.isEmpty()) {
			patternOperationSendToChangColor(_pfs);
		}else{
			
//		if (_pf != null) {
//			List<byte[]> cmdList = CommonMethod.lightFunToCMD(context, _pf,funDetail, lColor);
			
			TaskListener<ITask> listener = new TaskListener<ITask>() {

				@Override
				public void onStarted(ITask task, Object arg) {
					Logger.debug(null, "yang test onStarted");
				}

				@Override
				public void onCanceled(ITask task, Object arg) {
				}

				@Override
				public void onFail(ITask task, Object[] arg) {
					JxshApp.closeLoading();
					Logger.debug(null, "yang test onFail");
				}

				@Override
				public void onSuccess(ITask task, Object[] arg) {
					JxshApp.closeLoading();
					Logger.debug(null, "yang test onSuccess");
//					JxshApp.showToast(context, CommDefines.getSkinManager()
//							.string(R.string.light_color_setting));
//					_pf.setBrightness((int) (curProgress * 255 / 75));
//					_pf.setIntColor(curColor);
//					_pf.setLightColor(lColor);
//					ProductFunDaoImpl _pfDaoImpl = new ProductFunDaoImpl(
//							getActivity());
//					_pfDaoImpl.update(_pf);
//					patternOperationSendToChangColor(_pfs);
				}

				@Override
				public void onProcess(ITask task, Object[] arg) {

				}
			};
			
			if (cmdList == null || cmdList.size() < 1) {
				return;
			}

			// ----------------------- offline ---------------------
			if(NetworkModeSwitcher.useOfflineMode(getActivity())) {
				OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(context, cmdList);
				offlineSender.addListener(listener);
				offlineSender.send();
				return;
			}
			// ------------------------- end -----------------------
			byte[] allCmd = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				for (int i = 0; i < cmdList.size(); i++) {
					bos.write(cmdList.get(i));
				}
				allCmd = bos.toByteArray();
				Logger.debug(null, "send:" + new String(allCmd));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//			byte[] cmd = cmdList.get(0);
			CommonDeviceControlByServerTask cdcbsTask = new CommonDeviceControlByServerTask(
					context, allCmd, false);

			if (pfs == null || pfs.empty()) {
				JxshApp.closeLoading();
				if (cdcbsTask != null) {
					cdcbsTask.setcloseSocketLongAfterTaskFinish(true);
					Logger.warn("tangl", "关闭连接");
				}
			}
			cdcbsTask.addListener(listener);
			// cdcbsTask.start();
			JxshApp.getDatanAgentHandler().post(cdcbsTask);
		}

	}
	
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
        	switch (wheel.getId()) {
			case R.id.wheel_mode:
			case R.id.wheel_delay_time:
				modeStr = MODES[wheelMode.getCurrentItem()]+ MODE_TIME[wheelDelay.getCurrentItem()];
				break;

			default:
				break;
			}
        }
    };
    
    /**
     * 设置颜色变化的监听 返回当前选择颜色值
     * @return
     */
    public OnActionUpListener setListener(){
    	if (upListener == null) {
			upListener = new OnActionUpListener() {
				
				@Override
				public void sendColor(int color) {
					mCircleProgressBar1.setPaintColor(color);
					mCircleProgressBar1.setMainProgress(0D);
//					showPop();
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
					if (_funList != null) {
						Stack<ProductFun> _ppoStack = new Stack<ProductFun>();
						for (int i = _funList.size() - 1; i >= 0; i--) {
							_ppoStack.push(_funList.get(i));
						}
						JxshApp.showLoading(
								context,
								CommDefines.getSkinManager().string(
										R.string.qing_qiu_chu_li_zhong));
						patternOperationSendToChangColor(_ppoStack);

					}
					
				}
			};
		}
    	return upListener;
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
