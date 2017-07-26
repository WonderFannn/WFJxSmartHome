package com.jinxin.jxsmarthome.ui.widget.textviewex;

/***
 * 通知文字改变-基类
 * @author Administrator 2013-7-5 下午5:15:01

 */
public abstract class TextSizeChangeMonitor {
	public interface ITextSizeChange {
		void onTextSizeChange(int type);
	}

	public abstract int getCurrentType();

	public abstract void notifyChange(int textType);

	public abstract void addChange(ITextSizeChange change);

	public abstract void removeChange(ITextSizeChange change);
}