package com.jinxin.veinunlock.camera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import xgzx.VeinUnlock.VeinCore;
import xgzx.VeinUnlock.VeinCore.DisplayStatus;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp;

/**
 * 识别窗口
 * @author Administrator
 *
 */
public class ImageCaptureCallback implements PreviewCallback{

	public DisplayStatus m_iDisplayStatus = DisplayStatus.STATUS_INIT;
	public boolean m_bIsProcessing = false;
	public boolean m_bFocusOk = false;

	private boolean m_bFlashOpen = false;
	private final int m_iImgWidth = VeinCore.m_iImgWidth;
	private final int m_iImgHeight = VeinCore.m_iImgHeight;
	private final int m_iWristWidth = VeinCore.m_iWristWidth;
	private final int m_iWristHeight = VeinCore.m_iWristHeight;

	private byte[] m_FrameBuf = new byte[m_iImgWidth*m_iImgHeight*3/2];

	SurfaceHolder mHolder;
	private View  mView;
	private Camera mCamera;
	private CameraManager cameraManager = null;
	private Context mContext;
	private AutoFocusCallback mAfcb = null;

	private int m_iCheckBrightCnt = 0;
	private int m_iCheckOkCnt = 0;
	private int m_iCheckOkNum = 3;
	private int m_iRegCnt = 0;
	private int m_iWorkMode = 0;
	private int m_iLeft = 0;
	private int m_iRight = 0;
	private int m_iTop = 0;
	private int m_iBottom = 0;
	private int m_iPartType = 0;

	private ImageView imgView;
	private Bitmap bmpDraw;
	private BitmapDrawable drw;
	private Paint p;
	private Canvas c;
	private String Msg;
	private int font_size;
	private Bitmap big_bitMap = null;
	private String[] PartName = null;
	private AlertDialog.Builder alert_dialog_builder;
	private AlertDialog alert_dialog;
	private AlertDialog part_alert_dialog;
	private PopupWindow m_PopupButton = null;
	private Button m_btnCtrl = null;
	private PopupWindow m_PopupDlgW = null;

	private float m_fXx = (float) 0.0;
	private float m_fYy = (float) 0.0;

	private int m_iDispW = 0;
	private int m_iDispH = 0;

	private PopupWindow mPopupWindow;
	private Resources resource;
	private Handler mHandler;

	private String[] m_sModeText = null;
	private Rect m_rect = new Rect(0, 0, 0, 0);///(left,top,right,bottom)

	public long m_iStartTime;
	private int m_iTimeExtend = 0;
	private int m_iAuthTimes = 0;

	public ImageCaptureCallback(SurfaceHolder holder,Context context,View view,Handler handler,
			AutoFocusCallback afcb, int work_mode,int time_out) {
		mHolder = holder;
		mView = view;
		mContext = context;
		resource = mContext.getResources();
		mHandler = handler;
		mAfcb = afcb;
		m_iWorkMode = work_mode;
		if(m_iWorkMode == VeinCore.MODE_AUTO_REG)
		{
			VeinCore.m_iMaxRegNum = 8;
		} else {
			VeinCore.m_iMaxRegNum = 3;
		}
		m_iTimeExtend = time_out;
		Logger.error("ICCB", "WorkMode:" + m_iWorkMode);
	}
	public void initDisplayData(int disW,int disH){
		m_iDispW = disW;
		m_iDispH = disH;
		
		if(bmpDraw != null && !bmpDraw.isRecycled()) bmpDraw.recycle();
		///绘制可见的 透明窗口
		if(bmpDraw == null){
			bmpDraw = Bitmap.createBitmap(m_iDispW, m_iDispH, Bitmap.Config.ARGB_8888); // RGB_565 couldn't make the background transparent.
		}
		if(imgView == null){
			imgView = new ImageView(mContext);
			imgView.setImageBitmap(bmpDraw);
		}
		mPopupWindow = new PopupWindow(imgView);
		mPopupWindow.setWidth(m_iDispW);
		mPopupWindow.setHeight(m_iDispH);
		mPopupWindow.setFocusable(false); // If true, PopupWindow disappears by clicking outside it.
		mPopupWindow.setClippingEnabled(false); // If true, PopupWindow doesn't protrude beyond the screen.
		mPopupWindow.showAtLocation(mView, Gravity.NO_GRAVITY, 0, 0);
		
		m_btnCtrl = new Button(mContext);
		m_PopupButton = new PopupWindow(m_btnCtrl); // Make PopupWindow with no frames
		
		int offset_x = (m_iImgWidth - m_iWristWidth)/2;
		int offset_y = (m_iImgHeight - m_iWristHeight)/2;
		m_fXx = (float)m_iDispW/(float)m_iImgWidth;///获取屏幕实际尺寸和规定的图像尺寸间的比例关系：横向
		m_fYy = (float)m_iDispH/(float)m_iImgHeight;///获取屏幕实际尺寸和规定的图像尺寸间的比例关系：纵向

		m_rect.left = (int)(offset_x*m_fXx);
		m_rect.right = (int)((offset_x + m_iWristWidth)*m_fXx);
		m_rect.top = (int)(offset_y*m_fYy);
		m_rect.bottom = (int)((offset_y + m_iWristHeight)*m_fYy);
		m_iDisplayStatus = DisplayStatus.STATUS_INIT;

		if(m_iWorkMode == VeinCore.MODE_AUTH)
		{
			if(JxshApp.m_VeinCore.GetRegUserNum() > 1)
			{
				JxshApp.m_VeinCore.m_iUser = 1;
				DispPartSelectDlg();
			} else {
				m_iWorkMode = VeinCore.MODE_AUTO_AUTH;
			}
		}
		if(m_iWorkMode == VeinCore.MODE_AUTO_AUTH)
		{
			OpenCamera();
			m_iDisplayStatus = DisplayStatus.STATUS_CHECKING;
			m_iStartTime = System.currentTimeMillis();
			StartPreviewFrame();
			//Flashlight(125);
			m_iCheckBrightCnt = 0;
			m_iCheckOkCnt = 0;
			m_iCheckOkNum = VeinCore.m_iCheckOkNum;
			//有效顺序是手背，手腕和手掌
			JxshApp.m_VeinCore.m_iUser = JxshApp.m_VeinCore.GetVerifyUserId();
		}
		if(m_iWorkMode == VeinCore.MODE_REG || m_iWorkMode == VeinCore.MODE_AUTO_REG)
		{
			JxshApp.m_VeinCore.m_iUser = 1;
			DispPartSelectDlg();
		}
	}
	private void OpenCamera()
	{
		if(cameraManager == null)
		{
			cameraManager = new CameraManager(mContext, mHolder);
			try {
				mCamera = cameraManager.initCamera();
			} catch (Exception e) {
				e.printStackTrace();
				// TODO：提示用户退出
				FinshSendMessage(VeinCore.MSG_CAMERA_ERROR);
				return;
			}
			cameraManager.setCameraParameter(mCamera, m_iImgWidth, m_iImgHeight);///设置相机的扫描属性
			try {
				mCamera.setPreviewDisplay(mHolder);
			} catch (IOException exception) {
				FinshSendMessage(VeinCore.MSG_CAMERA_ERROR);
			}
			initDisplayElement();////初始化画笔
		}
	}
	public void FinshSendMessage(int msg)
	{
		Logger.error("FinshSendMessage", "msg " + msg);
		StopPreviewFrame ();
		Flashlight(0);
		String model = "";
		try{
			model = Build.MODEL.substring(0, 3);
		}catch(Exception e){
			model = "";
		}
		if((model.equals("Mil"))){
			mCamera.stopPreview();//摩托罗拉上要加上这句话，否则横竖屏切换出现间断
		}
		Message Msg = mHandler.obtainMessage(msg);
		mHandler.sendMessage(Msg);
		destoryImageWindow();
	}
	
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (m_bIsProcessing || m_iDisplayStatus == DisplayStatus.STATUS_INIT) return;
		
		if(m_iWorkMode == VeinCore.MODE_REG || m_iWorkMode == VeinCore.MODE_AUTO_REG)
		{
			//每次只登记3个模板
			if (m_iDisplayStatus == DisplayStatus.STATUS_REG_OK)
			{
				if(m_iRegCnt >= JxshApp.m_VeinCore.m_iMaxRegNum)
				{
					//登记成功
					JxshApp.m_VeinCore.VeinSaveEnroll(JxshApp.m_VeinCore.m_iUser);
					FinshSendMessage(VeinCore.MSG_REG_OK);
					return;
				} else {
					if(m_iWorkMode == VeinCore.MODE_AUTO_REG)
					{
						m_iDisplayStatus = DisplayStatus.STATUS_CHECKING;
					} else {
						m_iDisplayStatus = DisplayStatus.STATUS_INIT;
						DispRegAlertDlg(JxshApp.m_VeinCore.m_iUser - 1,"", m_iRegCnt);
					}
					
				}
			}
			if (m_iDisplayStatus == DisplayStatus.STATUS_REG_FAILURE)
			{
				FinshSendMessage(VeinCore.MSG_REG_FAILURE);
				return;
			}
			if(m_iDisplayStatus == DisplayStatus.STATUS_REG_DIFF)
			{
				//与上次采集不一致
				m_iDisplayStatus = DisplayStatus.STATUS_INIT;
				Msg = resource.getString(R.string.msg_reg_diff);
				DispRegAlertDlg(JxshApp.m_VeinCore.m_iUser - 1,Msg, m_iRegCnt);
			}
		} else {
			if(m_iTimeExtend > 0){
				//超时
				if((System.currentTimeMillis()-m_iStartTime) > m_iTimeExtend*1000){
					FinshSendMessage(VeinCore.MSG_TIME_OUT);
					return;
				}
			}
			if (m_iDisplayStatus == DisplayStatus.STATUS_AUTH_OK)
			{
				//验证成功
				FinshSendMessage(VeinCore.MSG_AUTH_OK);
				return;
			}
			if(m_iAuthTimes > VeinCore.m_iMaxAuthNum)
			{
				//验证失败
				m_iAuthTimes = 0;
				//DispRetryDlg();
				FinshSendMessage(VeinCore.MSG_AUTH_FAILURE);
				return;
			}
		}

		int left = (m_iImgWidth - m_iWristWidth)/2;
		int right = (m_iImgWidth - m_iWristWidth)/2 + m_iWristWidth;
		int Offset = (m_iImgWidth - m_iWristWidth)/4;
		int Check = -1;
		m_FrameBuf = data;
		
		if(JxshApp.m_VeinCore.m_CheckEnable == 1)
		{
			byte[] Pos = new byte[32];
			int ret = JxshApp.m_VeinCore.CheckHand (m_FrameBuf, Pos); 
			//检测到的部位跟选择的不一致
			if(ret > 0) 
			{
				m_iPartType = ret;
				m_iLeft = Pos[0] + Pos[1]*256;
				m_iRight = Pos[2] + Pos[3]*256;
				m_iTop = Pos[4] + Pos[5]*256;
				m_iBottom = Pos[6] + Pos[7]*256;
				if ((m_iLeft >= (left - Offset) && m_iLeft <= (left + Offset))
						&& (m_iRight >= (right - Offset) && m_iRight <= (right + Offset)))
				{
					if(m_iTop > 0) m_iTop = m_iTop + 20;
					if(m_iBottom > 0) m_iBottom = m_iBottom - 20;
					if(m_iWorkMode == VeinCore.MODE_REG || m_iWorkMode == VeinCore.MODE_AUTO_REG)
					{
						if(m_iPartType == JxshApp.m_VeinCore.m_iUser)Check = 0;
					} else {
						Check = 0;
					}
				}
			} else {
				m_iPartType = 0;
			}
		} else if(JxshApp.m_VeinCore.m_CheckEnable == 2) {
			int offset = 20;
			m_iLeft = left + offset;
			m_iRight = right - offset;
			m_iTop = 30 + offset;
			m_iBottom = 210 - offset;
			Check = JxshApp.m_VeinCore.CheckSkin(m_FrameBuf, m_iLeft, m_iRight, m_iTop, m_iBottom);
			if(Check > 0 && Check < 1000) 
			{
				Check = 0;
				m_iPartType = 1;
			}
		} else {
			m_iLeft = left;
			m_iRight = right;
			m_iTop = 30;
			m_iBottom = 210;
			Check = JxshApp.m_VeinCore.CheckSkin(m_FrameBuf, m_iLeft, m_iRight, m_iTop, m_iBottom);
			if(Check > 0 && Check < 1000) 
			{
				Check = 0;
				m_iPartType = 1;
			}
		}
		if (Check != 0)
		{
			//图像检测不合格
			m_iPartType = 0;
			if(m_iDisplayStatus == DisplayStatus.STATUS_OK_BTN)
			{
				m_iDisplayStatus = DisplayStatus.STATUS_CHECKING;
				m_PopupButton.dismiss();
			}
			m_iCheckOkCnt = 0;
			if(Check == -1)
			{
				Msg = "图像太暗";
				if(m_iCheckBrightCnt++ > 8)
				{
					if(m_bFlashOpen == false)
					{
						Flashlight(125);
					}
				}
			} else if(Check == -2) {
				Msg = "图像太亮";
			} else if(Check == -3) {
				Msg = "没有检测到静脉";
			} else if(Check == -4) {
				Msg = "没有检测到静脉";
			} else if(Check == -5) {
				Msg = "不是静脉图像";
			} else {
				Msg = "没有检测到静脉" + Check;
			}
			//Msg = resource.getString(R.string.msg_no_skin);
		} else {
			//连续检测成功
			if (m_iCheckOkCnt++ >= m_iCheckOkNum)
			{
				if(m_bFocusOk == false)
				{
					StartPreviewFrameFocus();
				}
				m_iCheckOkCnt = 0;
				m_iCheckOkNum = 2;
				Msg = resource.getString(R.string.msg_skin_ok);
				if(m_iWorkMode == VeinCore.MODE_AUTO_AUTH)
				{
					//可以直接自动验证
					m_iDisplayStatus = DisplayStatus.STATUS_OK_IMG;
				}
				if(m_iDisplayStatus == DisplayStatus.STATUS_CHECKING)
				{
					if(m_iWorkMode == VeinCore.MODE_AUTO_REG)
					{
						Msg = Msg + "[" + m_iRegCnt + "/" + JxshApp.m_VeinCore.m_iMaxRegNum + "]";
						m_iStartTime = System.currentTimeMillis();
						m_iCheckBrightCnt = 0;
						m_iDisplayStatus = DisplayStatus.STATUS_OK_IMG;
					} else {
						//提示用户注册或验证
						m_iDisplayStatus = DisplayStatus.STATUS_OK_BTN;
						if(m_iWorkMode == VeinCore.MODE_REG)
						{
							switch(m_iRegCnt)
							{
							case 0:
								DisplayButton(resource.getString(R.string.msg_reg_1));
								break;
							case 1:
								DisplayButton(resource.getString(R.string.msg_reg_2));
								break;
							case 2:
								DisplayButton(resource.getString(R.string.msg_reg_3));
								break;
							}
						} else {
							DisplayButton(resource.getString(R.string.msg_start_auth));
						}
					}
				}else if(m_iDisplayStatus == DisplayStatus.STATUS_OK_IMG){
					//用户确认后
					if(m_bFocusOk)
					{	
						if (m_iWorkMode == VeinCore.MODE_REG || m_iWorkMode == VeinCore.MODE_AUTO_REG)
						{
							Msg = resource.getString(R.string.msg_reging);
							Msg = Msg + "[" + m_iRegCnt + "/" + JxshApp.m_VeinCore.m_iMaxRegNum + "]";
						} else {
							if(m_iAuthTimes == VeinCore.m_iMaxAuthNum/2)
							{
								m_bFocusOk = false;//启用对焦
								m_iCheckOkNum = 5;
								Msg = resource.getString(R.string.msg_auth_retry);
							} else {
								Msg = resource.getString(R.string.msg_authing);
							}
						}
						m_bIsProcessing = true;
						ProcessThread proc = new ProcessThread();
						proc.start();
					} else {
						Msg = resource.getString(R.string.msg_auto_focus);
					}
				}
			}
		}
		DisplayLineText (m_iDisplayStatus);
	}

	public void VeinReg(byte[] data, int part, int left, int right, int top, int bottom) {
		int m_iVeinResult = JxshApp.m_VeinCore.VeinEnroll(data, JxshApp.m_VeinCore.m_iUser, part, left, right, top, bottom);
		if (m_iVeinResult != 0)
		{
			Logger.error("VeinEnroll", "m_iVeinResult:" + m_iVeinResult);
			if(m_iVeinResult == 9)
			{
				//Toast.makeText(mContext, "不是同一个部位!", Toast.LENGTH_LONG).show();
				m_iDisplayStatus = DisplayStatus.STATUS_REG_DIFF;
			} else {
				m_iDisplayStatus = DisplayStatus.STATUS_REG_FAILURE;
			}
		} else {
			m_iDisplayStatus = DisplayStatus.STATUS_REG_OK;
			m_iRegCnt++;
		}
	}
	
	private void VeinAuth(byte[] data, int part, int left, int right, int top, int bottom) {
		//Logger.error("VeinAuth", "part:" + part);
		long start = System.currentTimeMillis();
		int ret = 0;
		if(m_iWorkMode == VeinCore.MODE_AUTO_AUTH)
		{
			ret = JxshApp.m_VeinCore.VeinVerify(m_FrameBuf, 0, part, m_iLeft, m_iRight, m_iTop, m_iBottom);
		} else {
			ret = JxshApp.m_VeinCore.VeinVerify(m_FrameBuf, JxshApp.m_VeinCore.m_iUser, part, m_iLeft, m_iRight, m_iTop, m_iBottom);
		}
		start = System.currentTimeMillis() - start;
		Logger.error("VeinAuth", "ret:" + ret + " ReTry:" + m_iAuthTimes + " ms:" + start);
		if (ret > 0)
		{
			if(false)
			{
				start = System.currentTimeMillis();
				int size = JxshApp.m_VeinCore.GetVeinChara(m_FrameBuf, m_iPartType, m_iLeft, m_iRight, m_iTop, m_iBottom);
				start = System.currentTimeMillis() - start;
				Logger.error("GetVeinChara", "size:" + size + " ms:" + start);
			}
			m_iDisplayStatus = DisplayStatus.STATUS_AUTH_OK;
			m_iAuthTimes = 0;
		} else {
			m_iDisplayStatus = DisplayStatus.STATUS_OK_IMG;
			m_iAuthTimes++;
		}
	}
	private class ProcessThread extends Thread {
		@Override
		public void run() {
			try {
				//int part = AutoUnlock.m_VeinCore.m_iUser;
				int part = 0;
				if (m_iWorkMode == VeinCore.MODE_REG || m_iWorkMode == VeinCore.MODE_AUTO_REG)
				{
					VeinReg(m_FrameBuf, part, m_iLeft, m_iRight, m_iTop, m_iBottom);
				} else {
					VeinAuth(m_FrameBuf, part, m_iLeft, m_iRight, m_iTop, m_iBottom);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				m_bIsProcessing = false;
			}
		}
	}

	public void StartPreviewFrame () {
		if(mCamera != null){
			mCamera.setPreviewCallback(this);
		}
	}
	
	public void StartPreviewFrameFocus () {
		if(mCamera != null){
			if(mAfcb != null)
			{
				mCamera.autoFocus(mAfcb);
			} else {
				mCamera.setPreviewCallback(this);
			}
		}
	}

	public void StopPreviewFrame () {
		while(m_bIsProcessing);
		if(mCamera != null){
			mCamera.autoFocus(null);
			mCamera.setPreviewCallback(null);
		}
	}
	private void initDisplayElement(){
		Msg = "";
		if(mPopupWindow == null)
		{
			return;
		}
		if(font_size == 0){
			font_size = ((m_iDispW/24)>>2)<<2;
		}
		if(imgView == null){
			imgView = (ImageView)mPopupWindow.getContentView();
		}
		if(bmpDraw == null){
			drw = (BitmapDrawable)imgView.getDrawable();
			bmpDraw = drw.getBitmap();
		}
		if(c == null){
			c = new Canvas(bmpDraw);
		}
		if(p == null){
			p = new Paint();
			p.setStyle(Paint.Style.STROKE); // Display lines only	
			p.setTextSize(font_size);
		}
		if(PartName == null){
			PartName = resource.getStringArray(R.array.vein_part);
		}
		m_sModeText = resource.getStringArray(R.array.mode_str);
	}
	private void drawRefrenceLinne(){///绘制检测到的边缘红线
		int offset = 20;
		float fLeft = (float)((m_iLeft - offset)*m_fXx);
		float fRight = (float)((m_iRight + offset)*m_fXx);
		float fTop = (float)((m_iTop - offset)*m_fYy);
		float fBottom = (float)((m_iBottom + offset)*m_fYy);
		p.setColor(Color.RED);
		c.drawLine(fLeft, fTop, fRight, fTop, p);
		c.drawLine(fLeft, fTop, fLeft, fBottom, p);
		c.drawLine(fRight, fTop, fRight, fBottom, p);
		c.drawLine(fLeft, fBottom, fRight, fBottom, p);
	}
	public void DisplayLineText(DisplayStatus status) {
		if(bmpDraw == null) return;
		
		bmpDraw.eraseColor(Color.TRANSPARENT);
		if(JxshApp.m_VeinCore.m_CheckEnable != 0)
		{
			String ModeStr = null;
			
			if(m_iWorkMode == VeinCore.MODE_AUTO_AUTH)
			{
				for(int i = 1; i <= 3; i++)
				{
					if(JxshApp.m_VeinCore.VeinGetTempNum(i) > 0)
					{
						if(i == 1)
							big_bitMap = BitmapFactory.decodeResource(resource, R.drawable.wrist_big);
						if(i == 2)
							big_bitMap = BitmapFactory.decodeResource(resource, R.drawable.opisthenar_big);
						if(i == 3)
							big_bitMap = BitmapFactory.decodeResource(resource, R.drawable.palm_big);
						int bitWidth = big_bitMap.getWidth();
						int bitHeight = big_bitMap.getHeight();
						float scaleWidth =m_iDispW/(float)bitWidth;
						float scaleHeight = m_iDispH/(float)bitHeight;
						Matrix matrix = new Matrix();
						matrix.postScale(scaleWidth, scaleHeight);
						c.drawBitmap(big_bitMap,matrix,p);
					}
				}
				ModeStr = "自动认证";
			} else {
				if(JxshApp.m_VeinCore.m_iUser == VeinCore.PART_BACKHAND_L || JxshApp.m_VeinCore.m_iUser == VeinCore.PART_BACKHAND_R){
					big_bitMap = BitmapFactory.decodeResource(resource, R.drawable.opisthenar_big);
				}else if(JxshApp.m_VeinCore.m_iUser == VeinCore.PART_PALM_L || JxshApp.m_VeinCore.m_iUser == VeinCore.PART_PALM_R){
					big_bitMap = BitmapFactory.decodeResource(resource, R.drawable.palm_big);
				}else if(JxshApp.m_VeinCore.m_iUser == VeinCore.PART_WRIST){
					big_bitMap = BitmapFactory.decodeResource(resource, R.drawable.wrist_big);
				}
				if(big_bitMap != null){
					int bitWidth = big_bitMap.getWidth();
					int bitHeight = big_bitMap.getHeight();
					float scaleWidth =m_iDispW/(float)bitWidth;
					float scaleHeight = m_iDispH/(float)bitHeight;
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					c.drawBitmap(big_bitMap,matrix,p);
				}
				ModeStr = PartName[JxshApp.m_VeinCore.m_iUser - 1]+m_sModeText[m_iWorkMode];
			}

			p.setColor(Color.GREEN);
			float coord_x = ((m_iDispW - ModeStr.length()*font_size)/2);//font_size;//m_sDisplayText.length() + font_size;
			float coord_y = font_size;
			c.drawText(ModeStr, coord_x, coord_y, p);
			
			c.drawLine(m_rect.left, m_rect.top, m_rect.right, m_rect.top, p);
			c.drawLine(m_rect.left, m_rect.top, m_rect.left, m_iDispH, p);
			c.drawLine(m_rect.right, m_rect.top, m_rect.right, m_iDispH, p);
		}

		drawRefrenceLinne();
		p.setColor(Color.RED);
		float coord_x = ((m_iDispW - Msg.length()*font_size)/2);
		float coord_y = m_iDispH/2 + font_size*2;
		c.drawText(Msg, coord_x, coord_y, p);
		mPopupWindow.getContentView().invalidate(); // update the text string
	}	
	public void destoryImageWindow(){
		//StopPreviewFrame();
		//Flashlight(0);
		if(mCamera != null){
			while(m_bIsProcessing); // Wait until the data processing thread ends.
			mCamera.setPreviewCallback(null);
			mCamera.cancelAutoFocus();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		cameraManager = null;

		if (mPopupWindow != null) mPopupWindow.dismiss(); mPopupWindow = null;
		if (m_PopupDlgW != null) m_PopupDlgW.dismiss(); m_PopupDlgW = null;
		if (m_PopupButton != null) m_PopupButton.dismiss(); m_PopupButton = null;
		
		imgView = null;
		bmpDraw = null;
		PartName = null;
		m_sModeText = null;
		m_FrameBuf = null;
	}
	
	private void DispPartSelectDlg() {
		LayoutInflater layout = LayoutInflater.from(mContext);
		View part_select = layout.inflate(R.layout.reg_part_select, null);
		GridView grid_view = (GridView) part_select.findViewById(R.id.reg_part_grid);
		HashMap<String, Object> map = null;
		ArrayList<HashMap<String, Object>> buttonList = new ArrayList<HashMap<String,Object>>();
		String[] part_name = resource.getStringArray(R.array.vein_part);
		int[] part_image = new int[part_name.length-1];
		if(JxshApp.m_VeinCore.m_CheckEnable == 0)
		{
			part_image[0] = R.drawable.wrist;
			part_image[1] = R.drawable.opisthenar;
			part_image[2] = R.drawable.palm;
			part_image[3] = R.drawable.back;
		} else {
			part_image[0] = R.drawable.wrist;
			part_image[1] = R.drawable.opisthenar;
			part_image[2] = R.drawable.palm;
			part_image[3] = R.drawable.back;
		}
		for(int i = 0; i < part_name.length-2; i++){
			if(m_iWorkMode == VeinCore.MODE_AUTH)
			{
				//验证的时候没有登记就不显示了
				if(JxshApp.m_VeinCore.VeinGetTempNum(i + 1) <= 0)
				{
					buttonList.add(null);
					continue;
				}
			}
			map = new HashMap<String, Object>();
			map.put("Image", part_image[i]);
			map.put("Name", part_name[i]);
			buttonList.add(map);
		}
		map = new HashMap<String, Object>();
		map.put("Image", part_image[3]);
		map.put("Name", part_name[3]);
		buttonList.add(map);
		
		SimpleAdapter adapter = new SimpleAdapter(mContext,buttonList,
				R.layout.grid_item,
				new String[]{"Image","Name"},
				new int[]{R.id.Image,R.id.Name});
		grid_view.setAdapter(adapter);
		alert_dialog_builder = new AlertDialog.Builder(mContext);
		if(m_iWorkMode == VeinCore.MODE_REG || m_iWorkMode == VeinCore.MODE_AUTO_REG)
		{
			alert_dialog_builder.setTitle(R.string.msg_sel_reg_part);
		} else {
			alert_dialog_builder.setTitle(R.string.msg_sel_auth_part);
		}
		
		alert_dialog_builder.setView(part_select);
		alert_dialog = alert_dialog_builder.create();	
		alert_dialog.setCancelable(false);
		alert_dialog.show();
		grid_view.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int view_id, long arg3) {
				if(view_id >= 3)
				{
					FinshSendMessage(VeinCore.MSG_FINISH_EXIT);
					alert_dialog.dismiss();
				} else {
					JxshApp.m_VeinCore.m_iUser = view_id + 1;
					System.out.println("调用" + JxshApp.m_VeinCore.m_iUser);
					int num = JxshApp.m_VeinCore.VeinGetTempNum(JxshApp.m_VeinCore.m_iUser);
					Logger.error("ICCB", "User " + JxshApp.m_VeinCore.m_iUser + " temp " + num);
					
					if(m_iWorkMode == VeinCore.MODE_REG || m_iWorkMode == VeinCore.MODE_AUTO_REG)
					{
						String[] part_alert = resource.getStringArray(R.array.vein_part_alert);
						m_iRegCnt = 0;
						DispRegAlertDlg(JxshApp.m_VeinCore.m_iUser,part_alert[view_id], m_iRegCnt);
						alert_dialog.dismiss();
					} else {
						if(num <= 0)
						{
							//没有登记
							Toast.makeText(mContext, "此部位没有登记，请先登记", Toast.LENGTH_LONG).show();
							return;
						}
						OpenCamera();
						m_iDisplayStatus = DisplayStatus.STATUS_CHECKING;
						alert_dialog.dismiss();
						m_iStartTime = System.currentTimeMillis();
						StartPreviewFrame();
						//Flashlight(125);
						m_iCheckBrightCnt = 0;
						m_iCheckOkCnt = 0;
						m_iCheckOkNum = VeinCore.m_iCheckOkNum;
					}
				}
			}
		});
	}
	private void DisplayButton(String str) {
		int len = 0;
		int textsize = 40;
		if (str.charAt(0) < 127)
			len = str.length()*textsize + textsize;
		else
			len = str.length()*textsize*2 + textsize;
		m_btnCtrl.setText(str);
		m_btnCtrl.setTextSize(textsize);
		m_PopupButton.setWidth(len);
		m_PopupButton.setHeight(120);
		m_PopupButton.setFocusable(false); // If true, PopupWindow disappears by clicking outside it.
		m_PopupButton.setClippingEnabled(false); // If true, PopupWindow doesn't protrude beyond the screen.
		m_PopupButton.showAtLocation(mView, Gravity.NO_GRAVITY, (m_iDispW - len)/2, m_iDispH/2 - textsize*2);
		m_btnCtrl.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				m_PopupButton.dismiss();
				m_iDisplayStatus = DisplayStatus.STATUS_OK_IMG;
				m_iStartTime = System.currentTimeMillis();
				m_iCheckBrightCnt = 0;
				m_iCheckOkCnt = 0;
				m_iCheckOkNum = VeinCore.m_iCheckOkNum;
				if(m_bFocusOk == false)
				{
					StartPreviewFrameFocus();
				}
			}
		});
	}
	private void DispRetryDlg(){
		Flashlight(0);
		m_iDisplayStatus = DisplayStatus.STATUS_INIT;
		LayoutInflater layout = LayoutInflater.from(mContext);
		View dlg = layout.inflate(R.layout.retry_dlg, null);
		dlg.setBackgroundResource(R.drawable.dialog_bg);
		TextView text = (TextView) dlg.findViewById(R.id.TextMsg);
		text.setText(R.string.msg_auth_failure);
		text.setTextSize(22);
		text.setTextColor(Color.GREEN);
		Button retry_btn = (Button) dlg.findViewById(R.id.retry_btn);
		retry_btn.setWidth(m_iDispW/4-5);
		Button cancel_btn = (Button) dlg.findViewById(R.id.cancel_btn);
		cancel_btn.setWidth(m_iDispW/4-5);
		m_PopupDlgW = new PopupWindow(dlg);
		m_PopupDlgW.setWidth(m_iDispW/2);
		m_PopupDlgW.setHeight(m_iDispH/2);
		m_PopupDlgW.setFocusable(false); // If true, PopupWindow disappears by clicking outside it.
		m_PopupDlgW.setClippingEnabled(false); // If true, PopupWindow doesn't protrude beyond the screen.
		m_PopupDlgW.showAtLocation(mView, Gravity.NO_GRAVITY, m_iDispW/4, m_iDispH/4);
		retry_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(m_PopupDlgW != null && m_PopupDlgW.isShowing()){
					m_PopupDlgW.dismiss();
					m_PopupDlgW = null;
				}
				//重试时如果登记了多个部位，选择验证部位，并启动自动对焦
				m_iStartTime = System.currentTimeMillis();
				if(JxshApp.m_VeinCore.GetRegUserNum() > 1)
				{
					JxshApp.m_VeinCore.m_iUser = 1;
					m_bFocusOk = false;
					m_iWorkMode = VeinCore.MODE_AUTO_AUTH;
					DispPartSelectDlg();
				} else {
					m_iDisplayStatus = DisplayStatus.STATUS_CHECKING;
					//Flashlight(125);
				}
			}
		});
		cancel_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(m_PopupDlgW != null && m_PopupDlgW.isShowing()){
					m_PopupDlgW.dismiss();
					m_PopupDlgW = null;
				}
				FinshSendMessage(VeinCore.MSG_AUTH_FAILURE);
			}
		});
	}
	
	private void DispRegAlertDlg(int part,String alert_msg,int regNum){
		LayoutInflater layout = LayoutInflater.from(mContext);
		View part_alert_view = layout.inflate(R.layout.reg_part_alert, null);
		ImageView part_img = (ImageView) part_alert_view.findViewById(R.id.part_img);
		if(JxshApp.m_VeinCore.m_CheckEnable == 0)
		{
			switch(part)
			{
			case VeinCore.PART_WRIST:
				part_img.setImageDrawable(resource.getDrawable(R.drawable.wrist));
				break;
			case VeinCore.PART_BACKHAND_L:
				part_img.setImageDrawable(resource.getDrawable(R.drawable.opisthenar));
				break;
			case VeinCore.PART_PALM_L:
				part_img.setImageDrawable(resource.getDrawable(R.drawable.palm));
				break;
			}
		} else {
			switch(part)
			{
			case VeinCore.PART_WRIST:
				part_img.setImageDrawable(resource.getDrawable(R.drawable.wrist_small));
				break;
			case VeinCore.PART_BACKHAND_L:
				part_img.setImageDrawable(resource.getDrawable(R.drawable.opisthenar_small));
				break;
			case VeinCore.PART_PALM_L:
				part_img.setImageDrawable(resource.getDrawable(R.drawable.palm_small));
				break;
			}
		}
		TextView part_alert = (TextView) part_alert_view.findViewById(R.id.part_alert);
		part_alert.setText(alert_msg);
		if(alert_msg == null || alert_msg.equals("")){
			part_alert.setVisibility(View.GONE);
		}
		Button reg_btn = (Button) part_alert_view.findViewById(R.id.begin_reg_btn);
		Button reselect_btn = (Button) part_alert_view.findViewById(R.id.reselect_rge_btn);
		Button exit_btn = (Button) part_alert_view.findViewById(R.id.exit_reg_btn);
		if(regNum == 0){
			reg_btn.setText(R.string.start_reg);
			reselect_btn.setVisibility(View.VISIBLE);
			exit_btn.setVisibility(View.VISIBLE);
		}else{
			reg_btn.setText(R.string.continue_reg);
			reselect_btn.setVisibility(View.GONE);
			exit_btn.setVisibility(View.GONE);
		}
		AlertDialog.Builder alert_dialog = new AlertDialog.Builder(mContext);
		alert_dialog.setTitle(R.string.msg_part_alert);
		alert_dialog.setView(part_alert_view);
		part_alert_dialog = alert_dialog.create();
		part_alert_dialog.setCancelable(false);
		part_alert_dialog.show();
		alert_dialog = null;
		reg_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				OpenCamera();
				JxshApp.m_VeinCore.VeinDelEnroll(JxshApp.m_VeinCore.m_iUser);
				m_iDisplayStatus = DisplayStatus.STATUS_CHECKING;
				part_alert_dialog.dismiss();
				m_iStartTime = System.currentTimeMillis();
				big_bitMap = null;
				StartPreviewFrame();
				//注册是开闪光灯
				Flashlight(125);
				m_iCheckBrightCnt = 0;
				m_iCheckOkCnt = 0;
				//m_iCheckOkNum = 5;
				m_iCheckOkNum = 10;
				//m_iRegCnt = 0;
			}
		});
		exit_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView dialog_tv = new TextView(mContext);
				dialog_tv.setTextSize(22);
				dialog_tv.setText("清除当前注册数据?");
				new AlertDialog.Builder(mContext)
				.setTitle(R.string.alert)
				.setView(dialog_tv)
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						JxshApp.m_VeinCore.VeinDelEnroll(JxshApp.m_VeinCore.m_iUser);
						dialog.dismiss();
						if(mContext instanceof Activity) {
							((Activity)mContext).finish();
						}
					}
				})
				.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						    dialog.dismiss();
						    if(mContext instanceof Activity) {
								((Activity)mContext).finish();
							}
					}
				})
				.setCancelable(false)
				.show();
				//FinshSendMessage(VeinCore.MSG_FINISH_EXIT);
				part_alert_dialog.dismiss();
			}
		});
		reselect_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				part_alert_dialog.dismiss();
				Flashlight(0);
				StopPreviewFrame();
				DispPartSelectDlg();
			}
		});
	}
	
	private void Flashlight(int bright)
	{
		if(bright > 0)
    	{
			if(m_bFlashOpen == false)
			{
				m_bFlashOpen = true;
				//JxshApp.m_VeinCore.setLED(bright);
				if(cameraManager != null) {
				cameraManager.setFlashlightConventional(true);
				}
		    	//if(FlashlightManager.setFlashlight(true) < 0)
		    	{
		    		
		    	}
			}
    	} else {
    		if(m_bFlashOpen == true)
    		{
    			m_bFlashOpen = false;
	    		//JxshApp.m_VeinCore.setLED(0);
    			if(cameraManager != null) {
	    		cameraManager.setFlashlightConventional(false);
    			}
	    		//if(FlashlightManager.setFlashlight(false) < 0)
	    		{
	    			
	    		}
    		}
    	}
	}
} 
