package com.jinxin.jxsmarthome.entity;


/**
 * 添加模式指令单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerPatternUpdataCMDVO extends Base {
	private int patternId = -1;
	private int cmdId = -1;
	private int exeOrder = -1;
	public CustomerPatternUpdataCMDVO(int patternId, int cmdId, int exeOrder) {
		super();
		this.patternId = patternId;
		this.cmdId = cmdId;
		this.exeOrder = exeOrder;
	}
	public int getPatternId() {
		return patternId;
	}
	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}
	public int getCmdId() {
		return cmdId;
	}
	public void setCmdId(int cmdId) {
		this.cmdId = cmdId;
	}
	public int getExeOrder() {
		return exeOrder;
	}
	public void setExeOrder(int exeOrder) {
		this.exeOrder = exeOrder;
	}
	

}
