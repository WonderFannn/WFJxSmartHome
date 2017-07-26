package com.jinxin.jxsmarthome.ui.widget.tab;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.jinxin.jxsmarthome.R;

public class ScrollingTabsAdapter implements TabAdapter {

	private final Context context;
	private List<String> tabs;

	public ScrollingTabsAdapter(Context context, List<String> tabs) {
		if(tabIsNull(tabs)) {
			throw new IllegalArgumentException("tabs must at least have one.");
		}
		this.context = context;
		this.tabs = tabs;
	}

	public View getView(int position) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final Button tab = (Button) inflater.inflate(R.layout.tabs, null);
		if (position < tabs.size())
			tab.setText(tabs.get(position).toUpperCase(Locale.CHINA));
		return tab;
	}
	
	private boolean tabIsNull(List<String> tabs) {
		if(tabs == null || tabs.size() < 1) {
			return true;
		}
		return false;
	}

}
