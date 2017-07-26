package com.jinxin.jxsmarthome.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MaximumHeightListView extends ListView {

	private int listViewHeight = -1;

	public MaximumHeightListView(Context context) {
		super(context);
	}

	public MaximumHeightListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public MaximumHeightListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    public int getListViewHeight() {
            return listViewHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (listViewHeight > -1) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(listViewHeight,
                                    MeasureSpec.AT_MOST);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setListViewHeight(int listViewHeight) {
            this.listViewHeight = listViewHeight;
    }
}
