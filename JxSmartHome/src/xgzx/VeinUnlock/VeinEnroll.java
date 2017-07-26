package xgzx.VeinUnlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;
import com.jinxin.veinunlock.camera.ImageCaptureCallback;

public class VeinEnroll extends Activity {
	/** Called when the activity is first created. */
	private Preview mPreview;
	private Context mContext;
	private PowerManager.WakeLock mWakeLock;///设置手机一直处于亮光状态
	private  Vibrator mVibrator;
	private boolean isOnPause;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Logger.debug(ACTIVITY_SERVICE, "VeinEnroll.........!");
		super.onCreate(savedInstanceState);
		mContext = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);////设置全屏显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mVibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);

		mPreview = new Preview(this);
		setContentView(mPreview);

		////设置手持设备一直处于亮光状态
		PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE); 
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "XYTEST"); 
		mWakeLock.acquire(); 
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	///预览，不断的返回一个byte数组，称为回调函数

	class Preview extends SurfaceView implements SurfaceHolder.Callback {
		SurfaceHolder mHolder;
		ImageCaptureCallback iccb = null;
		private AutoFocusCallback afcb = null;
		private AlertDialog reg_ok_dialog;
		private AlertDialog password_dialog;
		Resources res = this.getResources();

		int m_iDispW = 0;
		int m_iDispH = 0;

		Preview(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			setFocusable(true); // Enable Focus in order to obtain Key events 
			setFocusableInTouchMode(true); // Ditto (in order to sense BACK-key as well)
		}

		public class AutoFocusCallback implements Camera.AutoFocusCallback {
			public void onAutoFocus(boolean success, Camera camera)
			{
				Logger.error("AutoFocus", "callback...");
				iccb.m_bFocusOk = true;
			}
		}
		
		public void surfaceCreated(SurfaceHolder holder) {
			if(isOnPause){
				return;
			}
			m_iDispW = getWidth();
			m_iDispH = getHeight();
			afcb = new AutoFocusCallback();
			iccb = new ImageCaptureCallback(mHolder,mContext,(View)getParent(),
					mHandler,afcb,VeinCore.MODE_AUTO_REG,0);
			iccb.initDisplayData(m_iDispW, m_iDispH);
			iccb.m_bFocusOk = false;//注册的时候启用自动对焦
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			iccb.destoryImageWindow();
			afcb = null;
			mHolder = null;
			res = null;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			if(isOnPause){
				return;
			}
		}
		
		private View getDialogView(String Msg){
			LayoutInflater layout = LayoutInflater.from(mContext);
			View dlg_v = layout.inflate(R.layout.dlg, null);
			TextView TvMsg = ((TextView)dlg_v.findViewById(R.id.TextMsg));
			TvMsg.setText(Msg);
			TvMsg.setGravity(Gravity.CENTER);
			TvMsg.setTextSize(30);
			if(Msg.length()>12){
				TvMsg.setTextSize(20);
			}
			TvMsg.setTextColor(Color.GREEN);
			return dlg_v ;
		}
		
		private void PasswordDlg (){
			String msg = mPreview.res.getString(R.string.msg_reg_ok);
			RegOkDlg(msg);
		}
		
		private void RegOkDlg (String Msg){
			String title = res.getString(R.string.msg_title);
			View dlg_v = getDialogView(Msg);
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(title);
			builder.setView(dlg_v);
			builder.setPositiveButton(R.string.msg_continue_reg, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					iccb = new ImageCaptureCallback(mHolder,mContext,(View)getParent(),
							mHandler,afcb,VeinCore.MODE_AUTO_REG,0);
					iccb.initDisplayData(m_iDispW, m_iDispH);
					iccb.m_bFocusOk = false;//注册的时候启用自动对焦
				}
			});
			builder.setNeutralButton(R.string.msg_pre_ident, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					iccb = new ImageCaptureCallback(mHolder,mContext,(View)getParent(),
							mHandler,afcb,VeinCore.MODE_AUTO_AUTH,0);
					iccb.initDisplayData(m_iDispW, m_iDispH);
					iccb.m_bFocusOk = true;//验证时不用自动对焦
				}
			});
			builder.setNegativeButton(R.string.btn_exit, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
//					Intent SetIntent = new Intent(VeinEnroll.this, MainActivity.class);
//					SetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(SetIntent);
					finish();
				}
			});
			reg_ok_dialog = builder.create();
			reg_ok_dialog.setCancelable(false);
			reg_ok_dialog.show();
			
			//TODO:注册成功登记
			String _account = CommUtil.getCurrentLoginAccount();
			SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_VEIN, true);
		}
	}
	private final Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VeinCore.MSG_AUTH_OK:
				mPreview.RegOkDlg(mPreview.res.getString(R.string.msg_auth_success));
				break;
			case VeinCore.MSG_TIME_OUT:
			case VeinCore.MSG_AUTH_FAILURE:
				mPreview.RegOkDlg(mPreview.res.getString(R.string.msg_auth_failure));
				break;
			case VeinCore.MSG_REG_OK:
				if(mVibrator != null){
					mVibrator.vibrate(50);  
				}
				SharedPreferences settings = getSharedPreferences("VEINLOCK", Context.MODE_WORLD_READABLE
						| Context.MODE_WORLD_WRITEABLE);
				String SavePwd = settings.getString("PASSWORD", "");
				if(SavePwd.length() < 2)
				{
					mPreview.PasswordDlg();
				} else {
					String str = mPreview.res.getString(R.string.msg_reg_ok) + ",密码：" + SavePwd;
					mPreview.RegOkDlg(str);
				}
				break;
			case VeinCore.MSG_REG_FAILURE:
				mPreview.RegOkDlg(mPreview.res.getString(R.string.msg_reg_failure));
				break;
			case VeinCore.MSG_FINISH_EXIT:
//				Intent SetIntent = new Intent(VeinEnroll.this, MainActivity.class);
//				SetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(SetIntent);
				finish();
				break;
			}
		};
	};

	@Override
	protected void onPause() {
		isOnPause = true;
		if(mPreview != null && mPreview.mHolder != null){
			mPreview.surfaceDestroyed(mPreview.mHolder);
		}
		super.onPause();
		finish();
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
		mWakeLock = null;
		mPreview.mHolder = null;
		mPreview = null;
		mContext = null;
	}
	@Override
	public void onBackPressed() {
//		Intent SetIntent = new Intent(VeinEnroll.this, MainActivity.class);
//		SetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(SetIntent);
		finish();
	}
}
