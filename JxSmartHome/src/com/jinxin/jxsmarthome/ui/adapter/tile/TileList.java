package com.jinxin.jxsmarthome.ui.adapter.tile;

import java.util.ArrayList;

public class TileList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;
	public static final String EMPTY_STRING = "";
	ArrayList<Integer> range = new ArrayList<Integer>();

	public void split() {
		range.add(Integer.valueOf(size() - sumRange()));
	}

	public ArrayList<Integer> getRange() {
		return range;
	}
	
	private int sumRange() {
		int count = 0;
		for (int subCount : range) {
			count += subCount;
		}
		return count;
	}

}
