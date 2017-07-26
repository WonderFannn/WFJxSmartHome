package com.jinxin.jxsmarthome.ui.widget.textviewex;

/***
 * 通知文字改变-实现
 *  * 用法：在Application中TextSizeChangeMonitor monitor = new TextSizeChangeMonitorImpl();
		TextViewEx.Manager.setTextSizeChangeMonitor(monitor);
 * huangnenghai

 */
import java.util.HashSet;
import java.util.Set;



public class TextSizeChangeMonitorImpl extends TextSizeChangeMonitor {
	Set<ITextSizeChange> changeList = new HashSet<ITextSizeChange>();
	int currentType = TextViewEx.TEXT_TYPE_MIDDLE;

	@Override
	public int getCurrentType() {
		return currentType;
	}

	@Override
	public void notifyChange(int textType) {
		if (currentType != textType) {
			currentType = textType;
			for (ITextSizeChange change : changeList) {
				change.onTextSizeChange(textType);
			}
		}
	}

	@Override
	public void addChange(ITextSizeChange change) {
		changeList.add(change);
	}

	@Override
	public void removeChange(ITextSizeChange change) {
		changeList.remove(change);
	}
}
