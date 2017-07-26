package com.jinxin.veinunlock.data;
public class DeviceInfo{
	 private int m_iType;
	 private boolean m_Enable;
	 private int m_iLevel;
	 private int m_iTimeout;
	 private String m_strMac;
	 private int m_iMode;
	 public int getM_iType() {
		return m_iType;
	}
	public void setM_iType(int m_iType) {
		this.m_iType = m_iType;
	}
	public boolean isM_Enable() {
		return m_Enable;
	}
	public void setM_Enable(boolean m_Enable) {
		this.m_Enable = m_Enable;
	}
	public int getM_iLevel() {
		return m_iLevel;
	}
	public void setM_iLevel(int m_iLevel) {
		this.m_iLevel = m_iLevel;
	}
	public int getM_iTimeout() {
		switch(m_iTimeout)
		{
		case 1:
			return 10;
		case 2:
			return 20;
		case 3:
			return 30;
		default:
			return 0;
		}
	}
	public void setM_iTimeout(int m_iTimeout) {
		this.m_iTimeout = m_iTimeout;
	}
	public String getM_strMac() {
		return m_strMac;
	}
	public void setM_strMac(String m_strMac) {
		this.m_strMac = m_strMac;
	}
	public int getM_iMode() {
		return m_iMode;
	}
	public void setM_iMode(int m_iMode) {
		this.m_iMode = m_iMode;
	}
	
}