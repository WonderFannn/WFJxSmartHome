package com.jinxin.jxsmarthome.fragment;

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
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.wheelview.ArrayWheelAdapter;
import com.jinxin.jxsmarthome.ui.widget.wheelview.OnWheelChangedListener;
import com.jinxin.jxsmarthome.ui.widget.wheelview.WheelView;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.widget.progressBar.CircleProgress;
import com.jinxin.widget.progressBar.ProgressUtil;
import com.jinxin.widget.progressBar.RectColorView;
import com.jinxin.widget.progressBar.UniversalColorView;
import com.jinxin.widget.progressBar.UniversalColorView.OnActionUpListener;

/**
 * 灯带控制
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
@SuppressLint("HandlerLeak")
public class LightBeltFragment extends DialogFragment  implements OnClickListener, OnCheckedChangeListener, OnSeekBarChangeListener {
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
	private RelativeLayout selectLayout;
	private RelativeLayout changeLayout;
	private LinearLayout fadeLayout;
	private Button cModeBtn;// 改变模式按钮
	private TextView cSelectBtn;
	private TextView cChangeBtn;
	private TextView cFadeBtn;

	private Boolean isLoading = true;

	private ProductFun productFun;
	private FunDetail funDetail;
	private ArrayList<ProductFun> _funList;
	private ArrayList<String> idList;
	
	private OnActionUpListener upListener;

	private String type = "";
	
	private RadioGroup mRadioGroup;
	private int controlType = 0;	// 控制类型：0-彩灯，1-白灯
	private SeekBar mFadeLevel;
	private EditText mFadeTime;
	private int fadeValueNow;
	private Button mFadeBright;
	private Button mFadeDark;
	private Button mFadeStop;
	private RadioButton whiteRadio;
	
	private WheelView wheelMode = null;
	private WheelView wheelDelay = null;
	private int fadeMode = 1;
	private String fadeTime = "1";
	
	List<byte[]> cmdAll = new ArrayList<byte[]>();        
	
	private static String[] MODES = new String[]{"渐亮","渐暗","魔幻灯光"};
	private static String[] MODE_TIME = new String[]{"01","02",
		"03","04","05","06","07","08","09","10","11","12","13",
		"14","15","16","17","18","19","20","21","22","23","24",
		"25","26","27","28","29","30"};

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
		view = CommDefines.getSkinManager().view(R.layout.fragment_pop_light,
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
		this.fadeLayout = (LinearLayout)view.findViewById(R.id.color_fade_layout);
		this.cSelectBtn = (TextView) view.findViewById(R.id.color_select_btn);
		this.cChangeBtn = (TextView) view.findViewById(R.id.color_change_btn);
		this.cFadeBtn = (TextView) view.findViewById(R.id.color_fade_btn);
		this.mRadioGroup = (RadioGroup) view.findViewById(R.id.pop_light_choose);
		this.cModeBtn = (Button) view
				.findViewById(R.id.color_mode_change_btn);
		this.mFadeLevel = (SeekBar) view.findViewById(R.id.pop_light_seekbar);
//		this.mFadeTime = (EditText) view.findViewById(R.id.pop_light_time);
//		this.mFadeBright = (Button) view.findViewById(R.id.pop_light_fade_bright);
//		this.mFadeDark = (Button) view.findViewById(R.id.pop_light_fade_dark);
//		this.mFadeStop = (Button) view.findViewById(R.id.pop_light_cancel);
		
		//灯带没白灯设置，隐藏白灯选项
		this.whiteRadio = (RadioButton) view.findViewById(R.id.pop_light_white);
		this.whiteRadio.setVisibility(View.INVISIBLE);
		view.findViewById(R.id.pop_light_color).setVisibility(View.INVISIBLE);
		
		view.findViewById(R.id.lights_btn).setOnClickListener(this);
		
		/*************************彩灯模式变换设置***************************/
		this.wheelMode = (WheelView) view.findViewById(R.id.wheel_mode);
		this.wheelDelay = (WheelView) view.findViewById(R.id.wheel_delay_time);
		
		wheelMode.setCyclic(false);
		wheelDelay.setCyclic(true);
		wheelMode.setLabel("");
		wheelDelay.setLabel("S");
		wheelMode.setAdapter(new ArrayWheelAdapter<String>(MODES));
		wheelDelay.setAdapter(new ArrayWheelAdapter<String>(MODE_TIME));
		wheelMode.setCurrentItem(0);
		wheelDelay.setCurrentItem(1);
		wheelMode.addChangingListener(changedListener);
		wheelDelay.addChangingListener(changedListener);
		/********************end*****************************************/
		
		rLayout.setOnClickListener(this);
		lightOff.setOnClickListener(this);
		lightOn.setOnClickListener(this);
		cSelectBtn.setOnClickListener(this);
		cChangeBtn.setOnClickListener(this);
		cModeBtn.setOnClickListener(this);
		cFadeBtn.setOnClickListener(this);
//		mFadeLevel.setOnSeekBarChangeListener(this);
//		mFadeBright.setOnClickListener(this);
//		mFadeDark.setOnClickListener(this);
//		mFadeStop.setOnClickListener(this);
		
		/*************添加色盘 事件监听***************/
		this.mCircleColorView.addListener(mRectColorView.getListener());
		this.mRectColorView.addListener(mCircleColorView.getListener());
		
		this.mCircleColorView.addActionListener(setListener());
		this.mRectColorView.addActionListener(setListener());
		this.mRadioGroup.setOnCheckedChangeListener(this);
		/*******************end***********************/
		
		LayoutInflater mLayoutInflater = LayoutInflater.from(context);
		popView = (ViewGroup) mLayoutInflater.inflate(R.layout.pup_layout,
				new LinearLayout(getActivity()), false);
		tvProgress = (TextView) popView.findViewById(R.id.pup_text);
		pw = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, false);
		pw.setContentView(popView);
		pw.setOutsideTouchable(false); // 设置是否允许在外点击使其消失
		pw.setFocusable(false);
		pw.setTouchable(false);
		pw.setIgnoreCheekPress();
		
		panel.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
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
					type = "mvToLevel";
					curProgress = mCircleProgressBar1.getMainProgress();
					intA = (int) (curProgress * 255D / 75);
					curColor = Color.argb(intA, intR, intG, intB);
					// rLayout.setBackgroundColor(curColor);
					mCircleProgressBar1.setPaintColor(curColor);
					if (_funList != null) {
						Stack<ProductFun> _ppoStack = new Stack<ProductFun>();
						for (int i = _funList.size() - 1; i >= 0; i--) {
							_ppoStack.push(_funList.get(i));
						}
						Map<String, Object> params = new HashMap<String, Object>();
						/* 亮度 */
						params.put("light", StringUtils.integerToHexString(intA));
						/* 时间 */
						params.put("time",  StringUtils.integerToHexString(1));
						/* 亮度关*/
						params.put("lightColor", StringUtils.integerToHexString(0));
						/* 关的时间 */
						params.put("timeColor", StringUtils.integerToHexString(1));
						if(controlType == 0) {
							/* 设备关的目标（白灯）*/
							params.put("dstColor", "0x02");
						}else {
							/* 设备关的目标（彩灯）*/
							params.put("dstColor", "0x01");
						}
						sendCmd(type, params);
					}
					break;
				}

				return true;
			}
		});
	}

	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
        	switch (wheel.getId()) {
			case R.id.wheel_mode:
				fadeMode = wheelMode.getCurrentItem();
				break;
			case R.id.wheel_delay_time:
				fadeTime = MODE_TIME[wheelDelay.getCurrentItem()];
				break;

			default:
				break;
			}
        }
    };
	
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.color_select_btn:
		case R.id.color_change_btn:
		case R.id.color_fade_btn:
			setSelection(v.getId());
			break;
		case R.id.color_light_off:
			break;
		case R.id.color_light_on:
			break;
		case R.id.lights_btn:
			if (fadeMode == 2) { //魔幻灯光
				operateColorAutoMode();
			}else{ //颜色渐变切换
				operateColorFade(fadeMode, fadeTime);
			}
		break;
//		case R.id.pop_light_fade_bright:
//			if (JxshApp.instance.isClinkable) {
//				return;
//			}
//			operateColorFade(0);
//			break;
//		case R.id.pop_light_fade_dark:
//			operateColorFade(1);
//			break;
//		case R.id.pop_light_cancel:
//			operateColorFadeStop();
//			break;
		case R.id.color_mode_change_btn:
			operateColorAutoMode();
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isLoading = true;
	}

    /**
     * 设置颜色变化的监听 返回当前选择颜色值
     * @return
     */
    public OnActionUpListener setListener(){
    	if (upListener == null) {
			upListener = new OnActionUpListener() {
				@Override
				public void sendColor(int color) {
					type = "hueandsat";
					mCircleProgressBar1.setPaintColor(color);
					mCircleProgressBar1.setMainProgress(0D);
					Message message = handler.obtainMessage();
					message.what = 0;
					message.arg1 = 0;
//					handler.sendMessage(message);
					message.sendToTarget();
					intA = 0;
					intR = Color.red(color);
					intG = Color.green(color);
					intB = Color.blue(color);
					if (_funList != null) {
						Stack<ProductFun> _ppoStack = new Stack<ProductFun>();
						for (int i = _funList.size() - 1; i >= 0; i--) {
							_ppoStack.push(_funList.get(i));
						}
						Map<String, Object> params = new HashMap<String, Object>();
						Logger.debug(null, intR + "," + intG + "," + intB);
						float[] hueAndSat = StringUtils.rgb2hsb(intR, intG, intB);
						params.put("hue", StringUtils.integerToHexString(Math.round(hueAndSat[0])));
						params.put("sat", StringUtils.integerToHexString(Math.round(hueAndSat[1]*100)));
						params.put("light", StringUtils.integerToHexString(Math.round(hueAndSat[2]*100)));
						params.put("time",  StringUtils.integerToHexString(1));
						sendCmd(type, params);
					}
					
				}
			};
		}
    	return upListener;
    }
    
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		controlType = (checkedId == R.id.pop_light_white ? 1 : 0); 
		
		/** 发送关闭指令（如果切换为彩灯，关白灯，反之亦然） */
		/* 操作类型(关灯) */
		type = "mvToLevel";
		/* 操作参数  */
		Map<String, Object> params = new HashMap<String, Object>();
		/* 亮度 */
		params.put("light",  StringUtils.integerToHexString(100));
		/* 操作时间  */
		params.put("time",  StringUtils.integerToHexString(1));
		/* 设备目标地址  */
		params.put("src", "0x01");
		/* 亮度关*/
		params.put("lightColor", StringUtils.integerToHexString(0));
		/* 关的时间 */
		params.put("timeColor", StringUtils.integerToHexString(1));
		if(controlType == 0) {
			/* 设备关的目标（白灯）*/
			params.put("dstColor", "0x02");
		}else {
			/* 设备关的目标（彩灯）*/
			params.put("dstColor", "0x01");
		}
		sendCmd(type, params);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Logger.debug(null, "progress:" + progress);
		fadeValueNow = (int) (progress * 255/100.0);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	
	@SuppressWarnings("deprecation")
	private void setSelection(int id) {
		switch(id) {
		case R.id.color_select_btn:
			cSelectBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_hover));
			cChangeBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_bg));
			cFadeBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_bg));
			selectLayout.setVisibility(View.VISIBLE);
			changeLayout.setVisibility(View.GONE);
			fadeLayout.setVisibility(View.GONE);
			break;
		case R.id.color_change_btn:
			cSelectBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_bg));
			cChangeBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_hover));
			cFadeBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_bg));
			selectLayout.setVisibility(View.GONE);
			changeLayout.setVisibility(View.VISIBLE);
			fadeLayout.setVisibility(View.GONE);
			break;
		case R.id.color_fade_btn:
			cSelectBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_bg));
			cChangeBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_bg));
			cFadeBtn.setBackgroundDrawable(CommDefines.getSkinManager()
					.drawable(R.drawable.bottom_hover));
			selectLayout.setVisibility(View.GONE);
			changeLayout.setVisibility(View.GONE);
			fadeLayout.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	/**
	 * 颜色渐变
	 * @param mode
	 * @param time
	 */
	private void operateColorFade(int mode, String time) {
//		Editable timeEditor = mFadeTime.getText();
		String timeEditor = time;
		String timeText = "1";
		if(timeEditor != null) timeText = 
				TextUtils.isEmpty(timeEditor.toString()) ? "1" : timeEditor.toString();
		int fadeTime = Integer.parseInt(timeText);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", StringUtils.integerToHexString(mode));
		params.put("step", StringUtils.integerToHexString(fadeValueNow));
		params.put("time", StringUtils.integerToHexString(fadeTime * 10));
		sendCmd("step", params);
	}
	
	/**
	 * 魔幻灯光
	 */
	private void operateColorAutoMode() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hue", StringUtils.integerToHexString(0));
		params.put("sat", StringUtils.integerToHexString(254));
		params.put("light", StringUtils.integerToHexString(1));
		params.put("step", StringUtils.integerToHexString(45));
		params.put("time", StringUtils.integerToHexString(5));
		sendCmd("autoMode", params);
	}
	
	private void operateColorFadeStop() {
		sendCmd("stop", new HashMap<String, Object>());
	}
	
	private void sendCmd(String type, Map<String, Object> params) {
		if (_funList != null) {
			Stack<ProductFun> _ppoStack = new Stack<ProductFun>();
			for (int i = _funList.size() - 1; i >= 0; i--) {
				_ppoStack.push(_funList.get(i));
			}
			cmdAll.clear();
			patternOperationSendToChangColor(_ppoStack,type,params);
		};
		
    }
	
	/**
	 * 模式指令发送（递归发送）
	 * 
	 * @param ppos
	 */
	private void patternOperationSendToChangColor(Stack<ProductFun> pfs,final String type, final Map<String, Object> params) {//模式操作 批量发送
//		if (pfs == null || pfs.empty()) {
//			JxshApp.closeLoading();
//			return;
//		}
		
		if (JxshApp.instance.isClinkable) {
			JxshApp.showToast(getActivity(), CommDefines.getSkinManager().string(R.string.take_a_break));
			Logger.debug(null, "上次操作未完成，请稍后");
			return;
		}
		
		final Stack<ProductFun> _pfs = pfs;
		final ProductFun _pf = _pfs.pop();
		if (_pf != null) {
			
			params.put("src", "0x01");
			if(controlType == 0) params.put("dst", "0x01");
			else params.put("dst", "0x02");
			
			TaskListener<ITask> listener = new TaskListener<ITask>() {

				@Override
				public void onStarted(ITask task, Object arg) {
					JxshApp.showLoading(getActivity(), "发送指令...");
				}

				@Override
				public void onCanceled(ITask task, Object arg) {
				}

				@Override
				public void onFail(ITask task, Object[] arg) {
					JxshApp.closeLoading();
				}

				@Override
				public void onSuccess(ITask task, Object[] arg) {
//					patternOperationSendToChangColor(_pfs, type, params);
				}

				@Override
				public void onProcess(ITask task, Object[] arg) {

				}
			};
			
			if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
//				String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(getActivity(), _pf.getWhId());
				//通过MAC去匹配对应网关IP
				String zegbingWhId = AppUtil.getGetwayMACByProductWhId(getActivity(), _pf.getWhId());
				String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(getActivity(), zegbingWhId);
				OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
				List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), _pf, funDetail, params, type);
				cmdAll.addAll(cmdList);
				if (!pfs.isEmpty()) {
					patternOperationSendToChangColor(_pfs, type, params);
				}else{
					OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(getActivity(), 
							localHost + ":3333", cmdList, true, false);
					offlineSender.addListener(listener);
					offlineSender.send();
				}
			}else {
				OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
				
				List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), _pf, funDetail, params, type);
				cmdAll.addAll(cmdList);
				if (!pfs.isEmpty()) {
					patternOperationSendToChangColor(_pfs, type, params);
				}else{
					OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), DatanAgentConnectResource.HOST_ZEGBING, 
							cmdList, true);
					onlineSender.addListener(listener);
					onlineSender.send();
				}
			}
			
			//防止多次发送相同指令
			JxshApp.instance.isClinkable = true;

		}

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
