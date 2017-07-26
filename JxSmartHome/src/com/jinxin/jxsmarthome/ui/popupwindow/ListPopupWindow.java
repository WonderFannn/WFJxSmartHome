
package com.jinxin.jxsmarthome.ui.popupwindow;

import java.util.ArrayList;
import java.util.List;

import com.jinxin.jxsmarthome.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * List类型弹出窗口
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ListPopupWindow extends PopupWindow {

    private View contentView = null;

    private View view = null;

    private ListView listView = null;

    private List<String> listStr = new ArrayList<String>();

    private String selectStr = null;
    private int selectIndex = -1;

    private Context context = null;

    private LayoutInflater inflater = null;

    private int itemWidth = LayoutParams.WRAP_CONTENT;

    /**
     * @param context
     * @param str[] 显示的内容
     * @param view 显示在哪个view边上
     * @param itemWidth item 的宽度
     * @param initializeStr 初始内容信息
     */
    public ListPopupWindow(Context context, List<String> strTypes, View view, int itemWidth,
            CharSequence initializeStr) {
        super(LayoutInflater.from(context).inflate(
                R.layout.popupwindow_list_layout, null), itemWidth,
                LayoutParams.WRAP_CONTENT, true);
        this.contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_list_layout, null);
        contentView.setLayoutParams(new LayoutParams(itemWidth,
                LayoutParams.FILL_PARENT));
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.view = view;
        listStr = strTypes;
        selectStr = (String) initializeStr;
        initList();
        this.setContentView(contentView);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 取得选中的String
     * 
     * @return string
     */
    public String getSelectStr() {
        return selectStr;
    }

    private void initList() {
        this.listView = (ListView) contentView.findViewById(R.id.listView);
        ListViewSimpleAdapter adapter = new ListViewSimpleAdapter(listStr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListViewOnItemClickListener());
    }

    public void dismiss() {
        super.dismiss();
        view.setSelected(false);
    }

    /**
     * 显示PopupWindow
     */
    public void show() {
        /*
         * popupWindow.showAsDropDown（View view）弹出对话框，位置在紧挨着view组件
         * showAsDropDown(View anchor, int xoff, int yoff)弹出对话框，位置在紧挨着view组件，x y
         * 代表着偏移量 showAtLocation(View parent, int gravity, int x, int y)弹出对话框
         * parent 父布局 gravity 依靠父布局的位置如Gravity.CENTER x y 坐标值
         */
        this.showAsDropDown(view);
        view.setSelected(true);
    }

    /**
     * 显示PopupWindow
     * 
     * @param xoff
     * @param yoff
     */
    public void show(int xoff, int yoff) {
        this.setContentView(contentView);
        this.showAsDropDown(view, xoff, yoff);
        view.setSelected(true);
    }

    class ListViewSimpleAdapter extends BaseAdapter {

        private List<String> strs = null;

        private ListViewSimpleAdapter(List<String> _strs) {
            strs = new ArrayList<String>();
            for (String str : _strs) {
                strs.add(str);
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return strs.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return strs.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView = inflater.inflate(R.layout.popupwindow_list_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_cusomer_name);
            textView.setText(strs.get(position).toString());
//            convertView.setBackgroundResource(R.drawable.option_top_state);
//            if (listStr.size() - 1 == position) {
//                convertView.setBackgroundResource(R.drawable.option_bottom_state);
//            }
            return convertView;
        }
    }

    class ListViewOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            selectStr = listStr.get(arg2).toString();
            selectIndex = arg2;
            ListPopupWindow.this.dismiss();
        }
    }

	public int getSelectIndex() {
		return selectIndex;
	}
}
