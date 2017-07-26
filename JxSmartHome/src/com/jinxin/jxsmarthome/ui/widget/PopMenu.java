package com.jinxin.jxsmarthome.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.ui.adapter.PopupMenuAdapter;

/**
 * 自定义弹出菜单
 * @author TangLong
 * @company 金鑫智慧
 */
public class PopMenu {
	private List<String> menus;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;
	
	public PopMenu(Context context) {
		this.context = context;
		menus = new ArrayList<String>();
		
		View view = LayoutInflater.from(context).inflate(R.layout.pop_menu, null);
		listView = (ListView)view.findViewById(R.id.menu_listview);
		listView.setAdapter(new PopupMenuAdapter(context, menus));
		listView.setFocusable(true);
		listView.setFocusableInTouchMode(true);
		
		//popupWindow = new PopupWindow(view, 100, LayoutParams.WRAP_CONTENT);
		popupWindow = new PopupWindow(view, context.getResources()
				.getDimensionPixelSize(R.dimen.popmenu_width), LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	
	// 菜单点击监听
	public void setOnItemClickListener(OnItemClickListener listener) {
		listView.setOnItemClickListener(listener);
	}
	
	// 增加菜单
	public void addItem(String item) {
		menus.add(item);
	} 
	
	// 增加多个菜单
	public void addItems(String[] item) {
		for(int i = 0; i < item.length; i++) {
			menus.add(item[i]);
		}
	}
	
	// 下拉显示
	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent, 10, context.getResources().getDimensionPixelSize(R.dimen.popmenu_yoff));
	}
	
	// 上浮显示
	public void showAsDropUp(View parent) {
		popupWindow.showAsDropDown(parent, -40, -120);
		//popupWindow.showAtLocation(listView, Gravity.CENTER_HORIZONTAL|Gravity.TOP, 100, 400);
	}
	
	// 取消菜单
	public void dismiss() {
		popupWindow.dismiss();
	}
}
