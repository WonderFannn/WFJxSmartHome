package com.jinxin.veinunlock.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
public class CameraManager {

//	private Context mContext;
//	private SurfaceHolder mHolder;
	private Camera camera;
	public CameraManager(Context context,SurfaceHolder holder) {
//		mContext = context;
//		mHolder = holder;
	}
	public Camera initCamera()throws IOException {
	    if (camera == null) {
	        camera = Camera.open();
	        if (camera == null) {
	          throw new IOException();
	        }}
		return camera;
	}

	public void setCameraParameter(Camera mCamera,int m_iImgWidth,int m_iImgHeight){
		Camera.Parameters parameters = mCamera.getParameters();
		//parameters.setExposureCompensation(0);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		parameters.setPreviewSize(m_iImgWidth, m_iImgHeight);
		parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
		parameters.setPreviewFrameRate(15);
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}
	public void releaseCamera(Camera mCamera){
		mCamera.setPreviewCallback(null);
		mCamera.cancelAutoFocus();
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	public void setFlashlightConventional(boolean active)
	{
		if(camera != null)
		{
			Camera.Parameters p = camera.getParameters();
			if (active)
				p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			else
				p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			camera.setParameters(p);
		}
	}
}
