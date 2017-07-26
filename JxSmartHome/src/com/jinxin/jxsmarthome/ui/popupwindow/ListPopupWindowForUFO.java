
package com.jinxin.jxsmarthome.ui.popupwindow;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

/**
 * 遥控器选择弹出窗口
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ListPopupWindowForUFO extends PopupWindow {

    private View contentView = null;

    private View view = null;

    private ListView listView = null;

    private int selectIndex = 0;

    private Context context = null;
    List<CustomerProduct> cpList = null;

	private ListViewSimpleAdapter adapter = null;

    /**
     * @param context
     * @param str[] 显示的内容
     * @param view 显示在哪个view边上
     * @param itemWidth item 的宽度
     * @param initializeStr 初始内容信息
     */
    public ListPopupWindowForUFO(Context context, View view, List<CustomerProduct> cpList, int itemWidth) {
        super(LayoutInflater.from(context).inflate(
                R.layout.popupwindow_list_layout, null), itemWidth,
                LayoutParams.WRAP_CONTENT, true);
        this.contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_list_layout, null);
        contentView.setLayoutParams(new LayoutParams(itemWidth,LayoutParams.WRAP_CONTENT));
        this.context = context;
        this.view = view;
        this.cpList = cpList;
        initList();
        this.setContentView(contentView);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
    }
    
    private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
    	
    };

    private void initList() {
        this.listView = (ListView) contentView.findViewById(R.id.listView);
        adapter = new ListViewSimpleAdapter(cpList);
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
//        this.showAsDropDown(view);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
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
//        this.showAsDropDown(view, xoff, yoff);
        this.showAtLocation(view, Gravity.RIGHT|Gravity.TOP, xoff, yoff);
        view.setSelected(true);
    }

    class ListViewSimpleAdapter extends BaseAdapter {
    	
    	List<CustomerProduct> cpList = null;

        private ListViewSimpleAdapter(List<CustomerProduct> cpList) {
        	this.cpList = cpList;
        }

        @Override
        public int getCount() {
            return cpList != null ? cpList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return cpList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder holder = null;
        	if (convertView == null) {
        		convertView = CommDefines.getSkinManager().view(
    					R.layout.popupwindow_ufo_select_item);
    			holder = new ViewHolder(convertView);
    			convertView.setTag(holder);
			}else{
				holder =  (ViewHolder) convertView.getTag();
			}
        	FunDetailConfig  funConfig = getConfigByWhId(cpList.get(position).getWhId());
        	if (funConfig != null) {
        		holder.textView.setText(funConfig.getAlias());
			}else{
				holder.textView.setText("未命名");
			}
        	if (selectIndex == position) {
				holder.imageView.setVisibility(View.VISIBLE);
			}else{
				holder.imageView.setVisibility(View.INVISIBLE);
			}
            return convertView;
        }
        class ViewHolder{
        	TextView textView;
        	ImageView imageView;
        	public ViewHolder(View view) {
        		textView = (TextView) view.findViewById(R.id.tv_cusomer_name);
        		imageView = (ImageView) view.findViewById(R.id.iv_check_btn);
			}
        }
    }

    class ListViewOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            selectIndex = arg2;
            handler.sendEmptyMessage(0);
            ListPopupWindowForUFO.this.dismiss();
        }
    }
    
    /**
     * 通过whid查找config 获取飞碟名字
     * @param whId
     * @return
     */
    private FunDetailConfig getConfigByWhId(String whId){
    	FunDetailConfigDaoImpl fdcDaoImpl = new FunDetailConfigDaoImpl(context);
    	List<FunDetailConfig> configs = fdcDaoImpl.find(null, "whId=?", new String[]{whId}, null, null, null, null);
    	if (configs != null && configs.size() > 0) {
			return configs.get(0);
		}
    	return null;
    }

    public void setSelectIndex(int index){
    	selectIndex = index;
    	handler.sendEmptyMessage(0);
    }
    
	public int getSelectIndex() {
		return selectIndex;
	}
}
