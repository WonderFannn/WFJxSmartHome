package com.jinxin.widget.progressBar;

/**
 * @author YangJiJun
 *弧形进度条公用方法
 */
public class ProgressUtil {
	
	//颜色RGB值
	private static String _a = "";
	private static String _r = "";
	private static String _g = "";
	private static String _b = "";
	/**
	 * @return 以progress为中心的当前位置的角度
	 */
	public static double getAngle(double xTouch, double yTouch,double circleWidth,double circleHeight) {
		double x = xTouch - (circleWidth / 2d);
		double y = circleHeight - yTouch - (circleHeight / 2d);

		switch (getQuadrant(x, y)) {
		case 1:
			return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

		case 2:
		case 3:
			return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);

		case 4:
			return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

		default:
			return 0;
		}
	}
	
    /**
	 * 计算应该返回的角度数值
	 * 
	 * @param a
	 * @return 小于0时返回0;大于315时返回315
	 */
	public static double getProgress(double a) {
		double tmp = a - 135D;
		if (tmp < -90D) {
			tmp = 225D + a;
		} else if (tmp < -45D && tmp > -90D) {
			tmp = 270D;
		}else if (tmp < 0 && tmp > -45) {
			tmp = 0D;
		}
		return tmp;
	}
	
	/**
	 * @return 返回象限
	 */
	public static int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}
	public static String setRgbToString(int a,int r,int g,int b){
		if (a<10) {
			_a = "00"+a;
		}else if (a>=10 && a < 100) {
			_a = "0"+a;
		}else if(a>=100){
			_a = a+"";
		}
		if (r<10) {
			_r = "00"+r;
		}else if (r>=10 && r < 100) {
			_r = "0"+r;
		}else if(r>=100){
			_r = r+"";
		}
		if (g<10) {
			_g = "00"+g;
		}else if (g>=10 && g < 100) {
			_g = "0"+g;
		}else if(g>=100){
			_g = g+"";
		}
		if (b<10) {
			_b = "00"+b;
		}else if (b>=10 && b < 100) {
			_b = "0"+b;
		}else if(b>=100){
			_b = b+"";
		}
		return _r+_g+_b+_a;
	}
	
}
