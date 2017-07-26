package com.jinxin.jxsmarthome.entity;

/**
 * 定时任务的任务周期表达式
 * @author TangLong
 * @company 金鑫智慧
 */
public class CornExpression {
	private String period;		// 周期 	eg：(0,1,2,3,4,5,6)|(0,1,2,3,4)|2014-02-22|(0,3,6)
	private String time;		// 时间 	eg：09:00
	private int type;			// 周期类型     eg：1-周|2-工作日|3-当天|4-自定义
	
	public CornExpression() {}
	
	public CornExpression(String period, String time) {
		this(1, period, time);
	}
	
	public CornExpression(int type, String period, String time) {
		this.type = type;
		this.period = period;
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "period:" + period + ", time:" + time;
	}
	
	/////////////////////////////////////
	// getters and setters
	/////////////////////////////////////
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
