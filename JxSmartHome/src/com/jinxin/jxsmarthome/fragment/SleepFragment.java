package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.jinxin.jxsmarthome.R;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import u.aly.ca;

public class SleepFragment extends Fragment{
	
	private View view;
	private View viewpagerView;
	private View hideView;
	private PieChart mChart;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_geocoder_sleep,container,false);
//		System.out.println("=top=="+pager.getPaddingTop());
		mChart = (PieChart) view.findViewById(R.id.fragment_geocoder_sleep_piechart); 
		mChart.setTransparentCircleColor(R.color.piechart_bacgroud);
		PieData mPieData = getPieData(2);    
        showChart(mChart, mPieData); 
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
private void showChart(PieChart pieChart, PieData pieData) {    
	    
        pieChart.setHoleRadius(41.5f);  //半径    
        pieChart.setTransparentCircleRadius(105f); // 半透明圈    
        
        pieChart.setDescription(null);    
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字    
        pieChart.setRotationAngle(90); // 初始旋转角度    
        pieChart.setUsePercentValues(false);  //显示成百分比  
        pieChart.setRotationEnabled(false);
        
        //设置数据    
        pieChart.setData(pieData);     
        //设置比例图
        Legend mLegend = pieChart.getLegend();
        //是否显示模块提示
        mLegend.setEnabled(false);
        pieChart.animateXY(1000, 1000);  //设置动画    
    }    
    
    /**  
     *   
     * @param count 分成几部分  
     * @param range  
     */    
    private PieData getPieData(int count) {    
            
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容    
    
        for (int i = 0; i < count; i++) {    
            xValues.add("");  //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4    
        }    
    
        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据    
    
        // 饼图数据    
        /**  
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38  
         * 所以 14代表的百分比就是14%   
         */    
        float quarterly1 = 14;    
        float quarterly2 = 14;    
        yValues.add(new Entry(75, 0));    
        yValues.add(new Entry(25, 1));    
        //y轴的集合    
        PieDataSet pieDataSet = new PieDataSet(yValues, "Quarterly Revenue 2014"/*显示在比例图上*/);    
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离    
        ArrayList<Integer> colors = new ArrayList<Integer>();    
        // 饼图颜色    
        colors.add(Color.rgb(225, 197, 012));    
        colors.add(Color.rgb(214, 214, 214));    
        pieDataSet.setColors(colors);  
        pieDataSet.setDrawValues(false);
        DisplayMetrics metrics = getResources().getDisplayMetrics();    
        float px = 5 * (metrics.densityDpi / 160f);    
        pieDataSet.setSelectionShift(px); // 选中态多出的长度    
        PieData pieData = new PieData(xValues, pieDataSet);    
        return pieData;    
    } 

}
