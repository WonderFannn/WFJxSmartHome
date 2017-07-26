package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductConnectionVO;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.widget.progressBar.CircleProgress;
import com.jinxin.widget.progressBar.ProgressUtil;
import com.jinxin.widget.progressBar.RectColorView;
import com.jinxin.widget.progressBar.UniversalColorView;
import com.jinxin.widget.progressBar.UniversalColorView.OnActionUpListener;

/**
 * 模式设置球泡灯，水晶灯颜色
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
@SuppressLint({ "HandlerLeak", "NewApi" })
public class ConnectionPopLightFragment extends DialogFragment  implements OnClickListener, OnSeekBarChangeListener {
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
	private static String OPERATING_AUTOMODE = "automode";
	private static String OPERATION_HUEANDSAT = "hueandsat";
	
	private int controlType = 0;	// 控制类型：0-彩灯，1-白灯
	private SeekBar mFadeLevel;
	private EditText mFadeTime;
	private int fadeValueNow;
	private Button mFadeBright;
	private Button mFadeDark;
	private Button mFadeStop;
	private CheckBox coolLight;//炫彩灯光
	private TextView tvName;
	
	private String strColor = null;// 初始布局背景String颜色值
	private Boolean isChanged = false;
	private ProductConnectionVO connnectionVO = null;

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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = CommDefines.getSkinManager().view(R.layout.fragment_pop_light_for_mode,
				container);
		initView(view);
		initData();
		initCircle();
		return view;
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
		this.cModeBtn = (Button) view
				.findViewById(R.id.color_mode_change_btn);
		this.mFadeLevel = (SeekBar) view.findViewById(R.id.pop_light_seekbar);
		this.tvName = (TextView) view.findViewById(R.id.tv_light_name);
		this.coolLight = (CheckBox) view.findViewById(R.id.pop_light_white);
		
//		this.mFadeTime = (EditText) view.findViewById(R.id.pop_light_time);
//		this.mFadeBright = (Button) view.findViewById(R.id.pop_light_fade_bright);
//		this.mFadeDark = (Button) view.findViewById(R.id.pop_light_fade_dark);
//		this.mFadeStop = (Button) view.findViewById(R.id.pop_light_cancel);
		
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
		coolLight.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					connnectionVO.getProductConnection().setOperation(OPERATING_AUTOMODE);
					connnectionVO.getProductConnection().setParaDesc(StringUtils.rgbToHsvJsonForAutoMode());//炫彩灯光参数设置
					JxshApp.showToast(getActivity(), "已设置炫彩灯光模式,灯光颜色将自动变化");
				}else{//重置彩灯颜色
					connnectionVO.getProductConnection().setOperation(OPERATION_HUEANDSAT);
					connnectionVO.getProductConnection().setParaDesc(StringUtils.rgbToHsvJson(intR, intG, intB));
					JxshApp.showToast(getActivity(), "已关闭炫彩灯光模式,请重新选择灯光颜色");
				}
			}

		});
		
		/*************添加色盘 事件监听***************/
		this.mCircleColorView.addListener(mRectColorView.getListener());
		this.mRectColorView.addListener(mCircleColorView.getListener());
		
		this.mCircleColorView.addActionListener(setListener());
		this.mRectColorView.addActionListener(setListener());
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
					String _color = StringUtils.rgbToHsvJson(intR, intG, intB);
					connnectionVO.getProductConnection().setParaDesc(_color);
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
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_REFRESH_CONNECTION_UI, null);
	}

	/**
	 * 获取控件宽高
	 */
	private void initData() {
		_funList = new ArrayList<ProductFun>();
		connnectionVO = (ProductConnectionVO) getArguments().getSerializable(
				"productConnectionVO");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		if (connnectionVO != null) {
			productFun = connnectionVO.getProductFun();
		}
		if (productFun != null) {
			_funList.add(productFun);
			tvName.setText(productFun.getFunName());
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
			if (connnectionVO != null
					&& connnectionVO.getProductConnection() != null) {
			    String operation = connnectionVO.getProductConnection().getOperation(); 
			    strColor = connnectionVO.getProductConnection().getParaDesc();
				if ("autoMode".equals(operation) || "automode".equals(operation)) {
					coolLight.setChecked(true);
				}else{
					if (!TextUtils.isEmpty(strColor)) {
						try {
							JSONObject jb = new JSONObject(strColor);
							String _color = jb.getString("color");
							intR = stringToInt((String) _color.subSequence(0, 3));
							intG = stringToInt((String) _color.subSequence(3, 6));
							intB = stringToInt((String) _color.subSequence(6, 9));
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
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						mColor = Color.rgb(intR, intG, intB);// 初始弹出对话框颜色
						// rLayout.setBackgroundColor(Color.rgb(intR, intG, intB));
						mCircleProgressBar1.setPaintColor(Color.rgb(intR, intG,
								intB));
					}
				}
				if (intA >= 0) {// 初始进度
					mProgress = (intA / 255D * 75D) + "";
					mCircleProgressBar1.setMainProgress(Double
							.parseDouble(mProgress));
				}

			}
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
//		case R.id.pop_light_fade_bright:
//			operateColorFade(0);
//			break;
//		case R.id.pop_light_fade_dark:
//			operateColorFade(1);
//			break;
//		case R.id.pop_light_cancel:
//			operateColorFadeStop();
//			break;
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
					coolLight.setChecked(false);//关闭炫彩灯光
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
					Logger.debug(null, intR + "," + intG + "," + intB);
					String _color = StringUtils.rgbToHsvJson(intR, intG, intB);
					connnectionVO.getProductConnection().setParaDesc(_color);
					
				}
			};
		}
    	return upListener;
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
	
	private void operateColorFade(int mode) {
		Editable timeEditor = mFadeTime.getText();
		String timeText = "1";
		if(timeEditor != null) timeText = TextUtils.isEmpty(timeEditor.toString()) ? "1" : timeEditor.toString();
		int fadeTime = Integer.parseInt(timeText);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", StringUtils.integerToHexString(mode));
		params.put("step", StringUtils.integerToHexString(fadeValueNow));
		params.put("time", StringUtils.integerToHexString(fadeTime * 10));
		sendCmd("step", params);
	}
	
	private void operateColorFadeStop() {
		sendCmd("stop", new HashMap<String, Object>());
	}
	
	private void sendCmd(String type, Map<String, Object> params) {
		params.put("src", "0x01");
		if(controlType == 0) params.put("dst", "0x01");
		else params.put("dst", "0x02");
		
		if (NetworkModeSwitcher.useOfflineMode(getActivity())) {
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(
					getActivity(), cmdList);
			offlineSender.send();
		}else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			
			List<byte[]> cmdList = cmdGenerator.generateCmd2(getActivity(), productFun, funDetail, params, type);
			
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), DatanAgentConnectResource.HOST_ZEGBING, 
					cmdList, true);
			onlineSender.send();
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
